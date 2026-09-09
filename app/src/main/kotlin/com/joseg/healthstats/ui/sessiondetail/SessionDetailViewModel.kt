package com.joseg.healthstats.ui.sessiondetail

import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joseg.healthstats.data.repository.HealthConnectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SessionDetailViewModel(private val repository: HealthConnectRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionDetailUiState())
    val uiState: StateFlow<SessionDetailUiState> = _uiState.asStateFlow()

    fun loadSession(session: ExerciseSessionRecord) {
        _uiState.value = SessionDetailUiState(session = session, isLoading = true)
        viewModelScope.launch {
            val detail = repository.getSessionDetail(session)
            _uiState.update { it.copy(detail = detail, isLoading = false) }
        }
    }
}
