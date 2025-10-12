package com.example.geographyexplorer


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// 1. Modify the constructor to accept a lambda function for handling clicks.
class CategoryAdapter(
    private var categoryList: List<GeographyCategory>,
    private val onItemClicked: (GeographyCategory) -> Unit // The click listener
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryIcon: ImageView = itemView.findViewById(R.id.imageViewCategoryIcon)
        val categoryName: TextView = itemView.findViewById(R.id.textViewCategoryName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_card, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.categoryName.text = category.name
        holder.categoryIcon.setImageResource(category.imageResId)

        // 2. Set the click listener on the item's view.
        holder.itemView.setOnClickListener {
            onItemClicked(category) // Call the lambda function passed from the activity
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filteredList: List<GeographyCategory>) {
        categoryList = filteredList // Update the list
        notifyDataSetChanged() // Refresh the RecyclerView
    }
}