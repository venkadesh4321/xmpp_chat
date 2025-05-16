package com.venki.xmppdemo.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.venki.xmppdemo.repository.UserPreferenceRepository
import com.venki.xmppdemo.ui.chat.ChatActivity
import com.venki.xmppdemo.ui.login.LoginActivity
import kotlinx.coroutines.launch

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val userPreferenceRepository = UserPreferenceRepository()
            val creds = userPreferenceRepository.getCredentials(applicationContext)

            val intent = if (creds.first.isNotEmpty() && creds.second.isNotEmpty()) {
                Intent(this@SplashActivity, ChatActivity::class.java)
            } else {
                Intent(this@SplashActivity, LoginActivity::class.java)
            }

            startActivity(intent)
            finish()
        }
    }
}