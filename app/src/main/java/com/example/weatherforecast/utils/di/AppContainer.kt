package com.example.weatherforecast.utils.di

import android.content.Context
import com.example.weatherforecast.data.db.DataBaseHelper
import com.example.weatherforecast.data.network.RetrofitHelper
import com.example.weatherforecast.data.network.service.WeatherApiService
import com.example.weatherforecast.data.weather.WeatherRepo
import com.example.weatherforecast.data.weather.dataSource.local.WeatherLocalSource
import com.example.weatherforecast.data.weather.dataSource.remote.WeatherRemoteSource

class AppContainer(val appContext: Context) {

    val weatherRepo: WeatherRepo by lazy {
        val weatherService: WeatherApiService = RetrofitHelper.weatherService
        val dataBaseHelper: DataBaseHelper = DataBaseHelper.getInstance(appContext)
        val weatherDao = dataBaseHelper.currentWeatherDao()
        val forecastDao = dataBaseHelper.forecastDao()
        val favoriteDao = dataBaseHelper.favoriteDao()
        val weatherRemoteDataSource: WeatherRemoteSource = WeatherRemoteSource(weatherService)
        val weatherLocalDataSource: WeatherLocalSource =
            WeatherLocalSource(weatherDao, forecastDao, favoriteDao)

        WeatherRepo(weatherRemoteDataSource, weatherLocalDataSource)
    }
}