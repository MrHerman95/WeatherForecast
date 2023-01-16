package com.hermanbocharov.weatherforecast.data.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LocalNamesDto(
    @SerializedName("ru")
    @Expose
    val ru: String? = null,

    @SerializedName("uk")
    @Expose
    val uk: String? = null
)