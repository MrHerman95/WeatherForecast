package com.hermanbocharov.weatherforecast.domain

import androidx.lifecycle.LiveData

interface WeatherRepository {
    fun getCurrentWeather(): LiveData<CurrentWeather>
    fun loadData()
}