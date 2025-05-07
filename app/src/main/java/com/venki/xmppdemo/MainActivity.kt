package com.venki.xmppdemo

import android.content.Context
import android.os.Bundle
import android.util.Log
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

class MainActivity : ComponentActivity() {
    private val TAG = MainActivity::class.simpleName

    private lateinit var submitBtn: Button
    private lateinit var userNameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var statusTextView: TextView

    private lateinit var chatList: ListView
    private lateinit var messageEditText: EditText
    private lateinit var toEditText: EditText
    private lateinit var sendBtn: Button

    private val messages = mutableListOf<String>()
    private var isConnected = false

    private var xmppManager: XmppManager? = null
    private var chatListAdapter: ChatListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        configureListAdapter()
        xmppManager = XmppManager()

        submitBtn.setOnClickListener {
            val userName = userNameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (userName.isNotEmpty() && password.isNotEmpty()) {
                Log.d(TAG, "onCreate: user name - $userName password - $password")
                lifecycleScope.launch {
                    isConnected = xmppManager?.connect(userName, password)!!
                    if (isConnected) {
                        Log.d(TAG, "onCreate: connected")
                        statusTextView.text = "Connected"
                        Toast.makeText(this@MainActivity, "Connected", Toast.LENGTH_SHORT).show()

                        xmppManager?.setupIncomingMessageListener { from, message ->
                            runOnUiThread {
                                addMessage(from, message)
                            }
                        }
                    } else {
                        Log.d(TAG, "onCreate: not connected")
                        statusTextView.text = "Not connected"
                        Toast.makeText(this@MainActivity, "Connection failed", Toast.LENGTH_SHORT)
                            .show()
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
                Toast.makeText(this, "Enter message", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isConnected) {
                messages.add("Me to $recipient: $message")
                lifecycleScope.launch {
                    xmppManager?.sendMessage(recipient, message)
                }
                messageEditText.text.clear()
                chatListAdapter?.notifyDataSetChanged()
            } else {
                Toast.makeText(this, "Not Connected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configureListAdapter() {
        chatListAdapter = ChatListAdapter(this, messages)
        chatList.adapter = chatListAdapter
    }

    private fun addMessage(from: String, message: String) {
        Log.d(TAG, "addMessage: from: $from message: $message")
        messages.add("${from.substringBefore("@")}: $message")
        chatListAdapter?.notifyDataSetChanged()
    }

    private fun initViews() {
        submitBtn = findViewById(R.id.submit_btn)
        userNameEditText = findViewById(R.id.user_name_edit_text)
        passwordEditText = findViewById(R.id.password_edit_text)
        statusTextView = findViewById(R.id.tv_status)
        chatList = findViewById(R.id.ltv_chat)
        messageEditText = findViewById(R.id.et_message)
        toEditText = findViewById(R.id.et_recipient)
        sendBtn = findViewById(R.id.btn_send)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState:")
        outState.putStringArrayList("messages", ArrayList(messages))
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d(TAG, "onRestoreInstanceState")
        savedInstanceState.getStringArrayList("messages")?.let {
            messages.addAll(it)
            chatListAdapter?.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        xmppManager?.disconnect()
    }
}

class ChatListAdapter(
    private val context: Context,
    private val messages: MutableList<String>,
) : ArrayAdapter<String>(context, R.layout.row_chat, messages) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView ?: View.inflate(context, R.layout.row_chat, null)
        val chat = getItem(position)
        val chatTextView = view.findViewById<TextView>(R.id.tv_chat)
        chatTextView.text = chat
        return view
    }
}