package com.hermanbocharov.weatherforecast.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.hermanbocharov.weatherforecast.data.database.entities.DailyForecastEntity

@Dao
interface DailyForecastDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDailyForecast(daily: List<DailyForecastEntity>)
}