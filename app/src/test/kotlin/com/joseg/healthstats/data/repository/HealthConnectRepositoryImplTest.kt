package com.joseg.healthstats.data.repository

import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.metadata.DataOrigin
import com.joseg.healthstats.fakes.FakeHealthConnectClient
import com.joseg.healthstats.fakes.metadataWithDataOrigin
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class HealthConnectRepositoryImplTest {

    private fun session(
        source: String,
        exerciseType: Int,
        start: Instant = Instant.parse("2026-01-01T08:00:00Z"),
        end: Instant = Instant.parse("2026-01-01T09:00:00Z"),
    ) = ExerciseSessionRecord(
        startTime = start,
        startZoneOffset = null,
        endTime = end,
        endZoneOffset = null,
        exerciseType = exerciseType,
        metadata = metadataWithDataOrigin(DataOrigin(source)),
    )

    @Test
    fun `listSources returns distinct sources across all sessions`() = runTest {
        val fakeClient = FakeHealthConnectClient().apply {
            exerciseSessions = listOf(
                session("com.strava", ExerciseSessionRecord.EXERCISE_TYPE_RUNNING),
                session("com.garmin.android.apps.connectmobile", ExerciseSessionRecord.EXERCISE_TYPE_BIKING),
                session("com.strava", ExerciseSessionRecord.EXERCISE_TYPE_HIKING),
            )
        }
        val repository = HealthConnectRepositoryImpl(fakeClient)

        assertEquals(
            listOf("com.garmin.android.apps.connectmobile", "com.strava"),
            repository.listSources(),
        )
    }

    @Test
    fun `listSources returns empty list when no data is accessible`() = runTest {
        val repository = HealthConnectRepositoryImpl(FakeHealthConnectClient())
        assertEquals(emptyList<String>(), repository.listSources())
    }

    @Test
    fun `listSources pages through multiple result pages`() = runTest {
        val fakeClient = FakeHealthConnectClient(pageSize = 2).apply {
            exerciseSessions = (1..5).map { session("com.source$it", ExerciseSessionRecord.EXERCISE_TYPE_RUNNING) }
        }
        val repository = HealthConnectRepositoryImpl(fakeClient)

        assertEquals(5, repository.listSources().size)
    }

    @Test
    fun `listExerciseTypes returns distinct types across all sessions`() = runTest {
        val fakeClient = FakeHealthConnectClient().apply {
            exerciseSessions = listOf(
                session("com.strava", ExerciseSessionRecord.EXERCISE_TYPE_RUNNING),
                session("com.strava", ExerciseSessionRecord.EXERCISE_TYPE_RUNNING),
                session("com.strava", ExerciseSessionRecord.EXERCISE_TYPE_BIKING),
            )
        }
        val repository = HealthConnectRepositoryImpl(fakeClient)

        assertEquals(
            listOf(ExerciseSessionRecord.EXERCISE_TYPE_BIKING, ExerciseSessionRecord.EXERCISE_TYPE_RUNNING)
                .sorted(),
            repository.listExerciseTypes(),
        )
    }

    private fun sessionRepositoryWithFixtures(): HealthConnectRepositoryImpl {
        val fakeClient = FakeHealthConnectClient().apply {
            exerciseSessions = listOf(
                session(
                    "com.strava",
                    ExerciseSessionRecord.EXERCISE_TYPE_RUNNING,
                    start = Instant.parse("2026-01-01T08:00:00Z"),
                    end = Instant.parse("2026-01-01T09:00:00Z"),
                ),
                session(
                    "com.strava",
                    ExerciseSessionRecord.EXERCISE_TYPE_BIKING,
                    start = Instant.parse("2026-02-01T08:00:00Z"),
                    end = Instant.parse("2026-02-01T10:00:00Z"),
                ),
                session(
                    "com.garmin.android.apps.connectmobile",
                    ExerciseSessionRecord.EXERCISE_TYPE_RUNNING,
                    start = Instant.parse("2026-03-01T08:00:00Z"),
                    end = Instant.parse("2026-03-01T09:00:00Z"),
                ),
            )
        }
        return HealthConnectRepositoryImpl(fakeClient)
    }

    @Test
    fun `querySessions with no filter returns every session, newest first`() = runTest {
        val repository = sessionRepositoryWithFixtures()
        val result = repository.querySessions(SessionFilter())
        assertEquals(3, result.size)
        assertEquals(Instant.parse("2026-03-01T08:00:00Z"), result.first().startTime)
    }

    @Test
    fun `querySessions filters by source`() = runTest {
        val repository = sessionRepositoryWithFixtures()
        val result = repository.querySessions(SessionFilter(source = "com.strava"))
        assertEquals(2, result.size)
        assertEquals(setOf("com.strava"), result.map { it.metadata.dataOrigin.packageName }.toSet())
    }

    @Test
    fun `querySessions filters by exercise type`() = runTest {
        val repository = sessionRepositoryWithFixtures()
        val result = repository.querySessions(
            SessionFilter(exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_RUNNING),
        )
        assertEquals(2, result.size)
        assertEquals(
            setOf(ExerciseSessionRecord.EXERCISE_TYPE_RUNNING),
            result.map { it.exerciseType }.toSet(),
        )
    }

    @Test
    fun `querySessions filters by date range using start time`() = runTest {
        val repository = sessionRepositoryWithFixtures()
        val result = repository.querySessions(
            SessionFilter(
                startDate = Instant.parse("2026-01-15T00:00:00Z"),
                endDate = Instant.parse("2026-02-15T00:00:00Z"),
            ),
        )
        assertEquals(1, result.size)
        assertEquals(Instant.parse("2026-02-01T08:00:00Z"), result.single().startTime)
    }

    @Test
    fun `querySessions combines source, type, and date range with AND`() = runTest {
        val repository = sessionRepositoryWithFixtures()
        val result = repository.querySessions(
            SessionFilter(
                source = "com.strava",
                exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_RUNNING,
                startDate = Instant.parse("2025-12-01T00:00:00Z"),
                endDate = Instant.parse("2026-06-01T00:00:00Z"),
            ),
        )
        assertEquals(1, result.size)
        val onlyMatch = result.single()
        assertEquals("com.strava", onlyMatch.metadata.dataOrigin.packageName)
        assertEquals(ExerciseSessionRecord.EXERCISE_TYPE_RUNNING, onlyMatch.exerciseType)
    }

    @Test
    fun `querySessions returns empty list when nothing matches`() = runTest {
        val repository = sessionRepositoryWithFixtures()
        val result = repository.querySessions(SessionFilter(source = "com.nonexistent.app"))
        assertEquals(emptyList<ExerciseSessionRecord>(), result)
    }
}
