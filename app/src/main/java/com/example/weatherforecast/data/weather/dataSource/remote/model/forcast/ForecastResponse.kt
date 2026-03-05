package com.example.weatherforecast.data.weather.dataSource.remote.model.forcast

import com.example.weatherforecast.data.weather.dataSource.remote.model.weather.Clouds
import com.example.weatherforecast.data.weather.dataSource.remote.model.weather.Coord
import com.example.weatherforecast.data.weather.dataSource.remote.model.weather.Main
import com.example.weatherforecast.data.weather.dataSource.remote.model.weather.Rain
import com.example.weatherforecast.data.weather.dataSource.remote.model.weather.Snow
import com.example.weatherforecast.data.weather.dataSource.remote.model.weather.WeatherDescription
import com.example.weatherforecast.data.weather.dataSource.remote.model.weather.Wind
import com.google.gson.annotations.SerializedName

data class ForecastResponse(
    @SerializedName("cod") val cod: String,
    @SerializedName("message") val message: Int,
    @SerializedName("cnt") val cnt: Int,
    @SerializedName("list") val list: List<ForecastItem>,
    @SerializedName("city") val city: City
)

data class ForecastItem(
    @SerializedName("dt") val dt: Long,
    @SerializedName("dt_txt") val dtTxt: String, //"2024-03-10 14:00:00"
    @SerializedName("visibility") val visibility: Int,
    @SerializedName("pop") val pop: Double,
    @SerializedName("weather") val weather: List<WeatherDescription>,
    @SerializedName("main") val main: Main,
    @SerializedName("wind") val wind: Wind,
    @SerializedName("clouds") val clouds: Clouds,
    @SerializedName("rain") val rain: Rain?,
    @SerializedName("snow") val snow: Snow?,
    @SerializedName("sys") val sys: ForecastSys
)

data class ForecastSys(
    @SerializedName("pod") val pod: String
)

data class City(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("country") val country: String,
    @SerializedName("population") val population: Int,
    @SerializedName("timezone") val timezone: Int,
    @SerializedName("sunrise") val sunrise: Long,
    @SerializedName("sunset") val sunset: Long,
    @SerializedName("coord") val coord: Coord
)

