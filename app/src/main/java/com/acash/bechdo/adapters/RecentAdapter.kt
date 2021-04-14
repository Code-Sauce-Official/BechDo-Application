package com.acash.bechdo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.acash.bechdo.R
import com.acash.bechdo.models.Recents
import kotlinx.android.synthetic.main.category_row.view.image
import kotlinx.android.synthetic.main.recent_row.view.*

class RecentAdapter(private val list: ArrayList<Recents>): RecyclerView.Adapter<RecentAdapter.RecentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecentViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.recent_row, parent, false
        )
    )

    override fun onBindViewHolder(holder: RecentViewHolder, position: Int) {
        val product: Recents = list[position]

        holder.itemView.apply{
            image.setImageResource(product.id)
            tvRecentProduct.text = product.pname
            tvRecentPrice.text = product.price
        }
    }

    override fun getItemCount(): Int = list.size

    class RecentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}