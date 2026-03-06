package com.example.weatherforecast.data.appPreferences

import android.content.Context
import android.location.Location

class AppPreferences(val context: Context) {
    private val sp = context.getSharedPreferences("weather_sp", Context.MODE_PRIVATE)

    fun saveLocation(location: Location) {
        sp.edit()
            .putFloat("lat", location.latitude.toFloat())
            .putFloat("lon", location.longitude.toFloat())
            .apply()
    }

    fun getSavedLocation(): Pair<Double, Double>? {
        val lat = sp.getFloat("lat", 0.0F).toDouble()
        val lon = sp.getFloat("lon", 0.0F).toDouble()

        if (lat == 0.0) return null
        return Pair(lat, lon)
    }
}