package com.ufpr.equilibrium

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.ufpr.equilibrium.utils.SessionManager

@HiltAndroidApp
class EquilibriumApp : Application() {
    companion object {
        lateinit var appContext: Application
            private set
    }
    override fun onCreate() {
        super.onCreate()
        appContext = this
        SessionManager.init(this)
    }
}


