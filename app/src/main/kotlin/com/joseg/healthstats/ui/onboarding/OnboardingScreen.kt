package com.joseg.healthstats.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.joseg.healthstats.ui.theme.Spacing

@Composable
fun OnboardingScreen(
    uiState: OnboardingUiState,
    onInstallOrUpdateClick: () -> Unit,
    onRequestPermissionsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize().padding(Spacing.xl), contentAlignment = Alignment.Center) {
        when (uiState) {
            is OnboardingUiState.CheckingAvailability,
            is OnboardingUiState.AwaitingPermissions,
            is OnboardingUiState.Ready,
            -> CircularProgressIndicator()

            is OnboardingUiState.HealthConnectUnavailable -> MessageWithAction(
                icon = Icons.Filled.CloudOff,
                headline = "Health Connect isn't available",
                message = "This device doesn't support Health Connect, so HealthStats can't run here.",
            )

            is OnboardingUiState.HealthConnectUpdateRequired -> MessageWithAction(
                icon = Icons.Filled.CloudOff,
                headline = "Almost there",
                message = "HealthStats needs Health Connect installed or updated before it can read your activity data.",
                actionLabel = "Install or update Health Connect",
                onAction = onInstallOrUpdateClick,
            )

            is OnboardingUiState.PermissionsRequiredBlocking -> MessageWithAction(
                icon = Icons.Filled.Lock,
                headline = "Welcome to HealthStats",
                message = "HealthStats needs at least exercise session access to show your activity history.",
                actionLabel = "Grant permissions",
                onAction = onRequestPermissionsClick,
            )
        }
    }
}

@Composable
private fun MessageWithAction(
    icon: ImageVector,
    headline: String,
    message: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp),
            )
        }
        Text(
            text = headline,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = Spacing.sm),
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        if (actionLabel != null && onAction != null) {
            Button(
                onClick = onAction,
                modifier = Modifier.padding(top = Spacing.sm),
            ) { Text(actionLabel) }
        }
    }
}
