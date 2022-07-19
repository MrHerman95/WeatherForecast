package com.hermanbocharov.weatherforecast.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "hourly_forecast",
    primaryKeys = [
        "forecast_time",
        "location_id"
    ],
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
data class HourlyForecastEntity(
    @ColumnInfo(name = "forecast_time")
    val forecastTime: Int,

    @ColumnInfo(name = "location_id")
    val locationId: Int,

    val temp: Int,
    val pressure: Int,
    val humidity: Int,
    val cloudiness: Int,
    val uvi: Double,
    val rain: Double?,
    val snow: Double?,

    @ColumnInfo(name = "wind_speed")
    val windSpeed: Double,

    @ColumnInfo(name = "wind_degree")
    val windDegree: Int,

    @ColumnInfo(name = "wind_gust")
    val windGust: Double?,

    @ColumnInfo(name = "weather_condition_id")
    val weatherConditionId: Int,

    @ColumnInfo(name = "timezone_offset")
    val timezoneOffset: Int
)