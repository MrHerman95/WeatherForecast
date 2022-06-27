package com.hermanbocharov.weatherforecast.domain

class GetCurrentLocationUseCase(
    private val repository: OpenWeatherRepository
) {
    operator fun invoke() = repository.getCurrentLocation()
}