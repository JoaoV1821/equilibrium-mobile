package com.ufpr.equilibrium.feature_questionnaire

data class Question (
    val id: Int,
    val text: String,
    val options: List<Option>,
    val allowNote: Boolean = false // se precisa de campo de observação
)