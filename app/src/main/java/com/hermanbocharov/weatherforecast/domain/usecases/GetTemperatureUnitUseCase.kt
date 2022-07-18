package com.hermanbocharov.weatherforecast.domain.usecases

import com.hermanbocharov.weatherforecast.domain.repository.OpenWeatherRepository
import javax.inject.Inject

class GetTemperatureUnitUseCase @Inject constructor(
    private val repository: OpenWeatherRepository
) {
    operator fun invoke() = repository.getTemperatureUnit()
}