package com.joseg.healthstats.data.repository

import androidx.health.connect.client.units.Length
import kotlin.math.abs

/** A target distance ± tolerance, e.g. "5K ± 0.2 km". */
data class DistanceBucket(val target: Length, val tolerance: Length = Length.kilometers(0.2)) {
    fun contains(distance: Length): Boolean = abs(distance.inMeters - target.inMeters) <= tolerance.inMeters

    companion object {
        val FIVE_K = DistanceBucket(target = Length.kilometers(5.0))
    }
}
