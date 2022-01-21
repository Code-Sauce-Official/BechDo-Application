package com.acash.bechdo.adapters

import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.acash.bechdo.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_item_product_pic_post.view.*

class ProductPicsPostAdapter(private val pics:ArrayList<Uri>):RecyclerView.Adapter<ProductPicsPostAdapter.ProductPicsHolder>() {
    var onClick: (() -> Unit)? = null
    class ProductPicsHolder(itemView: View) :RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductPicsHolder =
        ProductPicsHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_product_pic_post,parent,false))

    override fun onBindViewHolder(holder: ProductPicsHolder, position: Int) {
        holder.itemView.apply {
            Glide.with(this)
                .clear(imgView)

            if (position == 0) {
                cardView.background = ContextCompat.getDrawable(context, R.drawable.add_product_pic)
                imgView.setBackgroundColor(Color.TRANSPARENT)
                setOnClickListener {
                    onClick?.invoke()
                }
            } else if (position < pics.size + 1) {
                Glide.with(this).load(pics[position - 1])
                    .error(R.drawable.default_image)
                    .into(imgView)
            }
        }
    }

    override fun getItemCount() = 8
}