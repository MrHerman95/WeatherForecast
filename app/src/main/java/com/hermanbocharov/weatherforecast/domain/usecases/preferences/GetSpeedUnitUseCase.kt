package com.hermanbocharov.weatherforecast.domain.usecases.preferences

import com.hermanbocharov.weatherforecast.domain.repository.OpenWeatherRepository
import javax.inject.Inject

class GetSpeedUnitUseCase @Inject constructor(
    private val repository: OpenWeatherRepository
) {
    operator fun invoke() = repository.getSpeedUnit()
}