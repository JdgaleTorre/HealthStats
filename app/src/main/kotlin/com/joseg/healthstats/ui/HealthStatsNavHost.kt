package com.joseg.healthstats.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.joseg.healthstats.di.AppViewModelFactory
import com.joseg.healthstats.ui.onboarding.OnboardingScreen
import com.joseg.healthstats.ui.onboarding.OnboardingUiState
import com.joseg.healthstats.ui.onboarding.OnboardingViewModel
import com.joseg.healthstats.ui.sessiondetail.SessionDetailScreen
import com.joseg.healthstats.ui.sessiondetail.SessionDetailViewModel
import com.joseg.healthstats.ui.sessionlist.SessionListScreen
import com.joseg.healthstats.ui.sessionlist.SessionListViewModel
import com.joseg.healthstats.ui.trends.TrendsScreen
import com.joseg.healthstats.ui.trends.TrendsViewModel

private object Routes {
    const val ONBOARDING = "onboarding"
    const val SESSIONS = "sessions"
    const val SESSION_DETAIL = "sessionDetail"
    const val TRENDS = "trends"
}

@Composable
fun HealthStatsNavHost(
    viewModelFactory: AppViewModelFactory,
    onInstallOrUpdateHealthConnect: () -> Unit,
    onRequestPermissions: () -> Unit,
    permissionResultTrigger: Int = 0,
) {
    val navController = rememberNavController()
    // Health Connect records aren't a NavType-friendly shape, so the session tapped in the list
    // is hoisted here and read back by the detail route instead of passed through nav args.
    var selectedSession by remember { mutableStateOf<ExerciseSessionRecord?>(null) }

    NavHost(navController = navController, startDestination = Routes.ONBOARDING) {
        composable(Routes.ONBOARDING) {
            val viewModel: OnboardingViewModel = viewModel(factory = viewModelFactory.factory)
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(uiState) {
                if (uiState is OnboardingUiState.Ready) {
                    navController.navigate(Routes.SESSIONS) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            }
            LaunchedEffect(permissionResultTrigger) {
                if (permissionResultTrigger > 0) viewModel.refreshPermissions()
            }

            OnboardingScreen(
                uiState = uiState,
                onInstallOrUpdateClick = onInstallOrUpdateHealthConnect,
                onRequestPermissionsClick = onRequestPermissions,
            )
        }

        composable(Routes.SESSIONS) {
            MainScaffold(navController = navController) {
                val viewModel: SessionListViewModel = viewModel(factory = viewModelFactory.factory)
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                SessionListScreen(
                    uiState = uiState,
                    onSessionClick = { session ->
                        selectedSession = session
                        navController.navigate(Routes.SESSION_DETAIL)
                    },
                )
            }
        }

        composable(Routes.SESSION_DETAIL) {
            val viewModel: SessionDetailViewModel = viewModel(factory = viewModelFactory.factory)
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(selectedSession) {
                selectedSession?.let(viewModel::loadSession)
            }

            SessionDetailScreen(uiState = uiState)
        }

        composable(Routes.TRENDS) {
            MainScaffold(navController = navController) {
                val viewModel: TrendsViewModel = viewModel(factory = viewModelFactory.factory)
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                TrendsScreen(
                    uiState = uiState,
                    onSourceSelected = viewModel::setSource,
                    onExerciseTypeSelected = viewModel::setExerciseType,
                    onMetricSelected = viewModel::setMetric,
                )
            }
        }
    }
}

@Composable
private fun MainScaffold(navController: NavHostController, content: @Composable () -> Unit) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == Routes.SESSIONS,
                    onClick = {
                        navController.navigate(Routes.SESSIONS) {
                            popUpTo(Routes.SESSIONS) { inclusive = true }
                        }
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                    label = { Text("Sessions") },
                )
                NavigationBarItem(
                    selected = currentRoute == Routes.TRENDS,
                    onClick = {
                        navController.navigate(Routes.TRENDS) {
                            popUpTo(Routes.SESSIONS)
                        }
                    },
                    icon = { Icon(Icons.Filled.ShowChart, contentDescription = null) },
                    label = { Text("Trends") },
                )
            }
        },
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            content()
        }
    }
}
