package com.hermanbocharov.weatherforecast.domain

class LoadWeatherForecastUseCase(
    private val repository: WeatherRepository
) {
    operator fun invoke() = repository.loadWeatherForecast()
}