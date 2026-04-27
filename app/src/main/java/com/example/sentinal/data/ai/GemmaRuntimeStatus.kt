package com.example.sentinal.data.ai

sealed interface GemmaRuntimeStatus {
    data object Ready : GemmaRuntimeStatus
    data object Disabled : GemmaRuntimeStatus
    data object ModelMissing : GemmaRuntimeStatus
    data object RuntimeNotReady : GemmaRuntimeStatus
}