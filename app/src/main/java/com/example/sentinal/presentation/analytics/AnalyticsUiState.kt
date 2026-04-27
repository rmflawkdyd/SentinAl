package com.example.sentinal.presentation.analytics

import com.example.sentinal.domain.model.AnalyticsSummary

sealed interface AnalyticsUiState {
    data object Loading: AnalyticsUiState
    data object Empty: AnalyticsUiState
    data class Error(val message: String): AnalyticsUiState
    data class Success(
        val summary: AnalyticsSummary,
    ): AnalyticsUiState
}