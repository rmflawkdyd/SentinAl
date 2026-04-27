package com.example.sentinal.domain.repository

import com.example.sentinal.data.local.entity.AppUsageDailyEntity
import kotlinx.coroutines.flow.Flow

interface AppUsageDailyRepository {
    suspend fun upsertDailyUsage(items: List<AppUsageDailyEntity>)
    fun observeDailyUsageRange(
        fromDay: Long,
        toDay: Long
    ): Flow<List<AppUsageDailyEntity>>

    suspend fun getDailyUsageByPackage(
        packageName: String,
        fromDay: Long,
        toDay: Long
    ): List<AppUsageDailyEntity>
}