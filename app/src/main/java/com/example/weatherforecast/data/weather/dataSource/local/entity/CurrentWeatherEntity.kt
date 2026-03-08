package com.example.weatherforecast.data.weather.dataSource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_weather")
data class CurrentWeatherEntity(
    @PrimaryKey
    val id: Int = 1,

    val lat: Double,
    val lon: Double,

    val cityName: String,
    val temp: Double,
    val feelsLike: Double,
    val description: String,
    val icon: String,
    val humidity: Int,
    val windSpeed: Double,
    val pressure: Int,
    val clouds: Int,
    val sunrise: Long,
    val sunset: Long,

    val cachedAt: Long = System.currentTimeMillis()
)

data class LatLonEntity(
    val lat: Double,
    val lon: Double
)