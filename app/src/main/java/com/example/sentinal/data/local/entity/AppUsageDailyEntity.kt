package com.example.sentinal.data.local.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "app_usage_daily",
    primaryKeys = ["dateEpochDay","packageName"],
    indices = [
        Index("dateEpochDay"),
        Index(value = ["packageName", "dateEpochDay"])
    ]
)
data class AppUsageDailyEntity(
    val dateEpochDay: Long,
    val packageName: String,
    val usageTimeMillis: Long,
    val nightUsageMillis: Long,
    val launchCount: Int,
    val category: String?
)
