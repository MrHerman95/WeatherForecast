package com.hermanbocharov.weatherforecast.domain.usecases.preferences

import com.hermanbocharov.weatherforecast.domain.repository.OpenWeatherRepository
import javax.inject.Inject

class SaveSpeedUnitUseCase @Inject constructor(
    private val repository: OpenWeatherRepository
) {
    operator fun invoke(unitId: Int) = repository.saveSpeedUnit(unitId)
}