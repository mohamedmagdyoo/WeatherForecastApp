package com.example.weatherforecast.utils

import com.example.labs.BuildConfig


class AppConstants {
    companion object {
        const val API_KEY = BuildConfig.WEATHER_API_KEY
        const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
        const val TAG = "asd -->"
        const val DEFAULT_LAT = 60.0626
        const val DEFAULT_LON = 61.2497
    }
}