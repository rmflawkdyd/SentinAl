package com.example.sentinal.domain.model

data class InsightSummary(
    val title:String,
    val body: String,
    val source: String,
    val modelTier: ModelTier,
)
