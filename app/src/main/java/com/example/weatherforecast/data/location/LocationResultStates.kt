package com.example.weatherforecast.data.location

import android.location.Location

sealed class LocationResultStates {
    data class Success(val location: Location) : LocationResultStates()
    object PermissionDenied : LocationResultStates()
    object GpsDisabled : LocationResultStates()
    object LocationNull : LocationResultStates()
}