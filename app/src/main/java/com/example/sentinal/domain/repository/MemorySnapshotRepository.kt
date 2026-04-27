package com.example.sentinal.domain.repository

import com.example.sentinal.data.local.entity.MemorySnapshotEntity
import kotlinx.coroutines.flow.Flow

interface MemorySnapshotRepository {
    suspend fun insertMemorySnapshots(snapshots: List<MemorySnapshotEntity>)
    fun observeMemorySnapshots(fromTimestamp: Long): Flow<List<MemorySnapshotEntity>>
    suspend fun getLastSnapshot(): MemorySnapshotEntity?
}