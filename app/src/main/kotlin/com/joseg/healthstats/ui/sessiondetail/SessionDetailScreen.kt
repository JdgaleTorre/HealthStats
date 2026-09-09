package com.joseg.healthstats.ui.sessiondetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Velocity
import com.joseg.healthstats.ui.common.exerciseTypeLabel
import java.time.Duration
import java.util.Locale

@Composable
fun SessionDetailScreen(uiState: SessionDetailUiState, modifier: Modifier = Modifier) {
    val session = uiState.session ?: return
    val detail = uiState.detail

    Column(modifier = modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text = session.title?.takeIf { it.isNotBlank() } ?: exerciseTypeLabel(session.exerciseType),
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = exerciseTypeLabel(session.exerciseType),
            style = MaterialTheme.typography.bodyMedium,
        )

        if (detail != null) {
            detail.duration?.let { StatRow("Duration", formatDuration(it)) }
            detail.distance?.let { StatRow("Distance", formatDistance(it)) }
            detail.calories?.let { StatRow("Calories", formatCalories(it)) }
            detail.heartRateAvg?.let { avg ->
                val range = if (detail.heartRateMin != null && detail.heartRateMax != null) {
                    " (${detail.heartRateMin}–${detail.heartRateMax})"
                } else {
                    ""
                }
                StatRow("Heart rate", "$avg bpm avg$range")
            }
            detail.elevationGained?.let { StatRow("Elevation gained", formatDistance(it)) }
            detail.speedAvg?.let { StatRow("Avg speed", formatSpeed(it)) }
            detail.steps?.let { StatRow("Steps", String.format(Locale.getDefault(), "%,d", it)) }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
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
