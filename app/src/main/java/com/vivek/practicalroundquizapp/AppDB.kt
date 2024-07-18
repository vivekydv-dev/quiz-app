package com.vivek.practicalroundquizapp

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [QuizEntity::class], version = 1)
@TypeConverters(ArrayListConverter::class)
abstract class AppDB : RoomDatabase() {
    abstract val quizDao: QuizDao
}