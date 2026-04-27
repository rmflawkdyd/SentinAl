package com.example.sentinal.domain.model

data class DailyAppUsageAggregate(
    val dateEpochDay: Long,
    val packageName: String,
    val usageTimeMillis: Long,
    val launchCount: Int,
    val category: String?
)
