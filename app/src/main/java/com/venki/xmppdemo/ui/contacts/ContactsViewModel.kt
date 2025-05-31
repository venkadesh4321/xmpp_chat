package com.venki.xmppdemo.ui.contacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venki.xmppdemo.data.network.IncomingMessage
import com.venki.xmppdemo.data.network.XmppConnectionState
import com.venki.xmppdemo.data.network.XmppManager
import com.venki.xmppdemo.model.Contact
import com.venki.xmppdemo.repository.XmppRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ContactsViewModel(
    private val xmppRepository: XmppRepository
) : ViewModel() {
    private val TAG = ContactsViewModel::class.simpleName
    private val _contact: MutableLiveData<MutableList<Contact>> =
        MutableLiveData<MutableList<Contact>>(mutableListOf())
    val contact: LiveData<MutableList<Contact>>
        get() = _contact
    private val contactMap = mutableMapOf<String, Contact>()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        observeIncomingMessages()
    }

    private fun observeIncomingMessages() {
        viewModelScope.launch {
            XmppManager.incomingMessages.collect { event ->
                if (event is IncomingMessage) {
                    contactMap[event.from]?.let { contact ->
                        contact.latestMessage = event.message
                        _contact.postValue(contactMap.values.toList() as MutableList<Contact>?) // re-post updated list
                    }
                }
            }
        }
    }

    fun getContacts() {
        viewModelScope.launch {
            _isLoading.postValue(true)

            val state =
                XmppManager.connectionState.first { it is XmppConnectionState.Authenticated }

            val contactList = xmppRepository.getContacts()
            contactList.forEach { contactMap[it.jid] = it }
            _contact.postValue(contactList)
            _isLoading.postValue(false)
        }
    }
}