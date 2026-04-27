package com.example.sentinal.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sentinal.data.local.entity.AppUsageDailyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppUsageDailyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailyUsage(items:List<AppUsageDailyEntity>)

    @Query(
        """
            SELECT * FROM app_usage_daily
            WHERE dateEpochDay BETWEEN :fromDay AND :toDay
            ORDER BY dateEpochDay ASC
        """
    )
    fun observeDailyUsageRange(
        fromDay: Long,
        toDay: Long,
    ): Flow<List<AppUsageDailyEntity>>

    @Query("""
        SELECT * FROM app_usage_daily
        WHERE packageName =:packageName
        AND dateEpochDay BETWEEN :fromDay AND :toDay
        ORDER BY dateEpochDay ASC
    """)

    suspend fun getDailyUsageByPackage(
        packageName: String,
        fromDay: Long,
        toDay: Long,
    ): List<AppUsageDailyEntity>
}