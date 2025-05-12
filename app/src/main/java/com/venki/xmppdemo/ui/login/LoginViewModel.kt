package com.venki.xmppdemo.ui.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venki.xmppdemo.model.Chat
import com.venki.xmppdemo.network.XmppManager
import com.venki.xmppdemo.repository.UserPreferenceRepository
import com.venki.xmppdemo.repository.XmppRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val xmppRepository: XmppRepository,
    private val userPreferenceRepository: UserPreferenceRepository
): ViewModel() {
    private val TAG = LoginViewModel::class.simpleName
    private val _status = MutableLiveData<Boolean>()
    val status: LiveData<Boolean> = _status

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
}