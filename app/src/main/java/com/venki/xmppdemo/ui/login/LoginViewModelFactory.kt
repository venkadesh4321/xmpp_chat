package com.venki.xmppdemo.ui.login

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.venki.xmppdemo.repository.UserPreferenceRepository
import com.venki.xmppdemo.repository.XmppRepository

class LoginViewModelFactory(
    private val xmppRepository: XmppRepository,
    private val userPreferenceRepository: UserPreferenceRepository
): ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(xmppRepository, userPreferenceRepository) as T
    }
}