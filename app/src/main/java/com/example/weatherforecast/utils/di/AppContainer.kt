package com.example.weatherforecast.utils.di

import android.content.Context
import com.example.weatherforecast.data.alert.AlertRepo
import com.example.weatherforecast.data.alert.dataSorce.local.AlertLocalDataSource
import com.example.weatherforecast.data.db.DataBaseHelper
import com.example.weatherforecast.data.network.RetrofitHelper
import com.example.weatherforecast.data.network.service.WeatherApiService
import com.example.weatherforecast.data.weather.WeatherRepo
import com.example.weatherforecast.data.weather.dataSource.local.WeatherLocalSource
import com.example.weatherforecast.data.weather.dataSource.remote.WeatherRemoteSource

class AppContainer(val appContext: Context) {

    val dataBaseHelper: DataBaseHelper = DataBaseHelper.getInstance(appContext)
    val weatherRepo: WeatherRepo by lazy {
        val weatherService: WeatherApiService = RetrofitHelper.weatherService
        val weatherDao = dataBaseHelper.currentWeatherDao()
        val forecastDao = dataBaseHelper.forecastDao()
        val favoriteDao = dataBaseHelper.favoriteDao()
        val weatherRemoteDataSource: WeatherRemoteSource = WeatherRemoteSource(weatherService)
        val weatherLocalDataSource: WeatherLocalSource =
            WeatherLocalSource(weatherDao, forecastDao, favoriteDao)

        WeatherRepo(weatherRemoteDataSource, weatherLocalDataSource)
    }

    val alertRepo: AlertRepo by lazy {
        val alertDao = dataBaseHelper.alertDao()
        val alertLocalDataSource = AlertLocalDataSource(alertDao)
        AlertRepo(alertLocalDataSource)
    }


}