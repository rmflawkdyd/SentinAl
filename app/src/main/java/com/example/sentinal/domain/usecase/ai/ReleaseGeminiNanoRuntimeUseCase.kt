package com.example.sentinal.domain.usecase.ai

import com.example.sentinal.domain.ai.GeminiNanoProvider
import javax.inject.Inject

class ReleaseGeminiNanoRuntimeUseCase @Inject constructor(
    private val geminiNanoProvider: GeminiNanoProvider,
) {
    operator fun invoke() {
        geminiNanoProvider.release()
    }
}
