package com.example.sentinal.domain.usecase.memory

import com.example.sentinal.data.local.entity.MemorySnapshotEntity
import com.example.sentinal.domain.repository.MemorySnapshotRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveMemorySnapshotsUseCase @Inject constructor(
    private val memorySnapshotRepository: MemorySnapshotRepository
) {
    operator fun invoke(fromTimestamp: Long): Flow<List<MemorySnapshotEntity>>{
        return memorySnapshotRepository.observeMemorySnapshots(fromTimestamp)
    }
}
