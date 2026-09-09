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
}
