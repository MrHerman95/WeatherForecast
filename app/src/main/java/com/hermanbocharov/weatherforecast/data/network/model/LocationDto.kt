package com.hermanbocharov.weatherforecast.data.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LocationDto(
    @SerializedName("name")
    @Expose
    private val name: String? = null,

    @SerializedName("lat")
    @Expose
    private val lat: Double? = null,

    @SerializedName("lon")
    @Expose
    private val lon: Double? = null,

    @SerializedName("country")
    @Expose
    private val country: String? = null,

    @SerializedName("state")
    @Expose
    private val state: String? = null
)