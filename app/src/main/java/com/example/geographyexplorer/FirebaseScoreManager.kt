package com.example.geographyexplorer

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FirebaseScoreManager {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private fun getUserScoresRef(): DatabaseReference? {
        val userId = auth.currentUser?.uid ?: return null
        return database.getReference("quiz_results").child(userId)
    }

    // Save quiz result
    fun saveScore(result: QuizResult) {
        val ref = getUserScoresRef() ?: return
        val newResultRef = ref.push()
        newResultRef.setValue(result)
    }

    // Get High Score
    fun getHighScore(callback: (QuizResult?) -> Unit) {
        val ref = getUserScoresRef() ?: return

        ref.orderByChild("score").limitToLast(1)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    var highScore: QuizResult? = null

                    for (child in snapshot.children) {
                        highScore = child.getValue(QuizResult::class.java)
                    }

                    callback(highScore)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null)
                }
            })
    }

    // Get Score History
    fun getScoreHistory(callback: (List<QuizResult>) -> Unit) {
        val ref = getUserScoresRef() ?: return

        ref.orderByChild("timestamp")
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    val list = mutableListOf<QuizResult>()

                    for (child in snapshot.children) {
                        val result = child.getValue(QuizResult::class.java)
                        if (result != null) {
                            list.add(result)
                        }
                    }

                    list.reverse() // newest first

                    callback(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList())
                }
            })
    }
}