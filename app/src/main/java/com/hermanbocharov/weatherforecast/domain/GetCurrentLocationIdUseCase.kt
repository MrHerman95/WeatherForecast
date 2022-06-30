package com.hermanbocharov.weatherforecast.domain

import javax.inject.Inject

class GetCurrentLocationIdUseCase @Inject constructor(
    private val repository: OpenWeatherRepository
) {
    operator fun invoke() = repository.getCurrentLocationId()
}