package com.venki.xmppdemo.ui.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.venki.xmppdemo.R
import com.venki.xmppdemo.service.MyService

class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.simpleName
    private lateinit var startService: Button
    private lateinit var stopService: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate: ${Thread.currentThread().name}")
        startService = findViewById(R.id.start_service_btn)
        stopService = findViewById(R.id.stop_service_btn)

        val intent = Intent(this, MyService::class.java)
        startService.setOnClickListener {
            startService(intent)
        }

        stopService.setOnClickListener {
            stopService(intent)
        }
    }
}