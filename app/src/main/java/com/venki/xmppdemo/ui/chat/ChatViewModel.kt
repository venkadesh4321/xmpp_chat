package com.venki.xmppdemo.ui.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venki.xmppdemo.data.network.XmppConnectionState
import com.venki.xmppdemo.data.network.XmppManager
import com.venki.xmppdemo.model.Chat
import com.venki.xmppdemo.repository.XmppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val xmppRepository: XmppRepository
) : ViewModel() {
    private val TAG = ChatViewModel::class.simpleName

    private var _chats = MutableLiveData<MutableList<Chat>>(mutableListOf())
    val chats: LiveData<MutableList<Chat>> = _chats

    private val _connectionState = MutableStateFlow<XmppConnectionState>(XmppConnectionState.Idle)
    val connectionState: StateFlow<XmppConnectionState> = _connectionState.asStateFlow()

    init {
        observeIncomingMessages()
    }

    private fun observeIncomingMessages() {
        viewModelScope.launch {
            XmppManager.incomingMessages.collect { incomingMessage ->
                Log.d(TAG, "observeIncomingMessages: $incomingMessage")
                addChat(Chat(incomingMessage.message, false))
            }
        }
    }

    private fun addChat(chat: Chat) {
        Log.d(TAG, "addChat: $chat")
        val updatedList = _chats.value?.toMutableList() ?: mutableListOf()
        updatedList.add(chat)
        _chats.postValue(updatedList)
    }

    fun sendMessage(recipient: String, message: String) {
        addChat(Chat(message, true))
        viewModelScope.launch {
            xmppRepository.sendMessage(recipient, message)
        }
    }
}