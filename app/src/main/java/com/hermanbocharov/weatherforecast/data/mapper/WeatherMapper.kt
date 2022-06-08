package com.hermanbocharov.weatherforecast.data.mapper

import com.hermanbocharov.weatherforecast.data.database.CurrentWeatherFullData
import com.hermanbocharov.weatherforecast.data.database.CurrentWeatherEntity
import com.hermanbocharov.weatherforecast.data.database.LocationEntity
import com.hermanbocharov.weatherforecast.data.database.WeatherConditionEntity
import com.hermanbocharov.weatherforecast.data.network.model.CurrentDto
import com.hermanbocharov.weatherforecast.data.network.model.LocationDto
import com.hermanbocharov.weatherforecast.data.network.model.WeatherConditionDto
import com.hermanbocharov.weatherforecast.domain.CurrentWeather
import com.hermanbocharov.weatherforecast.domain.Location
import kotlin.math.roundToInt

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
        temp = dto.temp.roundToInt(),
        feelsLike = dto.feelsLike.roundToInt(),
        weatherConditionId = dto.weather[0].id,
        locationId = locationId
    )

    fun mapEntityToCurrentWeatherDomain(entity: CurrentWeatherFullData): CurrentWeather {
        return CurrentWeather(
            temp = entity.currentWeather.temp,
            feelsLike = entity.currentWeather.feelsLike,
            cityName = entity.location.name,
            description = entity.weatherCondition.description,
            updateTime = entity.currentWeather.updateTime
        )
    }

    fun mapDtoToLocationDomain(dto: LocationDto): Location {
        return Location(
            name = dto.name,
            lat = dto.lat,
            lon = dto.lon,
            country = dto.country,
            state = dto.state
        )
    }
}