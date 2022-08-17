package com.hermanbocharov.weatherforecast.domain.entities

data class HourlyForecast(
    val forecastTime: Int,
    val timezone: String,
    val temp: Int,
    val pressure: Double,
    val humidity: Int,
    val cloudiness: Int,
    val uvi: Double,
    val rain: Double?,
    val snow: Double?,
    val windSpeed: Double,
    val windDirection: String,
    val windGust: Double?,
    val tempUnit: Int,
    val pressureUnit: Int,
    val precipitationUnit: Int,
    val windSpeedUnit: Int,
    val cityName: String,
    val description: String,
    var isSelected: Boolean = false
)