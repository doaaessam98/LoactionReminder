package com.udacity.project4.ui.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.ui.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import com.udacity.project4.utils.showDialog
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.util.*


@AndroidEntryPoint
class SelectLocationFragment : BaseFragment<FragmentSelectLocationBinding,SaveReminderViewModel>() ,OnMapReadyCallback {

    override val _viewModel: SaveReminderViewModel by activityViewModels()
    override fun getViewDataBinding(): FragmentSelectLocationBinding {
        return  FragmentSelectLocationBinding.inflate(layoutInflater)
    }

    private  val  fusedLocationClient :  FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireActivity())

    }

    private lateinit var geocoder : Geocoder

    private  var activityResultLauncherPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){result->
             handleRequestPermissionResult(result)

        }



    private lateinit var mMap: GoogleMap
    private var marker: Marker? = null




    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = _viewModel
        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)
         setUpMap()
         searchOnMap()
        binding.btnSaveLocation.setOnClickListener {
            onLocationSelected()
        }
    }

    private fun searchOnMap() {
        binding.seachView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                val location: String = binding.seachView.query.toString()
                var addressList: List<Address>? = null

                if(location.isNotEmpty()){
                    geocoder = Geocoder(requireContext())

                    try {
                        addressList = geocoder.getFromLocationName(location, 1);

                    }catch (e:IOException){
                        Log.e(TAG, "onQueryTextSubmit:${e.printStackTrace()} ")
                    }
                    val address = addressList!![0]
                    setLocationOnMap(address.latitude,address.longitude)


                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
               return false
            }
        })



    }
    @SuppressLint("SuspiciousIndentation")
    private fun setUpMap() {
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
             mapFragment.getMapAsync(this)
    }

    private fun onLocationSelected() {
        if(marker==null) {
            _viewModel.showSnackBar.value = getString(R.string.err_select_location)
        } else {
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
            getUserLocation()
        mMap.setOnPoiClickListener { poi ->
            setLocationOnMap(poi.latLng.latitude,poi.latLng.latitude)
        }
        mMap.setOnMapLongClickListener { latLng ->
            setLocationOnMap(latLng.latitude,latLng.latitude)

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

    private fun updateLocation(latitude: Double, longitude:Double,locationName: String? = null) {
        _viewModel.latitude.value = latitude
        _viewModel.longitude.value = longitude
        _viewModel.reminderSelectedLocationStr.value = locationName
    }


    @SuppressLint("MissingPermission")
    private fun getUserLocation() {
        if(isLocationPermissionGranted()){
            if(isGpsEnable()){
                 getCurrentLocation()
             }else{
                 // need to handel when enabled
                 enableGps()
             }
        } else{
            requestLocationPermission()
        }

    }
    private fun isLocationPermissionGranted():Boolean {
        return (ActivityCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }
    private fun requestLocationPermission() {
        activityResultLauncherPermissions.launch(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)
        )
    }
    private fun handleRequestPermissionResult(result: Map<String,Boolean>) {
        if(result.all { result -> result.value }) {
            getCurrentLocation()

        } else{
             if(shouldShowRequestPermissionRationale( Manifest.permission.ACCESS_FINE_LOCATION)){
                                  showDialog()
             }else{
                 getUserLocation()
             }

        }
    }
    private fun enableGps() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Please enable gps for better experience")
            .setPositiveButton(
                "Enable"
            ) { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                requireActivity().startActivity(intent)
            }
            .setCancelable(false)
            .show()

    }
    private fun isGpsEnable(): Boolean {
            val locationManager =
                context?.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        }


    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        mMap.isMyLocationEnabled = true
        val locationRequest = LocationRequest.Builder(10000).setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMinUpdateIntervalMillis(100).setMaxUpdates(1).build()
        val currentLocationRequest = CurrentLocationRequest.Builder().setPriority(Priority.PRIORITY_HIGH_ACCURACY).build()
        val currentLocation = fusedLocationClient.getCurrentLocation(currentLocationRequest, null)
        currentLocation.addOnCompleteListener {
             if(it.isSuccessful){
                 it.result?.let {location->
                     Log.e(TAG, "getCurrentLocation: ", )
                     setLocationOnMap(location.latitude,location.longitude)
                 }
             }else {
                 val locationCallback = object : LocationCallback() {
                     override fun onLocationResult(locationResult: LocationResult) {
                         val locationResult = locationResult.lastLocation
                         if (locationResult != null) {
                             Log.e(TAG, "2222222222222222222222222222222222222: ", )
                             setLocationOnMap(locationResult.latitude,locationResult.longitude)
                             val myLocation =
                                 LatLng(locationResult.latitude, locationResult.longitude)
                             mMap.animateCamera(
                                 CameraUpdateFactory.newLatLngZoom(
                                     myLocation, 18f
                                 )
                             )

                         }
                     }
                 }
                 fusedLocationClient.requestLocationUpdates(
                     locationRequest, locationCallback, Looper.myLooper()
                 )
             }
            }

    }

    private fun setLocationOnMap(latitude: Double, longitude: Double){
        Log.e(TAG, "setLocationOnMap: ", )
        val latLng = LatLng(latitude,longitude)
        val location = getCompleteAddressString(latLng.latitude, latLng.longitude)


                val snippet = String.format(
            Locale.getDefault(),
            "Lat: %1$.5f, Long: %2$.5f",
            latLng.latitude,
            latLng.longitude
        )

        mMap.clear()
        marker = mMap.addMarker(MarkerOptions().position(latLng).title(location).snippet(snippet))
        marker?.showInfoWindow()
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        binding.btnSaveLocation.text = getString(R.string.save)



//        val snippet = String.format(
//            Locale.getDefault(),
//            "Lat: %1$.5f, Long: %2$.5f",
//            latLng.latitude,
//            latLng.longitude
//        )
//        marker = mMap.addMarker(
//            MarkerOptions()
//                .position(latLng)
//                .title("Selected Location")
//                .snippet(snippet)
//        )
//        binding.btnSaveLocation.text = getString(R.string.save)
//
//        marker?.showInfoWindow()
//        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 25f))





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
            Log.e(TAG, "getCompleteAddressString:${e.printStackTrace()} ", )

        }
        return strAdd
    }



}
