package com.joseg.healthstats.ui.sessionlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joseg.healthstats.data.repository.HealthConnectRepository
import com.joseg.healthstats.data.repository.SessionFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SessionListViewModel(private val repository: HealthConnectRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionListUiState())
    val uiState: StateFlow<SessionListUiState> = _uiState.asStateFlow()

    init {
        loadFilterOptions()
        loadSessions()
    }

    fun updateFilter(filter: SessionFilter) {
        _uiState.update { it.copy(filter = filter) }
        loadSessions()
    }

    private fun loadFilterOptions() {
        viewModelScope.launch {
            val sources = repository.listSources()
            val types = repository.listExerciseTypes()
            _uiState.update { it.copy(availableSources = sources, availableExerciseTypes = types) }
        }
    }

    private fun loadSessions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val sessions = repository.querySessions(_uiState.value.filter)
            _uiState.update { it.copy(sessions = sessions, isLoading = false) }
        }
    }
}
