package com.example.weatherforecast.data.location

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import com.example.weatherforecast.data.appPreferences.AppPreferences
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationService(val context: Activity) {
    companion object {
        const val REQUEST_LOCATION_CODE = 2026
    }
    val fusedClint = LocationServices.getFusedLocationProviderClient(context)
    val appPreferences = AppPreferences.getInstance(context)

    @SuppressLint("MissingPermission")
    suspend fun tryGetLastLocation(): LocationResultStates {

        if (!isGPSEnabled()) {
            return LocationResultStates.GpsDisabled
        }
        if (!checkLocationPermeation()) {
            return LocationResultStates.PermissionDenied
        }
        // i used suspendCancellableCoroutine to convert from callback to coroutine
        val state = suspendCancellableCoroutine { task ->
            fusedClint.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        appPreferences.saveLocation(location)
                        task.resume(LocationResultStates.Success(location))
                    } else {
                        task.resume(LocationResultStates.LocationNull)
                    }
                }
                .addOnFailureListener {
                    task.resume(LocationResultStates.LocationNull)
                }
        }

        return state
    }

    fun checkLocationPermeation(): Boolean {
        return context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
                || context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

    }

    fun isGPSEnabled(): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    }

    fun getLocationPermeation() {
        val listOfPermeation = arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )

        context.requestPermissions(
            listOfPermeation,
            REQUEST_LOCATION_CODE
        )
        //then you have to override the fun to fetch the result in the activity
    }

    fun getGpsEnabled() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        context.startActivity(intent)
    }


}