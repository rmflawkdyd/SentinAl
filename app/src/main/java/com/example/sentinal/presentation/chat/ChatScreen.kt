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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sentinal.R
import com.example.sentinal.domain.model.ChatMessage
import com.example.sentinal.domain.model.ChatMessageRole
import com.example.sentinal.presentation.design.BottomInputFade
import com.example.sentinal.presentation.design.HeaderBlock
import com.example.sentinal.presentation.design.ScreenSurface
import com.example.sentinal.presentation.design.SentinCard
import com.example.sentinal.presentation.design.StateMessage
import com.example.sentinal.ui.theme.SentinAITextStyles
import com.example.sentinal.ui.theme.SentinAIInk
import com.example.sentinal.ui.theme.SentinAIInputBorder
import com.example.sentinal.ui.theme.SentinAIMetric
import com.example.sentinal.ui.theme.SentinAIMuted
import com.example.sentinal.ui.theme.SentinAINavy
import com.example.sentinal.ui.theme.SentinAIPanel
import com.example.sentinal.ui.theme.SentinAIText
import com.example.sentinal.ui.theme.SentinAITransparent
import com.example.sentinal.ui.theme.SentinAIWhite

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
            title = stringResource(R.string.chat_title),
            message = stringResource(R.string.chat_loading_message),
            isLoading = true,
        )
    }
}

@Composable
private fun ChatEmpty(
    modifier: Modifier = Modifier,
    inputBottomPadding: Dp,
) {
    val listState = rememberLazyListState()

    ScreenSurface(modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            HeaderBlock(
                title = stringResource(R.string.chat_title),
                subtitle = stringResource(R.string.chat_subtitle),
            )
            Box(modifier = Modifier.weight(1f)) {
                ChatChrome(
                    modifier = Modifier.fillMaxSize(),
                    input = "",
                    isSending = false,
                    inputBottomPadding = inputBottomPadding,
                    listState = listState,
                    onInputChanged = {},
                    onSendClick = {},
                ) {
                    item {
                        StateMessage(
                            title = stringResource(R.string.chat_empty_title),
                            message = stringResource(R.string.chat_empty_message),
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
            title = stringResource(R.string.chat_error_title),
            message = message,
            buttonText = stringResource(R.string.common_retry),
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
    val listState = rememberLazyListState()

    LaunchedEffect(state.message.size, state.isSending) {
        val lastItemIndex = state.message.size + if (state.isSending) 1 else 0
        listState.animateScrollToItem(lastItemIndex)
    }

    ScreenSurface(modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            HeaderBlock(
                title = stringResource(R.string.chat_title),
                subtitle = stringResource(R.string.chat_subtitle),
            )
            Box(modifier = Modifier.weight(1f)) {
                ChatChrome(
                    modifier = Modifier.fillMaxSize(),
                    input = state.input,
                    isSending = state.isSending,
                    inputBottomPadding = inputBottomPadding,
                    listState = listState,
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
                                color = SentinAINavy,
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
    listState: LazyListState,
    onInputChanged: (String) -> Unit,
    onSendClick: () -> Unit,
    content: androidx.compose.foundation.lazy.LazyListScope.() -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(),
            state = listState,
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
                    SentinAINavy,
                    RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                )
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            Text(
                text = message.text,
                color = SentinAIWhite,
                style = SentinAITextStyles.Body,
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
            text = message.source ?: stringResource(R.string.chat_default_source),
            color = SentinAIInk,
            style = SentinAITextStyles.SmallLabel,
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
                    color = SentinAIText,
                    style = SentinAITextStyles.BodyRelaxed,
                )
            }
        }

    }
}

@Composable
private fun AiMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(SentinAIPanel, RoundedCornerShape(8.dp))
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = label, color = SentinAIMuted, style = SentinAITextStyles.Tiny)
        Text(text = value, color = SentinAIMetric, style = SentinAITextStyles.Body)
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
            .background(SentinAIWhite, RoundedCornerShape(16.dp))
            .border(1.dp, SentinAIInputBorder, RoundedCornerShape(16.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = input,
            onValueChange = onInputChanged,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(text = stringResource(R.string.chat_input_placeholder), color = SentinAIMuted)
            },
            singleLine = false,
            maxLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = SentinAIText,
                unfocusedTextColor = SentinAIText,
                disabledTextColor = SentinAIText,
                focusedBorderColor = SentinAITransparent,
                unfocusedBorderColor = SentinAITransparent,
                disabledBorderColor = SentinAITransparent,
                focusedContainerColor = SentinAITransparent,
                unfocusedContainerColor = SentinAITransparent,
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
                containerColor = SentinAINavy,
                contentColor = SentinAIWhite,
                disabledContainerColor = SentinAINavy,
                disabledContentColor = SentinAIWhite,
            ),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(0.dp),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_send),
                contentDescription = stringResource(R.string.chat_send_content_description),
                tint = SentinAIWhite,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}
