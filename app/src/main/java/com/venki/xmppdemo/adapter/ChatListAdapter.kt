package com.venki.xmppdemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.venki.xmppdemo.R
import com.venki.xmppdemo.model.Chat

class ChatListAdapter(
    private val context: Context,
    private val chats: MutableList<Chat>,
) : ArrayAdapter<Chat>(context, R.layout.row_chat, chats) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val chat = getItem(position)
        val layoutRes = if (chat?.isSent!!) R.layout.row_sent else R.layout.row_receive
        val view = LayoutInflater.from(context).inflate(layoutRes, null, false)

        val chatTextView = view.findViewById<TextView>(R.id.tv_chat)
        chatTextView.textAlignment =
            if (chat.isSent) View.TEXT_ALIGNMENT_TEXT_END else View.TEXT_ALIGNMENT_TEXT_START

        chatTextView.text = chat.message
        return view
    }

    fun updateChats(it: MutableList<Chat>?) {
        it?.let {
            chats.clear()
            chats.addAll(it)
            notifyDataSetChanged()
        }
    }
}