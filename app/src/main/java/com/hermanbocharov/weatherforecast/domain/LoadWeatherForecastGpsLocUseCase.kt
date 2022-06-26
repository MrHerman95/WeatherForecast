package com.hermanbocharov.weatherforecast.domain

class LoadWeatherForecastGpsLocUseCase(
    private val repository: WeatherRepository
) {
    operator fun invoke() = repository.loadWeatherForecastGpsLoc()
}