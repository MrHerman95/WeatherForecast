package com.hermanbocharov.weatherforecast.data.network

import com.hermanbocharov.weatherforecast.data.network.model.WeatherForecastDto
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("onecall")
    fun getWeatherForecast(
        @Query(QUERY_PARAM_LATITUDE) latitude: Double,
        @Query(QUERY_PARAM_LONGITUDE) longitude: Double,
        @Query(QUERY_PARAM_EXCLUDE) exclude: String = DEFAULT_EXCLUDE_DATA,
        @Query(QUERY_PARAM_UNITS) units: String = DEFAULT_UNIT,
        @Query(QUERY_PARAM_LANG) lang: String = DEFAULT_LANG,
        @Query(QUERY_PARAM_API_KEY) apiKey: String = API_KEY
    ): Single<WeatherForecastDto>

    companion object {
        private const val QUERY_PARAM_LATITUDE = "lat"
        private const val QUERY_PARAM_LONGITUDE = "lon"
        private const val QUERY_PARAM_EXCLUDE = "exclude"
        private const val QUERY_PARAM_UNITS = "units"
        private const val QUERY_PARAM_LANG = "lang"
        private const val QUERY_PARAM_API_KEY = "appid"

        private const val DEFAULT_EXCLUDE_DATA = "minutely,alerts"
        private const val DEFAULT_UNIT = "metric"
        private const val DEFAULT_LANG = "en"
        private const val API_KEY = "1856da01856db44702200da928d39be1"
    }
}