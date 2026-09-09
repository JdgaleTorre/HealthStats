package com.joseg.healthstats.ui.onboarding

import androidx.lifecycle.ViewModel
import com.joseg.healthstats.data.healthconnect.HealthConnectManager

/** Drives Health Connect availability detection and the permission request flow. */
class OnboardingViewModel(private val healthConnectManager: HealthConnectManager) : ViewModel()
