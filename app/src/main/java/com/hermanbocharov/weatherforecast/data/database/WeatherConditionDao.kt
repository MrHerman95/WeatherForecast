package com.hermanbocharov.weatherforecast.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface WeatherConditionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeatherCondition(weatherCondition: WeatherConditionDbModel)
}