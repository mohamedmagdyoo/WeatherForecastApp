package com.example.weatherforecast.data.weather.dataSource.local.mappers

import com.example.weatherforecast.data.weather.dataSource.local.entity.CurrentWeatherEntity
import com.example.weatherforecast.data.weather.dataSource.local.entity.ForecastEntity
import com.example.weatherforecast.data.weather.dataSource.remote.model.forcast.ForecastResponse
import com.example.weatherforecast.data.weather.dataSource.remote.model.weather.CurrentWeatherResponse

fun CurrentWeatherResponse.toEntity(): CurrentWeatherEntity {
    return CurrentWeatherEntity(
        cityName = name,
        temp = main.temp,
        feelsLike = main.feelsLike,
        description = weather.firstOrNull()?.description ?: "",
        icon = weather.firstOrNull()?.icon ?: "",
        humidity = main.humidity,
        windSpeed = wind.speed,
        pressure = main.pressure,
        clouds = clouds.all,
        sunrise = sys.sunrise,
        sunset = sys.sunset
    )
}

fun ForecastResponse.toEntityList(lat: Double, lon: Double): List<ForecastEntity> {
    return list.map { item ->
        ForecastEntity(
            id = "${lat}_${lon}_${item.dt}",
            lat = lat,
            lon = lon,
            dtTxt = item.dtTxt,
            temp = item.main.temp,
            tempMin = item.main.tempMin,
            tempMax = item.main.tempMax,
            icon = item.weather.firstOrNull()?.icon ?: "",
            description = item.weather.firstOrNull()?.description ?: "",
            dt = item.dt
        )
    }
}
