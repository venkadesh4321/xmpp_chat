package com.venki.xmppdemo.ui.contacts

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venki.xmppdemo.data.network.XmppManager
import com.venki.xmppdemo.repository.UserPreferenceRepository
import com.venki.xmppdemo.repository.XmppRepository
import kotlinx.coroutines.launch

class ContactsViewModel(
    private val xmppRepository: XmppRepository,
    private val userPreferenceRepository: UserPreferenceRepository
): ViewModel() {
    private val TAG = ContactsViewModel::class.simpleName
    private val _contacts: MutableLiveData<MutableList<String>> = MutableLiveData<MutableList<String>>(mutableListOf())
    val contacts: LiveData<MutableList<String>>
        get() = _contacts

    suspend fun getContacts() {
        viewModelScope.launch {
            if (!XmppManager.isConnected()) {
                XmppManager.connect()
            }

            if (!XmppManager.isAuthenticated()) {
                // Replace with actual credentials retrieval
                val creds = userPreferenceRepository.getCredentials()
                if (creds.first.isNotEmpty() && creds.second.isNotEmpty()) {
                    XmppManager.login(creds.first, creds.second)
                } else {
                    Log.d(TAG, "Not connected")
                    return@launch
                }
            }

            val contactList = XmppManager.getRoasterEntries() as MutableList<String>
            _contacts.postValue(contactList)
        }
    }
}