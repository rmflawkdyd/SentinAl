package com.example.sentinal.domain.ai

import com.example.sentinal.domain.model.ChatAnswer
import com.example.sentinal.domain.model.ChatContext
import com.example.sentinal.domain.model.ChatIntent
import com.example.sentinal.domain.model.GeminiNanoRuntimeStatus
import com.example.sentinal.domain.model.GuardianResult
import com.example.sentinal.domain.model.InsightSummary

interface GeminiNanoProvider {
    fun isAvailable(): Boolean

    fun getStatus(): GeminiNanoRuntimeStatus

    suspend fun generateChatAnswer(
        question: String,
        intent: ChatIntent,
        context: ChatContext,
    ): ChatAnswer?

    suspend fun generateGuardianInsight(
        result: GuardianResult,
    ): InsightSummary?

    fun release()
}
