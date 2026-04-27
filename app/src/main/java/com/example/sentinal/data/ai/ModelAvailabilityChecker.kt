package com.example.sentinal.data.ai

import com.example.sentinal.domain.ai.GeminiNanoProvider
import com.example.sentinal.domain.ai.GemmaProvider
import com.example.sentinal.domain.model.ModelTier
import javax.inject.Inject

class ModelAvailabilityChecker @Inject constructor(
    private val geminiNanoProvider: GeminiNanoProvider,
    private val gemmaProvider: GemmaProvider,
) {
    fun isGeminiNanoAvailable(): Boolean {
        return geminiNanoProvider.isAvailable()
    }

    fun isGemmaAvailable(): Boolean {
        return gemmaProvider.isAvailable()
    }

    fun getAvailableChatTier(): ModelTier {
        return when {
            isGeminiNanoAvailable() -> ModelTier.GeminiNano
            isGemmaAvailable() -> ModelTier.Gemma
            else -> ModelTier.Template
        }
    }

    fun getAvailableInsightTier(): ModelTier {
        return when {
            isGeminiNanoAvailable() -> ModelTier.GeminiNano
            isGemmaAvailable() -> ModelTier.Gemma
            else -> ModelTier.Template
        }
    }
}
