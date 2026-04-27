package com.example.sentinal.presentation.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun OnboardingScreen(
    paddingValues: PaddingValues,
    onPermissionGranted: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if(uiState is OnboardingUiState.Granted){
            onPermissionGranted()
        }
    }

    when(val state = uiState){
        OnboardingUiState.Loading->{
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator()
                Text(
                    text = "권한 상태를 확인하는 중입니다.",
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }

        OnboardingUiState.NeedsPermission->{
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "SentinAl 시작하기",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "최근 5분 분석과 주간 리포트를 위해 앱 사용 기록 권한이 필요합니다.",
                    modifier = Modifier.padding(top = 16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = "데이터는 기기 안에서만 처리되며 외부로 전송되지 않습니다.",
                    modifier = Modifier.padding(top = 12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )

                Button(
                    onClick = viewModel::openUsageAccessSettings,
                    modifier = Modifier.padding(top = 24.dp)
                ) {
                    Text(text = "권한 설정 열기")
                }
                Button(
                    onClick = viewModel::refreshPermissionState,
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Text(text = "권한 다시 확인")
                }
            }
        }

        is OnboardingUiState.Error ->{
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error
                )
                Button(
                    onClick = viewModel::refreshPermissionState,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(
                        text = "다시 시도"
                    )
                }
            }
            }

        OnboardingUiState.Granted -> Unit
    }


}