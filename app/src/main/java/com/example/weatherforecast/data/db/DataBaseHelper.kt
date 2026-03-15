package com.example.weatherforecast.data.db


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherforecast.data.alert.model.Alert
import com.example.weatherforecast.data.db.dao.AlertDao
import com.example.weatherforecast.data.db.dao.CurrentWeatherDao
import com.example.weatherforecast.data.db.dao.FavoriteDao
import com.example.weatherforecast.data.weather.model.entity.CurrentWeatherEntity
import com.example.weatherforecast.data.db.dao.ForecastDao
import com.example.weatherforecast.data.weather.model.entity.FavoriteLocation
import com.example.weatherforecast.data.weather.model.entity.ForecastEntity

@Database(
    entities = [
        CurrentWeatherEntity::class,
        ForecastEntity::class,
        FavoriteLocation::class,
        Alert::class
    ],
    version = 4,
)
abstract class DataBaseHelper : RoomDatabase() {
    companion object {
        @Volatile // to make all the treads read from the same var
        private var INSTANCE: DataBaseHelper? = null

        fun getInstance(appContext: Context): DataBaseHelper {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    appContext.applicationContext,
                    DataBaseHelper::class.java,
                    "weather_database"
                )
                    .fallbackToDestructiveMigration()
                    .build().also {
                        INSTANCE = it
                    } // now will return the new db and init the instance then return last line
            }
        }
    }

    abstract fun currentWeatherDao(): CurrentWeatherDao
    abstract fun forecastDao(): ForecastDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun alertDao(): AlertDao
}

