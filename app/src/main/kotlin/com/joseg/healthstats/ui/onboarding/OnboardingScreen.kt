package com.joseg.healthstats.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingScreen(
    uiState: OnboardingUiState,
    onInstallOrUpdateClick: () -> Unit,
    onRequestPermissionsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        when (uiState) {
            is OnboardingUiState.CheckingAvailability,
            is OnboardingUiState.AwaitingPermissions,
            is OnboardingUiState.Ready,
            -> CircularProgressIndicator()

            is OnboardingUiState.HealthConnectUnavailable -> MessageWithAction(
                message = "This device doesn't support Health Connect, so HealthStats can't run here.",
            )

            is OnboardingUiState.HealthConnectUpdateRequired -> MessageWithAction(
                message = "HealthStats needs Health Connect installed or updated before it can read your activity data.",
                actionLabel = "Install or update Health Connect",
                onAction = onInstallOrUpdateClick,
            )

            is OnboardingUiState.PermissionsRequiredBlocking -> MessageWithAction(
                message = "HealthStats needs at least exercise session access to show your activity history.",
                actionLabel = "Grant permissions",
                onAction = onRequestPermissionsClick,
            )
        }
    }
}

@Composable
private fun MessageWithAction(
    message: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(text = message, style = MaterialTheme.typography.bodyLarge)
        if (actionLabel != null && onAction != null) {
            Button(onClick = onAction) { Text(actionLabel) }
        }
    }
}
