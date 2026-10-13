package com.engear.vansales.merchandiser

import android.app.AlertDialog
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.engear.vansales.R
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityCustomerProfileBinding
import com.engear.vansales.model.CustomerData
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomerProfileActivity : BaseActivity(), OnMapReadyCallback {

    lateinit var binding: ActivityCustomerProfileBinding
    private var mMap: GoogleMap? = null
    var customerObj: CustomerData.DataCustomer? = null
    var customerCode: String? = null
    var custlat: Double = 0.0
    var custLong: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerProfileBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {

        customerCode = intent.getStringExtra("customer_code")
        customerObj = db!!.getCustomerDetail(customerCode!!)

        custlat = db!!.getCustomerLatitude(customerCode!!, "lat").toDouble()
        custLong = db!!.getCustomerLatitude(customerCode!!, "long").toDouble()
        setToolbarTitle("Customer Profile")

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)


        binding.txtCustomerName.text =
            customerObj!!.userDetail.firstname + " " + customerObj!!.userDetail.lastname
        binding.txtCustomerCode.text = customerObj!!.customer_code

        var mPaentDetail = db!!.getCustomerParentDetail(customerObj!!.user_id.toString())
        binding.txtParentCode!!.text = mPaentDetail["ParentCode"]

        var orderTypeId = ""
        if (customerObj!!.customer_type_id == "2") {
            orderTypeId = "Cash"
        } else if (customerObj!!.customer_type_id == "3") {
            orderTypeId = "B2B"
        } else {
            orderTypeId = "Credit"
        }

        if (customerObj!!.customer_address_1 != null) {
            binding.txtBill!!.text = "Address 1: "+customerObj!!.customer_address_1
        } else {
            binding.txtBill!!.text = "Address 1: "
        }
        if (customerObj!!.customer_address_2 != null) {
            binding.txtBill2!!.text = "Address 2: "+customerObj!!.customer_address_2
        } else {
            binding.txtBill2!!.text = "Address 2: "
        }
        binding.txtCustomerType.text = orderTypeId

        var shelfCount = db!!.getCustomerDistributionCount(customerObj!!.userDetail.id.toString())
        binding.txtShelfDisplay.text = shelfCount

        var productCount = db!!.getCustomerProductCount(customerObj!!.userDetail.id.toString())
        binding.txtProductList.text = productCount

        var channel = db!!.getCustomerChannel(customerObj!!.customer_code)
        binding.txtChannel!!.text = channel

        var subchannel = db!!.getCustomerSubChannel(customerObj!!.customer_code)
        binding.txtSubChannel!!.text = subchannel


        binding.btnProductList.setOnClickListener {
            startActivity(Intent(me, CustomerItemActivity::class.java).apply {
                this.putExtra("customer_code", customerCode)
                this.putExtra("type", "Customer")
            })
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_location, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_store -> {
                if (customerObj!!.channel_id == "1") {
                    showLocationCapture()
                } else {
                    var mGeoEnable =
                        db!!.getCustomerCaptureLocationGeoChecking(customerObj!!.user_id.toString())
                    if (mGeoEnable == "1") {
                        showLocationCapture()
                    } else {
                        Toast.makeText(
                            this,
                            "Capture location option is disable for this customer",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                return true
            }

            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return false
    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0
        // Add a marker in Sydney and move the camera
        if (customerObj!!.latitude!!.isNotEmpty() && customerObj!!.longitude!!
                .isNotEmpty()
        ) {
            val TutorialsPoint =
                LatLng(custlat, custLong)
            mMap!!.addMarker(MarkerOptions().position(TutorialsPoint).title(""))
            mMap!!.moveCamera(CameraUpdateFactory.newLatLng(TutorialsPoint))
        }
    }

    fun showLocationCapture() {
        AlertDialog.Builder(this)
            .setTitle("Select Option")
            .setMessage("Are you sure you want to capture this current location as customer location?")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(
                "Yes"
            ) { dialog, whichButton ->
                dialog.dismiss()
                getCurrentLocation()
            }
            .setNegativeButton("No", { dialog, whichButton ->
                dialog.dismiss()
            }).show()
    }

    fun getCurrentLocation() {

        var isOpen = false

        showOnlyProgressDialog()

        Utils.initialiseLocationVariables(
            this@CustomerProfileActivity,
            object : Utils.LocationUpdate {
                override fun onLocationDetect(isDetect: Boolean, mLocation: Location) {
                    if (isDetect) {
                        dismissDialog()
                        if (!isOpen) {
                            isOpen = true
                            captureLocation(
                                mLocation.latitude.toString(),
                                mLocation.longitude.toString()
                            )
                        }
                        Utils.stopLocationUpdates()

                    } else {
                        dismissDialog()
                        if (!isOpen) {
                            isOpen = true

                        }
                        Utils.stopLocationUpdates()
                    }
                }
            })

        Utils.startLocationUpdates(this@CustomerProfileActivity)
    }

    private fun captureLocation(
        mLatitude: String,
        mLongitude: String
    ) {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty("customer_id", db!!.getCustomerInfoId(customerObj!!.user_id.toString()))
        jObj.addProperty("customer_lat", mLatitude)
        jObj.addProperty("customer_long", mLongitude)

        Log.e("Location Request", jObj.toString())

        ApiClient.getClient(this).captureLocation(
            "Bearer ${userInfo!!.data.accessToken}",
            MyApplication.X_DOMAIN, jObj
        )
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {

                    dismissDialog()
                    if (response.isSuccessful) {
                        //Utils.logData(this@DownloadingDataActivity, "Journey Plan Response")
                        if (response.body()!!["status"].asBoolean) {

                            Toast.makeText(
                                this@CustomerProfileActivity,
                                "Location capture Successfully!!",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {
                            Toast.makeText(
                                this@CustomerProfileActivity,
                                "Location capture Fail!!",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    } else {
                        Toast.makeText(
                            this@CustomerProfileActivity,
                            "Location capture Fail!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    dismissDialog()
                    Toast.makeText(
                        this@CustomerProfileActivity,
                        "Location capture Fail!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}