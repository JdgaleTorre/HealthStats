package com.joseg.healthstats.fakes

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.HealthConnectFeatures
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.aggregate.AggregationResult
import androidx.health.connect.client.aggregate.AggregationResultGroupedByDuration
import androidx.health.connect.client.aggregate.AggregationResultGroupedByPeriod
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.request.AggregateGroupByDurationRequest
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ChangesTokenRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.response.ChangesResponse
import androidx.health.connect.client.response.InsertRecordsResponse
import androidx.health.connect.client.response.ReadRecordResponse
import androidx.health.connect.client.response.ReadRecordsResponse
import kotlin.reflect.KClass

/**
 * Minimal in-memory fake of [HealthConnectClient] for repository unit tests. Only
 * [readRecords] and [aggregate] are backed by test fixtures; every other member is a
 * read/write operation this app never calls, so it throws if exercised by mistake.
 */
class FakeHealthConnectClient(
    private val pageSize: Int = 1000,
) : HealthConnectClient {

    var exerciseSessions: List<ExerciseSessionRecord> = emptyList()

    /** AggregateRequest exposes no public properties to branch on, so tests queue responses in call order. */
    val aggregateResponses: ArrayDeque<AggregationResult> = ArrayDeque()

    var readRecordsCallCount: Int = 0
        private set
    var aggregateCallCount: Int = 0
        private set

    override val permissionController: PermissionController
        get() = throw NotImplementedError("not used by repository tests")

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T : Record> readRecords(
        request: ReadRecordsRequest<T>,
    ): ReadRecordsResponse<T> {
        readRecordsCallCount++
        require(request.recordType == ExerciseSessionRecord::class) {
            "FakeHealthConnectClient only fixtures ExerciseSessionRecord reads"
        }
        val matching = exerciseSessions.filter { session ->
            (request.dataOriginFilter.isEmpty() || session.metadata.dataOrigin in request.dataOriginFilter) &&
                !session.startTime.isBefore(request.timeRangeFilter.startTime ?: session.startTime) &&
                !session.endTime.isAfter(request.timeRangeFilter.endTime ?: session.endTime)
        }
        val effectivePageSize = minOf(pageSize, request.pageSize)
        val startIndex = request.pageToken?.toInt() ?: 0
        val endIndex = minOf(startIndex + effectivePageSize, matching.size)
        val page = if (startIndex < matching.size) matching.subList(startIndex, endIndex) else emptyList()
        val nextToken = if (endIndex < matching.size) endIndex.toString() else ""
        return ReadRecordsResponse(page as List<T>, nextToken)
    }

    override suspend fun aggregate(request: AggregateRequest): AggregationResult {
        aggregateCallCount++
        return aggregateResponses.removeFirstOrNull()
            ?: throw NotImplementedError("no fixture aggregate response queued")
    }

    override suspend fun insertRecords(records: List<Record>): InsertRecordsResponse =
        throw NotImplementedError("app is read-only")

    override suspend fun updateRecords(records: List<Record>) =
        throw NotImplementedError("app is read-only")

    override suspend fun deleteRecords(
        recordType: KClass<out Record>,
        recordIdsList: List<String>,
        clientRecordIdsList: List<String>,
    ) = throw NotImplementedError("app is read-only")

    override suspend fun deleteRecords(
        recordType: KClass<out Record>,
        timeRangeFilter: androidx.health.connect.client.time.TimeRangeFilter,
    ) = throw NotImplementedError("app is read-only")

    override suspend fun <T : Record> readRecord(
        recordType: KClass<T>,
        recordId: String,
    ): ReadRecordResponse<T> = throw NotImplementedError("not used by repository tests")

    override suspend fun aggregateGroupByDuration(
        request: AggregateGroupByDurationRequest,
    ): List<AggregationResultGroupedByDuration> = throw NotImplementedError("not used by repository tests")

    override suspend fun aggregateGroupByPeriod(
        request: AggregateGroupByPeriodRequest,
    ): List<AggregationResultGroupedByPeriod> = throw NotImplementedError("not used by repository tests")

    override suspend fun getChangesToken(request: ChangesTokenRequest): String =
        throw NotImplementedError("not used by repository tests")

    override suspend fun getChanges(changesToken: String): ChangesResponse =
        throw NotImplementedError("not used by repository tests")
}
