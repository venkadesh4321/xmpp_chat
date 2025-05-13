package com.venki.xmppdemo.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.datastore by preferencesDataStore(name = "user_prefs")

object UserPreferences {
    private val PREF_NAME = stringPreferencesKey("username")
    private val PREF_PASSWORD = stringPreferencesKey("password")

    suspend fun saveCredentials(context: Context, userName: String, password: String) {
        context.datastore.edit { preferences ->
            preferences[PREF_NAME] = userName
            preferences[PREF_PASSWORD] = password
        }
    }

    suspend fun getCredentials(context: Context): Pair<String, String> {
        val preferences = context.datastore.data.first()
        val userName = preferences[PREF_NAME] ?: ""
        val password = preferences[PREF_PASSWORD] ?: ""
        return Pair(userName, password)
    }

    suspend fun clearCredentials(context: Context) {
        context.datastore.edit { preferences ->
            preferences.remove(PREF_NAME)
            preferences.remove(PREF_PASSWORD)
        }
    }
}