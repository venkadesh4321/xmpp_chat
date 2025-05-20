package com.venki.xmppdemo.ui.chat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.venki.xmppdemo.R
import com.venki.xmppdemo.adapter.ChatListAdapter
import com.venki.xmppdemo.repository.XmppRepository

class ChatActivity : AppCompatActivity() {
    private val TAG = ChatActivity::class.simpleName

    private lateinit var chatList: ListView
    private lateinit var messageEditText: EditText
    private lateinit var sendBtn: ImageButton

    private var chatListAdapter: ChatListAdapter? = null
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var recipient: String
    private lateinit var jId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        initViews()
        initViewModelAndObserver()

        messageEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val message = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

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
        chatList = findViewById(R.id.ltv_chat)
        messageEditText = findViewById(R.id.et_message)
        sendBtn = findViewById(R.id.btn_send)

        chatListAdapter = ChatListAdapter(this, mutableListOf())
        chatList.adapter = chatListAdapter
        chatList.selector = getDrawable(android.R.color.transparent)

        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        recipient = intent.getStringExtra("recipient") ?: ""
        jId = intent.getStringExtra("jid") ?: ""

        supportActionBar?.title = recipient
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