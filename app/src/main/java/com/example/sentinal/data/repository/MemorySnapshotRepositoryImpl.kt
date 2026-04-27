package com.example.sentinal.data.repository

import com.example.sentinal.data.local.datasource.MemorySnapshotLocalDataSource
import com.example.sentinal.data.local.entity.MemorySnapshotEntity
import com.example.sentinal.domain.repository.MemorySnapshotRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MemorySnapshotRepositoryImpl @Inject constructor(
    private val memorySnapshotLocalDataSource: MemorySnapshotLocalDataSource
): MemorySnapshotRepository {
    override suspend fun insertMemorySnapshots(snapshots: List<MemorySnapshotEntity>) {
        memorySnapshotLocalDataSource.insertMemorySnapshots(snapshots)
    }

    override fun observeMemorySnapshots(fromTimestamp: Long): Flow<List<MemorySnapshotEntity>> {
       return memorySnapshotLocalDataSource.observeMemorySnapshots(fromTimestamp)
    }

    override suspend fun getLastSnapshot(): MemorySnapshotEntity? {
        return memorySnapshotLocalDataSource.getLastSnapshot()
    }
}
