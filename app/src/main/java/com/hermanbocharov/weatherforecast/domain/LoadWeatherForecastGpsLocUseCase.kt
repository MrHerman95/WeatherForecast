package com.hermanbocharov.weatherforecast.domain

import javax.inject.Inject

class LoadWeatherForecastGpsLocUseCase @Inject constructor(
    private val repository: OpenWeatherRepository
) {
    operator fun invoke() = repository.loadWeatherForecastGpsLoc()
}