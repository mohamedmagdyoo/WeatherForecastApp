package com.example.weatherforecast.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.weatherforecast.data.alert.model.Alert
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {
    @Insert
    suspend fun insertAlert(alert: Alert): Long

    @Delete
    suspend fun deleteAlert(alert: Alert)

    @Query("SELECT * FROM alerts")
    fun getAllAlerts(): Flow<List<Alert>>

    @Query("UPDATE alerts SET isActive = :isActive WHERE id = :alertId")
    suspend fun updateAlertActivation(alertId: Int, isActive: Boolean)

    @Query("SELECT * FROM alerts WHERE id = :alertId")
    suspend fun getAlertById(alertId: Long): Alert?
}
