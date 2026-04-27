package com.example.sentinal.data.ai

import com.example.sentinal.domain.ai.GeminiNanoProvider
import com.example.sentinal.domain.model.ChatAnswer
import com.example.sentinal.domain.model.ChatContext
import com.example.sentinal.domain.model.ChatIntent
import com.example.sentinal.domain.model.GuardianResult
import com.example.sentinal.domain.model.InsightSummary
import javax.inject.Inject

class NoOpGeminiNanoProvider @Inject constructor() : GeminiNanoProvider {

    override fun isAvailable(): Boolean = false

    override suspend fun generateChatAnswer(
        question: String,
        intent: ChatIntent,
        context: ChatContext,
    ): ChatAnswer? = null

    override suspend fun generateGuardianInsight(
        result: GuardianResult,
    ): InsightSummary? = null

    override fun release() = Unit
}
