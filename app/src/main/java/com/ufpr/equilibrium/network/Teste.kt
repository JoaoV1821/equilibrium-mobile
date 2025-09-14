package com.ufpr.equilibrium.network

import java.util.UUID


data class Teste (
    val type: String?,
    val healthProfessionalId: String,
    val patientId: String,
    val healthcareUnitId: String?,
    val date: java.util.Date,
    val totalTime: String,
    val time_end: String,
    val time_init: String,
    val sensorData: List<Map<String, Any>>
)
