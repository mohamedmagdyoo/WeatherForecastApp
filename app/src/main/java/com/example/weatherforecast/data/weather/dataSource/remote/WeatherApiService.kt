package com.example.weatherforecast.data.weather.dataSource.remote

import com.example.weatherforecast.data.weather.dataSource.remote.model.forcast.ForecastResponse
import com.example.weatherforecast.data.weather.dataSource.remote.model.weather.CurrentWeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    //?lat={lat}&lon={lon}&appid={API key}
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") unit: String,
        @Query("lang") lang: String
    ): Response<CurrentWeatherResponse>

    //api.openweathermap.org/data/2.5/forecast?lat={lat}&lon={lon}&appid={API key}
    @GET("forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") unit: String,
        @Query("lang") lang: String
    ): Response<ForecastResponse>
}