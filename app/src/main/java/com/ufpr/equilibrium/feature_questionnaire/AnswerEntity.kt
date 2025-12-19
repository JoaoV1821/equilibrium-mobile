package com.ufpr.equilibrium.feature_questionnaire

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "answers")
data class AnswerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val questionnaireId: String = "ivcf20",
    val questionId: String,  // UUID da API
    val selectedOptionIndex: Int, // -1 se não respondeu
    val score: Int,
    val note: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
