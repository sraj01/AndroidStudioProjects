package com.example.tasklyy

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.tasklyy.databinding.ActivityMapLocationPickerBinding
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.io.IOException
import java.util.*

class MapLocationPickerScreen : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapLocationPickerBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var currentMarker: Marker? = null
    private var selectedAddress: String? = null

    companion object {
        const val EXTRA_ADDRESS = "selected_address"
        const val INITIAL_ADDRESS_HINT = "initial_address_hint"
        private const val DEFAULT_ZOOM_LEVEL = 15f
        private const val TAG = "MapLocationPicker"
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d(TAG, "ACCESS_FINE_LOCATION permission granted by user.")
                // because we are inside this 'isGranted' check.
                getCurrentLocation(selectPoint = true)
            } else {
                Log.w(TAG, "ACCESS_FINE_LOCATION permission denied by user.")
                Toast.makeText(
                    this,
                    "Location permission denied. Cannot get current location.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapLocationPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarMapPicker)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.places_api_key))
        }

        setupAutocompleteFragment()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.fabMyLocation.setOnClickListener { checkLocationPermissionAndGetLocation() }
        binding.btnConfirmMapLocation.setOnClickListener { confirmSelection() }
    }

    private fun setupAutocompleteFragment() {
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.map_picker_fragment) as AutocompleteSupportFragment

        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG))
        autocompleteFragment.setHint(getString(R.string.select_location))

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                handlePlaceSelection(place)
            }

            override fun onError(status: Status) {
                Log.e(TAG, "An error occurred during place selection: $status")
                Toast.makeText(this@MapLocationPickerScreen, "Search Error: $status", Toast.LENGTH_SHORT).show()
            }
        })


        val mapFragment =
            autocompleteFragment.childFragmentManager.findFragmentByTag("com.google.android.libraries.places.widget.internal.ui.SupportMapFragment") as? SupportMapFragment

        if (mapFragment != null) {
            mapFragment.getMapAsync(this)
        } else {
            autocompleteFragment.view?.post {
                val nestedMapFragment = autocompleteFragment.childFragmentManager.fragments.firstOrNull { it is SupportMapFragment } as? SupportMapFragment
                if (nestedMapFragment == null) {
                    Log.e(TAG, "Could not find SupportMapFragment inside AutocompleteSupportFragment")
                    Toast.makeText(this, "Error initializing map.", Toast.LENGTH_LONG).show()
                    return@post
                }
                nestedMapFragment.getMapAsync(this)
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.setOnMapClickListener { latLng -> handleMapClick(latLng) }

        val initialAddress = intent.getStringExtra(INITIAL_ADDRESS_HINT)
        if (!initialAddress.isNullOrEmpty()) {
            checkLocationPermissionAndGetLocation(moveCameraOnly = true)
        } else {
            checkLocationPermissionAndGetLocation(moveCameraOnly = true)
        }
    }

    private fun handlePlaceSelection(place: Place) {
        val latLng = place.latLng ?: return
        selectedAddress = place.address
        placeMarkerAndMoveCamera(latLng, place.name ?: "Selected Location")
        Log.i(TAG, "Place selected: ${place.name}, Address: ${place.address}")
    }

    private fun handleMapClick(latLng: LatLng) {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses: List<Address>? = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                selectedAddress = address.getAddressLine(0)
                placeMarkerAndMoveCamera(latLng, selectedAddress ?: "Clicked Location")
            } else {
                selectedAddress = "Lat: ${latLng.latitude}, Lng: ${latLng.longitude}"
                placeMarkerAndMoveCamera(latLng, "Selected Point")
            }
        } catch (e: IOException) {
            Log.e(TAG, "Geocoder service not available", e)
        }
    }

    private fun placeMarkerAndMoveCamera(latLng: LatLng, title: String) {
        if (!::googleMap.isInitialized) return
        currentMarker?.remove()
        currentMarker = googleMap.addMarker(MarkerOptions().position(latLng).title(title))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM_LEVEL))
        currentMarker?.showInfoWindow()
    }

    private fun checkLocationPermissionAndGetLocation(moveCameraOnly: Boolean = false) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation(selectPoint = !moveCameraOnly)
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    private fun getCurrentLocation(selectPoint: Boolean) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                if (selectPoint) {
                    handleMapClick(currentLatLng)
                } else {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM_LEVEL))
                }
            } else {
                Toast.makeText(this, "Could not get current location.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Log.e(TAG, "Failed to get current location", it)
        }
    }

    private fun confirmSelection() {
        if (selectedAddress != null) {
            val resultIntent = Intent().apply {
                putExtra(EXTRA_ADDRESS, selectedAddress)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        } else {
            Toast.makeText(this, "Please select a location first", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
