package com.example.sentinal.data.ai

import android.util.Log
import com.example.sentinal.domain.ai.InsightGenerator
import com.example.sentinal.domain.model.GuardianResult
import com.example.sentinal.domain.model.InsightSummary
import com.example.sentinal.domain.model.ModelTier
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

class FallbackInsightGenerator @Inject constructor(
    private val modelAvailabilityChecker: ModelAvailabilityChecker,
    private val geminiNanoInsightGenerator: GeminiNanoInsightGenerator,
    private val gemmaInsightGenerator: GemmaInsightGenerator,
    private val templateInsightGenerator: TemplateInsightGenerator,
) : InsightGenerator {

    override suspend fun generateGuardianInsight(
        result: GuardianResult,
    ): InsightSummary {
        val tier = modelAvailabilityChecker.getAvailableInsightTier()
        Log.d(TAG, "selectedTier=$tier, guardianStatus=${result.status}, score=${result.score}")

        return when (tier) {
            ModelTier.GeminiNano -> {
                Log.d(TAG, "trying Gemini Nano")
                val geminiInsight = withTimeoutOrNull(2_000L) {
                    geminiNanoInsightGenerator.tryGenerate(result)
                }

                if (geminiInsight != null) {
                    Log.d(TAG, "Gemini Nano success")
                    geminiInsight
                } else {
                    Log.d(TAG, "Gemini Nano returned null, trying Gemma/Template")
                    tryGemmaOrTemplate(result)
                }
            }

            ModelTier.Gemma -> {
                Log.d(TAG, "trying Gemma")
                tryGemmaOrTemplate(result)
            }

            ModelTier.Template -> {
                Log.d(TAG, "using Template directly")
                templateInsightGenerator.generateGuardianInsight(result)
            }
        }
    }

    private suspend fun tryGemmaOrTemplate(
        result: GuardianResult,
    ): InsightSummary {
        val gemmaInsight = withTimeoutOrNull(2_000L) {
            gemmaInsightGenerator.tryGenerate(result)
        }

        return if (gemmaInsight != null) {
            Log.d(TAG, "Gemma success")
            gemmaInsight
        } else {
            Log.d(TAG, "Gemma returned null, fallback to Template")
            templateInsightGenerator.generateGuardianInsight(result)
        }
    }

    private companion object {
        const val TAG = "SentinAI-FallbackInsight"
    }
}
