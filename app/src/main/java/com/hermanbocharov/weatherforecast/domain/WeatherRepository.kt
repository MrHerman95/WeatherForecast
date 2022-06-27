package com.hermanbocharov.weatherforecast.domain

import io.reactivex.rxjava3.core.Single

interface WeatherRepository {
    fun getCurrentWeather(): Single<CurrentWeather>
    fun loadWeatherForecastGpsLoc(): Single<Unit>
    fun loadWeatherForecastCurLoc(): Single<Unit>
    fun getListOfCities(city: String): Single<List<Location>>
    fun getCurrentLocation(): Single<Location>
    fun getCurrentLocationId(): Int
    fun addNewLocation(location: Location): Single<Unit>
}