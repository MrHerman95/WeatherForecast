package com.hermanbocharov.weatherforecast.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_condition")
data class WeatherConditionEntity(
    @PrimaryKey
    val id: Int,
    val main: String,
    val description: String
)