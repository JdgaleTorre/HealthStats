package com.joseg.healthstats.data.repository

import androidx.health.connect.client.aggregate.AggregationResult
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ElevationGainedRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.records.metadata.DataOrigin
import com.joseg.healthstats.fakes.FakeHealthConnectClient
import com.joseg.healthstats.fakes.dataOriginFilterForTest
import com.joseg.healthstats.fakes.metadataWithDataOrigin
import com.joseg.healthstats.fakes.timeRangeFilterForTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
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

    @Test
    fun `getSessionDetail on a base-fields-only source omits richer fields`() = runTest {
        val fakeClient = FakeHealthConnectClient()
        val stravaSession = session("com.strava", ExerciseSessionRecord.EXERCISE_TYPE_RUNNING)
        fakeClient.aggregateResponses.add(
            AggregationResult(
                longValues = mapOf(
                    ExerciseSessionRecord.EXERCISE_DURATION_TOTAL.metricKey to 3600L,
                ),
                doubleValues = mapOf(
                    DistanceRecord.DISTANCE_TOTAL.metricKey to 5000.0,
                    TotalCaloriesBurnedRecord.ENERGY_TOTAL.metricKey to 400.0,
                ),
                dataOrigins = setOf(DataOrigin("com.strava")),
            ),
        )
        val repository = HealthConnectRepositoryImpl(fakeClient)

        val detail = repository.getSessionDetail(stravaSession)

        assertNotNull(detail.duration)
        assertNotNull(detail.distance)
        assertNotNull(detail.calories)
        assertNull(detail.heartRateAvg)
        assertNull(detail.heartRateMin)
        assertNull(detail.heartRateMax)
        assertNull(detail.elevationGained)
        assertNull(detail.speedAvg)
        assertNull(detail.steps)
    }

    @Test
    fun `getSessionDetail on a richer source includes every field`() = runTest {
        val fakeClient = FakeHealthConnectClient()
        val garminSession = session("com.garmin.android.apps.connectmobile", ExerciseSessionRecord.EXERCISE_TYPE_RUNNING)
        fakeClient.aggregateResponses.add(
            AggregationResult(
                longValues = mapOf(
                    ExerciseSessionRecord.EXERCISE_DURATION_TOTAL.metricKey to 3600L,
                    HeartRateRecord.BPM_AVG.metricKey to 145L,
                    HeartRateRecord.BPM_MIN.metricKey to 110L,
                    HeartRateRecord.BPM_MAX.metricKey to 172L,
                    StepsRecord.COUNT_TOTAL.metricKey to 8200L,
                ),
                doubleValues = mapOf(
                    DistanceRecord.DISTANCE_TOTAL.metricKey to 5000.0,
                    TotalCaloriesBurnedRecord.ENERGY_TOTAL.metricKey to 400.0,
                    ElevationGainedRecord.ELEVATION_GAINED_TOTAL.metricKey to 60.0,
                    SpeedRecord.SPEED_AVG.metricKey to 2.7,
                ),
                dataOrigins = setOf(DataOrigin("com.garmin.android.apps.connectmobile")),
            ),
        )
        val repository = HealthConnectRepositoryImpl(fakeClient)

        val detail = repository.getSessionDetail(garminSession)

        assertNotNull(detail.duration)
        assertNotNull(detail.distance)
        assertNotNull(detail.calories)
        assertNotNull(detail.heartRateAvg)
        assertNotNull(detail.heartRateMin)
        assertNotNull(detail.heartRateMax)
        assertNotNull(detail.elevationGained)
        assertNotNull(detail.speedAvg)
        assertNotNull(detail.steps)
        assertEquals(145L, detail.heartRateAvg)
        assertEquals(8200L, detail.steps)
    }

    @Test
    fun `getSessionDetail scopes the aggregate query to only the session's own source, even with an overlapping session from another source`() =
        runTest {
            val stravaSession = session(
                "com.strava",
                ExerciseSessionRecord.EXERCISE_TYPE_RUNNING,
                start = Instant.parse("2026-01-01T08:00:00Z"),
                end = Instant.parse("2026-01-01T09:00:00Z"),
            )
            // Overlaps the same time window from a different source.
            val garminSession = session(
                "com.garmin.android.apps.connectmobile",
                ExerciseSessionRecord.EXERCISE_TYPE_RUNNING,
                start = Instant.parse("2026-01-01T08:00:00Z"),
                end = Instant.parse("2026-01-01T09:00:00Z"),
            )
            val fakeClient = FakeHealthConnectClient().apply {
                exerciseSessions = listOf(stravaSession, garminSession)
                aggregateResponses.add(
                    AggregationResult(longValues = emptyMap(), doubleValues = emptyMap(), dataOrigins = emptySet()),
                )
            }
            val repository = HealthConnectRepositoryImpl(fakeClient)

            repository.getSessionDetail(stravaSession)

            val request = fakeClient.capturedAggregateRequests.single()
            assertEquals(setOf(DataOrigin("com.strava")), request.dataOriginFilterForTest)
            assertEquals(stravaSession.startTime, request.timeRangeFilterForTest.startTime)
            assertEquals(stravaSession.endTime, request.timeRangeFilterForTest.endTime)
        }

    private fun distanceDurationResult(distanceMeters: Double, durationSeconds: Long) = AggregationResult(
        longValues = mapOf(ExerciseSessionRecord.EXERCISE_DURATION_TOTAL.metricKey to durationSeconds),
        doubleValues = mapOf(DistanceRecord.DISTANCE_TOTAL.metricKey to distanceMeters),
        dataOrigins = emptySet(),
    )

    @Test
    fun `computeTrendSessions keeps only sessions whose aggregated distance falls in the bucket`() = runTest {
        // querySessions sorts newest-first, so aggregate fixtures must be queued in that order.
        val day1 = session(
            "com.strava",
            ExerciseSessionRecord.EXERCISE_TYPE_RUNNING,
            start = Instant.parse("2026-01-01T08:00:00Z"),
            end = Instant.parse("2026-01-01T08:25:00Z"),
        )
        val day2 = session(
            "com.strava",
            ExerciseSessionRecord.EXERCISE_TYPE_RUNNING,
            start = Instant.parse("2026-01-02T08:00:00Z"),
            end = Instant.parse("2026-01-02T08:40:00Z"),
        )
        val day3 = session(
            "com.strava",
            ExerciseSessionRecord.EXERCISE_TYPE_RUNNING,
            start = Instant.parse("2026-01-03T08:00:00Z"),
            end = Instant.parse("2026-01-03T08:24:00Z"),
        )
        val fakeClient = FakeHealthConnectClient().apply {
            exerciseSessions = listOf(day1, day2, day3)
            // Queried newest-first: day3, day2, day1.
            aggregateResponses.add(distanceDurationResult(distanceMeters = 4900.0, durationSeconds = 1440))
            aggregateResponses.add(distanceDurationResult(distanceMeters = 8000.0, durationSeconds = 2400))
            aggregateResponses.add(distanceDurationResult(distanceMeters = 5000.0, durationSeconds = 1500))
        }
        val repository = HealthConnectRepositoryImpl(fakeClient)

        val trend = repository.computeTrendSessions(
            exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_RUNNING,
            source = "com.strava",
            bucket = DistanceBucket.FIVE_K,
        )

        assertEquals(listOf(day1.startTime, day3.startTime), trend.map { it.date })
    }

    @Test
    fun `computeTrendSessions honors a custom bucket`() = runTest {
        val session10k = session("com.strava", ExerciseSessionRecord.EXERCISE_TYPE_RUNNING)
        val fakeClient = FakeHealthConnectClient().apply {
            exerciseSessions = listOf(session10k)
            aggregateResponses.add(distanceDurationResult(distanceMeters = 10100.0, durationSeconds = 3000))
        }
        val repository = HealthConnectRepositoryImpl(fakeClient)

        val trend = repository.computeTrendSessions(
            exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_RUNNING,
            source = "com.strava",
            bucket = DistanceBucket(
                target = androidx.health.connect.client.units.Length.kilometers(10.0),
                tolerance = androidx.health.connect.client.units.Length.kilometers(0.2),
            ),
        )

        assertEquals(1, trend.size)
    }

    @Test
    fun `computeTrendSessions switching source recomputes using only that source's sessions`() = runTest {
        val stravaSession = session("com.strava", ExerciseSessionRecord.EXERCISE_TYPE_RUNNING)
        val garminSession = session("com.garmin.android.apps.connectmobile", ExerciseSessionRecord.EXERCISE_TYPE_RUNNING)
        val fakeClient = FakeHealthConnectClient().apply {
            exerciseSessions = listOf(stravaSession, garminSession)
        }
        val repository = HealthConnectRepositoryImpl(fakeClient)

        fakeClient.aggregateResponses.add(distanceDurationResult(distanceMeters = 5000.0, durationSeconds = 1500))
        val stravaTrend = repository.computeTrendSessions(
            exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_RUNNING,
            source = "com.strava",
            bucket = DistanceBucket.FIVE_K,
        )
        assertEquals(1, stravaTrend.size)
        assertEquals(
            setOf("com.strava"),
            fakeClient.capturedAggregateRequests.flatMap { it.dataOriginFilterForTest }.map { it.packageName }.toSet(),
        )

        fakeClient.capturedAggregateRequests.clear()
        fakeClient.aggregateResponses.add(distanceDurationResult(distanceMeters = 5000.0, durationSeconds = 1600))
        val garminTrend = repository.computeTrendSessions(
            exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_RUNNING,
            source = "com.garmin.android.apps.connectmobile",
            bucket = DistanceBucket.FIVE_K,
        )
        assertEquals(1, garminTrend.size)
        assertEquals(
            setOf("com.garmin.android.apps.connectmobile"),
            fakeClient.capturedAggregateRequests.flatMap { it.dataOriginFilterForTest }.map { it.packageName }.toSet(),
        )
    }
}
