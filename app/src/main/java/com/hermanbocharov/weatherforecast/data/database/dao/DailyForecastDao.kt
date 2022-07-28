package com.hermanbocharov.weatherforecast.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hermanbocharov.weatherforecast.data.database.entities.DailyForecastEntity

@Dao
interface DailyForecastDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDailyForecast(daily: List<DailyForecastEntity>)

    @Query("DELETE FROM daily_forecast WHERE location_id=:locationId")
    fun deleteOldDailyForecast(locationId: Int)
}