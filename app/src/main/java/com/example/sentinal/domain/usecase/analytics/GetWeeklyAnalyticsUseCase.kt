package com.example.sentinal.domain.usecase.analytics

import com.example.sentinal.data.appinfo.AppLabelResolver
import com.example.sentinal.domain.model.AnalyticsSummary
import com.example.sentinal.domain.model.AppUsagePoint
import com.example.sentinal.domain.model.CategoryUsagePoint
import com.example.sentinal.domain.model.DailyUsagePoint
import com.example.sentinal.domain.repository.AppUsageDailyRepository
import com.example.sentinal.domain.rule.AnalyticsRuleEngine
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

class GetWeeklyAnalyticsUseCase @Inject constructor(
    private val appUsageDailyRepository: AppUsageDailyRepository,
    private val analyticsRuleEngine: AnalyticsRuleEngine,
    private val appLabelResolver: AppLabelResolver,
){
    suspend operator fun invoke(
        today: LocalDate = LocalDate.now()
    ): AnalyticsSummary {

        val currentFromDay = today.minusDays(6).toEpochDay()
        val currentToDay = today.toEpochDay()

        val previousFromDay = today.minusDays(13).toEpochDay()
        val previousToDay = today.minusDays(7).toEpochDay()

        val currentItems = appUsageDailyRepository
            .observeDailyUsageRange(
                fromDay = currentFromDay,
                toDay = currentToDay
            )
            .first()

        val previousItems = appUsageDailyRepository
            .observeDailyUsageRange(
                fromDay = previousFromDay,
                toDay = previousToDay
            )
            .first()

        val totalUsageMillis = currentItems.sumOf {it.usageTimeMillis }
        val previousTotalUsageMillis = previousItems.sumOf { it.usageTimeMillis }
        val nightUsageMillis = currentItems.sumOf { it.nightUsageMillis }
        val nightUsageRatePercent = if (totalUsageMillis <= 0L) {
            0f
        } else {
            nightUsageMillis.toFloat() / totalUsageMillis.toFloat() * 100f
        }

        val usageByDay = currentItems
            .groupBy { it.dateEpochDay }
            .mapValues { (_, items) ->
                items.sumOf { it.usageTimeMillis }
            }

        val dailyUsages = (currentFromDay..currentToDay)
            .map { dateEpochDay ->
                DailyUsagePoint(
                    dateEpoch = dateEpochDay,
                    usageMillis = usageByDay[dateEpochDay] ?: 0L,
                )
            }

        val categoryUsages = currentItems
            .groupBy { it.category ?: "기타" }
            .map { (category, items) ->
                CategoryUsagePoint(
                    category = category,
                    usageMillis = items.sumOf { it.usageTimeMillis },
                )
            }
            .sortedByDescending { it.usageMillis }

        val topApps = currentItems.
            groupBy { it.packageName }
            .map{(packageName, items) ->
                AppUsagePoint(
                    packageName = packageName,
                    appName = appLabelResolver.resolveAppName(packageName),
                    usageMillis = items.sumOf { it.usageTimeMillis },
                    launchCount = items.sumOf { it.launchCount }

                )
            }
            .filter { it.usageMillis>0L }
            .sortedByDescending { it.usageMillis }
            .take(5)

        return AnalyticsSummary(
            totalUsageMillis = totalUsageMillis,
            changeRatePercent = analyticsRuleEngine.calculateChangeRatePercent(
                currentTotalMillis = totalUsageMillis,
                previousTotalMillis = previousTotalUsageMillis,
            ),
            nightUsageRatePercent = nightUsageRatePercent,
            dailyUsages = dailyUsages,
            categoryUsages = categoryUsages,
            topApps = topApps,
        )

    }
}
