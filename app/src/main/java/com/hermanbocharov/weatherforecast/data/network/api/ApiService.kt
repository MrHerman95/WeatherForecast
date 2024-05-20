package com.hermanbocharov.weatherforecast.data.network.api

import com.hermanbocharov.weatherforecast.data.network.model.LocationDto
import com.hermanbocharov.weatherforecast.data.network.model.WeatherForecastDto
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("data/3.0/onecall")
    fun getWeatherForecast(
        @Query(QUERY_PARAM_LATITUDE) latitude: Double,
        @Query(QUERY_PARAM_LONGITUDE) longitude: Double,
        @Query(QUERY_PARAM_EXCLUDE) exclude: String = DEFAULT_EXCLUDE_DATA,
        @Query(QUERY_PARAM_UNITS) units: String = DEFAULT_UNIT,
        @Query(QUERY_PARAM_LANG) lang: String = DEFAULT_LANG,
        @Query(QUERY_PARAM_API_KEY) apiKey: String = API_KEY
    ): Single<WeatherForecastDto>

    @GET("geo/1.0/reverse")
    fun getLocationByCoordinates(
        @Query(QUERY_PARAM_LATITUDE) latitude: Double,
        @Query(QUERY_PARAM_LONGITUDE) longitude: Double,
        @Query(QUERY_PARAM_LIMIT) limit: Int = 1,
        @Query(QUERY_PARAM_API_KEY) apiKey: String = API_KEY
    ): Single<List<LocationDto>>

    @GET("geo/1.0/direct")
    fun getListOfCities(
        @Query(QUERY_PARAM_GEOCODING) cityCountry: String,
        @Query(QUERY_PARAM_LIMIT) limit: Int = 5,
        @Query(QUERY_PARAM_API_KEY) apiKey: String = API_KEY
    ): Single<List<LocationDto>>

    companion object {
        private const val QUERY_PARAM_LATITUDE = "lat"
        private const val QUERY_PARAM_LONGITUDE = "lon"
        private const val QUERY_PARAM_GEOCODING = "q"
        private const val QUERY_PARAM_EXCLUDE = "exclude"
        private const val QUERY_PARAM_UNITS = "units"
        private const val QUERY_PARAM_LANG = "lang"
        private const val QUERY_PARAM_LIMIT = "limit"
        private const val QUERY_PARAM_API_KEY = "appid"

        private const val DEFAULT_EXCLUDE_DATA = "minutely,alerts"
        private const val DEFAULT_UNIT = "metric"
        private const val DEFAULT_LANG = "en"
        private const val API_KEY = "1856da01856db44702200da928d39be1"
    }
}