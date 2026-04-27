package com.example.sentinal.data.collector.model

data class UsageEventRaw(
    val timestamp:Long,
    val packageName:String,
    val className: String?,
    val eventType: Int,
)
