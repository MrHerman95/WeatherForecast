package com.hermanbocharov.weatherforecast.data.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RenameColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import com.hermanbocharov.weatherforecast.data.database.dao.CurrentWeatherDao
import com.hermanbocharov.weatherforecast.data.database.dao.CurrentWeatherFullDataDao
import com.hermanbocharov.weatherforecast.data.database.dao.DailyForecastDao
import com.hermanbocharov.weatherforecast.data.database.dao.DailyForecastFullDataDao
import com.hermanbocharov.weatherforecast.data.database.dao.HourlyForecastDao
import com.hermanbocharov.weatherforecast.data.database.dao.HourlyForecastFullDataDao
import com.hermanbocharov.weatherforecast.data.database.dao.LocationDao
import com.hermanbocharov.weatherforecast.data.database.dao.WeatherConditionDao
import com.hermanbocharov.weatherforecast.data.database.entities.CurrentWeatherEntity
import com.hermanbocharov.weatherforecast.data.database.entities.DailyForecastEntity
import com.hermanbocharov.weatherforecast.data.database.entities.HourlyForecastEntity
import com.hermanbocharov.weatherforecast.data.database.entities.LocationEntity
import com.hermanbocharov.weatherforecast.data.database.entities.WeatherConditionEntity

@Database(
    entities = [
        LocationEntity::class,
        WeatherConditionEntity::class,
        CurrentWeatherEntity::class,
        HourlyForecastEntity::class,
        DailyForecastEntity::class
    ],
    version = 2,
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2,
            spec = AppDatabase.MyAutoMigration::class
        )
    ],
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    @RenameColumn(tableName = "location", fromColumnName = "name", toColumnName = "name_en")
    @RenameColumn(tableName = "location", fromColumnName = "country", toColumnName = "country_code")
    class MyAutoMigration : AutoMigrationSpec

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