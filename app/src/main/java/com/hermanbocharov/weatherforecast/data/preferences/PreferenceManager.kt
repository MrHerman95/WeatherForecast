package com.hermanbocharov.weatherforecast.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class PreferenceManager(
    context: Context
) {

    private val appContext = context.applicationContext

    private val preference: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)

    fun saveLastUpdateTime(timestamp: Int) {
        preference.edit().putInt(KEY_LAST_UPDATE_TIME, timestamp).apply()
    }

    fun getLastUpdateTime(): Int {
        return preference.getInt(KEY_LAST_UPDATE_TIME, 0)
    }

    fun saveCurrentLocationId(id: Int) {
        preference.edit().putInt(KEY_LOCATION_ID, id).apply()
    }

    fun getCurrentLocationId(): Int {
        return preference.getInt(KEY_LOCATION_ID, 0)
    }

    companion object {
        private const val KEY_LAST_UPDATE_TIME = "key_last_update_time"
        private const val KEY_LOCATION_ID = "key_location_id"
    }
}