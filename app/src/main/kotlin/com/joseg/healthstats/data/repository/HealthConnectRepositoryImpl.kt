package com.joseg.healthstats.data.repository

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.metadata.DataOrigin
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant

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
