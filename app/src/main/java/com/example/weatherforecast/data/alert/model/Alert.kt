package com.example.weatherforecast.data.alert.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "alerts")
data class Alert(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val alertType: AlertType = AlertType.TEMPERATURE,
    val alertValue: Double? = null,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long = System.currentTimeMillis() + 3_600_000L, // after an hour
    val alarmKind: AlarmKind = AlarmKind.NOTIFICATION,
    val isActive: Boolean = true
)

enum class AlertType(val displayName: String, val unit: String) {
    TEMPERATURE("Temperature", "°C"),
    WIND("Wind", "m/s"),
    RAIN("Rain", "");

}

enum class AlarmKind(val displayName: String) {
    NOTIFICATION("Notification"),
    SOUND("Alarm Sound")
}

fun Long.toDateString(): String =
    SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(this))