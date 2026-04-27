package com.example.sentinal.presentation.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sentinal.domain.model.ChatMessage
import com.example.sentinal.domain.model.ChatMessageRole

@Composable
fun ChatScreen(
    paddingValues: PaddingValues,
    viewModel: ChatViewModel = hiltViewModel()
) {
    DisposableEffect(viewModel) {
        onDispose {
            viewModel.releaseGemmaSession()
            viewModel.releaseGeminiSession()
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val state = uiState) {
        ChatUiState.Loading -> ChatLoading(
            modifier = Modifier.padding(paddingValues),
        )

        ChatUiState.Empty -> ChatEmpty(
            modifier = Modifier.padding(paddingValues),
        )

        is ChatUiState.Error -> ChatError(
            modifier = Modifier.padding(paddingValues),
            message = state.message,
            onRetry = viewModel::retryToInitialState,
        )

        is ChatUiState.Success -> ChatSuccess(
            modifier = Modifier.padding(paddingValues),
            state = state,
            onInputChanged = viewModel::onInputChanged,
            onSendClick = viewModel::sendMessage,
        )
    }
}

@Composable
private fun ChatLoading(
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier.fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Text(
            text = "AI Chat을 준비하는 중입니다.",
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}


@Composable
private fun ChatEmpty(
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier.fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "아직 대화가 없습니다.",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun ChatError(
    modifier: Modifier = Modifier,
    message: String,
    onRetry:()-> Unit
) {
    Column(
        modifier = modifier.fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "답변을 생성하지 못했습니다.",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.titleMedium
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

@Composable
private fun ChatSuccess(
    modifier: Modifier = Modifier,
    state: ChatUiState.Success,
    onInputChanged: (String) -> Unit,
    onSendClick:()-> Unit
){
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "AI Chat",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "내부 데이터 기준으로만 답변합니다. 바이러스 검사, 하드웨어 고장, 온도 측정은 직접 수행하지 않습니다.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.message) { message ->
                ChatMessageItem(message = message)
            }

            if (state.isSending){
                item{
                    CircularProgressIndicator()
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = state.input,
                onValueChange = onInputChanged,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(text = "휴대폰 상태에 대해 질문하기")
                },
                singleLine = true,
            )
            Button(
                onClick = onSendClick,
                enabled = state.input.isNotBlank() && !state.isSending,
            ) {
                Text(text = "전송")
            }
        }
    }

}

@Composable
private fun ChatMessageItem(
    message: ChatMessage
){
    Card(
        modifier = Modifier.fillMaxWidth(),
    ){
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = if (message.role == ChatMessageRole.User) "나" else "SentinAI",
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                text = message.text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )

            message.source?.let { source ->
                Text(
                    text = "출처: $source",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
