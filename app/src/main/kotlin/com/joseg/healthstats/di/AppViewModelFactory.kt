package com.joseg.healthstats.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.joseg.healthstats.data.healthconnect.HealthConnectManager
import com.joseg.healthstats.data.repository.HealthConnectRepository
import com.joseg.healthstats.ui.onboarding.OnboardingViewModel
import com.joseg.healthstats.ui.sessiondetail.SessionDetailViewModel
import com.joseg.healthstats.ui.sessionlist.SessionListViewModel
import com.joseg.healthstats.ui.trends.TrendsViewModel

/**
 * Builds every ViewModel the app has from container-provided dependencies. No DI framework.
 *
 * [repository] is a provider, not a value: [HealthConnectRepository] wraps a
 * [androidx.health.connect.client.HealthConnectClient] that is only safe to create once Health
 * Connect has reported itself available, so it must stay unresolved until a ViewModel that
 * actually needs it is constructed.
 */
class AppViewModelFactory(
    private val healthConnectManager: HealthConnectManager,
    private val repository: () -> HealthConnectRepository,
) {
    val factory: ViewModelProvider.Factory = viewModelFactory {
        initializer { OnboardingViewModel(healthConnectManager) }
        initializer { SessionListViewModel(repository()) }
        initializer { SessionDetailViewModel(repository()) }
        initializer { TrendsViewModel(repository()) }
    }
}
