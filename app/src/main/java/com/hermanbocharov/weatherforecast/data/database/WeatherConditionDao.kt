package com.hermanbocharov.weatherforecast.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface WeatherConditionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertWeatherCondition(weatherCondition: WeatherConditionEntity)
}