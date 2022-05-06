package com.hermanbocharov.weatherforecast.domain

class LoadDataUseCase(
    private val repository: WeatherRepository
) {
    operator fun invoke() = repository.loadData()
}