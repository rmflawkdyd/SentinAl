package com.example.sentinal.data.collector.mapper

import com.example.sentinal.data.collector.model.MemorySnapshotRaw
import com.example.sentinal.data.local.entity.MemorySnapshotEntity
import javax.inject.Inject

class MemorySnapshotMapper @Inject constructor() {

    fun map(raw: MemorySnapshotRaw): MemorySnapshotEntity{
        val availablePercentage = if(raw.totalMemBytes>0L) {
            (raw.availableMemBytes.toFloat() / raw.totalMemBytes.toFloat()) * 100f
        } else {
            0f
        }

        return MemorySnapshotEntity(
            timestamp = raw.timestamp,
            availableMemBytes = raw.availableMemBytes,
            totalMemBytes = raw.totalMemBytes,
            availablePercentage = availablePercentage,
            lowMemory = raw.lowMemory,
        )
    }
}
