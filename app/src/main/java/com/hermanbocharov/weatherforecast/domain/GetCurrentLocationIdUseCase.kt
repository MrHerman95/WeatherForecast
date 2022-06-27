package com.hermanbocharov.weatherforecast.domain

class GetCurrentLocationIdUseCase(
    private val repository: OpenWeatherRepository
) {
    operator fun invoke() = repository.getCurrentLocationId()
}