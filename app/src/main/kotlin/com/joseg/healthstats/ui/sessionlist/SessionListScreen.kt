package com.joseg.healthstats.ui.sessionlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.joseg.healthstats.ui.common.EmptyState
import com.joseg.healthstats.ui.common.ExerciseTypeBadge
import com.joseg.healthstats.ui.common.SectionHeader
import com.joseg.healthstats.ui.common.exerciseTypeLabel
import com.joseg.healthstats.ui.common.sourceAppLabel
import com.joseg.healthstats.ui.theme.Spacing
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val rowDateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy · h:mm a")

const val SessionListTestTag = "sessionList"

@Composable
fun SessionListScreen(
    uiState: SessionListUiState,
    onSessionClick: (ExerciseSessionRecord) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        SectionHeader(title = "Sessions")
        if (uiState.sessions.isEmpty() && !uiState.isLoading) {
            SessionListEmptyState(modifier = Modifier.fillMaxSize())
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().testTag(SessionListTestTag),
                contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                items(uiState.sessions, key = { "${it.metadata.id}_${it.startTime}" }) { session ->
                    SessionCard(session = session, onClick = { onSessionClick(session) })
                }
            }
        }
    }
}

@Composable
private fun SessionCard(session: ExerciseSessionRecord, onClick: () -> Unit) {
    val zone = session.startZoneOffset?.let { ZoneId.ofOffset("UTC", it) } ?: ZoneId.systemDefault()
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            ExerciseTypeBadge(exerciseType = session.exerciseType)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.title?.takeIf { it.isNotBlank() } ?: exerciseTypeLabel(session.exerciseType),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "${exerciseTypeLabel(session.exerciseType)} · " +
                        "${rowDateFormatter.format(session.startTime.atZone(zone))} · " +
                        sourceAppLabel(session.metadata.dataOrigin.packageName),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = Spacing.xs).size(20.dp),
            )
        }
    }
}

@Composable
private fun SessionListEmptyState(modifier: Modifier = Modifier) {
    EmptyState(
        icon = Icons.Filled.EventBusy,
        title = "No sessions yet",
        message = "No sessions match your filters.",
        modifier = modifier,
    )
}
