package com.example.weatherforecast.data.network

import com.example.weatherforecast.data.network.service.WeatherApiService
import com.example.weatherforecast.utils.AppConstants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    private val retrofit = Retrofit
        .Builder()
        .baseUrl(AppConstants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val weatherService = retrofit.create(WeatherApiService::class.java)
}