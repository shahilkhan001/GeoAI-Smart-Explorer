package com.example.geographyexplorer

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.geographyexplorer.databinding.ActivityDetailBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class DetailActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnCameraIdleListener {

    private lateinit var binding: ActivityDetailBinding
    private var googleMap: GoogleMap? = null
    private var categoryName: String? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastValidLongitude: Double = 0.0

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            enableMyLocation()
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        categoryName = intent.getStringExtra("CATEGORY_NAME")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = categoryName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
        binding.fabMapType.setOnClickListener { showMapTypeDialog() }
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        menuInflater.inflate(R.menu.map_menu, menu)
        val searchItem = menu.findItem(R.id.action_map_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    searchLocation(query)
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        return true
    }

    private fun searchLocation(query: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(this@DetailActivity)
                val addressList: List<Address>? = geocoder.getFromLocationName(query, 1)
                if (!addressList.isNullOrEmpty()) {
                    val address = addressList[0]
                    val latLng = LatLng(address.latitude, address.longitude)
                    withContext(Dispatchers.Main) {
                        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
                        googleMap?.addMarker(MarkerOptions().position(latLng).title(query))
                        Toast.makeText(applicationContext, "Found: ${address.getAddressLine(0)}", Toast.LENGTH_LONG).show()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Location not found", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Error searching. Check your network.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true
        googleMap?.mapType = GoogleMap.MAP_TYPE_HYBRID
        googleMap?.setMinZoomPreference(1.0f)
        googleMap?.setPadding(0, 0, 0, 180)
        showLocationForCategory(categoryName)
        checkLocationPermission()
        googleMap?.setOnCameraIdleListener(this)
    }

    override fun onCameraIdle() {
        val cameraPosition = googleMap?.cameraPosition ?: return
        val currentLongitude = cameraPosition.target.longitude
        val wrappedLongitude = (currentLongitude + 180) % 360 - 180
        if (lastValidLongitude != wrappedLongitude) {
            lastValidLongitude = wrappedLongitude
            val newLatLng = LatLng(cameraPosition.target.latitude, wrappedLongitude)
            val newCameraPosition = CameraPosition.builder()
                .target(newLatLng)
                .zoom(cameraPosition.zoom)
                .bearing(cameraPosition.bearing)
                .tilt(cameraPosition.tilt)
                .build()
            googleMap?.moveCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition))
        }
    }

    private fun showMapTypeDialog() {
        val mapTypeOptions = arrayOf("Satellite", "Terrain", "Normal")
        val currentMapType = googleMap?.mapType ?: GoogleMap.MAP_TYPE_HYBRID
        val checkedItem = when (currentMapType) {
            GoogleMap.MAP_TYPE_HYBRID -> 0
            GoogleMap.MAP_TYPE_TERRAIN -> 1
            GoogleMap.MAP_TYPE_NORMAL -> 2
            else -> 0
        }
        AlertDialog.Builder(this)
            .setTitle("Select Map Type")
            .setSingleChoiceItems(mapTypeOptions, checkedItem) { dialog, which ->
                val newMapType = when (which) {
                    0 -> GoogleMap.MAP_TYPE_HYBRID
                    1 -> GoogleMap.MAP_TYPE_TERRAIN
                    2 -> GoogleMap.MAP_TYPE_NORMAL
                    else -> GoogleMap.MAP_TYPE_HYBRID
                }
                googleMap?.mapType = newMapType
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> enableMyLocation()
            else -> locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap?.isMyLocationEnabled = true
        }
    }

    private fun showLocationForCategory(category: String?) {
        val locationData = getLocationData(category)
        locationData?.let { (location, zoom, title) ->
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoom))
            lastValidLongitude = location.longitude
            googleMap?.addMarker(MarkerOptions().position(location).title(title))
        }
    }

    private fun getLocationData(category: String?): Triple<LatLng, Float, String>? {
        return when (category) {
            "Continents" -> Triple(LatLng(9.0820, 8.6753), 3f, "Africa (Nigeria region)")
            // Central Africa visible easily

            "Oceans" -> Triple(LatLng(0.0000, -160.0000), 3f, "Pacific Ocean")
            // Mid-Pacific, zoomed out

            "Mountains" -> Triple(LatLng(27.9881, 86.9250), 12f, "Mount Everest")
            // Everest, Nepal/Tibet

            "Deserts" -> Triple(LatLng(23.4162, 25.6628), 6f, "Sahara Desert, Egypt")
            // Central Sahara, dunes visible

            "Rivers" -> Triple(LatLng(30.0444, 31.2357), 8f, "Nile River, Cairo")
            // Nile around Cairo, very distinct

            "Landmarks" -> Triple(LatLng(48.8584, 2.2945), 15f, "Eiffel Tower, Paris")
            // Famous landmark, visible cityscape

            "Volcanoes" -> Triple(LatLng(19.4069, -155.2834), 11f, "Mauna Loa, Hawaii")
            // World’s largest volcano, very visible

            "Lakes" -> Triple(LatLng(-1.4127, 29.8815), 9f, "Lake Kivu, Africa")
            // Big and easily seen on map

            "Islands" -> Triple(LatLng(-17.7134, 178.0650), 7f, "Fiji Islands")
            // Island chain in Pacific, very clear

            "Forests" -> Triple(LatLng(-3.4653, -62.2159), 6f, "Amazon Rainforest, Brazil")
            // Dense green satellite view

            else -> Triple(LatLng(20.5937, 78.9629), 5f, "India")
        }
    }


    override fun onResume() { super.onResume(); binding.mapView.onResume() }
    override fun onStart() { super.onStart(); binding.mapView.onStart() }
    override fun onStop() { super.onStop(); binding.mapView.onStop() }
    override fun onPause() { super.onPause(); binding.mapView.onPause() }
    override fun onDestroy() { super.onDestroy(); binding.mapView.onDestroy() }
    override fun onLowMemory() { super.onLowMemory(); binding.mapView.onLowMemory() }
    override fun onSupportNavigateUp(): Boolean { onBackPressedDispatcher.onBackPressed(); return true }
}