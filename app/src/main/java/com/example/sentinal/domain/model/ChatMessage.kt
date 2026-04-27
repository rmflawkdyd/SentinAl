package com.example.sentinal.domain.model

data class ChatMessage(
    val id: String,
    val role: ChatMessageRole,
    val text: String,
    val source: String? =null,
    val modelTier: ModelTier?=null
)

enum class ChatMessageRole{
    User,
    Assistant
}