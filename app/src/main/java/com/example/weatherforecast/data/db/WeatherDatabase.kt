package com.example.weatherforecast.data.db


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherforecast.data.db.dao.CurrentWeatherDao
import com.example.weatherforecast.data.weather.dataSource.local.entity.CurrentWeatherEntity
import com.example.weatherforecast.data.db.dao.ForecastDao
import com.example.weatherforecast.data.weather.dataSource.local.entity.ForecastEntity

@Database(
    entities = [CurrentWeatherEntity::class, ForecastEntity::class],
    version = 1,
)
abstract class WeatherDatabase : RoomDatabase() {
    companion object {
        @Volatile // to make all the treads read from the same var
        private var INSTANCE: WeatherDatabase? = null

        fun getInstance(appContext: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    appContext.applicationContext,
                    WeatherDatabase::class.java,
                    "weather_database"
                ).build().also {
                    INSTANCE = it
                } // now will return the new db and init the instance then return last line
            }
        }
    }

    abstract fun currentWeatherDao(): CurrentWeatherDao
    abstract fun forecastDao(): ForecastDao
}