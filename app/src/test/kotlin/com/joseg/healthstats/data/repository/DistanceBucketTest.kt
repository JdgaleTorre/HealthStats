package com.joseg.healthstats.data.repository

import androidx.health.connect.client.units.Length
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DistanceBucketTest {

    @Test
    fun `default 5K preset accepts distances within the default 1km tolerance`() {
        val bucket = DistanceBucket.FIVE_K
        assertTrue(bucket.contains(Length.kilometers(5.0)))
        assertTrue(bucket.contains(Length.kilometers(4.0)))
        assertTrue(bucket.contains(Length.kilometers(6.0)))
    }

    @Test
    fun `default 5K preset rejects distances outside the tolerance`() {
        val bucket = DistanceBucket.FIVE_K
        assertFalse(bucket.contains(Length.kilometers(3.9)))
        assertFalse(bucket.contains(Length.kilometers(6.1)))
        assertFalse(bucket.contains(Length.kilometers(10.0)))
    }

    @Test
    fun `custom bucket uses its own target and tolerance`() {
        val bucket = DistanceBucket(target = Length.kilometers(10.0), tolerance = Length.kilometers(0.5))
        assertTrue(bucket.contains(Length.kilometers(9.6)))
        assertTrue(bucket.contains(Length.kilometers(10.5)))
        assertFalse(bucket.contains(Length.kilometers(9.4)))
        assertFalse(bucket.contains(Length.kilometers(10.6)))
        assertFalse(bucket.contains(Length.kilometers(5.0)))
    }
}
