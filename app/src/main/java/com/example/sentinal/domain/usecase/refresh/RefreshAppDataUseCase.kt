package com.example.sentinal.domain.usecase.refresh

import com.example.sentinal.domain.usecase.aggregate.AggregateDailyAppUsageUseCase
import com.example.sentinal.domain.usecase.aggregate.AggregateRecentDeviceDataUseCase
import com.example.sentinal.domain.usecase.collector.CollectDeviceDataUseCase
import javax.inject.Inject

class RefreshAppDataUseCase @Inject constructor(
    private val collectDeviceDataUseCase: CollectDeviceDataUseCase,
    private val aggregateRecentDeviceDataUseCase: AggregateRecentDeviceDataUseCase,
    private val aggregateDailyAppUsageUseCase: AggregateDailyAppUsageUseCase,
){
    suspend operator fun invoke(now:Long = System.currentTimeMillis(),): RefreshWindow{
        val recentWindowStart = now - FIVE_MINUTES_MILLIS
        val dayWindowStart = now - ONE_DAY_MILLIS

        collectDeviceDataUseCase(
            fromTimestamp = dayWindowStart,
            toTimestamp = now,
        )

        aggregateRecentDeviceDataUseCase(
            windowStart = recentWindowStart,
            windowEnd = now
        )

        aggregateDailyAppUsageUseCase(
            fromTimestamp = dayWindowStart,
            toTimestamp = now
        )

        return RefreshWindow(
            recentWindowStart = recentWindowStart,
            recentWindowEnd = now,
            chartFromTimestamp = now - TWENTY_FOUR_HOURS_MILLIS,
        )
    }

    private companion object {
        const val FIVE_MINUTES_MILLIS = 5 * 60 * 1000L
        const val ONE_DAY_MILLIS = 24 * 60 * 60 * 1000L
        const val TWENTY_FOUR_HOURS_MILLIS = 24 * 60 * 60 * 1000L
    }
}

data class RefreshWindow(
    val recentWindowStart: Long,
    val recentWindowEnd: Long,
    val chartFromTimestamp: Long,
)