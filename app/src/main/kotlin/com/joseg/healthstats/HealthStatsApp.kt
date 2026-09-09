package com.joseg.healthstats

import android.app.Application
import com.joseg.healthstats.di.AppContainer
import com.joseg.healthstats.di.DefaultAppContainer

class HealthStatsApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
