package com.acash.bechdo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.acash.bechdo.viewholders.ProductViewHolder
import com.acash.bechdo.R
import com.acash.bechdo.models.Product

class RecentAdapter(private val list: ArrayList<Product>): RecyclerView.Adapter<ProductViewHolder>() {

    var onClick: ((productJsonString:String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_product, parent, false
        )
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product: Product = list[position]
        holder.bind(product,onClick)
    }

    override fun getItemCount(): Int = list.size

}