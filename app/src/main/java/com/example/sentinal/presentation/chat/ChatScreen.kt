package com.example.sentinal.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sentinal.R
import com.example.sentinal.domain.model.ChatMessage
import com.example.sentinal.domain.model.ChatMessageRole
import com.example.sentinal.presentation.design.BottomInputFade
import com.example.sentinal.presentation.design.HeaderBlock
import com.example.sentinal.presentation.design.ScreenSurface
import com.example.sentinal.presentation.design.SentinCard
import com.example.sentinal.presentation.design.SentinInk
import com.example.sentinal.presentation.design.SentinMuted
import com.example.sentinal.presentation.design.SentinNavy
import com.example.sentinal.presentation.design.SentinPanel
import com.example.sentinal.presentation.design.SentinText
import com.example.sentinal.presentation.design.StateMessage

@Composable
fun ChatScreen(
    paddingValues: PaddingValues,
    viewModel: ChatViewModel = hiltViewModel(),
) {
    DisposableEffect(viewModel) {
        onDispose {
            viewModel.releaseGemmaSession()
            viewModel.releaseGeminiSession()
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val density = LocalDensity.current
    val scaffoldBottomPadding = paddingValues.calculateBottomPadding()
    val imeBottomPadding = with(density) {
        WindowInsets.ime.getBottom(this).toDp()
    }
    val inputBottomPadding = (imeBottomPadding - scaffoldBottomPadding).coerceAtLeast(0.dp)

    when (val state = uiState) {
        ChatUiState.Loading -> ChatLoading(Modifier.padding(paddingValues))
        ChatUiState.Empty -> ChatEmpty(
            modifier = Modifier.padding(paddingValues),
            inputBottomPadding = inputBottomPadding,
        )
        is ChatUiState.Error -> ChatError(
            modifier = Modifier.padding(paddingValues),
            message = state.message,
            onRetry = viewModel::retryToInitialState,
        )
        is ChatUiState.Success -> ChatSuccess(
            modifier = Modifier.padding(paddingValues),
            state = state,
            inputBottomPadding = inputBottomPadding,
            onInputChanged = viewModel::onInputChanged,
            onSendClick = viewModel::sendMessage,
        )
    }
}

@Composable
private fun ChatLoading(modifier: Modifier = Modifier) {
    ScreenSurface(modifier) {
        StateMessage(
            title = "AI Chat",
            message = "AI Chat을 준비하는 중입니다.",
            isLoading = true,
        )
    }
}

@Composable
private fun ChatEmpty(
    modifier: Modifier = Modifier,
    inputBottomPadding: Dp,
) {
    ScreenSurface(modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            HeaderBlock(
                title = "AI chat",
                subtitle = "Ask about your device activity and health",
            )
            Box(modifier = Modifier.weight(1f)) {
                ChatChrome(
                    modifier = Modifier.fillMaxSize(),
                    input = "",
                    isSending = false,
                    inputBottomPadding = inputBottomPadding,
                    onInputChanged = {},
                    onSendClick = {},
                ) {
                    item {
                        StateMessage(
                            title = "아직 대화가 없습니다.",
                            message = "내부 데이터 기준으로만 답변할 수 있습니다.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatError(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: () -> Unit,
) {
    ScreenSurface(modifier) {
        StateMessage(
            title = "답변을 생성하지 못했습니다.",
            message = message,
            buttonText = "다시 시도",
            onButtonClick = onRetry,
        )
    }
}

@Composable
private fun ChatSuccess(
    modifier: Modifier = Modifier,
    state: ChatUiState.Success,
    inputBottomPadding: Dp,
    onInputChanged: (String) -> Unit,
    onSendClick: () -> Unit,
) {
    ScreenSurface(modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            HeaderBlock(
                title = "AI chat",
                subtitle = "Ask about your device activity and health",
            )
            Box(modifier = Modifier.weight(1f)) {
                ChatChrome(
                    modifier = Modifier.fillMaxSize(),
                    input = state.input,
                    isSending = state.isSending,
                    inputBottomPadding = inputBottomPadding,
                    onInputChanged = onInputChanged,
                    onSendClick = onSendClick,
                ) {
                    items(state.message) { message ->
                        ChatMessageItem(message = message)
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                    if (state.isSending) {
                        item {
                            CircularProgressIndicator(
                                color = SentinNavy,
                            )
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(56.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatChrome(
    modifier: Modifier = Modifier,
    input: String,
    isSending: Boolean,
    inputBottomPadding: Dp,
    onInputChanged: (String) -> Unit,
    onSendClick: () -> Unit,
    content: androidx.compose.foundation.lazy.LazyListScope.() -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 136.dp),
            verticalArrangement = Arrangement.Top,
            content = content,
        )
        BottomInputFade(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = inputBottomPadding)
        ) {
            ChatInput(
                input = input,
                isSending = isSending,
                onInputChanged = onInputChanged,
                onSendClick = onSendClick,
            )
        }
    }
}

@Composable
private fun ChatMessageItem(message: ChatMessage) {
    if (message.role == ChatMessageRole.User) {
        UserMessage(message)
    } else {
        AiMessage(message)
    }
}

@Composable
private fun UserMessage(message: ChatMessage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End,
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 298.dp)
                .background(
                    SentinNavy,
                    RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                )
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            Text(
                text = message.text,
                color = Color.White,
                fontSize = 16.sp,
                lineHeight = 24.sp,
            )
        }
    }
}

@Composable
private fun AiMessage(message: ChatMessage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = message.source ?: "SentinAI Rule Engine Analysis",
            color = SentinInk,
            fontSize = 11.sp,
            lineHeight = 16.sp,
        )
        SentinCard(
            modifier = Modifier.widthIn(max = 315.dp),
            shape = RoundedCornerShape(topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
        ) {
            Column(
                modifier = Modifier.padding(21.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = message.text,
                    color = SentinText,
                    fontSize = 16.sp,
                    lineHeight = 26.sp,
                )
            }
        }

    }
}

@Composable
private fun AiMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(SentinPanel, RoundedCornerShape(8.dp))
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = label, color = SentinMuted, fontSize = 10.sp, lineHeight = 15.sp)
        Text(text = value, color = Color(0xFF406372), fontSize = 16.sp, lineHeight = 24.sp)
    }
}

@Composable
private fun ChatInput(
    modifier: Modifier = Modifier,
    input: String,
    isSending: Boolean,
    onInputChanged: (String) -> Unit,
    onSendClick: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(1.dp, Color(0x99E2E8F0), RoundedCornerShape(16.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = input,
            onValueChange = onInputChanged,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(text = "What should I optimize?", color = SentinMuted)
            },
            singleLine = false,
            maxLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = SentinText,
                unfocusedTextColor = SentinText,
                disabledTextColor = SentinText,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
            ),
        )
        Button(
            onClick = {
                onSendClick()
                focusManager.clearFocus()
                keyboardController?.hide()
            },
            enabled = input.isNotBlank() && !isSending,
            modifier = Modifier.size(44.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SentinNavy,
                contentColor = Color.White,
                disabledContainerColor = SentinNavy,
                disabledContentColor = Color.White,
            ),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(0.dp),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_send),
                contentDescription = "Send message",
                tint = Color.White,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}
