package com.example.weatherforecast.view.alertScreens.workManager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weatherforecast.data.alert.AlertRepo
import com.example.weatherforecast.utils.di.MyApplication

class AlarmWorker(appContext: Context, workerParams: WorkerParameters, val repo: AlertRepo) :
    CoroutineWorker(appContext, workerParams) {
    val appContainer = (appContext as MyApplication).appContainer

    override suspend fun doWork(): Result {
        val weatherRepo = appContainer.weatherRepo

        TODO("Not yet implemented")
    }
}