package com.example.sentinal.data.ai

import com.example.sentinal.domain.ai.InsightGenerator
import com.example.sentinal.domain.model.GuardianResult
import com.example.sentinal.domain.model.GuardianStatus
import com.example.sentinal.domain.model.InsightSummary
import com.example.sentinal.domain.model.ModelTier
import javax.inject.Inject

class TemplateInsightGenerator @Inject constructor(): InsightGenerator {
    override suspend fun generateGuardianInsight(result: GuardianResult): InsightSummary {
        val title = when (result.status) {
            GuardianStatus.NORMAL -> "안정적인 상태"
            GuardianStatus.CAUTION -> "주의가 필요한 상태"
            GuardianStatus.DANGER -> "위험 상태"
        }

        val body = when (result.status) {
            GuardianStatus.NORMAL -> {
                "최근 5분 동안 특이 징후 없이 안정적인 상태입니다. " +
                        "메모리 여유는 ${result.avgAvailableMemPercent.format(1)}%이며, " +
                        "앱 전환은 ${result.appSwitchCount}회였습니다."
            }

            GuardianStatus.CAUTION -> {
                "최근 5분 동안 주의가 필요한 패턴이 감지되었습니다. " +
                        "메모리 여유는 ${result.avgAvailableMemPercent.format(1)}%이고, " +
                        "앱 전환은 ${result.appSwitchCount}회였습니다. " +
                        result.insight
            }

            GuardianStatus.DANGER -> {
                "최근 5분 동안 위험 수준의 패턴이 감지되었습니다. " +
                        "즉시 상태를 확인하는 것이 좋습니다. " +
                        result.insight
            }
        }

        return InsightSummary(
            title = title,
            body = body,
            source = "Template",
            modelTier = ModelTier.Template,
        )
    }

    private fun Float.format(digits: Int): String {
        return "%.${digits}f".format(this)
    }
}