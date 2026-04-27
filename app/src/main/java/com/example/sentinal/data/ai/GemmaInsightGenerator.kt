package com.example.sentinal.data.ai

import com.example.sentinal.domain.ai.GemmaProvider
import com.example.sentinal.domain.model.GuardianResult
import com.example.sentinal.domain.model.InsightSummary
import javax.inject.Inject

class GemmaInsightGenerator @Inject constructor(
    private val gemmaProvider: GemmaProvider,
) {

    suspend fun tryGenerate(
        result: GuardianResult,
    ): InsightSummary? {
        return gemmaProvider.generateGuardianInsight(result)
    }
}
