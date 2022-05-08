package com.hermanbocharov.weatherforecast.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.hermanbocharov.weatherforecast.data.repository.WeatherRepositoryImpl
import com.hermanbocharov.weatherforecast.domain.GetCurrentWeatherUseCase
import com.hermanbocharov.weatherforecast.domain.LoadDataUseCase

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WeatherRepositoryImpl(application)
    private val loadDataUseCase = LoadDataUseCase(repository)
    private val getCurrentWeatherUseCase = GetCurrentWeatherUseCase(repository)

    fun getCurrentWeather() = getCurrentWeatherUseCase()

    init {
        loadDataUseCase()
    }
}