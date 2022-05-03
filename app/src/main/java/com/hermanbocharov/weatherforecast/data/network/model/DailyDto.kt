package com.hermanbocharov.weatherforecast.data.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DailyDto(
    @SerializedName("dt")
    @Expose
    val forecastTime: Int,

    @SerializedName("sunrise")
    @Expose
    val sunrise: Int,

    @SerializedName("sunset")
    @Expose
    val sunset: Int,

    @SerializedName("moonrise")
    @Expose
    val moonrise: Int,

    @SerializedName("moonset")
    @Expose
    val moonset: Int,

    @SerializedName("moon_phase")
    @Expose
    val moonPhase: Double,

    @SerializedName("temp")
    @Expose
    val temp: TemperatureDto,

    @SerializedName("feels_like")
    @Expose
    val feelsLike: FeelsLikeDto,

    @SerializedName("pressure")
    @Expose
    val pressure: Int,

    @SerializedName("humidity")
    @Expose
    val humidity: Int,

    @SerializedName("dew_point")
    @Expose
    val dewPoint: Double,

    @SerializedName("wind_speed")
    @Expose
    val windSpeed: Double,

    @SerializedName("wind_deg")
    @Expose
    val windDeg: Int,

    @SerializedName("wind_gust")
    @Expose
    val windGust: Double? = null,

    @SerializedName("weather")
    @Expose
    val weather: List<WeatherConditionDto>,

    @SerializedName("clouds")
    @Expose
    val clouds: Int,

    @SerializedName("pop")
    @Expose
    val precipitationProbability: Double,

    @SerializedName("rain")
    @Expose
    val rainVolume: Double? = null,

    @SerializedName("snow")
    @Expose
    val snowVolume: Double? = null,

    @SerializedName("uvi")
    @Expose
    val uvi: Double
)