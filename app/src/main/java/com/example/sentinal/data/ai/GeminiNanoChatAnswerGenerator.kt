package com.example.sentinal.data.ai

import com.example.sentinal.domain.ai.GeminiNanoProvider
import com.example.sentinal.domain.model.ChatAnswer
import com.example.sentinal.domain.model.ChatContext
import com.example.sentinal.domain.model.ChatIntent
import javax.inject.Inject

class GeminiNanoChatAnswerGenerator @Inject constructor(
    private val geminiNanoProvider: GeminiNanoProvider,
) {
    suspend fun tryGenerate(
        question: String,
        intent: ChatIntent,
        context: ChatContext,
    ): ChatAnswer? {
        return geminiNanoProvider.generateChatAnswer(
            question = question,
            intent = intent,
            context = context,
        )
    }
}
