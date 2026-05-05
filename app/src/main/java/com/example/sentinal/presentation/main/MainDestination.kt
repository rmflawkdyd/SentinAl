package com.example.sentinal.presentation.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.sentinal.R

enum class MainDestination(
    @param:StringRes val labelRes: Int,
    @param:DrawableRes val iconRes: Int,
) {
    Dashboard(
        labelRes = R.string.nav_dashboard,
        iconRes = R.drawable.ic_dashboard,
    ),
    Analytics(
        labelRes = R.string.nav_analytics,
        iconRes = R.drawable.ic_analytics,
    ),
    Chat(
        labelRes = R.string.nav_chat,
        iconRes = R.drawable.ic_chat,
    ),
}
