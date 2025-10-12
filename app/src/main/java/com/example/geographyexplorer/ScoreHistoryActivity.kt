package com.example.geographyexplorer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.geographyexplorer.databinding.ActivityScoreHistoryBinding
import kotlinx.coroutines.launch

class ScoreHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScoreHistoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoreHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@ScoreHistoryActivity)
            val results = db.quizResultDao().getRecentResults()
            binding.rvScoreHistory.adapter = ScoreHistoryAdapter(results)
        }
    }
}