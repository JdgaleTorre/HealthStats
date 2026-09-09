package com.joseg.healthstats.ui.sessionlist

import androidx.health.connect.client.records.ExerciseSessionRecord
import com.joseg.healthstats.data.repository.SessionFilter

data class SessionListUiState(
    val sessions: List<ExerciseSessionRecord> = emptyList(),
    val availableSources: List<String> = emptyList(),
    val availableExerciseTypes: List<Int> = emptyList(),
    val filter: SessionFilter = SessionFilter(),
    val isLoading: Boolean = true,
)
