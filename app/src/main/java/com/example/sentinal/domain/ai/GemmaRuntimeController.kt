package com.example.sentinal.domain.ai

import com.example.sentinal.domain.model.GemmaRuntimeStatus

interface GemmaRuntimeController {
    fun getStatus(): GemmaRuntimeStatus

    fun release()
}
