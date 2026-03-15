package com.example.weatherforecast.data.weather.dataSource.remote

import com.example.weatherforecast.data.network.service.WeatherApiService
import com.example.weatherforecast.data.weather.model.dto.forcast.ForecastResponse
import com.example.weatherforecast.data.weather.model.dto.weather.CurrentWeatherResponse
import com.example.weatherforecast.utils.AppConstants

class WeatherRemoteSource(private val weatherApiService: WeatherApiService) : WeatherRemoteSourceInterface {
    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        unit: String,
        lang: String
    ): Result<CurrentWeatherResponse> {
        val response = weatherApiService.getCurrentWeather(
            lat = lat,
            lon = lon,
            unit = unit,
            lang = lang,
            apiKey = AppConstants.API_KEY
        )

        if (response.isSuccessful) {
            val data = response.body()
                ?: return Result.failure(IllegalStateException("Empty Data"))

            return Result.success(data)
        } else {
            return Result.failure(IllegalStateException("Call failed error code:${response.code()}"))
        }
    }

    override suspend fun getForecast(
        lat: Double,
        lon: Double,
        unit: String,
        lang: String
    ): Result<ForecastResponse> {
        val response = weatherApiService.getForecast(
            lat = lat,
            lon = lon,
            unit = unit,
            lang = lang,
            apiKey = AppConstants.API_KEY
        )

        if (response.isSuccessful) {
            val data = response.body()
                ?: return Result.failure(IllegalStateException("Empty Data"))

            return Result.success(data)
        } else {
            return Result.failure(IllegalStateException("Call failed error code:${response.code()}"))

        }
    }

}