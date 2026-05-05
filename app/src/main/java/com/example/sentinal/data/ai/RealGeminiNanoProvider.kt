package com.example.sentinal.data.ai

import android.content.Context
import android.os.Build
import android.util.Log
import com.example.sentinal.BuildConfig
import com.example.sentinal.domain.ai.GeminiNanoProvider
import com.example.sentinal.domain.model.ChatAnswer
import com.example.sentinal.domain.model.ChatContext
import com.example.sentinal.domain.model.ChatIntent
import com.example.sentinal.domain.model.GeminiNanoRuntimeStatus
import com.example.sentinal.domain.model.GuardianResult
import com.example.sentinal.domain.model.InsightSummary
import com.example.sentinal.domain.model.ModelTier
import com.google.mlkit.genai.prompt.Candidate
import com.google.mlkit.genai.prompt.GenerateContentResponse
import com.google.mlkit.genai.prompt.Generation
import com.google.mlkit.genai.prompt.GenerativeModel
import com.google.mlkit.genai.prompt.TextPart
import com.google.mlkit.genai.prompt.generateContentRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class RealGeminiNanoProvider @Inject constructor(
    @param:ApplicationContext private val context: Context,
): GeminiNanoProvider {
    @Volatile
    private var generativeModel: GenerativeModel? = null

    override fun isAvailable(): Boolean {
        return getStatus() is GeminiNanoRuntimeStatus.Ready
    }

    override fun getStatus(): GeminiNanoRuntimeStatus {
        if (!BuildConfig.ENABLE_GEMINI_NANO) {
            Log.d(TAG, "Gemini Nano disabled by BuildConfig")
            return GeminiNanoRuntimeStatus.Disabled
        }

        if (!isSupportedAndroidVersion()) {
            Log.d(TAG, "Gemini Nano unsupported Android version: sdk=${Build.VERSION.SDK_INT}")
            return GeminiNanoRuntimeStatus.UnsupportedAndroid
        }

        if (!hasAiCorePackage()) {
            Log.d(TAG, "Gemini Nano AICore package missing")
            return GeminiNanoRuntimeStatus.AiCoreMissing
        }

        if (!isRuntimeReady()) {
            Log.d(TAG, "Gemini Nano runtime not ready")
            return GeminiNanoRuntimeStatus.RuntimeNotReady
        }

        return GeminiNanoRuntimeStatus.Ready
    }

    override suspend fun generateChatAnswer(
        question: String,
        intent: ChatIntent,
        context: ChatContext,
    ): ChatAnswer? {
        val status = getStatus()
        if (status !is GeminiNanoRuntimeStatus.Ready) {
            Log.d(TAG, "Gemini Nano chat skipped: status=$status")
            return null
        }

        val prompt = buildChatPrompt(
            question = question,
            intent = intent,
            context = context,
        )

        Log.d(TAG, "Gemini Nano chat promptLength=${prompt.length}")

        val output = runPrompt(prompt) ?: return null

        Log.d(TAG, "Gemini Nano chat success, outputLength=${output.length}")

        return ChatAnswer(
            text = output,
            source = "Gemini Nano 온디바이스 응답",
            modelTier = ModelTier.GeminiNano,
        )
    }

    override suspend fun generateGuardianInsight(
        result: GuardianResult,
    ): InsightSummary? {
        val status = getStatus()
        if (status !is GeminiNanoRuntimeStatus.Ready) {
            Log.d(TAG, "Gemini Nano guardian skipped: status=$status")
            return null
        }

        val prompt = buildGuardianPrompt(result)

        Log.d(TAG, "Gemini Nano guardian promptLength=${prompt.length}")

        val output = runPrompt(prompt) ?: return null

        Log.d(TAG, "Gemini Nano guardian success, outputLength=${output.length}")

        return InsightSummary(
            title = "Gemini Nano 인사이트",
            body = output,
            source = AiResponseLabels.GUARDIAN_RECENT_FIVE_MINUTE_DATA,
            modelTier = ModelTier.GeminiNano,
        )
    }

    override fun release() {
        generativeModel?.close()
        generativeModel = null
    }

    private fun buildChatPrompt(
        question: String,
        intent: ChatIntent,
        context: ChatContext,
    ): String {
        return buildString {
            appendLine("너는 SentinAI의 온디바이스 AI 어시스턴트다.")
            appendLine("외부 지식은 사용하지 말고 내부 데이터만 기반으로 답하라.")
            appendLine("질문 의도: $intent")
            appendLine("질문: $question")
            appendLine("최근 7일 총 사용 시간: ${context.analyticsSummary.totalUsageMillis}")
            appendLine("변화율: ${context.analyticsSummary.changeRatePercent}")
            appendLine("심야 사용 비중: ${context.analyticsSummary.nightUsageRatePercent}")
            appendLine("카테고리 개수: ${context.analyticsSummary.categoryUsages.size}")
            appendLine("상위 앱 개수: ${context.analyticsSummary.topApps.size}")
            context.guardianResult?.let { guardian ->
                appendLine("현재 상태: ${guardian.status}")
                appendLine("현재 점수: ${guardian.score}")
                appendLine("메모리 여유율: ${guardian.avgAvailableMemPercent}")
                appendLine("앱 전환 횟수: ${guardian.appSwitchCount}")
            }
        }
    }

    private fun buildGuardianPrompt(
        result: GuardianResult,
    ): String {
        return buildString {
            appendLine("너는 SentinAI의 기기 보호 인사이트 생성기다.")
            appendLine("제공된 내부 데이터만 사용해 2~3문장 인사이트를 작성하라.")
            appendLine("상태: ${result.status}")
            appendLine("점수: ${result.score}")
            appendLine("메모리 여유율: ${result.avgAvailableMemPercent}")
            appendLine("앱 전환 횟수: ${result.appSwitchCount}")
            appendLine("기존 rule insight: ${result.insight}")
        }
    }

    private suspend fun runPrompt(prompt: String): String? {
        return runCatching {
            Log.d(TAG, "Gemini Nano generateContent start")
            val request = generateContentRequest(TextPart(prompt)) {
                temperature = 0.2f
                topK = 20
                candidateCount = 1
                maxOutputTokens = 256
            }

            val response = getGenerativeModel().generateContent(request)
            val text = extractText(response)
            Log.d(TAG, "Gemini Nano generateContent end, hasText=${!text.isNullOrBlank()}")
            text
        }.onFailure { throwable ->
            Log.e(TAG, "Gemini Nano prompt failed", throwable)
        }.getOrNull()
    }

    private fun extractText(response: GenerateContentResponse): String? {
        val candidate = response.candidates.firstOrNull() ?: return null
        val text = candidate.text.trim()

        if (text.isBlank()) {
            return null
        }

        return if (candidate.finishReason == Candidate.FinishReason.MAX_TOKENS) {
            "$text\n(FinishReason: MAX_TOKENS)"
        } else {
            text
        }
    }

    private fun getGenerativeModel(): GenerativeModel {
        generativeModel?.let { return it }

        return synchronized(this) {
            generativeModel ?: Generation.getClient().also {
                generativeModel = it
            }
        }
    }

    private fun isRuntimeReady(): Boolean {
        return runCatching {
            getGenerativeModel()
            true
        }.getOrElse {
            Log.d(TAG, "Gemini Nano runtime not ready", it)
            false
        }
    }

    private fun isSupportedAndroidVersion(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }

    private fun hasAiCorePackage(): Boolean {
        return runCatching {
            context.packageManager.getPackageInfo(AI_CORE_PACKAGE_NAME, 0)
            true
        }.getOrElse {
            false
        }
    }

    private companion object {
        const val TAG = "SentinAI-GeminiNano"
        const val AI_CORE_PACKAGE_NAME = "com.google.android.aicore"
    }
}
