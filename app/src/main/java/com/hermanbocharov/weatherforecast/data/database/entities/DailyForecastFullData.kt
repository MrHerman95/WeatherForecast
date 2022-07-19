package com.hermanbocharov.weatherforecast.data.database.entities

import androidx.room.Embedded
import androidx.room.Relation

data class DailyForecastFullData(
    @Embedded val dailyForecast: DailyForecastEntity,

    @Relation(
        entity = LocationEntity::class, parentColumn = "location_id", entityColumn = "id"
    )
    val location: LocationEntity,

    @Relation(
        entity = WeatherConditionEntity::class,
        parentColumn = "weather_condition_id",
        entityColumn = "id"
    )
    val weatherCondition: WeatherConditionEntity
)