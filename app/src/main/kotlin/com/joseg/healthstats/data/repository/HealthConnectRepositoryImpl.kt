package com.joseg.healthstats.data.repository

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.aggregate.AggregateMetric
import androidx.health.connect.client.aggregate.AggregationResult
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ElevationGainedRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.records.metadata.DataOrigin
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant

private fun <T : Any> AggregationResult.orNull(metric: AggregateMetric<T>): T? =
    if (contains(metric)) get(metric) else null

class HealthConnectRepositoryImpl(private val client: HealthConnectClient) : HealthConnectRepository {

    override suspend fun listSources(): List<String> =
        readAllExerciseSessions()
            .map { it.metadata.dataOrigin.packageName }
            .distinct()
            .sorted()

    override suspend fun listExerciseTypes(): List<Int> =
        readAllExerciseSessions()
            .map { it.exerciseType }
            .distinct()
            .sorted()

    override suspend fun querySessions(filter: SessionFilter): List<ExerciseSessionRecord> {
        val timeRangeFilter = TimeRangeFilter.between(
            filter.startDate ?: Instant.EPOCH,
            filter.endDate ?: Instant.now(),
        )
        val dataOriginFilter = filter.source?.let { setOf(DataOrigin(it)) } ?: emptySet()
        return readAllExerciseSessions(timeRangeFilter, dataOriginFilter)
            .filter { filter.exerciseType == null || it.exerciseType == filter.exerciseType }
            // TimeRangeFilter matches any session overlapping the range; the spec is stricter
            // ("start time falls within that range"), so re-check start time explicitly.
            .filter { filter.startDate == null || !it.startTime.isBefore(filter.startDate) }
            .filter { filter.endDate == null || it.startTime.isBefore(filter.endDate) }
            .sortedByDescending { it.startTime }
    }

    override suspend fun getSessionDetail(session: ExerciseSessionRecord): SessionDetail {
        val result = client.aggregate(
            AggregateRequest(
                metrics = setOf(
                    ExerciseSessionRecord.EXERCISE_DURATION_TOTAL,
                    DistanceRecord.DISTANCE_TOTAL,
                    TotalCaloriesBurnedRecord.ENERGY_TOTAL,
                    HeartRateRecord.BPM_AVG,
                    HeartRateRecord.BPM_MIN,
                    HeartRateRecord.BPM_MAX,
                    ElevationGainedRecord.ELEVATION_GAINED_TOTAL,
                    SpeedRecord.SPEED_AVG,
                    StepsRecord.COUNT_TOTAL,
                ),
                timeRangeFilter = TimeRangeFilter.between(session.startTime, session.endTime),
                dataOriginFilter = setOf(session.metadata.dataOrigin),
            ),
        )
        return SessionDetail(
            duration = result.orNull(ExerciseSessionRecord.EXERCISE_DURATION_TOTAL),
            distance = result.orNull(DistanceRecord.DISTANCE_TOTAL),
            calories = result.orNull(TotalCaloriesBurnedRecord.ENERGY_TOTAL),
            heartRateAvg = result.orNull(HeartRateRecord.BPM_AVG),
            heartRateMin = result.orNull(HeartRateRecord.BPM_MIN),
            heartRateMax = result.orNull(HeartRateRecord.BPM_MAX),
            elevationGained = result.orNull(ElevationGainedRecord.ELEVATION_GAINED_TOTAL),
            speedAvg = result.orNull(SpeedRecord.SPEED_AVG),
            steps = result.orNull(StepsRecord.COUNT_TOTAL),
        )
    }

    override suspend fun computeTrendSessions(
        exerciseType: Int,
        source: String,
        bucket: DistanceBucket,
        startDate: Instant?,
        endDate: Instant?,
    ): List<TrendSessionPoint> {
        val sessions = querySessions(
            SessionFilter(source = source, exerciseType = exerciseType, startDate = startDate, endDate = endDate),
        )
        return sessions.mapNotNull { session ->
            val detail = getSessionDetail(session)
            val distance = detail.distance ?: return@mapNotNull null
            val duration = detail.duration ?: return@mapNotNull null
            if (!bucket.contains(distance)) return@mapNotNull null
            TrendSessionPoint(date = session.startTime, duration = duration, distance = distance)
        }.sortedBy { it.date }
    }

    /** Pages through every accessible [ExerciseSessionRecord], unfiltered. */
    private suspend fun readAllExerciseSessions(
        timeRangeFilter: TimeRangeFilter = TimeRangeFilter.between(Instant.EPOCH, Instant.now()),
        dataOriginFilter: Set<DataOrigin> = emptySet(),
    ): List<ExerciseSessionRecord> {
        val allRecords = mutableListOf<ExerciseSessionRecord>()
        var pageToken: String? = null
        do {
            val response = client.readRecords(
                ReadRecordsRequest(
                    recordType = ExerciseSessionRecord::class,
                    timeRangeFilter = timeRangeFilter,
                    dataOriginFilter = dataOriginFilter,
                    pageToken = pageToken,
                ),
            )
            allRecords += response.records
            pageToken = response.pageToken?.takeIf { it.isNotEmpty() }
        } while (pageToken != null)
        return allRecords
    }
}
