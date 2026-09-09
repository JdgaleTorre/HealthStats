package com.joseg.healthstats.ui.trends

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.joseg.healthstats.data.repository.TrendMetric
import com.joseg.healthstats.data.repository.metricValueSeconds
import com.joseg.healthstats.ui.common.exerciseTypeLabel
import com.joseg.healthstats.ui.common.sourceAppLabel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.lineSeries
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart

@Composable
fun TrendsScreen(
    uiState: TrendsUiState,
    onSourceSelected: (String) -> Unit,
    onExerciseTypeSelected: (Int) -> Unit,
    onMetricSelected: (TrendMetric) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        FilterRow(
            options = uiState.availableSources,
            selected = uiState.source,
            label = { sourceAppLabel(it) },
            onSelected = onSourceSelected,
        )
        FilterRow(
            options = uiState.availableExerciseTypes,
            selected = uiState.exerciseType,
            label = { exerciseTypeLabel(it) },
            onSelected = onExerciseTypeSelected,
        )
        FilterRow(
            options = listOf(TrendMetric.FINISH_TIME, TrendMetric.PACE),
            selected = uiState.metric,
            label = { if (it == TrendMetric.FINISH_TIME) "Finish time" else "Pace" },
            onSelected = onMetricSelected,
        )

        if (uiState.points.isEmpty() && !uiState.isLoading) {
            TrendsEmptyState(modifier = Modifier.fillMaxSize())
        } else {
            TrendChart(uiState = uiState, modifier = Modifier.fillMaxWidth().height(240.dp).padding(top = 16.dp))
        }
    }
}

@Composable
private fun <T> FilterRow(
    options: List<T>,
    selected: T?,
    label: (T) -> String,
    onSelected: (T) -> Unit,
) {
    if (options.isEmpty()) return
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp),
    ) {
        items(options) { option ->
            FilterChip(
                selected = option == selected,
                onClick = { onSelected(option) },
                label = { Text(label(option)) },
            )
        }
    }
}

@Composable
private fun TrendChart(uiState: TrendsUiState, modifier: Modifier = Modifier) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(uiState.points, uiState.metric) {
        modelProducer.runTransaction {
            lineSeries {
                series(
                    x = uiState.points.indices.map { it.toDouble() },
                    y = uiState.points.map { it.metricValueSeconds(uiState.metric) },
                )
            }
        }
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(),
        ),
        modelProducer = modelProducer,
        modifier = modifier,
    )
}

@Composable
private fun TrendsEmptyState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.padding(24.dp), contentAlignment = Alignment.Center) {
        Text(
            text = "No sessions match this bucket yet.",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
