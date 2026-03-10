package com.example.weatherforecast.view.homeScreen.viewModel

import com.example.weatherforecast.data.weather.dataSource.local.entity.CurrentWeatherEntity
import com.example.weatherforecast.data.weather.dataSource.remote.dto.forcast.ForecastResult
import com.example.weatherforecast.data.weather.dataSource.remote.dto.weather.CurrentWeatherResponse

sealed class HomeScreenState {
    object Loading : HomeScreenState()
    data class Success(
        val currentWeather: CurrentWeatherEntity,
        val forecastResult: ForecastResult
    ) : HomeScreenState()

    object Error : HomeScreenState()

}