package com.example.turbo

import android.app.Application
import com.mazenrashed.printooth.Printooth
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TurboApp:Application() {

    override fun onCreate() {
        super.onCreate()
        Printooth.init(this)
    }
}