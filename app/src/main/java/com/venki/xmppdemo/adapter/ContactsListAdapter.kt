package com.venki.xmppdemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.venki.xmppdemo.R
import com.venki.xmppdemo.model.Contact

class ContactsListAdapter(
    private val context: Context,
    private val contacts: MutableList<Contact>,
) : ArrayAdapter<Contact>(context, 0, contacts) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val contact = getItem(position)
        val view = convertView?: LayoutInflater.from(context).inflate(R.layout.row_contacts, null, false)

        val chatTextView = view.findViewById<TextView>(R.id.tv_contact_name)

        chatTextView.text = contact?.name
        return view
    }

    fun updateContacts(it: MutableList<Contact>?) {
        it?.let {
            contacts.clear()
            contacts.addAll(it)
            notifyDataSetChanged()
        }
    }
}