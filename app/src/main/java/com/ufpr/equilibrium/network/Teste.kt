package com.ufpr.equilibrium.network


data class Teste (
    val type: String?,
    val cpfHealthProfessional: String?,
    val cpfPatient: String?,
    val id_healthUnit: Int?,
    val date: java.util.Date,
    val totalTime: String,
    val sensorData: List<Map<String, Any>>
)
