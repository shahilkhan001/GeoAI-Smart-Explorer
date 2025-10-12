package com.example.geographyexplorer

object Constants {
    fun getQuestions(): ArrayList<Question> {
        val questionsList = ArrayList<Question>()

        val q1 = Question(
            1,
            "Which is the largest desert in the world?",
            0,
            "Sahara Desert",
            "Arabian Desert",
            "Gobi Desert",
            "Antarctic Polar Desert",
            4
        )
        questionsList.add(q1)

        val q2 = Question(
            2,
            "What is the longest river in the world?",
            0,
            "Amazon River",
            "Nile River",
            "Yangtze River",
            "Mississippi River",
            2
        )
        questionsList.add(q2)

        val q3 = Question(
            3,
            "Mount Everest is located in which mountain range?",
            0,
            "The Andes",
            "The Rockies",
            "The Himalayas",
            "The Alps",
            3
        )
        questionsList.add(q3)

        return questionsList
    }
}