package com.joseg.healthstats.ui.sessiondetail

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.metadata.DataOrigin
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Velocity
import com.joseg.healthstats.data.repository.SessionDetail
import com.joseg.healthstats.fakes.metadataWithDataOrigin
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.Duration
import java.time.Instant

@RunWith(RobolectricTestRunner::class)
class SessionDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val session = ExerciseSessionRecord(
        startTime = Instant.parse("2026-01-01T08:00:00Z"),
        startZoneOffset = null,
        endTime = Instant.parse("2026-01-01T09:00:00Z"),
        endZoneOffset = null,
        exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_RUNNING,
        title = "Morning Run",
        metadata = metadataWithDataOrigin(DataOrigin("com.strava")),
    )

    @Test
    fun `base-fields-only source shows core stats and omits richer ones`() {
        val detail = SessionDetail(
            duration = Duration.ofHours(1),
            distance = Length.kilometers(5.0),
            calories = Energy.kilocalories(400.0),
            heartRateAvg = null,
            heartRateMin = null,
            heartRateMax = null,
            elevationGained = null,
            speedAvg = null,
            steps = null,
        )

        composeTestRule.setContent {
            MaterialTheme {
                SessionDetailScreen(SessionDetailUiState(session = session, detail = detail, isLoading = false))
            }
        }

        composeTestRule.onNodeWithText("Duration").assertExists()
        composeTestRule.onNodeWithText("Distance").assertExists()
        composeTestRule.onNodeWithText("Calories").assertExists()
        composeTestRule.onNodeWithText("Heart rate").assertDoesNotExist()
        composeTestRule.onNodeWithText("Elevation gained").assertDoesNotExist()
        composeTestRule.onNodeWithText("Avg speed").assertDoesNotExist()
        composeTestRule.onNodeWithText("Steps").assertDoesNotExist()
    }

    @Test
    fun `richer source shows every stat`() {
        val detail = SessionDetail(
            duration = Duration.ofHours(1),
            distance = Length.kilometers(5.0),
            calories = Energy.kilocalories(400.0),
            heartRateAvg = 145L,
            heartRateMin = 110L,
            heartRateMax = 172L,
            elevationGained = Length.meters(60.0),
            speedAvg = Velocity.kilometersPerHour(9.5),
            steps = 8200L,
        )

        composeTestRule.setContent {
            MaterialTheme {
                SessionDetailScreen(SessionDetailUiState(session = session, detail = detail, isLoading = false))
            }
        }

        composeTestRule.onNodeWithText("Duration").assertExists()
        composeTestRule.onNodeWithText("Distance").assertExists()
        composeTestRule.onNodeWithText("Calories").assertExists()
        composeTestRule.onNodeWithText("Heart rate").assertExists()
        composeTestRule.onNodeWithText("Elevation gained").assertExists()
        composeTestRule.onNodeWithText("Avg speed").assertExists()
        composeTestRule.onNodeWithText("Steps").assertExists()
    }
}
