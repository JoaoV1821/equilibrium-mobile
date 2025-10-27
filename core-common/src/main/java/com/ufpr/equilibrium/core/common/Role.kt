package com.ufpr.equilibrium.core.common

sealed class Role(val value: String) {
    data object Patient : Role("PATIENT")
    data object HealthProfessional : Role("HEALTH_PROFESSIONAL")

    companion object {
        fun from(raw: String?): Role? = when (raw) {
            Patient.value -> Patient
            HealthProfessional.value -> HealthProfessional
            else -> null
        }
    }
}


