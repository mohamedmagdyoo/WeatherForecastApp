package com.example.weatherforecast.data.location

import android.location.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LocationService {
    fun getLastLocation(): Location {
        return Location("")
    }

    fun getLocationUpdates(): Flow<Location> {
        return flow {
            emit(Location(""))
        }
    }
}