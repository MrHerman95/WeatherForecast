package com.hermanbocharov.weatherforecast.domain

import io.reactivex.rxjava3.core.Single

interface WeatherRepository {
    fun getCurrentWeatherFromDb(): Single<CurrentWeather>
    fun loadWeatherForecast(): Single<Unit>
    fun getListOfCities(city: String): Single<List<Location>>
    fun getCurrentLocation(): Single<Location>
    fun getCurrentLocationId(): Int
    fun addNewLocation(location: Location)
}