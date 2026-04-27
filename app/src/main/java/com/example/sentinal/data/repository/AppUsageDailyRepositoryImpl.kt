package com.example.sentinal.data.repository

import com.example.sentinal.data.local.datasource.AppUsageDailyLocalDataSource
import com.example.sentinal.data.local.entity.AppUsageDailyEntity
import com.example.sentinal.domain.repository.AppUsageDailyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppUsageDailyRepositoryImpl @Inject constructor(
    private val appUsageDailyLocalDataSource: AppUsageDailyLocalDataSource
): AppUsageDailyRepository {
    override suspend fun upsertDailyUsage(items: List<AppUsageDailyEntity>) {
        appUsageDailyLocalDataSource.upsertDailyUsage(items)
    }

    override fun observeDailyUsageRange(
        fromDay: Long,
        toDay: Long,
    ): Flow<List<AppUsageDailyEntity>> {
        return appUsageDailyLocalDataSource.observeDailyUsageRange(fromDay,toDay)
    }

    override suspend fun getDailyUsageByPackage(
        packageName: String,
        fromDay: Long,
        toDay: Long,
    ): List<AppUsageDailyEntity> {
        return appUsageDailyLocalDataSource.getDailyUsageByPackage(packageName,fromDay,toDay)
    }
}
