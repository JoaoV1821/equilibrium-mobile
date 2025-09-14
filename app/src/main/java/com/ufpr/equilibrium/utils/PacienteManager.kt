package com.ufpr.equilibrium.utils

import android.content.Context
import android.content.SharedPreferences
import java.util.UUID

object PacienteManager {

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences("paciente_cpf", Context.MODE_PRIVATE)
    }

    var cpf: String?
        get() = prefs?.getString("CPF", null)
        set(value) = prefs?.edit()?.putString("CPF", value)?.apply()!!

    var teste: String?
        get() = prefs?.getString("CPF", null)
        set(value) = prefs?.edit()?.putString("CPF", value)?.apply()!!

    var uuid: UUID?
        get() = prefs?.getString("id", null)?.let { UUID.fromString(it) }
        set(value) {
            prefs?.edit()?.putString("id", value?.toString())?.apply()
        }


    fun clearPacienteCpf() {
        prefs?.edit()?.clear()?.apply()
    }

}