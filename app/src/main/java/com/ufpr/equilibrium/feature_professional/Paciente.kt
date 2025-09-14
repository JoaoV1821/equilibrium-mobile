package com.ufpr.equilibrium.feature_professional

import java.util.UUID

data class Paciente (

    val id: UUID,
    val fullName: String,
    val cpf: String,
    val age: Int,
    val weight: Float,
    val height: Int,
    val downFall: Boolean
)
