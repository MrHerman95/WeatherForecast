package com.hermanbocharov.weatherforecast.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "weather_current",
    foreignKeys = [
        ForeignKey(
            entity = LocationEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("location_id"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WeatherConditionEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("weather_condition_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CurrentWeatherEntity(
    @PrimaryKey
    val id: Int,

    @ColumnInfo(name = "current_time")
    val currentTime: Int,

    val temp: Double,

    @ColumnInfo(name = "feels_like")
    val feelsLike: Double,

    @ColumnInfo(name = "weather_condition_id")
    val weatherConditionId: Int,

    @ColumnInfo(name = "location_id")
    val locationId: Int
)