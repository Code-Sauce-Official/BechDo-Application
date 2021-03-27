package com.acash.bechdo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val activityRef = this

        val categories = Categories.getCategories()
        val categoryAdapter = CategoryAdapter(categories, activityRef)

        rvCategory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true)
        (rvCategory.layoutManager as LinearLayoutManager).scrollToPosition(categories.size-1)
        rvCategory.adapter = categoryAdapter

        val recents = Recents.getRecents()
        val recentAdapter = RecentAdapter(recents)

        rvRecent.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true)
        (rvRecent.layoutManager as LinearLayoutManager).scrollToPosition(recents.size-1)
        rvRecent.adapter = recentAdapter
    }


}