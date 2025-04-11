package com.ufpr.equilibrium


data class Teste(
    val tipo: String?,
    val cpfProfissional: String?,
    val cpfPaciente: String?,
    val id_unidade: String?,
    val dadosSensor: List<Map<String, Any>>
)
