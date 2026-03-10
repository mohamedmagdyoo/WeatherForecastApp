package com.example.weatherforecast.data.appPreferences

import com.example.labs.R

fun String.toTempUnitDisplay(): Int = when (this) {
    "metric" -> R.string.celsius_c
    "imperial" -> R.string.fahrenheit_f
    "standard" -> R.string.kelvin_k
    else -> R.string.celsius_c
}

fun String.toWindUnitDisplay(): Int = when (this) {
    "metric" -> R.string.m_s
    "imperial" -> R.string.mph
    else -> R.string.m_s
}

fun String.toLanguageDisplay(): Int = when (this) {
    "en" -> R.string.english
    "ar" -> R.string.arabic
    else -> R.string.english
}

fun Int.toTempUnitApi(): String = when (this) {
    R.string.celsius_c -> "metric"
    R.string.fahrenheit_f -> "imperial"
    R.string.kelvin_k -> "standard"
    else -> "metric"
}

fun Int.toWindUnitApi(): String = when (this) {
    R.string.m_s -> "metric"
    R.string.mph -> "imperial"
    else -> "metric"
}

fun Int.toLanguageApi(): String = when (this) {
    R.string.english -> "en"
    R.string.arabic -> "ar"
    else -> "en"
}

fun Int.toLocationSource(): String = when (this) {
    R.string.gps -> "GPS"
    R.string.map -> "Map"
    else -> "GPS"
}

fun String.toLocationSourceDisplay(): Int = when (this) {
    "GPS" -> R.string.gps
    "Map" -> R.string.map
    else -> R.string.gps
}

