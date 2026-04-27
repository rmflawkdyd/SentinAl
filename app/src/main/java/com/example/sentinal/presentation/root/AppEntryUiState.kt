package com.example.sentinal.presentation.root

sealed interface AppEntryUiState {
    data object Loading: AppEntryUiState
    data object NeedsOnboarding : AppEntryUiState
    data object Ready : AppEntryUiState
    data class Error(val message: String) : AppEntryUiState
}