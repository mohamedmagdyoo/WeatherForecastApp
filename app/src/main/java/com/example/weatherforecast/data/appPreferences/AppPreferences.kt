package com.example.weatherforecast.data.appPreferences

import android.content.Context
import android.location.Location
import android.util.Log
import com.example.weatherforecast.utils.AppConstants
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class AppPreferences(val context: Context) {
    private val _settingsChanged =
        MutableSharedFlow<UserSettings>(replay = 1, extraBufferCapacity = 1)
    val settingsChanged = _settingsChanged.asSharedFlow()

    private val sp = context.getSharedPreferences("weather_sp", Context.MODE_PRIVATE)

    companion object {
        @Volatile
        private var INSTANCE: AppPreferences? = null

        fun getInstance(context: Context): AppPreferences {
            return INSTANCE ?: synchronized(this) {
                AppPreferences(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    fun notifyChanged(settings: UserSettings = getUserSettings()) {
        _settingsChanged.tryEmit(getUserSettings())
    }


    fun getUserSettings(): UserSettings {
        val lat = getLat()
        val lon = getLon()
        val lang = getSavedLanguage()
        val unit = getSavedUnit()
        return UserSettings(lang, unit, TempLocation(lat, lon))
    }

    //Setters
    fun saveLocation(location: Location) {
        val result = sp.edit()
            .putFloat("lat", location.latitude.toFloat())
            .putFloat("lon", location.longitude.toFloat())
            .commit()
        if (result) {
            Log.d(AppConstants.TAG, "saveLocation: Start emit ${getUserSettings().location.lat}")
            val result2 = _settingsChanged.tryEmit(getUserSettings())
            if (result2) {
                Log.d(AppConstants.TAG, "saveLocation: emitted")
            } else {
                Log.d(AppConstants.TAG, "saveLocation: not emitted")
            }
        } else {
            Log.d(AppConstants.TAG, "failed to save location")
        }
    }

    suspend fun saveLanguage(lang: String) {
        sp.edit()
            .putString("lang", lang)
            .apply()
        _settingsChanged.emit(getUserSettings())
    }

    suspend fun saveUnit(unit: String) {
        sp.edit()
            .putString("unit", unit)
            .apply()
        _settingsChanged.emit(getUserSettings())
    }


    //Getters
    fun getSavedUnit(): String {
        return sp.getString("unit", "metric") ?: "metric"
    }

    fun getSavedLanguage(): String {
        return sp.getString("lang", "eng") ?: "eng"
    }

    fun getLat(): Double {
        return sp.getFloat("lat", AppConstants.DEFAULT_LAT.toFloat()).toDouble()
    }

    fun getLon(): Double {
        return sp.getFloat("lon", AppConstants.DEFAULT_LON.toFloat()).toDouble()
    }
}