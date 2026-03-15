package com.example.weatherforecast.data.alert.dataSorce.local

import com.example.weatherforecast.data.alert.model.Alert
import kotlinx.coroutines.flow.Flow

interface AlertLocalDataSourceInterface {
    suspend fun insertAlert(alert: Alert): Result<Long>
    suspend fun deleteAlert(alert: Alert): Result<Unit>
    fun getAllAlerts(): Flow<List<Alert>>
    suspend fun updateAlertActivation(alertId: Int, isActive: Boolean): Result<Unit>
    suspend fun getAlertById(alertId: Long): Result<Alert>
}

