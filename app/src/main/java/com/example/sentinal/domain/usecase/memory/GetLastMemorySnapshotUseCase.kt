package com.example.sentinal.domain.usecase.memory

import com.example.sentinal.data.local.entity.MemorySnapshotEntity
import com.example.sentinal.domain.repository.MemorySnapshotRepository
import javax.inject.Inject

class GetLastMemorySnapshotUseCase @Inject constructor(
    private val memorySnapshotRepository: MemorySnapshotRepository
) {
    suspend operator fun invoke(): MemorySnapshotEntity? {
        return memorySnapshotRepository.getLastSnapshot()

    }
}
