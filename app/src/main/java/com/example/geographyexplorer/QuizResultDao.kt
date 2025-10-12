package com.example.geographyexplorer

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface QuizResultDao {
    @Insert
    suspend fun insert(quizResult: QuizResult)

    // Query to get the result with the highest score
    @Query("SELECT * FROM quiz_results_table ORDER BY score DESC LIMIT 1")
    suspend fun getHighScore(): QuizResult?

    // 1. Get the 10 most recent results
    @Query("SELECT * FROM quiz_results_table ORDER BY timestamp DESC LIMIT 10")
    suspend fun getRecentResults(): List<QuizResult>

    // 2. Get the total count of results
    @Query("SELECT COUNT(*) FROM quiz_results_table")
    suspend fun getCount(): Int

    // 3. Delete all results from the table
    @Query("DELETE FROM quiz_results_table")
    suspend fun clearAll()
}