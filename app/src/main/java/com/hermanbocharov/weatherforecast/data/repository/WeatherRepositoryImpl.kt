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
import com.hermanbocharov.weatherforecast.domain.Location
import com.hermanbocharov.weatherforecast.domain.WeatherRepository
import io.reactivex.rxjava3.core.Single
import java.util.concurrent.TimeUnit

class WeatherRepositoryImpl(
    application: Application
) : WeatherRepository {

    private val apiService = ApiFactory.apiService
    private val db = AppDatabase.getInstance(application)
    private val prefs = PreferenceManager(application)
    private val mapper = WeatherMapper()
    private val locationDataSource = FusedLocationDataSource(application)

    override fun getCurrentWeather(): Single<CurrentWeather> {
        Log.d("TEST_OF_LOADING_DATA", "System time = ${TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())}")
        Log.d("TEST_OF_LOADING_DATA", "Last update time = ${prefs.getLastUpdateTime()}")
        return if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - prefs.getLastUpdateTime() < UPDATE_FREQUENCY) {
            Log.d("TEST_OF_LOADING_DATA", "From db")
            db.currentWeatherFullDataDao()
                .getCurrentWeatherFullData(getCurrentLocationId())
                .map { mapper.mapEntityToCurrentWeatherDomain(it) }
        } else {
            Log.d("TEST_OF_LOADING_DATA", "From internet")
            loadWeatherForecastCurLoc()
                .flatMap {
                    db.currentWeatherFullDataDao()
                        .getCurrentWeatherFullData(getCurrentLocationId())
                        .map { mapper.mapEntityToCurrentWeatherDomain(it) }
                }
        }
    }

    override fun loadWeatherForecastCurLoc(): Single<Unit> {
        return getCurrentLocation()
            .flatMap {
                apiService.getWeatherForecast(latitude = it.lat, longitude = it.lon)
            }
            .map {
                db.weatherConditionDao().insertWeatherCondition(
                    mapper.mapWeatherConditionDtoToEntity(it.current.weather[0])
                )

                db.currentDao().insertCurrentWeather(
                    mapper.mapCurrentWeatherDtoToEntity(
                        it.current, getCurrentLocationId()
                    )
                )

                prefs.saveLastUpdateTime(it.current.updateTime)
            }
    }

    override fun loadWeatherForecastGpsLoc(): Single<Unit> {
        return locationDataSource.getLastLocation()
            .flatMap {
                Single.zip(
                    apiService.getLocationByCoordinates(
                        latitude = it.latitude,
                        longitude = it.longitude
                    ),
                    apiService.getWeatherForecast(latitude = it.latitude, longitude = it.longitude)
                ) { location, weather ->
                    FullWeatherInfoDto(location[0], weather)
                }
            }
            .map {
                db.weatherConditionDao().insertWeatherCondition(
                    mapper.mapWeatherConditionDtoToEntity(it.weatherForecast.current.weather[0])
                )

                val locationId = db.locationDao().insertLocation(
                    mapper.mapLocationDtoToEntity(it.location)
                ).blockingGet().toInt()

                db.currentDao().insertCurrentWeather(
                    mapper.mapCurrentWeatherDtoToEntity(
                        it.weatherForecast.current, locationId
                    )
                )

                prefs.saveCurrentLocationId(locationId)
                prefs.saveLastUpdateTime(it.weatherForecast.current.updateTime)
            }
    }

    override fun getListOfCities(city: String): Single<List<Location>> {
        return apiService.getListOfCities(city)
            .map {
                it.map { mapper.mapDtoToLocationDomain(it) }
            }
    }

    override fun getCurrentLocation(): Single<Location> {
        return db.locationDao().getLocation(getCurrentLocationId())
            .map { mapper.mapEntityToLocationDomain(it) }
    }

    override fun getCurrentLocationId(): Int {
        return prefs.getCurrentLocationId()
    }

    override fun addNewLocation(location: Location): Single<Unit> {
        return db.locationDao().insertLocation(mapper.mapLocationDomainToEntity(location))
            .map {
                Log.d("TEST_OF_LOADING_DATA", "New loc id = $it")
                prefs.saveCurrentLocationId(it.toInt())
            }
    }

    companion object {
        private const val UPDATE_FREQUENCY = 1 * 60; /* in seconds */
    }
}