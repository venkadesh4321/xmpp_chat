package com.venki.xmppdemo.data.network

sealed class XmppConnectionState {
    object Idle : XmppConnectionState()
    object Connecting : XmppConnectionState()
    object Connected : XmppConnectionState()
    object Authenticated : XmppConnectionState()
    object Disconnected : XmppConnectionState()
    data class Error(val message: String) : XmppConnectionState()
}