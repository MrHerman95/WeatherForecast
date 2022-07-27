package com.hermanbocharov.weatherforecast.data.mapper

import com.hermanbocharov.weatherforecast.data.database.entities.*
import com.hermanbocharov.weatherforecast.data.network.model.LocationDto
import com.hermanbocharov.weatherforecast.data.network.model.WeatherConditionDto
import com.hermanbocharov.weatherforecast.data.network.model.WeatherForecastDto
import com.hermanbocharov.weatherforecast.domain.entities.CurrentWeather
import com.hermanbocharov.weatherforecast.domain.entities.Direction.EAST
import com.hermanbocharov.weatherforecast.domain.entities.Direction.NORTH
import com.hermanbocharov.weatherforecast.domain.entities.Direction.NORTHEAST
import com.hermanbocharov.weatherforecast.domain.entities.Direction.NORTHWEST
import com.hermanbocharov.weatherforecast.domain.entities.Direction.SOUTH
import com.hermanbocharov.weatherforecast.domain.entities.Direction.SOUTHEAST
import com.hermanbocharov.weatherforecast.domain.entities.Direction.SOUTHWEST
import com.hermanbocharov.weatherforecast.domain.entities.Direction.WEST
import com.hermanbocharov.weatherforecast.domain.entities.HourlyForecast
import com.hermanbocharov.weatherforecast.domain.entities.Location
import com.hermanbocharov.weatherforecast.domain.entities.TemperatureUnit
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.text.Typography.ndash
import kotlin.text.Typography.plusMinus

class OpenWeatherMapper @Inject constructor() {

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

    fun mapWeatherForecastDtoToHourlyForecastEntityList(
        dto: WeatherForecastDto,
        locationId: Int
    ): List<HourlyForecastEntity> {
        val hourlyForecast = mutableListOf<HourlyForecastEntity>()
        for (hour in dto.hourly) {
            val item = HourlyForecastEntity(
                forecastTime = hour.forecastTime,
                locationId = locationId,
                temp = hour.temp.roundToInt(),
                pressure = hour.pressure,
                humidity = hour.humidity,
                cloudiness = hour.clouds,
                uvi = hour.uvi,
                rain = hour.rain?.last1h,
                snow = hour.snow?.last1h,
                windSpeed = hour.windSpeed,
                windDegree = hour.windDeg,
                windGust = hour.windGust,
                weatherConditionId = hour.weather[0].id,
                timezoneName = dto.timezoneName
            )
            hourlyForecast.add(item)
        }
        return hourlyForecast
    }

    fun mapWeatherForecastDtoToDailyForecastEntityList(
        dto: WeatherForecastDto,
        locationId: Int
    ): List<DailyForecastEntity> {
        val dailyForecast = mutableListOf<DailyForecastEntity>()
        for (day in dto.daily) {
            val item = DailyForecastEntity(
                forecastTime = day.forecastTime,
                locationId = locationId,
                sunrise = day.sunrise,
                sunset = day.sunset,
                tempMin = day.temp.min.roundToInt(),
                tempMax = day.temp.max.roundToInt(),
                pressure = day.pressure,
                humidity = day.humidity,
                cloudiness = day.clouds,
                uvi = day.uvi,
                rain = day.rainVolume,
                snow = day.snowVolume,
                windSpeed = day.windSpeed,
                windDegree = day.windDeg,
                windGust = day.windGust,
                weatherConditionId = day.weather[0].id,
                timezoneOffset = dto.timezoneOffset
            )
            dailyForecast.add(item)
        }
        return dailyForecast
    }

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

    fun mapHourlyForecastFullDataToDomain(
        entityList: List<HourlyForecastFullData>,
        tempUnit: Int
    ): List<HourlyForecast> {
        val hourlyForecast = mutableListOf<HourlyForecast>()

        for (hour in entityList) {
            hourlyForecast.add(
                HourlyForecast(
                    forecastTime = hour.hourlyForecast.forecastTime,
                    timezone = hour.hourlyForecast.timezoneName,
                    temp = hour.hourlyForecast.temp,
                    pressure = convertHPaToMmHg(hour.hourlyForecast.pressure),
                    humidity = hour.hourlyForecast.humidity,
                    cloudiness = hour.hourlyForecast.cloudiness,
                    uvi = hour.hourlyForecast.uvi,
                    rain = hour.hourlyForecast.rain,
                    snow = hour.hourlyForecast.snow,
                    windSpeed = hour.hourlyForecast.windSpeed,
                    windDirection = convertWindDegreeToDirection(hour.hourlyForecast.windDegree),
                    windGust = hour.hourlyForecast.windGust,
                    tempUnit = tempUnit,
                    cityName = hour.location.name,
                    description = hour.weatherCondition.main
                )
            )
        }

        return hourlyForecast
    }

    private fun convertCountryCodeToName(code: String): String {
        return Locale("", code).displayCountry
    }

    private fun convertCelsiusToFahrenheit(celsius: Int): Int {
        return (celsius * 1.8).roundToInt() + 32
    }

    private fun convertHPaToMmHg(pressure: Int): Int {
        return (pressure / 1.333).roundToInt()
    }

    private fun convertWindDegreeToDirection(degree: Int): String {
        return when (degree) {
            in 24..68 -> NORTHEAST
            in 69..113 -> EAST
            in 114..158 -> SOUTHEAST
            in 159..203 -> SOUTH
            in 204..248 -> SOUTHWEST
            in 249..293 -> WEST
            in 294..336 -> NORTHWEST
            else -> NORTH
        }
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