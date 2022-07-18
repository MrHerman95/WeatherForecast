package com.hermanbocharov.weatherforecast.data.preferences

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.hermanbocharov.weatherforecast.di.ApplicationScope
import com.hermanbocharov.weatherforecast.domain.entities.TemperatureUnit
import javax.inject.Inject

@ApplicationScope
class PreferenceManager @Inject constructor(
    application: Application
) {

    private val appContext = application.applicationContext

    private val preferences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)

    fun saveLastUpdateTime(timestamp: Int) {
        preferences.edit().putInt(KEY_LAST_UPDATE_TIME, timestamp).apply()
    }

    fun getLastUpdateTime(): Int {
        return preferences.getInt(KEY_LAST_UPDATE_TIME, 0)
    }

    fun saveCurrentLocationId(id: Int) {
        preferences.edit().putInt(KEY_LOCATION_ID, id).apply()
    }

    fun getCurrentLocationId(): Int {
        return preferences.getInt(KEY_LOCATION_ID, 0)
    }

    fun saveTemperatureUnit(unitId: Int) {
        preferences.edit().putInt(KEY_TEMPERATURE_UNITS, unitId).apply()
    }

    fun getTemperatureUnit(): Int {
        return preferences.getInt(KEY_TEMPERATURE_UNITS, TemperatureUnit.CELSIUS)
    }

    companion object {
        private const val KEY_LAST_UPDATE_TIME = "key_last_update_time"
        private const val KEY_LOCATION_ID = "key_location_id"
        private const val KEY_TEMPERATURE_UNITS = "key_temperature_units"
    }
}