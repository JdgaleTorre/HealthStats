package com.joseg.healthstats.data.healthconnect

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** The app is read-only: it must never request a Health Connect write permission. */
class NoWritePermissionsTest {

    @Test
    fun `required permission set is non-empty and read-only`() {
        assertTrue(HealthConnectManager.REQUIRED_PERMISSIONS.isNotEmpty())
        HealthConnectManager.REQUIRED_PERMISSIONS.forEach { permission ->
            assertTrue(
                "$permission is not a read permission",
                permission.startsWith("android.permission.health.READ_"),
            )
            assertFalse(
                "$permission looks like a write permission",
                permission.contains("WRITE", ignoreCase = true),
            )
        }
    }

    @Test
    fun `required permissions cover exactly the seven record types the app reads`() {
        assertEquals(7, HealthConnectManager.REQUIRED_PERMISSIONS.size)
    }
}
