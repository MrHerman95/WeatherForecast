package com.hermanbocharov.weatherforecast.data.database.dao

import androidx.room.*
import com.hermanbocharov.weatherforecast.data.database.entities.LocationEntity
import io.reactivex.rxjava3.core.Single

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocation(location: LocationEntity): Single<Long>

    @Query("SELECT * FROM location WHERE id=:locationId LIMIT 1")
    fun getLocation(locationId: Int): Single<LocationEntity>

    @Query("SELECT * FROM location ORDER BY isPinned DESC, id DESC LIMIT 5")
    fun getListOfRecentCities(): Single<List<LocationEntity>>

    @Update
    fun updateLocation(location: LocationEntity): Single<Unit>
}