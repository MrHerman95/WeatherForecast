package com.hermanbocharov.weatherforecast.data.mapper

import com.hermanbocharov.weatherforecast.data.database.CurrentWeatherEntity
import com.hermanbocharov.weatherforecast.data.database.CurrentWeatherFullData
import com.hermanbocharov.weatherforecast.data.database.LocationEntity
import com.hermanbocharov.weatherforecast.data.database.WeatherConditionEntity
import com.hermanbocharov.weatherforecast.data.network.model.LocationDto
import com.hermanbocharov.weatherforecast.data.network.model.WeatherConditionDto
import com.hermanbocharov.weatherforecast.data.network.model.WeatherForecastDto
import com.hermanbocharov.weatherforecast.domain.CurrentWeather
import com.hermanbocharov.weatherforecast.domain.Location
import com.hermanbocharov.weatherforecast.domain.TemperatureUnit
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.text.Typography.ndash
import kotlin.text.Typography.plusMinus

class WeatherMapper @Inject constructor() {

    fun mapDtoToLocationEntity(dto: LocationDto) = LocationEntity(
        name = dto.name,
        lat = dto.lat,
        lon = dto.lon,
        country = convertCountryCodeToName(dto.country),
        state = dto.state
    )

    fun mapDtoToLocationDomain(dto: LocationDto): Location {
        return Location(
            name = dto.name,
            lat = dto.lat,
            lon = dto.lon,
            country = convertCountryCodeToName(dto.country),
            state = dto.state
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

    fun mapWeatherConditionDtoToEntity(dto: WeatherConditionDto) = WeatherConditionEntity(
        id = dto.id,
        main = dto.main,
        description = dto.description
    )

    fun mapWeatherForecastDtoToCurrentWeatherEntity(dto: WeatherForecastDto, locationId: Int) =
        CurrentWeatherEntity(
            updateTime = dto.current.updateTime,
            temp = dto.current.temp.roundToInt(),
            feelsLike = dto.current.feelsLike.roundToInt(),
            weatherConditionId = dto.current.weather[0].id,
            locationId = locationId,
            timezoneOffset = dto.timezoneOffset,
            timezoneName = dto.timezoneName
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

        convertTimezoneOffsetToTimezone(entity.currentWeather.timezoneOffset)

        return CurrentWeather(
            temp = temperature,
            feelsLike = feelsLike,
            tempUnit = tempUnit,
            cityName = entity.location.name,
            description = entity.weatherCondition.description.replaceFirstChar {
                it.titlecase(Locale.getDefault())
            },
            timezone = convertTimezoneOffsetToTimezone(entity.currentWeather.timezoneOffset),
            timezoneName = entity.currentWeather.timezoneName
        )
    }

    private fun convertCountryCodeToName(code: String): String {
        return Locale("", code).displayCountry
    }

    private fun convertCelsiusToFahrenheit(celsius: Int): Int {
        return (celsius * 1.8).roundToInt() + 32
    }

    private fun convertTimezoneOffsetToTimezone(offset: Int): String {
        val hours = TimeUnit.SECONDS.toHours(offset.toLong())
        val minutes = TimeUnit.SECONDS.toMinutes(offset - TimeUnit.HOURS.toSeconds(hours))
        val sign = when {
            offset < 0 -> ndash
            offset > 0 -> "+"
            else -> plusMinus
        }

        return String.format("UTC%s%02d:%02d", sign, abs(hours), abs(minutes))
    }
}