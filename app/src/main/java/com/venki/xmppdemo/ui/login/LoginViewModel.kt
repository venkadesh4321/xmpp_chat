package com.venki.xmppdemo.ui.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venki.xmppdemo.data.network.XmppManager
import com.venki.xmppdemo.repository.UserPreferenceRepository
import com.venki.xmppdemo.repository.XmppRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val xmppRepository: XmppRepository,
    private val userPreferenceRepository: UserPreferenceRepository
) : ViewModel() {
    private val TAG = LoginViewModel::class.simpleName

    private val _status = MutableLiveData<Boolean>()
    val status: LiveData<Boolean> = _status

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun connectAndLogin(userName: String, password: String) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            XmppManager.connect()
            if (xmppRepository.login(userName, password)) {
                Log.d(TAG, "Logged in successfully")
                saveCredentials(userName, password)
                _status.postValue(true)
                _isLoading.postValue(false)
            } else {
                Log.d(TAG, "Login failed")
                _status.postValue(false)
                _isLoading.postValue(false)
            }
        }
    }

    private fun saveCredentials(userName: String, password: String) {
        viewModelScope.launch {
            userPreferenceRepository.saveCredentials(userName, password)
        }
    }
}