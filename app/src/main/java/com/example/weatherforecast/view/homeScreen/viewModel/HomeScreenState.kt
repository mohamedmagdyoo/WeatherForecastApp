package com.example.weatherforecast.view.homeScreen.viewModel

import com.example.weatherforecast.data.weather.dataSource.remote.model.forcast.ForecastResult
import com.example.weatherforecast.data.weather.dataSource.remote.model.weather.CurrentWeatherResponse

sealed class HomeScreenState {
    object Loading : HomeScreenState()
    data class Success(
        val currentWeather: CurrentWeatherResponse,
        val forecastResult: ForecastResult
    ) : HomeScreenState()

    data class Error(val error: String) : HomeScreenState()

}