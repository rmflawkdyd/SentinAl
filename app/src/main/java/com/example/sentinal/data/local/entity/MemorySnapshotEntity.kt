package com.example.sentinal.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "memory_snapshots",
    indices = [Index("timestamp")]
)
data class MemorySnapshotEntity(
    @PrimaryKey val timestamp: Long,
    val availableMemBytes: Long,
    val totalMemBytes: Long,
    val availablePercentage: Float,
    val lowMemory: Boolean
)
