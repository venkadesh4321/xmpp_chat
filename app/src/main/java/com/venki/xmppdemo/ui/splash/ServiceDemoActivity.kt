package com.venki.xmppdemo.ui.splash

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.venki.xmppdemo.R

class ServiceDemoActivity : AppCompatActivity() {
    private val TAG = ServiceDemoActivity::class.simpleName

    private lateinit var startService: Button
    private lateinit var stopService: Button
    private lateinit var bindService: Button
    private lateinit var unBindService: Button
    private lateinit var getRandomNo: Button
    private lateinit var randoNumberTextView: TextView

    private var isServiceBound = false
    private lateinit var serviceConnection: ServiceConnection

    private val GET_RANDOM_NUMBER = 1
    private lateinit var requestMessenger: Messenger
    private lateinit var responseMessenger: Messenger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startService = findViewById(R.id.start_service_btn)
        stopService = findViewById(R.id.stop_service_btn)
        bindService = findViewById(R.id.bind_service_btn)
        unBindService = findViewById(R.id.unbind_service_btn)
        getRandomNo = findViewById(R.id.get_random_no_btn)
        randoNumberTextView = findViewById(R.id.random_no_text_view)

        val intent = Intent().apply {
            component = ComponentName(
                "com.venki.remoteservicedemo",
                "com.venki.remoteservicedemo.MyService"
            )
        }

        startService.setOnClickListener {
            startService(intent)
        }

        stopService.setOnClickListener {
            stopService(intent)
        }

        bindService.setOnClickListener {
            Log.d(TAG, "onCreate: $isServiceBound")
            if (!isServiceBound) {
                serviceConnection = object : ServiceConnection {
                    override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                        Log.d(TAG, "Service Connected")
                        requestMessenger = Messenger(binder)

                        // ✅ Create response Messenger
                        val handler = Handler(Looper.getMainLooper()) { msg ->
                            if (msg.what == GET_RANDOM_NUMBER) {
                                val randomNumber = msg.arg1
                                Log.d(TAG, "Received Random Number: $randomNumber")
                                randoNumberTextView.text = randomNumber.toString()
                                true
                            } else false
                        }
                        responseMessenger = Messenger(handler)

                        isServiceBound = true
                    }

                    override fun onServiceDisconnected(name: ComponentName?) {
                        Log.d(TAG, "Service Disconnected")
                        isServiceBound = false
                    }
                }

                bindService(intent, serviceConnection, BIND_AUTO_CREATE)
            }
        }

        unBindService.setOnClickListener {
            if (isServiceBound) {
                unbindService(serviceConnection)
                isServiceBound = false
            }
        }

        getRandomNo.setOnClickListener {
            if (isServiceBound) {
                val msg = Message.obtain(null, GET_RANDOM_NUMBER)
                msg.replyTo = responseMessenger
                try {
                    requestMessenger.send(msg)
                } catch (e: Exception) {
                    Log.e(TAG, "Error sending message to service", e)
                }
            } else {
                randoNumberTextView.text = "Service is not bound"
            }
        }
    }
}
