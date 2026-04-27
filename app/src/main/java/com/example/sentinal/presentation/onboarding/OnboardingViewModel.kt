package com.example.sentinal.presentation.onboarding

import androidx.lifecycle.ViewModel
import com.example.sentinal.data.permission.UsageAccessSettingsNavigator
import com.example.sentinal.domain.usecase.permission.HasUsageStatsPermissionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val hasUsageStatsPermission: HasUsageStatsPermissionUseCase,
    private val usageAccessSettingsNavigator: UsageAccessSettingsNavigator
): ViewModel(){
    private val _uiState = MutableStateFlow<OnboardingUiState>(OnboardingUiState.Loading)
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()
    init {
        refreshPermissionState()
    }
    fun refreshPermissionState(){
        _uiState.value = runCatching {
            if(hasUsageStatsPermission()){
                OnboardingUiState.Granted
            }else{
                OnboardingUiState.NeedsPermission
            }
        }.getOrElse {throwable ->
            OnboardingUiState.Error(
                throwable.message ?: "권한 상태를 확인할 수 없습니다."
            )

        }
    }
    fun openUsageAccessSettings(){
        usageAccessSettingsNavigator.open()
    }
}