package com.example.sentinal.data.ai

interface GemmaRuntime {
    fun isReady(): Boolean

    suspend fun generate(
        prompt: String,
    ): GemmaInferenceResult

    fun release()
}
