package com.hermanbocharov.weatherforecast.data.repository

import android.util.Log
import com.hermanbocharov.weatherforecast.data.database.dao.*
import com.hermanbocharov.weatherforecast.data.geolocation.FusedLocationDataSource
import com.hermanbocharov.weatherforecast.data.mapper.OpenWeatherMapper
import com.hermanbocharov.weatherforecast.data.network.NetworkManager
import com.hermanbocharov.weatherforecast.data.network.api.ApiService
import com.hermanbocharov.weatherforecast.data.network.model.FullWeatherInfoDto
import com.hermanbocharov.weatherforecast.data.network.model.WeatherForecastDto
import com.hermanbocharov.weatherforecast.data.preferences.PreferenceManager
import com.hermanbocharov.weatherforecast.domain.entities.CurrentWeather
import com.hermanbocharov.weatherforecast.domain.entities.DailyForecast
import com.hermanbocharov.weatherforecast.domain.entities.HourlyForecast
import com.hermanbocharov.weatherforecast.domain.entities.Location
import com.hermanbocharov.weatherforecast.domain.repository.OpenWeatherRepository
import com.hermanbocharov.weatherforecast.exception.NoInternetException
import io.reactivex.rxjava3.core.Single
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class OpenWeatherRepositoryImpl @Inject constructor(
    private val mapper: OpenWeatherMapper,
    private val apiService: ApiService,
    private val networkManager: NetworkManager,
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
            if (networkManager.isNetworkAvailable()) {
                loadWeatherForecastCurLoc()
                    .flatMap {
                        currentWeatherFullDataDao
                            .getCurrentWeatherFullData(getCurrentLocationId())
                            .map {
                                mapper.mapEntityToCurrentWeatherDomain(
                                    it,
                                    getTemperatureUnit()
                                )
                            }
                    }
            }
            else {
                Single.error(NoInternetException())
            }
        }
    }

    override fun getHourlyForecast(): Single<List<HourlyForecast>> {
        return hourlyForecastFullDataDao.getHourlyForecastFullData(getCurrentLocationId())
            .map {
                mapper.mapHourlyForecastFullDataToDomain(
                    it,
                    getTemperatureUnit(),
                    getSpeedUnit(),
                    getPrecipitationUnit(),
                    getPressureUnit()
                )
            }
    }

    override fun getDailyForecast(): Single<List<DailyForecast>> {
        return dailyForecastFullDataDao.getDailyForecastFullData(getCurrentLocationId())
            .map { mapper.mapDailyForecastFullDataToDomain(it, getTemperatureUnit()) }
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

    override fun getListOfCities(city: String, country: String): Single<List<Location>> {
        val countryISO = when {
            country.length == ISO_LENGTH -> country
            country.length >= MIN_COUNTRY_NAME_LENGTH -> mapper.mapCountryNameToISOCode(country)
            else -> ""
        }
        val cityCountry = "$city,$countryISO"

        return if (networkManager.isNetworkAvailable()) {
            apiService.getListOfCities(cityCountry)
                .map {
                    it.map { mapper.mapDtoToLocationDomain(it) }
                }
        } else {
            Single.error(NoInternetException())
        }
    }

    override fun getCurrentLocation(): Single<Location> {
        return locationDao.getLocation(getCurrentLocationId())
            .map { mapper.mapEntityToLocationDomain(it) }
    }

    override fun getCurrentLocationId(): Int = prefs.getCurrentLocationId()

    override fun getSpeedUnit(): Int = prefs.getSpeedUnit()
    override fun saveSpeedUnit(unitId: Int) = prefs.saveSpeedUnit(unitId)

    override fun getTemperatureUnit(): Int = prefs.getTemperatureUnit()
    override fun saveTemperatureUnit(unitId: Int) = prefs.saveTemperatureUnit(unitId)

    override fun getPrecipitationUnit(): Int = prefs.getPrecipitationUnit()
    override fun savePrecipitationUnit(unitId: Int) = prefs.savePrecipitationUnit(unitId)

    override fun getPressureUnit(): Int = prefs.getPressureUnit()
    override fun savePressureUnit(unitId: Int) = prefs.savePressureUnit(unitId)


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

        hourlyForecastDao.deleteOldHourlyForecast(locationId)
        hourlyForecastDao.insertHourlyForecast(
            mapper.mapWeatherForecastDtoToHourlyForecastEntityList(forecast, locationId)
        )

        dailyForecastDao.deleteOldDailyForecast(locationId)
        dailyForecastDao.insertDailyForecast(
            mapper.mapWeatherForecastDtoToDailyForecastEntityList(forecast, locationId)
        )
    }

    companion object {
        private const val UPDATE_FREQUENCY = 1 * 60 /* in seconds */
        private const val ISO_LENGTH = 2
        private const val MIN_COUNTRY_NAME_LENGTH = 3
    }
}