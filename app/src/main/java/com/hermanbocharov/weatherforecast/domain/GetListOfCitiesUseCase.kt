package com.hermanbocharov.weatherforecast.domain

class GetListOfCitiesUseCase(
    private val repository: WeatherRepository
) {
    operator fun invoke(city: String) = repository.getListOfCities(city)
}