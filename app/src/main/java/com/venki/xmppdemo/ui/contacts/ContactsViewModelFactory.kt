package com.venki.xmppdemo.ui.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.venki.xmppdemo.repository.UserPreferenceRepository
import com.venki.xmppdemo.repository.XmppRepository

class ContactsViewModelFactory(
    private val xmppRepository: XmppRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactsViewModel::class.java)) {
            return ContactsViewModel(xmppRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}