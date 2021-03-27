package com.acash.bechdo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.category_row.view.*
import kotlinx.android.synthetic.main.category_row.view.image
import kotlinx.android.synthetic.main.recent_row.view.*

class RecentAdapter(val list: ArrayList<Recents>): RecyclerView.Adapter<RecentAdapter.RecentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.recent_row, parent, false
        )
        return RecentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecentViewHolder, position: Int) {
        val user: Recents = list[position]

        holder.itemView.image.setImageResource(user.id)
        holder.itemView.tvRecentProduct.text = user.pname
        holder.itemView.tvRecentPrice.text = user.price
    }

    override fun getItemCount(): Int = list.size

    class RecentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}