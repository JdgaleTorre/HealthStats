package com.joseg.healthstats.ui.onboarding

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OnboardingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setContent(uiState: OnboardingUiState, onInstall: () -> Unit = {}, onRequest: () -> Unit = {}) {
        composeTestRule.setContent {
            MaterialTheme {
                OnboardingScreen(
                    uiState = uiState,
                    onInstallOrUpdateClick = onInstall,
                    onRequestPermissionsClick = onRequest,
                )
            }
        }
    }

    @Test
    fun `unavailable state shows an explanatory message and no action`() {
        setContent(OnboardingUiState.HealthConnectUnavailable)
        composeTestRule.onNodeWithText("This device doesn't support Health Connect, so HealthStats can't run here.")
            .assertExists()
    }

    @Test
    fun `update-required state offers to install or update`() {
        var clicked = false
        setContent(OnboardingUiState.HealthConnectUpdateRequired, onInstall = { clicked = true })

        composeTestRule.onNodeWithText("Install or update Health Connect").performClick()
        assert(clicked)
    }

    @Test
    fun `permissions-blocking state offers to grant permissions`() {
        var clicked = false
        setContent(OnboardingUiState.PermissionsRequiredBlocking, onRequest = { clicked = true })

        composeTestRule.onNodeWithText("Grant permissions").performClick()
        assert(clicked)
    }
}
