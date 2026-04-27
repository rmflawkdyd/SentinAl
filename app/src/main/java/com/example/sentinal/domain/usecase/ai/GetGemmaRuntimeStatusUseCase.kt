package com.example.sentinal.domain.usecase.ai

import com.example.sentinal.BuildConfig
import com.example.sentinal.data.ai.GemmaRuntimeStatus
import com.example.sentinal.data.ai.RealGemmaRuntime
import javax.inject.Inject

class GetGemmaRuntimeStatusUseCase @Inject constructor(
    private val realGemmaRuntime: RealGemmaRuntime
) {
    operator fun invoke(): GemmaRuntimeStatus{
        if (!BuildConfig.ENABLE_GEMMA) {
            return GemmaRuntimeStatus.Disabled
        }

        return realGemmaRuntime.getStatus()
    }
}