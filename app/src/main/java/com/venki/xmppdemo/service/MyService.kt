package com.venki.xmppdemo.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

class MyService: Service() {
    private val TAG = MyService::class.simpleName

    private val MIN_VALUE = 0
    private val MAX_VALUE = 100
    private var randomNumberGenerator = false
    private var randomNumber = 0

    class MyServiceBinder : Binder() {
        fun getService(): MyService {
            return MyService()
        }
    }

    private val binder = MyServiceBinder()

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind")
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: ${Thread.currentThread().name}")
        randomNumberGenerator = true
        Thread {
            startRandomNumberGeneration()
        }.start()
        return super.onStartCommand(intent, flags, startId)
    }

    fun startRandomNumberGeneration() {
        Log.d(TAG, "startRandomNumberGeneration ${Thread.currentThread().name}")
        // Simulate random number generation
        while(randomNumberGenerator) {
            // Generate a random number between MIN_VALUE and MAX_VALUE
            val generatedNumber = (MIN_VALUE..MAX_VALUE).random()
            Log.d(TAG, "Generated random number: $generatedNumber")
            Thread.sleep(1000) // Simulate delay for demonstration
        }
    }

    fun stopRandomNumberGeneration() {
        Log.d(TAG, "stopRandomNumberGeneration")
        randomNumberGenerator = false
        // Logic to stop random number generation if needed
    }

    fun getRandomNumber(): Int {
        return randomNumber
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRandomNumberGeneration()
        Log.d(TAG, "onDestroy")
    }
}