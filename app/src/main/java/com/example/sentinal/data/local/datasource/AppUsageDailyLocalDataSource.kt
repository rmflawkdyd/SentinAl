package com.example.sentinal.data.local.datasource


import com.example.sentinal.data.local.dao.AppUsageDailyDao
import com.example.sentinal.data.local.entity.AppUsageDailyEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppUsageDailyLocalDataSource @Inject constructor(
    private val appUsageDailyDao: AppUsageDailyDao
) {
    suspend fun upsertDailyUsage(items: List<AppUsageDailyEntity>) {
        appUsageDailyDao.upsertDailyUsage(items)
    }

    fun observeDailyUsageRange(
        fromDay: Long,
        toDay: Long,
    ): Flow<List<AppUsageDailyEntity>> {
        return appUsageDailyDao.observeDailyUsageRange(fromDay, toDay)
    }

    suspend fun getDailyUsageByPackage(
        packageName: String,
        fromDay: Long,
        toDay: Long,
    ): List<AppUsageDailyEntity>{
        return appUsageDailyDao.getDailyUsageByPackage(packageName,fromDay,toDay)
    }

}