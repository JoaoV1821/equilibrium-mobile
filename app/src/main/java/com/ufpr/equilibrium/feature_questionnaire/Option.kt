package com.ufpr.equilibrium.feature_questionnaire

data class Option (
    val id: String? = null,  // UUID da API (opcional para retrocompatibilidade)
    val text: String,
    val score: Int
)