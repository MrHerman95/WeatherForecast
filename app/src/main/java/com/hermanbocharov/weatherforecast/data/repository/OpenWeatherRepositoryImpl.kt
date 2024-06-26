package com.hermanbocharov.weatherforecast.data.repository

import com.hermanbocharov.weatherforecast.BuildConfig
import com.hermanbocharov.weatherforecast.data.database.dao.CurrentWeatherDao
import com.hermanbocharov.weatherforecast.data.database.dao.CurrentWeatherFullDataDao
import com.hermanbocharov.weatherforecast.data.database.dao.DailyForecastDao
import com.hermanbocharov.weatherforecast.data.database.dao.DailyForecastFullDataDao
import com.hermanbocharov.weatherforecast.data.database.dao.HourlyForecastDao
import com.hermanbocharov.weatherforecast.data.database.dao.HourlyForecastFullDataDao
import com.hermanbocharov.weatherforecast.data.database.dao.LocationDao
import com.hermanbocharov.weatherforecast.data.database.dao.WeatherConditionDao
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
import com.hermanbocharov.weatherforecast.domain.entities.Language
import com.hermanbocharov.weatherforecast.domain.entities.Location
import com.hermanbocharov.weatherforecast.domain.repository.OpenWeatherRepository
import com.hermanbocharov.weatherforecast.exception.NoInternetException
import io.reactivex.rxjava3.core.Single
import java.util.Locale
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

    private var currentLocale: String = ""

    override fun getCurrentWeather(): Single<CurrentWeather> {
        return if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - prefs.getLastUpdateTime() < UPDATE_FREQUENCY
            && currentLocale == prefs.getSavedLocale()
            && BuildConfig.VERSION_NAME == prefs.getAppVersion()
        ) {
            currentWeatherFullDataDao
                .getCurrentWeatherFullData(getCurrentLocationId())
                .map { mapper.mapEntityToCurrentWeatherDomain(it, getTemperatureUnit()) }
        } else {
            currentLocale = prefs.getSavedLocale()
            prefs.saveAppVersion(BuildConfig.VERSION_NAME)
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
            } else {
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
                apiService.getWeatherForecast(
                    latitude = it.lat,
                    longitude = it.lon,
                    lang = getForecastLanguage()
                )
            }
            .map {
                insertWeatherForecastToDatabase(it, getCurrentLocationId())
                prefs.saveLastUpdateTime(it.current.updateTime)
                prefs.saveCurrentLocale(getForecastLanguage())
            }
    }

    override fun loadWeatherForecastGpsLoc(): Single<Unit> {
        return locationDataSource.getCurrentLocation()
            .flatMap {
                Single.zip(
                    apiService.getLocationByCoordinates(
                        latitude = it.latitude,
                        longitude = it.longitude
                    ),
                    apiService.getWeatherForecast(
                        latitude = it.latitude,
                        longitude = it.longitude,
                        lang = getForecastLanguage()
                    )
                ) { location, weather ->
                    FullWeatherInfoDto(location[0], weather)
                }
            }
            .map {
                val locationId = locationDao.insertLocation(
                    mapper.mapDtoToLocationEntity(it.location)
                ).blockingGet().toInt()

                insertWeatherForecastToDatabase(it.weatherForecast, locationId)

                prefs.saveCurrentLocationId(locationId)
                prefs.saveAppVersion(BuildConfig.VERSION_NAME)
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
                    it.map {
                        locationDao.insertLocation(mapper.mapDtoToLocationEntity(it)).blockingGet()
                        mapper.mapDtoToLocationDomain(it)
                    }
                }
        } else {
            Single.error(NoInternetException())
        }
    }

    override fun getCurrentLocation(): Single<Location> {
        return locationDao.getLocation(getCurrentLocationId())
            .flatMap {
                if (it.countryCode.length != 2) {
                    apiService.getLocationByCoordinates(latitude = it.lat, longitude = it.lon)
                        .map {
                            val locationId =
                                locationDao.insertLocation(mapper.mapDtoToLocationEntity(it[0]))
                                    .blockingGet().toInt()
                            prefs.saveCurrentLocationId(locationId)
                            prefs.saveAppVersion(BuildConfig.VERSION_NAME)
                            mapper.mapDtoToLocationDomain(it[0])
                        }
                } else {
                    Single.just(mapper.mapEntityToLocationDomain(it))
                }
            }
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

    override fun getAppLanguage(): Language =
        requireNotNull(Language.fromString(prefs.getSavedLocale()))

    override fun setAppLanguage(language: Language) = prefs.saveCurrentLocale(language.value)

    override fun setNewLocation(location: Location): Single<Unit> {
        return locationDao.getLocation(lat = location.lat, lon = location.lon)
            .flatMap {
                prefs.saveCurrentLocationId(it.id)
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

    private fun getForecastLanguage(): String {
        return when (Locale.getDefault().language) {
            "ru" -> "ru"
            "uk" -> "uk"
            else -> "en"
        }
    }

    companion object {
        private const val UPDATE_FREQUENCY = 15 * 60 /* in seconds */
        private const val ISO_LENGTH = 2
        private const val MIN_COUNTRY_NAME_LENGTH = 3
    }
}