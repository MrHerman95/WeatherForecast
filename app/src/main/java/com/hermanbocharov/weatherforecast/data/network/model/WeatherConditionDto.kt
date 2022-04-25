package com.hermanbocharov.weatherforecast.data.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WeatherConditionDto(
    @SerializedName("id")
    @Expose
    private val id: Int? = null,

    @SerializedName("main")
    @Expose
    private val main: String? = null,

    @SerializedName("description")
    @Expose
    private val description: String? = null,

    @SerializedName("icon")
    @Expose
    private val icon: String? = null
)