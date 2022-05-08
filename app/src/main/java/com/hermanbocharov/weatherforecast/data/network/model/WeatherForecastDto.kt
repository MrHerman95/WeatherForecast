package com.hermanbocharov.weatherforecast.data.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WeatherForecastDto(
    @SerializedName("timezone")
    @Expose
    val timezoneName: String,

    @SerializedName("timezone_offset")
    @Expose
    val timezoneOffset: Int,

    @SerializedName("current")
    @Expose
    val current: CurrentDto,

    @SerializedName("hourly")
    @Expose
    val hourly: List<HourlyDto>,

    @SerializedName("daily")
    @Expose
    val daily: List<DailyDto>
)