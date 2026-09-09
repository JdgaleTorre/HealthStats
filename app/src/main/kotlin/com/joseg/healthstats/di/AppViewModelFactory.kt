package com.joseg.healthstats.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.joseg.healthstats.data.healthconnect.HealthConnectManager
import com.joseg.healthstats.ui.onboarding.OnboardingViewModel

/** Builds every ViewModel the app has from container-provided dependencies. No DI framework. */
class AppViewModelFactory(
    private val healthConnectManager: HealthConnectManager,
) {
    val factory: ViewModelProvider.Factory = viewModelFactory {
        initializer { OnboardingViewModel(healthConnectManager) }
    }
}
