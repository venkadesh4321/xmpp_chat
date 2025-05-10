package com.venki.xmppdemo.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.ConnectionListener
import org.jivesoftware.smack.ReconnectionManager
import org.jivesoftware.smack.XMPPConnection
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
    private var isIncomingMessageListenerSet = false
    private var connectionListener: ConnectionListener? = null
    private var onStatusChangedCallback: ((String) -> Unit)? = null
    private var reconnectionListener: ReconnectionManager? = null

    suspend fun connect(onStatusChanged: ((String) -> Unit)? = null) {
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

                // Set status callback
                onStatusChangedCallback = onStatusChanged
                addConnectionListener()

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
                if (xmppConnection?.isAuthenticated == true) {
                    ReconnectionManager.getInstanceFor(xmppConnection).enableAutomaticReconnection()
                }
                Log.d(TAG, "Logged in as: ${xmppConnection?.user}")
            } catch (e: Exception) {
                Log.e(TAG, "Login failed: ${e.message}")
            }
        }
    }

    suspend fun sendMessage(to: String, messageBody: String) {
        return withContext(Dispatchers.IO) {
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
    }

    fun setupIncomingMessageListener(onNewMessage: (from: String, message: String) -> Unit) {
        if (!isIncomingMessageListenerSet) {
            val chatManager = ChatManager.getInstanceFor(xmppConnection)
            chatManager.addIncomingListener { from, message, chat ->
                Log.d("XmppManager", "New message from $from: ${message.body}")
                onNewMessage(from.asBareJid().toString(), message.body)
                isIncomingMessageListenerSet = true
            }
        }
    }

    private fun addConnectionListener() {
        if (connectionListener == null) {
            connectionListener = object : ConnectionListener {
                override fun connecting(connection: XMPPConnection?) {
                    Log.d(TAG, "Connecting")
                    onStatusChangedCallback?.invoke("Connecting")
                }

                override fun connected(connection: XMPPConnection?) {
                    Log.d(TAG, "Connected")
                    onStatusChangedCallback?.invoke("Connected")
                }

                override fun authenticated(connection: XMPPConnection?, resumed: Boolean) {
                    Log.d(TAG, "Authenticated")
                    onStatusChangedCallback?.invoke("Authenticated")
                }

                override fun connectionClosed() {
                    Log.d(TAG, "Connection closed")
                    onStatusChangedCallback?.invoke("Disconnected")
                }

                override fun connectionClosedOnError(e: Exception?) {
                    Log.d(TAG, "Connection closed on error: ${e?.message}")
                    onStatusChangedCallback?.invoke("Disconnected (Error)")
                }
            }
        }

        // Only attach if connection object is ready
        xmppConnection?.addConnectionListener(connectionListener!!)
    }

    fun isConnected(): Boolean = xmppConnection?.isConnected == true
    fun isAuthenticated(): Boolean = xmppConnection?.isAuthenticated == true
    fun getUser(): String? = xmppConnection?.user?.asEntityBareJidIfPossible()?.toString()

    fun disconnect() {
        xmppConnection?.disconnect()
        Log.d(TAG, "disconnected")
    }
}