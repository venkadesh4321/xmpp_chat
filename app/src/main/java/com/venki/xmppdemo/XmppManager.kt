package com.venki.xmppdemo

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jxmpp.jid.impl.JidCreate

class XmppManager {
    private val TAG = XmppManager::class.simpleName

    private val hostName = "siruthuli.duckdns.org"
    private val domain = "siruthuli.duckdns.org"
    private val port = 5222
    private var connection: XMPPTCPConnection? = null

    suspend fun connect(userName: String, password: String): Boolean {
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
                Log.d(TAG, "Connected to server: ${connection?.host}")
                connection?.login()
                Log.d(TAG, "logged in in as {$userName}")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Connection failed: ${e.message}")
                false
            }
        }
    }

    suspend fun sendMessage(to: String, messageBody: String) {
        try {
            val chatManager = ChatManager.getInstanceFor(connection)
            val finalJid = if(to.contains("@")) to else "$to@$domain"
            val jid = JidCreate.entityBareFrom(finalJid)
            Log.d("XmppManager", "Message sent to $to: $messageBody")
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

    fun disconnect() {
        connection?.disconnect()
        Log.d(TAG, "disconnected")
    }
}