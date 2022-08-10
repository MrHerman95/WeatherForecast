package com.hermanbocharov.weatherforecast.data.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "location",
    indices = [Index(value = ["lat", "lon"], unique = true)]
)
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String?,
    val isPinned: Boolean = false
)