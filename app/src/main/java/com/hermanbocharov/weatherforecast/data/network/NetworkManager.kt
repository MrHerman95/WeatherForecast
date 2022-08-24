package com.hermanbocharov.weatherforecast.data.network

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.hermanbocharov.weatherforecast.di.ApplicationScope
import javax.inject.Inject

@ApplicationScope
class NetworkManager @Inject constructor(
    application: Application
) {

    private val connectivityManager =
        application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun isNetworkAvailable(): Boolean {
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}