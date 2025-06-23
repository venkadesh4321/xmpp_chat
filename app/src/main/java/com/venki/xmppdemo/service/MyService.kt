package com.venki.xmppdemo.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

class MyService : Service() {
    private val TAG = MyService::class.simpleName

    private val MIN_VALUE = 0
    private val MAX_VALUE = 100
    private var randomNumberGenerator = false
    private var randomNumber: Int = 0

    inner class MyServiceBinder : Binder() {
        fun getService(): MyService = this@MyService
    }

    private val binder: IBinder = MyServiceBinder()

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
        return START_STICKY
    }

    fun startRandomNumberGeneration() {
        Log.d(TAG, "startRandomNumberGeneration ${Thread.currentThread().name}")
        // Simulate random number generation
        while (randomNumberGenerator) {
            try {
                Thread.sleep(1000) // Simulate delay for demonstration
                if (randomNumberGenerator) {
                    // Generate a random number between MIN_VALUE and MAX_VALUE
                    randomNumber = (MIN_VALUE..MAX_VALUE).random()
                    Log.d(TAG, "Generated random number: $randomNumber")
                }
            } catch (e: InterruptedException) {
                Log.e(TAG, "Thread interrupted", e)
            }
        }
    }

    fun stopRandomNumberGeneration() {
        Log.d(TAG, "stopRandomNumberGeneration")
        randomNumberGenerator = false
        // Logic to stop random number generation if needed
    }

    fun getRandomNumber(): Int {
        Log.d(TAG, "getRandomNumber: ${Thread.currentThread().name}")
        Log.d(TAG, "getRandomNumber: $randomNumber")
        return randomNumber
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRandomNumberGeneration()
        Log.d(TAG, "onDestroy")
    }
}