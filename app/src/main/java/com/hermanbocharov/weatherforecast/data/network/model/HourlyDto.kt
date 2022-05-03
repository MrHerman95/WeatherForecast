package com.hermanbocharov.weatherforecast.data.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class HourlyDto(
    @SerializedName("dt")
    @Expose
    val forecastTime: Int,

    @SerializedName("temp")
    @Expose
    val temp: Double,

    @SerializedName("feels_like")
    @Expose
    val feelsLike: Double,

    @SerializedName("pressure")
    @Expose
    val pressure: Int,

    @SerializedName("humidity")
    @Expose
    val humidity: Int,

    @SerializedName("dew_point")
    @Expose
    val dewPoint: Double,

    @SerializedName("uvi")
    @Expose
    val uvi: Double,

    @SerializedName("clouds")
    @Expose
    val clouds: Int,

    @SerializedName("visibility")
    @Expose
    val visibility: Int,

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

    @SerializedName("pop")
    @Expose
    val precipitationProbability: Double,

    @SerializedName("rain")
    @Expose
    val rain: RainVolumeDto? = null,

    @SerializedName("snow")
    @Expose
    val snow: SnowVolumeDto? = null
)