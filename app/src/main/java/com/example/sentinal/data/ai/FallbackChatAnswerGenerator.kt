package com.example.sentinal.data.ai

import android.util.Log
import com.example.sentinal.domain.ai.ChatAnswerGenerator
import com.example.sentinal.domain.model.ChatAnswer
import com.example.sentinal.domain.model.ChatContext
import com.example.sentinal.domain.model.ChatIntent
import com.example.sentinal.domain.model.ModelTier
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

class FallbackChatAnswerGenerator @Inject constructor(
    private val modelAvailabilityChecker: ModelAvailabilityChecker,
    private val geminiNanoChatAnswerGenerator: GeminiNanoChatAnswerGenerator,
    private val gemmaChatAnswerGenerator: GemmaChatAnswerGenerator,
    private val templateChatAnswerGenerator: TemplateChatAnswerGenerator,
) : ChatAnswerGenerator {

    override suspend fun generateAnswer(
        question: String,
        intent: ChatIntent,
        context: ChatContext,
    ): ChatAnswer {
        val tier = modelAvailabilityChecker.getAvailableChatTier()
        Log.d(TAG, "selectedTier=$tier, intent=$intent")

        return when (tier) {
            ModelTier.GeminiNano -> {
                Log.d(TAG, "trying Gemini Nano")
                val geminiAnswer = withTimeoutOrNull(2_000L) {
                    geminiNanoChatAnswerGenerator.tryGenerate(
                        question = question,
                        intent = intent,
                        context = context,
                    )
                }

                if (geminiAnswer != null) {
                    Log.d(TAG, "Gemini Nano success")
                    geminiAnswer
                } else {
                    Log.d(TAG, "Gemini Nano returned null, trying Gemma/Template")
                    tryGemmaOrTemplate(
                        question = question,
                        intent = intent,
                        context = context,
                    )
                }
            }

            ModelTier.Gemma -> {
                Log.d(TAG, "trying Gemma")
                tryGemmaOrTemplate(
                    question = question,
                    intent = intent,
                    context = context,
                )
            }

            ModelTier.Template -> {
                Log.d(TAG, "using Template directly")
                templateChatAnswerGenerator.generateAnswer(
                    question = question,
                    intent = intent,
                    context = context,
                )
            }
        }
    }

    private suspend fun tryGemmaOrTemplate(
        question: String,
        intent: ChatIntent,
        context: ChatContext,
    ): ChatAnswer {
        val gemmaAnswer = withTimeoutOrNull(2_000L) {
            gemmaChatAnswerGenerator.tryGenerate(
                question = question,
                intent = intent,
                context = context,
            )
        }

        return if (gemmaAnswer != null) {
            Log.d(TAG, "Gemma success")
            gemmaAnswer
        } else {
            Log.d(TAG, "Gemma returned null, fallback to Template")
            templateChatAnswerGenerator.generateAnswer(
                question = question,
                intent = intent,
                context = context,
            )
        }
    }

    private companion object {
        const val TAG = "SentinAI-FallbackChat"
    }
}
