package com.example.sentinal.presentation.main

import androidx.annotation.DrawableRes
import com.example.sentinal.R

enum class MainDestination(
    val label: String,
    @DrawableRes val iconRes: Int,
) {
    Dashboard(
        label = "Dashboard",
        iconRes = R.drawable.ic_dashboard,
    ),
    Analytics(
        label = "Analytics",
        iconRes = R.drawable.ic_analytics,
    ),
    Chat(
        label = "AI Chat",
        iconRes = R.drawable.ic_chat,
    ),
}
