package com.hermanbocharov.weatherforecast.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object PermissionsManager {

    fun isLocationPermissionGranted(context: Context): Boolean {
        return isPermissionGranted(
            context,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    fun onRequestLocationPermissionResult(
        fragment: Fragment,
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ) {
        onRequestPermissionResult(
            fragment,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            onGranted,
            onDenied
        )
    }

    private fun onRequestPermissionResult(
        fragment: Fragment,
        permission: Array<String>,
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ) {
        val requestMultiplePermissions =
            fragment.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val granted = permissions.entries.any {
                    it.value
                }

                when (granted) {
                    true -> onGranted()
                    false -> onDenied()
                }
            }

        requestMultiplePermissions.launch(
            permission
        )
    }

    private fun isPermissionGranted(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) return true
        }
        return false
    }
}