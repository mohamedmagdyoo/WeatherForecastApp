package com.example.weatherforecast.data.weather.dataSource.remote.dto.forcast

import com.example.weatherforecast.data.weather.dataSource.remote.dto.weather.Main
import com.example.weatherforecast.data.weather.dataSource.remote.dto.weather.WeatherDescription
import com.google.gson.annotations.SerializedName
data class ForecastResponse(
    @SerializedName("list") val list: List<ForecastItem>,
    @SerializedName("city") val city: City
)

data class ForecastItem(
    @SerializedName("dt") val dt: Long,
    @SerializedName("dt_txt") val dtTxt: String,
    @SerializedName("main") val main: Main,        // reuse Main from above
    @SerializedName("weather") val weather: List<WeatherDescription>  // reuse too
)

data class City(
    @SerializedName("name") val name: String       // only need city name
)