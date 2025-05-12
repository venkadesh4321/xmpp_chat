package com.venki.xmppdemo.ui.chat

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.venki.xmppdemo.repository.UserPreferenceRepository
import com.venki.xmppdemo.repository.XmppRepository

class ChatViewModelFactory(
    private val context: Context,
    private val xmppRepository: XmppRepository,
    private val userPreferenceRepository: UserPreferenceRepository
): ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return ChatViewModel(context, xmppRepository, userPreferenceRepository) as T
    }
}