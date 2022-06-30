package com.hermanbocharov.weatherforecast.data.geolocation

import android.app.Application
import android.content.Context
import android.util.Log
import com.google.android.gms.location.LocationServices
import com.hermanbocharov.weatherforecast.di.ApplicationScope
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

@ApplicationScope
class FusedLocationDataSource @Inject constructor(
    application: Application
) {

    private val locationClient =
        LocationServices.getFusedLocationProviderClient(application.applicationContext)

    fun getLastLocation(): Single<GpsCoordinates> {

        return Single.create { e ->
            try {
                locationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val gpsCoordinates = GpsCoordinates(location.latitude, location.longitude)
                        e.onSuccess(gpsCoordinates)
                    } else {
                        e.onError(Throwable("Location is turned off"))
                    }
                }
            } catch (unlikely: SecurityException) {
                Log.d("TEST_OF_LOADING_DATA", "Lost location permissions. $unlikely")
            }
        }
    }
}