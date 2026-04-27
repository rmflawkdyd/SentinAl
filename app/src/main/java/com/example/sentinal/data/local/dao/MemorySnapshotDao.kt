package com.example.sentinal.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sentinal.data.local.entity.MemorySnapshotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemorySnapshotDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemorySnapshots(snapshots: List<MemorySnapshotEntity>)

    @Query(
        """
            SELECT * FROM memory_snapshots
            WHERE timestamp>= :fromTimestamp
            ORDER BY timestamp ASC
        """
    )
    fun observeMemorySnapshots(fromTimestamp: Long): Flow<List<MemorySnapshotEntity>>

    @Query(
        """
            SELECT * FROM memory_snapshots
            ORDER BY timestamp DESC
            LIMIT 1
        """
    )
    suspend fun getLastSnapshot(): MemorySnapshotEntity?
}