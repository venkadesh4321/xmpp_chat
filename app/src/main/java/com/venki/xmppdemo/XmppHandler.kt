package com.venki.xmppdemo

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jivesoftware.smack.AbstractXMPPConnection
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration

class XmppHandler(
    private val domain: String,
    private val host: String,
    private val port: Int,
    private val context: Context
    ) {
    private var connection: AbstractXMPPConnection? = null
    private val TAG = XmppHandler::class.simpleName

    suspend fun connect(userName: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val config = XMPPTCPConnectionConfiguration.builder()
                    .setXmppDomain(domain)
                    .setHost(host)
                    .setPort(port)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .setUsernameAndPassword(userName, password)
                    .setResource("Android")
                    .setHostnameVerifier { hostname, session ->
                        Log.d("SSL", "Hostname verifier: $hostname")
                        true  // Always trust
                    }
                    .setCustomSSLContext(getCustomSslContext(context))
                    .setCompressionEnabled(false)
                    .build()

                connection = XMPPTCPConnection(config)
                Log.d(TAG, "Trying to connect...")
                connection?.connect()
                Log.d(TAG, "Connected to server: ${connection?.host}")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Connection failed: ${e.message}")
                false
            }
        }

    }

    fun disconnect() {
        connection?.disconnect()
        Log.d(TAG, "Disconnected")
    }
}