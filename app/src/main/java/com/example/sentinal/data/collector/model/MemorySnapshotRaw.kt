package com.example.sentinal.data.collector.model

data class MemorySnapshotRaw(
    val timestamp:Long,
    val availableMemBytes: Long,
    val totalMemBytes: Long,
    val lowMemory: Boolean,
)
