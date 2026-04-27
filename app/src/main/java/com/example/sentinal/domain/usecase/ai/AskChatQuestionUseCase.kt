package com.example.sentinal.domain.usecase.ai

import com.example.sentinal.domain.ai.ChatAnswerGenerator
import com.example.sentinal.domain.model.ChatAnswer
import com.example.sentinal.domain.rule.ChatQuestionClassifier
import javax.inject.Inject

class AskChatQuestionUseCase @Inject constructor(
    private val buildChatContextUseCase: BuildChatContextUseCase,
    private val chatQuestionClassifier: ChatQuestionClassifier,
    private val chatAnswerGenerator: ChatAnswerGenerator,
) {
    suspend operator fun invoke(question: String): ChatAnswer {
        val intent = chatQuestionClassifier.classify(question)
        val context = buildChatContextUseCase()

        return chatAnswerGenerator.generateAnswer(
            question = question,
            intent = intent,
            context = context
        )
    }
}