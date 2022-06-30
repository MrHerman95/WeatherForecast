package com.hermanbocharov.weatherforecast.domain

import javax.inject.Inject

class GetListOfCitiesUseCase @Inject constructor(
    private val repository: OpenWeatherRepository
) {
    operator fun invoke(city: String) = repository.getListOfCities(city)
}