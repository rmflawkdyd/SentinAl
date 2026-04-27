package com.example.sentinal.domain.usecase.ai

import com.example.sentinal.data.ai.GeminiNanoRuntimeStatus
import com.example.sentinal.data.ai.RealGeminiNanoProvider
import javax.inject.Inject

class GetGeminiNanoRuntimeStatusUseCase @Inject constructor(
    private val realGeminiNanoProvider: RealGeminiNanoProvider,
) {
    operator fun invoke(): GeminiNanoRuntimeStatus {
        return realGeminiNanoProvider.getStatus()
    }
}
