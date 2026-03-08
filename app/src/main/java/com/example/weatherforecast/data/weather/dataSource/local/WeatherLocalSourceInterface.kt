package com.example.weatherforecast.data.weather.dataSource.local

import com.example.weatherforecast.data.weather.dataSource.local.entity.CurrentWeatherEntity
import com.example.weatherforecast.data.weather.dataSource.local.entity.ForecastEntity
import com.example.weatherforecast.data.weather.dataSource.local.entity.LatLonEntity
import kotlinx.coroutines.flow.Flow

interface WeatherLocalSourceInterface {
    // Current Weather
    fun getCurrentWeather(): Flow<CurrentWeatherEntity?>

    suspend fun insertCurrentWeather(entity: CurrentWeatherEntity)

    suspend fun getLastCurrentWeatherCachedAt(): Long?

    // Forecast
    fun getForecast(): Flow<List<ForecastEntity>>

    suspend fun insertForecasts(entities: List<ForecastEntity>)

    suspend fun getLastForecastCachedAt(lat: Double, lon: Double): Long?
    suspend fun getSavedLatLon(): Result<LatLonEntity>?

}