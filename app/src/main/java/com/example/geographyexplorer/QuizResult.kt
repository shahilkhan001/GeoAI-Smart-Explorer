package com.example.geographyexplorer

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_results_table")
data class QuizResult(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val score: Int,
    val totalQuestions: Int,
    val timestamp: Long = System.currentTimeMillis()
)