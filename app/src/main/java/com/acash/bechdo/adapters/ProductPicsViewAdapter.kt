package com.acash.bechdo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.acash.bechdo.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_item_product_pic_view.view.*

class ProductPicsViewAdapter(private val downloadUrlsPics:ArrayList<String>) : RecyclerView.Adapter<ProductPicsViewAdapter.PicViewHolder>(
) {
    var onClick: ((Int) -> Unit)? = null

    class PicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PicViewHolder =
        PicViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_product_pic_view, parent, false
        ))

    override fun onBindViewHolder(holder: PicViewHolder, position: Int) {
        holder.itemView.apply {
            Glide.with(this).load(downloadUrlsPics[position]).placeholder(R.drawable.default_image)
                .error(R.drawable.default_image)
                .into(imgView)

            imgView.setOnClickListener {
                onClick?.invoke(position)
            }
        }
    }

    override fun getItemCount(): Int = downloadUrlsPics.size
}



