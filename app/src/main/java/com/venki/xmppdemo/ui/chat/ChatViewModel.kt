package com.venki.xmppdemo.ui.chat

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venki.xmppdemo.model.Chat
import com.venki.xmppdemo.network.XmppManager
import com.venki.xmppdemo.repository.UserPreferenceRepository
import com.venki.xmppdemo.repository.XmppRepository
import kotlinx.coroutines.launch

class ChatViewModel(
    private val context: Context,
    private val xmppRepository: XmppRepository,
    private val userPreferenceRepository: UserPreferenceRepository
): ViewModel() {
    private val TAG = ChatViewModel::class.simpleName

    private var _chats = MutableLiveData<MutableList<Chat>>(mutableListOf())
    val chats: LiveData<MutableList<Chat>> = _chats

    private var _status = MutableLiveData<String>()
    val status: LiveData<String> = _status

    init {
        viewModelScope.launch {
            Log.d(TAG, "Connecting to XMPP server")
            connect()
        }
    }

    private fun connect() {
        viewModelScope.launch {
            XmppManager.connect { status ->
                _status.postValue(status)
                if (status == "Connected") {
                    xmppRepository.setupIncomingMessageListener(
                        onMessageReceived = { from, message ->
                            Log.d(TAG, "Message received from $from: $message")
                            addChat(Chat(message, false))
                        }
                    )
                    viewModelScope.launch {
                        val pair = userPreferenceRepository.getCredentials(context)
                        if (pair.first.isNotEmpty() && pair.second.isNotEmpty()) {
                            Log.d(TAG, "Credentials found")
                            connectAndLogin(context, pair.first, pair.second)
                        }
                    }
                }
            }
        }
    }

    private fun addChat(chat: Chat) {
        Log.d(TAG, "addChat: $chat")
        val updatedList = _chats.value?.toMutableList() ?: mutableListOf()
        updatedList.add(chat)
        _chats.postValue(updatedList)
    }

    fun connectAndLogin(context: Context, userName: String, password: String) {
        viewModelScope.launch {
            if (xmppRepository.login(userName, password)) {
                Log.d(TAG, "Logged in successfully")
                saveCredentials(context, userName, password)
                _status.postValue("Logged in successfully")
            } else {
                Log.d(TAG, "Login failed")
                _status.postValue("Login failed")
            }
        }
    }

    fun sendMessage(recipient: String, message: String) {
        addChat(Chat(message, true))
        viewModelScope.launch {
            xmppRepository.sendMessage(recipient, message)
        }
    }

    private fun saveCredentials(context: Context, userName: String, password: String) {
        viewModelScope.launch {
            userPreferenceRepository.saveCredentials(context, userName, password)
        }
    }
}