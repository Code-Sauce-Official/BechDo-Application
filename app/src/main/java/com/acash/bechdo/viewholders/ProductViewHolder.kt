package com.acash.bechdo.viewholders

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.acash.bechdo.R
import com.acash.bechdo.activities.MainActivity
import com.acash.bechdo.fragments.mainactivity.ProductInfoFragment
import com.acash.bechdo.models.Product
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.android.synthetic.main.list_item_product.view.*

class ProductViewHolder(itemView: View,private val activity: Activity):RecyclerView.ViewHolder(itemView) {

    fun bind(product:Product){
        itemView.apply {
            cardProduct.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))

            Glide.with(this).load(product.downLoadUrlsPics[0]).placeholder(R.drawable.defaultavatar)
                .error(R.drawable.defaultavatar)
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
                val fragmentToSet = ProductInfoFragment()
                val bundle = Bundle()

                val gson = Gson()
                val productJsonString = gson.toJson(product)

                bundle.putString("ProductJsonString",productJsonString)
                fragmentToSet.arguments = bundle
                (activity as MainActivity).setFragment(fragmentToSet)
            }

        }
    }

}