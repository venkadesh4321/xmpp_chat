package com.venki.xmppdemo.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.venki.xmppdemo.repository.UserPreferenceRepository
import com.venki.xmppdemo.repository.XmppRepository

class MainViewModelFactory(
    private val context: Context,
    private val xmppRepository: XmppRepository,
    private val userPreferenceRepository: UserPreferenceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(context, xmppRepository, userPreferenceRepository) as T
    }
}