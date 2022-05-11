package com.hermanbocharov.weatherforecast.data.geolocation

import android.content.Context
import android.util.Log
import com.google.android.gms.location.LocationServices
import io.reactivex.rxjava3.core.Single

class FusedLocationDataSource(
    context: Context
) {

    private val locationClient = LocationServices.getFusedLocationProviderClient(context)

    fun getLastLocation(): Single<GpsCoordinates> {

        return Single.create { e ->
            try {
                locationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val gpsCoordinates = GpsCoordinates(location.latitude, location.longitude)
                        e.onSuccess(gpsCoordinates)
                    } else {
                        Log.d("TEST_OF_LOADING_DATA", "Location is turned off")
                    }
                }
            } catch (unlikely: SecurityException) {
                Log.d("TEST_OF_LOADING_DATA", "Lost location permissions. $unlikely")
            }
        }
    }
}