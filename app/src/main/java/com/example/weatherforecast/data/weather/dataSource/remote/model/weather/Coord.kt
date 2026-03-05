package com.example.weatherforecast.data.weather.dataSource.remote.model.weather

import com.google.gson.annotations.SerializedName

data class Coord(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double
)