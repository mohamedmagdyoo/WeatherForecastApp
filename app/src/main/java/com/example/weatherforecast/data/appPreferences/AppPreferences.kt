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

    private val _languageChanged = MutableSharedFlow<String>()
    val languageChanged = _languageChanged.asSharedFlow()

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
        val lang = getLanguage()
        val unit = getTempUnit()
        return UserSettings(lang, unit, TempLocation(lat, lon))
    }

    //Location
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

    fun getLat(): Double {
        return sp.getFloat("lat", AppConstants.DEFAULT_LAT.toFloat()).toDouble()
    }

    fun getLon(): Double {
        return sp.getFloat("lon", AppConstants.DEFAULT_LON.toFloat()).toDouble()
    }


    //Language
    suspend fun saveLanguage(lang: String) {
        sp.edit()
            .putString("lang", lang)
            .apply()
//        _settingsChanged.emit(getUserSettings())
        _languageChanged.emit(lang)
    }

    fun getLanguage(): String {
        return sp.getString("lang", "en") ?: "en"
    }

    //Unit
    suspend fun saveWindUnit(unit: String) {
        sp.edit()
            .putString("wind_unit", unit)
            .apply()
        _settingsChanged.emit(getUserSettings())
    }

    fun getWindUnit(): String {
        return sp.getString("wind_unit", "metric") ?: "metric"
    }

    //Speed
    suspend fun saveTempUnit(unit: String) {
        sp.edit()
            .putString("temp_unit", unit)
            .apply()
        _settingsChanged.emit(getUserSettings())
    }

    fun getTempUnit(): String {
        return sp.getString("temp_unit", "metric") ?: "metric"
    }

    //LocationMethod
    suspend fun setLocationMethod(method: String) {
        sp.edit()
            .putString("location_method", method)
            .apply()
        _settingsChanged.emit(getUserSettings())
    }

    fun getLocationMethod(): String {
        return sp.getString("location_method", "gps") ?: "gps"
    }

    //Notifications
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        sp.edit()
            .putBoolean("notifications_enabled", enabled)
            .apply()
        _settingsChanged.emit(getUserSettings())
    }

    fun getNotificationsEnabled(): Boolean {
        return sp.getBoolean("notifications_enabled", true)
    }
}

