package com.example.weatherforecast.view.alertScreens.workManager

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weatherforecast.data.alert.model.AlarmKind
import com.example.weatherforecast.data.alert.model.AlertType.RAIN
import com.example.weatherforecast.data.alert.model.AlertType.TEMPERATURE
import com.example.weatherforecast.data.alert.model.AlertType.WIND
import com.example.weatherforecast.MyApplication
import com.example.weatherforecast.utils.AppConstants
import com.example.weatherforecast.utils.notification.NotificationHelper
import kotlinx.coroutines.flow.first

class AlarmWorker(val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    private val appContainer = (appContext as MyApplication).appContainer
    private val weatherRepo = appContainer.weatherRepo
    private val alertRepo = appContainer.alertRepo

    override suspend fun doWork(): Result {
        Log.d(AppConstants.TAG, "AlarmWorker: start doWork")
        val alertId = inputData.getLong("alertId", -1)
        if (alertId == -1.toLong()) {
            Log.d(AppConstants.TAG, "AlarmWorker: problem with send the alarm id")
            return Result.failure()
        }

        val alert = alertRepo.getAlertById(alertId).getOrNull()

        if (alert == null) {
            Log.d(AppConstants.TAG, "AlarmWorker: no alarm with id: $alertId in db")
            return Result.failure()
        }

        if (!alert.isActive) {
            Log.d(AppConstants.TAG, "AlarmWorker: alarm is not active")
            return Result.failure()
        }

        val currentWeather = weatherRepo.getCurrentWeather().first()
        if (currentWeather == null) {
            Log.d(AppConstants.TAG, "AlarmWorker: no current weather")
            return Result.failure()
        }

        val conditionMet = when (alert.alertType) {
            TEMPERATURE -> currentWeather.temp >= (alert.alertValue ?: 0.0)
            WIND -> currentWeather.windSpeed >= (alert.alertValue ?: 0.0)
            RAIN -> currentWeather.clouds >= 85
        }

        if (conditionMet) {
            Log.d(AppConstants.TAG, "AlarmWorker: tryPushNotification")

            when (alert.alarmKind) {
                AlarmKind.NOTIFICATION -> NotificationHelper.sendNotification(appContext, alert)
                AlarmKind.SOUND -> NotificationHelper.sendNotificationWithSound(appContext, alert)
            }
        } else {
            Log.d(AppConstants.TAG, "AlarmWorker: condition not met")
        }
        alertRepo.deleteAlert(alert)
        return Result.success()
    }
}