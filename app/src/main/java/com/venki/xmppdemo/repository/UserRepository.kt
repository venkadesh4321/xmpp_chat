package com.venki.xmppdemo.repository

import android.content.Context
import com.venki.xmppdemo.data.local.UserPreferences

class UserPreferenceRepository(
    private val context: Context
) {
    suspend fun saveCredentials(userName: String, password: String) =
        UserPreferences.saveCredentials(context, userName, password)

    suspend fun getCredentials(): Pair<String, String> =
        UserPreferences.getCredentials(context)

    suspend fun clearCredentials() =
        UserPreferences.clearCredentials(context)

    suspend fun isLoggedIn(): Boolean {
        val pair = UserPreferences.getCredentials(context)
        return pair.first.isNotEmpty() && pair.second.isNotEmpty()
    }
}