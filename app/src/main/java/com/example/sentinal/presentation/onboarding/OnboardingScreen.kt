package com.example.sentinal.presentation.onboarding

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sentinal.R
import com.example.sentinal.presentation.design.ScreenSurface
import com.example.sentinal.presentation.design.SentinCard
import com.example.sentinal.presentation.design.SentinInk
import com.example.sentinal.presentation.design.SentinLine
import com.example.sentinal.presentation.design.SentinNavy
import com.example.sentinal.presentation.design.SentinText
import com.example.sentinal.presentation.design.StateMessage

@Composable
fun OnboardingScreen(
    paddingValues: PaddingValues,
    onPermissionGranted: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshPermissionState()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is OnboardingUiState.Granted) {
            onPermissionGranted()
        }
    }

    when (val state = uiState) {
        OnboardingUiState.Loading -> OnboardingLoading(Modifier.padding(paddingValues))
        OnboardingUiState.NeedsPermission -> OnboardingPermission(
            modifier = Modifier.padding(paddingValues),
            onOpenSettings = viewModel::openUsageAccessSettings,
        )
        is OnboardingUiState.Error -> OnboardingError(
            modifier = Modifier.padding(paddingValues),
            message = state.message,
            onRetry = viewModel::refreshPermissionState,
        )
        OnboardingUiState.Granted -> Unit
    }
}

@Composable
private fun OnboardingLoading(modifier: Modifier = Modifier) {
    ScreenSurface(modifier) {
        StateMessage(
            title = "SentinAI",
            message = "권한 상태를 확인하는 중입니다.",
            isLoading = true,
        )
    }
}

@Composable
private fun OnboardingError(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: () -> Unit,
) {
    ScreenSurface(modifier) {
        StateMessage(
            title = "권한 상태를 확인할 수 없습니다.",
            message = message,
            buttonText = "다시 시도",
            onButtonClick = onRetry,
        )
    }
}

@Composable
private fun OnboardingPermission(
    modifier: Modifier = Modifier,
    onOpenSettings: () -> Unit,
) {
    val activity = LocalActivity.current

    ScreenSurface(modifier) {
        Box(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
            ) {
                OnboardingHeader()
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.Top,
                ) {
                    IntroSection()
                    Spacer(modifier = Modifier.height(6.dp))
                    PermissionBulletCard()
                    OnboardingFooter(
                        onOpenSettings = onOpenSettings,
                        onDecline = { activity?.finishAffinity() },
                    )
                }

            }
        }
    }
}

@Composable
private fun OnboardingHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom,
    ) {
        Row(
            modifier = Modifier.padding(bottom = 0.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "SentinAI",
                color = SentinNavy,
                fontSize = 22.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun IntroSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Turn usage access into private insights",
            color = SentinInk,
            fontSize = 24.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "Usage Access lets SentinAI summarize activity and device signals on this device only.",
            modifier = Modifier.padding(top = 10.dp),
            color = Color(0xFF44474D),
            fontSize = 15.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun PermissionBulletCard() {
    SentinCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PermissionBullet(
                icon = R.drawable.ic_chart,
                text = "Build Guardian scores from local usage metrics",
            )
            PermissionBullet(
                icon = R.drawable.ic_doc,
                text = "Keep collected signals on your phone",
            )
            PermissionBullet(
                icon = R.drawable.ic_summary,
                text = "Generate summaries with on-device AI fallback",
            )
            PermissionBullet(
                icon = R.drawable.ic_star,
                text = "Never use external diagnosis or search",
            )
        }
    }
}

@Composable
private fun PermissionBullet(
    icon: Int,
    text: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {

        Icon(
            painter = painterResource(id = icon),
            contentDescription = "",
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = text,
            color = SentinText,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun OnboardingFooter(
    onOpenSettings: () -> Unit,
    onDecline: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = onOpenSettings,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SentinNavy),
            shape = RoundedCornerShape(16.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_accept),
                    tint = Color.White,
                    contentDescription = "",
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = "Accept",
                    color = Color.White,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        OutlinedButton(
            onClick = onDecline,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = SentinNavy),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, SentinLine),
        ) {
            Text(
                text = "Decline",
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

    }
}




