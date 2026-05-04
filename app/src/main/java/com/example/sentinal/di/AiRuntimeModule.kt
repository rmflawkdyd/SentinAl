package com.example.sentinal.di

import com.example.sentinal.data.ai.GemmaInferenceEngine
import com.example.sentinal.data.ai.GemmaRuntime
import com.example.sentinal.data.ai.RealGeminiNanoProvider
import com.example.sentinal.data.ai.RealGemmaProvider
import com.example.sentinal.data.ai.RealGemmaRuntime
import com.example.sentinal.data.ai.RealLiteRtGemmaInferenceEngine
import com.example.sentinal.domain.ai.GeminiNanoProvider
import com.example.sentinal.domain.ai.GemmaProvider
import com.example.sentinal.domain.ai.GemmaRuntimeController
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
    ): GeminiNanoProvider {
        return realProvider
    }

    @Provides
    @Singleton
    fun provideGemmaProvider(
        realProvider: RealGemmaProvider,
    ): GemmaProvider {
        return realProvider
    }

    @Provides
    @Singleton
    fun provideGemmaRuntime(
        realRuntime: RealGemmaRuntime,
    ): GemmaRuntime {
        return realRuntime
    }

    @Provides
    @Singleton
    fun provideGemmaRuntimeController(
        realRuntime: RealGemmaRuntime,
    ): GemmaRuntimeController {
        return realRuntime
    }

    @Provides
    @Singleton
    fun provideGemmaInferenceEngine(
        realEngine: RealLiteRtGemmaInferenceEngine,
    ): GemmaInferenceEngine {
        return realEngine
    }
}
