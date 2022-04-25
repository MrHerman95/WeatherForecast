package com.hermanbocharov.weatherforecast.data.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RainVolumeDto(
    @SerializedName("1h")
    @Expose
    private val last1h: Double? = null
)