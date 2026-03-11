package com.example.weatherforecast.data.weather.dataSource.local.entity

import androidx.room.Entity

@Entity(
    tableName = "favorites",
    primaryKeys = ["lat", "lon"]
)
data class FavoriteLocation(
    val lat: Double,
    val lon: Double,
    val cityName: String,

    // Weather snapshot
    val temp: Double,
    val description: String,
    val iconCode: String,
    val humidity: Int,
    val windSpeed: Double,
    val pressure: Int,
    val clouds: Int,

    val lastUpdated: Long = System.currentTimeMillis()
)