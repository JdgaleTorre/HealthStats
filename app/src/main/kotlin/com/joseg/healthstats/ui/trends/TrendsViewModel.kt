package com.joseg.healthstats.ui.trends

import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joseg.healthstats.data.repository.DistanceBucket
import com.joseg.healthstats.data.repository.HealthConnectRepository
import com.joseg.healthstats.data.repository.TrendMetric
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrendsViewModel(private val repository: HealthConnectRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(TrendsUiState())
    val uiState: StateFlow<TrendsUiState> = _uiState.asStateFlow()

    init {
        loadFilterOptions()
    }

    private fun loadFilterOptions() {
        viewModelScope.launch {
            val sources = repository.listSources()
            val types = repository.listExerciseTypes()
            _uiState.update {
                it.copy(
                    availableSources = sources,
                    availableExerciseTypes = types,
                    source = it.source ?: sources.firstOrNull(),
                    exerciseType = it.exerciseType ?: types.firstOrNull { t -> t == ExerciseSessionRecord.EXERCISE_TYPE_RUNNING }
                        ?: types.firstOrNull(),
                )
            }
            recompute()
        }
    }

    fun setSource(source: String) {
        _uiState.update { it.copy(source = source) }
        recompute()
    }

    fun setExerciseType(exerciseType: Int) {
        _uiState.update { it.copy(exerciseType = exerciseType) }
        recompute()
    }

    fun setBucket(bucket: DistanceBucket) {
        _uiState.update { it.copy(bucket = bucket) }
        recompute()
    }

    fun setMetric(metric: TrendMetric) {
        _uiState.update { it.copy(metric = metric) }
    }

    fun setDateRange(startDate: java.time.Instant?, endDate: java.time.Instant?) {
        _uiState.update { it.copy(startDate = startDate, endDate = endDate) }
        recompute()
    }

    private fun recompute() {
        val state = _uiState.value
        val source = state.source
        val exerciseType = state.exerciseType
        if (source == null || exerciseType == null) {
            _uiState.update { it.copy(points = emptyList(), isLoading = false) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val points = repository.computeTrendSessions(
                exerciseType = exerciseType,
                source = source,
                bucket = state.bucket,
                startDate = state.startDate,
                endDate = state.endDate,
            )
            _uiState.update { it.copy(points = points, isLoading = false) }
        }
    }
}
