package com.example.weatherforecast.data.alert.dataSorce.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherforecast.data.alert.model.Alert
import com.example.weatherforecast.data.alert.model.AlertType
import com.example.weatherforecast.data.db.DataBaseHelper
import com.example.weatherforecast.data.db.dao.AlertDao
import io.mockk.coVerify
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlertLocalDataSourceTest {
    lateinit var alertLocalDataSource: AlertLocalDataSourceInterface
    lateinit var alertDao: AlertDao
    lateinit var db: DataBaseHelper

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            context = ApplicationProvider.getApplicationContext(),
            klass = DataBaseHelper::class.java,
        )
            .build()
        alertDao = db.alertDao()
        alertLocalDataSource = AlertLocalDataSource(alertDao)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertAlert_returnsGeneratedId() = runTest {
        // Given
        val alert = Alert()

        // When
        val result = alertLocalDataSource.insertAlert(alert)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun deleteAlert_alertNoLongerInDb() = runTest {
        // Given — insert first
        val alert = Alert(alertType = AlertType.RAIN, startTime = 0L, endTime = 1L)
        val generatedId = alertLocalDataSource.insertAlert(alert).getOrNull()!!
        val alertWithId = alert.copy(id = generatedId.toInt())

        // When
        alertLocalDataSource.deleteAlert(alertWithId)

        //Then
        val result = alertLocalDataSource.getAllAlerts().first()
        assertFalse(result.contains(alertWithId))
    }

    @Test
    fun getAllAlerts_returnsAllInsertedAlerts() = runTest {
        // Given
        val alert1 = Alert(alertType = AlertType.TEMPERATURE, startTime = 0L, endTime = 1L)
        val alert2 = Alert(alertType = AlertType.WIND, startTime = 0L, endTime = 1L)
        alertLocalDataSource.insertAlert(alert1)
        alertLocalDataSource.insertAlert(alert2)

        // When
        val result = alertLocalDataSource.getAllAlerts().first()

        // Then
        assertEquals(2, result.size)
    }
}