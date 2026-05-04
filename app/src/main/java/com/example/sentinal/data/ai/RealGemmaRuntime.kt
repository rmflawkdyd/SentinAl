package com.example.sentinal.data.ai

import android.content.Context
import com.example.sentinal.BuildConfig
import com.example.sentinal.domain.ai.GemmaRuntimeController
import com.example.sentinal.domain.model.GemmaRuntimeStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RealGemmaRuntime @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val gemmaInferenceEngine: GemmaInferenceEngine,
) : GemmaRuntime, GemmaRuntimeController {

    override fun isReady(): Boolean {
        return getStatus() is GemmaRuntimeStatus.Ready
    }

    override fun getStatus(): GemmaRuntimeStatus {
        if (!BuildConfig.ENABLE_GEMMA) {
            return GemmaRuntimeStatus.Disabled
        }

        if (!hasModelAsset(AiModelPaths.GEMMA_MODEL_ASSET_PATH)) {
            return GemmaRuntimeStatus.ModelMissing
        }

        if (!gemmaInferenceEngine.isInitialized()) {
            return GemmaRuntimeStatus.RuntimeNotReady
        }

        return GemmaRuntimeStatus.Ready
    }

    override suspend fun generate(
        prompt: String,
    ): GemmaInferenceResult = withContext(Dispatchers.IO) {
        if (getStatus() !is GemmaRuntimeStatus.Ready) {
            return@withContext GemmaInferenceResult.ModelNotReady
        }

        runCatching {
            val output = runInference(prompt)
            if (output.isBlank()) {
                GemmaInferenceResult.EmptyOutput
            } else {
                GemmaInferenceResult.Success(output)
            }
        }.getOrElse { throwable ->
            GemmaInferenceResult.Failure(throwable)
        }
    }

    override fun release() {
        gemmaInferenceEngine.release()
    }

    private fun hasModelAsset(assetPath: String): Boolean {
        return runCatching {
            context.assets.open(assetPath).use { input ->
                input.available() >= 0
            }
        }.getOrElse {
            false
        }
    }

    private suspend fun runInference(prompt: String): String {
        return gemmaInferenceEngine.infer(prompt)
    }
}
