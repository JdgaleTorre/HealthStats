package com.joseg.healthstats.ui.trends

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.units.Length
import com.joseg.healthstats.data.repository.TrendMetric
import com.joseg.healthstats.data.repository.TrendSessionPoint
import com.joseg.healthstats.ui.theme.HealthStatsTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.Duration
import java.time.Instant

@RunWith(RobolectricTestRunner::class)
class TrendsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val fixturePoints = listOf(
        TrendSessionPoint(Instant.parse("2026-01-01T08:00:00Z"), Duration.ofMinutes(26), Length.kilometers(5.0)),
        TrendSessionPoint(Instant.parse("2026-02-01T08:00:00Z"), Duration.ofMinutes(25), Length.kilometers(5.0)),
        TrendSessionPoint(Instant.parse("2026-03-01T08:00:00Z"), Duration.ofMinutes(24), Length.kilometers(5.0)),
    )

    @Test
    fun `renders the chart and filter controls against fixture data`() {
        val uiState = TrendsUiState(
            points = fixturePoints,
            availableSources = listOf("com.strava"),
            availableExerciseTypes = listOf(ExerciseSessionRecord.EXERCISE_TYPE_RUNNING),
            source = "com.strava",
            exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_RUNNING,
            metric = TrendMetric.FINISH_TIME,
            isLoading = false,
        )

        composeTestRule.setContent {
            HealthStatsTheme(darkTheme = false, dynamicColor = false) {
                TrendsScreen(
                    uiState = uiState,
                    onSourceSelected = {},
                    onExerciseTypeSelected = {},
                    onMetricSelected = {},
                )
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Strava").assertExists()
        composeTestRule.onNodeWithText("Running").assertExists()
        composeTestRule.onNodeWithText("Finish time").assertExists()
        composeTestRule.onNodeWithText("No sessions match this bucket yet.").assertDoesNotExist()
    }

    @Test
    fun `empty state shows when no sessions match the bucket`() {
        val uiState = TrendsUiState(
            points = emptyList(),
            availableSources = listOf("com.strava"),
            availableExerciseTypes = listOf(ExerciseSessionRecord.EXERCISE_TYPE_RUNNING),
            source = "com.strava",
            exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_RUNNING,
            isLoading = false,
        )

        composeTestRule.setContent {
            HealthStatsTheme(darkTheme = false, dynamicColor = false) {
                TrendsScreen(
                    uiState = uiState,
                    onSourceSelected = {},
                    onExerciseTypeSelected = {},
                    onMetricSelected = {},
                )
            }
        }

        composeTestRule.onNodeWithText("No sessions match this bucket yet.").assertExists()
    }

    @Test
    fun `initial loading state with no points yet does not crash the chart`() {
        // This is TrendsUiState()'s actual default: isLoading = true, points = emptyList().
        // Vico's chart model throws IllegalArgumentException on an empty series, so the
        // screen must never compose the chart in this state.
        val uiState = TrendsUiState(isLoading = true, points = emptyList())

        composeTestRule.setContent {
            HealthStatsTheme(darkTheme = false, dynamicColor = false) {
                TrendsScreen(
                    uiState = uiState,
                    onSourceSelected = {},
                    onExerciseTypeSelected = {},
                    onMetricSelected = {},
                )
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("No sessions match this bucket yet.").assertDoesNotExist()
    }
}
