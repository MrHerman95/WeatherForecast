package com.hermanbocharov.weatherforecast.domain

data class CurrentWeather(
    val temp: Int,
    val feelsLike: Int,
    val tempUnit: Int,
    val cityName: String,
    val description: String,
    val timezone: String,
    val timezoneName: String
)