package com.venki.xmppdemo.ui.splash

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.venki.xmppdemo.R
import com.venki.xmppdemo.service.MyService

class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.simpleName

    private lateinit var startService: Button
    private lateinit var stopService: Button
    private lateinit var bindService: Button
    private lateinit var unBindService: Button
    private lateinit var getRandomNo: Button
    private lateinit var randoNumberTextView: TextView

    private lateinit var myService: MyService
    private var isServiceBound = false
    private lateinit var serviceConnection: ServiceConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate: ${Thread.currentThread().name}")
        startService = findViewById(R.id.start_service_btn)
        stopService = findViewById(R.id.stop_service_btn)
        bindService = findViewById(R.id.bind_service_btn)
        unBindService = findViewById(R.id.unbind_service_btn)
        getRandomNo = findViewById(R.id.get_random_no_btn)
        randoNumberTextView = findViewById(R.id.random_no_text_view)

        val intent = Intent(this, MyService::class.java)
        startService.setOnClickListener {
            startService(intent)
        }

        stopService.setOnClickListener {
            stopService(intent)
        }

        bindService.setOnClickListener {
            if (!isServiceBound) {
                serviceConnection = object : ServiceConnection {
                    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                        Log.d(TAG, "Service Connected")
                        val binder = service as MyService.MyServiceBinder
                        myService = binder.getService()
                        Log.d(TAG, "onServiceConnected: " + myService)
                    }

                    override fun onServiceDisconnected(name: ComponentName?) {
                        Log.d(TAG, "Service Disconnected")
                        isServiceBound = false
                    }
                }
                bindService(intent, serviceConnection, BIND_AUTO_CREATE)
                isServiceBound = true
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
                val randomNumber = myService.getRandomNumber()
                Log.d(TAG, "Random Number: $randomNumber")
                randoNumberTextView.text = randomNumber.toString()
            } else {
                Log.d(TAG, "Service is not bound")
                randoNumberTextView.text = "Service is not bound"
            }
        }
    }
}