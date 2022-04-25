package com.hermanbocharov.weatherforecast.data.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class HourlyDto(
    @SerializedName("dt")
    @Expose
    private val forecastTime: Int? = null,

    @SerializedName("temp")
    @Expose
    private val temp: Double? = null,

    @SerializedName("feels_like")
    @Expose
    private val feelsLike: Double? = null,

    @SerializedName("pressure")
    @Expose
    private val pressure: Int? = null,

    @SerializedName("humidity")
    @Expose
    private val humidity: Int? = null,

    @SerializedName("dew_point")
    @Expose
    private val dewPoint: Double? = null,

    @SerializedName("uvi")
    @Expose
    private val uvi: Double? = null,

    @SerializedName("clouds")
    @Expose
    private val clouds: Int? = null,

    @SerializedName("visibility")
    @Expose
    private val visibility: Int? = null,

    @SerializedName("wind_speed")
    @Expose
    private val windSpeed: Double? = null,

    @SerializedName("wind_deg")
    @Expose
    private val windDeg: Int? = null,

    @SerializedName("wind_gust")
    @Expose
    private val windGust: Double? = null,

    @SerializedName("weather")
    @Expose
    private val weather: List<WeatherConditionDto>? = null,

    @SerializedName("pop")
    @Expose
    private val precipitationProbability: Double? = null,

    @SerializedName("rain")
    @Expose
    private val rain: RainVolumeDto? = null,

    @SerializedName("snow")
    @Expose
    private val snow: SnowVolumeDto? = null
)