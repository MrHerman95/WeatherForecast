package com.hermanbocharov.weatherforecast.data.database

import androidx.room.Embedded
import androidx.room.Relation

data class CurrentWeatherFullData(
    @Embedded val currentWeather: CurrentWeatherEntity,

    @Relation(
        entity = LocationEntity::class, parentColumn = "location_id", entityColumn = "id"
    )
    val location: LocationEntity,

    @Relation(
        entity = WeatherConditionEntity::class, parentColumn = "weather_condition_id", entityColumn = "id"
    )
    val weatherCondition: WeatherConditionEntity
)
