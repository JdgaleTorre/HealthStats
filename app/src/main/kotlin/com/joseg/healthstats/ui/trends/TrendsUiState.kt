package com.joseg.healthstats.ui.trends

import com.joseg.healthstats.data.repository.DistanceBucket
import com.joseg.healthstats.data.repository.TrendMetric
import com.joseg.healthstats.data.repository.TrendSessionPoint
import java.time.Instant

data class TrendsUiState(
    val points: List<TrendSessionPoint> = emptyList(),
    val availableSources: List<String> = emptyList(),
    val availableExerciseTypes: List<Int> = emptyList(),
    val source: String? = null,
    val exerciseType: Int? = null,
    val bucket: DistanceBucket = DistanceBucket.FIVE_K,
    val metric: TrendMetric = TrendMetric.FINISH_TIME,
    val startDate: Instant? = null,
    val endDate: Instant? = null,
    val isLoading: Boolean = true,
)
