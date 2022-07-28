package com.hermanbocharov.weatherforecast.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hermanbocharov.weatherforecast.data.database.dao.*
import com.hermanbocharov.weatherforecast.data.database.entities.*

@Database(
    entities = [
        LocationEntity::class,
        WeatherConditionEntity::class,
        CurrentWeatherEntity::class,
        HourlyForecastEntity::class,
        DailyForecastEntity::class
    ],
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
    abstract fun hourlyDao(): HourlyForecastDao
    abstract fun dailyDao(): DailyForecastDao
    abstract fun currentWeatherFullDataDao(): CurrentWeatherFullDataDao
    abstract fun hourlyForecastFullDataDao(): HourlyForecastFullDataDao
    abstract fun dailyForecastFullDataDao(): DailyForecastFullDataDao
}