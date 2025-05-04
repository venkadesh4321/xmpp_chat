package com.venki.xmppdemo

import android.content.Context
import android.util.Log
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

fun getCustomSslContext(context: Context): SSLContext {
    val cf = java.security.cert.CertificateFactory.getInstance("X.509")
    val caInput: InputStream = context.resources.openRawResource(R.raw.my_cert)
    val ca = cf.generateCertificate(caInput)
    Log.d("SSL", "Loaded CA: ${(ca as X509Certificate).subjectDN}")
    caInput.close()

    // Create a KeyStore containing our trusted CAs
    val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
    keyStore.load(null, null)
    keyStore.setCertificateEntry("ca", ca)

    // Create a TrustManager that trusts the CAs in our KeyStore
    val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
    tmf.init(keyStore)

    // Create an SSLContext that uses our TrustManager
    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, tmf.trustManagers, null)
    return sslContext
}
