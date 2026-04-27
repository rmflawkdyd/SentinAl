package com.example.sentinal.data.ai

import javax.inject.Inject

class NoOpGemmaInferenceEngine @Inject constructor() : GemmaInferenceEngine {

    override fun isInitialized(): Boolean = false

    override suspend fun infer(
        prompt: String,
    ): String {
        throw UnsupportedOperationException(
            "Gemma LiteRT inference engine is not connected yet. Prompt length=${prompt.length}"
        )
    }

    override fun release() = Unit
}
