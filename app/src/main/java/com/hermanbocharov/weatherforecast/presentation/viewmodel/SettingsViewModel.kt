package com.hermanbocharov.weatherforecast.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hermanbocharov.weatherforecast.domain.entities.Language
import com.hermanbocharov.weatherforecast.domain.usecases.preferences.GetAppLanguageUseCase
import com.hermanbocharov.weatherforecast.domain.usecases.preferences.GetPrecipitationUnitUseCase
import com.hermanbocharov.weatherforecast.domain.usecases.preferences.GetPressureUnitUseCase
import com.hermanbocharov.weatherforecast.domain.usecases.preferences.GetSpeedUnitUseCase
import com.hermanbocharov.weatherforecast.domain.usecases.preferences.GetTemperatureUnitUseCase
import com.hermanbocharov.weatherforecast.domain.usecases.preferences.SavePrecipitationUnitUseCase
import com.hermanbocharov.weatherforecast.domain.usecases.preferences.SavePressureUnitUseCase
import com.hermanbocharov.weatherforecast.domain.usecases.preferences.SaveSpeedUnitUseCase
import com.hermanbocharov.weatherforecast.domain.usecases.preferences.SaveTemperatureUnitUseCase
import com.hermanbocharov.weatherforecast.domain.usecases.preferences.SetAppLanguageUseCase
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val getTemperatureUnitUseCase: GetTemperatureUnitUseCase,
    private val saveTemperatureUnitUseCase: SaveTemperatureUnitUseCase,
    private val getPrecipitationUnitUseCase: GetPrecipitationUnitUseCase,
    private val savePrecipitationUnitUseCase: SavePrecipitationUnitUseCase,
    private val getSpeedUnitUseCase: GetSpeedUnitUseCase,
    private val saveSpeedUnitUseCase: SaveSpeedUnitUseCase,
    private val getPressureUnitUseCase: GetPressureUnitUseCase,
    private val savePressureUnitUseCase: SavePressureUnitUseCase,
    private val setAppLanguageUseCase: SetAppLanguageUseCase,
    private val getAppLanguageUseCase: GetAppLanguageUseCase
) : ViewModel() {

    private val _temperatureUnit = MutableLiveData<Int>()
    val temperatureUnit: LiveData<Int>
        get() = _temperatureUnit

    private val _precipitationUnit = MutableLiveData<Int>()
    val precipitationUnit: LiveData<Int>
        get() = _precipitationUnit

    private val _speedUnit = MutableLiveData<Int>()
    val speedUnit: LiveData<Int>
        get() = _speedUnit

    private val _pressureUnit = MutableLiveData<Int>()
    val pressureUnit: LiveData<Int>
        get() = _pressureUnit

    private val _language = MutableLiveData<Language>()
    val language: LiveData<Language>
        get() = _language

    init {
        _temperatureUnit.value = getTemperatureUnitUseCase()
        _precipitationUnit.value = getPrecipitationUnitUseCase()
        _speedUnit.value = getSpeedUnitUseCase()
        _pressureUnit.value = getPressureUnitUseCase()
        _language.value = getAppLanguageUseCase()
    }

    fun saveTemperatureUnit(unitId: Int) {
        saveTemperatureUnitUseCase(unitId)
        _temperatureUnit.value = getTemperatureUnitUseCase()
    }

    fun savePrecipitationUnit(unitId: Int) {
        savePrecipitationUnitUseCase(unitId)
        _precipitationUnit.value = getPrecipitationUnitUseCase()
    }

    fun saveSpeedUnit(unitId: Int) {
        saveSpeedUnitUseCase(unitId)
        _speedUnit.value = getSpeedUnitUseCase()
    }

    fun savePressureUnit(unitId: Int) {
        savePressureUnitUseCase(unitId)
        _pressureUnit.value = getPressureUnitUseCase()
    }

    fun setAppLanguage(language: Language) {
        setAppLanguageUseCase(language)
        _language.value = language
    }

    fun getTemperatureUnit(): Int = getTemperatureUnitUseCase()
    fun getPrecipitationUnit(): Int = getPrecipitationUnitUseCase()
    fun getSpeedUnit(): Int = getSpeedUnitUseCase()
    fun getPressureUnit(): Int = getPressureUnitUseCase()
    fun getAppLanguage(): Language = getAppLanguageUseCase()
}