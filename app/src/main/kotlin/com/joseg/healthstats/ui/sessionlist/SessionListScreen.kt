package com.joseg.healthstats.ui.sessionlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.joseg.healthstats.ui.common.exerciseTypeLabel
import com.joseg.healthstats.ui.common.sourceAppLabel
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val rowDateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy · h:mm a")

@Composable
fun SessionListScreen(
    uiState: SessionListUiState,
    onSessionClick: (ExerciseSessionRecord) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (uiState.sessions.isEmpty() && !uiState.isLoading) {
            SessionListEmptyState(modifier = Modifier.fillMaxSize())
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(uiState.sessions, key = { "${it.metadata.id}_${it.startTime}" }) { session ->
                    SessionRow(session = session, onClick = { onSessionClick(session) })
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun SessionRow(session: ExerciseSessionRecord, onClick: () -> Unit) {
    val zone = session.startZoneOffset?.let { ZoneId.ofOffset("UTC", it) } ?: ZoneId.systemDefault()
    ListItem(
        headlineContent = {
            Text(session.title?.takeIf { it.isNotBlank() } ?: exerciseTypeLabel(session.exerciseType))
        },
        supportingContent = {
            Text(
                "${exerciseTypeLabel(session.exerciseType)} · " +
                    "${rowDateFormatter.format(session.startTime.atZone(zone))} · " +
                    sourceAppLabel(session.metadata.dataOrigin.packageName),
            )
        },
        modifier = Modifier.clickable(onClick = onClick),
    )
}

@Composable
private fun SessionListEmptyState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.padding(24.dp), contentAlignment = Alignment.Center) {
        Text(
            text = "No sessions match your filters.",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
