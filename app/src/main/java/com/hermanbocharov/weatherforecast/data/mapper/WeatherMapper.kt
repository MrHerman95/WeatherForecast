package com.hermanbocharov.weatherforecast.data.mapper

import com.hermanbocharov.weatherforecast.data.database.CurrentWeatherFullData
import com.hermanbocharov.weatherforecast.data.database.CurrentWeatherEntity
import com.hermanbocharov.weatherforecast.data.database.LocationEntity
import com.hermanbocharov.weatherforecast.data.database.WeatherConditionEntity
import com.hermanbocharov.weatherforecast.data.network.model.CurrentDto
import com.hermanbocharov.weatherforecast.data.network.model.LocationDto
import com.hermanbocharov.weatherforecast.data.network.model.WeatherConditionDto
import com.hermanbocharov.weatherforecast.domain.CurrentWeather
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

    fun mapEntityToCurrentWeatherDomain(entity: CurrentWeatherFullData?): CurrentWeather? {
        entity?.let {
            return CurrentWeather(
                temp = it.currentWeather.temp,
                feelsLike = it.currentWeather.feelsLike,
                cityName = it.location.name,
                description = it.weatherCondition.description,
                updateTime = it.currentWeather.updateTime
            )
        }
        return null
    }
}