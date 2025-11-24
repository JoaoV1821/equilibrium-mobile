package com.ufpr.equilibrium.feature_questionnaire

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AnswerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(answer: AnswerEntity)

    @Query("SELECT * FROM answers WHERE questionnaireId = :qid")
    suspend fun getAnswersFor(qid: String): List<AnswerEntity>

    @Query("DELETE FROM answers WHERE questionnaireId = :qid")
    suspend fun clear(qid: String)
}
