package com.acash.bechdo.viewholders

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.acash.bechdo.R
import com.acash.bechdo.models.Inbox
import com.acash.bechdo.utils.formatAsListItem
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_item_user.view.*

class InboxViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    fun bind(inbox: Inbox, onClick:(name:String, uid:String, thumbImg:String)->Unit) = with(itemView){
        countTv.isVisible = inbox.count>0
        countTv.text = inbox.count.toString()
        timeTv.text = inbox.time.formatAsListItem(context)

        titleTv.text = inbox.name
        subtitleTv.text = inbox.msg

        if(inbox.image!="")
            Glide.with(itemView).load(inbox.image).placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar).into(userImgView)
        else Glide.with(itemView).load(R.drawable.default_avatar).into(userImgView)

        setOnClickListener{
            onClick.invoke(inbox.name,inbox.from,inbox.image)
        }
    }
}