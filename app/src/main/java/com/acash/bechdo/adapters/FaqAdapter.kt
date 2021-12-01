package com.acash.bechdo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.acash.bechdo.R
import kotlinx.android.synthetic.main.list_item_faq.view.*

class FaqAdapter(private val arrQues: Array<Int>, private val arrAns: Array<Int>):RecyclerView.Adapter<FaqAdapter.FaqViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FaqViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.list_item_faq,parent,false)
    )

    override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {
        holder.itemView.apply {
            tvQues.text = context.getString(arrQues[position])
            tvAns.text = context.getString(arrAns[position])
            btnExpand.setOnClickListener {
                if(tvAns.maxLines==2){
                    btnExpand.setImageResource(R.drawable.ic_arrow_bottom_circle)
                    tvAns.maxLines = 10
                }else{
                    btnExpand.setImageResource(R.drawable.ic_arrow_right_circle)
                    tvAns.maxLines = 2
                }
            }
        }
    }

    override fun getItemCount() = arrQues.size

    class FaqViewHolder(itemView:View): RecyclerView.ViewHolder(itemView)

}