package com.example.sentinal.domain.model

sealed interface GemmaRuntimeStatus {
    data object Ready : GemmaRuntimeStatus
    data object Disabled : GemmaRuntimeStatus
    data object ModelMissing : GemmaRuntimeStatus
    data object RuntimeNotReady : GemmaRuntimeStatus
}
