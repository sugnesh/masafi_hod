package com.engear.vansales.activities

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.engear.vansales.R
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityCustomerShopBinding
import com.engear.vansales.databinding.AlertPopupViewBinding
import com.engear.vansales.firebase.MyFirebaseMessagingService
import com.engear.vansales.interfaces.OnDialog
import com.engear.vansales.merchandiser.DistributionListActivity
import com.engear.vansales.model.BlockData
import com.engear.vansales.model.CustomerData
import com.engear.vansales.model.GeoApprovalData
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sh.locus.lotr.model.VisitStatusRequest
import sh.locus.lotr.sdk.LocusLotrSdk
import sh.locus.lotr.sdk.LotrSdkReadyCallback
import sh.locus.lotr.sdk.VisitStatusUpdateParams
import sh.locus.lotr.sdk.auth.UserAuthParams
import sh.locus.lotr.sdk.exception.LotrSdkError
import kotlin.math.roundToInt

class CustomerShopActivity : BaseActivity(), View.OnClickListener {

    lateinit var binding: ActivityCustomerShopBinding
    var customerObj: CustomerData.DataCustomer? = null
    var customerCode: String? = null
    var lobCode: String = ""
    var accountNumber: String = ""
    var mMobile: String = ""
    var mEmail: String = ""
    var mProgressDialog: ProgressDialog? = null
    var balance: Double = 0.0
    var salesman_route_sales_enable: String = ""
    var salesman_route_collection_enable: String = ""
    var salesman_route_return_enable: String = ""
    var isTCBlock = false
    var noOfInvoice: String = "0"
    var strTypeCheck = ""
    var isDayBlaock: String = "0"
    var isLimitBlaock: String = "0"
    var iCashBalance: String? = "0"
    var mCustomerBalance: String? = "0"

    var locationManager: LocationManager? = null
    private var waitDialog: AlertDialog? = null
    var forceCheckIn: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerShopBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )

        init()
    }

    private fun init() {

        customerCode = intent.getStringExtra("customer_code")
        iCashBalance = intent.getStringExtra("iCashBalance")
        accountNumber = intent.getStringExtra("icashAccount")!!
        mMobile = intent.getStringExtra("mobile")!!
        mEmail = intent.getStringExtra("email")!!
        mCustomerBalance = intent.getStringExtra("accountBalance")
        customerObj = db!!.getCustomerDetail(customerCode!!)
        Log.e("customer", Gson().toJson(customerObj))
        if (intent.getStringExtra("lob_code") != null) {
            lobCode = intent.getStringExtra("lob_code")!!
        }

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        locationManager!!.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            500, 2f, locationListenerGPS
        )

        forceCheckIn =
            mPreferenceManager!!.getString(AppConstants.FORCECHECKINENABLE).toString()

        setToolbarTitle(customerObj!!.userDetail.firstname + " " + customerObj!!.userDetail.lastname)

        isDayBlaock = db!!.isCustomerDayBlock(customerObj!!.userDetail.id.toString())
        isLimitBlaock = db!!.isCustomerLimitBlock(customerObj!!.userDetail.id.toString())

        salesman_route_sales_enable =
            mPreferenceManager!!.getString(AppConstants.SALESENABLE).toString()
        salesman_route_collection_enable =
            mPreferenceManager!!.getString(AppConstants.COLLECTIONENABLE).toString()
        salesman_route_return_enable =
            mPreferenceManager!!.getString(AppConstants.RETURNENABLE).toString()

        mPreferenceManager!!.setString(
            AppConstants.CUSTOMERPRINTENABLE,
            customerObj!!.printEnable
        )


        if (mPreferenceManager!!.getString(AppConstants.ROLE) == "1") {
            binding.lytTopView.visibility = View.GONE
            binding.lytBottomView.visibility = View.VISIBLE
        } else if (mPreferenceManager!!.getString(AppConstants.ROLE) == "4" || mPreferenceManager!!.getString(
                AppConstants.ROLE
            ) == "3" || mPreferenceManager!!.getString(
                AppConstants.ROLE
            ) == "6"
        ) {

            binding.lytBottomView.visibility = View.VISIBLE
            binding.txtOrder.text = "Delivery"
            binding.txtReturn.text = "Order"

            if (customerObj!!.channel_id == "1") {
                binding.lytTopView.visibility = View.VISIBLE
                Log.e("Categor", customerObj!!.customer_category_id.toString())
                if (customerObj!!.customer_category_id == "85" || customerObj!!.customer_category_id == "92") {
                    binding.btnSale.isClickable = true
                    binding.btnSale.alpha = 1.0F
                    binding.btnSale.isEnabled = true
                } else {
                    binding.btnSale.isClickable = false
                    binding.btnSale.alpha = 0.5F
                    binding.btnSale.isEnabled = false
                }
            } else {
                binding.lytTopView.visibility = View.GONE
            }

            if (mPreferenceManager!!.getString(
                    AppConstants.ROLE
                ) == "6"
            ) {
                binding.btnSale.isClickable = false
                binding.btnSale.alpha = 0.5F
                binding.btnSale.isEnabled = false
                binding.btnOrder.isClickable = false
                binding.btnOrder.alpha = 0.5F
                binding.btnOrder.isEnabled = false
                binding.btnCollection.isClickable = false
                binding.btnCollection.alpha = 0.5F
                binding.btnCollection.isEnabled = false
                binding.btnRPick.isClickable = false
                binding.btnRPick.alpha = 0.5F
                binding.btnRPick.isEnabled = false
                binding.btnDeviate.isClickable = false
                binding.btnDeviate.alpha = 0.5F
                binding.btnDeviate.isEnabled = false
            }
        } else {
            binding.lytDownView.visibility = View.GONE
        }

        if (customerObj!!.customer_type_id == "3") {
            var invCount =
                db!!.getCustomerOpenInvoice(customerObj!!.userDetail.id.toString()).toInt()
            noOfInvoice = invCount.toString()
            var pendingInv =
                db!!.getTCCustomerPendingInvoice(customerObj!!.userDetail.id.toString())
            isTCBlock = pendingInv >= invCount
        }

        // customerObj!!.isOverDue = "1"
        binding.btnSale.setOnClickListener(this)
        binding.btnReturn.setOnClickListener(this)
        binding.btnCollection.setOnClickListener(this)
        binding.btnTopUp.setOnClickListener(this)
        binding.btnOrder.setOnClickListener(this)
        binding.btnDeviate.setOnClickListener(this)
        binding.btnEndVisit.setOnClickListener(this)
        binding.btnRPick.setOnClickListener(this)
    }

    var locationListenerGPS: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val latitude = location.latitude
            val longitude = location.longitude
            AppConstants.Latitude = latitude.toString()
            AppConstants.Longitude = longitude.toString()
            val msg =
                "New Latitude: " + AppConstants.Latitude.toString() + "New Longitude: " + AppConstants.Longitude
            Log.e("Location Fetch:", msg)
            //Toast.makeText(CustomerDetailActivity.this, msg, Toast.LENGTH_LONG).show();
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSale -> {
                Utils.logData(
                    this@CustomerShopActivity,
                    "On Sale Fab click"
                )

                if (isTCBlock) {
                    Utils.displayAlert(
                        this@CustomerShopActivity,
                        "You can't sale to this customer because pending outstanding invoice is more than $noOfInvoice"
                    )
                } else {
                    if (isDayBlaock == "1") {
                        Utils.displayAlert(
                            this@CustomerShopActivity,
                            "You can't sale to this customer because customer is block!!"
                        )
                    } else if (isLimitBlaock == "2") {
                        Utils.displayAlert(
                            this@CustomerShopActivity,
                            "You can't take order to this customer because customer is block!!"
                        )
                    } else {

                        var mGeoEnable =
                            db!!.getCustomerSalesGeoChecking(customerObj!!.user_id.toString())
                        if (mGeoEnable == "1") {
                            strTypeCheck = "sale"
                            getCurrentLocation()
                        } else {
                            startActivity(Intent(me, InvoiceActivity::class.java).apply {
                                this.putExtra("customer_code", customerCode)
                                this.putExtra("lob_code", lobCode)
                                this.putExtra("iCashBalance", iCashBalance)
                                this.putExtra("accountBalance", mCustomerBalance)
                                this.putExtra("balance", balance.toString())
                                this.putExtra("accountNumber", accountNumber)
                                this.putExtra("mobile", mMobile)
                                this.putExtra("email", mEmail)
                            })
                        }

                    }

                }

            }

            R.id.btnReturn -> {
                Utils.logData(
                    this@CustomerShopActivity,
                    "On Return Fab click"
                )

                if (mPreferenceManager!!.getString(AppConstants.ROLE) == "6" || mPreferenceManager!!.getString(
                        AppConstants.ROLE
                    ) == "3"
                ) {
                    startActivity(Intent(me, OrderActivity::class.java).apply {
                        this.putExtra("customer_code", customerCode)
                        this.putExtra("lob_code", lobCode)
                    })
                } else {
                    var mGeoEnable =
                        db!!.getCustomerReturnGeoChecking(customerObj!!.user_id.toString())
                    if (mGeoEnable == "1") {
                        strTypeCheck = "return"
                        getCurrentLocation()
                    } else {
                        startActivity(Intent(me, ReturnActivity::class.java).apply {
                            this.putExtra("customer_code", customerCode)
                            this.putExtra("lob_code", lobCode)
                        })
                    }
                }


            }

            R.id.btnRPick -> {
                Utils.logData(
                    this@CustomerShopActivity,
                    "On Return Fab click"
                )

                startActivity(Intent(me, ReturnActivity::class.java).apply {
                    this.putExtra("customer_code", customerCode)
                    this.putExtra("lob_code", lobCode)
                })

            }

            R.id.btnCollection -> {
                Utils.logData(
                    this@CustomerShopActivity,
                    "On Collection Fab click"
                )
                var mGeoEnable =
                    db!!.getCustomerCollectionGeoChecking(customerObj!!.user_id.toString())
                if (mGeoEnable == "1") {
                    strTypeCheck = "collection"
                    getCurrentLocation()
                } else {
                    CollectionActivity.clearAll()
                    startActivity(Intent(me, CollectionActivity::class.java).apply {
                        this.putExtra("customer_code", customerCode)
                        this.putExtra("lob_code", lobCode)
                    })
                }

            }

            R.id.btnTopUp -> {
                Utils.logData(
                    this@CustomerShopActivity,
                    "On Topup Fab click"
                )
                //showDialog()
                startActivity(Intent(me, IcashTopupActivity::class.java).apply {
                    this.putExtra("customer_code", customerCode)
                    this.putExtra("mobile", mMobile)
                    this.putExtra("email", mEmail)
                    this.putExtra("icashAccount", accountNumber)
                })
            }

            R.id.btnOrder -> {
                Utils.logData(
                    this@CustomerShopActivity,
                    "On Exchange Fab click"
                )
                //showDialog()
                if (mPreferenceManager!!.getString(AppConstants.ROLE) == "4" || mPreferenceManager!!.getString(
                        AppConstants.ROLE
                    ) == "3"
                ) {
                    startActivity(Intent(me, DeliveryActivity::class.java).apply {
                        this.putExtra("customer_code", customerCode)
                        this.putExtra("lob_code", lobCode)
                        this.putExtra("iCashBalance", iCashBalance)
                        this.putExtra("accountBalance", mCustomerBalance)
                        this.putExtra("accountNumber", accountNumber)
                        this.putExtra("mobile", mMobile)
                        this.putExtra("email", mEmail)
                        this.putExtra("balance", "1000")
                    })

                } else {
                    if (isTCBlock) {
                        Utils.displayAlert(
                            this@CustomerShopActivity,
                            "You can't place order to this customer because pending outstanding invoice is more than $noOfInvoice"
                        )
                    } else {
                        if (isDayBlaock == "1") {
                            Utils.displayAlert(
                                this@CustomerShopActivity,
                                "You can't take order to this customer because customer is block!!"
                            )
                        } else if (isLimitBlaock == "2") {
                            Utils.displayAlert(
                                this@CustomerShopActivity,
                                "You can't take order to this customer because customer is block!!"
                            )
                        } else {
                            startActivity(Intent(me, OrderActivity::class.java).apply {
                                this.putExtra("customer_code", customerCode)
                                this.putExtra("lob_code", lobCode)
                            })
                        }

                    }

                }

            }

            R.id.btnOrder -> {
                Utils.logData(
                    this@CustomerShopActivity,
                    "On Exchange Fab click"
                )
                //showDialog()
                if (isDayBlaock == "1") {
                    Utils.displayAlert(
                        this@CustomerShopActivity,
                        "You can't take order to this customer because customer is block!!"
                    )
                } else if (isLimitBlaock == "2") {
                    Utils.displayAlert(
                        this@CustomerShopActivity,
                        "You can't take order to this customer because customer is block!!"
                    )
                } else {
                    startActivity(Intent(me, OrderActivity::class.java).apply {
                        this.putExtra("customer_code", customerCode)
                        this.putExtra("lob_code", lobCode)
                    })
                }
            }

            R.id.btnEndVisit -> {
                Utils.logData(
                    this@CustomerShopActivity,
                    "On End Visit Fab click"
                )

                var endTime = Utils.get24CurrentTime()
                db!!.updateLocusCustomerEndVisit(
                    endTime,
                    customerObj!!.userDetail.id.toString()
                )
                val intent = Intent(this, JourneyPlanActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }

            R.id.btnDeviate -> {
                Utils.logData(
                    this@CustomerShopActivity,
                    "On Deviate Fab click"
                )

                var mDelivery = db!!.getCustomerDeliveryOrder(customerObj!!.user_id.toString())
                login(
                    MyApplication.LOCUS_CLIENT,
                    mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.salesman_code,
                    mPreferenceManager!!.getString(AppConstants.LOCUS_PASSWORD).toString(),
                    mDelivery
                )

                db!!.updateLocusCustomerDeviateVisit(
                    customerObj!!.userDetail.id.toString()
                )
                val intent = Intent(this, JourneyPlanActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (customerCode != null) {
            customerObj = db!!.getCustomerDetail(customerCode!!)

            if (customerObj!!.customer_type_id == "3") {
                var invCount =
                    db!!.getCustomerOpenInvoice(customerObj!!.userDetail.id.toString()).toInt()
                noOfInvoice = invCount.toString()
                var pendingInv =
                    db!!.getTCCustomerPendingInvoice(customerObj!!.userDetail.id.toString())
                isTCBlock = pendingInv >= invCount
            }
        }

        if (CollectionActivity.isCollectionDone) {
            CollectionActivity.isCollectionDone = false
            finish()
        }

        registerReceiver(
            broadcastReceiver2,
            IntentFilter(MyFirebaseMessagingService.BROADCAST_ACTION)
        )

    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver2)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_transaction, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }

            R.id.action_survey -> {
                startActivity(
                    Intent(this, TransactionsActivity::class.java).apply {
                        this.putExtra("customer", customerObj!!.customer_code)

                    })
                return true
            }
        }
        return false
    }

    private fun showDialog() {
        val arrUData = arrayOf<String>("With Reference", "Without Reference")

        val mBuilder = AlertDialog.Builder(this@CustomerShopActivity)
        mBuilder.setTitle("Select an option")
        mBuilder.setSingleChoiceItems(arrUData, -1) { dialogInterface, i ->
            if (i == 0) {
                if (Utils.isNetworkAvailable(this)) {
                    startActivity(Intent(this, ExchangeActivity::class.java).apply {
                        this.putExtra("customer_code", customerCode)
                        this.putExtra("lob_code", lobCode)
                        this.putExtra("reType", "0")
                    })
                } else {
                    Toast.makeText(this, "Network not available!", Toast.LENGTH_SHORT).show()
                }
            } else {
                startActivity(Intent(this, ExchangeActivity::class.java).apply {
                    this.putExtra("customer_code", customerCode)
                    this.putExtra("lob_code", lobCode)
                    this.putExtra("reType", "1")
                })
            }
            dialogInterface.dismiss()
        }
        // Set the neutral/cancel button click listener
        mBuilder.setNeutralButton("Cancel") { dialog, which ->
            // Do something when click the neutral button
            dialog.cancel()
        }

        val mDialog = mBuilder.create()
        mDialog.show()
    }

    fun getCurrentLocation() {

        var isOpen = false

        showOnlyProgressDialog()

        Utils.initialiseLocationVariables(
            this@CustomerShopActivity,
            object : Utils.LocationUpdate {
                override fun onLocationDetect(isDetect: Boolean, mLocation: Location) {
                    if (isDetect) {
                        dismissDialog()
                        if (!isOpen) {
                            isOpen = true
                            openCUstomer(
                                mLocation.latitude.toString(),
                                mLocation.longitude.toString()
                            )
                        }
                        Utils.stopLocationUpdates()

                    } else {
                        dismissDialog()
                        if (!isOpen) {
                            isOpen = true
                            openCUstomer(
                                "",
                                ""
                            )
                        }
                        Utils.stopLocationUpdates()
                    }
                }
            })

        Utils.startLocationUpdates(this@CustomerShopActivity)
    }

    fun openCUstomer(mLatitude: String, mLongitude: String) {

        if (isVerifyGPS(
                customerObj!!.latitude!!.toString(),
                customerObj!!.longitude!!.toString(),
                mLatitude,
                mLongitude,
                customerObj!!.user_id.toString()
            )
        ) {
            AppConstants.Latitude = mLatitude
            AppConstants.Longitude = mLongitude
            if (strTypeCheck == "sale") {
                startActivity(Intent(me, InvoiceActivity::class.java).apply {
                    this.putExtra("customer_code", customerCode)
                    this.putExtra("lob_code", lobCode)
                    this.putExtra("balance", balance.toString())
                })
            } else if (strTypeCheck == "delivery") {
                startActivity(Intent(me, DeliveryActivity::class.java).apply {
                    this.putExtra("customer_code", customerCode)
                    this.putExtra("lob_code", lobCode)
                    this.putExtra("balance", "1000")
                })
            } else if (strTypeCheck == "collection") {
                CollectionActivity.clearAll()
                startActivity(Intent(me, CollectionActivity::class.java).apply {
                    this.putExtra("customer_code", customerCode)
                    this.putExtra("lob_code", lobCode)
                })
            } else if (strTypeCheck == "return") {
                startActivity(Intent(me, ReturnActivity::class.java).apply {
                    this.putExtra("customer_code", customerCode)
                    this.putExtra("lob_code", lobCode)
                })
            }
        } else {
            AppConstants.Latitude = mLatitude
            AppConstants.Longitude = mLongitude
            gpsDialog(
                customerObj!!.latitude!!.toString(),
                customerObj!!.longitude!!.toString(),
                customerObj!!, 0, "0"
            )

        }

    }

    fun isVerifyGPS(
        customerLatitude: String,
        customerLongitude: String,
        salesmanLatitude: String,
        salesmanLongitude: String,
        customerId: String
    ): Boolean {

        var isRadius = false

        if (customerLatitude.isEmpty()) {
            return true
        } else {
            val customerLocation = Location("")
            customerLocation.latitude = customerLatitude.toDouble()
            customerLocation.longitude = customerLongitude.toDouble()

            val salesmanLocation = Location("")
            salesmanLocation.latitude = salesmanLatitude.toDouble()
            salesmanLocation.longitude = salesmanLongitude.toDouble()

            val results: FloatArray = FloatArray(1)
            Location.distanceBetween(
                salesmanLatitude.toDouble(), salesmanLongitude.toDouble(),
                customerLatitude.toDouble(), customerLongitude.toDouble(), results
            )
            if (results.isEmpty()) {
                isRadius = true
            } else {
                var mMeter = results[0].roundToInt()
                Log.e("Meter", "" + mMeter)
                val radius: Double = db!!.getCustomerRadius(customerId).toDouble()
                isRadius = mMeter <= 100
            }
        }

        return isRadius

    }

    private fun gpsDialog(
        mCUstLat: String,
        mCustLon: String,
        mCustomer: CustomerData.DataCustomer,
        position: Int,
        isSeq: String
    ) {
        try {
            val custLat = mCUstLat.toDoubleOrNull()
            val custLon = mCustLon.toDoubleOrNull()
            val salesLat = AppConstants.Latitude.toDoubleOrNull()
            val salesLon = AppConstants.Longitude.toDoubleOrNull()

            if (custLat == null || custLon == null || salesLat == null || salesLon == null) {
                Toast.makeText(this, "Invalid GPS coordinates", Toast.LENGTH_SHORT).show()
                return
            }

            val customerLocation = Location("").apply {
                latitude = custLat
                longitude = custLon
            }

            val salesmanLocation = Location("").apply {
                latitude = salesLat
                longitude = salesLon
            }

            val distance = salesmanLocation.distanceTo(customerLocation)
            Log.e("Distance", "$distance")

            val binding = AlertPopupViewBinding.inflate(LayoutInflater.from(this))
            val dialog = AlertDialog.Builder(this).create().apply {
                setView(binding.root)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setCancelable(true)
            }

            val results = FloatArray(1)
            Location.distanceBetween(salesLat, salesLon, custLat, custLon, results)
            val mRadiusDistance = results.getOrNull(0)?.roundToInt() ?: 0
            Log.e("Meter", "$mRadiusDistance")

            binding.tvCusLatitude.text = "Customer Location : $mCUstLat, $mCustLon"
            binding.tvMerLatitude.text = "Salesman Location : $salesLat, $salesLon"
            binding.tvRadius.text = "Distance Radius : $mRadiusDistance m"

            binding.btnConfirm.setOnClickListener {
                dialog.dismiss()
                sendApprovalRequest(
                    mCustomer.user_id.toString(),
                    mCUstLat,
                    mCustLon,
                    mRadiusDistance.toString()
                )
            }

            binding.btnNo.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error displaying GPS dialog", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendApprovalRequest(
        customerId: String,
        cusLatitude: String,
        cusLongitude: String,
        mRadius: String
    ) {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty("salesman_id", mPreferenceManager!!.getUserInfo()!!.data.userInfo.id)
        jObj.addProperty("salesman_lat", AppConstants.Latitude)
        jObj.addProperty("salesman_long", AppConstants.Longitude)
        jObj.addProperty("customer_id", customerId)
        jObj.addProperty("customer_lat", cusLatitude)
        jObj.addProperty("customer_long", cusLongitude)
        jObj.addProperty("customer_long", cusLongitude)
        jObj.addProperty(
            "supervisor_id",
            mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.supervisor.id
        )
        jObj.addProperty("radius", mRadius)
        jObj.addProperty("type", "checkin")
        Log.e("GeoApproval request", jObj.toString())

        ApiClient.getClient(this).sendGeoApprovalRequest(
            "Bearer ${userInfo!!.data.accessToken}", MyApplication.X_DOMAIN,
            jObj
        ).enqueue(object : Callback<GeoApprovalData> {
            override fun onResponse(
                call: Call<GeoApprovalData>,
                response: Response<GeoApprovalData>
            ) {
                Log.e("CustomerBlock Success", "Success")
                dismissDialog()
                if (response.isSuccessful) {
                    if (response.body()!!.status!!) {
                        showWaitDialog()
                    } else {
                        Utils.displayAlert(this@CustomerShopActivity, "Request Sent fail!!")
                    }
                } else {
                    Utils.displayAlert(this@CustomerShopActivity, "Request Sent fail!!")
                }

            }

            override fun onFailure(call: Call<GeoApprovalData>, error: Throwable) {
                Log.e("CustomerBlock Fail", "Fail")
                dismissDialog()
                Utils.displayAlert(this@CustomerShopActivity, "Request Sent fail!!")
            }
        })
    }

    fun showWaitDialog() {
        mProgressDialog = ProgressDialog(this@CustomerShopActivity);
        mProgressDialog!!.setMessage("Please wait until supervisor approve..."); // Setting Message
        mProgressDialog!!.setTitle(""); // Setting Title
        mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog!!.show(); // Display Progress Dialog
        mProgressDialog!!.setCancelable(false);
    }

    private val broadcastReceiver2 = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            if (mProgressDialog != null) {

                if (mProgressDialog!!.isShowing)
                    mProgressDialog!!.dismiss()
            }

            if (intent.getStringExtra("status") != null) {

                if (intent.getStringExtra("status") == "Approve") {
                    if (intent.hasExtra("status")) {
                        showSimpleDialog(
                            "Your request has been approved by supervisor!",
                            "Alert",
                            object : OnDialog {
                                override fun onOk() {

                                    if (strTypeCheck == "sale") {
                                        startActivity(
                                            Intent(
                                                me,
                                                InvoiceActivity::class.java
                                            ).apply {
                                                this.putExtra("customer_code", customerCode)
                                                this.putExtra("lob_code", lobCode)
                                                this.putExtra("balance", balance.toString())
                                            })
                                    } else if (strTypeCheck == "delivery") {
                                        startActivity(
                                            Intent(
                                                me,
                                                DeliveryActivity::class.java
                                            ).apply {
                                                this.putExtra("customer_code", customerCode)
                                                this.putExtra("lob_code", lobCode)
                                                this.putExtra("balance", "1000")
                                            })
                                    } else if (strTypeCheck == "collection") {
                                        CollectionActivity.clearAll()
                                        startActivity(
                                            Intent(
                                                me,
                                                CollectionActivity::class.java
                                            ).apply {
                                                this.putExtra("customer_code", customerCode)
                                                this.putExtra("lob_code", lobCode)
                                            })
                                    } else if (strTypeCheck == "return") {
                                        startActivity(Intent(me, ReturnActivity::class.java).apply {
                                            this.putExtra("customer_code", customerCode)
                                            this.putExtra("lob_code", lobCode)
                                        })
                                    }

                                }
                            })
                    }
                } else {
                    Utils.displayAlert(
                        this@CustomerShopActivity,
                        "Your request has been rejected by supervisor!"
                    )
                }
            } else {
                Utils.displayAlert(
                    this@CustomerShopActivity,
                    "Your request has been rejected by supervisor!"
                )
            }
        }
    }

    private fun login(clientId: String, userId: String, password: String, mOrder: String) {

        val sdkReadyCallback = object : LotrSdkReadyCallback {
            override fun onAuthenticated() {
                //Toast.makeText(this@JourneyPlanActivity,"Locus Login Successfully", Toast.LENGTH_LONG).show()
                updateTaskSTatus(mOrder)
                //updateTourSTatus()
            }

            override fun onError(error: LotrSdkError) {
                Toast.makeText(this@CustomerShopActivity, error.message, Toast.LENGTH_LONG).show()
                error.throwable?.printStackTrace()
            }
        }

        // App can use either user's or client's login based on BuildConfig
        LocusLotrSdk.init(
            this@CustomerShopActivity,
            UserAuthParams(clientId, userId, password),
            true,
            sdkReadyCallback
        )

    }

    private fun updateTaskSTatus(mOrder: String) {

        val asyncUpdatesManager: sh.locus.lotr.sdk.AsyncUpdatesManager =
            LocusLotrSdk.beginAsyncUpdateInsertion()
        val checklistValues = mapOf(
            "isOtpVerified" to "true",
            "packageCondition" to "Good",
            "customerSignature" to "Yes"
        )

        val taskUpdate = VisitStatusUpdateParams(
            taskId = mOrder,
            visitId = "customer",
            status = VisitStatusRequest.StatusEnum.WAITING,
            checklistValues
        )

        asyncUpdatesManager.addVisitStatusUpdate(taskUpdate)
        asyncUpdatesManager.commit()

    }

}