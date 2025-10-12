package com.example.geographyexplorer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class ScoreHistoryAdapter(private val results: List<QuizResult>) :
    RecyclerView.Adapter<ScoreHistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val scoreText: TextView = view.findViewById(R.id.tvScoreItem)
        val timestampText: TextView = view.findViewById(R.id.tvTimestampItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_score_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = results[position]
        holder.scoreText.text = "Score: ${result.score}/${result.totalQuestions}"
        holder.timestampText.text = formatTimestamp(result.timestamp)
    }

    override fun getItemCount() = results.size

    private fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return format.format(date)
    }
}