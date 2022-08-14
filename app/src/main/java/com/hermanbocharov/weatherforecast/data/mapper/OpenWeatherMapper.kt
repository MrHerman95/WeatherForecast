package com.hermanbocharov.weatherforecast.data.mapper

import com.hermanbocharov.weatherforecast.data.database.entities.*
import com.hermanbocharov.weatherforecast.data.geolocation.CountriesISO
import com.hermanbocharov.weatherforecast.data.network.model.LocationDto
import com.hermanbocharov.weatherforecast.data.network.model.WeatherConditionDto
import com.hermanbocharov.weatherforecast.data.network.model.WeatherForecastDto
import com.hermanbocharov.weatherforecast.domain.entities.*
import com.hermanbocharov.weatherforecast.domain.entities.Direction.EAST
import com.hermanbocharov.weatherforecast.domain.entities.Direction.NORTH
import com.hermanbocharov.weatherforecast.domain.entities.Direction.NORTHEAST
import com.hermanbocharov.weatherforecast.domain.entities.Direction.NORTHWEST
import com.hermanbocharov.weatherforecast.domain.entities.Direction.SOUTH
import com.hermanbocharov.weatherforecast.domain.entities.Direction.SOUTHEAST
import com.hermanbocharov.weatherforecast.domain.entities.Direction.SOUTHWEST
import com.hermanbocharov.weatherforecast.domain.entities.Direction.WEST
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
        for (i in 0 until dto.hourly.size step 2) {
            val item = HourlyForecastEntity(
                forecastTime = dto.hourly[i].forecastTime,
                locationId = locationId,
                temp = dto.hourly[i].temp.roundToInt(),
                pressure = dto.hourly[i].pressure,
                humidity = dto.hourly[i].humidity,
                cloudiness = dto.hourly[i].clouds,
                uvi = dto.hourly[i].uvi,
                rain = dto.hourly[i].rain?.last1h,
                snow = dto.hourly[i].snow?.last1h,
                windSpeed = dto.hourly[i].windSpeed,
                windDegree = dto.hourly[i].windDeg,
                windGust = dto.hourly[i].windGust,
                weatherConditionId = dto.hourly[i].weather[0].id,
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
                timezoneName = dto.timezoneName
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
        tempUnit: Int,
        speedUnit: Int,
        precipitationUnit: Int,
        pressureUnit: Int
    ): List<HourlyForecast> {
        val hourlyForecast = mutableListOf<HourlyForecast>()

        for (hour in entityList) {
            var temperature = hour.hourlyForecast.temp
            if (tempUnit == TemperatureUnit.FAHRENHEIT) {
                temperature = convertCelsiusToFahrenheit(temperature)
            }

            var windSpeed = hour.hourlyForecast.windSpeed
            var windGust = hour.hourlyForecast.windGust
            when (speedUnit) {
                SpeedUnit.KILOMETERS_PER_HOUR -> {
                    if (windGust != null) windGust = convertMsToKph(windGust)
                    windSpeed = convertMsToKph(windSpeed)
                }
                SpeedUnit.MILES_PER_HOUR -> {
                    if (windGust != null) windGust = convertMsToMph(windGust)
                    windSpeed = convertMsToMph(windSpeed)
                }
            }

            var rain = hour.hourlyForecast.rain
            var snow = hour.hourlyForecast.snow
            if (precipitationUnit == PrecipitationUnit.INCHES) {
                if (rain != null) rain = convertMmToIn(rain)
                if (snow != null) snow = convertMmToIn(snow)
            }

            var pressure = hour.hourlyForecast.pressure.toDouble()
            when (pressureUnit) {
                PressureUnit.MILLIMETERS_HG -> pressure = convertHPaToMmHg(pressure)
                PressureUnit.INCHES_HG -> pressure = convertHPaToInHg(pressure)
            }

            hourlyForecast.add(
                HourlyForecast(
                    forecastTime = hour.hourlyForecast.forecastTime,
                    timezone = hour.hourlyForecast.timezoneName,
                    temp = temperature,
                    pressure = pressure,
                    humidity = hour.hourlyForecast.humidity,
                    cloudiness = hour.hourlyForecast.cloudiness,
                    uvi = hour.hourlyForecast.uvi,
                    rain = rain,
                    snow = snow,
                    windSpeed = windSpeed,
                    windDirection = convertWindDegreeToDirection(hour.hourlyForecast.windDegree),
                    windGust = windGust,
                    tempUnit = tempUnit,
                    cityName = hour.location.name,
                    description = hour.weatherCondition.main
                )
            )
        }
        return hourlyForecast
    }

    fun mapDailyForecastFullDataToDomain(
        entityList: List<DailyForecastFullData>,
        tempUnit: Int
    ): List<DailyForecast> {
        val dailyForecast = mutableListOf<DailyForecast>()

        for (day in entityList) {
            var temperatureMin = day.dailyForecast.tempMin
            var temperatureMax = day.dailyForecast.tempMax
            if (tempUnit == TemperatureUnit.FAHRENHEIT) {
                temperatureMin = convertCelsiusToFahrenheit(temperatureMin)
                temperatureMax = convertCelsiusToFahrenheit(temperatureMax)
            }

            dailyForecast.add(
                DailyForecast(
                    forecastTime = day.dailyForecast.forecastTime,
                    timezone = day.dailyForecast.timezoneName,
                    minTemp = temperatureMin,
                    maxTemp = temperatureMax,
                    sunriseTime = day.dailyForecast.sunrise,
                    sunsetTime = day.dailyForecast.sunset,
                    tempUnit = tempUnit
                )
            )
        }
        return dailyForecast
    }

    fun mapCountryNameToISOCode(name: String): String {
        val countryName = name.lowercase().removePrefix("the ")
        return CountriesISO.countryNameISO[countryName] ?: ""
    }

    private fun convertCountryCodeToName(code: String): String {
        return Locale("", code).displayCountry
    }

    private fun convertMsToKph(speed: Double): Double {
        return speed * 3.6
    }

    private fun convertMsToMph(speed: Double): Double {
        return speed * 2.237
    }

    private fun convertCelsiusToFahrenheit(celsius: Int): Int {
        return (celsius * 1.8).roundToInt() + 32
    }

    private fun convertMmToIn(precipitation: Double): Double {
        return precipitation / 25.4
    }

    private fun convertHPaToMmHg(pressure: Double): Double {
        return pressure / 1.333224
    }

    private fun convertHPaToInHg(pressure: Double): Double {
        return pressure / 33.863887
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