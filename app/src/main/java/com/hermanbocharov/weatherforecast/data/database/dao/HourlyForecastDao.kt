package com.hermanbocharov.weatherforecast.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.hermanbocharov.weatherforecast.data.database.entities.HourlyForecastEntity

@Dao
interface HourlyForecastDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHourlyForecast(hourly: List<HourlyForecastEntity>)
}