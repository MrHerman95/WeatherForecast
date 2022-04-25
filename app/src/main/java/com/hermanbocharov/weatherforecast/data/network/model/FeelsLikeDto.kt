package com.hermanbocharov.weatherforecast.data.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class FeelsLikeDto(
    @SerializedName("day")
    @Expose
    private val day: Double? = null,

    @SerializedName("night")
    @Expose
    private val night: Double? = null,

    @SerializedName("eve")
    @Expose
    private val eve: Double? = null,

    @SerializedName("morn")
    @Expose
    private val morn: Double? = null
)