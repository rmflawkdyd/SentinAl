package com.example.sentinal.presentation.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sentinal.domain.model.AnalyticsSummary
import com.example.sentinal.domain.model.AppUsagePoint
import com.example.sentinal.presentation.design.BarChart
import com.example.sentinal.presentation.design.HeaderBlock
import com.example.sentinal.presentation.design.LegendDot
import com.example.sentinal.presentation.design.ScreenSurface
import com.example.sentinal.presentation.design.SegmentedUsageBar
import com.example.sentinal.presentation.design.SentinCard
import com.example.sentinal.presentation.design.StateMessage
import com.example.sentinal.ui.theme.SentinAIAccent
import com.example.sentinal.ui.theme.SentinAIAppIconBg
import com.example.sentinal.ui.theme.SentinAICaptionText
import com.example.sentinal.ui.theme.SentinAICategoryRest
import com.example.sentinal.ui.theme.SentinAIDanger
import com.example.sentinal.ui.theme.SentinAIGood
import com.example.sentinal.ui.theme.SentinAIInfo
import com.example.sentinal.ui.theme.SentinAIInk
import com.example.sentinal.ui.theme.SentinAILine
import com.example.sentinal.ui.theme.SentinAIMint
import com.example.sentinal.ui.theme.SentinAIMuted
import com.example.sentinal.ui.theme.SentinAINavy
import com.example.sentinal.ui.theme.SentinAISecondaryText
import com.example.sentinal.ui.theme.SentinAISubtle
import com.example.sentinal.ui.theme.SentinAIText
import com.example.sentinal.ui.theme.SentinAITextStyles
import java.time.LocalDate

@Composable
fun AnalyticsScreen(
    paddingValues: PaddingValues,
    viewModel: AnalyticsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        AnalyticsUiState.Loading -> AnalyticsLoading(Modifier.padding(paddingValues))
        AnalyticsUiState.Empty -> AnalyticsEmpty(
            modifier = Modifier.padding(paddingValues),
            onReload = viewModel::loadAnalytics,
        )
        is AnalyticsUiState.Error -> AnalyticsError(
            modifier = Modifier.padding(paddingValues),
            message = state.message,
            onReload = viewModel::loadAnalytics,
        )
        is AnalyticsUiState.Success -> AnalyticsSuccess(
            modifier = Modifier.padding(paddingValues),
            summary = state.summary,
        )
    }
}

@Composable
private fun AnalyticsLoading(modifier: Modifier = Modifier) {
    ScreenSurface(modifier) {
        StateMessage(
            title = "Analytics",
            message = "주간 분석 데이터를 불러오는 중입니다.",
            isLoading = true,
        )
    }
}

@Composable
private fun AnalyticsEmpty(
    modifier: Modifier = Modifier,
    onReload: () -> Unit,
) {
    ScreenSurface(modifier) {
        StateMessage(
            title = "아직 분석할 사용 데이터가 없습니다.",
            message = "일별 집계가 만들어지면 주간 리포트가 표시됩니다.",
            buttonText = "다시 시도",
            onButtonClick = onReload,
        )
    }
}

@Composable
private fun AnalyticsError(
    modifier: Modifier = Modifier,
    message: String,
    onReload: () -> Unit,
) {
    ScreenSurface(modifier) {
        StateMessage(
            title = "분석 데이터를 불러오지 못했습니다.",
            message = message,
            buttonText = "다시 시도",
            onButtonClick = onReload,
        )
    }
}

@Composable
private fun AnalyticsSuccess(
    modifier: Modifier = Modifier,
    summary: AnalyticsSummary,
) {
    ScreenSurface(modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            HeaderBlock("Analytics", "Weekly usage report")
            WeeklyUsageCard(summary)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ChangeCard(summary.changeRatePercent, Modifier.weight(1f))
                NightUsageCard(summary.nightUsageRatePercent, Modifier.weight(1f))
            }
            TopAppsSection(summary.topApps.take(5))
            CategoryUsageCard(summary)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun WeeklyUsageCard(summary: AnalyticsSummary) {
    val values = summary.dailyUsages.map { it.usageMillis.toFloat() }
    val chartValues = values.ifEmpty { List(7) { 0f } }
    val highlightIndex = chartValues
        .indices
        .filter { chartValues[it] > 0f }
        .maxByOrNull { chartValues[it] }
    val weekdayLabels = summary.dailyUsages.map { it.dateEpoch.toWeekdayLabel() }

    SentinCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "TOTAL USAGE",
                color = SentinAIInfo,
                style = SentinAITextStyles.Overline.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = summary.totalUsageMillis.toCompactHoursText(),
                color = SentinAIInk,
                style = SentinAITextStyles.MetricLarge,
            )
            BarChart(
                values = chartValues,
                highlightIndex = highlightIndex,
                activeColor = SentinAIInk,
                inactiveColor = SentinAILine,
                hideZeroValues = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                val labels = if (weekdayLabels.size == chartValues.size) {
                    weekdayLabels
                } else {
                    listOf("M", "T", "W", "T", "F", "S", "S")
                }
                labels.forEachIndexed { index, day ->
                    Text(
                        text = day,
                        color = if (index == highlightIndex) SentinAIInk else SentinAISubtle,
                        style = SentinAITextStyles.Tiny.copy(
                            fontWeight = if (index == highlightIndex) FontWeight.Bold else FontWeight.Normal,
                        ),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun ChangeCard(changeRatePercent: Float, modifier: Modifier = Modifier) {
    val isDown = changeRatePercent <= 0f
    SentinCard(modifier = modifier.height(136.dp), shape = RoundedCornerShape(16.dp)) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Change",
                color = SentinAISecondaryText,
                style = SentinAITextStyles.Label.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = "${if (isDown) "↓" else "↑"} ${changeRatePercent.format(0)}%",
                color = if (isDown) SentinAIGood else SentinAIDanger,
                style = SentinAITextStyles.SectionTitle,
            )
            Text(text = "vs last week", color = SentinAICaptionText, style = SentinAITextStyles.Caption)
        }
    }
}

@Composable
private fun NightUsageCard(nightUsageRatePercent: Float, modifier: Modifier = Modifier) {
    SentinCard(modifier = modifier.height(136.dp), shape = RoundedCornerShape(16.dp)) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Night usage",
                color = SentinAISecondaryText,
                style = SentinAITextStyles.Label.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = "${nightUsageRatePercent.format(0)}%",
                color = SentinAIInk,
                style = SentinAITextStyles.SectionTitle,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(SentinAILine, RoundedCornerShape(999.dp)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth((nightUsageRatePercent / 100f).coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(SentinAINavy, RoundedCornerShape(999.dp)),
                )
            }
            Text(text = "10pm - 6am", color = SentinAICaptionText, style = SentinAITextStyles.Caption)
        }
    }
}

@Composable
private fun TopAppsSection(apps: List<AppUsagePoint>) {
    Column(
        modifier = Modifier.padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Top 5 Apps",
            color = SentinAIInk,
            style = SentinAITextStyles.SectionTitle,
        )
        if (apps.isEmpty()) {
            SentinCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "앱별 사용량 데이터가 없습니다.",
                    modifier = Modifier.padding(20.dp),
                    color = SentinAIMuted,
                    style = SentinAITextStyles.BodySmall,
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                apps.forEachIndexed { index, app ->
                    AppUsageRow(index = index, app = app)
                }
            }
        }
    }
}

@Composable
private fun AppUsageRow(index: Int, app: AppUsagePoint) {
    SentinCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(SentinAIAppIconBg, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    AppIconBadge(packageName = app.packageName)
                }
                Column {
                    Row() {
                        Text(
                            text = app.displayName(),
                            color = SentinAIInk,
                            style = SentinAITextStyles.Body.copy(fontWeight = FontWeight.SemiBold),
                        )
                        if (app.appName == null) {
                            Text(
                                text = app.packageName,
                                color = SentinAISubtle,
                                style = SentinAITextStyles.SmallLabel,
                            )
                        }
                        Spacer(modifier = Modifier.size(12.dp))
                        Text(
                            text = app.usageMillis.toCompactHoursText(),
                            color = SentinAIInk,
                            style = SentinAITextStyles.Body.copy(fontWeight = FontWeight.Bold),
                        )
                    }

                    Text(
                        text = "${app.launchCount} launches",
                        color = SentinAISecondaryText,
                        style = SentinAITextStyles.BodySmall,
                    )
                }
            }

        }
    }
}

@Composable
private fun AppIconBadge(
    packageName: String,
) {
    val context = LocalContext.current
    val appIconBitmap = remember(packageName, context) {
        runCatching {
            val drawable = context.packageManager.getApplicationIcon(packageName)
            drawable.toBitmap().asImageBitmap()
        }.getOrNull()
    }

    if (appIconBitmap != null) {
        Image(
            bitmap = appIconBitmap,
            contentDescription = packageName,
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
private fun CategoryUsageCard(summary: AnalyticsSummary) {
    val totalUsageMillis = summary.categoryUsages.sumOf { it.usageMillis }.takeIf { it > 0L }
    val categories = summary.categoryUsages.take(4)
    val colors = listOf(SentinAINavy, SentinAIAccent, SentinAIMint, SentinAICategoryRest)
    val segments = if (totalUsageMillis == null || categories.isEmpty()) {
        listOf(45f to SentinAINavy, 30f to SentinAIAccent, 15f to SentinAIMint, 10f to SentinAICategoryRest)
    } else {
        categories.mapIndexed { index, item -> item.usageMillis.toFloat() to colors[index % colors.size] }
    }

    Column(
        modifier = Modifier.padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Category Usage",
            color = SentinAIInk,
            style = SentinAITextStyles.SectionTitle,
        )
        SentinCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                SegmentedUsageBar(segments = segments, modifier = Modifier.fillMaxWidth())
                if (totalUsageMillis == null || categories.isEmpty()) {
                    Text(
                        text = "카테고리 데이터가 없습니다.",
                        color = SentinAIMuted,
                        style = SentinAITextStyles.BodySmall,
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        categories.chunked(2).forEach { rowItems ->
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                rowItems.forEachIndexed { localIndex, item ->
                                    val index = categories.indexOf(item).takeIf { it >= 0 } ?: localIndex
                                    CategoryLegendItem(
                                        name = item.category,
                                        value = item.usageMillis.toCompactHoursText(),
                                        color = colors[index % colors.size],
                                        modifier = Modifier.weight(1f),
                                    )
                                }
                                if (rowItems.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryLegendItem(
    name: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LegendDot(color)
        Text(text = name, color = SentinAIText, style = SentinAITextStyles.BodySmall)
        Text(
            text = value,
            color = SentinAIInk,
            style = SentinAITextStyles.BodySmall.copy(fontWeight = FontWeight.Bold),
        )
    }
}

private fun Float.format(digits: Int): String = "%.${digits}f".format(this)

private fun AppUsagePoint.displayName(): String {
    return appName ?: packageName.toFallbackAppName()
}

private fun String.toFallbackAppName(): String {
    val rawName = substringAfterLast('.')
        .ifBlank { this }
        .replace('_', ' ')
        .replace('-', ' ')

    return rawName.replaceFirstChar { firstChar ->
        if (firstChar.isLowerCase()) {
            firstChar.titlecase()
        } else {
            firstChar.toString()
        }
    }
}

private fun Long.toWeekdayLabel(): String {
    return when (LocalDate.ofEpochDay(this).dayOfWeek.value) {
        1 -> "M"
        2 -> "T"
        3 -> "W"
        4 -> "T"
        5 -> "F"
        6 -> "S"
        else -> "S"
    }
}

private fun Long.toCompactHoursText(): String {
    val totalMinutes = this / (1000L * 60L)
    val hours = totalMinutes / 60L
    val minutes = totalMinutes % 60L
    return if (hours > 0L) {
        "${hours}h ${minutes}m"
    } else {
        "${minutes}m"
    }
}
