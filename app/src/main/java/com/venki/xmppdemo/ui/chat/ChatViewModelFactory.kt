package com.venki.xmppdemo.ui.chat

import androidx.lifecycle.ViewModelProvider
import com.venki.xmppdemo.repository.XmppRepository
import com.venki.xmppdemo.ui.contacts.ContactsViewModel

class ChatViewModelFactory(
    private val xmppRepository: XmppRepository
) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(xmppRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}