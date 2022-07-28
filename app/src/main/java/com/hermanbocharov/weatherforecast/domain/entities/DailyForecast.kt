package com.hermanbocharov.weatherforecast.domain.entities

data class DailyForecast(
    val forecastTime: Int,
    val timezone: String,
    val minTemp: Int,
    val maxTemp: Int,
    val sunriseTime: Int,
    val sunsetTime: Int,
    val tempUnit: Int
)