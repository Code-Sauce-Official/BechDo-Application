package com.acash.bechdo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.acash.bechdo.R
import com.acash.bechdo.models.ChatEvent
import com.acash.bechdo.models.DateHeader
import com.acash.bechdo.models.Messages
import com.acash.bechdo.utils.formatAsTime
import kotlinx.android.synthetic.main.list_item_chat_receive_msg.view.*
import kotlinx.android.synthetic.main.list_item_date_header.view.*

class ChatAdapter(private val list: MutableList<ChatEvent>, private val currentUid: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var likeMsg: ((msgId: String, isLiked: Boolean) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflate = { layout: Int ->
            LayoutInflater.from(parent.context).inflate(layout, parent, false)
        }

        return when (viewType) {
            TEXT_MESSAGE_RECEIVED -> MessageViewHolder(inflate(R.layout.list_item_chat_receive_msg))
            TEXT_MESSAGE_SENT -> MessageViewHolder(inflate(R.layout.list_item_chat_sent_msg))
            else -> DateViewHolder(inflate(R.layout.list_item_date_header))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = list[position]) {
            is DateHeader -> {
                holder.itemView.textView.text = item.date
            }

            is Messages -> {
                holder.itemView.apply {
                    tvMsg.text = item.msg
                    tvTime.text = item.sentAt.formatAsTime()
                    heartImg.isVisible = item.liked
                }

                when (getItemViewType(position)) {
                    TEXT_MESSAGE_RECEIVED -> {
                        holder.itemView.recvMsgCardView.setOnClickListener(object :
                            DoubleClickListener() {
                            override fun onDoubleClick(v: View?) {
                                likeMsg?.invoke(item.msgId, !item.liked)
                            }
                        })
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int {
        return when (val event = list[position]) {
            is Messages -> {
                if (event.senderId == currentUid) {
                    TEXT_MESSAGE_SENT
                } else TEXT_MESSAGE_RECEIVED
            }
            is DateHeader -> DATE_HEADER
            else -> UNSUPPORTED
        }
    }

    class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        private const val UNSUPPORTED = -1
        private const val TEXT_MESSAGE_RECEIVED = 0
        private const val TEXT_MESSAGE_SENT = 1
        private const val DATE_HEADER = 2
    }
}

abstract class DoubleClickListener : View.OnClickListener {
    private var lastClickTime: Long = 0

    override fun onClick(v: View?) {
        val clickTime = System.currentTimeMillis()
        lastClickTime = if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
            onDoubleClick(v)
            0
        }else clickTime
    }

    abstract fun onDoubleClick(v: View?)

    companion object {
        private const val DOUBLE_CLICK_TIME_DELTA: Long = 300
    }
}