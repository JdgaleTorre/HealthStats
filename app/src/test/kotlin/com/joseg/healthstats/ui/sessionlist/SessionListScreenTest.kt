package com.joseg.healthstats.ui.sessionlist

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.joseg.healthstats.data.repository.HealthConnectRepositoryImpl
import com.joseg.healthstats.fakes.FakeHealthConnectClient
import com.joseg.healthstats.fakes.metadataWithDataOrigin
import androidx.health.connect.client.records.metadata.DataOrigin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class SessionListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun session(index: Int) = ExerciseSessionRecord(
        startTime = Instant.parse("2026-01-0${index}T08:00:00Z"),
        startZoneOffset = null,
        endTime = Instant.parse("2026-01-0${index}T09:00:00Z"),
        endZoneOffset = null,
        exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_RUNNING,
        title = "Run $index",
        metadata = metadataWithDataOrigin(DataOrigin("com.strava")),
    )

    @Test
    fun `rendering the session list issues no per-row aggregate query`() {
        val fakeClient = FakeHealthConnectClient().apply {
            exerciseSessions = (1..5).map { session(it) }
        }
        val repository = HealthConnectRepositoryImpl(fakeClient)
        val uiState = SessionListUiState(
            sessions = fakeClient.exerciseSessions,
            isLoading = false,
        )

        composeTestRule.setContent {
            androidx.compose.material3.MaterialTheme {
                SessionListScreen(uiState = uiState, onSessionClick = {})
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Run 1").assertExists()
        composeTestRule.onNodeWithText("Run 5").assertExists()
        assertEquals(0, fakeClient.aggregateCallCount)
        // The screen renders straight from the ExerciseSessionRecord list it's given; it never
        // calls the repository itself, so readRecords should not have been invoked either.
        assertEquals(0, fakeClient.readRecordsCallCount)
    }

    @Test
    fun `empty state shows when no sessions match and not loading`() {
        val uiState = SessionListUiState(sessions = emptyList(), isLoading = false)

        composeTestRule.setContent {
            androidx.compose.material3.MaterialTheme {
                SessionListScreen(uiState = uiState, onSessionClick = {})
            }
        }

        composeTestRule.onNodeWithText("No sessions match your filters.").assertExists()
    }
}
