package com.example.weatherforecast.view.homeScreen.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.data.appPreferences.AppPreferences
import com.example.weatherforecast.data.appPreferences.UserSettings
import com.example.weatherforecast.data.weather.WeatherRepo
import com.example.weatherforecast.data.weather.dataSource.local.entity.LatLonEntity
import com.example.weatherforecast.utils.AppConstants
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeViewModel(
    private val weatherRepo: WeatherRepo,
    private val appPreferences: AppPreferences
) : ViewModel() {
    private val _screenState = MutableStateFlow<HomeScreenState>(HomeScreenState.Loading)
    val screenState = _screenState.asStateFlow()


    init {
        viewModelScope.launch {
            appPreferences.settingsChanged.collect { newSettings ->
                Log.d(AppConstants.TAG, "Collect new settings:${newSettings.location.lat} ")
                refreshData(newSettings)
                getScreenData()
            }
        }
    }

    private suspend fun refreshData(newSettings: UserSettings) {
        val lat = newSettings.location.lat
        val lon = newSettings.location.lon
        val lang = newSettings.language
        val unit = newSettings.unit

        Log.d(AppConstants.TAG, " ... lat:$lat lon:$lon lang:$lang unit:$unit")
        val result = weatherRepo.refreshWeatherData(lat, lon, unit, lang)
        if (result.isFailure) {
            Log.d(AppConstants.TAG, "refresh failed: ${result.exceptionOrNull()}")
            _screenState.value = HomeScreenState.Error(
                result.exceptionOrNull()?.message.toString()
            )
        }
    }

    private fun getScreenData() {
        viewModelScope.launch {
            combine(
                weatherRepo.getCurrentWeather(),
                weatherRepo.getForecast()
            ) { currentWeather, forecast ->
                if (currentWeather != null) {
                    HomeScreenState.Success(currentWeather, forecast)
                } else {
                    Log.d(AppConstants.TAG, "getScreenData: DB Empty")
                    HomeScreenState.Loading
                }
            }.catch { ex ->
                _screenState.value = HomeScreenState.Error(ex.message.toString())
            }.collect { state ->
                _screenState.value = state
            }
        }
    }

//    fun workWithMockData() {
//        viewModelScope.launch {
//            refreshData(appPreferences.getUserSettings())
//        }
//    }
}

@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(val weatherRepo: WeatherRepo, val appPreferences: AppPreferences) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(weatherRepo, appPreferences) as T
    }
}