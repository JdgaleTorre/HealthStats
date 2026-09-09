package com.joseg.healthstats.di

import android.content.Context
import com.joseg.healthstats.data.healthconnect.HealthConnectManager

/** Single manual-DI container for the app: one [HealthConnectManager], no framework. */
interface AppContainer {
    val healthConnectManager: HealthConnectManager
    val viewModelFactory: AppViewModelFactory
}

class DefaultAppContainer(context: Context) : AppContainer {
    private val appContext = context.applicationContext

    override val healthConnectManager: HealthConnectManager by lazy {
        HealthConnectManager(appContext)
    }

    override val viewModelFactory: AppViewModelFactory by lazy {
        AppViewModelFactory(healthConnectManager)
    }
}
