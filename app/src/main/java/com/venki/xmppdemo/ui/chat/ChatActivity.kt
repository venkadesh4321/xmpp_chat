package com.venki.xmppdemo.ui.chat

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.venki.xmppdemo.ChatListAdapter
import com.venki.xmppdemo.R
import com.venki.xmppdemo.network.XmppManager
import com.venki.xmppdemo.repository.UserPreferenceRepository
import com.venki.xmppdemo.repository.XmppRepository

class ChatActivity : ComponentActivity() {
    private val TAG = ChatActivity::class.simpleName

    private lateinit var toEditText: EditText
    private lateinit var statusTextView: TextView
    private lateinit var chatList: ListView
    private lateinit var messageEditText: EditText
    private lateinit var sendBtn: Button

    private var chatListAdapter: ChatListAdapter? = null
    private lateinit var chatViewModel: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        initViews()
        initViewModelAndObserver()

        sendBtn.setOnClickListener {
            val message = messageEditText.text.toString().trim()
            val recipient = toEditText.text.toString().trim()

            if (recipient.isEmpty()) {
                Toast.makeText(this, "Enter recipient", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (message.isEmpty()) {
                Toast.makeText(this, "Type something", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (XmppManager.isAuthenticated()) {
                chatViewModel.sendMessage(recipient, message)
                messageEditText.text.clear()
            } else {
                Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initViews() {
        toEditText = findViewById(R.id.et_recipient)
        statusTextView = findViewById(R.id.tv_status)
        chatList = findViewById(R.id.ltv_chat)
        messageEditText = findViewById(R.id.et_message)
        sendBtn = findViewById(R.id.btn_send)

        chatListAdapter = ChatListAdapter(this, mutableListOf())
        chatList.adapter = chatListAdapter
        chatList.selector = getDrawable(android.R.color.transparent)
    }

    private fun initViewModelAndObserver() {
        val xmppRepository = XmppRepository()
        val userPreferenceRepository = UserPreferenceRepository()
        val chatViewModelFactory =
            ChatViewModelFactory(applicationContext, xmppRepository, userPreferenceRepository)
        chatViewModel =
            ViewModelProvider(this, chatViewModelFactory)[ChatViewModel::class.java]

        chatViewModel.chats.observe(this) {
            chatListAdapter?.updateChats(it)
        }

        chatViewModel.status.observe(this) {
            statusTextView.text = it
        }
    }
}