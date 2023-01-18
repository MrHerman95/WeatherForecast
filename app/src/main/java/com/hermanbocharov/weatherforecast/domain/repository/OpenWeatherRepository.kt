package com.hermanbocharov.weatherforecast.domain.repository

import com.hermanbocharov.weatherforecast.domain.entities.CurrentWeather
import com.hermanbocharov.weatherforecast.domain.entities.DailyForecast
import com.hermanbocharov.weatherforecast.domain.entities.HourlyForecast
import com.hermanbocharov.weatherforecast.domain.entities.Location
import io.reactivex.rxjava3.core.Single

interface OpenWeatherRepository {
    fun getCurrentWeather(): Single<CurrentWeather>
    fun getHourlyForecast(): Single<List<HourlyForecast>>
    fun getDailyForecast(): Single<List<DailyForecast>>
    fun loadWeatherForecastGpsLoc(): Single<Unit>
    fun loadWeatherForecastCurLoc(): Single<Unit>
    fun getListOfCities(city: String, country: String): Single<List<Location>>
    fun getCurrentLocation(): Single<Location>
    fun setNewLocation(location: Location): Single<Unit>
    fun getCurrentLocationId(): Int
    fun getSpeedUnit(): Int
    fun saveSpeedUnit(unitId: Int)
    fun getTemperatureUnit(): Int
    fun saveTemperatureUnit(unitId: Int)
    fun getPrecipitationUnit(): Int
    fun savePrecipitationUnit(unitId: Int)
    fun getPressureUnit(): Int
    fun savePressureUnit(unitId: Int)
}