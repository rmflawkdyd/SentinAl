package com.example.sentinal.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sentinal.domain.model.ChatMessage
import com.example.sentinal.domain.model.ChatMessageRole
import com.example.sentinal.domain.usecase.ai.AskChatQuestionUseCase
import com.example.sentinal.domain.usecase.ai.ReleaseGemmaRuntimeUseCase
import com.example.sentinal.domain.usecase.ai.ReleaseGeminiNanoRuntimeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val askChatQuestionUseCase: AskChatQuestionUseCase,
    private val releaseGemmaRuntimeUseCase: ReleaseGemmaRuntimeUseCase,
    private val releaseGeminiNanoRuntimeUseCase: ReleaseGeminiNanoRuntimeUseCase,
): ViewModel(){

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Success(
        message = listOf(
            ChatMessage(
                id = "welcome",
                role = ChatMessageRole.Assistant,
                text = "휴대폰 상태, 발열 참고, 느려짐, 배터리 사용 패턴, 보안 관련 제한 안내, 주간 사용량에 대해 물어볼 수 있어요.",
                source = "SentinAI 안내",
            )
            )
    ))

    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun onInputChanged(input: String){
        val currentState = _uiState.value
        if(currentState is ChatUiState.Success){
            _uiState.value = currentState.copy(input = input)
        }
    }

    fun sendMessage(){
        val currentState = _uiState.value
        if(currentState !is ChatUiState.Success) return

        val question = currentState.input.trim()
        if(question.isEmpty() || currentState.isSending)return

        val userMessage = ChatMessage(
            id = "user-${System.currentTimeMillis()}",
            role = ChatMessageRole.User,
            text = question,
        )

        _uiState.value = currentState.copy(
            message = currentState.message + userMessage,
            input = "",
            isSending = true
        )

        viewModelScope.launch {
            runCatching {
                askChatQuestionUseCase(question)
            }.onSuccess { answer ->
                val latestState = _uiState.value
                if(latestState is ChatUiState.Success){
                    val assistantMessage = ChatMessage(
                        id = "assistant-${System.currentTimeMillis()}",
                        role = ChatMessageRole.Assistant,
                        text = answer.text,
                        source = answer.source,
                        modelTier = answer.modelTier
                    )

                    _uiState.value = latestState.copy(
                        message = latestState.message + assistantMessage,
                        isSending = false,
                    )
                }
            }.onFailure { throwable ->
                _uiState.value = ChatUiState.Error(
                    message = throwable.message ?: "답변을 생성하지 못했습니다.",
                )

            }
        }


    }

    fun retryToInitialState() {
        _uiState.value = ChatUiState.Success(
            message = listOf(
                ChatMessage(
                    id = "welcome-${System.currentTimeMillis()}",
                    role = ChatMessageRole.Assistant,
                    text = "다시 질문해 주세요. 내부 데이터 기준으로 답변할게요.",
                    source = "SentinAI 안내",
                )
            )
        )
    }

    fun releaseGemmaSession() {
        releaseGemmaRuntimeUseCase()
    }

    fun releaseGeminiSession() {
        releaseGeminiNanoRuntimeUseCase()
    }

}
