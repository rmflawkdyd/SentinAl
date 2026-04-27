package com.example.sentinal.domain.ai

import com.example.sentinal.domain.model.GuardianResult
import com.example.sentinal.domain.model.InsightSummary

interface InsightGenerator {
    suspend fun generateGuardianInsight(result: GuardianResult): InsightSummary
}