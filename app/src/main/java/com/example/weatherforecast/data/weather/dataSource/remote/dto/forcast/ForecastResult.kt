package com.example.weatherforecast.data.weather.dataSource.remote.dto.forcast

data class ForecastResult(
    val listOfHourlyForecast: List<HourlyForecast>,
    val listOfDailyForecast: List<DailyForecast>
)

data class HourlyForecast(
    val time: String,
    val temp: Double,
    val icon: String,
    val description: String
)

data class DailyForecast(
    val day: String,
    val minTemp: Double,
    val maxTemp: Double,
    val icon: String,
    val description: String
)