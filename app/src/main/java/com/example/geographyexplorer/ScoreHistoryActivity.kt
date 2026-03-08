package com.example.geographyexplorer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.geographyexplorer.databinding.ActivityScoreHistoryBinding

class ScoreHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScoreHistoryBinding
    private val firebaseManager = FirebaseScoreManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoreHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadScoreHistory()
    }

    private fun loadScoreHistory() {

        firebaseManager.getScoreHistory { results ->

            runOnUiThread {
                binding.rvScoreHistory.adapter = ScoreHistoryAdapter(results)
            }

        }
    }
}