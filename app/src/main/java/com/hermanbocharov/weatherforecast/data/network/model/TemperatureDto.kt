package com.hermanbocharov.weatherforecast.data.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TemperatureDto(
    @SerializedName("day")
    @Expose
    val day: Double,

    @SerializedName("min")
    @Expose
    val min: Double,

    @SerializedName("max")
    @Expose
    val max: Double,

    @SerializedName("night")
    @Expose
    val night: Double,

    @SerializedName("eve")
    @Expose
    val eve: Double,

    @SerializedName("morn")
    @Expose
    val morn: Double
)