package com.ufpr.equilibrium.feature_professional

data class PacienteModel(
    val user: User,
    val birthday: String,
    val weight: Int,
    val height: Float,
    val zipCode: String,
    val street: String,
    val number: String,
    val complement: String,
    val neighborhood: String,
    val scholarship: String,
    val socio_economic_level: String,
    val city: String,
    val state: String,
)
