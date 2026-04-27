package com.example.sentinal.domain.usecase.aggregate

import android.app.usage.UsageEvents
import com.example.sentinal.data.local.entity.DeviceAggregate5MinEntity
import com.example.sentinal.domain.repository.DeviceAggregateRepository
import com.example.sentinal.domain.repository.MemorySnapshotRepository
import com.example.sentinal.domain.repository.UsageRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AggregateRecentDeviceDataUseCase @Inject constructor(
    private val usageRepository: UsageRepository,
    private val memorySnapshotRepository: MemorySnapshotRepository,
    private val deviceAggregateRepository: DeviceAggregateRepository,
) {
    suspend operator fun invoke(
        windowStart: Long,
        windowEnd: Long,
    ){
        val usageEvents = usageRepository.observeRecentUsageEvents(windowStart)
            .first()
            .filter { it.timestamp in windowStart..windowEnd }

        val memorySnapshots = memorySnapshotRepository.observeMemorySnapshots(windowStart)
            .first()
            .filter { it.timestamp in windowStart..windowEnd }

        val avgAvailableMemPercent = memorySnapshots.map { it.availablePercentage }
            .average()
            .toFloat()
            .let{if(it.isNaN()) 0f else it}

        val foregroundEvents = usageEvents.filter {
            it.eventType == UsageEvents.Event.ACTIVITY_RESUMED
        }

        val appSwitchCount = if(foregroundEvents.isEmpty()){
            0
        }else{
            foregroundEvents
                .zipWithNext()
                .count { (prev, next) -> prev.packageName != next.packageName }
        }

        val topPackageName = foregroundEvents
            .groupingBy { it.packageName }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key

        val entity = DeviceAggregate5MinEntity(
            windowStart = windowStart,
            windowEnd = windowEnd,
            avgAvailableMemPercent = avgAvailableMemPercent,
            appSwitchCount = appSwitchCount,
            foregroundAppCount = foregroundEvents.size,
            topPackageName = topPackageName,
            updatedAt = System.currentTimeMillis(),
        )

        deviceAggregateRepository.upsertAggregate(entity)
    }

}