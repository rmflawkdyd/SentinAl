package com.example.sentinal.domain.model

data class GuardianChartPoint(
    val timestamp: Long,
    val availableMemPercent: Float,
    val appSwitchCount: Int,
)
