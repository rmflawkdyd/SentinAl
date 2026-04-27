package com.example.sentinal.data.ai

sealed interface GemmaInferenceResult {
    data class Success(
        val text: String,
    ) : GemmaInferenceResult

    data object ModelNotReady : GemmaInferenceResult
    data object EmptyOutput : GemmaInferenceResult

    data class Failure(
        val throwable: Throwable,
    ) : GemmaInferenceResult
}
