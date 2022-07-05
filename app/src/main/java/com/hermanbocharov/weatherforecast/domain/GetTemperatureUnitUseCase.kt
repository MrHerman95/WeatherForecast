package com.hermanbocharov.weatherforecast.domain

import javax.inject.Inject

class GetTemperatureUnitUseCase @Inject constructor(
    private val repository: OpenWeatherRepository
) {
    operator fun invoke() = repository.getTemperatureUnit()
}