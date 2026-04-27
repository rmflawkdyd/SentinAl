package com.example.sentinal.data.ai

sealed interface GeminiNanoRuntimeStatus {
    data object Ready : GeminiNanoRuntimeStatus
    data object Disabled : GeminiNanoRuntimeStatus
    data object UnsupportedAndroid : GeminiNanoRuntimeStatus
    data object AiCoreMissing : GeminiNanoRuntimeStatus
    data object RuntimeNotReady : GeminiNanoRuntimeStatus
}
