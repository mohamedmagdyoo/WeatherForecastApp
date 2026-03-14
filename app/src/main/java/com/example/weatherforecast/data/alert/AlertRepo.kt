package com.example.weatherforecast.data.alert

import com.example.weatherforecast.data.alert.dataSorce.local.AlertLocalDataSource
import com.example.weatherforecast.data.alert.model.Alert
import kotlinx.coroutines.flow.Flow

class AlertRepo(private val localDataSource: AlertLocalDataSource) : AlertRepoInterface {
    override suspend fun insertAlert(alert: Alert): Result<Unit> {
        return localDataSource.insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: Alert): Result<Unit> {
        return localDataSource.deleteAlert(alert)
    }

    override fun getAllAlerts(): Flow<List<Alert>> {
        return localDataSource.getAllAlerts()
    }

    override suspend fun updateAlertActivation(
        alertId: Int,
        isActive: Boolean
    ): Result<Unit> {
        return localDataSource.updateAlertActivation(alertId, isActive)
    }

    override suspend fun getAlertById(alertId: Int): Result<Alert> {
        return localDataSource.getAlertById(alertId)
    }
}