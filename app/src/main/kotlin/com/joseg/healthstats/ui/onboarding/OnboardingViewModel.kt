package com.joseg.healthstats.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joseg.healthstats.data.healthconnect.HealthConnectAvailability
import com.joseg.healthstats.data.healthconnect.HealthConnectManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Drives Health Connect availability detection and the permission request flow. */
class OnboardingViewModel(private val healthConnectManager: HealthConnectManager) : ViewModel() {

    private val _uiState = MutableStateFlow<OnboardingUiState>(OnboardingUiState.CheckingAvailability)
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        checkAvailability()
    }

    fun checkAvailability() {
        when (healthConnectManager.getSdkStatus()) {
            HealthConnectAvailability.Unavailable ->
                _uiState.value = OnboardingUiState.HealthConnectUnavailable
            HealthConnectAvailability.UpdateRequired ->
                _uiState.value = OnboardingUiState.HealthConnectUpdateRequired
            HealthConnectAvailability.Available -> {
                _uiState.value = OnboardingUiState.AwaitingPermissions
                refreshPermissions()
            }
        }
    }

    fun refreshPermissions() {
        viewModelScope.launch {
            val granted = healthConnectManager.grantedPermissions()
            _uiState.value = if (HealthConnectManager.EXERCISE_SESSION_READ_PERMISSION in granted) {
                OnboardingUiState.Ready(granted)
            } else {
                // Without exercise-session read access the app has nothing to browse,
                // regardless of which other record types were granted.
                OnboardingUiState.PermissionsRequiredBlocking
            }
        }
    }
}
