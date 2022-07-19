package com.hermanbocharov.weatherforecast.data.repository

import android.util.Log
import com.hermanbocharov.weatherforecast.data.database.dao.*
import com.hermanbocharov.weatherforecast.data.geolocation.FusedLocationDataSource
import com.hermanbocharov.weatherforecast.data.mapper.OpenWeatherMapper
import com.hermanbocharov.weatherforecast.data.network.api.ApiService
import com.hermanbocharov.weatherforecast.data.network.model.FullWeatherInfoDto
import com.hermanbocharov.weatherforecast.data.network.model.WeatherForecastDto
import com.hermanbocharov.weatherforecast.data.preferences.PreferenceManager
import com.hermanbocharov.weatherforecast.domain.entities.CurrentWeather
import com.hermanbocharov.weatherforecast.domain.entities.Location
import com.hermanbocharov.weatherforecast.domain.repository.OpenWeatherRepository
import io.reactivex.rxjava3.core.Single
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class OpenWeatherRepositoryImpl @Inject constructor(
    private val mapper: OpenWeatherMapper,
    private val apiService: ApiService,
    private val currentDao: CurrentWeatherDao,
    private val hourlyForecastDao: HourlyForecastDao,
    private val dailyForecastDao: DailyForecastDao,
    private val weatherConditionDao: WeatherConditionDao,
    private val locationDao: LocationDao,
    private val currentWeatherFullDataDao: CurrentWeatherFullDataDao,
    private val hourlyForecastFullDataDao: HourlyForecastFullDataDao,
    private val dailyForecastFullDataDao: DailyForecastFullDataDao,
    private val locationDataSource: FusedLocationDataSource,
    private val prefs: PreferenceManager
) : OpenWeatherRepository {

    override fun getCurrentWeather(): Single<CurrentWeather> {
        Log.d(
            "TEST_OF_LOADING_DATA",
            "System time = ${TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())}"
        )
        Log.d("TEST_OF_LOADING_DATA", "Last update time = ${prefs.getLastUpdateTime()}")
        return if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - prefs.getLastUpdateTime() < UPDATE_FREQUENCY) {
            Log.d("TEST_OF_LOADING_DATA", "From db")
            currentWeatherFullDataDao
                .getCurrentWeatherFullData(getCurrentLocationId())
                .map { mapper.mapEntityToCurrentWeatherDomain(it, getTemperatureUnit()) }
        } else {
            Log.d("TEST_OF_LOADING_DATA", "From internet")
            loadWeatherForecastCurLoc()
                .flatMap {
                    currentWeatherFullDataDao
                        .getCurrentWeatherFullData(getCurrentLocationId())
                        .map { mapper.mapEntityToCurrentWeatherDomain(it, getTemperatureUnit()) }
                }
        }
    }

    override fun loadWeatherForecastCurLoc(): Single<Unit> {
        return getCurrentLocation()
            .flatMap {
                apiService.getWeatherForecast(latitude = it.lat, longitude = it.lon)
            }
            .map {
                Log.d(
                    "TEST_OF_LOADING_DATA",
                    "Timezone name = ${it.timezoneName}, offset = ${it.timezoneOffset}"
                )
                for (cond in it.current.weather) {
                    Log.d("TEST_OF_LOADING_DATA", "Condition = $cond")
                }

                insertWeatherForecastToDatabase(it, getCurrentLocationId())

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
                Log.d(
                    "TEST_OF_LOADING_DATA",
                    "Timezone name = ${it.weatherForecast.timezoneName}, offset = ${it.weatherForecast.timezoneOffset}"
                )

                val locationId = locationDao.insertLocation(
                    mapper.mapDtoToLocationEntity(it.location)
                ).blockingGet().toInt()

                insertWeatherForecastToDatabase(it.weatherForecast, locationId)

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
        return locationDao.getLocation(getCurrentLocationId())
            .map { mapper.mapEntityToLocationDomain(it) }
    }

    override fun getCurrentLocationId(): Int {
        return prefs.getCurrentLocationId()
    }

    override fun getTemperatureUnit(): Int {
        return prefs.getTemperatureUnit()
    }

    override fun saveTemperatureUnit(unitId: Int) {
        prefs.saveTemperatureUnit(unitId)
    }

    override fun addNewLocation(location: Location): Single<Unit> {
        return locationDao.insertLocation(mapper.mapLocationDomainToEntity(location))
            .flatMap {
                Log.d("TEST_OF_LOADING_DATA", "New loc id = $it")
                prefs.saveCurrentLocationId(it.toInt())
                loadWeatherForecastCurLoc()
            }
    }

    private fun insertAllWeatherConditions(forecast: WeatherForecastDto) {
        weatherConditionDao.insertWeatherCondition(
            mapper.mapWeatherConditionDtoToEntity(forecast.current.weather[0])
        )

        for (hour in forecast.hourly) {
            weatherConditionDao.insertWeatherCondition(
                mapper.mapWeatherConditionDtoToEntity(hour.weather[0])
            )
        }

        for (day in forecast.daily) {
            weatherConditionDao.insertWeatherCondition(
                mapper.mapWeatherConditionDtoToEntity(day.weather[0])
            )
        }
    }

    private fun insertWeatherForecastToDatabase(
        forecast: WeatherForecastDto,
        locationId: Int
    ) {
        insertAllWeatherConditions(forecast)

        currentDao.insertCurrentWeather(
            mapper.mapWeatherForecastDtoToCurrentWeatherEntity(forecast, locationId)
        )

        hourlyForecastDao.insertHourlyForecast(
            mapper.mapWeatherForecastDtoToHourlyForecastEntityList(forecast, locationId)
        )

        dailyForecastDao.insertDailyForecast(
            mapper.mapWeatherForecastDtoToDailyForecastEntityList(forecast, locationId)
        )
    }

    companion object {
        private const val UPDATE_FREQUENCY = 1 * 60 /* in seconds */
    }
}