package com.example.sentinal.domain.model

data class RecentDeviceAggregate(
    val windowStart: Long,
    val windowEnd: Long,
    val avgAvailableMemPercent: Float,
    val appSwitchCount: Int,
    val foregroundAppCount:Int,
    val topPackageName: String?,
)
