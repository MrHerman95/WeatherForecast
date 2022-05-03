package com.hermanbocharov.weatherforecast.data.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "location",
    indices = [Index(value = ["lat", "lon"], unique = true)]
)
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String?
)