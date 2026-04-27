package com.example.sentinal.presentation.guardian

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sentinal.domain.model.GuardianChartPoint
import com.example.sentinal.domain.usecase.aggregate.GetDeviceAggregatesForRangeUseCase
import com.example.sentinal.domain.usecase.ai.GenerateGuardianInsightUseCase
import com.example.sentinal.domain.usecase.ai.ReleaseGemmaRuntimeUseCase
import com.example.sentinal.domain.usecase.ai.ReleaseGeminiNanoRuntimeUseCase
import com.example.sentinal.domain.usecase.guardian.GetGuardianResultUseCase
import com.example.sentinal.domain.usecase.refresh.RefreshAppDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GuardianViewModel @Inject constructor(
    private val refreshAppDataUseCase: RefreshAppDataUseCase,
    private val getGuardianResultUseCase: GetGuardianResultUseCase,
    private val generateGuardianInsightUseCase: GenerateGuardianInsightUseCase,
    private val getDeviceAggregatesForRangeUseCase: GetDeviceAggregatesForRangeUseCase,
    private val releaseGemmaRuntimeUseCase: ReleaseGemmaRuntimeUseCase,
    private val releaseGeminiNanoRuntimeUseCase: ReleaseGeminiNanoRuntimeUseCase,

    ): ViewModel() {

    private val _uiState = MutableStateFlow<GuardianUiState>(GuardianUiState.Loading)
    val uiState: StateFlow<GuardianUiState> = _uiState.asStateFlow()

    init {
        loadGuardian()
    }

    fun loadGuardian(){
        viewModelScope.launch {
            _uiState.value = GuardianUiState.Loading
            runCatching {
                val refreshWindow = refreshAppDataUseCase()

                val guardianResult = getGuardianResultUseCase()?:return@runCatching GuardianUiState.Empty

                val insight = generateGuardianInsightUseCase(guardianResult)

                val chartPoints = getDeviceAggregatesForRangeUseCase(refreshWindow.chartFromTimestamp)
                    .map { aggregate->
                        GuardianChartPoint(
                            timestamp = aggregate.windowStart,
                            availableMemPercent = aggregate.avgAvailableMemPercent,
                            appSwitchCount = aggregate.appSwitchCount,
                        )
                    }
                GuardianUiState.Success(
                    score = guardianResult.score,
                    status = guardianResult.status,
                    insightTitle = insight.title,
                    insightBody = insight.body,
                    source = insight.source,
                    modelTier = insight.modelTier,
                    chartPoints = chartPoints,

                    )
            }.onSuccess { state ->
                _uiState.value = state
            }.onFailure { throwable ->
                _uiState.value = GuardianUiState.Error(
                    message = throwable.message ?: "알 수 없는 오류가 발생했습니다."
                )
            }
        }
    }

    fun releaseGemmaSession() {
        releaseGemmaRuntimeUseCase()
    }

    fun releaseGeminiSession() {
        releaseGeminiNanoRuntimeUseCase()
    }

}
