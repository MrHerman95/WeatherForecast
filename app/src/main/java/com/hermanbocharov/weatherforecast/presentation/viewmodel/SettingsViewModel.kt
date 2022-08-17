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
        getTemperatureUnit()
        getPressureUnit()
        getSpeedUnit()
        getPrecipitationUnit()
    }

    fun saveTemperatureUnit(unitId: Int) {
        saveTemperatureUnitUseCase(unitId)
        getTemperatureUnit()
    }

    fun savePrecipitationUnit(unitId: Int) {
        savePrecipitationUnitUseCase(unitId)
        getPrecipitationUnit()
    }

    fun saveSpeedUnit(unitId: Int) {
        saveSpeedUnitUseCase(unitId)
        getSpeedUnit()
    }

    fun savePressureUnit(unitId: Int) {
        savePressureUnitUseCase(unitId)
        getPressureUnit()
    }

    private fun getTemperatureUnit() {
        _temperatureUnit.value = getTemperatureUnitUseCase()
    }

    private fun getPrecipitationUnit() {
        _precipitationUnit.value = getPrecipitationUnitUseCase()
    }

    private fun getSpeedUnit() {
        _speedUnit.value = getSpeedUnitUseCase()
    }

    private fun getPressureUnit() {
        _pressureUnit.value = getPressureUnitUseCase()
    }
}