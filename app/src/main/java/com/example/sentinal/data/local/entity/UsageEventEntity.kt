package com.example.sentinal.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "usage_events",
    indices = [Index("timestamp"), Index(value = ["packageName", "timestamp"])]
)
data class UsageEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val packageName: String,
    val className: String?,
    val eventType: Int,
    val createdAt: Long
)
