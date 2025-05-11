package com.venki.xmppdemo.repository

import android.content.Context
import com.venki.xmppdemo.network.UserPreferences

class UserPreferenceRepository {
    suspend fun saveCredentials(context: Context, userName: String, password: String) =
        UserPreferences.saveCredentials(context, userName, password)

    suspend fun getCredentials(context: Context): Pair<String, String> =
        UserPreferences.getCredentials(context)

    suspend fun clearCredentials(context: Context) =
        UserPreferences.clearCredentials(context)
}