package com.engear.vansales.activities

import android.os.Bundle
import android.view.LayoutInflater
import com.engear.vansales.R
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.databinding.ActivityLocationBinding
import com.engear.vansales.model.CustomerData
import com.engear.vansales.utils.Utils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions


class LocationActivity : BaseActivity(), OnMapReadyCallback {
    lateinit var binding: ActivityLocationBinding
    private var mMap: GoogleMap? = null
    private var mSalesmanLC: LatLng? = null
    private var mCUstomerLC: LatLng? = null
    var mapFragment: SupportMapFragment? = null
    var customerCode: String? = null
    var markerPoints: ArrayList<LatLng> = ArrayList()
    var mType: String = ""
    var mCustLat: String = ""
    var mCustLong: String = ""

    private val locationArrayList: ArrayList<LatLng>? = null

    companion object {
        var allCustomerList: ArrayList<CustomerData.DataCustomer> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@LocationActivity,
            "On location Screen "
        )
        init()
        setUpMap()
    }

    private fun init() {
        setToolbarTitle("Location")

        mType = intent.getStringExtra("Type").toString()

        if (mType == "Single") {
            mCustLat = intent.getStringExtra("Latitude")!!
            mCustLong = intent.getStringExtra("Long")!!
        } else {

        }
    }

    private fun setUpMap() {

        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0
        mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
        mSalesmanLC = LatLng(AppConstants.Latitude.toDouble(), AppConstants.Longitude.toDouble())

        if (mType == "Single") {
            if (!mCustLat.isEmpty()) {
                mCUstomerLC = LatLng(mCustLat.toDouble(), mCustLong.toDouble())
                markerPoints.add(mCUstomerLC!!);

                // Creating MarkerOptions
                val options = MarkerOptions()

                // Setting the position of the marker
                options.position(mSalesmanLC!!)

                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))

                mMap!!.addMarker(options)

                val options1 = MarkerOptions()

                // Setting the position of the marker
                options1.position(mCUstomerLC!!)

                options1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))

                // Add new marker to the Google Map Android API V2

                // Add new marker to the Google Map Android API V2
                mMap!!.addMarker(options1)

                val bounds: LatLngBounds =
                    LatLngBounds.Builder().include(mSalesmanLC).include(mCUstomerLC).build()
                mMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120))
            }
        } else {

            val b = LatLngBounds.Builder()
            for (mData in allCustomerList!!) {
                if (mData.latitude!!.isNotEmpty()) {
                    val TamWorth = LatLng(mData.latitude!!.toDouble(), mData.longitude!!.toDouble())

                    var mMark = MarkerOptions().position(TamWorth).title(mData.userDetail.firstname)
                    mMap!!.addMarker(
                        MarkerOptions().position(TamWorth).title(mData.userDetail.firstname)
                    )
                    b.include(mMark.position)
                    // below line is use to zoom our camera on map.

                    // below line is use to zoom our camera on map.
                    mMap!!.animateCamera(CameraUpdateFactory.zoomTo(18.0f))

                    // below line is use to move our camera to the specific location.

                    // below line is use to move our camera to the specific location.
                    mMap!!.moveCamera(CameraUpdateFactory.newLatLng(TamWorth))
                }
            }

            val bounds = b.build()
            val padding = 20 // offset from edges of the map in pixels
            val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
            mMap!!.animateCamera(cu)

        }


    }
}