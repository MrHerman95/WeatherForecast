package com.hermanbocharov.weatherforecast.domain.usecases

import com.hermanbocharov.weatherforecast.domain.entities.Location
import com.hermanbocharov.weatherforecast.domain.repository.OpenWeatherRepository
import javax.inject.Inject

class SetNewLocationUseCase @Inject constructor(
    private val repository: OpenWeatherRepository
) {
    operator fun invoke(location: Location) = repository.setNewLocation(location)
}