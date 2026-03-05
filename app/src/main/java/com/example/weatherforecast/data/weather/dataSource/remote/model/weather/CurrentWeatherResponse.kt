package com.example.weatherforecast.data.weather.dataSource.remote.model.weather

import com.google.gson.annotations.SerializedName

data class CurrentWeatherResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("base") val base: String,
    @SerializedName("cod") val cod: Int,
    @SerializedName("timezone") val timezone: Int,
    @SerializedName("dt") val dt: Long,
    @SerializedName("visibility") val visibility: Int,
    @SerializedName("coord") val coord: Coord,
    @SerializedName("weather") val weather: List<WeatherDescription>,
    @SerializedName("main") val main: Main,
    @SerializedName("wind") val wind: Wind,
    @SerializedName("clouds") val clouds: Clouds,
    @SerializedName("sys") val sys: Sys,
    @SerializedName("rain") val rain: Rain?,
    @SerializedName("snow") val snow: Snow?
)