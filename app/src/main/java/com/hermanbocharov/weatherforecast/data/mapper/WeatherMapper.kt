package com.hermanbocharov.weatherforecast.data.mapper

import com.hermanbocharov.weatherforecast.data.database.CurrentWeatherEntity
import com.hermanbocharov.weatherforecast.data.database.CurrentWeatherFullData
import com.hermanbocharov.weatherforecast.data.database.LocationEntity
import com.hermanbocharov.weatherforecast.data.database.WeatherConditionEntity
import com.hermanbocharov.weatherforecast.data.network.model.CurrentDto
import com.hermanbocharov.weatherforecast.data.network.model.LocationDto
import com.hermanbocharov.weatherforecast.data.network.model.WeatherConditionDto
import com.hermanbocharov.weatherforecast.domain.CurrentWeather
import com.hermanbocharov.weatherforecast.domain.Location
import com.hermanbocharov.weatherforecast.domain.TemperatureUnit
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

class WeatherMapper @Inject constructor() {

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

    fun mapEntityToCurrentWeatherDomain(
        entity: CurrentWeatherFullData,
        tempUnit: Int
    ): CurrentWeather {
        var temperature = entity.currentWeather.temp
        var feelsLike = entity.currentWeather.feelsLike

        if (tempUnit == TemperatureUnit.FAHRENHEIT) {
            temperature = convertCelsiusToFahrenheit(temperature)
            feelsLike = convertCelsiusToFahrenheit(feelsLike)
        }

        return CurrentWeather(
            temp = temperature,
            feelsLike = feelsLike,
            tempUnit = tempUnit,
            cityName = entity.location.name,
            description = entity.weatherCondition.description.replaceFirstChar {
                it.titlecase(Locale.getDefault())
            },
            updateTime = entity.currentWeather.updateTime
        )
    }

    fun mapEntityToLocationDomain(entity: LocationEntity): Location {
        return Location(
            name = entity.name,
            lat = entity.lat,
            lon = entity.lon,
            country = entity.country,
            state = entity.state
        )
    }

    fun mapLocationDomainToEntity(domain: Location): LocationEntity {
        return LocationEntity(
            name = domain.name,
            lat = domain.lat,
            lon = domain.lon,
            country = domain.country,
            state = domain.state
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

    private fun convertCelsiusToFahrenheit(celsius: Int): Int {
        return (celsius * 1.8).roundToInt() + 32
    }
}