package com.hermanbocharov.weatherforecast.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.hermanbocharov.weatherforecast.data.database.entities.WeatherConditionEntity

@Dao
interface WeatherConditionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeatherCondition(weatherCondition: WeatherConditionEntity)
}