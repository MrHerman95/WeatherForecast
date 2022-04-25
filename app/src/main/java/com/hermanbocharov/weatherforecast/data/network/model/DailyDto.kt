package com.hermanbocharov.weatherforecast.data.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DailyDto(
    @SerializedName("dt")
    @Expose
    private val forecastTime: Int? = null,

    @SerializedName("sunrise")
    @Expose
    private val sunrise: Int? = null,

    @SerializedName("sunset")
    @Expose
    private val sunset: Int? = null,

    @SerializedName("moonrise")
    @Expose
    private val moonrise: Int? = null,

    @SerializedName("moonset")
    @Expose
    private val moonset: Int? = null,

    @SerializedName("moon_phase")
    @Expose
    private val moonPhase: Double? = null,

    @SerializedName("temp")
    @Expose
    private val temp: TemperatureDto? = null,

    @SerializedName("feels_like")
    @Expose
    private val feelsLike: FeelsLikeDto? = null,

    @SerializedName("pressure")
    @Expose
    private val pressure: Int? = null,

    @SerializedName("humidity")
    @Expose
    private val humidity: Int? = null,

    @SerializedName("dew_point")
    @Expose
    private val dewPoint: Double? = null,

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

    @SerializedName("clouds")
    @Expose
    private val clouds: Int? = null,

    @SerializedName("pop")
    @Expose
    private val precipitationProbability: Int? = null,

    @SerializedName("rain")
    @Expose
    private val rainVolume: Double? = null,

    @SerializedName("snow")
    @Expose
    private val snowVolume: Double? = null,

    @SerializedName("uvi")
    @Expose
    private val uvi: Int? = null
)