package com.example.sentinal.domain.usecase.ai

import com.example.sentinal.domain.ai.GemmaRuntimeController
import javax.inject.Inject

class ReleaseGemmaRuntimeUseCase @Inject constructor(
    private val gemmaRuntimeController: GemmaRuntimeController,
) {
    operator fun invoke() {
        gemmaRuntimeController.release()
    }
}
