package com.example.sentinal.presentation.onboarding

sealed interface OnboardingUiState {
    data object Loading: OnboardingUiState
    data object NeedsPermission: OnboardingUiState
    data object Granted: OnboardingUiState
    data class Error(val message: String): OnboardingUiState
}