package com.example.sentinal.presentation.analytics


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sentinal.domain.model.AnalyticsSummary

@Composable
fun AnalyticsScreen(
    paddingValues: PaddingValues,
    viewModel: AnalyticsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        AnalyticsUiState.Loading -> AnalyticsLoading(
            modifier = Modifier.padding(paddingValues)
        )

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
private fun AnalyticsLoading(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator()
        Text(
            text = "주간 분석 데이터를 불러오는 중입니다.",
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

@Composable
private fun AnalyticsEmpty(
    modifier: Modifier = Modifier,
    onReload: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "아직 분석할 사용 데이터가 없습니다.",
            style = MaterialTheme.typography.titleMedium,
        )
        Button(
            onClick = onReload,
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Text(text = "다시 시도")
        }
    }
}

@Composable
private fun AnalyticsError(
    modifier: Modifier = Modifier,
    message: String,
    onReload: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "분석 데이터를 불러오지 못했습니다.",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = message,
            modifier = Modifier.padding(top = 8.dp),
        )
        Button(
            onClick = onReload,
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Text(text = "다시 시도")
        }
    }
}

@Composable
private fun AnalyticsSuccess(
    modifier: Modifier = Modifier,
    summary: AnalyticsSummary,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Analytics",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        TotalUsageCard(summary.totalUsageMillis)
        ChangeRateCard(summary.changeRatePercent)
        NightUsageRateCard(summary.nightUsageRatePercent)
        WeeklyBarChart(summary)
        TopAppsCard(summary)
        CategoryPieChart(summary)
    }
}

@Composable
private fun ChangeRateCard(
    changeRatePercent: Float,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "변화율 카드",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "지난 기간 대비 ${changeRatePercent.format(1)}%",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
private fun WeeklyBarChart(
    summary: AnalyticsSummary,
) {
    val maxUsageMillis = summary.dailyUsages.maxOfOrNull { it.usageMillis } ?: 0L

    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "주간 사용 시간",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )

            if (summary.dailyUsages.isEmpty() || maxUsageMillis <= 0L) {
                Text(text = "일별 사용량 데이터가 없습니다.")
            } else {
                summary.dailyUsages.forEach { point ->
                    val ratio = point.usageMillis.toFloat() / maxUsageMillis.toFloat()

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = "Day ${point.dateEpoch}",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Text(
                                text = point.usageMillis.toHoursText(),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(ratio.coerceIn(0f, 1f))
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(MaterialTheme.colorScheme.primary),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryPieChart(
    summary: AnalyticsSummary,
) {
    val totalUsageMillis = summary.categoryUsages.sumOf { it.usageMillis }

    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "카테고리별 사용 시간",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )

            if (summary.categoryUsages.isEmpty() || totalUsageMillis <= 0L) {
                Text(text = "카테고리 데이터가 없습니다.")
            } else {
                summary.categoryUsages.forEach { point ->
                    val percent = point.usageMillis.toFloat() /
                            totalUsageMillis.toFloat() * 100f

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = point.category,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            text = "${point.usageMillis.toHoursText()} / ${percent.format(1)}%",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TotalUsageCard(
    totalUsageMillis: Long,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "최근 7일 총 사용 시간",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = totalUsageMillis.toHoursText(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun NightUsageRateCard(
    nightUsageRatePercent: Float,
){
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "심야 사용 비중",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "${nightUsageRatePercent.format(1)}%",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = "밤 10시부터 다음 날 오전 6시까지의 사용 시간을 기준으로 계산합니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun TopAppsCard(
    summary: AnalyticsSummary
){
    Card(
        modifier = Modifier.fillMaxWidth()
    ){
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "많이 사용한 앱 Top 5",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )

            if(summary.topApps.isEmpty()){
                Text(
                    text = "앱별 사용량 데이터가 없습니다.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }else{
                summary.topApps.forEachIndexed { index, app ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,

                    ) {
                        Text(
                            text = "${index + 1}. ${app.appName}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                        )

                        Text(
                            text = "실행 ${app.launchCount}회",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    Text(
                        text = app.usageMillis.toHoursText(),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}


private fun Float.format(digits: Int): String {
    return "%.${digits}f".format(this)
}

private fun Long.toHoursText(): String {
    val hours = this / (1000f * 60f * 60f)
    return "${hours.format(1)}시간"
}
