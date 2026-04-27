package com.example.sentinal.domain.rule

import javax.inject.Inject

class AnalyticsRuleEngine @Inject constructor() {
    fun calculateChangeRatePercent(
        currentTotalMillis:Long,
        previousTotalMillis: Long,
    ):Float{
        if(previousTotalMillis<=0L) return 0f
        return ((currentTotalMillis - previousTotalMillis).toFloat() /
                previousTotalMillis.toFloat()) * 100f
    }
}