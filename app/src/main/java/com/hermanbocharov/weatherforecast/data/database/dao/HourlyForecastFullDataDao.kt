package com.hermanbocharov.weatherforecast.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.hermanbocharov.weatherforecast.data.database.entities.HourlyForecastFullData
import io.reactivex.rxjava3.core.Single

@Dao
interface HourlyForecastFullDataDao {
    @Transaction
    @Query("SELECT * FROM hourly_forecast WHERE location_id=:locationId LIMIT $MAX_HOURS_FORECAST")
    fun getHourlyForecastFullData(locationId: Int): Single<List<HourlyForecastFullData>>

    companion object {
        private const val MAX_HOURS_FORECAST = 48
    }
}