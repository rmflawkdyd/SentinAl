package com.example.sentinal.di

import com.example.sentinal.data.ai.FallbackChatAnswerGenerator
import com.example.sentinal.data.ai.FallbackInsightGenerator
import com.example.sentinal.domain.ai.ChatAnswerGenerator
import com.example.sentinal.domain.ai.InsightGenerator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiModule {

    @Binds
    @Singleton
    abstract fun bindInsightGenerator(
        impl: FallbackInsightGenerator
    ): InsightGenerator

    @Binds
    @Singleton
    abstract fun bindChatAnswerGenerator(
        impl: FallbackChatAnswerGenerator
    ): ChatAnswerGenerator

}
