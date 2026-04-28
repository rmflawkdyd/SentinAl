package com.example.sentinal.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sentinal.presentation.analytics.AnalyticsScreen
import com.example.sentinal.presentation.chat.ChatScreen
import com.example.sentinal.presentation.design.SentinBg
import com.example.sentinal.presentation.design.SentinNavy
import com.example.sentinal.presentation.design.SentinSubtle
import com.example.sentinal.presentation.guardian.GuardianScreen

@Composable
fun MainShellScreen() {
    var selectedDestination by rememberSaveable {
        mutableStateOf(MainDestination.Dashboard)
    }

    Scaffold(
        containerColor = SentinBg,
        bottomBar = {
            SentinBottomBar(
                modifier = Modifier.navigationBarsPadding(),
                selectedDestination = selectedDestination,
                onDestinationSelected = { selectedDestination = it },
            )
        },
    ) { innerPadding ->
        when (selectedDestination) {
            MainDestination.Dashboard -> GuardianScreen(paddingValues = innerPadding)
            MainDestination.Analytics -> AnalyticsScreen(paddingValues = innerPadding)
            MainDestination.Chat -> ChatScreen(paddingValues = innerPadding)
        }
    }
}


@Composable
private fun SentinBottomBar(
    modifier: Modifier = Modifier,
    selectedDestination: MainDestination,
    onDestinationSelected: (MainDestination) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(81.dp)
            .background(Color.White.copy(alpha = 0.88f))
            .border(width = 1.dp, color = Color(0x66E2E8F0))
            .padding(horizontal = 36.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        MainDestination.entries.forEach { destination ->
            val selected = destination == selectedDestination
            Column(
                modifier = Modifier
                    .size(width = 72.dp, height = 64.dp)
                    .clickable { onDestinationSelected(destination) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    painter = painterResource(id = destination.iconRes),
                    contentDescription = destination.label,
                    tint = if (selected) SentinNavy else SentinSubtle,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = destination.label,
                    modifier = Modifier.padding(top = 4.dp),
                    color = if (selected) SentinNavy else SentinSubtle,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
