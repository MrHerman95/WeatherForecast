package com.hermanbocharov.weatherforecast.domain

class LoadWeatherForecastGpsLocUseCase(
    private val repository: OpenWeatherRepository
) {
    operator fun invoke() = repository.loadWeatherForecastGpsLoc()
}