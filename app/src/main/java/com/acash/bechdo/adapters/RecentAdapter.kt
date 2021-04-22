package com.acash.bechdo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.acash.bechdo.R
import com.acash.bechdo.models.Product
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recent_row.view.*

class RecentAdapter(private val list: ArrayList<Product>): RecyclerView.Adapter<RecentAdapter.RecentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecentViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.recent_row, parent, false
        )
    )

    override fun onBindViewHolder(holder: RecentViewHolder, position: Int) {
        val product: Product = list[position]

        holder.itemView.apply{
            Picasso.get().load(product.downLoadUrlsPics[0]).into(image)
            tvRecentProduct.isSelected = true
            tvRecentPrice.isSelected = true
            tvRecentProduct.text = product.title
            tvRecentPrice.text = "₹ ${product.price}"
        }
    }

    override fun getItemCount(): Int = list.size

    class RecentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}