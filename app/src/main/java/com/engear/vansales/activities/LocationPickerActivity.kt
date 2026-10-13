package com.engear.vansales.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.engear.vansales.R
import com.engear.vansales.databinding.ActivityLocationPickerBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import android.os.Build
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.api.model.Place
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.util.Locale

class LocationPickerActivity :
    AppCompatActivity(),
    OnMapReadyCallback {

    private lateinit var binding: ActivityLocationPickerBinding

    private lateinit var googleMap: GoogleMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var latitude = 0.0
    private var longitude = 0.0
    private var address = ""

    companion object {
        private const val LOCATION_PERMISSION = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLocationPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Places SDK once
        if (!Places.isInitialized()) {
            Places.initialize(
                applicationContext,
                getString(R.string.google_maps_key)
            )
        }

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map)
                    as SupportMapFragment

        mapFragment.getMapAsync(this)

        binding.btnConfirm.setOnClickListener {

            val intent = Intent()

            intent.putExtra("address", address)

            intent.putExtra("latitude", latitude)

            intent.putExtra("longitude", longitude)

            setResult(RESULT_OK, intent)

            finish()

        }

        binding.btnMyLocation.setOnClickListener {
            moveToCurrentLocation()
        }

        binding.searchCard.setOnClickListener {

            val fields = listOf(

                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG

            )

            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN,
                fields
            ).build(this)

            placesLauncher.launch(intent)

        }
    }

    private fun getAddress(latLng: LatLng) {

        val geocoder = Geocoder(this, Locale.getDefault())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            geocoder.getFromLocation(
                latLng.latitude,
                latLng.longitude,
                1
            ) { addresses ->

                updateAddress(addresses)

            }

        } else {

            @Suppress("DEPRECATION")

            val addresses = geocoder.getFromLocation(
                latLng.latitude,
                latLng.longitude,
                1
            )

            updateAddress(addresses)

        }

    }

    private fun updateAddress(addresses: List<Address>?) {

        if (addresses.isNullOrEmpty())
            return

        address = addresses[0].getAddressLine(0)

        binding.txtAddress.text = address

    }

    private val placesLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            when (result.resultCode) {

                RESULT_OK -> {
                    result.data?.let {
                        val place = Autocomplete.getPlaceFromIntent(it)

                        place.latLng?.let { latLng ->
                            googleMap.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(latLng, 17f)
                            )
                        }
                    }
                }

                RESULT_CANCELED -> {
                    // User pressed back
                }

                AutocompleteActivity.RESULT_ERROR -> {
                    val status = Autocomplete.getStatusFromIntent(result.data!!)
                    Log.e("Places", status.statusMessage ?: "Unknown error")
                }
            }
        }

    override fun onMapReady(map: GoogleMap) {

        googleMap = map

        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.isZoomControlsEnabled = false
        googleMap.uiSettings.isMapToolbarEnabled = false
        googleMap.uiSettings.isRotateGesturesEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = false

        requestLocationPermission()
    }

    private fun requestLocationPermission() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            enableMyLocation()

        } else {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION
            )

        }
    }

    private fun moveToCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->

                location?.let {

                    val latLng = LatLng(
                        it.latitude,
                        it.longitude
                    )

                    googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            latLng,
                            17f
                        )
                    )
                }
            }
    }

    private fun enableMyLocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        googleMap.isMyLocationEnabled = true

        moveToCurrentLocation()

        googleMap.setOnCameraIdleListener {

            val target = googleMap.cameraPosition.target

            latitude = target.latitude
            longitude = target.longitude

            binding.txtLatitude.text = latitude.toString()
            binding.txtLongitude.text = longitude.toString()

            getAddress(target)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (requestCode == LOCATION_PERMISSION) {

            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {

                enableMyLocation()

            }
        }
    }
}