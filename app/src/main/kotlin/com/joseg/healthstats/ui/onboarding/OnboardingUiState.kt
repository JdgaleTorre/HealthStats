package com.joseg.healthstats.ui.onboarding

/** App-level state driven by Health Connect availability and permission grants. */
sealed interface OnboardingUiState {
    /** Availability hasn't been checked yet. */
    data object CheckingAvailability : OnboardingUiState

    /** Health Connect cannot run on this device. */
    data object HealthConnectUnavailable : OnboardingUiState

    /** Health Connect is not installed, or needs an update, before the app can proceed. */
    data object HealthConnectUpdateRequired : OnboardingUiState

    /** Health Connect is available; permissions haven't been requested/checked yet. */
    data object AwaitingPermissions : OnboardingUiState

    /**
     * Health Connect is available and the user has granted at least exercise-session read
     * access. [grantedPermissions] may be a subset of [com.joseg.healthstats.data.healthconnect.HealthConnectManager.REQUIRED_PERMISSIONS];
     * ungranted record types are simply omitted from the UI, not treated as an error.
     */
    data class Ready(val grantedPermissions: Set<String>) : OnboardingUiState

    /** Health Connect is available but the user denied every permission, including exercise session. */
    data object PermissionsRequiredBlocking : OnboardingUiState
}
