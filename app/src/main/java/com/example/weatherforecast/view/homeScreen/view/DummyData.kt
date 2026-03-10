package com.example.weatherforecast.view.homeScreen.view

import com.example.weatherforecast.data.weather.dataSource.local.entity.CurrentWeatherEntity
import com.example.weatherforecast.data.weather.dataSource.remote.dto.forcast.DailyForecast
import com.example.weatherforecast.data.weather.dataSource.remote.dto.forcast.ForecastResult
import com.example.weatherforecast.data.weather.dataSource.remote.dto.forcast.HourlyForecast

val dummyCurrentWeather = CurrentWeatherEntity(
    id = 1,
    lat = 30.06,
    lon = 31.24,
    cityName = "Cairo",
    temp = 28.5,
    feelsLike = 27.0,
    description = "clear sky",
    icon = "01d",
    humidity = 45,
    windSpeed = 3.5,
    pressure = 1013,
    clouds = 10,
    sunrise = 1710045600L,
    sunset = 1710088800L,
    cachedAt = System.currentTimeMillis()
)

val dummyForecast =
    ForecastResult(
    listOfHourlyForecast = listOf(
        HourlyForecast(time = "12:00", temp = 28.5, icon = "01d", description = "clear sky"),
        HourlyForecast(time = "15:00", temp = 30.0, icon = "02d", description = "few clouds"),
        HourlyForecast(time = "18:00", temp = 27.0, icon = "03d", description = "scattered clouds"),
        HourlyForecast(time = "21:00", temp = 24.0, icon = "04n", description = "broken clouds"),
        HourlyForecast(time = "00:00", temp = 21.0, icon = "01n", description = "clear sky"),
        HourlyForecast(time = "03:00", temp = 19.0, icon = "01n", description = "clear sky"),
        HourlyForecast(time = "06:00", temp = 20.0, icon = "02d", description = "few clouds"),
        HourlyForecast(time = "09:00", temp = 25.0, icon = "01d", description = "clear sky"),
    ),
    listOfDailyForecast = listOf(
        DailyForecast(
            day = "Monday",
            minTemp = 18.0,
            maxTemp = 30.0,
            icon = "01d",
            description = "clear sky"
        ),
        DailyForecast(
            day = "Tuesday",
            minTemp = 17.0,
            maxTemp = 28.0,
            icon = "02d",
            description = "few clouds"
        ),
        DailyForecast(
            day = "Wednesday",
            minTemp = 16.0,
            maxTemp = 25.0,
            icon = "10d",
            description = "light rain"
        ),
        DailyForecast(
            day = "Thursday",
            minTemp = 15.0,
            maxTemp = 23.0,
            icon = "09d",
            description = "shower rain"
        ),
        DailyForecast(
            day = "Friday",
            minTemp = 18.0,
            maxTemp = 27.0,
            icon = "01d",
            description = "clear sky"
        ),
    )
)