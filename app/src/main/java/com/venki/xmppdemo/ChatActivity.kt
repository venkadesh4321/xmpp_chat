package com.venki.xmppdemo

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.activity.ComponentActivity

class ChatActivity : ComponentActivity() {
    private lateinit var chatList: ListView
    private lateinit var messageEditText: EditText
    private lateinit var sendBtn: Button

    val messages = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        chatList = findViewById(R.id.ltv_chat)
        messageEditText = findViewById(R.id.et_message)
        sendBtn = findViewById(R.id.btn_send)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, messages)
        chatList.adapter = adapter

        sendBtn.setOnClickListener {
            val message = messageEditText.text.toString()
            if (message.isNotEmpty()) {
                messages.add(message)
                messageEditText.text.clear()
                adapter.notifyDataSetChanged()
            }
        }
    }
}