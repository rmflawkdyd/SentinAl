package com.example.sentinal.data.ai

import com.example.sentinal.domain.ai.GemmaProvider
import com.example.sentinal.domain.model.ChatAnswer
import com.example.sentinal.domain.model.ChatContext
import com.example.sentinal.domain.model.ChatIntent
import javax.inject.Inject

class GemmaChatAnswerGenerator @Inject constructor(
    private val gemmaProvider: GemmaProvider,
) {
    suspend fun tryGenerate(
        question: String,
        intent: ChatIntent,
        context: ChatContext,
    ): ChatAnswer? {
        return gemmaProvider.generateChatAnswer(
            question = question,
            intent = intent,
            context = context,
        )
    }
}
