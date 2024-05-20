package com.hermanbocharov.weatherforecast.domain.usecases.preferences

import com.hermanbocharov.weatherforecast.domain.entities.Language
import com.hermanbocharov.weatherforecast.domain.repository.OpenWeatherRepository
import javax.inject.Inject

class SetAppLanguageUseCase @Inject constructor(
    private val repository: OpenWeatherRepository
) {
    operator fun invoke(language: Language) = repository.setAppLanguage(language)
}