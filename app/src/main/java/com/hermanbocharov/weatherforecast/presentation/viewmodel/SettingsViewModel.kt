package com.hermanbocharov.weatherforecast.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hermanbocharov.weatherforecast.domain.usecases.preferences.*
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val getTemperatureUnitUseCase: GetTemperatureUnitUseCase,
    private val saveTemperatureUnitUseCase: SaveTemperatureUnitUseCase,
    private val getPrecipitationUnitUseCase: GetPrecipitationUnitUseCase,
    private val savePrecipitationUnitUseCase: SavePrecipitationUnitUseCase,
    private val getSpeedUnitUseCase: GetSpeedUnitUseCase,
    private val saveSpeedUnitUseCase: SaveSpeedUnitUseCase,
    private val getPressureUnitUseCase: GetPressureUnitUseCase,
    private val savePressureUnitUseCase: SavePressureUnitUseCase
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

    init {
        _temperatureUnit.value = getTemperatureUnitUseCase()
        _precipitationUnit.value = getPrecipitationUnitUseCase()
        _speedUnit.value = getSpeedUnitUseCase()
        _pressureUnit.value = getPressureUnitUseCase()
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

    fun getTemperatureUnit(): Int = getTemperatureUnitUseCase()
    fun getPrecipitationUnit(): Int = getPrecipitationUnitUseCase()
    fun getSpeedUnit(): Int = getSpeedUnitUseCase()
    fun getPressureUnit(): Int = getPressureUnitUseCase()
}