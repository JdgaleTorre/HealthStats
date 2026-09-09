package com.joseg.healthstats.data.healthconnect

import androidx.health.connect.client.HealthConnectClient
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class HealthConnectManagerTest {

    private fun managerWithStatus(status: Int): HealthConnectManager {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        return object : HealthConnectManager(context) {
            override fun rawSdkStatus(): Int = status
        }
    }

    @Test
    fun `available status maps to Available`() {
        val manager = managerWithStatus(HealthConnectClient.SDK_AVAILABLE)
        assertEquals(HealthConnectAvailability.Available, manager.getSdkStatus())
    }

    @Test
    fun `update-required status maps to UpdateRequired`() {
        val manager = managerWithStatus(HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED)
        assertEquals(HealthConnectAvailability.UpdateRequired, manager.getSdkStatus())
    }

    @Test
    fun `unavailable status maps to Unavailable`() {
        val manager = managerWithStatus(HealthConnectClient.SDK_UNAVAILABLE)
        assertEquals(HealthConnectAvailability.Unavailable, manager.getSdkStatus())
    }
}
