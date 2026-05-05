package com.example.sentinal.data.ai

import android.content.Context
import android.util.Log
import com.example.sentinal.BuildConfig
import com.example.sentinal.domain.ai.GemmaProvider
import com.example.sentinal.domain.model.ChatAnswer
import com.example.sentinal.domain.model.ChatContext
import com.example.sentinal.domain.model.ChatIntent
import com.example.sentinal.domain.model.GuardianResult
import com.example.sentinal.domain.model.InsightSummary
import com.example.sentinal.domain.model.ModelTier
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class RealGemmaProvider @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val gemmaRuntime: GemmaRuntime,
): GemmaProvider {
    override fun isAvailable(): Boolean {
        if (!BuildConfig.ENABLE_GEMMA) return false
        if (!hasModelAsset(AiModelPaths.GEMMA_MODEL_ASSET_PATH)) return false
        return gemmaRuntime.isReady()
    }

    override suspend fun generateChatAnswer(
        question: String,
        intent: ChatIntent,
        context: ChatContext,
    ): ChatAnswer? {
        val prompt = buildChatPrompt(
            question = question,
            intent = intent,
            context = context,
        )

        Log.d(TAG, "Gemma chat promptLength=${prompt.length}")

        return when (val result = gemmaRuntime.generate(prompt)) {
            is GemmaInferenceResult.Success -> {
                Log.d(TAG, "Gemma chat success, outputLength=${result.text.length}")
                ChatAnswer(
                    text = result.text,
                    source = "Gemma 로컬 모델 응답",
                    modelTier = ModelTier.Gemma,
                )
            }

            GemmaInferenceResult.ModelNotReady -> {
                Log.d(TAG, "Gemma chat result=ModelNotReady")
                null
            }

            GemmaInferenceResult.EmptyOutput -> {
                Log.d(TAG, "Gemma chat result=EmptyOutput")
                null
            }

            is GemmaInferenceResult.Failure -> {
                Log.e(TAG, "Gemma chat failure", result.throwable)
                null
            }
        }
    }

    override suspend fun generateGuardianInsight(result: GuardianResult): InsightSummary? {
        val prompt = buildGuardianPrompt(result)

        Log.d(TAG, "Gemma guardian promptLength=${prompt.length}")

        return when (val inference = gemmaRuntime.generate(prompt)) {
            is GemmaInferenceResult.Success -> {
                Log.d(TAG, "Gemma guardian success, outputLength=${inference.text.length}")
                InsightSummary(
                    title = "Gemma 인사이트",
                    body = inference.text,
                    source = AiResponseLabels.GUARDIAN_RECENT_FIVE_MINUTE_DATA,
                    modelTier = ModelTier.Gemma,
                )
            }

            GemmaInferenceResult.ModelNotReady -> {
                Log.d(TAG, "Gemma guardian result=ModelNotReady")
                null
            }

            GemmaInferenceResult.EmptyOutput -> {
                Log.d(TAG, "Gemma guardian result=EmptyOutput")
                null
            }

            is GemmaInferenceResult.Failure -> {
                Log.e(TAG, "Gemma guardian failure", inference.throwable)
                null
            }
        }
    }

    private fun buildChatPrompt(
        question: String,
        intent: ChatIntent,
        context: ChatContext,
    ): String {
        return buildString {
            appendLine("너는 SentinAI의 온디바이스 AI 어시스턴트다.")
            appendLine("외부 지식은 사용하지 말고 제공된 내부 데이터만 기반으로 대답하라.")
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

    private fun hasModelAsset(assetPath: String): Boolean{
        return runCatching {
            context.assets.open(assetPath).use{input->
                input.available() >=0
            }
        }.getOrElse {
            false
        }
    }

    private companion object {
        const val TAG = "SentinAI-GemmaProvider"
    }
}
