package com.example.weatherforecast.data.weather.dataSource.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherforecast.data.weather.dataSource.local.entity.CurrentWeatherEntity
import com.example.weatherforecast.data.weather.dataSource.local.entity.FavoriteLocation
import com.example.weatherforecast.data.weather.dataSource.local.entity.ForecastEntity
import com.example.weatherforecast.data.weather.dataSource.local.entity.LatLonEntity
import kotlinx.coroutines.flow.Flow

interface WeatherLocalSourceInterface {
    // Current Weather
    fun getCurrentWeather(): Flow<CurrentWeatherEntity?>

    suspend fun insertCurrentWeather(entity: CurrentWeatherEntity)
    suspend fun getLastCurrentWeatherCachedAt(): Long?

    // Forecast
    fun getForecast(): Flow<List<ForecastEntity>>
    suspend fun insertForecasts(entities: List<ForecastEntity>)
    suspend fun getLastForecastCachedAt(lat: Double, lon: Double): Long?
    suspend fun getSavedLatLon(): Result<LatLonEntity>?

    //Favorites
    fun getFavorites(): Flow<List<FavoriteLocation>>
    suspend fun insertFavorite(location: FavoriteLocation)
    suspend fun deleteFavorite(location: FavoriteLocation)
    suspend fun getFavoriteByLatLon(lat: Double, lon: Double): Result<FavoriteLocation>
}