package com.joseg.healthstats.fakes

import androidx.health.connect.client.records.metadata.DataOrigin
import androidx.health.connect.client.records.metadata.Device
import androidx.health.connect.client.records.metadata.Metadata
import java.time.Instant

/**
 * [Metadata]'s primary constructor is `internal` to the connect-client module, and none of its
 * public factory functions (`manualEntry`, `autoRecorded`, ...) accept a [DataOrigin] — the SDK
 * expects a record's origin to always be assigned by the platform, never fabricated by an app.
 * Test fixtures need a specific origin per source app, so this reflectively invokes that
 * constructor, which the JVM itself still exposes as public (only Kotlin's compiler enforces
 * the module boundary).
 */
fun metadataWithDataOrigin(dataOrigin: DataOrigin): Metadata {
    val constructor = Metadata::class.java.getDeclaredConstructor(
        Int::class.javaPrimitiveType,
        String::class.java,
        DataOrigin::class.java,
        Instant::class.java,
        String::class.java,
        Long::class.javaPrimitiveType,
        Device::class.java,
    )
    constructor.isAccessible = true
    return constructor.newInstance(
        Metadata.RECORDING_METHOD_UNKNOWN,
        "",
        dataOrigin,
        Instant.EPOCH,
        null,
        0L,
        null,
    )
}
