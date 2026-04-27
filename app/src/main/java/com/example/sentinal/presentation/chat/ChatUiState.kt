package com.example.sentinal.presentation.chat

import com.example.sentinal.domain.model.ChatMessage

sealed interface ChatUiState {
    data object Loading: ChatUiState
    data object Empty: ChatUiState

    data class Error(
        val message: String,
    ): ChatUiState

    data class Success(
        val message:List<ChatMessage>,
        val input: String="",
        val isSending: Boolean = false
    ): ChatUiState
}