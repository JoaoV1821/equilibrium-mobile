package com.ufpr.equilibrium.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.ufpr.equilibrium.network.Usuario

object SessionManager {
    private var prefs: SharedPreferences? = null
    private val gson = Gson()

    fun init(context: Context) {
        prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    }

    var token: String?
        get() = prefs?.getString("TOKEN", null)
        set(value) = prefs?.edit()?.putString("TOKEN", value)?.apply()!!

    var user: Usuario?
        get() {
            val json = prefs?.getString("USUARIO", null)
            return json?.let { gson.fromJson(it, Usuario::class.java) }
        }
        set(value) {
            val json = gson.toJson(value)
            prefs?.edit()?.putString("USUARIO", json)?.apply()
        }

    fun clearSession() {
        prefs?.edit()?.clear()?.apply()
    }

    fun isLoggedIn(): Boolean {
        return token != null
    }
}
