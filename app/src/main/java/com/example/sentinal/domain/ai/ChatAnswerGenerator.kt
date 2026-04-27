package com.example.sentinal.domain.ai


import com.example.sentinal.domain.model.ChatAnswer
import com.example.sentinal.domain.model.ChatContext
import com.example.sentinal.domain.model.ChatIntent

interface ChatAnswerGenerator {
    suspend fun generateAnswer(
        question: String,
        intent: ChatIntent,
        context: ChatContext,
    ): ChatAnswer
}