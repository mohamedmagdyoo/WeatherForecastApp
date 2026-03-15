package com.example.weatherforecast.data.alert

import app.cash.turbine.test
import com.example.weatherforecast.data.alert.dataSorce.local.AlertLocalDataSource
import com.example.weatherforecast.data.alert.dataSorce.local.AlertLocalDataSourceInterface
import com.example.weatherforecast.data.alert.model.Alert
import com.example.weatherforecast.data.alert.model.AlertType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AlertRepoTest {

    lateinit var repo: AlertRepo
    lateinit var localDataSource: AlertLocalDataSourceInterface
    val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        localDataSource = mockk(relaxed = true)
        repo = AlertRepo(localDataSource)
    }

    @After
    fun cleanUp() {
        Dispatchers.resetMain()
    }

    @Test
    fun insertAlert_returnsSuccessWithGeneratedId() = runTest {
        // Given
        val alert = Alert()
        coEvery { localDataSource.insertAlert(alert) } returns Result.success(1L)

        // When
        val result = repo.insertAlert(alert)

        // Then
        assertEquals(Result.success(1L), result)
    }

    @Test
    fun deleteAlert_callsLocalDataSourceWithCorrectAlert() = runTest {
        // Given
        val alert = Alert()
        coEvery { localDataSource.deleteAlert(alert) } returns Result.success(Unit)

        // When
        repo.deleteAlert(alert)

        // Then
        coVerify(exactly = 1) { localDataSource.deleteAlert(alert) }
    }

    @Test
    fun getAllAlerts_returnsFlowFromLocalDataSource() = runTest {
        // Given
        val fakeAlerts = listOf(
            Alert(id = 1, alertType = AlertType.RAIN, startTime = 0L, endTime = 1L),
            Alert(id = 2, alertType = AlertType.WIND, startTime = 0L, endTime = 1L)
        )
        every { localDataSource.getAllAlerts() } returns flowOf(fakeAlerts)

        // When
        val result = repo.getAllAlerts().first()
        //Then
        assertEquals(fakeAlerts, result)

    //        Can use also
//        repo.getAllAlerts().test {
//            assertEquals(fakeAlerts, awaitItem())
//            cancelAndIgnoreRemainingEvents() // cause i just want the first value
//        }
    }
}