package com.joseg.healthstats.data.repository

import androidx.health.connect.client.units.Length
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Duration
import java.time.Instant

class TrendDataTest {

    private val point = TrendSessionPoint(
        date = Instant.parse("2026-01-01T08:00:00Z"),
        duration = Duration.ofMinutes(25), // 1500s
        distance = Length.kilometers(5.0),
    )

    @Test
    fun `finish time metric is the whole-session duration in seconds`() {
        assertEquals(1500.0, point.metricValueSeconds(TrendMetric.FINISH_TIME), 0.001)
    }

    @Test
    fun `pace metric is duration divided by distance`() {
        // 1500s over 5km = 300 s/km
        assertEquals(300.0, point.metricValueSeconds(TrendMetric.PACE), 0.001)
    }
}
