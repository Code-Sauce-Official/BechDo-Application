package com.acash.bechdo

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.acash.bechdo.models.Product
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_item_product.view.*

class ProductViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

    fun bind(product:Product){
        itemView.apply {
            Glide.with(this).load(product.downLoadUrlsPics[0]).placeholder(R.drawable.defaultavatar)
                .into(image)
            tvTitle.isSelected = true
            tvPrice.isSelected = true
            tvTitle.text = product.title
            tvPrice.text = "${product.price}"
        }
    }

}