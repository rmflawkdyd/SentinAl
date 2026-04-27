package com.example.sentinal.domain.usecase.collector

import com.example.sentinal.domain.usecase.permission.HasUsageStatsPermissionUseCase
import javax.inject.Inject

class CollectDeviceDataUseCase @Inject constructor(
    private val hasUsageStatsPermissionUseCase: HasUsageStatsPermissionUseCase,
    private val collectUsageEventsUseCase: CollectUsageEventsUseCase,
    private val collectMemorySnapshotUseCase: CollectMemorySnapshotUseCase,
) {
    suspend operator fun invoke(fromTimestamp: Long, toTimestamp: Long) {
        if(hasUsageStatsPermissionUseCase()){
            collectUsageEventsUseCase(fromTimestamp, toTimestamp)
        }

        collectMemorySnapshotUseCase()
    }
}