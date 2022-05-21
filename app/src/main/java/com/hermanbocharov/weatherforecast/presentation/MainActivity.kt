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
import com.google.android.material.snackbar.Snackbar
import com.hermanbocharov.weatherforecast.BuildConfig
import com.hermanbocharov.weatherforecast.R
import com.hermanbocharov.weatherforecast.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavView.selectedItemId = R.id.weather_now_page

        if (savedInstanceState == null) {
            val startFragment = CurrentWeatherFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container_view, startFragment)
                .commit()
        }

        binding.bottomNavView.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.location_page -> LocationFragment.newInstance()
                R.id.weather_now_page -> CurrentWeatherFragment.newInstance()
                R.id.weather_forecast_page -> WeatherForecastFragment.newInstance()
                R.id.settings_page -> SettingsFragment.newInstance()
                else -> throw RuntimeException("There is no fragment for itemId ${item.itemId}")
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, fragment)
                .commit()
            return@setOnItemSelectedListener true
        }

        /*if (locationPermissionApproved()) {
            viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
            viewModel.currentWeather.observe(this) {
                Log.d("TEST_OF_LOADING_DATA", it.cityName)
                Log.d("TEST_OF_LOADING_DATA", it.temp.toString())
                Log.d("TEST_OF_LOADING_DATA", it.feelsLike.toString())
                Log.d("TEST_OF_LOADING_DATA", it.description)
                Log.d("TEST_OF_LOADING_DATA", it.updateTime.toString())
            }
        } else {
            requestLocationPermission()
        }*/
    }

    /*private fun locationPermissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }*/

    /*private fun requestLocationPermission() {

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
            Log.d("TEST_OF_LOADING_DATA", "Request foreground only permission")
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                ACCESS_COARSE_LOCATION_RC
            )
        }
    }*/

    /*override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == ACCESS_COARSE_LOCATION_RC && grantResults.isNotEmpty()) {
            val permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (permissionGranted) {
                Log.d("TEST_OF_LOADING_DATA", "Permission Granted onRequestPermissionsResult()")
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
    }*/
}