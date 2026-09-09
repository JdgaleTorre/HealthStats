package com.joseg.healthstats.fakes

import androidx.health.connect.client.records.metadata.DataOrigin
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter

/**
 * [AggregateRequest]'s `dataOriginFilter`/`timeRangeFilter` accessors are internal to the
 * connect-client module (named `...$connect_client_release`), so tests that need to assert what
 * scope a repository actually queried with must reach them via reflection, same rationale as
 * [metadataWithDataOrigin].
 */
val AggregateRequest.dataOriginFilterForTest: Set<DataOrigin>
    @Suppress("UNCHECKED_CAST")
    get() {
        val method = AggregateRequest::class.java.getDeclaredMethod("getDataOriginFilter\$connect_client_release")
        method.isAccessible = true
        return method.invoke(this) as Set<DataOrigin>
    }

val AggregateRequest.timeRangeFilterForTest: TimeRangeFilter
    get() {
        val method = AggregateRequest::class.java.getDeclaredMethod("getTimeRangeFilter\$connect_client_release")
        method.isAccessible = true
        return method.invoke(this) as TimeRangeFilter
    }
