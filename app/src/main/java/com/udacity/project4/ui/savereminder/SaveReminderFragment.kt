package com.udacity.project4.ui.savereminder

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.BuildConfig
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSaveReminderBinding
import com.udacity.project4.ui.savereminder.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.ui.savereminder.geofence.GeofenceBroadcastReceiver.Companion.ACTION_GEOFENCE_EVENT
import com.udacity.project4.ui.reminderslist.ReminderDataItem
import com.udacity.project4.utils.LocationPermissionHelper
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import com.udacity.project4.utils.showDialog
import dagger.hilt.android.AndroidEntryPoint
import org.koin.android.ext.android.inject

@AndroidEntryPoint
class SaveReminderFragment : BaseFragment<FragmentSaveReminderBinding,SaveReminderViewModel>() {

    override fun getViewDataBinding(): FragmentSaveReminderBinding {
        return  FragmentSaveReminderBinding.inflate(layoutInflater)
    }
    override val _viewModel: SaveReminderViewModel by activityViewModels()
    private val runningQOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    private  var activityResultLauncherPermissions =
         registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ result->
              handelPermissionResult(result)

         }

    @SuppressLint("SuspiciousIndentation")
    private val locationSettingResultRequest =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                checkLocationSettingsAndStartGeofence(false)
            } else{

            }

        }

    private lateinit var geofencingClient: GeofencingClient



    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(requireContext(), GeofenceBroadcastReceiver::class.java)
         intent.action = ACTION_GEOFENCE_EVENT
        PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(
                requireContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or FLAG_MUTABLE
            )
        }
        else {
            PendingIntent.getBroadcast(
                requireContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDisplayHomeAsUpEnabled(true)
        binding.viewModel = _viewModel
        geofencingClient = LocationServices.getGeofencingClient(requireActivity())
        binding.selectLocation.setOnClickListener {
            _viewModel.navigationCommand.value =
                NavigationCommand.To(SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment())
        }

        binding.saveReminder.setOnClickListener {
            if (_viewModel.validateEnteredData(_viewModel.getReminder())) {
                checkLocationPermissionsAndStartGeofencing()
        }
        }
    }



    private fun checkLocationPermissionsAndStartGeofencing() {
        if (foregroundLocationPermissionApproved() && backgroundLocationPermissionApproved()) {
            checkLocationSettingsAndStartGeofence()
        } else {
            if (!foregroundLocationPermissionApproved()) {
                requestForegroundLocationPermissions()
            }
            if (!backgroundLocationPermissionApproved()) {
                requestBackgroundLocationPermission()
            }

        }
    }
    private fun requestForegroundLocationPermissions(){
         activityResultLauncherPermissions.launch(
             arrayOf(
                 Manifest.permission.ACCESS_FINE_LOCATION,
                 Manifest.permission.ACCESS_COARSE_LOCATION
             )
         )
     }
    @TargetApi(Build.VERSION_CODES.Q)
    private fun requestBackgroundLocationPermission() {
        if (runningQOrLater) {
            activityResultLauncherPermissions
                .launch(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
        } else {
            return
        }
    }

    private fun foregroundLocationPermissionApproved(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @TargetApi(Build.VERSION_CODES.Q)
    private fun backgroundLocationPermissionApproved(): Boolean {
        return if (runningQOrLater) {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
    private fun checkLocationSettingsAndStartGeofence(resolve: Boolean = true) {

        //location request and location request builder
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        //get settings client by location services
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                try {
                    locationSettingResultRequest.launch(
                        IntentSenderRequest.Builder(exception.resolution.intentSender).build())
                } catch (e: Exception) {
                    Log.e("Location settings",
                        "Error getting location settings ${e.message.toString()}")
                }
            } else {
                Snackbar.make(
                    requireView(),
                    R.string.location_required_error,
                    Snackbar.LENGTH_LONG
                ).setAction(R.string.ok) {
                    checkLocationSettingsAndStartGeofence()
                }.show()
            }
        }
        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                addGeofenceForRemainder()
            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun addGeofenceForRemainder() {
        val currentGeofenceData = _viewModel.getReminder()

        val geofence = Geofence.Builder()
            .setRequestId(currentGeofenceData.id)
            .setCircularRegion(
                currentGeofenceData.latitude!!,
                currentGeofenceData.longitude!!,
                100f
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
            addOnSuccessListener {
                _viewModel.validateAndSaveReminder(_viewModel.getReminder())
            }
            addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Failed to add Geofence", Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun handelPermissionResult(result: Map<String, Boolean>){
        if(result.all { result -> result.value }) {
            checkLocationSettingsAndStartGeofence()

        } else{
            if(shouldShowRequestPermissionRationale( Manifest.permission.ACCESS_FINE_LOCATION)||
                shouldShowRequestPermissionRationale( Manifest.permission.ACCESS_COARSE_LOCATION)||
                shouldShowRequestPermissionRationale( Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            ){
                showDialog()
            }else{
                checkLocationPermissionsAndStartGeofencing()
            }

        }

    }


    override fun onDestroy() {
        super.onDestroy()
        _viewModel.onClear()
    }
}
