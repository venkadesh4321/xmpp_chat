package com.venki.xmppdemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {
    private val TAG = LoginActivity::class.simpleName

    private lateinit var submitBtn: Button
    private lateinit var userNameEditText: EditText
    private lateinit var passwordEditText: EditText
    private val xmppManager = XmppHandler(
        "siruthuli.duckdns.org",
        "3.111.47.160", // or IP
        5222,
        this
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        submitBtn = findViewById(R.id.submit_btn)
        userNameEditText = findViewById(R.id.user_name_edit_text)
        passwordEditText = findViewById(R.id.password_edit_text)

        submitBtn.setOnClickListener {
            val userName = userNameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (userName.isNotEmpty() && password.isNotEmpty()) {
                Log.d(TAG, "onCreate: user name - $userName password - $password")
                lifecycleScope.launch {
                    val connected = xmppManager.connect(userName, password)
                    if (connected) {
                        Log.d(TAG, "onCreate: connected")
                        Toast.makeText(this@LoginActivity, "Connected", Toast.LENGTH_SHORT).show()
                        // Navigate to chat activity
                        startActivity(Intent(this@LoginActivity, ChatActivity::class.java))
                    } else {
                        Log.d(TAG, "onCreate: not connected")
                        Toast.makeText(this@LoginActivity, "Connection failed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {
                Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}