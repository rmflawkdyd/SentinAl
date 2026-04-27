package com.example.sentinal.presentation.root


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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sentinal.presentation.main.MainShellScreen
import com.example.sentinal.presentation.onboarding.OnboardingScreen

@Composable
fun AppEntryScreen(
    paddingValues: PaddingValues,
    viewModel: AppEntryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when(val state = uiState){
        AppEntryUiState.Loading ->{
            AppEntryLoading(
                modifier = Modifier.padding(paddingValues)
            )
        }

        AppEntryUiState.NeedsOnboarding->{
            OnboardingScreen(
                paddingValues = paddingValues,
                onPermissionGranted = viewModel::refresh
            )
        }

        AppEntryUiState.Ready -> {
            MainShellScreen(
                paddingValues = paddingValues
            )
        }

        is AppEntryUiState.Error -> {
            AppEntryError(
                modifier = Modifier.padding(paddingValues),
                message = state.message,
                onRetry = viewModel::refresh,
            )
        }
    }
}

@Composable
private fun AppEntryLoading(
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
            text = "앱 시작 상태를 확인하는 중입니다.",
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

@Composable
private fun AppEntryError(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "앱을 시작할 수 없습니다.",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = message,
            modifier = Modifier.padding(top = 8.dp),
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Text(text = "다시 시도")
        }
    }
}