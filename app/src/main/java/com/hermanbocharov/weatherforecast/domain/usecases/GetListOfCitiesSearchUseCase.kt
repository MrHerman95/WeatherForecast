package com.hermanbocharov.weatherforecast.domain.usecases

import com.hermanbocharov.weatherforecast.domain.repository.OpenWeatherRepository
import javax.inject.Inject

class GetListOfCitiesSearchUseCase @Inject constructor(
    private val repository: OpenWeatherRepository
) {
    operator fun invoke(city: String, country: String) = repository.getListOfCitiesSearch(city, country)
}