package com.joseg.healthstats.ui.sessiondetail

import androidx.health.connect.client.records.ExerciseSessionRecord
import com.joseg.healthstats.data.repository.SessionDetail

data class SessionDetailUiState(
    val session: ExerciseSessionRecord? = null,
    val detail: SessionDetail? = null,
    val isLoading: Boolean = true,
)
