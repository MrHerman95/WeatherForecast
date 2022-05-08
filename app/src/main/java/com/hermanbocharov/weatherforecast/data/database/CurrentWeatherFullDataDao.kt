package com.hermanbocharov.weatherforecast.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface CurrentWeatherFullDataDao {
    @Transaction
    @Query("SELECT * FROM weather_current")
    fun getCurrentWeatherCompleteData(): LiveData<List<CurrentWeatherFullData>>
}