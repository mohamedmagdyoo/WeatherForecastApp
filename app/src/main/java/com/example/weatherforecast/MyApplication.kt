package com.example.weatherforecast

import android.app.Application
import com.example.weatherforecast.utils.di.AppContainer
import com.example.weatherforecast.utils.notification.NotificationHelper

class MyApplication : Application() {

    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannels(this)
        appContainer = AppContainer(this)
    }
}