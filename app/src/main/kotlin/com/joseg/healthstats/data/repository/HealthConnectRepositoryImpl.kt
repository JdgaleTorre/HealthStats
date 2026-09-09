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
