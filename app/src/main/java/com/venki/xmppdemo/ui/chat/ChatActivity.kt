package com.venki.xmppdemo.ui.chat

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.venki.xmppdemo.R
import com.venki.xmppdemo.adapter.ChatListAdapter
import com.venki.xmppdemo.data.network.XmppConnectionState
import com.venki.xmppdemo.repository.XmppRepository
import kotlinx.coroutines.launch

class ChatActivity : ComponentActivity() {
    private val TAG = ChatActivity::class.simpleName

    private lateinit var toEditText: EditText
    private lateinit var recipientTextView: TextView
    private lateinit var chatList: ListView
    private lateinit var messageEditText: EditText
    private lateinit var sendBtn: Button

    private var chatListAdapter: ChatListAdapter? = null
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var recipient: String
    private lateinit var jId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        initViews()
        initViewModelAndObserver()

        recipient = intent.getStringExtra("recipient") ?: ""
        jId = intent.getStringExtra("jid") ?: ""

        recipientTextView.text = recipient

        sendBtn.setOnClickListener {
            val message = messageEditText.text.toString().trim()

            if (jId.isEmpty()) {
                Toast.makeText(this, "No recipient", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (message.isEmpty()) {
                Toast.makeText(this, "Type something", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            chatViewModel.sendMessage(jId, message)
            messageEditText.text.clear()
        }
    }

    private fun initViews() {
        toEditText = findViewById(R.id.et_recipient)
        recipientTextView = findViewById(R.id.tv_recipient)
        chatList = findViewById(R.id.ltv_chat)
        messageEditText = findViewById(R.id.et_message)
        sendBtn = findViewById(R.id.btn_send)

        chatListAdapter = ChatListAdapter(this, mutableListOf())
        chatList.adapter = chatListAdapter
        chatList.selector = getDrawable(android.R.color.transparent)
    }

    private fun initViewModelAndObserver() {
        val xmppRepository = XmppRepository()
        val chatViewModelFactory = ChatViewModelFactory(xmppRepository)
        chatViewModel =
            ViewModelProvider(this, chatViewModelFactory)[ChatViewModel::class.java]

        chatViewModel.chats.observe(this) {
            chatListAdapter?.updateChats(it)
        }
    }
}