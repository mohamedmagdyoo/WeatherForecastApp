package com.example.weatherforecast.view.alertScreens.alertScreen.viewModel

import com.example.weatherforecast.data.alert.AlertRepoInterface
import com.example.weatherforecast.data.alert.model.Alert
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)

class AlertViewModelTest {
    lateinit var alertViewModelTest: AlertViewModel
    lateinit var alertRepo: AlertRepoInterface
    val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        alertRepo = mockk()
        //here every with each method called from repo
        every { alertRepo.getAllAlerts() } returns flowOf(emptyList())
        alertViewModelTest = AlertViewModel(alertRepo)
    }

    @After
    fun cleanUp() {
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun deleteAlert_alert_useRepoToDeleteIt() {
        //Given
        val alert = Alert()

        //When
        alertViewModelTest.deleteAlert(alert)

        //Then
        coVerify {
            alertRepo.deleteAlert(alert = alert)
        }
    }

    @Test
    fun updateAlertActivation_alertId_isActive_useRepoToUpdateIt() {
        //Given
        val alertId = 1
        val isActive = true

        //When
        alertViewModelTest.updateAlertActivation(alertId, isActive)

        //Then
        coVerify {
            alertRepo.updateAlertActivation(alertId, isActive)
        }
    }


    @Test
    fun onDeniedPermeation_tellScreenThePermationDenied() {
        //When
        alertViewModelTest.onDeniedPermeation()
        //Then
        assertEquals(AlertState.OnDenied, alertViewModelTest.screenState.value)
    }
}

