package com.example.weatherforecast.data.weather.dataSource.remote

import com.example.weatherforecast.data.weather.model.dto.forcast.ForecastResponse
import com.example.weatherforecast.data.weather.model.dto.weather.CurrentWeatherResponse

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

