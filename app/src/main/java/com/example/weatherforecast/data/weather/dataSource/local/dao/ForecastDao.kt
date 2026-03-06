package com.example.weatherforecast.data.weather.dataSource.local.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherforecast.data.weather.dataSource.local.entity.ForecastEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ForecastDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecasts(entities: List<ForecastEntity>)

    @Query("SELECT * FROM forecast_table WHERE lat = :lat AND lon = :lon ORDER BY dt ASC")
    fun getForecast(lat: Double, lon: Double): Flow<List<ForecastEntity>>

    @Query("delete from forecast_table")
    suspend fun deleteAll()

    // delete old forecast before inserting new one
    @Query("DELETE FROM forecast_table WHERE lat = :lat AND lon = :lon")
    suspend fun deleteForecastByLocation(lat: Double, lon: Double)

    @Query("SELECT cachedAt FROM forecast_table WHERE lat = :lat AND lon = :lon LIMIT 1")
    suspend fun getLastCachedAt(lat: Double, lon: Double): Long?
}