package com.example.sentinal.data.ai

import com.example.sentinal.domain.ai.GeminiNanoProvider
import com.example.sentinal.domain.model.GuardianResult
import com.example.sentinal.domain.model.InsightSummary
import javax.inject.Inject

class GeminiNanoInsightGenerator @Inject constructor(
    private val geminiNanoProvider: GeminiNanoProvider,
) {

    suspend fun tryGenerate(
        result: GuardianResult,
    ): InsightSummary? {
        return geminiNanoProvider.generateGuardianInsight(result)
    }
}
