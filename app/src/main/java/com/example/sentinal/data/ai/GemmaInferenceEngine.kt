package com.example.sentinal.data.ai

interface GemmaInferenceEngine {
    fun isInitialized(): Boolean

    suspend fun infer(
        prompt: String,
    ): String

    fun release()
}
