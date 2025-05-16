package com.venki.xmppdemo.ui.contacts

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.venki.xmppdemo.R
import com.venki.xmppdemo.repository.UserPreferenceRepository
import com.venki.xmppdemo.repository.XmppRepository
import kotlinx.coroutines.launch

class ContactsActivity: ComponentActivity() {
    private val TAG = ContactsActivity::class.simpleName
    private lateinit var contactsViewModel: ContactsViewModel

    private lateinit var contactListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        initView()
        initViewModelAndObserver()
    }

    private fun initViewModelAndObserver() {
        val xmppRepository = XmppRepository()
        val userPreferenceRepository = UserPreferenceRepository(this)
        val contactsViewModelFactory = ContactsViewModelFactory(xmppRepository, userPreferenceRepository)
        contactsViewModel = ViewModelProvider(this, contactsViewModelFactory)
            .get(ContactsViewModel::class.java)

        lifecycleScope.launch {
            contactsViewModel.getContacts()
        }

        contactsViewModel.contacts.observe(this) { contacts ->
            val contactsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, contacts)
            contactListView.adapter = contactsAdapter
            contactsAdapter.notifyDataSetChanged()
        }
    }

    private fun initView() {
        contactListView = findViewById(R.id.ltv_contacts)
    }
}