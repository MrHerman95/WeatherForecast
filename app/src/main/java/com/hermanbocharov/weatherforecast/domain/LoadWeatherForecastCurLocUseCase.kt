package com.hermanbocharov.weatherforecast.domain

class LoadWeatherForecastCurLocUseCase(
    private val repository: OpenWeatherRepository
) {
    operator fun invoke() = repository.loadWeatherForecastCurLoc()
}