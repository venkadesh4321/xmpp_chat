package com.venki.xmppdemo.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.venki.xmppdemo.repository.UserPreferenceRepository
import com.venki.xmppdemo.ui.JoinScreen
import com.venki.xmppdemo.ui.Routes
import com.venki.xmppdemo.ui.VeeChatNavHostGraph
import com.venki.xmppdemo.ui.chat.ChatActivity
import com.venki.xmppdemo.ui.contacts.ContactsActivity
import com.venki.xmppdemo.ui.login.LoginActivity
import com.venki.xmppdemo.ui.login.LoginScreen
import kotlinx.coroutines.launch

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            VeeChatApp()
        }

        /*lifecycleScope.launch {
            val userPreferenceRepository = UserPreferenceRepository(applicationContext)
            val creds = userPreferenceRepository.getCredentials()

            val intent = if (creds.first.isNotEmpty() && creds.second.isNotEmpty()) {
                Intent(this@SplashActivity, ContactsActivity::class.java)
            } else {
                Intent(this@SplashActivity, LoginActivity::class.java)
            }

            startActivity(intent)
            finish()
        }*/
    }

    @Composable
    fun VeeChatApp() {
        VeeChatNavHostGraph()
    }
}