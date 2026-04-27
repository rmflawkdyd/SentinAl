package com.example.sentinal.presentation.root

import androidx.lifecycle.ViewModel
import com.example.sentinal.domain.usecase.permission.HasUsageStatsPermissionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AppEntryViewModel @Inject constructor(
    private val hasUsageStatsPermission: HasUsageStatsPermissionUseCase,
): ViewModel(){

    private val _uiState = MutableStateFlow<AppEntryUiState>(AppEntryUiState.Loading)
    val uiState: StateFlow<AppEntryUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh(){
        _uiState.value = runCatching {
            if(hasUsageStatsPermission()){
                AppEntryUiState.Ready
            }else{
                AppEntryUiState.NeedsOnboarding
            }
        }.getOrElse{throwable ->
            AppEntryUiState.Error(
                throwable.message ?: "앱 시작 상태를 확인할 수 없습니다."
            )
        }
    }
}