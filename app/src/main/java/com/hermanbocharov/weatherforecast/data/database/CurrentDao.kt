package com.hermanbocharov.weatherforecast.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CurrentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrentWeather(current: CurrentDbModel)

    @Query("SELECT * FROM weather_current WHERE location_id=:locationId LIMIT 1")
    fun getCurrentWeather(locationId: Int): LiveData<CurrentDbModel>
}