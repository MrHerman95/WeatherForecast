package com.hermanbocharov.weatherforecast.data.repository

import android.app.Application
import android.util.Log
import com.hermanbocharov.weatherforecast.data.database.AppDatabase
import com.hermanbocharov.weatherforecast.data.geolocation.FusedLocationDataSource
import com.hermanbocharov.weatherforecast.data.mapper.WeatherMapper
import com.hermanbocharov.weatherforecast.data.network.ApiFactory
import com.hermanbocharov.weatherforecast.data.network.model.FullWeatherInfoDto
import com.hermanbocharov.weatherforecast.data.preferences.PreferenceManager
import com.hermanbocharov.weatherforecast.domain.CurrentWeather
import com.hermanbocharov.weatherforecast.domain.WeatherRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class WeatherRepositoryImpl(
    application: Application
) : WeatherRepository {

    private val apiService = ApiFactory.apiService
    private val db = AppDatabase.getInstance(application)
    private val prefs = PreferenceManager(application)
    private val mapper = WeatherMapper()
    private val locationDataSource = FusedLocationDataSource(application)

    override fun getCurrentWeatherFromDb(): Single<CurrentWeather> {
        return db.currentWeatherFullDataDao()
            .getCurrentWeatherFullData(prefs.getCurrentLocationId())
            .map { mapper.mapEntityToCurrentWeatherDomain(it) }
    }

    override fun loadWeatherForecast(): Single<Unit> {
        return locationDataSource.getLastLocation()
            .flatMap {
                Single.zip(
                    apiService.getLocation(latitude = it.latitude, longitude = it.longitude),
                    apiService.getWeatherForecast(latitude = it.latitude, longitude = it.longitude)
                    //apiService.getLocation(latitude = 35.652832, longitude = 139.839478),
                    //apiService.getWeatherForecast(latitude = 35.652832, longitude = 139.839478)
                ) { location, weather ->
                    FullWeatherInfoDto(location[0], weather)
                }
            }
            .map {
                db.weatherConditionDao().insertWeatherCondition(
                    mapper.mapWeatherConditionDtoToEntity(it.weatherForecast.current.weather[0])
                )

                val locationId: Int = db.locationDao().insertLocation(
                    mapper.mapLocationDtoToEntity(it.location)
                ).toInt()

                db.currentDao().insertCurrentWeather(
                    mapper.mapCurrentWeatherDtoToEntity(
                        it.weatherForecast.current, locationId
                    )
                )

                prefs.saveCurrentLocationId(locationId)
            }
    }
}