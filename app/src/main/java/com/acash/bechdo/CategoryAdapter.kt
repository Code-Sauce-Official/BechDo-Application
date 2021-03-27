package com.acash.bechdo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.category_row.view.*

class CategoryAdapter(private val list: ArrayList<Categories>, val context: Context): RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.category_row, parent, false
        )
        return CategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val user: Categories = list[position]

        val colors = context.resources.getIntArray(R.array.random_colors)
        holder.itemView.image.setBackgroundColor(colors[position%3])
        holder.itemView.image.setImageResource(user.id)
        holder.itemView.tvCategory.text = user.caption
    }

    override fun getItemCount(): Int = list.size

    class CategoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

}