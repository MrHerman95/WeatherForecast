package com.hermanbocharov.weatherforecast.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.hermanbocharov.weatherforecast.data.database.entities.DailyForecastFullData
import io.reactivex.rxjava3.core.Single

@Dao
interface DailyForecastFullDataDao {
    @Transaction
    @Query("SELECT * FROM daily_forecast WHERE location_id=:locationId LIMIT $MAX_DAYS_FORECAST")
    fun getDailyForecastFullData(locationId: Int): Single<List<DailyForecastFullData>>

    companion object {
        private const val MAX_DAYS_FORECAST = 8
    }
}