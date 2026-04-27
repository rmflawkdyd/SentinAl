package com.example.sentinal.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "device_aggregate_5min",
    indices = [Index("windowStart")]
)
data class DeviceAggregate5MinEntity(
    @PrimaryKey val windowStart: Long,
    val windowEnd: Long,
    val avgAvailableMemPercent: Float,
    val appSwitchCount:Int,
    val foregroundAppCount: Int,
    val topPackageName: String?,
    val updatedAt: Long
)
