package com.hermanbocharov.weatherforecast.data.database.entities

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
    @ColumnInfo(name = "update_time")
    val updateTime: Int,

    val temp: Int,

    @ColumnInfo(name = "feels_like")
    val feelsLike: Int,

    @ColumnInfo(name = "weather_condition_id")
    val weatherConditionId: Int,

    @PrimaryKey
    @ColumnInfo(name = "location_id")
    val locationId: Int,

    @ColumnInfo(name = "timezone_offset")
    val timezoneOffset: Int,

    @ColumnInfo(name = "timezone_name")
    val timezoneName: String
)