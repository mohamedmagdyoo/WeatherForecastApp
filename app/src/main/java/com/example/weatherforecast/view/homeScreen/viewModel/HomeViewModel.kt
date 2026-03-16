package com.example.weatherforecast.view.homeScreen.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.data.appPreferences.AppPreferences
import com.example.weatherforecast.data.appPreferences.util.UserSettings
import com.example.weatherforecast.data.weather.WeatherRepo
import com.example.weatherforecast.data.weather.WeatherRepoInterface
import com.example.weatherforecast.data.weather.model.entity.CurrentWeatherEntity
import com.example.weatherforecast.data.weather.model.dto.forcast.ForecastResult
import com.example.weatherforecast.utils.AppConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeViewModel(
    private val weatherRepo: WeatherRepoInterface,
    private val appPreferences: AppPreferences
) : ViewModel() {
    private val _screenState = MutableStateFlow<HomeScreenState>(HomeScreenState.Error)
    val screenState = _screenState.asStateFlow()


    init {
        viewModelScope.launch {
            // here i collect any change with the setting even the location change
            appPreferences.settingsChanged.collect { newSettings ->
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

        Log.d(AppConstants.TAG, " refresh... lat:$lat lon:$lon lang:$lang unit:$unit")
        val result = weatherRepo.refreshWeatherData(lat, lon, unit, lang)
        if (result.isFailure) {
            Log.d(AppConstants.TAG, "refresh failed: ${result.exceptionOrNull()}")
            _screenState.value = HomeScreenState.Error
        }
    }

    fun checkIfRefreshNeeded(): Boolean {
        val locationMethod = appPreferences.getLocationMethod()
        return true
    }

    private fun getScreenData() {
        viewModelScope.launch {
            combine(
                weatherRepo.getCurrentWeather(),
                weatherRepo.getForecast()
            ) { currentWeather, forecast ->
                if (currentWeather != null) {
                    convertWindValueIfNeed(currentWeather)
                    HomeScreenState.Success(currentWeather, forecast)
                } else {
                    Log.d(AppConstants.TAG, "getScreenData: DB Empty")
                    HomeScreenState.Loading
                }
            }.catch { ex ->
                Log.d(AppConstants.TAG, "getScreenDataEx: ${ex.message}")
                Log.d(AppConstants.TAG, "getScreenData: DB Empty")
                _screenState.value = HomeScreenState.Error
            }.collect { state ->
                _screenState.value = state
            }
        }
    }


    private fun convertWindValueIfNeed(currentWeather: CurrentWeatherEntity) {

        val windUnit = appPreferences.getWindUnit()      // what user wants
        val tempUnit = appPreferences.getTempUnit()      // what API returned

        val apiReturnedImperial = tempUnit == "imperial"
        val userWantsImperial = windUnit == "imperial"

        currentWeather.windSpeed = when {

            // API mph -> user wants m/s
            apiReturnedImperial && !userWantsImperial ->
                currentWeather.windSpeed / 2.23694

            // API m/s -> user wants mph
            !apiReturnedImperial && userWantsImperial ->
                currentWeather.windSpeed * 2.23694

            else -> currentWeather.windSpeed
        }
    }

    fun enforceRefresh() {
        viewModelScope.launch {
            val settings = appPreferences.getUserSettings()

            refreshData(settings)
        }
    }
}

sealed class HomeScreenState {
    object Loading : HomeScreenState()
    data class Success(
        val currentWeather: CurrentWeatherEntity,
        val forecastResult: ForecastResult
    ) : HomeScreenState()

    object Error : HomeScreenState()

}

@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(val weatherRepo: WeatherRepo, val appPreferences: AppPreferences) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(weatherRepo, appPreferences) as T
    }
}