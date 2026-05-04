package com.example.sentinal.domain.usecase.aggregate

import android.app.usage.UsageEvents
import com.example.sentinal.data.appinfo.AppCategoryResolver
import com.example.sentinal.data.appinfo.UsageStatsAppFilter
import com.example.sentinal.data.local.entity.AppUsageDailyEntity
import com.example.sentinal.data.local.entity.UsageEventEntity
import com.example.sentinal.domain.repository.AppUsageDailyRepository
import com.example.sentinal.domain.repository.UsageRepository
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

class AggregateDailyAppUsageUseCase @Inject constructor(
    private val usageRepository: UsageRepository,
    private val appUsageDailyRepository: AppUsageDailyRepository,
    private val appCategoryResolver: AppCategoryResolver,
    private val usageStatsAppFilter: UsageStatsAppFilter,
) {
    suspend operator fun invoke(fromTimestamp: Long,toTimestamp:Long,zoneId: ZoneId = ZoneId.systemDefault(),){
        val usageEvents = usageRepository
            .observeRecentUsageEvents(fromTimestamp)
            .first()
            .filter { it.timestamp in fromTimestamp..toTimestamp }
            .filterNot { usageStatsAppFilter.shouldExclude(it.packageName) }
            .distinctBy {
                UsageEventKey(
                    timestamp = it.timestamp,
                    packageName = it.packageName,
                    className = it.className,
                    eventType = it.eventType,
                )
            }
            .sortedBy { it.timestamp }

        val dailyItems = usageEvents.groupBy {
            val dateEpochDay = Instant.ofEpochMilli(it.timestamp)
                .atZone(zoneId)
                .toLocalDate()
                .toEpochDay()

            dateEpochDay to it.packageName
        }.map{(key,events)->
            val dateEpochDay = key.first
            val packageName = key.second
            val groupEndTimestamp = minOf(
                toTimestamp,
                endOfDayMillis(dateEpochDay,zoneId)
            )

            val durationSummary = calculateUsageDurationSummary(
                events = events.sortedBy { it.timestamp },
                fallbackEndTimestamp = groupEndTimestamp,
                zoneId = zoneId,
            )

            AppUsageDailyEntity(
                dateEpochDay = dateEpochDay,
                packageName = packageName,
                usageTimeMillis = durationSummary.totalMillis,
                nightUsageMillis = durationSummary.nightMillis,
                launchCount = events.count {
                    it.eventType == UsageEvents.Event.ACTIVITY_RESUMED
                },
                category = appCategoryResolver.resolveCategory(packageName),
            )

        }

        appUsageDailyRepository.upsertDailyUsage(dailyItems)
    }

    private data class UsageDurationSummary(
        val totalMillis: Long,
        val nightMillis: Long,
    )

    private fun calculateUsageDurationSummary(
        events: List<UsageEventEntity>,
        fallbackEndTimestamp: Long,
        zoneId: ZoneId,
    ): UsageDurationSummary {
        var activeStartTimestamp: Long? = null
        var totalMillis = 0L
        var nightMillis = 0L

        events.forEach { event->
            when(event.eventType){
                UsageEvents.Event.ACTIVITY_RESUMED ->{
                    if(activeStartTimestamp == null){
                        activeStartTimestamp = event.timestamp
                    }
                }

                UsageEvents.Event.ACTIVITY_PAUSED ->{
                    val startTimestamp = activeStartTimestamp
                    if(startTimestamp!=null && event.timestamp >startTimestamp){
                        totalMillis+=event.timestamp -startTimestamp
                        nightMillis += calculateNightOverlapMillis(
                            startTimestamp = startTimestamp,
                            endTimestamp = event.timestamp,
                            zoneId = zoneId,
                        )
                        activeStartTimestamp = null
                    }
                }
            }
        }

        val startTimestamp = activeStartTimestamp
        if(startTimestamp !=null && fallbackEndTimestamp >startTimestamp){
            totalMillis+=fallbackEndTimestamp- startTimestamp
            nightMillis += calculateNightOverlapMillis(
                startTimestamp = startTimestamp,
                endTimestamp = fallbackEndTimestamp,
                zoneId = zoneId,
            )
        }

        return UsageDurationSummary(
            totalMillis = totalMillis,
            nightMillis = nightMillis,
        )
    }

    private fun calculateNightOverlapMillis(
        startTimestamp: Long,
        endTimestamp: Long,
        zoneId: ZoneId,
    ): Long {
        if (endTimestamp <= startTimestamp) return 0L

        var totalMillis = 0L
        var date = Instant.ofEpochMilli(startTimestamp)
            .atZone(zoneId)
            .toLocalDate()
            .minusDays(1)

        val endDate = Instant.ofEpochMilli(endTimestamp)
            .atZone(zoneId)
            .toLocalDate()
            .plusDays(1)

        while (!date.isAfter(endDate)) {
            val nightStart = date
                .atTime(LocalTime.of(22, 0))
                .atZone(zoneId)
                .toInstant()
                .toEpochMilli()

            val nightEnd = date
                .plusDays(1)
                .atTime(LocalTime.of(6, 0))
                .atZone(zoneId)
                .toInstant()
                .toEpochMilli()

            totalMillis += overlapMillis(
                startA = startTimestamp,
                endA = endTimestamp,
                startB = nightStart,
                endB = nightEnd,
            )

            date = date.plusDays(1)
        }

        return totalMillis
    }

    private fun overlapMillis(
        startA: Long,
        endA: Long,
        startB: Long,
        endB: Long,
    ): Long {
        val start = maxOf(startA, startB)
        val end = minOf(endA, endB)
        return (end - start).coerceAtLeast(0L)
    }

    private fun endOfDayMillis(
        dateEpochDay: Long,
        zoneId: ZoneId, ): Long{
        return LocalDate.ofEpochDay(dateEpochDay)
            .plusDays(1)
            .atStartOfDay(zoneId)
            .toInstant()
            .toEpochMilli()
    }
}

private data class UsageEventKey(
    val timestamp: Long,
    val packageName: String,
    val className: String?,
    val eventType: Int,
)
