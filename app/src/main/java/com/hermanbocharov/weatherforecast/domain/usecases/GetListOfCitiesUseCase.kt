package com.hermanbocharov.weatherforecast.domain.usecases

import com.hermanbocharov.weatherforecast.domain.repository.OpenWeatherRepository
import javax.inject.Inject

class GetListOfCitiesUseCase @Inject constructor(
    private val repository: OpenWeatherRepository
) {
    operator fun invoke(city: String, country: String) = repository.getListOfCities(city, country)
}