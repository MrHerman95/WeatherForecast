package com.hermanbocharov.weatherforecast.data.mapper

import com.hermanbocharov.weatherforecast.data.database.CurrentWeatherEntity
import com.hermanbocharov.weatherforecast.data.database.LocationEntity
import com.hermanbocharov.weatherforecast.data.database.WeatherConditionEntity
import com.hermanbocharov.weatherforecast.data.network.model.CurrentDto
import com.hermanbocharov.weatherforecast.data.network.model.LocationDto
import com.hermanbocharov.weatherforecast.data.network.model.WeatherConditionDto

class WeatherMapper {

    fun mapLocationDtoToEntity(dto: LocationDto) = LocationEntity(
        name = dto.name,
        lat = dto.lat,
        lon = dto.lon,
        country = dto.country,
        state = dto.state
    )

    fun mapWeatherConditionDtoToEntity(dto: WeatherConditionDto) = WeatherConditionEntity(
        id = dto.id,
        main = dto.main,
        description = dto.description
    )

    fun mapCurrentWeatherDtoToEntity(dto: CurrentDto, locationId: Int) = CurrentWeatherEntity(
        updateTime = dto.updateTime,
        temp = dto.temp,
        feelsLike = dto.feelsLike,
        weatherConditionId = dto.weather[0].id,
        locationId = locationId
    )
}