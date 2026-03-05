package com.example.weatherforecast.data.weather.dataSource.remote.model.weather

import com.google.gson.annotations.SerializedName

data class WeatherDescription(
    @SerializedName("id")          val id: Int,
    @SerializedName("main")        val main: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon")        val icon: String
) {
    val iconUrl: String
        get() = "https://openweathermap.org/img/wn/${icon}@2x.png"
}