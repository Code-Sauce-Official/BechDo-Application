package com.acash.bechdo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.acash.bechdo.adapters.CategoryAdapter
import com.acash.bechdo.adapters.RecentAdapter
import com.acash.bechdo.models.Categories
import com.acash.bechdo.models.Recents
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val activityRef = this

        val categories = Categories.getCategories()
        val categoryAdapter = CategoryAdapter(categories, activityRef)

        rvCategory.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            scrollToPosition(0)
            adapter = categoryAdapter
        }

        val recents = Recents.getRecents()
        val recentAdapter = RecentAdapter(recents)

        rvRecent.apply{
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            scrollToPosition(0)
            adapter = recentAdapter
        }

    }
}