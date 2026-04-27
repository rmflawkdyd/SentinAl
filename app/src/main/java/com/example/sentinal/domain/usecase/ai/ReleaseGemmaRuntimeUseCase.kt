package com.example.sentinal.domain.usecase.ai

import com.example.sentinal.data.ai.GemmaRuntime
import javax.inject.Inject

class ReleaseGemmaRuntimeUseCase @Inject constructor(
    private val gemmaRuntime: GemmaRuntime,
) {
    operator fun invoke() {
        gemmaRuntime.release()
    }
}
