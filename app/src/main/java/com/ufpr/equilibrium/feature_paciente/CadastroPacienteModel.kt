package com.ufpr.equilibrium.feature_paciente

data class CadastroPacienteModel (
    val cpf: String,
    val dateOfBirth: String,
    val educationLevel: String,
    val socioEconomicStatus: String,
    val cep: String,
    val street: String,
    val number: Int,
    val neighborhood: String,
    val city: String,
    val state: String,
    val weight: Float,
    val age: Int,
    val downFall: Boolean,
    val gender: String,
    val profile: String,
)
