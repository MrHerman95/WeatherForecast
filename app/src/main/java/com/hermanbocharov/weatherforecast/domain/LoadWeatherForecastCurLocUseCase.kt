package com.hermanbocharov.weatherforecast.domain

class LoadWeatherForecastCurLocUseCase(
    private val repository: WeatherRepository
) {
    operator fun invoke() = repository.loadWeatherForecastCurLoc()
}