package com.ufpr.equilibrium.feature_questionnaire

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [AnswerEntity::class],
    version = 1,
    exportSchema = false
)
abstract class QuestionnaireDatabase : RoomDatabase() {

    abstract fun answerDao(): AnswerDao

    companion object {
        @Volatile
        private var INSTANCE: QuestionnaireDatabase? = null

        fun getInstance(context: Context): QuestionnaireDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    QuestionnaireDatabase::class.java,
                    "questionnaire.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}


