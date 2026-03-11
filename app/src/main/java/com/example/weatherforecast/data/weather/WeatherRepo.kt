package com.example.weatherforecast.data.weather

import android.location.Location
import android.util.Log
import com.example.weatherforecast.data.weather.dataSource.local.WeatherLocalSourceInterface
import com.example.weatherforecast.data.weather.dataSource.local.entity.CurrentWeatherEntity
import com.example.weatherforecast.data.weather.dataSource.local.entity.FavoriteLocation
import com.example.weatherforecast.data.weather.dataSource.local.entity.LatLonEntity
import com.example.weatherforecast.data.weather.dataSource.local.mappers.toEntity
import com.example.weatherforecast.data.weather.dataSource.local.mappers.toEntityList
import com.example.weatherforecast.data.weather.dataSource.local.mappers.toFavoriteLocation
import com.example.weatherforecast.data.weather.dataSource.local.mappers.toForecastResult
import com.example.weatherforecast.data.weather.dataSource.remote.WeatherRemoteSourceInterface
import com.example.weatherforecast.data.weather.dataSource.remote.dto.forcast.ForecastResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WeatherRepo(
    private val remote: WeatherRemoteSourceInterface,
    private val local: WeatherLocalSourceInterface
) : WeatherRepoInterface {


    override fun getCurrentWeather(): Flow<CurrentWeatherEntity?> {
        return local.getCurrentWeather()
    }

    override suspend fun getCurrentWeatherOnce(
        lat: Double,
        lon: Double,
        unit: String,
        lang: String
    ): Result<CurrentWeatherEntity> {
        val value = remote.getCurrentWeather(lat, lon, unit, lang).getOrNull()?.toEntity(lat, lon)
        if (value != null)
            return Result.success(value)
        return Result.failure(Exception("No data"))
    }

    override fun getForecast(): Flow<ForecastResult> {
        return local.getForecast()
            .map { entities -> entities.toForecastResult() }
    }


    override suspend fun refreshWeatherData(
        lat: Double,
        lon: Double,
        unit: String,
        lang: String
    ): Result<Unit> {
        return try {
            val currentWeatherResult = remote.getCurrentWeather(lat, lon, unit, lang)
            val forecastResult = remote.getForecast(lat, lon, unit, lang)

            if (currentWeatherResult.isSuccess && forecastResult.isSuccess) {
                local.insertCurrentWeather(
                    currentWeatherResult.getOrThrow().toEntity(lat, lon)
                )
                local.insertForecasts(
                    forecastResult.getOrThrow().toEntityList(lat, lon)
                )
                Result.success(Unit)
            } else {
                val error = currentWeatherResult.exceptionOrNull()
                Result.failure(error ?: Exception("Unknown Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun isCacheStale(): Boolean {
        val cachedAt = local.getLastCurrentWeatherCachedAt() ?: return true
        val thirtyMinutes = 30 * 60 * 1000L
        return System.currentTimeMillis() - cachedAt > thirtyMinutes
    }

    override fun isLocationFarEnough(
        oldLat: Double, oldLon: Double,
        newLat: Double, newLon: Double
    ): Boolean {
        val results = FloatArray(1)
        Location.distanceBetween(oldLat, oldLon, newLat, newLon, results)
        return results[0] > 5000  // 5km
    }

    override suspend fun getSavedLatLon(): Result<LatLonEntity>? {
        return local.getSavedLatLon()
    }


    //Favorites
    override fun getFavorites(): Flow<List<FavoriteLocation>> {
        return local.getFavorites()
    }

    override suspend fun insertFavorite(favLocation: FavoriteLocation) {
        local.insertFavorite(favLocation)
    }

    override suspend fun getFavoriteLocation(
        lat: Double,
        lon: Double,
        unit: String,
        lang: String,
        cityName: String
    ): Result<FavoriteLocation> {
        //1) fetch the curren weathe
        //2) map it to favLocate entity
        //3) save it to db
        try {
            val result = getCurrentWeatherOnce(lat, lon, unit, lang)

            if (result.isSuccess) {
                val currentWeather = result.getOrThrow()
                val favoriteLocation = currentWeather.toFavoriteLocation()

                return Result.success(favoriteLocation)
            } else {
                return Result.failure(result.exceptionOrNull()!!)
            }
        } catch (ex: Exception) {
            return Result.failure(ex)
        }
    }

    override suspend fun deleteFavorite(location: FavoriteLocation) {
        local.deleteFavorite(location)
    }

    override suspend fun getFavoriteByLatLon(lat: Double, lon: Double): Result<FavoriteLocation> {
        return local.getFavoriteByLatLon(lat, lon)
    }
}