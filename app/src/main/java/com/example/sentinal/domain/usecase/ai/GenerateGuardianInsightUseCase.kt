package com.example.sentinal.domain.usecase.ai

import com.example.sentinal.domain.ai.InsightGenerator
import com.example.sentinal.domain.model.GuardianResult
import com.example.sentinal.domain.model.InsightSummary
import javax.inject.Inject

class GenerateGuardianInsightUseCase @Inject constructor(
    private val insightGenerator: InsightGenerator
) {
    suspend operator fun invoke(result: GuardianResult): InsightSummary{
        return insightGenerator.generateGuardianInsight(result)
    }
}