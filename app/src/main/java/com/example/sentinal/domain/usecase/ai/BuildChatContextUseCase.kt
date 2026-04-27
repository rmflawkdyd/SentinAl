package com.example.sentinal.domain.usecase.ai

import com.example.sentinal.domain.model.ChatContext
import com.example.sentinal.domain.usecase.analytics.GetWeeklyAnalyticsUseCase
import com.example.sentinal.domain.usecase.guardian.GetGuardianResultUseCase
import com.example.sentinal.domain.usecase.refresh.RefreshAppDataUseCase
import javax.inject.Inject

class BuildChatContextUseCase @Inject constructor(
    private val refreshAppDataUseCase: RefreshAppDataUseCase,
    private val getGuardianResultUseCase: GetGuardianResultUseCase,
    private val getWeeklyAnalyticsUseCase: GetWeeklyAnalyticsUseCase,
){
    suspend operator fun invoke(): ChatContext {
        refreshAppDataUseCase()
        return ChatContext(
            guardianResult = getGuardianResultUseCase(),
            analyticsSummary = getWeeklyAnalyticsUseCase()
        )
    }
}