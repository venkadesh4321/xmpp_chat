package com.venki.xmppdemo

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jxmpp.jid.impl.JidCreate

object XmppManager {
    private val TAG = XmppManager::class.simpleName

    private val hostName = "siruthuli.duckdns.org"
    private val domain = "siruthuli.duckdns.org"
    private val port = 5222
    private var xmppConnection: XMPPTCPConnection? = null

    suspend fun connect() {
        return withContext(Dispatchers.IO) {
            try {
                if (xmppConnection?.isConnected == true) {
                    Log.d(TAG, "Already connected")
                    return@withContext
                }
                val config = XMPPTCPConnectionConfiguration.builder()
                    .setXmppDomain(domain)
                    .setHost(hostName)
                    .setPort(port)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .build()

                xmppConnection = XMPPTCPConnection(config)
                Log.d(TAG, "Trying to connect...")
                xmppConnection?.connect()
                Log.d(TAG, "Connected to server: ${xmppConnection?.host}")
            } catch (e: Exception) {
                Log.e(TAG, "Connection failed: ${e.message}")
            }
        }
    }

    suspend fun login(userName: String, password: String) {
        return withContext(Dispatchers.IO) {
            try {
                if (xmppConnection?.isConnected != true) {
                    Log.e(TAG, "Not connected yet!")
                    return@withContext
                }
                if (xmppConnection?.isAuthenticated == true) {
                    Log.d(TAG, "Already logged in")
                    return@withContext
                }
                xmppConnection?.login(userName, password)
                Log.d(TAG, "Logged in as: ${xmppConnection?.user}")
            } catch (e: Exception) {
                Log.e(TAG, "Login failed: ${e.message}")
            }
        }
    }

    fun sendMessage(to: String, messageBody: String) {
        try {
            val chatManager = ChatManager.getInstanceFor(xmppConnection)
            val finalJid = if (to.contains("@")) to else "$to@$domain"
            val jid = JidCreate.entityBareFrom(finalJid)
            Log.d("XmppManager", "Message sent to $to: $messageBody")
            val chat = chatManager.chatWith(jid)
            chat.send(messageBody)
        } catch (e: Exception) {
            Log.e("XmppManager", "Failed to send message: ${e.message}")
        }
    }

    fun setupIncomingMessageListener(onNewMessage: (from: String, message: String) -> Unit) {
        val chatManager = ChatManager.getInstanceFor(xmppConnection)
        chatManager.addIncomingListener { from, message, chat ->
            Log.d("XmppManager", "New message from $from: ${message.body}")
            onNewMessage(from.asBareJid().toString(), message.body)
        }
    }

    fun isConnected(): Boolean = xmppConnection?.isConnected == true
    fun isAuthenticated(): Boolean = xmppConnection?.isAuthenticated == true

    fun disconnect() {
        xmppConnection?.disconnect()
        Log.d(TAG, "disconnected")
    }
}