package com.example.weatherforecast.view.settingsScreen.viewModel

import androidx.annotation.StringRes
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.viewModelScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherforecast.data.appPreferences.AppPreferences
import com.example.weatherforecast.data.appPreferences.util.toLanguageApi
import com.example.weatherforecast.data.appPreferences.util.toLanguageDisplay
import com.example.weatherforecast.data.appPreferences.util.toLocationSourceDisplay
import com.example.weatherforecast.data.appPreferences.util.toTempUnitDisplay
import com.example.weatherforecast.data.appPreferences.util.toWindUnitDisplay
import com.example.weatherforecast.view.mainActivity.Screens
import com.google.android.gms.maps.model.LatLng
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Double

@OptIn(ExperimentalCoroutinesApi::class)
//@RunWith(AndroidJUnit4::class)
class SettingsViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var viewModel: SettingsViewModel
    lateinit var prefs: AppPreferences
    val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        prefs = mockk(relaxed = true)
        every { prefs.getLat() } returns 1.0
        viewModel = SettingsViewModel(prefs)
    }

    @After
    fun cleanUp() {
        Dispatchers.resetMain()
    }

    @Test
    fun onSaveLocation_LatLng_saveLocationInPrefs() {
        //Given location
        val theSelectedLatLng = LatLng(1.0, 2.0)

        //When save the location in prefs
        viewModel.onSaveLocation(theSelectedLatLng)

        //Then the location should be saved in prefs
        assertEquals(SettingsScreenState.Success, viewModel.settingsScreenState.value)
        //or
        //        verify {
//            prefs.saveLocationWithLatAndLon(theSelectedLatLng.latitude, theSelectedLatLng.longitude)
//        }
    }


    @Test
    fun setNotificationsEnabled_true_saveInPrefs() {
        //Given
        val enabled = true

        //When
        viewModel.setNotificationsEnabled(enabled)

        //Then
        assertEquals(enabled, viewModel.uiDataState.value.notificationsEnabled)
    }

    @Test
    fun setLanguage_english_saveInPrefs() {
        //Given
        val langRes = 4 //e.g the value in R.strings

        //When
        viewModel.setLanguage(langRes)

        //Then
        assertEquals(langRes, viewModel.uiDataState.value.language)
    }

}
