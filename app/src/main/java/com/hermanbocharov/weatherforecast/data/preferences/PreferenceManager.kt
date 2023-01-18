package com.hermanbocharov.weatherforecast.data.preferences

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.hermanbocharov.weatherforecast.di.ApplicationScope
import com.hermanbocharov.weatherforecast.domain.entities.PrecipitationUnit
import com.hermanbocharov.weatherforecast.domain.entities.PressureUnit
import com.hermanbocharov.weatherforecast.domain.entities.SpeedUnit
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

    fun saveSpeedUnit(unitId: Int) {
        preferences.edit().putInt(KEY_SPEED_UNITS, unitId).apply()
    }

    fun getSpeedUnit(): Int {
        return preferences.getInt(KEY_SPEED_UNITS, SpeedUnit.METERS_PER_SECOND)
    }

    fun savePrecipitationUnit(unitId: Int) {
        preferences.edit().putInt(KEY_PRECIPITATION_UNITS, unitId).apply()
    }

    fun getPrecipitationUnit(): Int {
        return preferences.getInt(KEY_PRECIPITATION_UNITS, PrecipitationUnit.MILLIMETERS)
    }

    fun savePressureUnit(unitId: Int) {
        preferences.edit().putInt(KEY_PRESSURE_UNITS, unitId).apply()
    }

    fun getPressureUnit(): Int {
        return preferences.getInt(KEY_PRESSURE_UNITS, PressureUnit.MILLIMETERS_HG)
    }

    fun saveCurrentLocale(locale: String) {
        preferences.edit().putString(KEY_LOCALE, locale).apply()
    }

    fun getSavedLocale(): String {
        return preferences.getString(KEY_LOCALE, "en") ?: "en"
    }

    companion object {
        private const val KEY_LAST_UPDATE_TIME = "key_last_update_time"
        private const val KEY_LOCATION_ID = "key_location_id"
        private const val KEY_TEMPERATURE_UNITS = "key_temperature_units"
        private const val KEY_SPEED_UNITS = "key_speed_units"
        private const val KEY_PRECIPITATION_UNITS = "key_precipitation_units"
        private const val KEY_PRESSURE_UNITS = "key_pressure_units"
        private const val KEY_LOCALE = "key_locale"
    }
}