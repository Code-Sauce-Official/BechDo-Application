package com.acash.bechdo.viewholders

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.acash.bechdo.R
import com.acash.bechdo.models.Product
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.android.synthetic.main.list_item_product.view.*

class ProductViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

    fun bind(product:Product, onClick: ((productJsonString: String) -> Unit)?){
        itemView.apply {
            cardProduct.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))

            Glide.with(this).load(product.downLoadUrlsPics[0]).placeholder(R.drawable.default_image)
                .error(R.drawable.default_image)
                .into(image)

            tvTitle.isSelected = true
            tvPrice.isSelected = true
            tvTitle.text = product.title

            var price = if (product.price == 0L)
                "FREE"
            else "₹ ${product.price}"

            if(product.forRent){
                price += "/day"
                cardProduct.setCardBackgroundColor(ContextCompat.getColor(context,
                    R.color.light_blue
                ))
            }

            tvPrice.text = price

            cardProduct.setOnClickListener {
                onClick?.invoke(Gson().toJson(product))
            }

        }
    }

}