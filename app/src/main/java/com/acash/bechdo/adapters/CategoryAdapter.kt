package com.acash.bechdo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.acash.bechdo.R
import com.acash.bechdo.models.Categories
import kotlinx.android.synthetic.main.category_row.view.*

class CategoryAdapter(private val list: ArrayList<Categories>, val context: Context, private val colors: IntArray = context.resources.getIntArray(
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
        }
    }

    override fun getItemCount(): Int = list.size

    class CategoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

}