package com.example.sentinal.presentation.guardian

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun GuardianScreen(
    paddingValues: PaddingValues,
    viewModel: GuardianViewModel = hiltViewModel()
){
    DisposableEffect(viewModel) {
        onDispose {
            viewModel.releaseGemmaSession()
            viewModel.releaseGeminiSession()
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        GuardianUiState.Loading -> {
            GuardianLoading(
                modifier = Modifier.padding(paddingValues)
            )
        }

        GuardianUiState.Empty -> {
            GuardianEmpty(
                modifier = Modifier.padding(paddingValues),
                onReload = viewModel::loadGuardian,
            )
        }

        is GuardianUiState.Error -> {
            GuardianError(
                modifier = Modifier.padding(paddingValues),
                message = state.message,
                onReload = viewModel::loadGuardian,
            )
        }

        is GuardianUiState.Success -> {
            GuardianSuccess(
                modifier = Modifier.padding(paddingValues),
                state = state,
            )
        }
    }
}

@Composable
private fun GuardianLoading(
    modifier: Modifier = Modifier,
){
    Column(
        modifier =modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Text(
            text = "최근 데이터를 불러오는 중입니다.",
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
private fun GuardianEmpty(
    modifier: Modifier = Modifier,
    onReload:()-> Unit
){
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "표시할 Guardian 데이터가 없습니다.",
            style = MaterialTheme.typography.titleMedium
        )

        Button(
            onClick = onReload,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "다시 시도")
        }
    }
}

@Composable
private fun GuardianError(
    modifier: Modifier = Modifier,
    message: String,
    onReload:()-> Unit
){
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "오류가 발생했습니다.",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error
        )

        Text(
            text = message,
            modifier = Modifier.padding(top = 8.dp),

        )
        Button(
            onClick = onReload,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "다시 시도")
        }
    }
}

@Composable
private fun GuardianSuccess(
    modifier: Modifier = Modifier,
    state: GuardianUiState.Success,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Guardian",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "리소스 점수 ${state.score}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "상태: ${state.status}",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "24시간 그래프",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )

                if (state.chartPoints.isEmpty()) {
                    Text(
                        text = "아직 24시간 그래프를 표시할 데이터가 부족합니다.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                } else {
                    val latestPoint = state.chartPoints.last()

                    Text(
                        text = "집계 포인트 ${state.chartPoints.size}개",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = "최근 메모리 여유 ${latestPoint.availableMemPercent.format(1)}%",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = "최근 앱 전환 ${latestPoint.appSwitchCount}회",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }


        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = state.insightTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = state.insightBody,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = "Source: ${state.source}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

private fun Float.format(digits: Int): String {
    return "%.${digits}f".format(this)
}
