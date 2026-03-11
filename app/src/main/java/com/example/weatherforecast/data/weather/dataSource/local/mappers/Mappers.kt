package com.example.weatherforecast.data.weather.dataSource.local.mappers

import com.example.weatherforecast.data.weather.dataSource.local.entity.CurrentWeatherEntity
import com.example.weatherforecast.data.weather.dataSource.local.entity.FavoriteLocation
import com.example.weatherforecast.data.weather.dataSource.local.entity.ForecastEntity
import com.example.weatherforecast.data.weather.dataSource.remote.dto.forcast.DailyForecast
import com.example.weatherforecast.data.weather.dataSource.remote.dto.forcast.ForecastResponse
import com.example.weatherforecast.data.weather.dataSource.remote.dto.forcast.ForecastResult
import com.example.weatherforecast.data.weather.dataSource.remote.dto.forcast.HourlyForecast
import com.example.weatherforecast.data.weather.dataSource.remote.dto.weather.CurrentWeatherResponse
import java.text.SimpleDateFormat
import java.util.Locale

fun CurrentWeatherResponse.toEntity(lat: Double, lon: Double): CurrentWeatherEntity {
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
        sunset = sys.sunset,
        lat = lat,
        lon = lon
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

fun List<ForecastEntity>.toForecastResult(): ForecastResult {
    val hourly = take(8).map { item ->
        HourlyForecast(
            time = item.dtTxt.substring(11, 16),
            temp = item.temp,
            icon = item.icon,
            description = item.description
        )
    }

    val daily = groupBy { item ->
        item.dtTxt.substring(0, 10)
    }.map { (date, items) ->
        val middayItem = items.minByOrNull { item ->
            val hour = item.dtTxt.substring(11, 13).toInt()
            Math.abs(hour - 12)
        } ?: items.first()

        DailyForecast(
            day = date.toDayName(),
            minTemp = items.minOf { it.tempMin },
            maxTemp = items.maxOf { it.tempMax },
            icon = middayItem.icon,
            description = middayItem.description
        )
    }

    return ForecastResult(hourly, daily)
}

fun String.toDayName(): String {
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = dateFormatter.parse(this) ?: return this
    return SimpleDateFormat("EEEE", Locale.getDefault()).format(date)
    //"EEEE" is the full week day pattern like sunday not sun
    //Locale.getDefault() for lang or fonts on the user device
}

fun CurrentWeatherEntity.toFavoriteLocation(): FavoriteLocation {
    return FavoriteLocation(
        lat = lat,
        lon = lon,
        cityName = cityName,
        temp = temp,
        description = description,
        iconCode = icon,
        humidity = humidity,
        windSpeed = windSpeed,
        pressure = pressure,
        clouds = clouds
    )
}
