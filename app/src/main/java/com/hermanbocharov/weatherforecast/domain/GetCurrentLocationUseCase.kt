package com.hermanbocharov.weatherforecast.domain

class GetCurrentLocationUseCase(
    private val repository: WeatherRepository
) {
    operator fun invoke() = repository.getCurrentLocation()
}