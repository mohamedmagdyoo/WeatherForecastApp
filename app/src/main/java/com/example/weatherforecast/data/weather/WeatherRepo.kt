package com.example.weatherforecast.data.weather

import com.example.weatherforecast.data.weather.dataSource.remote.WeatherRemoteDataSource
import com.example.weatherforecast.data.weather.dataSource.remote.model.forcast.ForecastResponse
import com.example.weatherforecast.data.weather.dataSource.remote.model.weather.CurrentWeatherResponse

class WeatherRepo {
    val remote = WeatherRemoteDataSource()

    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        unit: String = "metric",
        lang: String = "en"
    ): Result<CurrentWeatherResponse> {
        return remote.getCurrentWeather(
            lat = lat,
            lon = lon,
            unit = unit,
            lang = lang,
        )
    }

    suspend fun getForecast(
        lat: Double,
        lon: Double,
        unit: String = "metric",
        lang: String = "en"
    ): Result<ForecastResponse> {
        return remote.getForecast(
            lat = lat,
            lon = lon,
            unit = unit,
            lang = lang,
        )
    }
}