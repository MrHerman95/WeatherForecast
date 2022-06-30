package com.hermanbocharov.weatherforecast.domain

import javax.inject.Inject

class GetCurrentWeatherUseCase @Inject constructor(
    private val repository: OpenWeatherRepository
) {
    operator fun invoke() = repository.getCurrentWeather()
}