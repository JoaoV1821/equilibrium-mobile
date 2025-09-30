package com.ufpr.equilibrium.feature_professional

data class PacientesEnvelope (
    val data: List<PacienteModelList>,
    val meta: Meta? = null
)