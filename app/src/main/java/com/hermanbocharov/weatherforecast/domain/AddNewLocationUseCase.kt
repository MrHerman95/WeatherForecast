package com.hermanbocharov.weatherforecast.domain

class AddNewLocationUseCase(
    private val repository: WeatherRepository
) {
    operator fun invoke(location: Location) = repository.addNewLocation(location)
}