package com.example.weatherforecast.data.alert

import com.example.weatherforecast.data.alert.model.Alert
import kotlinx.coroutines.flow.Flow

interface AlertRepoInterface {

    suspend fun insertAlert(alert: Alert): Result<Unit>
    suspend fun deleteAlert(alert: Alert): Result<Unit>
    fun getAllAlerts(): Flow<List<Alert>>
    suspend fun updateAlertActivation(alertId: Int, isActive: Boolean): Result<Unit>
    suspend fun getAlertById(alertId: Int): Result<Alert>


}
