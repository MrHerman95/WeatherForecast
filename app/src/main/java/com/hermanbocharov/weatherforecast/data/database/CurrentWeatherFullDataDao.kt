package com.hermanbocharov.weatherforecast.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import io.reactivex.rxjava3.core.Single

@Dao
interface CurrentWeatherFullDataDao {
    @Transaction
    @Query("SELECT * FROM weather_current WHERE location_id=:locationId LIMIT 1")
    fun getCurrentWeatherFullData(locationId: Int): Single<CurrentWeatherFullData>
}