package com.ufpr.equilibrium.utils

object RoleHelpers {
    fun isHealthProfessional(): Boolean = SessionManager.user?.role == "HEALTH_PROFESSIONAL"
    fun isPatient(): Boolean = SessionManager.user?.role == "PATIENT"
}


