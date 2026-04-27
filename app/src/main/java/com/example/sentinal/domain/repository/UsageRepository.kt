package com.example.sentinal.domain.repository

import com.example.sentinal.data.local.entity.UsageEventEntity
import kotlinx.coroutines.flow.Flow

interface UsageRepository {
    suspend fun insertUsageEvents(events:List<UsageEventEntity>)
    fun observeRecentUsageEvents(fromTimestamp:Long): Flow<List<UsageEventEntity>>
    suspend fun getUsageEventsByPackage(
        packageName: String,
        fromTimestamp: Long
    ):List<UsageEventEntity>
}