package com.example.geographyexplorer

data class QuizResult(

    var id: Int = 0,
    var score: Int = 0,
    var totalQuestions: Int = 0,
    var timestamp: Long = System.currentTimeMillis()

) {
    // Required empty constructor for Firebase
    constructor() : this(0, 0, 0, 0)
}