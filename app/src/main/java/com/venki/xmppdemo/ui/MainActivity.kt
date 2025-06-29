package com.venki.xmppdemo.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.venki.xmppdemo.ui.screen.VeeChatNavHostGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Surface {
                VeeChatApp()
            }
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