package com.joseg.healthstats.ui.onboarding

import androidx.test.core.app.ApplicationProvider
import com.joseg.healthstats.data.healthconnect.HealthConnectManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class OnboardingViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun fakeManager(
        rawStatus: Int,
        granted: Set<String> = emptySet(),
    ): HealthConnectManager {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        return object : HealthConnectManager(context) {
            override fun rawSdkStatus(): Int = rawStatus
            override suspend fun grantedPermissions(): Set<String> = granted
        }
    }

    @Test
    fun `unavailable status surfaces HealthConnectUnavailable`() = runTest {
        val viewModel = OnboardingViewModel(
            fakeManager(rawStatus = androidx.health.connect.client.HealthConnectClient.SDK_UNAVAILABLE),
        )
        assertEquals(OnboardingUiState.HealthConnectUnavailable, viewModel.uiState.value)
    }

    @Test
    fun `update-required status surfaces HealthConnectUpdateRequired`() = runTest {
        val viewModel = OnboardingViewModel(
            fakeManager(
                rawStatus = androidx.health.connect.client.HealthConnectClient
                    .SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED,
            ),
        )
        assertEquals(OnboardingUiState.HealthConnectUpdateRequired, viewModel.uiState.value)
    }

    @Test
    fun `available status moves past the availability check`() = runTest {
        val viewModel = OnboardingViewModel(
            fakeManager(rawStatus = androidx.health.connect.client.HealthConnectClient.SDK_AVAILABLE),
        )
        // Before the permission lookup coroutine resolves, state has already left CheckingAvailability.
        assert(viewModel.uiState.value !is OnboardingUiState.CheckingAvailability)
        assert(viewModel.uiState.value !is OnboardingUiState.HealthConnectUnavailable)
        assert(viewModel.uiState.value !is OnboardingUiState.HealthConnectUpdateRequired)
    }

    @Test
    fun `all permissions granted drives Ready with the full set`() = runTest {
        val viewModel = OnboardingViewModel(
            fakeManager(
                rawStatus = androidx.health.connect.client.HealthConnectClient.SDK_AVAILABLE,
                granted = HealthConnectManager.REQUIRED_PERMISSIONS,
            ),
        )
        advanceUntilIdle()
        assertEquals(
            OnboardingUiState.Ready(HealthConnectManager.REQUIRED_PERMISSIONS),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `partial permissions including exercise session drive Ready with the granted subset`() = runTest {
        val partial = setOf(
            HealthConnectManager.EXERCISE_SESSION_READ_PERMISSION,
            androidx.health.connect.client.permission.HealthPermission.getReadPermission(
                androidx.health.connect.client.records.DistanceRecord::class,
            ),
        )
        val viewModel = OnboardingViewModel(
            fakeManager(
                rawStatus = androidx.health.connect.client.HealthConnectClient.SDK_AVAILABLE,
                granted = partial,
            ),
        )
        advanceUntilIdle()
        assertEquals(OnboardingUiState.Ready(partial), viewModel.uiState.value)
    }

    @Test
    fun `all permissions denied drives PermissionsRequiredBlocking`() = runTest {
        val viewModel = OnboardingViewModel(
            fakeManager(
                rawStatus = androidx.health.connect.client.HealthConnectClient.SDK_AVAILABLE,
                granted = emptySet(),
            ),
        )
        advanceUntilIdle()
        assertEquals(OnboardingUiState.PermissionsRequiredBlocking, viewModel.uiState.value)
    }

    @Test
    fun `permissions granted without exercise session still blocks`() = runTest {
        val viewModel = OnboardingViewModel(
            fakeManager(
                rawStatus = androidx.health.connect.client.HealthConnectClient.SDK_AVAILABLE,
                granted = setOf(
                    androidx.health.connect.client.permission.HealthPermission.getReadPermission(
                        androidx.health.connect.client.records.DistanceRecord::class,
                    ),
                ),
            ),
        )
        advanceUntilIdle()
        assertEquals(OnboardingUiState.PermissionsRequiredBlocking, viewModel.uiState.value)
    }
}
