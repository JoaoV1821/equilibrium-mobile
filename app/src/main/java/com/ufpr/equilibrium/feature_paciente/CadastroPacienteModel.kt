package com.ufpr.equilibrium.feature_paciente

data class CadastroPacienteModel (
    val nome: String,
    val cpf: String,
    val dataNascimento: String,
    val sexo: String,
    val queda: Boolean
)
