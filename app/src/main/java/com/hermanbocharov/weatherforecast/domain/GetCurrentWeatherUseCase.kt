package com.hermanbocharov.weatherforecast.domain

class GetCurrentWeatherUseCase(
    private val repository: OpenWeatherRepository
) {
    operator fun invoke() = repository.getCurrentWeather()
}