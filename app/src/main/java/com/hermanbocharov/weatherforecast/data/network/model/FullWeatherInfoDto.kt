package com.hermanbocharov.weatherforecast.data.network.model

data class FullWeatherInfoDto(
    val location: LocationDto,
    val weatherForecast: WeatherForecastDto
)