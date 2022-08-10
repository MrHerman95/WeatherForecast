package com.hermanbocharov.weatherforecast.domain.entities

data class Location(
    val id: Int = 0,
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String? = null,
    val isPinned: Boolean = false
)