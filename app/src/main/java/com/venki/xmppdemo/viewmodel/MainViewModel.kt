package com.venki.xmppdemo.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venki.xmppdemo.model.Chat
import com.venki.xmppdemo.repository.XmppRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: XmppRepository
) : ViewModel() {
    private val TAG = MainViewModel::class.simpleName

    private var _chats = MutableLiveData<MutableList<Chat>>(mutableListOf())
    val chats: LiveData<MutableList<Chat>> = _chats

    private var _status = MutableLiveData<String>()
    val status: LiveData<String> = _status

    init {
        viewModelScope.launch {
            if (repository.connect()) {
                _status.postValue("Connected")
                repository.setupIncomingMessageListener { from, message ->
                    Log.d(TAG, "Received message from $from: $message")
                    addChat(Chat(message, false))
                }
            } else {
                _status.postValue("Failed Connection")
            }
        }
    }

    fun addChat(chat: Chat) {
        Log.d(TAG, "addChat: $chat")
        val updatedList = _chats.value?.toMutableList() ?: mutableListOf()
        updatedList.add(chat)
        _chats.postValue(updatedList)
    }

    fun connectAndLogin(userName: String, password: String) {
        viewModelScope.launch {
            if (repository.login(userName, password)) {
                Log.d(TAG, "Logged in successfully")
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
            repository.sendMessage(recipient, message)
        }
    }
}