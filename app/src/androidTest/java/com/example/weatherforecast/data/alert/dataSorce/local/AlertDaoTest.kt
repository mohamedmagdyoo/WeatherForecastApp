package com.example.weatherforecast.data.alert.dataSorce.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherforecast.data.alert.model.Alert
import com.example.weatherforecast.data.alert.model.AlertType
import com.example.weatherforecast.data.db.DataBaseHelper
import com.example.weatherforecast.data.db.dao.AlertDao
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlertDaoTest {

    lateinit var db: DataBaseHelper
    lateinit var alertDao: AlertDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            context = ApplicationProvider.getApplicationContext(),
            klass = DataBaseHelper::class.java
        ).build()

        alertDao = db.alertDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertAlert_returnsGeneratedId() = runTest {
        // Given
        val alert = Alert(alertType = AlertType.TEMPERATURE, startTime = 0L, endTime = 1L)

        // When
        val generatedId = alertDao.insertAlert(alert)

        // Then
        assertTrue(generatedId > 0)
    }

    @Test
    fun deleteAlert_alertNoLongerInDb() = runTest {
        // Given — insert first to get real id
        val alert = Alert()
        val generatedId = alertDao.insertAlert(alert)
        val alertWithId = alert.copy(id = generatedId.toInt())

        // When
        alertDao.deleteAlert(alertWithId)

        // Then
        val result = alertDao.getAllAlerts().first()
        assertTrue(result.isEmpty())
    }

    @Test
    fun getAllAlerts_returnsAllInsertedAlerts() = runTest {
        // Given
        val alert1 = Alert(alertType = AlertType.TEMPERATURE, startTime = 0L, endTime = 1L)
        val alert2 = Alert(alertType = AlertType.WIND, startTime = 0L, endTime = 1L)
        alertDao.insertAlert(alert1)
        alertDao.insertAlert(alert2)

        // When
        val result = alertDao.getAllAlerts().first()

        // Then
        assertEquals(2, result.size)
    }
}
