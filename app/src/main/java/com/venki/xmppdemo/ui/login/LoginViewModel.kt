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

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    fun connectAndLogin(context: Context, userName: String, password: String) {
        viewModelScope.launch {
            XmppManager.connect { status ->
                if (status == "Connected") {
                    viewModelScope.launch {
                        if (xmppRepository.login(userName, password)) {
                            Log.d(TAG, "Logged in successfully")
                            saveCredentials(context, userName, password)
                            _status.postValue(true)
                        } else {
                            Log.d(TAG, "Login failed")
                            _status.postValue(false)
                        }
                    }
                }
            }
        }
    }

    private fun saveCredentials(context: Context, userName: String, password: String) {
        viewModelScope.launch {
            userPreferenceRepository.saveCredentials(context, userName, password)
        }
    }

    fun isLoggedIn(context: Context) {
        viewModelScope.launch {
            val pair = userPreferenceRepository.getCredentials(context)
            if (pair.first.isNotEmpty()) {
                Log.d(TAG, "Credentials found")
                _status.postValue(true)
            } else {
                Log.d(TAG, "No credentials found")
                _status.postValue(false)
            }
        }
    }
}