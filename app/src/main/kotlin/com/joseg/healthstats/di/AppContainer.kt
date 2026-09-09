package com.joseg.healthstats.di

import android.content.Context
import com.joseg.healthstats.data.healthconnect.HealthConnectManager
import com.joseg.healthstats.data.repository.HealthConnectRepository
import com.joseg.healthstats.data.repository.HealthConnectRepositoryImpl

/** Single manual-DI container for the app: one [HealthConnectManager], no framework. */
interface AppContainer {
    val healthConnectManager: HealthConnectManager

    /** Only safe to access once Health Connect has reported itself available. */
    val repository: HealthConnectRepository
    val viewModelFactory: AppViewModelFactory
}

class DefaultAppContainer(context: Context) : AppContainer {
    private val appContext = context.applicationContext

    override val healthConnectManager: HealthConnectManager by lazy {
        HealthConnectManager(appContext)
    }

    override val repository: HealthConnectRepository by lazy {
        HealthConnectRepositoryImpl(healthConnectManager.client)
    }

    override val viewModelFactory: AppViewModelFactory by lazy {
        AppViewModelFactory(healthConnectManager) { repository }
    }
}
