package com.venki.xmppdemo.data.network

import android.util.Log
import com.venki.xmppdemo.model.Contact
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.ConnectionListener
import org.jivesoftware.smack.ReconnectionManager
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jxmpp.jid.impl.JidCreate

object XmppManager {
    private val TAG = XmppManager::class.simpleName

    private val hostName = "siruthuli.duckdns.org"
    private val domain = "siruthuli.duckdns.org"
    private val port = 5222
    private var xmppConnection: XMPPTCPConnection? = null
    private var connectionListener: ConnectionListener? = null

    private val _connectionState = MutableStateFlow<XmppConnectionState>(XmppConnectionState.Disconnected)
    val connectionState: StateFlow<XmppConnectionState> = _connectionState

    private val _incomingMessages = MutableSharedFlow<IncomingMessage>()
    val incomingMessages: SharedFlow<IncomingMessage> = _incomingMessages

    private var isIncomingMessageListenerSet = false

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
                    .setSendPresence(true)
                    .build()

                xmppConnection = XMPPTCPConnection(config)

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
                Log.d(TAG, "logging in")
                xmppConnection?.login(userName, password)
                if (xmppConnection?.isAuthenticated == true) {
                    Log.d(TAG, "Logged in as: ${xmppConnection?.user}")
                    ReconnectionManager.getInstanceFor(xmppConnection).enableAutomaticReconnection()
                    Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Login failed: ${e.message}")
            }
        }
    }

    suspend fun sendMessage(to: String, messageBody: String) {
        return withContext(Dispatchers.IO) {
            try {
                if (xmppConnection?.isAuthenticated == false) {
                    Log.d(TAG, "Not logged in")
                    return@withContext
                }
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

    private fun setupIncomingMessageListenerIfNeeded() {
        if (!isIncomingMessageListenerSet) {
            val chatManager = ChatManager.getInstanceFor(xmppConnection)
            chatManager.addIncomingListener { from, message, _ ->
                Log.d(TAG, "New message from $from: ${message.body}")
                emitIncomingMessage(IncomingMessage(from = from.asBareJid().toString(), message = message.body))
            }
            isIncomingMessageListenerSet = true
        }
    }

    private fun addConnectionListener() {
        if (connectionListener == null) {
            connectionListener = object : ConnectionListener {
                override fun connecting(connection: XMPPConnection?) {
                    Log.d(TAG, "Connecting")
                    emitStatus(XmppConnectionState.Connecting)
                }

                override fun connected(connection: XMPPConnection?) {
                    Log.d(TAG, "Connected")
                    emitStatus(XmppConnectionState.Connected)
                }

                override fun authenticated(connection: XMPPConnection?, resumed: Boolean) {
                    Log.d(TAG, "Authenticated")
                    emitStatus(XmppConnectionState.Authenticated)
                    setupIncomingMessageListenerIfNeeded()
                }

                override fun connectionClosed() {
                    Log.d(TAG, "Connection closed")
                    emitStatus(XmppConnectionState.Disconnected)
                    isIncomingMessageListenerSet = false
                }

                override fun connectionClosedOnError(e: Exception?) {
                    Log.d(TAG, "Connection closed on error: ${e?.message}")
                    emitStatus(XmppConnectionState.Disconnected)
                    isIncomingMessageListenerSet = false
                }
            }
        }

        // Only attach if connection object is ready
        xmppConnection?.addConnectionListener(connectionListener!!)
    }

    private fun emitStatus(state: XmppConnectionState) {
        CoroutineScope(Dispatchers.IO).launch {
            _connectionState.emit(state)
        }
    }

    private fun emitIncomingMessage(incomingMessage: IncomingMessage) {
        CoroutineScope(Dispatchers.IO).launch {
            _incomingMessages.emit(incomingMessage)
        }
    }

    fun getRoasterEntries(): List<Contact> {
        val roster = Roster.getInstanceFor(xmppConnection)
        if (!roster.isLoaded) {
            try {
                roster.reloadAndWait()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to reload roster: ${e.message}")
            }
        }

        val entries = roster.entries
        if (entries.isEmpty()) {
            Log.d(TAG, "No contacts found in roster")
            return emptyList()
        }
        return roster.entries.map {
            val userName = it.jid.localpartOrNull?.toString() ?: ""
            val fullJid = it.jid.asBareJid().toString()
            Contact(userName, fullJid)
        }
    }

    fun isConnected(): Boolean = xmppConnection?.isConnected == true
    fun isAuthenticated(): Boolean = xmppConnection?.isAuthenticated == true
    fun getUser(): String? = xmppConnection?.user?.asEntityBareJidIfPossible()?.toString()

    fun disconnect() {
        xmppConnection?.disconnect()
        Log.d(TAG, "disconnected")
    }
}