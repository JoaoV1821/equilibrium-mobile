package com.ufpr.equilibrium.network

data class Usuario(
    val id: String,
    val cpf: String,
    val fullName: String,
    val password: String,
    val phone: String,
    val gender: String,
    val role: String
)
