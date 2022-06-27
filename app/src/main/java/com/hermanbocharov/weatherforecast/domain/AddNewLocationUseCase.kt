package com.hermanbocharov.weatherforecast.domain

class AddNewLocationUseCase(
    private val repository: OpenWeatherRepository
) {
    operator fun invoke(location: Location) = repository.addNewLocation(location)
}