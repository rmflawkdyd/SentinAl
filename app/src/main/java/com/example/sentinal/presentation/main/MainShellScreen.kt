package com.example.sentinal.presentation.main

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLayoutDirection
import com.example.sentinal.presentation.analytics.AnalyticsScreen
import com.example.sentinal.presentation.chat.ChatScreen
import com.example.sentinal.presentation.guardian.GuardianScreen

@Composable
fun MainShellScreen(
    paddingValues: PaddingValues
) {
    var selectedDestination by rememberSaveable {
        mutableStateOf(MainDestination.Dashboard)
    }
    val layoutDirection = LocalLayoutDirection.current
    Scaffold(
        bottomBar = {
            NavigationBar {
                MainDestination.entries.forEach { destination ->
                    NavigationBarItem(
                        selected = selectedDestination ==destination,
                        onClick = { selectedDestination = destination},
                        label = { Text(text = destination.label)},
                        icon = {}
                    )
                }
            }
        }
    ) {innerPadding ->
        val mergedPadding = PaddingValues(
            top = paddingValues.calculateTopPadding() + innerPadding.calculateTopPadding(),
            bottom = paddingValues.calculateBottomPadding() + innerPadding.calculateBottomPadding(),
            start = paddingValues.calculateStartPadding(layoutDirection) +
                    innerPadding.calculateStartPadding(layoutDirection),
            end = paddingValues.calculateEndPadding(layoutDirection) +
                    innerPadding.calculateEndPadding(layoutDirection),
        )

        when (selectedDestination) {
            MainDestination.Dashboard -> GuardianScreen(
                paddingValues = mergedPadding,
            )

            MainDestination.Analytics -> AnalyticsScreen(
                paddingValues = mergedPadding,
            )

            MainDestination.Chat -> ChatScreen(
                paddingValues = mergedPadding,
            )
        }


    }
}