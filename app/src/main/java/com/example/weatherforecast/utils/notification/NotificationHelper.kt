package com.example.weatherforecast.utils.notification


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.labs.R
import com.example.weatherforecast.data.alert.model.Alert
import com.example.weatherforecast.data.alert.model.AlertType

object NotificationHelper {

    private const val CHANNEL_ID_SILENT = "weather_alert_silent"
    private const val CHANNEL_NAME_SILENT = "Weather Alerts"
    private const val CHANNEL_ID_SOUND = "weather_alert_sound"
    private const val CHANNEL_NAME_SOUND = "Weather Alerts With Sound"

    fun createNotificationChannels(appContext: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = appContext.getSystemService(NotificationManager::class.java)

            // silent channel
            val silentChannel = NotificationChannel(
                CHANNEL_ID_SILENT,
                CHANNEL_NAME_SILENT,
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "Weather alert notifications"
                setSound(null, null)
            }

            // sound channel
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM) // can across don't disturb mode
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val soundChannel = NotificationChannel(
                CHANNEL_ID_SOUND,
                CHANNEL_NAME_SOUND,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Weather alert notifications with sound"
                setSound(soundUri, audioAttributes)
                enableVibration(true)
            }

            manager.createNotificationChannel(silentChannel)
            manager.createNotificationChannel(soundChannel)
        }
    }

    fun sendNotification(context: Context, alert: Alert) {
        val manager = context.getSystemService(NotificationManager::class.java)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_SILENT)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle(buildTitle(alert))
            .setContentText(buildMessage(alert))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        manager.notify(alert.id, notification)
    }

    fun sendNotificationWithSound(context: Context, alert: Alert) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_SOUND)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle(buildTitle(alert))
            .setContentText(buildMessage(alert))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .setAutoCancel(true)
            .build()

        manager.notify(alert.id, notification)
    }

    private fun buildTitle(alert: Alert): String {
        return when (alert.alertType) {
            AlertType.TEMPERATURE -> "🌡️ Temperature Alert"
            AlertType.WIND -> "💨 Wind Alert"
            AlertType.RAIN -> "🌧️ Rain Alert"
        }
    }

    private fun buildMessage(alert: Alert): String {
        return when (alert.alertType) {
            AlertType.TEMPERATURE -> "Temperature has reached ${alert.alertValue}°C"
            AlertType.WIND -> "Wind speed has reached ${alert.alertValue} m/s"
            AlertType.RAIN -> "Rain is expected in your area"
        }
    }
}