package com.example.weatherforecast.data.appPreferences

data class UserSettings(
    val language: String,
    val unit: String,
    val location: TempLocation
)

data class TempLocation(
    var lat: Double,
    var lon: Double
)
