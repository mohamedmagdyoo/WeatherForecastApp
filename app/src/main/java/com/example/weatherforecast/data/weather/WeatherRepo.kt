package com.example.weatherforecast.data.weather

import com.example.weatherforecast.data.weather.dataSource.remote.WeatherRemoteDataSource
import com.example.weatherforecast.data.weather.dataSource.remote.model.forcast.DailyForecast
import com.example.weatherforecast.data.weather.dataSource.remote.model.forcast.ForecastResponse
import com.example.weatherforecast.data.weather.dataSource.remote.model.forcast.ForecastResult
import com.example.weatherforecast.data.weather.dataSource.remote.model.forcast.HourlyForecast
import com.example.weatherforecast.data.weather.dataSource.remote.model.weather.CurrentWeatherResponse
import java.text.SimpleDateFormat
import java.util.Locale

class WeatherRepo {
    val remote = WeatherRemoteDataSource()

    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        unit: String = "metric",
        lang: String = "en"
    ): Result<CurrentWeatherResponse> {
        return remote.getCurrentWeather(
            lat = lat,
            lon = lon,
            unit = unit,
            lang = lang,
        )
    }

    suspend fun getForecast(
        lat: Double,
        lon: Double,
        unit: String = "metric",
        lang: String = "en"
    ): Result<ForecastResult> {
        val result = remote.getForecast(
            lat = lat,
            lon = lon,
            unit = unit,
            lang = lang
        )

        if (result.isSuccess) {
            val data = result.getOrNull() ?: return Result.failure<ForecastResult>(
                Exception("Wrong with mapping")
            )
            val dataAfterMapping = data.toForecastResult()

            return Result.success(dataAfterMapping)
        } else {
            return Result.failure<ForecastResult>(Exception("Wrong with mapping"))
        }
    }
}

fun ForecastResponse.toForecastResult(): ForecastResult {

    val listOfHours = list
        .take(8)
        .map { item ->
            HourlyForecast(
                time = item.dtTxt.substring(11, 16),
                temp = item.main.temp,
                icon = item.weather.firstOrNull()?.icon ?: "",
                description = item.weather.firstOrNull()?.description ?: ""
            )
        }

    val listOfDays = list
        .groupBy { item ->
            item.dtTxt.substring(0, 10) //2024-03-10
        }
        .map { (day, items) ->
            DailyForecast(
                day = day.toDayName(),
                minTemp = items.minOf { it.main.tempMin },
                maxTemp = items.maxOf { it.main.tempMax },
                icon = items[4].weather.firstOrNull()?.icon
                    ?: items.first().weather.firstOrNull()?.icon ?: "",
                description = items[4].weather.firstOrNull()?.description
                    ?: items.first().weather.firstOrNull()?.description ?: ""
            )
        }
    return ForecastResult(listOfHours, listOfDays)
}

fun String.toDayName(): String {
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = dateFormatter.parse(this) ?: return this
    return SimpleDateFormat("EEEE", Locale.getDefault()).format(date)
    //"EEEE" is the full week day pattern like sunday not sun
    //Locale.getDefault() for lang or fonts on the user device
}