package com.hermanbocharov.weatherforecast.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hermanbocharov.weatherforecast.data.database.dao.CurrentWeatherDao
import com.hermanbocharov.weatherforecast.data.database.dao.CurrentWeatherFullDataDao
import com.hermanbocharov.weatherforecast.data.database.dao.LocationDao
import com.hermanbocharov.weatherforecast.data.database.dao.WeatherConditionDao
import com.hermanbocharov.weatherforecast.data.database.entities.CurrentWeatherEntity
import com.hermanbocharov.weatherforecast.data.database.entities.LocationEntity
import com.hermanbocharov.weatherforecast.data.database.entities.WeatherConditionEntity

@Database(
    entities = [LocationEntity::class, WeatherConditionEntity::class, CurrentWeatherEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        private var db: AppDatabase? = null
        private const val DB_NAME = "main.db"
        private val LOCK = Any()

        fun getInstance(context: Context): AppDatabase {
            db?.let { return it }

            synchronized(LOCK) {
                db?.let { return it }

                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    DB_NAME
                ).build()

                db = instance
                return instance
            }
        }
    }

    abstract fun locationDao(): LocationDao
    abstract fun weatherConditionDao(): WeatherConditionDao
    abstract fun currentDao(): CurrentWeatherDao
    abstract fun currentWeatherFullDataDao(): CurrentWeatherFullDataDao
}