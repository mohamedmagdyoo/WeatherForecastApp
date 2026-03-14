package com.example.weatherforecast.data.alert.dataSorce.local

import com.example.weatherforecast.data.alert.model.Alert
import com.example.weatherforecast.data.db.dao.AlertDao
import kotlinx.coroutines.flow.Flow

class AlertLocalDataSource(private val alertDao: AlertDao) : AlertLocalDataSourceInterface {
    override suspend fun insertAlert(alert: Alert): Result<Unit> {
        try {
            alertDao.insertAlert(alert)
        } catch (ex: Exception) {
            return Result.failure(ex)
        }
        return Result.success(Unit)
    }

    override suspend fun deleteAlert(alert: Alert): Result<Unit> {
        try {
            alertDao.deleteAlert(alert)
        } catch (ex: Exception) {
            return Result.failure(ex)
        }
        return Result.success(Unit)
    }

    override fun getAllAlerts(): Flow<List<Alert>> {
        return alertDao.getAllAlerts()
    }

    override suspend fun updateAlertActivation(
        alertId: Int,
        isActive: Boolean
    ): Result<Unit> {
        try {
            alertDao.updateAlertActivation(alertId, isActive)
            return Result.success(Unit)
        } catch (ex: Exception) {
            return Result.failure(ex)
        }
    }

    override suspend fun getAlertById(alertId: Int): Result<Alert> {
        try {
            val alert =
                alertDao.getAlertById(alertId = alertId)
                    ?: return Result.failure(Exception("No data"))
            return Result.success(alert)
        } catch (ex: Exception) {
            return Result.failure(ex)
        }
    }
}