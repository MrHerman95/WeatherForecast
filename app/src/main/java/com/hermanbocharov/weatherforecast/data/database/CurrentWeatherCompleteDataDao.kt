package com.hermanbocharov.weatherforecast.data.database

import androidx.lifecycle.LiveData
import androidx.room.Query
import androidx.room.Transaction

interface CurrentWeatherCompleteDataDao {
    @Transaction
    @Query("SELECT * FROM weather_current")
    fun getCurrentWeatherCompleteData(): LiveData<List<CurrentWeatherCompleteData>>
}