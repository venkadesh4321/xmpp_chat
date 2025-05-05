package com.venki.xmppdemo

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jivesoftware.smack.AbstractXMPPConnection
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jxmpp.jid.impl.JidCreate

class MainActivity : ComponentActivity() {
    private val TAG = MainActivity::class.simpleName

    private lateinit var submitBtn: Button
    private lateinit var userNameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var statusTextView: TextView

    private lateinit var chatList: ListView
    private lateinit var messageEditText: EditText
    private lateinit var sendBtn: Button

    private val messages = mutableListOf<String>()
    private var isConnected = false
    var connection: AbstractXMPPConnection? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        submitBtn = findViewById(R.id.submit_btn)
        userNameEditText = findViewById(R.id.user_name_edit_text)
        passwordEditText = findViewById(R.id.password_edit_text)
        statusTextView = findViewById(R.id.tv_status)
        chatList = findViewById(R.id.ltv_chat)
        messageEditText = findViewById(R.id.et_message)
        sendBtn = findViewById(R.id.btn_send)

        submitBtn.setOnClickListener {
            val userName = userNameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (userName.isNotEmpty() && password.isNotEmpty()) {
                Log.d(TAG, "onCreate: user name - $userName password - $password")
                lifecycleScope.launch {
                    isConnected = connect(userName, password)
                    if (isConnected) {
                        Log.d(TAG, "onCreate: connected")
                        statusTextView.text = "Connected"
                        Toast.makeText(this@MainActivity, "Connected", Toast.LENGTH_SHORT).show()

                        setupIncomingMessageListener { from, message ->
                            runOnUiThread {
                                messages.add("$from: $message")
//                                adapter.notifyDataSetChanged()
                                Log.d(TAG, "onCreate: " + message)
                            }
                        }
                    } else {
                        Log.d(TAG, "onCreate: not connected")
                        statusTextView.text = "Not Connected"
                        Toast.makeText(this@MainActivity, "Connection failed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {
                Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show()
            }
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, messages)
        chatList.adapter = adapter

        sendBtn.setOnClickListener {
            val message = messageEditText.text.toString()
            if (message.isNotEmpty() && isConnected) {
                messages.add(message)
                sendMessage("nantha@siruthuli.duckdns.org", message)
                messageEditText.text.clear()
                adapter.notifyDataSetChanged()
            }
        }

    }

    suspend fun connect(userName: String, password: String): Boolean {
        val hostName = "siruthuli.duckdns.org"
        val domain = "siruthuli.duckdns.org"
        val port = 5222

        return withContext(Dispatchers.IO) {
            try {
                val config = XMPPTCPConnectionConfiguration.builder()
                    .setXmppDomain(domain)
                    .setHost(hostName)
                    .setPort(port)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .setUsernameAndPassword(userName, password)
                    .build()

                connection = XMPPTCPConnection(config)
                Log.d(TAG, "Trying to connect...")
                connection?.connect()
                connection?.login()
                Log.d(TAG, "Connected to server: ${connection?.host}")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Connection failed: ${e.message}")
                false
            }
        }
    }

    fun sendMessage(toJid: String, messageBody: String) {
        try {
            val chatManager = ChatManager.getInstanceFor(connection)
            val jid = JidCreate.entityBareFrom(toJid)
            Log.d("XmppManager", "Message sent to $toJid: $messageBody")
            val chat = chatManager.chatWith(jid)
            chat.send(messageBody)
        } catch (e: Exception) {
            Log.e("XmppManager", "Failed to send message: ${e.message}")
        }
    }

    fun setupIncomingMessageListener(onNewMessage: (from: String, message: String) -> Unit) {
        val chatManager = ChatManager.getInstanceFor(connection)
        chatManager.addIncomingListener { from, message, chat ->
            Log.d("XmppManager", "New message from $from: ${message.body}")
            onNewMessage(from.asBareJid().toString(), message.body)
        }
    }
}