package com.hermanbocharov.weatherforecast.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class PermissionsManager {

    fun isLocationPermissionGranted(context: Context): Boolean {
        return isPermissionGranted(context, Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    fun onRequestLocationPermissionResult(
        fragment: Fragment,
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ) {
        onRequestPermissionResult(
            fragment,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            onGranted,
            onDenied
        )
    }

    private fun onRequestPermissionResult(
        fragment: Fragment,
        permission: String,
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ) {
        val requestMultiplePermissions =
            fragment.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val granted = permissions.entries.all {
                    it.value
                }

                when (granted) {
                    true -> onGranted()
                    false -> onDenied()
                }
            }

        requestMultiplePermissions.launch(
            arrayOf(permission)
        )
    }

    private fun requestPermission(activity: Activity, permission: String, requestCode: Int) {
        if (isPermissionGranted(activity, permission).not()) {
            ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
        }
    }

    private fun isPermissionGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}