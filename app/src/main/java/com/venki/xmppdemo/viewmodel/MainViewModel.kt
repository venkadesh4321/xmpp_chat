package com.venki.xmppdemo.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venki.xmppdemo.Chat
import com.venki.xmppdemo.repository.XmppRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: XmppRepository
) : ViewModel() {
    private var _chats = MutableLiveData<MutableList<Chat>>(mutableListOf())
    val chats: LiveData<MutableList<Chat>> = _chats

    private var _status = MutableLiveData<String>()
    val status: LiveData<String> = _status

    fun addChat(chat: Chat) {
        Log.d("MainModel", "addChat: $chat")
        val updatedList = _chats.value?.toMutableList() ?: mutableListOf()
        updatedList.add(chat)
        _chats.postValue(updatedList)
    }

    fun connectAndLogin(userName: String, password: String) {
        viewModelScope.launch {
            if (repository.connect()) {
                repository.setupIncomingMessageListener { from, message ->
                    Log.d("MainModel", "Received message from $from: $message")
                    addChat(Chat(message, false))
                }
                if (repository.login(userName, password)) {
                    Log.d("MainModel", "Logged in successfully")
                    _status.postValue("Logged in successfully")
                } else {
                    Log.d("MainModel", "Login failed")
                    _status.postValue("Login failed")
                }
            } else {
                _status.postValue("Failed Connection")
            }
        }
    }

    fun sendMessage(recipient: String, message: String) {
        addChat(Chat(message, true))
        viewModelScope.launch {
            repository.sendMessage(recipient, message)
        }
    }

    fun setupIncomingMessageListener() {

    }
}