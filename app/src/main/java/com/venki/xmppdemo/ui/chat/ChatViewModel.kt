package com.venki.xmppdemo.ui.chat

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venki.xmppdemo.data.network.XmppConnectionState
import com.venki.xmppdemo.data.network.XmppManager
import com.venki.xmppdemo.model.Chat
import com.venki.xmppdemo.repository.XmppRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ChatUiEvent {
    data class IncomingMessage(val chat: MutableList<Chat>) : ChatUiEvent()
    data class FallbackMessage(val fromJid: String, val message: String) : ChatUiEvent()
}

class ChatViewModel(
    private val xmppRepository: XmppRepository
) : ViewModel() {
    private val TAG = ChatViewModel::class.simpleName

    private var _chats = MutableLiveData<MutableList<Chat>>(mutableListOf())

    private val _connectionState = MutableStateFlow<XmppConnectionState>(XmppConnectionState.Idle)
    val connectionState: StateFlow<XmppConnectionState> = _connectionState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<ChatUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    fun observeIncomingMessagesFor(chatJid: String) {
        viewModelScope.launch {
            XmppManager.incomingMessages.collect { incomingMessage ->
                Log.d(TAG, "observeIncomingMessages: $incomingMessage")
                if (incomingMessage.from == chatJid) {
                    addChat(Chat(incomingMessage.message, false))
                } else {
                    _uiEvents.emit(
                        ChatUiEvent.FallbackMessage(
                            incomingMessage.from,
                            incomingMessage.message
                        )
                    )
                }
            }
        }
    }

    private suspend fun addChat(chat: Chat) {
        Log.d(TAG, "addChat: $chat")
        val updatedList = _chats.value?.toMutableList() ?: mutableListOf()
        updatedList.add(chat)
        _chats.postValue(updatedList)
        _uiEvents.emit(ChatUiEvent.IncomingMessage(updatedList))
    }

    suspend fun sendMessage(recipient: String, message: String) {
        addChat(Chat(message, true))
        viewModelScope.launch {
            xmppRepository.sendMessage(recipient, message)
        }
    }
}