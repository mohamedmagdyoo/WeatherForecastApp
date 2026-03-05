package com.example.weatherforecast.data.weather.dataSource.remote.model.weather

import com.google.gson.annotations.SerializedName

data class Snow(
    @SerializedName("1h") val lastHour: Double?,
    @SerializedName("3h") val lastThreeHours: Double?
)