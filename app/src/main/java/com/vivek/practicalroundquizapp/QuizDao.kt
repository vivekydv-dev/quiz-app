package com.vivek.practicalroundquizapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface QuizDao {
    @Query("SELECT * FROM 'quiz_table'")
    fun getAllQuizData(): List<QuizEntity>

    @Query("DELETE FROM 'quiz_table'")
    fun deleteAllQuizData()

    @Insert
    fun insertQuizData(quizData: QuizEntity)
}