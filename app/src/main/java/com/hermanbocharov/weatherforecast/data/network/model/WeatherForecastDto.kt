package com.hermanbocharov.weatherforecast.data.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WeatherForecastDto(
    @SerializedName("timezone")
    @Expose
    private val timezoneName: String? = null,

    @SerializedName("timezone_offset")
    @Expose
    private val timezoneOffset: Int? = null,

    @SerializedName("current")
    @Expose
    private val current: CurrentDto? = null,

    @SerializedName("hourly")
    @Expose
    private val hourly: List<HourlyDto>? = null,

    @SerializedName("daily")
    @Expose
    private val daily: List<DailyDto>? = null
)