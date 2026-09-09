package com.joseg.healthstats.ui.sessiondetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Velocity
import com.joseg.healthstats.ui.common.ExerciseTypeBadge
import com.joseg.healthstats.ui.common.StatCard
import com.joseg.healthstats.ui.common.exerciseTypeLabel
import com.joseg.healthstats.ui.theme.Spacing
import java.time.Duration
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val headerDateFormatter = DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy · h:mm a")

@Composable
fun SessionDetailScreen(uiState: SessionDetailUiState, modifier: Modifier = Modifier) {
    val session = uiState.session ?: return
    val detail = uiState.detail
    val zone = session.startZoneOffset?.let { ZoneId.ofOffset("UTC", it) } ?: ZoneId.systemDefault()

    Column(
        modifier = modifier.fillMaxWidth().padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.xl),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            ExerciseTypeBadge(exerciseType = session.exerciseType, size = 56.dp)
            Column {
                Text(
                    text = session.title?.takeIf { it.isNotBlank() } ?: exerciseTypeLabel(session.exerciseType),
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                    text = "${exerciseTypeLabel(session.exerciseType)} · " +
                        headerDateFormatter.format(session.startTime.atZone(zone)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        if (detail != null) {
            val stats = buildList {
                detail.duration?.let { add(StatEntry("Duration", formatDuration(it), Icons.Filled.Timer)) }
                detail.distance?.let { add(StatEntry("Distance", formatDistance(it), Icons.Filled.Route)) }
                detail.calories?.let { add(StatEntry("Calories", formatCalories(it), Icons.Filled.LocalFireDepartment)) }
                detail.heartRateAvg?.let { avg ->
                    val range = if (detail.heartRateMin != null && detail.heartRateMax != null) {
                        " (${detail.heartRateMin}–${detail.heartRateMax})"
                    } else {
                        ""
                    }
                    add(StatEntry("Heart rate", "$avg bpm avg$range", Icons.Filled.Favorite))
                }
                detail.elevationGained?.let { add(StatEntry("Elevation gained", formatDistance(it), Icons.Filled.Terrain)) }
                detail.speedAvg?.let { add(StatEntry("Avg speed", formatSpeed(it), Icons.Filled.Speed)) }
                detail.steps?.let {
                    add(StatEntry("Steps", String.format(Locale.getDefault(), "%,d", it), Icons.Filled.DirectionsWalk))
                }
            }
            StatGrid(stats)
        }
    }
}

private data class StatEntry(val label: String, val value: String, val icon: ImageVector)

@Composable
private fun StatGrid(stats: List<StatEntry>) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        stats.chunked(2).forEach { rowStats ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                rowStats.forEach { stat ->
                    StatCard(
                        label = stat.label,
                        value = stat.value,
                        icon = stat.icon,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (rowStats.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

private fun formatDuration(duration: Duration): String {
    val hours = duration.toHours()
    val minutes = duration.toMinutes() % 60
    return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
}

private fun formatDistance(length: Length): String =
    String.format(Locale.getDefault(), "%.2f km", length.inKilometers)

private fun formatCalories(energy: Energy): String =
    String.format(Locale.getDefault(), "%.0f kcal", energy.inKilocalories)

private fun formatSpeed(velocity: Velocity): String =
    String.format(Locale.getDefault(), "%.1f km/h", velocity.inKilometersPerHour)
