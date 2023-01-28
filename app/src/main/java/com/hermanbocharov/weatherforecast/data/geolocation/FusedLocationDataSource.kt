package com.hermanbocharov.weatherforecast.data.geolocation

import android.app.Application
import android.content.Context
import android.location.LocationManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.hermanbocharov.weatherforecast.data.network.NetworkManager
import com.hermanbocharov.weatherforecast.di.ApplicationScope
import com.hermanbocharov.weatherforecast.exception.GeolocationDisabledException
import com.hermanbocharov.weatherforecast.exception.NoInternetException
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject


@ApplicationScope
class FusedLocationDataSource @Inject constructor(
    application: Application,
    private val networkManager: NetworkManager,
) {

    private val locationClient =
        LocationServices.getFusedLocationProviderClient(application.applicationContext)

    private val locationManager =
        application.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun getCurrentLocation(): Single<GpsCoordinates> {

        return Single.create { e ->
            try {
                var gpsEnabled = false
                var networkEnabled = false

                if (!networkManager.isNetworkAvailable()) {
                    e.onError(NoInternetException())
                    return@create
                }

                try {
                    gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                } catch (ex: Exception) {
                }

                try {
                    networkEnabled =
                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                } catch (ex: Exception) {
                }

                if (!gpsEnabled && !networkEnabled) {
                    e.onError(GeolocationDisabledException())
                    return@create
                }

                locationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    object : CancellationToken() {
                        override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                            CancellationTokenSource().token

                        override fun isCancellationRequested() = false
                    })
                    .addOnSuccessListener {
                        it?.let {
                            val gpsCoordinates = GpsCoordinates(it.latitude, it.longitude)
                            e.onSuccess(gpsCoordinates)
                        }
                    }
                    .addOnFailureListener {
                        e.onError(GeolocationDisabledException())
                    }

            } catch (unlikely: SecurityException) {
            }
        }
    }
}