package com.joseg.healthstats.data.repository

/**
 * Thin, directly-testable wrapper translating app filter state into Health Connect
 * `readRecords`/`aggregate` calls. Never writes to Health Connect.
 */
interface HealthConnectRepository {
    /** Distinct source-app package names present across the user's accessible exercise sessions. */
    suspend fun listSources(): List<String>

    /** Distinct exercise types present across the user's accessible exercise sessions. */
    suspend fun listExerciseTypes(): List<Int>
}
