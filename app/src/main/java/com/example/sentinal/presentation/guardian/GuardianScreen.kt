package com.example.sentinal.presentation.guardian

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.annotation.StringRes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sentinal.R
import com.example.sentinal.domain.model.GuardianStatus
import com.example.sentinal.presentation.design.BarChart
import com.example.sentinal.presentation.design.Dot
import com.example.sentinal.presentation.design.HeaderBlock
import com.example.sentinal.presentation.design.MetricGlyph
import com.example.sentinal.presentation.design.ScoreRing
import com.example.sentinal.presentation.design.ScreenSurface
import com.example.sentinal.presentation.design.SentinCard
import com.example.sentinal.presentation.design.StateMessage
import com.example.sentinal.ui.theme.SentinAIActivityBarInactive
import com.example.sentinal.ui.theme.SentinAIDanger
import com.example.sentinal.ui.theme.SentinAIInfo
import com.example.sentinal.ui.theme.SentinAIInk
import com.example.sentinal.ui.theme.SentinAIMuted
import com.example.sentinal.ui.theme.SentinAINavy
import com.example.sentinal.ui.theme.SentinAIText
import com.example.sentinal.ui.theme.SentinAIWarning
import com.example.sentinal.ui.theme.SentinAIWhite
import com.example.sentinal.ui.theme.SentinAIWhite60
import com.example.sentinal.ui.theme.SentinAIWhite90
import com.example.sentinal.ui.theme.SentinAITextStyles

@Composable
fun GuardianScreen(
    paddingValues: PaddingValues,
    viewModel: GuardianViewModel = hiltViewModel(),
) {
    DisposableEffect(viewModel) {
        onDispose {
            viewModel.releaseGemmaSession()
            viewModel.releaseGeminiSession()
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        GuardianUiState.Loading -> GuardianLoading(Modifier.padding(paddingValues))
        GuardianUiState.Empty -> GuardianEmpty(
            modifier = Modifier.padding(paddingValues),
            onReload = viewModel::loadGuardian,
        )
        is GuardianUiState.Error -> GuardianError(
            modifier = Modifier.padding(paddingValues),
            message = state.message,
            onReload = viewModel::loadGuardian,
        )
        is GuardianUiState.Success -> GuardianSuccess(
            modifier = Modifier.padding(paddingValues),
            state = state,
        )
    }
}

@Composable
private fun GuardianLoading(modifier: Modifier = Modifier) {
    ScreenSurface(modifier) {
        StateMessage(
            title = stringResource(R.string.guardian_title),
            message = stringResource(R.string.guardian_loading_message),
            isLoading = true,
        )
    }
}

@Composable
private fun GuardianEmpty(
    modifier: Modifier = Modifier,
    onReload: () -> Unit,
) {
    ScreenSurface(modifier) {
        StateMessage(
            title = stringResource(R.string.guardian_empty_title),
            message = stringResource(R.string.guardian_empty_message),
            buttonText = stringResource(R.string.common_retry),
            onButtonClick = onReload,
        )
    }
}

@Composable
private fun GuardianError(
    modifier: Modifier = Modifier,
    message: String,
    onReload: () -> Unit,
) {
    ScreenSurface(modifier) {
        StateMessage(
            title = "오류가 발생했습니다.",
            message = message,
            buttonText = stringResource(R.string.common_retry),
            onButtonClick = onReload,
        )
    }
}

@Composable
private fun GuardianSuccess(
    modifier: Modifier = Modifier,
    state: GuardianUiState.Success,
) {
    val latestPoint = state.chartPoints.lastOrNull()
    val chartValues = if (state.chartPoints.isEmpty()) {
        listOf(40f, 35f, 50f, 65f, 80f, 55f, 90f, 30f, 45f, 35f, 60f, 40f)
    } else {
        state.chartPoints.takeLast(12).map { it.appSwitchCount.toFloat() }
    }

    ScreenSurface(modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            HeaderBlock(
                title = stringResource(R.string.guardian_title),
                subtitle = stringResource(R.string.guardian_subtitle),
            )
            ResourceScoreCard(state)
            ActivityCard(chartValues)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard(
                    title = stringResource(R.string.guardian_memory),
                    value = latestPoint?.let {
                        stringResource(
                            R.string.guardian_memory_available_format,
                            it.availableMemPercent.format(0),
                        )
                    } ?: stringResource(R.string.common_no_data),
                    iconRes = R.drawable.ic_memory,
                    modifier = Modifier.weight(1f),
                )
                MetricCard(
                    title = stringResource(R.string.guardian_app_switches),
                    value = latestPoint?.let {
                        stringResource(R.string.guardian_app_switches_recent_format, it.appSwitchCount)
                    } ?: stringResource(R.string.common_no_data),
                    iconRes = R.drawable.ic_switch,
                    modifier = Modifier.weight(1f),
                )
            }
            InsightCard(state)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ResourceScoreCard(state: GuardianUiState.Success) {
    SentinCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(horizontal = 21.dp, vertical = 36.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.guardian_resource_score),
                        color = SentinAIText,
                        style = SentinAITextStyles.SectionTitle,
                    )
                    Text(
                        text = stringResource(R.string.guardian_system_integrity),
                        color = SentinAIMuted,
                        style = SentinAITextStyles.Label.copy(fontWeight = FontWeight.Bold),
                    )
                }
                StatusPill(status = state.status)
            }
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                ScoreRing(score = state.score)
            }
        }
    }
}

@Composable
private fun StatusPill(status: GuardianStatus) {
    val color = when (status) {
        GuardianStatus.NORMAL -> SentinAIInfo
        GuardianStatus.CAUTION -> SentinAIWarning
        GuardianStatus.DANGER -> SentinAIDanger
    }
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.14f), RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Dot(color = color, modifier = Modifier.size(8.dp))
            Text(
                text = stringResource(status.labelRes()),
                color = color,
                style = SentinAITextStyles.Label.copy(fontWeight = FontWeight.Bold),
            )
        }
    }
}

@Composable
private fun ActivityCard(values: List<Float>) {
    SentinCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(21.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    text = stringResource(R.string.guardian_recent_app_switches),
                    color = SentinAIText,
                    style = SentinAITextStyles.Body,
                )
                Text(
                    text = stringResource(R.string.guardian_switch_count),
                    color = SentinAIMuted,
                    style = SentinAITextStyles.Label.copy(fontWeight = FontWeight.Bold),
                )
            }
            BarChart(
                values = values,
                inactiveColor = SentinAIActivityBarInactive,
                cornerRadius = 2f,
                hideZeroValues = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                AxisLabel(stringResource(R.string.guardian_axis_older))
                AxisLabel(stringResource(R.string.guardian_axis_recent))
                AxisLabel(stringResource(R.string.guardian_axis_now))
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    iconRes: Int,
    modifier: Modifier = Modifier,
) {
    SentinCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(17.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = title,
                color = SentinAIMuted,
                style = SentinAITextStyles.Label.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = value,
                color = SentinAIInk,
                style = SentinAITextStyles.InsightTitle,
            )
        }
    }
}

@Composable
private fun InsightCard(state: GuardianUiState.Success) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SentinAINavy, RoundedCornerShape(12.dp))
            .padding(horizontal = 20.dp, vertical = 20.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MetricGlyph(kind = "insight")
                Text(
                    text = state.insightTitle,
                    color = SentinAIWhite,
                    style = SentinAITextStyles.Body,
                )
            }
            Text(
                text = state.insightBody,
                color = SentinAIWhite90,
                style = SentinAITextStyles.BodyRelaxed,
            )
        }
    }
}

@Composable
private fun AxisLabel(text: String) {
    Text(
        text = text,
        color = SentinAIMuted,
        style = SentinAITextStyles.Label.copy(fontWeight = FontWeight.Bold),
    )
}

private fun Float.format(digits: Int): String = "%.${digits}f".format(this)

@StringRes
private fun GuardianStatus.labelRes(): Int {
    return when (this) {
        GuardianStatus.NORMAL -> R.string.guardian_status_normal
        GuardianStatus.CAUTION -> R.string.guardian_status_caution
        GuardianStatus.DANGER -> R.string.guardian_status_danger
    }
}
