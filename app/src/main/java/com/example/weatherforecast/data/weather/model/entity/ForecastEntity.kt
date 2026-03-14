package com.example.weatherforecast.data.weather.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "forecast_table")
data class ForecastEntity(
    @PrimaryKey
    val id: String,           //lat + lon + dt

    val lat: Double,          // needed for querying by location
    val lon: Double,

    val dtTxt: String,
    val dt: Long,
    val temp: Double,
    val tempMin: Double,
    val tempMax: Double,
    val icon: String,
    val description: String,

    val cachedAt: Long = System.currentTimeMillis()
)