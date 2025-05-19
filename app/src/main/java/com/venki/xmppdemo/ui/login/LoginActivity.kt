package com.venki.xmppdemo.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.venki.xmppdemo.R
import com.venki.xmppdemo.repository.UserPreferenceRepository
import com.venki.xmppdemo.repository.XmppRepository
import com.venki.xmppdemo.ui.chat.ChatActivity
import com.venki.xmppdemo.ui.contacts.ContactsActivity
import com.venki.xmppdemo.util.LoadingOverlayView

class LoginActivity : ComponentActivity() {
    private val TAG = LoginActivity::class.simpleName

    private lateinit var userNameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginBtn: Button
    private lateinit var loginProgressBar: LoadingOverlayView
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        initViewModelAndObserver()

        loginBtn.setOnClickListener {
            val userName = userNameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (userName.isNotEmpty() && password.isNotEmpty()) {
                loginViewModel.connectAndLogin(userName, password)
            } else {
                Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initViewModelAndObserver() {
        val xmppRepository = XmppRepository()
        val userPreferenceRepository = UserPreferenceRepository(applicationContext)
        val loginViewModelFactory = LoginViewModelFactory(
            xmppRepository,
            userPreferenceRepository
        )

        loginViewModel = ViewModelProvider(this, loginViewModelFactory)
            .get(LoginViewModel::class.java)

        loginViewModel.status.observe(this) { status ->
            if (status) {
                startActivity(Intent(this, ContactsActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
            }
        }

        loginViewModel.isLoading.observe(this) {
            loginProgressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun initViews() {
        loginBtn = findViewById(R.id.login_btn)
        userNameEditText = findViewById(R.id.user_name_edit_text)
        passwordEditText = findViewById(R.id.password_edit_text)
        loginProgressBar = findViewById(R.id.loading_overlay)
    }
}