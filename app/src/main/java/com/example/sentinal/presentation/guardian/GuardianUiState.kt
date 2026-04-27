package com.example.sentinal.presentation.guardian

import com.example.sentinal.domain.model.GuardianChartPoint
import com.example.sentinal.domain.model.GuardianStatus
import com.example.sentinal.domain.model.ModelTier

sealed interface GuardianUiState {
    data object Loading: GuardianUiState
    data object Empty: GuardianUiState
    data class Error(val message: String): GuardianUiState
    data class Success(
        val score: Int,
        val status: GuardianStatus,
        val insightTitle: String,
        val insightBody: String,
        val source: String,
        val modelTier: ModelTier,
        val chartPoints:List<GuardianChartPoint>
    ): GuardianUiState
}
