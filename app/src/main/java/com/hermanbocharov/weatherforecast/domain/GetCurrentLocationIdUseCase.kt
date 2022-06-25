package com.hermanbocharov.weatherforecast.domain

class GetCurrentLocationIdUseCase(
    private val repository: WeatherRepository
) {
    operator fun invoke() = repository.getCurrentLocationId()
}