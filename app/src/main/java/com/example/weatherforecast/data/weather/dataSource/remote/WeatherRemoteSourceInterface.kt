package com.example.weatherforecast.data.weather.dataSource.remote

import com.example.weatherforecast.data.weather.dataSource.local.entity.LatLonEntity
import com.example.weatherforecast.data.weather.dataSource.remote.dto.forcast.ForecastResponse
import com.example.weatherforecast.data.weather.dataSource.remote.dto.weather.CurrentWeatherResponse
import com.example.weatherforecast.utils.AppConstants

interface WeatherRemoteSourceInterface {
    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        unit: String,
        lang: String
    ): Result<CurrentWeatherResponse>

    suspend fun getForecast(
        lat: Double,
        lon: Double,
        unit: String,
        lang: String
    ): Result<ForecastResponse>

}

