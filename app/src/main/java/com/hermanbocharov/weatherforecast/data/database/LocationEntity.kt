package com.hermanbocharov.weatherforecast.data.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "location",
    indices = [Index(value = ["lat", "lon"], unique = true)]
)
data class LocationEntity(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}