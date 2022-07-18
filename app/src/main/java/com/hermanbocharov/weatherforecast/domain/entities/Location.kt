package com.hermanbocharov.weatherforecast.domain.entities

data class Location(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String? = null
)