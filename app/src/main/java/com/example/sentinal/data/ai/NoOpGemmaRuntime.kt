package com.example.sentinal.data.ai

import javax.inject.Inject

class NoOpGemmaRuntime @Inject constructor() : GemmaRuntime {

    override fun isReady(): Boolean = false

    override suspend fun generate(
        prompt: String,
    ): GemmaInferenceResult {
        return GemmaInferenceResult.ModelNotReady
    }

    override fun release() = Unit
}
