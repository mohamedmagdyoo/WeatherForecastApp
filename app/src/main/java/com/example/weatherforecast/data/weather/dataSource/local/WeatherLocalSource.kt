package com.example.weatherforecast.data.weather.dataSource.local


import com.example.weatherforecast.data.db.dao.CurrentWeatherDao
import com.example.weatherforecast.data.db.dao.FavoriteDao
import com.example.weatherforecast.data.db.dao.ForecastDao
import com.example.weatherforecast.data.weather.model.entity.CurrentWeatherEntity
import com.example.weatherforecast.data.weather.model.entity.FavoriteLocation
import com.example.weatherforecast.data.weather.model.entity.ForecastEntity
import com.example.weatherforecast.data.weather.model.entity.LatLonEntity
import kotlinx.coroutines.flow.Flow

class WeatherLocalSource : WeatherLocalSourceInterface {

    private val currentWeatherDao: CurrentWeatherDao
    private val forecastDao: ForecastDao
    private val favoriteDao: FavoriteDao

    constructor(
        currentWeatherDao: CurrentWeatherDao,
        forecastDao: ForecastDao,
        favoriteDao: FavoriteDao
    ) {
        this.currentWeatherDao = currentWeatherDao
        this.forecastDao = forecastDao
        this.favoriteDao = favoriteDao
    }

    // Current Weather
    override fun getCurrentWeather(): Flow<CurrentWeatherEntity?> {
        return currentWeatherDao.getCurrentWeather()
    }

    override suspend fun insertCurrentWeather(entity: CurrentWeatherEntity) {
        currentWeatherDao.insertCurrentWeather(entity)
    }

    override suspend fun getLastCurrentWeatherCachedAt(): Long? {
        return currentWeatherDao.getLastCachedAt()
    }


    // Forecast
    override fun getForecast(): Flow<List<ForecastEntity>> {
        return forecastDao.getForecast()
    }

    override suspend fun insertForecasts(entities: List<ForecastEntity>) {
        forecastDao.deleteAll()   // clean before insert
        forecastDao.insertForecasts(entities)
    }

    override suspend fun getLastForecastCachedAt(lat: Double, lon: Double): Long? {
        return forecastDao.getLastCachedAt(lat, lon)
    }

    override suspend fun getSavedLatLon(): Result<LatLonEntity> {
        val value = currentWeatherDao.getSavedLatLon()

        if (value != null) {
            return Result.success(value)
        }
        return Result.failure(Exception("No data"))
    }

    //Favorites
    override fun getFavorites(): Flow<List<FavoriteLocation>> {
        return favoriteDao.getAllFavorites()
    }

    override suspend fun insertFavorite(location: FavoriteLocation) {
        favoriteDao.insertFavorite(location)
    }

    override suspend fun deleteFavorite(location: FavoriteLocation) {
        favoriteDao.deleteFavorite(location)
    }

    override suspend fun getFavoriteByLatLon(lat: Double, lon: Double): Result<FavoriteLocation> {
        val value = favoriteDao.getFavoriteByLatLon(lat, lon)
        if (value != null) {
            return Result.success(value)
        }
        return Result.failure(Exception("No data"))
    }
}