package com.hermanbocharov.weatherforecast.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CurrentWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrentWeather(current: CurrentWeatherEntity)

    @Query("SELECT * FROM weather_current WHERE location_id=:locationId LIMIT 1")
    fun getCurrentWeather(locationId: Int): LiveData<CurrentWeatherEntity>
}