package com.hermanbocharov.weatherforecast.domain

class GetListOfCitiesUseCase(
    private val repository: OpenWeatherRepository
) {
    operator fun invoke(city: String) = repository.getListOfCities(city)
}