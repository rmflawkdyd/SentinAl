package com.example.sentinal.data.ai

class PlaceholderGemmaLiteRtSession : GemmaLiteRtSession {
    override suspend fun generate(
        prompt: String,
    ): String {
        throw UnsupportedOperationException(
            "LiteRT session is not connected yet. Prompt length=${prompt.length}"
        )
    }

    override fun close() = Unit
}
