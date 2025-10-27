package com.ufpr.equilibrium

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.ufpr.equilibrium.utils.SessionManager

@HiltAndroidApp
class EquilibriumApp : Application() {
    override fun onCreate() {
        super.onCreate()
        SessionManager.init(this)
    }
}


