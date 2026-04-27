package com.example.sentinal.domain.usecase.collector

import com.example.sentinal.data.collector.MemoryInfoCollector
import com.example.sentinal.data.collector.mapper.MemorySnapshotMapper
import com.example.sentinal.domain.repository.MemorySnapshotRepository
import javax.inject.Inject

class CollectMemorySnapshotUseCase @Inject constructor(
    private val memoryInfoCollector: MemoryInfoCollector,
    private val memorySnapshotMapper: MemorySnapshotMapper,
    private val memorySnapshotRepository: MemorySnapshotRepository,
) {
    suspend operator fun invoke() {
        val raw = memoryInfoCollector.collect()
        val entity = memorySnapshotMapper.map(raw)
        memorySnapshotRepository.insertMemorySnapshots(listOf(entity))
    }

}