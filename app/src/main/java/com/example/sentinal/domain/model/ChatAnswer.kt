package com.example.sentinal.domain.model

data class ChatAnswer(
    val text: String,
    val source: String,
    val modelTier:ModelTier,
)
