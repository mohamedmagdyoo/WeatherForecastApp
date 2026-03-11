package com.example.weatherforecast.data.weather

import com.example.weatherforecast.data.weather.dataSource.local.entity.CurrentWeatherEntity
import com.example.weatherforecast.data.weather.dataSource.local.entity.FavoriteLocation
import com.example.weatherforecast.data.weather.dataSource.local.entity.LatLonEntity
import com.example.weatherforecast.data.weather.dataSource.remote.dto.forcast.ForecastResult
import kotlinx.coroutines.flow.Flow

interface WeatherRepoInterface {
    fun getCurrentWeather(): Flow<CurrentWeatherEntity?>
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
    suspend fun inertFavorite(location: FavoriteLocation)
    suspend fun deleteFavorite(location: FavoriteLocation)
    suspend fun getFavoriteByLatLon(lat: Double, lon: Double): Result<FavoriteLocation>

}