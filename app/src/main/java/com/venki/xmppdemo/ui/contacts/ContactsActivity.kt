package com.venki.xmppdemo.ui.contacts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.venki.xmppdemo.R
import com.venki.xmppdemo.adapter.ChatListAdapter
import com.venki.xmppdemo.adapter.ContactsAdapter
import com.venki.xmppdemo.adapter.ContactsListAdapter
import com.venki.xmppdemo.model.Contact
import com.venki.xmppdemo.repository.UserPreferenceRepository
import com.venki.xmppdemo.repository.XmppRepository
import com.venki.xmppdemo.ui.chat.ChatActivity
import kotlinx.coroutines.launch

class ContactsActivity: ComponentActivity() {
    private val TAG = ContactsActivity::class.simpleName
    private lateinit var contactsViewModel: ContactsViewModel

    private lateinit var contactRecyclerView: RecyclerView
    private lateinit var contactsAdapter: ContactsAdapter
    private val contacts: MutableList<Contact> = mutableListOf()

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

        contactsViewModel.getContacts()

        contactsViewModel.contact.observe(this) { it ->
            contacts.clear()
            contacts.addAll(it)
            contactsAdapter.updateContacts(it)
        }
    }

    private fun initView() {
        contactRecyclerView = findViewById(R.id.rv_contacts)

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
}