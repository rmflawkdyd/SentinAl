package com.example.sentinal.domain.usecase.ai

import com.example.sentinal.domain.ai.GeminiNanoProvider
import com.example.sentinal.domain.model.GeminiNanoRuntimeStatus
import javax.inject.Inject

class GetGeminiNanoRuntimeStatusUseCase @Inject constructor(
    private val geminiNanoProvider: GeminiNanoProvider,
) {
    operator fun invoke(): GeminiNanoRuntimeStatus {
        return geminiNanoProvider.getStatus()
    }
}
