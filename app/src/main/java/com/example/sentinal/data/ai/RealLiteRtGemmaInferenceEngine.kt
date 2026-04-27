package com.example.sentinal.data.ai

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class RealLiteRtGemmaInferenceEngine @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : GemmaInferenceEngine {

    private val sessionMutex = Mutex()

    @Volatile
    private var session: GemmaLiteRtSession? = null

    override fun isInitialized(): Boolean {
        return hasModelAsset(AiModelPaths.GEMMA_MODEL_ASSET_PATH)
    }

    override suspend fun infer(prompt: String): String = withContext(Dispatchers.IO) {
        val activeSession = getOrCreateSession()
        return@withContext activeSession.generate(prompt).trim()
    }

    override fun release() {
        session?.close()
        session = null
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

    private suspend fun getOrCreateSession(): GemmaLiteRtSession {
        session?.let { return it }

        return sessionMutex.withLock {
            session?.let { return it }

            val modelFile = ensureModelFile()
            val created = createSession(modelFile)
            session = created
            created
        }
    }

    private fun ensureModelFile(): File {
        val targetFile = File(context.filesDir, "models/gemma-task.litertlm")

        if (targetFile.exists() && targetFile.length() > 0L) {
            return targetFile
        }

        targetFile.parentFile?.mkdirs()

        context.assets.open(AiModelPaths.GEMMA_MODEL_ASSET_PATH).use { input ->
            FileOutputStream(targetFile).use { output ->
                input.copyTo(output)
            }
        }

        return targetFile
    }

    private fun createSession(modelFile: File): GemmaLiteRtSession {
        val options = LlmInference.LlmInferenceOptions.builder()
            .setModelPath(modelFile.absolutePath)
            .setMaxTokens(256)
            .setMaxTopK(40)
            .build()

        val llmInference = LlmInference.createFromOptions(context,options)

        return object : GemmaLiteRtSession{
            override suspend fun generate(prompt: String): String {
                return llmInference.generateResponse(prompt)
            }

            override fun close() {
                llmInference.close()
            }
        }
    }
}
