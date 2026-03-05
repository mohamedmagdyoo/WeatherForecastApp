package com.example.weatherforecast.view.homeScreen.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.data.weather.WeatherRepo
import com.example.weatherforecast.utils.AppConstants
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay


class HomeViewModel(val weatherRepo: WeatherRepo) : ViewModel() {

    private val _screenState = MutableStateFlow<HomeScreenState>(HomeScreenState.Loading)
    val screenState = _screenState.asStateFlow()

    init {
        getScreenData()
    }

    fun getScreenData() {
        try {
            viewModelScope.launch {

                val job1 = async {
                    weatherRepo.getCurrentWeather(5.5, 5.5)
                }

                val job2 = async {
                    weatherRepo.getForecast(5.5, 5.5)
                }

                val currentWeather = job1.await()
                val forecast = job2.await()

                if (currentWeather.isSuccess && forecast.isSuccess) {
                    _screenState.value =
                        HomeScreenState.Success(currentWeather.getOrThrow(), forecast.getOrThrow())
                } else {
                    val error = currentWeather.exceptionOrNull()
                        ?: forecast.exceptionOrNull()
                    _screenState.value = HomeScreenState.Error(error?.message ?: "unknown error")
                }
            }
        } catch (ex: Exception) {
            Log.d(AppConstants.TAG, "getScreenData Exception: ${ex.message} ")
        }
    }

}

@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(val weatherRepo: WeatherRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return HomeViewModel(weatherRepo) as T
    }
}