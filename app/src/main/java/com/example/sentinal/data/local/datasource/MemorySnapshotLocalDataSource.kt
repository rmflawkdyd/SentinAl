package com.example.sentinal.data.local.datasource

import com.example.sentinal.data.local.dao.MemorySnapshotDao
import com.example.sentinal.data.local.entity.MemorySnapshotEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MemorySnapshotLocalDataSource @Inject constructor(
    private val memorySnapshotDao: MemorySnapshotDao
){
    suspend fun insertMemorySnapshots(snapshots: List<MemorySnapshotEntity>){
        memorySnapshotDao.insertMemorySnapshots(snapshots)
    }

    fun observeMemorySnapshots(fromTimestamp: Long): Flow<List<MemorySnapshotEntity>>{
        return memorySnapshotDao.observeMemorySnapshots(fromTimestamp)
    }

    suspend fun getLastSnapshot(): MemorySnapshotEntity?{
        return memorySnapshotDao.getLastSnapshot()
    }

}