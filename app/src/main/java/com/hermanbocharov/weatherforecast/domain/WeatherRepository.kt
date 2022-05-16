package com.hermanbocharov.weatherforecast.domain

import io.reactivex.rxjava3.core.Single

interface WeatherRepository {
    fun getCurrentWeatherFromDb(): Single<CurrentWeather>
    fun loadWeatherForecast(): Single<Unit>
}