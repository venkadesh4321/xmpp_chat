package com.venki.xmppdemo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.venki.xmppdemo.repository.XmppRepository

class MainViewModelFactory(
    private val xmppRepository: XmppRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(xmppRepository) as T
    }
}