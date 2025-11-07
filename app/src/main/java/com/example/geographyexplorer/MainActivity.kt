package com.example.geographyexplorer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.geographyexplorer.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import android.content.SharedPreferences

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance() // Initialize FirebaseAuth

        // Initialize SharedPreferences
        sharedPrefs = getSharedPreferences("AppLockPrefs", MODE_PRIVATE)

        // Set click listener for the Categories card
        binding.cardCategories.setOnClickListener {
            val intent = Intent(this, CategoryListActivity::class.java)
            startActivity(intent)
        }

        // Set click listener for the Quizzes card
        binding.cardQuizzes.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
        }

        // Set click listener for Score History card
        binding.cardScoreHistory.setOnClickListener {
            startActivity(Intent(this, ScoreHistoryActivity::class.java))
        }

        // Fetch and display the high score when the dashboard loads
        displayHighScore()

        // Add Logic for the App Lock Switch
        setupAppLockSwitch()

        // Set click listener for logout LinearLayout
        binding.logoutContainer.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            // Clear the activity stack to prevent user from going back
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupAppLockSwitch() {
        // Get the saved setting (default is 'false')
        val isAppLockEnabled = sharedPrefs.getBoolean("is_app_lock_enabled", false)
        binding.switchAppLock.isChecked = isAppLockEnabled

        binding.switchAppLock.setOnCheckedChangeListener { _, isChecked ->
            // Save the new setting
            sharedPrefs.edit().putBoolean("is_app_lock_enabled", isChecked).apply()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayHighScore() {
        val db = AppDatabase.getDatabase(this)
        lifecycleScope.launch {
            val highScore = db.quizResultDao().getHighScore()
            if (highScore != null) {
                binding.tvHighScore.text =
                    "High Score: ${highScore.score}/${highScore.totalQuestions}"
            } else {
                binding.tvHighScore.text = "No quizzes taken yet!"
            }
        }
    }
}
