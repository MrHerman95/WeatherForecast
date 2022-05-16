package com.hermanbocharov.weatherforecast.domain

class GetCurrentWeatherUseCase(
    private val repository: WeatherRepository
) {
    operator fun invoke() = repository.getCurrentWeatherFromDb()
}