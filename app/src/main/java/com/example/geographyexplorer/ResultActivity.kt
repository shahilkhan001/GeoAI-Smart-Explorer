package com.example.geographyexplorer

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.geographyexplorer.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private val firebaseManager = FirebaseScoreManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val totalQuestions = intent.getIntExtra("TOTAL_QUESTIONS", 0)
        val correctAnswers = intent.getIntExtra("CORRECT_ANSWERS", 0)

        val result = QuizResult(
            score = correctAnswers,
            totalQuestions = totalQuestions
        )

        // Save result to Firebase
        firebaseManager.saveScore(result)

        binding.progressBarResult.max = totalQuestions

        animateResultProgress(correctAnswers, totalQuestions)

        binding.tvScore.text = "Your Score: $correctAnswers out of $totalQuestions"

        binding.btnPlayAgain.setOnClickListener {
            startActivity(Intent(this, QuizActivity::class.java))
            finish()
        }

        binding.btnGoToDashboard.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun animateResultProgress(score: Int, total: Int) {

        val animator = ValueAnimator.ofInt(0, score)
        animator.duration = 1200

        animator.addUpdateListener {

            val progress = it.animatedValue as Int

            binding.progressBarResult.progress = progress
            binding.tvProgressResult.text = "$progress/$total"

        }

        animator.start()
    }
}