package com.venki.xmppdemo.ui.contacts

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venki.xmppdemo.data.network.XmppConnectionState
import com.venki.xmppdemo.data.network.XmppManager
import com.venki.xmppdemo.model.Contact
import com.venki.xmppdemo.repository.UserPreferenceRepository
import com.venki.xmppdemo.repository.XmppRepository
import kotlinx.coroutines.launch

class ContactsViewModel(
    private val xmppRepository: XmppRepository
): ViewModel() {
    private val TAG = ContactsViewModel::class.simpleName
    private val _contact: MutableLiveData<MutableList<Contact>> = MutableLiveData<MutableList<Contact>>(mutableListOf())
    val contact: LiveData<MutableList<Contact>>
        get() = _contact

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getContacts() {
        viewModelScope.launch {
            _isLoading.postValue(true)
            XmppManager.connectionState.collect { state ->
                when (state) {
                    is XmppConnectionState.Authenticated -> {
                            val contactList = xmppRepository.getContacts()
                            _isLoading.postValue(false)
                            _contact.postValue(contactList)
                    }
                    else -> {
                        _isLoading.postValue(false)
                    }
                }
            }
        }
    }
}