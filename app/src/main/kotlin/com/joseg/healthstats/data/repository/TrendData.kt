package com.joseg.healthstats.data.repository

import androidx.health.connect.client.units.Length
import java.time.Duration
import java.time.Instant

/** One session's whole-session distance/duration, for trend charting. */
data class TrendSessionPoint(val date: Instant, val duration: Duration, val distance: Length)

enum class TrendMetric { FINISH_TIME, PACE }

/** Seconds for [TrendMetric.FINISH_TIME], seconds-per-kilometer for [TrendMetric.PACE]. */
fun TrendSessionPoint.metricValueSeconds(metric: TrendMetric): Double = when (metric) {
    TrendMetric.FINISH_TIME -> duration.toMillis() / 1000.0
    TrendMetric.PACE -> (duration.toMillis() / 1000.0) / distance.inKilometers
}
