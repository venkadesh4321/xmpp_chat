package com.venki.xmppdemo

import android.app.Application
import android.util.Log
import com.venki.xmppdemo.data.network.XmppConnectionState
import com.venki.xmppdemo.data.network.XmppManager
import com.venki.xmppdemo.repository.UserPreferenceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyApplication: Application() {
    private val TAG = MyApplication::class.simpleName

    override fun onCreate() {
        super.onCreate()

        observeXmppConnectionStatus()
        attemptXmppAutoConnect()
    }

    private fun observeXmppConnectionStatus() {
        CoroutineScope(Dispatchers.Main).launch {
            XmppManager.connectionState.collect { state ->
                when (state) {
                    is XmppConnectionState.Connecting -> Log.d(TAG, "Connecting to XMPP...")
                    is XmppConnectionState.Connected -> Log.d(TAG, "Connected to XMPP")
                    is XmppConnectionState.Authenticated -> Log.d(TAG, "Authenticated with XMPP")
                    is XmppConnectionState.Disconnected -> Log.d(TAG, "Disconnected from XMPP")
                    is XmppConnectionState.Error -> Log.e(TAG, "XMPP Error: ${state.message}")
                    else -> {}
                }
            }
        }
    }

    private fun attemptXmppAutoConnect() {
        CoroutineScope(Dispatchers.IO).launch {
            val userPreferenceRepository = UserPreferenceRepository()
            val credentials = userPreferenceRepository.getCredentials(applicationContext)
            if (credentials.first.isNotEmpty() && credentials.second.isNotEmpty()) {
                Log.d("App", "Attempting auto-connect with saved credentials...")
                XmppManager.connect()
                XmppManager.login(credentials.first, credentials.second)
            } else {
                Log.d("App", "No saved credentials found for auto-login.")
            }
        }
    }
}