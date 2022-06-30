package com.hermanbocharov.weatherforecast.domain

import javax.inject.Inject

class GetCurrentLocationUseCase @Inject constructor(
    private val repository: OpenWeatherRepository
) {
    operator fun invoke() = repository.getCurrentLocation()
}