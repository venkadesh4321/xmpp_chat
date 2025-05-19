package com.venki.xmppdemo.ui.contacts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toolbar
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.venki.xmppdemo.R
import com.venki.xmppdemo.adapter.ContactsAdapter
import com.venki.xmppdemo.model.Contact
import com.venki.xmppdemo.repository.UserPreferenceRepository
import com.venki.xmppdemo.repository.XmppRepository
import com.venki.xmppdemo.ui.chat.ChatActivity
import com.venki.xmppdemo.ui.login.LoginActivity
import com.venki.xmppdemo.util.LoadingOverlayView
import kotlinx.coroutines.launch

class ContactsActivity: AppCompatActivity() {
    private val TAG = ContactsActivity::class.simpleName

    private lateinit var contactRecyclerView: RecyclerView
    private lateinit var contactsAdapter: ContactsAdapter
    private val contacts: MutableList<Contact> = mutableListOf()
    private lateinit var contactsViewModel: ContactsViewModel
    private lateinit var loadingProgress: LoadingOverlayView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        initView()
        initViewModelAndObserver()
    }

    private fun initViewModelAndObserver() {
        val xmppRepository = XmppRepository()
        val contactsViewModelFactory = ContactsViewModelFactory(xmppRepository)
        contactsViewModel = ViewModelProvider(this, contactsViewModelFactory)
            .get(ContactsViewModel::class.java)

        contactsViewModel.contact.observe(this) { it ->
            contacts.clear()
            contacts.addAll(it)
            contactsAdapter.updateContacts(it)
        }

        contactsViewModel.isLoading.observe(this) {
            if (it) loadingProgress.show() else loadingProgress.hide()
        }

        contactsViewModel.getContacts()
    }

    private fun initView() {
        contactRecyclerView = findViewById(R.id.rv_contacts)
        loadingProgress = findViewById(R.id.loading_overlay)

        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.title = "Contacts"

        contactsAdapter = ContactsAdapter(mutableListOf(), {
            Log.d(TAG, "Selected contact: $it")
            val intent = Intent(this, ChatActivity::class.java).apply {
                putExtra("recipient", it.name)
                putExtra("jid", it.jid)
            }
            startActivity(intent)
        })
        val linearLayout = LinearLayoutManager(this)
        contactRecyclerView.layoutManager = linearLayout
        contactRecyclerView.adapter = contactsAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_contacts, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                contactsViewModel.getContacts()
                true
            }
            R.id.action_logout -> {
                val userPreferenceRepository = UserPreferenceRepository(this)
                lifecycleScope.launch {
                    userPreferenceRepository.clearCredentials()
                }

                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}