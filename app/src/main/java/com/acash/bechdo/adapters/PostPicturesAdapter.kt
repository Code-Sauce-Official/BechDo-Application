package com.acash.bechdo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.acash.bechdo.R
import com.acash.bechdo.models.ProductPictures
import kotlinx.android.synthetic.main.post_pictures_row.view.*

class PostPicturesAdapter(
    val pics: ArrayList<ProductPictures>
) : RecyclerView.Adapter<PostPicturesAdapter.PicViewHolder>(
) {

    class PicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PicViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.post_pictures_row, parent, false
        )

        return PicViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PicViewHolder, position: Int) {

        holder.itemView.image_post.setImageResource(pics[position].ProductPics)
    }

    override fun getItemCount(): Int {
        return pics.size
    }


}



