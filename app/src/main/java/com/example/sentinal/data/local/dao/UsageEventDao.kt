package com.example.sentinal.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sentinal.data.local.entity.UsageEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsageEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsageEvents(events:List<UsageEventEntity>)

    @Query("""
        SELECT * FROM usage_events
        WHERE timestamp >= :fromTimestamp
        ORDER BY timestamp ASC
    """)
    fun observeUsageEvents(fromTimestamp: Long): Flow<List<UsageEventEntity>>

    @Query("""
        SELECT * FROM usage_events
        WHERE packageName = :packageName
        AND timestamp >= :fromTimestamp
        ORDER BY timestamp ASC
    """)
    suspend fun getUsageEventByPackage(
        packageName: String,
        fromTimestamp: Long,
    ):List<UsageEventEntity>
}