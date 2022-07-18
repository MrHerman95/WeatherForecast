package com.hermanbocharov.weatherforecast.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.hermanbocharov.weatherforecast.data.database.entities.CurrentWeatherEntity

@Dao
interface CurrentWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrentWeather(current: CurrentWeatherEntity)
}