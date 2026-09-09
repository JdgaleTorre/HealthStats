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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.joseg.healthstats.data.repository.TrendMetric
import com.joseg.healthstats.data.repository.metricValueSeconds
import com.joseg.healthstats.ui.common.EmptyState
import com.joseg.healthstats.ui.common.SectionHeader
import com.joseg.healthstats.ui.common.exerciseTypeLabel
import com.joseg.healthstats.ui.common.sourceAppLabel
import com.joseg.healthstats.ui.theme.Spacing
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.lineSeries
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme

@Composable
fun TrendsScreen(
    uiState: TrendsUiState,
    onSourceSelected: (String) -> Unit,
    onExerciseTypeSelected: (Int) -> Unit,
    onMetricSelected: (TrendMetric) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        SectionHeader(title = "Trends")
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            LabeledFilterRow(
                caption = "Source",
                options = uiState.availableSources,
                selected = uiState.source,
                label = { sourceAppLabel(it) },
                onSelected = onSourceSelected,
            )
            LabeledFilterRow(
                caption = "Exercise type",
                options = uiState.availableExerciseTypes,
                selected = uiState.exerciseType,
                label = { exerciseTypeLabel(it) },
                onSelected = onExerciseTypeSelected,
            )
            LabeledFilterRow(
                caption = "Metric",
                options = listOf(TrendMetric.FINISH_TIME, TrendMetric.PACE),
                selected = uiState.metric,
                label = { if (it == TrendMetric.FINISH_TIME) "Finish time" else "Pace" },
                onSelected = onMetricSelected,
            )
        }

        // Vico's chart model rejects an empty series, so it must never be composed with no
        // points, loading or not: show a spinner or the empty state until real data exists.
        when {
            uiState.points.isNotEmpty() ->
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                ) {
                    TrendChart(
                        uiState = uiState,
                        modifier = Modifier.fillMaxWidth().height(240.dp).padding(Spacing.md),
                    )
                }
            uiState.isLoading ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            else -> TrendsEmptyState(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun <T> LabeledFilterRow(
    caption: String,
    options: List<T>,
    selected: T?,
    label: (T) -> String,
    onSelected: (T) -> Unit,
) {
    if (options.isEmpty()) return
    Text(
        text = caption,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        contentPadding = PaddingValues(vertical = Spacing.xs),
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
        if (uiState.points.isEmpty()) return@LaunchedEffect
        modelProducer.runTransaction {
            lineSeries {
                series(
                    x = uiState.points.indices.map { it.toDouble() },
                    y = uiState.points.map { it.metricValueSeconds(uiState.metric) },
                )
            }
        }
    }

    val vicoTheme = rememberM3VicoTheme(lineCartesianLayerColors = listOf(MaterialTheme.colorScheme.primary))
    ProvideVicoTheme(vicoTheme) {
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
}

@Composable
private fun TrendsEmptyState(modifier: Modifier = Modifier) {
    EmptyState(
        icon = Icons.Filled.ShowChart,
        title = "No trend data yet",
        message = "No sessions match this bucket yet.",
        modifier = modifier,
    )
}
