package com.example.sentinal.di

import com.example.sentinal.BuildConfig
import com.example.sentinal.data.ai.GemmaInferenceEngine
import com.example.sentinal.data.ai.GemmaRuntime
import com.example.sentinal.data.ai.NoOpGeminiNanoProvider
import com.example.sentinal.data.ai.NoOpGemmaProvider
import com.example.sentinal.data.ai.NoOpGemmaInferenceEngine
import com.example.sentinal.data.ai.NoOpGemmaRuntime
import com.example.sentinal.data.ai.RealGeminiNanoProvider
import com.example.sentinal.data.ai.RealGemmaProvider
import com.example.sentinal.data.ai.RealGemmaRuntime
import com.example.sentinal.data.ai.RealLiteRtGemmaInferenceEngine
import com.example.sentinal.domain.ai.GeminiNanoProvider
import com.example.sentinal.domain.ai.GemmaProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiRuntimeModule {

    @Provides
    @Singleton
    fun provideGeminiNanoProvider(
        realProvider: RealGeminiNanoProvider,
        noOpProvider: NoOpGeminiNanoProvider,
    ): GeminiNanoProvider {
        return if (BuildConfig.ENABLE_GEMINI_NANO) {
            realProvider
        } else {
            noOpProvider
        }
    }

    @Provides
    @Singleton
    fun provideGemmaProvider(
        realProvider: RealGemmaProvider,
        noOpProvider: NoOpGemmaProvider,
    ): GemmaProvider {
        return if (BuildConfig.ENABLE_GEMMA) {
            realProvider
        } else {
            noOpProvider
        }
    }

    @Provides
    @Singleton
    fun provideGemmaRuntime(
        realRuntime: RealGemmaRuntime,
        noOpRuntime: NoOpGemmaRuntime,
    ): GemmaRuntime {
        return if (BuildConfig.ENABLE_GEMMA) {
            realRuntime
        } else {
            noOpRuntime
        }
    }

    @Provides
    @Singleton
    fun provideGemmaInferenceEngine(
        realEngine: RealLiteRtGemmaInferenceEngine,
        noOpEngine: NoOpGemmaInferenceEngine,
    ): GemmaInferenceEngine {
        return if (BuildConfig.ENABLE_GEMMA) {
            realEngine
        } else {
            noOpEngine
        }
    }
}
