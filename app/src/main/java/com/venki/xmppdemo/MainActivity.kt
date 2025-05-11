package com.venki.xmppdemo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.venki.xmppdemo.network.XmppManager
import com.venki.xmppdemo.repository.UserPreferenceRepository
import com.venki.xmppdemo.repository.XmppRepository
import com.venki.xmppdemo.viewmodel.MainViewModel
import com.venki.xmppdemo.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {
    private val TAG = MainActivity::class.simpleName

    private lateinit var mainViewModel: MainViewModel
    private lateinit var userNameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var statusTextView: TextView
    private lateinit var loginBtn: Button

    private lateinit var chatList: ListView
    private lateinit var messageEditText: EditText
    private lateinit var toEditText: EditText
    private lateinit var sendBtn: Button

    private var chatListAdapter: ChatListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViewModelAndObserver()
        initViews()

        loginBtn.setOnClickListener {
            val userName = userNameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (userName.isNotEmpty() && password.isNotEmpty()) {
                mainViewModel.connectAndLogin(applicationContext, userName, password)
            } else {
                Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show()
            }
        }

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
                mainViewModel.sendMessage(recipient, message)
                messageEditText.text.clear()
            } else {
                Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initViewModelAndObserver() {
        val xmppRepository = XmppRepository()
        val userPreferenceRepository = UserPreferenceRepository()
        mainViewModel =
            ViewModelProvider(this, MainViewModelFactory(applicationContext, xmppRepository, userPreferenceRepository))[MainViewModel::class.java]

        mainViewModel.chats.observe(this) {
            chatListAdapter?.updateChats(it)
        }

        mainViewModel.status.observe(this) {
            statusTextView.text = it
        }
    }

    private fun initViews() {
        loginBtn = findViewById(R.id.login_btn)
        userNameEditText = findViewById(R.id.user_name_edit_text)
        passwordEditText = findViewById(R.id.password_edit_text)
        statusTextView = findViewById(R.id.tv_status)
        chatList = findViewById(R.id.ltv_chat)
        messageEditText = findViewById(R.id.et_message)
        toEditText = findViewById(R.id.et_recipient)
        sendBtn = findViewById(R.id.btn_send)

        chatListAdapter = ChatListAdapter(this, mutableListOf())
        chatList.adapter = chatListAdapter
        chatList.selector = getDrawable(android.R.color.transparent)
    }
}
