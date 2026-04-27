package com.example.sentinal.data.ai

interface GemmaLiteRtSession {
    suspend fun generate(
        prompt: String,
    ): String

    fun close()
}
