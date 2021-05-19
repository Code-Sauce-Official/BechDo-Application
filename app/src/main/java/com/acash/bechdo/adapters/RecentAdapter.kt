package com.acash.bechdo.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.acash.bechdo.ProductViewHolder
import com.acash.bechdo.R
import com.acash.bechdo.models.Product

class RecentAdapter(private val list: ArrayList<Product>,private val activity: Activity): RecyclerView.Adapter<ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_product, parent, false
        )
        , activity
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product: Product = list[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = list.size

}