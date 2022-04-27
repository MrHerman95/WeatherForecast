package com.hermanbocharov.weatherforecast.data.network.model

data class FullWeatherInfoDto(
    private val location: LocationDto? = null,
    private val weatherForecast: WeatherForecastDto? = null
)