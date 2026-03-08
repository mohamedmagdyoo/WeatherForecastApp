package com.example.weatherforecast.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherforecast.data.weather.dataSource.local.entity.CurrentWeatherEntity
import com.example.weatherforecast.data.weather.dataSource.local.entity.LatLonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrentWeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(entity: CurrentWeatherEntity)

    @Query("SELECT * FROM current_weather WHERE id = 1")
    fun getCurrentWeather(): Flow<CurrentWeatherEntity?>

    @Query("SELECT cachedAt FROM current_weather WHERE id = 1")
    suspend fun getLastCachedAt(): Long?

    @Query("SELECT lat, lon FROM current_weather WHERE id = 1")
    suspend fun getSavedLatLon(): LatLonEntity?

}