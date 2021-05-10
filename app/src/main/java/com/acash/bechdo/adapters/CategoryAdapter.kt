package com.acash.bechdo.adapters

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.acash.bechdo.R
import com.acash.bechdo.activities.MainActivity
import com.acash.bechdo.fragments.mainactivity.PostsFragment
import com.acash.bechdo.models.Categories
import kotlinx.android.synthetic.main.category_row.view.*

class CategoryAdapter(private val list: ArrayList<Categories>, private val activity: Activity, private val colors: IntArray = activity.resources.getIntArray(
    R.array.random_colors
)): RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CategoryViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.category_row, parent, false
        )
    )

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category: Categories = list[position]

        holder.itemView.apply {
            image.setBackgroundColor(colors[position % 3])
            image.setImageResource(category.id)
            tvCategory.text = category.caption

            cardCategory.setOnClickListener {
                val fragmentToSet = PostsFragment()
                val bundle = Bundle()
                bundle.putString("Task","Category")
                bundle.putStringArrayList("FilterTags", arrayListOf(tvCategory.text.toString()))
                fragmentToSet.arguments = bundle
                (activity as MainActivity).setFragment(fragmentToSet)
            }
        }
    }

    override fun getItemCount(): Int = list.size

    class CategoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

}