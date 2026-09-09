package com.joseg.healthstats.data.healthconnect

/** Mirrors the branches [androidx.health.connect.client.HealthConnectClient.getSdkStatus] can report. */
sealed interface HealthConnectAvailability {
    data object Available : HealthConnectAvailability
    data object UpdateRequired : HealthConnectAvailability
    data object Unavailable : HealthConnectAvailability
}
