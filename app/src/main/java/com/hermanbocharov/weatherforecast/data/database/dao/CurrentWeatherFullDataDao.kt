package com.hermanbocharov.weatherforecast.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.hermanbocharov.weatherforecast.data.database.entities.CurrentWeatherFullData
import io.reactivex.rxjava3.core.Single

@Dao
interface CurrentWeatherFullDataDao {
    @Transaction
    @Query("SELECT * FROM weather_current WHERE location_id=:locationId LIMIT 1")
    fun getCurrentWeatherFullData(locationId: Int): Single<CurrentWeatherFullData>
}