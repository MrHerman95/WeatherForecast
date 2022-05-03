package com.hermanbocharov.weatherforecast.data.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SnowVolumeDto(
    @SerializedName("1h")
    @Expose
    val last1h: Double? = null
)