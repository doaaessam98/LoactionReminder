package com.udacity.project4.utils

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.BuildConfig
import com.udacity.project4.R

/**
 * This a helper class used to check and request permission
 * for a different android version with high accrue
 */
class LocationPermissionHelper(
    private val activity: Activity,
    private val snackBarView: View,
    private val activityResultLauncherPermissions: ActivityResultLauncher<Array<String>>,
    private val isBackgroundNeeded: Boolean
) {

    private val runningQOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkPermissionThenDoMethod(methodTODO: () -> Unit) {
        if (checkGPSEnabled()) {
            checkPermissionsAndStartingAMethod(methodTODO)
        }
    }

    /**
     * This method used to check that GPS/Location is enabled for
     * better experience and accurate location
     */
    private fun checkGPSEnabled(): Boolean {
        //init global activity to use it on more time
        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            MaterialAlertDialogBuilder(activity)
                .setMessage("Please enable gps for better experience")
                .setPositiveButton(
                    "Enable"
                ) { _, _ ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    activity.startActivity(intent)
                }
                .setCancelable(false)
                .show()
            return false
        }
        return true
    }

    /**
     * Start checking background and foreground permissions
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkPermissionsAndStartingAMethod(method: () -> Unit) {
        if (isBackgroundNeeded) {
            if (foregroundLocationPermissionApproved() && backgroundLocationPermissionApproved()) {
                method()
            } else {
                if (!foregroundLocationPermissionApproved()) {
                    requestForegroundLocationPermissions()
                }
                if (!backgroundLocationPermissionApproved()) {
                    requestBackgroundLocationPermission(method)
                }
                if (foregroundLocationPermissionApproved() && backgroundLocationPermissionApproved()) {
                    method()
                }
            }
        }else{
            if (foregroundLocationPermissionApproved()) {
                method()
            } else {
                if (!foregroundLocationPermissionApproved()) {
                    requestForegroundLocationPermissions()
                }
                if (foregroundLocationPermissionApproved()) {
                    method()
                }
            }
        }

    }

    /** ForegroundLocation **/

    /**
     * This method to request Foreground Location Permissions
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestForegroundLocationPermissions() {
        when {
            foregroundLocationPermissionApproved() -> {
                return
            }
            activity.shouldShowRequestPermissionRationale(
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                Snackbar.make(
                    snackBarView,
                    R.string.permission_denied_explanation, Snackbar.LENGTH_LONG
                )
                    .setAction(R.string.settings) {
                        // Displays App settings screen.
                        activity.startActivity(Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                    }.show()
            }
            else -> {
                activityResultLauncherPermissions.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    /**
     * This method to check Foreground Location Permissions
     * handle android 10 {Q}+
     */
    private fun foregroundLocationPermissionApproved(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /** BackgroundLocation **/

    /**
     * This method to request Background Location Permissions
     * handle android 10 {Q}+
     */
    @TargetApi(Build.VERSION_CODES.Q)
    private fun requestBackgroundLocationPermission(methodTODO: () -> Unit) {
        if (backgroundLocationPermissionApproved()) {
            methodTODO()
            return
        }
        if (runningQOrLater) {
            activityResultLauncherPermissions
                .launch(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
        } else {
            return
        }
    }

    /**
     * This method to check Background Location Permissions
     * handle android 10 {Q}+
     */
    @TargetApi(Build.VERSION_CODES.Q)
    private fun backgroundLocationPermissionApproved(): Boolean {
        return if (runningQOrLater) {
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }


}