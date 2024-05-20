package com.hermanbocharov.weatherforecast.domain.entities

enum class Language(val value: String) {
    English("en"),
    Russian("ru"),
    Ukrainian("uk");

    companion object {
        private val map = Language.values().associateBy(Language::value)
        fun fromString(type: String) = map[type]
    }
}