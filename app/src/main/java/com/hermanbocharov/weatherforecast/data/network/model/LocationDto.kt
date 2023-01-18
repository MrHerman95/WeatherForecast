package com.hermanbocharov.weatherforecast.data.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LocationDto(
    @SerializedName("name")
    @Expose
    val name: String,

    @SerializedName("local_names")
    @Expose
    val localNames: LocalNamesDto? = null,

    @SerializedName("lat")
    @Expose
    val lat: Double,

    @SerializedName("lon")
    @Expose
    val lon: Double,

    @SerializedName("country")
    @Expose
    val country: String,

    @SerializedName("state")
    @Expose
    val state: String? = null
)