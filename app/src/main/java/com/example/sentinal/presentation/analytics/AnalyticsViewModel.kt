package com.example.sentinal.presentation.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sentinal.domain.usecase.analytics.GetWeeklyAnalyticsUseCase
import com.example.sentinal.domain.usecase.refresh.RefreshAppDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel  @Inject constructor(
    private val refreshAppDataUseCase: RefreshAppDataUseCase,
    private val getWeeklyAnalyticsUseCase: GetWeeklyAnalyticsUseCase
): ViewModel(){
    private val _uiState = MutableStateFlow<AnalyticsUiState>(AnalyticsUiState.Loading)
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadAnalytics()
    }

    fun loadAnalytics(){
        viewModelScope.launch {
            _uiState.value = AnalyticsUiState.Loading

            runCatching {
                refreshAppDataUseCase()
                val summary = getWeeklyAnalyticsUseCase()
                if (summary.totalUsageMillis <= 0L) {
                    AnalyticsUiState.Empty
                } else {
                    AnalyticsUiState.Success(summary)
                }

            }.onSuccess { state ->
                _uiState.value = state
            }.onFailure { throwable ->
                _uiState.value = AnalyticsUiState.Error(
                    message = throwable.message?:"분석 데이터를 불러올 수 없습니다."
                )
            }
        }
    }
}