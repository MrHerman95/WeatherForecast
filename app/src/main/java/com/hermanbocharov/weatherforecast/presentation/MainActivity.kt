package com.hermanbocharov.weatherforecast.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.hermanbocharov.weatherforecast.BuildConfig
import com.hermanbocharov.weatherforecast.R
import com.hermanbocharov.weatherforecast.data.geolocation.FusedLocationDataSource
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (locationPermissionApproved()) {
            Log.d("MainActivity", "Permission Granted OnCreate()")
            viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
            viewModel.getCurrentWeather().observe(this) {
                Log.d("TEST_OF_LOADING_DATA", it.cityName)
                Log.d("TEST_OF_LOADING_DATA", it.temp.toString())
                Log.d("TEST_OF_LOADING_DATA", it.feelsLike.toString())
                Log.d("TEST_OF_LOADING_DATA", it.description)
                Log.d("TEST_OF_LOADING_DATA", it.updateTime.toString())
            }
        } else {
            requestLocationPermission()
        }
    }

    private fun locationPermissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    private fun requestLocationPermission() {

        // true -> rejected before, want to use the feature again
        // false -> user asked not to ask him any more / permission disable
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            Snackbar.make(
                findViewById(R.id.activity_main),
                "Location permission needed for core functionality",
                Snackbar.LENGTH_LONG
            )
                .setAction("OK") {
                    // Request permission
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                        ACCESS_COARSE_LOCATION_RC
                    )
                }
                .show()
        } else {
            Log.d("MainActivity", "Request foreground only permission")
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                ACCESS_COARSE_LOCATION_RC
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == ACCESS_COARSE_LOCATION_RC && grantResults.isNotEmpty()) {
            val permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (permissionGranted) {
                Log.d("MainActivity", "Permission Granted onRequestPermissionsResult()")
            } else {
                Snackbar.make(
                    findViewById(R.id.activity_main),
                    "Permission was denied but is needed for core functionality",
                    Snackbar.LENGTH_LONG
                )
                    .setAction("Settings") {
                        // Build intent that displays the App settings screen.
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts(
                            "package",
                            BuildConfig.APPLICATION_ID,
                            null
                        )
                        intent.data = uri
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                    .show()
            }
        }
    }

    companion object {
        private const val ACCESS_COARSE_LOCATION_RC = 100
    }
}