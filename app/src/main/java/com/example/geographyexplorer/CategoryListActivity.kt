package com.example.geographyexplorer

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import com.example.geographyexplorer.databinding.ActivityCategoryListBinding
import java.util.Locale

class CategoryListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryListBinding
    private lateinit var categoryAdapter: CategoryAdapter
    private val fullCategoryList: List<GeographyCategory> = getCategories()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Set up toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Geography Explorer"

        // ✅ Apply custom font & shadow to toolbar title
        customizeToolbarTitle(binding.toolbar)

        // ✅ RecyclerView setup
        val displayList = ArrayList(fullCategoryList)
        categoryAdapter = CategoryAdapter(displayList) { selectedCategory ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("CATEGORY_NAME", selectedCategory.name)
            startActivity(intent)
        }

        binding.recyclerViewCategories.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerViewCategories.adapter = categoryAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return true
            }
        })
        return true
    }

    private fun filter(text: String?) {
        val filteredList = ArrayList<GeographyCategory>()
        if (text.isNullOrEmpty()) {
            filteredList.addAll(fullCategoryList)
        } else {
            for (item in fullCategoryList) {
                if (item.name.lowercase(Locale.ROOT).contains(text.lowercase(Locale.ROOT))) {
                    filteredList.add(item)
                }
            }
        }
        categoryAdapter.filterList(filteredList)
    }

    private fun getCategories(): List<GeographyCategory> {
        return listOf(
            GeographyCategory("Continents", R.drawable.ic_continents),
            GeographyCategory("Oceans", R.drawable.ic_oceans),
            GeographyCategory("Mountains", R.drawable.ic_mountains),
            GeographyCategory("Deserts", R.drawable.ic_deserts),
            GeographyCategory("Rivers", R.drawable.ic_rivers),
            GeographyCategory("Landmarks", R.drawable.ic_landmarks),
            GeographyCategory("Volcanoes", R.drawable.ic_volcanoes),
            GeographyCategory("Lakes", R.drawable.ic_lakes),
            GeographyCategory("Islands", R.drawable.ic_islands),
            GeographyCategory("Forests", R.drawable.ic_forests)
        )
    }

    // ✅ Function to customize toolbar title
    private fun customizeToolbarTitle(toolbar: Toolbar) {
        toolbar.post {
            for (i in 0 until toolbar.childCount) {
                val view: View = toolbar.getChildAt(i)
                if (view is TextView && view.text == toolbar.title) {
                    view.typeface = Typeface.MONOSPACE
                    view.setTextColor(Color.WHITE)
                    view.textSize = 22f
                    view.setShadowLayer(8f, 6f, 6f, Color.BLACK)
                    break
                }
            }
        }
    }
}
