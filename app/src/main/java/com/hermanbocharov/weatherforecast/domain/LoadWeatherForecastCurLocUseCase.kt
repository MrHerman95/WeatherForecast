package com.hermanbocharov.weatherforecast.domain

import javax.inject.Inject

class LoadWeatherForecastCurLocUseCase @Inject constructor(
    private val repository: OpenWeatherRepository
) {
    operator fun invoke() = repository.loadWeatherForecastCurLoc()
}