package com.venki.xmppdemo

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.io.Serializable

class MainActivity : ComponentActivity() {
    private val TAG = MainActivity::class.simpleName

    private lateinit var userNameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var statusTextView: TextView
    private lateinit var loginBtn: Button

    private lateinit var chatList: ListView
    private lateinit var messageEditText: EditText
    private lateinit var toEditText: EditText
    private lateinit var sendBtn: Button

    private val chats = mutableListOf<Chat>()
    private var chatListAdapter: ChatListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        configureListAdapter()
        connectSocket()

        statusTextView.text = if (XmppManager.isAuthenticated()) "Logged in as ${
            XmppManager.getUser()?.substringBefore("@")
        }" else "Not logged in"

        loginBtn.setOnClickListener {
            val userName = userNameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (userName.isNotEmpty() && password.isNotEmpty()) {
                Log.d(TAG, "onCreate: user name - $userName password - $password")
                lifecycleScope.launch {
                    if (!XmppManager.isConnected()) {
                        connectSocket()
                    }
                    XmppManager.login(userName, password)
                    if (XmppManager.isAuthenticated()) {
                        Log.d(TAG, "onCreate: authenticated")
                        statusTextView.text = "Welcome $userName to VChat"
                    } else {
                        Log.d(TAG, "onCreate: not authenticated")
                        statusTextView.text = "Not Failed. Try again."
                    }
                }
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
                val chat = Chat(message, true)
                Log.d(TAG, "send - $chat")
                addChat(chat)
                lifecycleScope.launch {
                    XmppManager.sendMessage(recipient, message)
                }
                messageEditText.text.clear()
            } else {
                Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configureListAdapter() {
        chatListAdapter = ChatListAdapter(this, chats)
        chatList.adapter = chatListAdapter
    }

    private fun addChat(chat: Chat) {
        chats.add(chat)
        chatListAdapter?.notifyDataSetChanged()
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
    }

    private fun connectSocket() {
        lifecycleScope.launch {
            XmppManager.connect()

            if (XmppManager.isConnected()) {
                Log.d(TAG, "onCreate: connected")
                XmppManager.setupIncomingMessageListener { from, message ->
                    runOnUiThread {
                        val chat = Chat(message, false)
                        addChat(chat)
                    }
                }
            } else {
                Log.d(TAG, "onCreate: not connected")
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState:")
        outState.putSerializable("chats", ArrayList(chats))
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d(TAG, "onRestoreInstanceState")
        val restoredChats = savedInstanceState.getSerializable("chats") as? ArrayList<Chat>
        restoredChats?.let {
            chats.clear()
            chats.addAll(it)
            chatListAdapter?.notifyDataSetChanged()
        }
    }
}

data class Chat(var message: String, var isSent: Boolean) : Serializable

class ChatListAdapter(
    private val context: Context,
    private val chats: MutableList<Chat>,
) : ArrayAdapter<Chat>(context, R.layout.row_chat, chats) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView ?: LayoutInflater.from(context).inflate(R.layout.row_chat, null)
        val chat = getItem(position)

        val chatTextView = view.findViewById<TextView>(R.id.tv_chat)
        chatTextView.textAlignment =
            if (chat?.isSent!!) View.TEXT_ALIGNMENT_TEXT_END else View.TEXT_ALIGNMENT_TEXT_START

        chatTextView.text = chat.message
        return view
    }
}