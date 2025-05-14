package com.venki.xmppdemo.ui.contacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venki.xmppdemo.data.network.XmppManager
import com.venki.xmppdemo.repository.XmppRepository
import kotlinx.coroutines.launch

class ContactsViewModel(
    private val xmppRepository: XmppRepository
): ViewModel() {
    private val TAG = ContactsViewModel::class.simpleName
    private val _contacts: MutableLiveData<MutableList<String>> = MutableLiveData<MutableList<String>>(mutableListOf())
    val contacts: LiveData<MutableList<String>>
        get() = _contacts

    suspend fun getContacts() {
        viewModelScope.launch {
            if (xmppRepository.isConnected()) {
                val contacts = xmppRepository.getContacts()
                _contacts.postValue(contacts)
            } else {
                XmppManager.connect {
                    viewModelScope.launch {
                        val contacts = xmppRepository.getContacts()
                        _contacts.postValue(contacts)
                    }
                }
            }
        }
    }
}