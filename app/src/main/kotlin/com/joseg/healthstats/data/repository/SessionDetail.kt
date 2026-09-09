package com.joseg.healthstats.data.repository

import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Velocity
import java.time.Duration

/**
 * Per-session aggregates. Every field is nullable: a field is null exactly when the session's
 * source never contributed that record type, never as a stand-in zero.
 */
data class SessionDetail(
    val duration: Duration?,
    val distance: Length?,
    val calories: Energy?,
    val heartRateAvg: Long?,
    val heartRateMin: Long?,
    val heartRateMax: Long?,
    val elevationGained: Length?,
    val speedAvg: Velocity?,
    val steps: Long?,
)
