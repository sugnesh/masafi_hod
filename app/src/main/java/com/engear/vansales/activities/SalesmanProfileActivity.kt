package com.engear.vansales.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.service.autofill.UserData
import android.view.LayoutInflater
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.engear.vansales.R
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.databinding.ActivitySalesmanProfileBinding
import com.engear.vansales.merchandiser.CustomerItemActivity
import com.engear.vansales.utils.Utils

class SalesmanProfileActivity : BaseActivity(), OnMapReadyCallback {

    lateinit var binding: ActivitySalesmanProfileBinding
    var userInfo: UserData? = null
    private var mMap: GoogleMap? = null
    private var mSalesmanLC: Location? = null
    var mapFragment: SupportMapFragment? = null
    var productCount = "0"
    var mId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySalesmanProfileBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )

        init()
    }

    private fun init() {
        Utils.logData(
            this@SalesmanProfileActivity,
            "On Sale profile Screen "
        )
        setToolbarTitle("Profile")
        checkPermision()

        if (mPreferenceManager!!.getUserInfo() != null) {

            binding.txtCustomerName.text =
                mPreferenceManager!!.getUserInfo()!!.data.userInfo.firstname + " "
            mPreferenceManager!!.getUserInfo()!!.data.userInfo.lastname
            binding.txtCustomerContact.text =
                mPreferenceManager!!.getUserInfo()!!.data.userInfo.mobile
            binding.txtRole.text =
                mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.role.name
            binding.txtRoute.text =
                mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeName

        }

        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        if (mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.type.name.toString() == "Merchandise") {
            mId = mPreferenceManager!!.getUserId()!!
            productCount = db!!.getCustomerProductCount(mId)
            binding.txtProductList.text = productCount
        } else {
            mId =
                mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.id.toString()
            productCount =
                db!!.getCustomerProductCount(mId)
            binding.txtProductList.text = productCount
        }

        binding.btnProductList.setOnClickListener {
            if (productCount.toInt() > 0) {
                startActivity(Intent(me, CustomerItemActivity::class.java).apply {
                    this.putExtra("customer_code", mId)
                    this.putExtra("type", "Salesman")
                })
            }
        }

    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0
        if (mSalesmanLC != null) {
            val TutorialsPoint =
                LatLng(mSalesmanLC!!.latitude!!.toDouble(), mSalesmanLC!!.longitude!!.toDouble())
            mMap!!.addMarker(MarkerOptions().position(TutorialsPoint).title(""))
            mMap!!.moveCamera(CameraUpdateFactory.newLatLng(TutorialsPoint))
        }
    }

    fun checkPermision() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                10
            )
        } else if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                10
            )
        } else {
            if (!Utils.canGetLocation) {
                Utils.initialiseLocationVariables(
                    this@SalesmanProfileActivity,
                    object : Utils.LocationUpdate {
                        override fun onLocationDetect(isDetect: Boolean, mLocation: Location) {
                            if (isDetect) {
                                Utils.logData(
                                    this@SalesmanProfileActivity,
                                    "Location Fetch:" + AppConstants.Latitude.toString() + " ," +
                                            AppConstants.Longitude
                                )
                                mSalesmanLC = Location("Salesman")
                                mSalesmanLC!!.latitude = AppConstants.Latitude.toDouble()
                                mSalesmanLC!!.longitude = AppConstants.Longitude.toDouble()
                                if (mapFragment != null) {
                                    mapFragment!!.getMapAsync(this@SalesmanProfileActivity)
                                }

                            }
                        }
                    })
                Utils.startLocationUpdates(this@SalesmanProfileActivity)
            }
        }
    }
}