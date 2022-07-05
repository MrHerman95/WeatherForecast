package com.hermanbocharov.weatherforecast.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hermanbocharov.weatherforecast.domain.GetTemperatureUnitUseCase
import com.hermanbocharov.weatherforecast.domain.SaveTemperatureUnitUseCase
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val getTemperatureUnitUseCase: GetTemperatureUnitUseCase,
    private val saveTemperatureUnitUseCase: SaveTemperatureUnitUseCase
) : ViewModel() {

    private val _temperatureUnit = MutableLiveData<Int>()
    val temperatureUnit: LiveData<Int>
        get() = _temperatureUnit

    init {
        getTemperatureUnit()
    }

    fun saveTemperatureUnit(unitId: Int) {
        saveTemperatureUnitUseCase(unitId)
    }

    private fun getTemperatureUnit() {
        _temperatureUnit.value = getTemperatureUnitUseCase()
    }
}