package com.example.sentinal.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sentinal.data.local.entity.DeviceAggregate5MinEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceAggregateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAggregate(entity: DeviceAggregate5MinEntity)

    @Query(
        """
            SELECT * FROM device_aggregate_5min
            WHERE windowStart >= :fromWindowStart
            ORDER BY windowStart ASC
        """
    )

    fun observeAggregates(fromWindowStart: Long): Flow<List<DeviceAggregate5MinEntity>>

    @Query(
        """
            SELECT * FROM device_aggregate_5min
            ORDER BY windowStart DESC
            LIMIT 1
        """
    )
    suspend fun getLatestAggregate(): DeviceAggregate5MinEntity?


}