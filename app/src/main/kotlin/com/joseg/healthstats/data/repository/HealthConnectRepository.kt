package com.joseg.healthstats.data.repository

import androidx.health.connect.client.records.ExerciseSessionRecord
import java.time.Instant

/** Source/type/date filters for the session list. Filters combine with AND, not OR. */
data class SessionFilter(
    val source: String? = null,
    val exerciseType: Int? = null,
    val startDate: Instant? = null,
    val endDate: Instant? = null,
)

/**
 * Thin, directly-testable wrapper translating app filter state into Health Connect
 * `readRecords`/`aggregate` calls. Never writes to Health Connect.
 */
interface HealthConnectRepository {
    /** Distinct source-app package names present across the user's accessible exercise sessions. */
    suspend fun listSources(): List<String>

    /** Distinct exercise types present across the user's accessible exercise sessions. */
    suspend fun listExerciseTypes(): List<Int>

    /** Exercise sessions matching every non-null field of [filter], newest first. */
    suspend fun querySessions(filter: SessionFilter): List<ExerciseSessionRecord>

    /**
     * Aggregates scoped to [session]'s own start/end time window and its own source. Never
     * combines data from a different source into the result.
     */
    suspend fun getSessionDetail(session: ExerciseSessionRecord): SessionDetail

    /**
     * Whole-session distance/duration for every session of [exerciseType] from [source] whose
     * aggregated total distance falls within [bucket], restricted to [startDate]..[endDate].
     * Never depends on per-kilometer split or lap data.
     */
    suspend fun computeTrendSessions(
        exerciseType: Int,
        source: String,
        bucket: DistanceBucket,
        startDate: Instant? = null,
        endDate: Instant? = null,
    ): List<TrendSessionPoint>
}
