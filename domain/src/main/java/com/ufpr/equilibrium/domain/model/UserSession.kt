package com.ufpr.equilibrium.domain.model

data class UserSession(
    val token: String,
    val id: String?,
    val cpf: String?,
    val fullName: String?,
    val phone: String?,
    val gender: String?,
    val role: String?
)


