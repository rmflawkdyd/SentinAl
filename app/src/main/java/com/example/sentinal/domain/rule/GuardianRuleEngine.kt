package com.example.sentinal.domain.rule

import com.example.sentinal.data.local.entity.DeviceAggregate5MinEntity
import com.example.sentinal.domain.model.GuardianResult
import com.example.sentinal.domain.model.GuardianStatus
import javax.inject.Inject

class GuardianRuleEngine @Inject constructor() {
    fun evaluate(aggregate: DeviceAggregate5MinEntity): GuardianResult {
        val memIsLow = aggregate.avgAvailableMemPercent<15f

        val durationSeconds =((aggregate.windowEnd - aggregate.windowStart) / 1000f)
            .takeIf { it > 0f } ?: 1f

        val appSwitchPerSecond = aggregate.appSwitchCount/durationSeconds
        val appSwitchIsHigh = appSwitchPerSecond>3f

        val status = when{
            memIsLow || appSwitchIsHigh-> GuardianStatus.CAUTION
            else-> GuardianStatus.NORMAL
        }

        val score = when(status){
            GuardianStatus.NORMAL ->90
            GuardianStatus.CAUTION ->60
            GuardianStatus.DANGER ->30
        }

        val insight = buildInsight(
            memIsLow = memIsLow,
            appSwitchIsHigh = appSwitchIsHigh,
            avgAvailableMemPercent = aggregate.avgAvailableMemPercent,
            appSwitchCount = aggregate.appSwitchCount
        )

        return GuardianResult(
            score = score,
            status = status,
            insight = insight,
            avgAvailableMemPercent = aggregate.avgAvailableMemPercent,
            appSwitchCount = aggregate.appSwitchCount,
            windowStart = aggregate.windowStart,
            windowEnd = aggregate.windowEnd,
        )


    }

    private fun buildInsight(
        memIsLow: Boolean,
        appSwitchIsHigh: Boolean,
        avgAvailableMemPercent: Float,
        appSwitchCount: Int,
    ): String {
        return when {
            memIsLow && appSwitchIsHigh ->
                "메모리 여유가 낮고 앱 전환이 잦아 주의가 필요합니다."
            memIsLow ->
                "메모리 여유가 ${avgAvailableMemPercent.format(1)}%로 낮아 주의가 필요합니다."
            appSwitchIsHigh ->
                "최근 앱 전환이 ${appSwitchCount}회로 많아 집중 저하 가능성이 있습니다."
            else ->
                "최근 5분 동안 특이 징후 없이 안정적인 상태입니다."
        }
    }

    private fun Float.format(digits: Int): String {
        return "%.${digits}f".format(this)
    }
}