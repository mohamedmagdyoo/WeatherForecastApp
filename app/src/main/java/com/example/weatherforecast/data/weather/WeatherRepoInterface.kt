package com.example.weatherforecast.data.weather

import com.example.weatherforecast.data.weather.model.entity.CurrentWeatherEntity
import com.example.weatherforecast.data.weather.model.entity.FavoriteLocation
import com.example.weatherforecast.data.weather.model.entity.LatLonEntity
import com.example.weatherforecast.data.weather.model.dto.forcast.ForecastResult
import kotlinx.coroutines.flow.Flow

interface WeatherRepoInterface {
    //Current Weather
    fun getCurrentWeather(): Flow<CurrentWeatherEntity?>
    suspend fun getCurrentWeatherOnce(
        lat: Double,
        lon: Double,
        unit: String,
        lang: String
    ): Result<CurrentWeatherEntity>


    //Forecast
    fun getForecast(): Flow<ForecastResult>
    suspend fun refreshWeatherData(
        lat: Double,
        lon: Double,
        unit: String,
        lang: String
    ): Result<Unit>

    suspend fun isCacheStale(): Boolean
    fun isLocationFarEnough(
        oldLat: Double, oldLon: Double,
        newLat: Double, newLon: Double
    ): Boolean

    suspend fun getSavedLatLon(): Result<LatLonEntity>?


    //Favorites
    fun getFavorites(): Flow<List<FavoriteLocation>>
    suspend fun getFavoriteLocation(
        lat: Double,
        lon: Double,
        unit: String,
        lang: String,
        cityName: String
    ): Result<FavoriteLocation>

    suspend fun insertFavorite(favLocation: FavoriteLocation)

    suspend fun deleteFavorite(location: FavoriteLocation)
    suspend fun getFavoriteByLatLon(lat: Double, lon: Double): Result<FavoriteLocation>

}