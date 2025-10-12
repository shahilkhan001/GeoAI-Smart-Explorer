package com.example.geographyexplorer

data class Question(
    val id: Int,
    val questionText: String,
    val image: Int, // We can add images to questions later, 0 for now
    val optionOne: String,
    val optionTwo: String,
    val optionThree: String,
    val optionFour: String,
    val correctAnswer: Int // Will be 1, 2, 3, or 4
)