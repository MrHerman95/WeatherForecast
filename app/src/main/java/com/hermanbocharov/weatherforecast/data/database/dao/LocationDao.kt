package com.hermanbocharov.weatherforecast.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hermanbocharov.weatherforecast.data.database.entities.LocationEntity
import io.reactivex.rxjava3.core.Single

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocation(location: LocationEntity): Single<Long>

    @Query("SELECT * FROM location WHERE lat=:lat AND lon=:lon LIMIT 1")
    fun getLocation(lat: Double, lon: Double): Single<LocationEntity>

    @Query("SELECT * FROM location WHERE id=:locationId LIMIT 1")
    fun getLocation(locationId: Int): Single<LocationEntity>
}