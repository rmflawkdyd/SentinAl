package com.example.sentinal.domain.usecase.ai

import com.example.sentinal.domain.ai.GemmaRuntimeController
import com.example.sentinal.domain.model.GemmaRuntimeStatus
import javax.inject.Inject

class GetGemmaRuntimeStatusUseCase @Inject constructor(
    private val gemmaRuntimeController: GemmaRuntimeController,
) {
    operator fun invoke(): GemmaRuntimeStatus {
        return gemmaRuntimeController.getStatus()
    }
}
