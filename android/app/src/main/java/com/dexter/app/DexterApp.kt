package com.dexter.app

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DexterApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val original = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("DEXTER_CRASH", "=== CRASH en hilo: ${thread.name} ===", throwable)
            original?.uncaughtException(thread, throwable)
        }
    }
}
