package com.example.weatherforecast.data.weather.dataSource.remote.model.weather

import com.google.gson.annotations.SerializedName

data class Wind(
    @SerializedName("speed") val speed: Double,
    @SerializedName("deg") val deg: Int,
    @SerializedName("gust") val gust: Double?
)