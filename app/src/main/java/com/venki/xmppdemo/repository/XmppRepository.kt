package com.venki.xmppdemo.repository

import com.venki.xmppdemo.data.network.XmppManager
import com.venki.xmppdemo.model.Contact

class XmppRepository {
    private val TAG = XmppRepository::class.simpleName

    suspend fun connect(): Boolean {
        XmppManager.connect()
        return XmppManager.isConnected()
    }

    suspend fun login(userName: String, password: String): Boolean {
        XmppManager.login(userName, password)
        return XmppManager.isAuthenticated()
    }

    suspend fun sendMessage(recipient: String, message: String) {
        XmppManager.sendMessage(recipient, message)
    }

   suspend fun getContacts(): MutableList<Contact> {
       return XmppManager.getRoasterEntries().toMutableList()
   }

    fun isConnected(): Boolean = XmppManager.isConnected()
    fun isAuthenticated(): Boolean = XmppManager.isAuthenticated()
}