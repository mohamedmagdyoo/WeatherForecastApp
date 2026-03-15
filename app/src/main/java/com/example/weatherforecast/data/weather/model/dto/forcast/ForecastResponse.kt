package com.example.weatherforecast.data.weather.model.dto.forcast

import com.example.weatherforecast.data.weather.model.dto.weather.Main
import com.example.weatherforecast.data.weather.model.dto.weather.WeatherDescription
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