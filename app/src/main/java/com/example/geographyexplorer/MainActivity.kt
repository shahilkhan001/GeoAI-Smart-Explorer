package com.example.geographyexplorer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.geographyexplorer.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import android.content.SharedPreferences

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPrefs: SharedPreferences
    private val firebaseManager = FirebaseScoreManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        sharedPrefs = getSharedPreferences("AppLockPrefs", MODE_PRIVATE)

        // Toolbar setup
        setSupportActionBar(binding.toolbar)

        // Card navigation
        binding.cardCategories.setOnClickListener {
            animateCard(it) {
                startActivity(Intent(this, CategoryListActivity::class.java))
            }
        }

        binding.cardQuizzes.setOnClickListener {
            animateCard(it) {
                startActivity(Intent(this, QuizActivity::class.java))
            }
        }

        binding.cardScoreHistory.setOnClickListener {
            animateCard(it) {
                startActivity(Intent(this, ScoreHistoryActivity::class.java))
            }
        }

        displayHighScore()
    }

    // ---------------- HIGH SCORE ----------------

    @SuppressLint("SetTextI18n")
    private fun displayHighScore() {

        firebaseManager.getHighScore { firebaseScore ->

            runOnUiThread {

                if (firebaseScore != null) {
                    binding.tvHighScore.text =
                        "High Score: ${firebaseScore.score}/${firebaseScore.totalQuestions}"
                } else {
                    binding.tvHighScore.text = "No quizzes taken yet!"
                }

            }
        }
    }

    // ---------------- TOOLBAR MENU ----------------

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.menu_applock -> {

                val enabled = sharedPrefs.getBoolean("is_app_lock_enabled", false)
                val newState = !enabled

                sharedPrefs.edit().putBoolean("is_app_lock_enabled", newState).apply()

                Toast.makeText(
                    this,
                    if (newState) "App Lock Enabled" else "App Lock Disabled",
                    Toast.LENGTH_SHORT
                ).show()

                return true
            }

            R.id.menu_logout -> {

                firebaseAuth.signOut()

                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)
                finish()

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    // ---------------- CARD CLICK ANIMATION ----------------

    private fun animateCard(view: View, action: () -> Unit) {

        view.animate()
            .scaleX(0.92f)
            .scaleY(0.92f)
            .setDuration(120)
            .withEndAction {

                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(120)
                    .withEndAction { action() }
                    .start()

            }
            .start()
    }
}