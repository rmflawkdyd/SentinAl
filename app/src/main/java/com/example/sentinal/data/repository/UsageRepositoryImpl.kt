package com.example.sentinal.data.repository

import com.example.sentinal.data.local.datasource.UsageEventLocalDataSource
import com.example.sentinal.data.local.entity.UsageEventEntity
import com.example.sentinal.domain.repository.UsageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UsageRepositoryImpl @Inject constructor(
    private val usageEventLocalDataSource: UsageEventLocalDataSource,
): UsageRepository {
    override suspend fun insertUsageEvents(events: List<UsageEventEntity>) {
        usageEventLocalDataSource.insert(events)
    }

    override fun observeRecentUsageEvents(fromTimestamp: Long): Flow<List<UsageEventEntity>> {
        return usageEventLocalDataSource.observeRecent(fromTimestamp)
    }

    override suspend fun getUsageEventsByPackage(
        packageName: String,
        fromTimestamp: Long,
    ): List<UsageEventEntity> {
        return usageEventLocalDataSource.getUsageEventByPackage(packageName,fromTimestamp)
    }
}
