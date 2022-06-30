package com.hermanbocharov.weatherforecast.domain

import javax.inject.Inject

class AddNewLocationUseCase @Inject constructor(
    private val repository: OpenWeatherRepository
) {
    operator fun invoke(location: Location) = repository.addNewLocation(location)
}