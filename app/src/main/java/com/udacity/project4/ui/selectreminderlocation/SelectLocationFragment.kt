package com.udacity.project4.ui.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.BuildConfig
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.ui.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.LocationPermissionHelper
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*


private const val REQUEST_PERMISSIONS_REQUEST_CODE = 1
class SelectLocationFragment : BaseFragment() ,OnMapReadyCallback {

    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var activityResultLauncherPermissions: ActivityResultLauncher<Array<String>>

    private lateinit var mMap: GoogleMap
    private var marker: Marker? = null
    var userAskedPermissionBefore = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)
        binding.viewModel = _viewModel
        binding.lifecycleOwner = this
        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)
        setUpMap()
        enableUserLocation()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        LocationPermissionHelper(
            requireActivity(),
            requireView(), activityResultLauncherPermissions,
            false
        ).checkPermissionThenDoMethod {
            getUserLocation()
        }
        binding.btnSaveLocation.setOnClickListener {
            onLocationSelected()
        }
    }


    private fun setUpMap() {
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun onLocationSelected() {

        if(marker==null) {
            _viewModel.showSnackBar.value = getString(R.string.err_select_location)
        } else {
            Log.e(TAG, "onLocationSelected: ${marker!!.title}", )
            updateLocation(marker!!.position.latitude, marker!!.position.longitude,marker!!.title)
            _viewModel.navigationCommand.value = NavigationCommand.Back
        }


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {

            R.id.normal_map -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.hybrid_map -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            R.id.satellite_map -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_map -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            else -> {super.onOptionsItemSelected(item)}
        }

    override fun onMapReady(map: GoogleMap) {
            mMap = map
            setMapStyle(mMap)
            setPOIClick(mMap)
            setLongPressClick(mMap)


    }

    private fun setLongPressClick(map: GoogleMap)
    {
        map.setOnMapLongClickListener { latLng ->
            map.clear()
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )
            marker = map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Selected Location")
                    .snippet(snippet)
            )
            binding.btnSaveLocation.text = getString(R.string.save)

            marker?.showInfoWindow()
            map.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 25f))

        }
    }
    private fun setMapStyle(map: GoogleMap) {
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireContext(),R.raw.map_style)
              )

            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }
    private fun setPOIClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            map.clear()
            marker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            marker?.showInfoWindow()
            map.animateCamera(CameraUpdateFactory.newLatLng(poi.latLng))
        }
    }
    private fun updateLocation(latitude: Double, longitude:Double,locationName: String? = null) {
        _viewModel.latitude.value = latitude
        _viewModel.longitude.value = longitude
        _viewModel.reminderSelectedLocationStr.value = locationName
    }

    fun enableUserLocation(){
        activityResultLauncherPermissions =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
                if (result.all { result -> result.value }) {
                    getUserLocation()
                } else {

                    Snackbar.make(
                        requireView(),
                        R.string.permission_denied_explanation, Snackbar.LENGTH_LONG
                    )
                        .setAction(R.string.settings) {
                            startActivity(Intent().apply {
                                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                        }.show()
                }
            }



//        if(checkPermissionGranted()) {
//            if(gpsEnable()) {
//                getUserLocation()
//            } else {
//
//            }
//        } else if(shouldShowRequestPermission()) {
//            showDialog()
//        } else {
//            requestPermission()
//
//        }
    }

    @SuppressLint("MissingPermission")
    private fun getUserLocation() {
        if (::mMap.isInitialized){
            mMap.isMyLocationEnabled = true
        }
         val locationRequest = LocationRequest.Builder(10000).setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateIntervalMillis(100).setMaxUpdates(1).build()
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val currentLocationRequest = CurrentLocationRequest.Builder().setPriority(Priority.PRIORITY_HIGH_ACCURACY).build()
        val currentLocation = fusedLocationClient.getCurrentLocation(currentLocationRequest, null)
        currentLocation.addOnCompleteListener {
            if (it.isSuccessful) {
                mMap.isMyLocationEnabled = true
                val myCurrentLocation = it.result
                if (myCurrentLocation != null) {
                    setMyLocationOnMap(currentLocation.result)

                } else {
                    val locationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            val locationResult = locationResult.lastLocation
                            if (locationResult != null) {
                                val myLocation =
                                    LatLng(locationResult.latitude, locationResult.longitude)
                                mMap.animateCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        myLocation, 18f
                                    )
                                )

                            } else {
                                Log.e("TAG", "")
                            }
                        }
                    }
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest, locationCallback, Looper.myLooper()
                    )
                }
            } else {
                mMap.isMyLocationEnabled = false
            }
        }

    }


    private fun shouldShowRequestPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            userAskedPermissionBefore

        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE
        );
    }

    private fun showDialog() {
        val alertDialogBuilder =  AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Permission needed")
        alertDialogBuilder.setMessage("location  permission needed for accessing location")
        alertDialogBuilder.setPositiveButton("Open Setting",
            DialogInterface.OnClickListener { dialogInterface, i ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri: Uri = Uri.fromParts(
                    "package", requireActivity().packageName,
                    null
                )
                intent.data = uri
                 startActivity(intent)
            })
        alertDialogBuilder.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialogInterface, i ->
                Log.d(TAG, "onClick: Cancelling"
                )
            })

        val dialog: AlertDialog = alertDialogBuilder.create()
        dialog.show()



    }

    private fun gpsEnable(): Boolean {
        return true;
    }

    private fun checkPermissionGranted(): Boolean {
        return (ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                ==PackageManager.PERMISSION_GRANTED);

    }
    private fun setMyLocationOnMap(result: Location){
            mMap.apply {
                val userLocation = LatLng(result.latitude, result.longitude)
                val snippets = String.format(
                    Locale.getDefault(),
                    "Lat: %1$.5f, Long: %2$.5f",
                    userLocation.latitude,
                    userLocation.longitude
                )
                val location = getCompleteAddressString(userLocation.latitude, userLocation.longitude)

                addMarker(
                    MarkerOptions().position(userLocation).title(location).snippet(snippets)
                )
                animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 18f))
            }

            result.let {
                val lat = result.latitude
                val lng = result.longitude
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 15f))
            }

        }

    private fun getCompleteAddressString(latitude: Double, longitude: Double): String? {
        var strAdd = ""
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null) {
                val returnedAddress: Address = addresses[0]
                val strReturnedAddress = StringBuilder("")
                for (i in 0..returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }
                strAdd = strReturnedAddress.toString()

            } else {

            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()

        }
        return strAdd
    }


}
