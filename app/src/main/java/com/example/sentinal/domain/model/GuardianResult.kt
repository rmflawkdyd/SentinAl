package com.example.sentinal.domain.model

data class GuardianResult(
    val score:Int,
    val status: GuardianStatus,
    val insight: String,
    val avgAvailableMemPercent: Float,
    val appSwitchCount:Int,
    val windowStart: Long,
    val windowEnd: Long
)
