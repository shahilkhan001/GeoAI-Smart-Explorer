package com.example.geographyexplorer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.geographyexplorer.databinding.ActivityResultBinding
import kotlinx.coroutines.launch

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the score data from the Intent
        val totalQuestions = intent.getIntExtra("TOTAL_QUESTIONS", 0)
        val correctAnswers = intent.getIntExtra("CORRECT_ANSWERS", 0)

        // --- Save the quiz result to the database with max 10 entries ---
        val db = AppDatabase.getDatabase(this)
        lifecycleScope.launch {
            val dao = db.quizResultDao()
            dao.insert(QuizResult(score = correctAnswers, totalQuestions = totalQuestions))

            // Check if the count exceeds 10
            if (dao.getCount() > 10) {
                dao.clearAll()
                // Re-insert the current score so the list starts with it
                dao.insert(QuizResult(score = correctAnswers, totalQuestions = totalQuestions))
            }
        }
        // ---------------------------------------------------------------

        // Set the progress and text
        binding.progressBarResult.max = totalQuestions
        binding.progressBarResult.progress = correctAnswers
        binding.tvProgressResult.text = "$correctAnswers/$totalQuestions"

        // Set the final score text
        binding.tvScore.text = "Your Score: $correctAnswers out of $totalQuestions"

        // Set listener for the "Play Again" button
        binding.btnPlayAgain.setOnClickListener {
            startActivity(Intent(this, QuizActivity::class.java))
            finish()
        }

        // Set listener for the "Go to Dashboard" button
        binding.btnGoToDashboard.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
