package com.engear.vansales.activities

import android.Manifest
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.MenuItemCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.R
import com.engear.vansales.adapter.CustomerAdapter
import com.engear.vansales.adapter.VisitedCustomerAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityJourneyPlanBinding
import com.engear.vansales.databinding.OrderCommentDialogBinding
import com.engear.vansales.firebase.MyFirebaseMessagingService
import com.engear.vansales.interfaces.OnConfirmationDialogClick
import com.engear.vansales.interfaces.OnDialog
import com.engear.vansales.interfaces.OnShopItemClickListener
import com.engear.vansales.merchandiser.CustomerProfileActivity
import com.engear.vansales.model.*
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sh.locus.lotr.model.VisitStatusRequest
import sh.locus.lotr.sdk.LocusLotrSdk
import sh.locus.lotr.sdk.LotrSdkReadyCallback
import sh.locus.lotr.sdk.VisitStatusUpdateParams
import sh.locus.lotr.sdk.auth.UserAuthParams
import sh.locus.lotr.sdk.exception.LotrSdkError
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class JourneyPlanActivity : BaseActivity(), OnShopItemClickListener {

    private val filterCode = 200
    private val TAG: String = "JourneyPlanActivity"
    lateinit var binding: ActivityJourneyPlanBinding
    var recentCustomerList: ArrayList<CustomerData.DataCustomer> = ArrayList()
    var allCustomerList: ArrayList<CustomerData.DataCustomer> = ArrayList()
    var sequenceCustomerList: ArrayList<CustomerData.DataCustomer> = ArrayList()
    var allData: ArrayList<CustomerData.DataCustomer> = ArrayList()
    var day: String? = ""
    var planId: String? = ""
    var weekNumber: String? = "1"
    var mSeqAdapter: CustomerAdapter? = null
    var mAllAdapter: CustomerAdapter? = null
    var mTypeCustomer: String = "Sequence"
    var isFilter = false
    var isEnforceFlag: String? = null
    var mProgressDialog: ProgressDialog? = null
    var lobIdList = ""
    var mSelectedPosition: Int = -1
    var mISSeq: String = "0"
    var unSchedulePrinteble: String = ""
    var customerSkipPrinteble: String = ""
    var customerSkipOriginalPrinteble: String = ""
    private var mDialog: Dialog? = null
    var str_alert_message: String = ""

    var str_approval_types: String = ""
    var visitId: String? = "0"
    var cust_count: Int = 0

    var locationManager: LocationManager? = null
    private var waitDialog: AlertDialog? = null
    var forceCheckIn: String = ""

    companion object {
        var customerSequenceNo = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityJourneyPlanBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )


        Utils.logData(
            this@JourneyPlanActivity,
            "On Journey plan Screen "
        )
        init()
    }

    private fun init() {
        setToolbarTitle("Journey Plan")

        setNavigationView(mPreferenceManager!!.getFTokenId()!!)

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

        day = Utils.getCurrentTimeInPattern("EEEE")
        weekNumber = "week${Utils.getWeekNumberOfMonth()}"
        Log.e("WeekNumber", weekNumber!!)

        if (weekNumber == "week6") {
            weekNumber = "week4"
        }

        forceCheckIn =
            mPreferenceManager!!.getString(AppConstants.FORCECHECKINENABLE).toString()

        unSchedulePrinteble =
            mPreferenceManager!!.getString(AppConstants.UNSCHEDULEPRINTENABLE).toString()
        customerSkipPrinteble =
            mPreferenceManager!!.getString(AppConstants.CUSTOMERSKIPENABLE).toString()

        customerSkipOriginalPrinteble =
            mPreferenceManager!!.getString(AppConstants.CUSTOMERORIGINALSKIPENABLE).toString()

        var salesLOB: List<UserData.Data.UserInfo.SalesmanInfo.SalesmanLob>? = null
        if (mPreferenceManager!!.getUserInfo() != null) {
            if (mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.lobList != null) {
                salesLOB = mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.lobList

                for (mLobId in salesLOB) {
                    if (lobIdList.isEmpty()) {
                        lobIdList = mLobId.lob_id.toString()
                    }
                }
            }
        }

        binding.btnCustomersData!!.setOnClickListener {
            Utils.logData(
                this@JourneyPlanActivity,
                "On btnCustomersData click"
            )
            startActivity(Intent(this, NewCustomerSyncActivity::class.java))
        }

        binding.btnAdd.setOnClickListener {
            Utils.logData(
                this@JourneyPlanActivity,
                "On Add button click"
            )
            binding.fabMenu.close(true)
            startActivity(Intent(this, AddCustomerActivity::class.java))
        }

        binding.btnSequence.setOnClickListener {
            Utils.logData(
                this@JourneyPlanActivity,
                "On sequance button click "
            )
            mTypeCustomer = "Sequence"
            binding.rvSequenceList.visibility = View.VISIBLE
            binding.rvAllCustomerList.visibility = View.GONE
            allData = sequenceCustomerList
            setUpBackground(binding.btnSequence)
        }

        binding.btnCustomer.setOnClickListener {
            Utils.logData(
                this@JourneyPlanActivity,
                "On Cutomer buttn click"
            )
            mTypeCustomer = "All"
            binding.rvAllCustomerList.visibility = View.VISIBLE
            binding.rvSequenceList.visibility = View.GONE
            allData = allCustomerList
            setUpBackground(binding.btnCustomer)
        }

//        if (mPreferenceManager!!.getString(AppConstants.ROLE) == "4" || mPreferenceManager!!.getString(
//                AppConstants.ROLE
//            ) == "3"
//        ) {
//            getDeliveryCustomer()
//        }

        binding.btnRefresh.setOnClickListener {
            Utils.logData(
                this@JourneyPlanActivity,
                "On refresh button click"
            )

            if (mPreferenceManager!!.getString(AppConstants.ROLE) == "4" || mPreferenceManager!!.getString(
                    AppConstants.ROLE
                ) == "3"
            ) {
                getDeliveryCustomer()
            } else {
                if (mTypeCustomer == "Sequence") {
                    if (sequenceCustomerList.size > 0) {
                        getJourneyPlan()
                    } else {
                        getJourneyPlan()
                    }
                } else {
                    getJourneyPlan()
                }
            }
        }

        binding.btnSequence.performClick()

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

    private fun getData() {
        allCustomerList = ArrayList()
        sequenceCustomerList = ArrayList()
        allCustomerList = db!!.getAllCustomer(lobIdList)
        if (mPreferenceManager!!.getString(AppConstants.ROLE) == "4") {
            sequenceCustomerList = db!!.getAllCustomer(lobIdList)
        } else if (mPreferenceManager!!.getString(AppConstants.ROLE) == "3") {
            sequenceCustomerList = db!!.getDelvierySequnceCustomer()

            Collections.sort(sequenceCustomerList, object : Comparator<CustomerData.DataCustomer> {
                override fun compare(
                    o1: CustomerData.DataCustomer?,
                    o2: CustomerData.DataCustomer?
                ): Int {
                    val seq1 = o1?.planSeq?.toIntOrNull()
                    val seq2 = o2?.planSeq?.toIntOrNull()

                    return when {
                        seq1 == null && seq2 == null -> 0
                        seq1 == null -> 1
                        seq2 == null -> -1
                        else -> seq1.compareTo(seq2)
                    }
                }
            })

        } else {
            sequenceCustomerList = db!!.getSequnceCustomer(day!!, weekNumber!!, "")
        }

        Log.e("Customer Size", sequenceCustomerList.size.toString())

        //sequenceCustomerList = db!!.getSequnceCustomerTime(day!!,lobIdList)
        if (day != null) {
            planId = db!!.getSequncePlanId(day!!)
            isEnforceFlag = db!!.getIsEnforceFlag(day!!)
        } else {
            planId = ""
            isEnforceFlag = "0"
        }

        //total seq customer
        mPreferenceManager!!.setString(
            AppConstants.TOTALSEQCUSTOMER,
            sequenceCustomerList.size.toString()
        )

        allData = ArrayList()
        setUpRecycler()

        if (sequenceCustomerList.size > 0) {
            allData = sequenceCustomerList
            if (isEnforceFlag == "1") {
                binding.btnCustomer.isEnabled = false
                binding.btnCustomer.isClickable = false
                binding.btnCustomer.alpha = 0.8f
            }
        } else {
            allCustomerList = db!!.getAllCustomer(lobIdList)
            allData = allCustomerList
        }

//        if (sequenceCustomerList.size > 0) {
//            allData = sequenceCustomerList
////            binding.btnCustomer.isEnabled = false
////            binding.btnCustomer.isClickable = false
////            binding.btnCustomer.alpha = 0.8f
//        } else {
//            allCustomerList = db!!.getAllCustomer()
//            allData = allCustomerList
//        }
    }

    private fun setUpBackground(btn: MaterialButton) {
        val cslWhite = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorWhite))

        val cslAssent = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent))

        binding.btnSequence.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.hint_gray
            )
        )
        binding.btnSequence.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.transparent
            )
        )

        binding.btnSequence.strokeColor = cslAssent

        binding.btnCustomer.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.hint_gray
            )
        )
        binding.btnCustomer.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.transparent
            )
        )

        binding.btnCustomer.strokeColor = cslAssent


        btn.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.colorWhite
            )
        )
        btn.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.colorAccent
            )
        )

        btn.strokeColor = cslWhite

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_customer, menu)
        val color: Int
        color = resources.getColor(R.color.colorWhite)

        var drawable =
            menu!!.findItem(R.id.action_search).icon

        drawable = DrawableCompat.wrap(drawable!!)
        DrawableCompat.setTint(drawable, color)
        menu!!.findItem(R.id.action_search).icon = drawable

        val searchView: SearchView =
            MenuItemCompat.getActionView(menu!!.findItem(R.id.action_search)) as SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setIconifiedByDefault(true)
        val searchEditText =
            searchView.findViewById(androidx.appcompat.R.id.search_src_text) as EditText
        searchEditText.hint = "Search"

        searchEditText.setTextColor(color)
        searchEditText.setHintTextColor(color)

        val searchIcon =
            searchView.findViewById(androidx.appcompat.R.id.search_button) as ImageView
        searchIcon
            .setImageResource(R.drawable.ic_search)

        val searchClose =
            searchView.findViewById(androidx.appcompat.R.id.search_close_btn) as ImageView
        searchClose.imageTintList =
            ColorStateList.valueOf(color)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String?): Boolean {
                if (mTypeCustomer == "All") {
                    mAllAdapter!!.filter!!.filter(s)
                } else {
                    mSeqAdapter!!.filter!!.filter(s)
                }
                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_map -> {

                LocationActivity.allCustomerList = ArrayList()
                LocationActivity.allCustomerList = allCustomerList
                startActivity(Intent(this, LocationActivity::class.java).apply {
                    this.putExtra("Type", "Multiple")
                })
                return true
            }
        }
        return false
    }

    private fun setUpRecycler() {
        binding.rvVisitedCustomerList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvVisitedCustomerList.adapter = VisitedCustomerAdapter(this, recentCustomerList)

        binding.rvAllCustomerList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAllAdapter = CustomerAdapter(this, allCustomerList, "all")
        binding.rvAllCustomerList.adapter = mAllAdapter


//        Collections.sort(sequenceCustomerList, object : Comparator<CustomerData.DataCustomer> {
//            override fun compare(
//                o1: CustomerData.DataCustomer?,
//                o2: CustomerData.DataCustomer?
//            ): Int {
//                val seq1 = o1?.planSeq?.toIntOrNull()
//                val seq2 = o2?.planSeq?.toIntOrNull()
//
//                return when {
//                    seq1 == null && seq2 == null -> 0
//                    seq1 == null -> 1
//                    seq2 == null -> -1
//                    else -> seq1.compareTo(seq2)
//                }
//            }
//        })

//        for (i in sequenceCustomerList.indices) {
//            if (db!!.getDelveiryCountCustomer(sequenceCustomerList!![i].user_id.toString()) > 0) {
//                sequenceCustomerList!![i].cust_delviery_time = db!!.getCustomerDelivery(
//                    sequenceCustomerList!![i].user_id.toString(),
//                    ""
//                )[0].deliveryTime
//            } else {
//                sequenceCustomerList!![i].cust_delviery_time = "23:59:59"
//            }
//        }
//        Collections.sort(sequenceCustomerList, Comparator<CustomerData.DataCustomer?> { o1, o2 ->
//            try {
//                return@Comparator SimpleDateFormat("h:mm:ss").parse(o1!!.cust_delviery_time)
//                    .compareTo(SimpleDateFormat("hh:mm:ss").parse(o2!!.cust_delviery_time))
//            } catch (e: ParseException) {
//                return@Comparator 0
//            }
//        })

        Collections.sort(sequenceCustomerList, object : Comparator<CustomerData.DataCustomer> {
            override fun compare(
                o1: CustomerData.DataCustomer?,
                o2: CustomerData.DataCustomer?
            ): Int {
                val seq1 = o1?.planSeq?.toIntOrNull()
                val seq2 = o2?.planSeq?.toIntOrNull()

                return when {
                    seq1 == null && seq2 == null -> 0
                    seq1 == null -> 1
                    seq2 == null -> -1
                    else -> seq1.compareTo(seq2)
                }
            }
        })


        binding.rvSequenceList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mSeqAdapter = CustomerAdapter(this, sequenceCustomerList, "sequence")
        binding.rvSequenceList.adapter = mSeqAdapter
    }

    fun getCustomerPosition(custId: Int, mType: String): Int {
        var position = 0

        if (mType == "all") {
            for (i in allCustomerList!!.indices) {
                if (allCustomerList!![i].userDetail.id == custId) {
                    position = i
                    break
                }
            }
        } else {
            for (i in sequenceCustomerList!!.indices) {
                if (sequenceCustomerList!![i].userDetail.id == custId) {
                    position = i
                    break
                }
            }
        }
        return position
    }

    fun getCurrentLocation(mPosition: Int, type: String) {

        var isOpen = false

        showOnlyProgressDialog()

        Utils.initialiseLocationVariables(
            this@JourneyPlanActivity,
            object : Utils.LocationUpdate {
                override fun onLocationDetect(isDetect: Boolean, mLocation: Location) {
                    if (isDetect) {
                        dismissDialog()
                        if (!isOpen) {
                            isOpen = true
                            openCUstomer(
                                mPosition,
                                type,
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
                                mPosition,
                                type,
                                "",
                                ""
                            )
                        }
                        Utils.stopLocationUpdates()
                    }
                }
            })

        Utils.startLocationUpdates(this@JourneyPlanActivity)
    }

    fun openCUstomer(position: Int, type: String, mLatitude: String, mLongitude: String) {

        if (type == "all") {

            mISSeq = "0"

            if (isVerifyGPS(
                    allCustomerList[position].latitude!!.toString(),
                    allCustomerList[position].longitude!!.toString(),
                    mLatitude,
                    mLongitude,
                    allCustomerList[position].user_id.toString()
                )
            ) {
                AppConstants.Latitude = mLatitude
                AppConstants.Longitude = mLongitude
                shopDialog(allCustomerList[position], position, "0")
            } else {

                var gpsStatus =
                    db!!.getCustomerGeoStatus(allCustomerList[position].user_id.toString())
                if (gpsStatus == "Send") {

                    var gpsId =
                        db!!.getCustomerGeoStatusID(allCustomerList[position].user_id.toString())
                    Log.e("GPS_id", gpsId)
                    GeoApprovalNotiStatus(
                        gpsId,
                        allCustomerList[position].user_id.toString(),
                        mISSeq
                    )

                } else if (gpsStatus == "Reject") {
                    showConfirmAlertDialog(
                        "You can not visit this customer because Geo approval Request was rejected by supervisor. if you want to visit again please take approval from supervisor.",
                        object : OnConfirmationDialogClick {
                            override fun onOk() {
                                AppConstants.Latitude = mLatitude
                                AppConstants.Longitude = mLongitude
                                gpsDialog(
                                    allCustomerList[position].latitude!!.toString(),
                                    allCustomerList[position].longitude!!.toString(),
                                    allCustomerList[position], position, "0"
                                )
                            }

                            override fun onCancel() {
                            }
                        })

                } else if (gpsStatus == "Approve") {
                    shopDialog(allCustomerList[position], position, "0")
                } else {
                    AppConstants.Latitude = mLatitude
                    AppConstants.Longitude = mLongitude
                    gpsDialog(
                        allCustomerList[position].latitude!!.toString(),
                        allCustomerList[position].longitude!!.toString(),
                        allCustomerList[position], position, "0"
                    )
                }

            }

        } else {
            mISSeq = "1"
            if (isVerifyGPS(
                    sequenceCustomerList[position].latitude!!.toString(),
                    sequenceCustomerList[position].longitude!!.toString(),
                    mLatitude,
                    mLongitude,
                    sequenceCustomerList[position].user_id.toString()
                )
            ) {
                AppConstants.Latitude = mLatitude
                AppConstants.Longitude = mLongitude
                shopDialog(sequenceCustomerList[position], position, "1")
            } else {

                var gpsStatus =
                    db!!.getCustomerGeoStatus(sequenceCustomerList[position].user_id.toString())

                if (gpsStatus == "Send") {

                    var gpsId =
                        db!!.getCustomerGeoStatusID(sequenceCustomerList[position].user_id.toString())
                    Log.e("GPS_id", gpsId)
                    GeoApprovalNotiStatus(
                        gpsId,
                        sequenceCustomerList[position].user_id.toString(),
                        mISSeq
                    )

//                    Utils.displayAlert(
//                        this@JourneyPlanActivity,
//                        "Geo approval Request is already sent. please wait until supervisor is approved."
//                    )
                } else if (gpsStatus == "Reject") {
                    showConfirmAlertDialog(
                        "You can not visit this customer because Geo approval Request was rejected by supervisor. if you want to visit again please take approval from supervisor.",
                        object : OnConfirmationDialogClick {
                            override fun onOk() {
                                AppConstants.Latitude = mLatitude
                                AppConstants.Longitude = mLongitude
                                gpsDialog(
                                    sequenceCustomerList[position].latitude!!.toString(),
                                    sequenceCustomerList[position].longitude!!.toString(),
                                    sequenceCustomerList[position], position, "0"
                                )
                            }

                            override fun onCancel() {
                            }
                        })
                } else if (gpsStatus == "Approve") {
                    shopDialog(sequenceCustomerList[position], position, "0")
                } else {
                    AppConstants.Latitude = mLatitude
                    AppConstants.Longitude = mLongitude
                    gpsDialog(
                        sequenceCustomerList[position].latitude!!.toString(),
                        sequenceCustomerList[position].longitude!!.toString(),
                        sequenceCustomerList[position], position, "1"
                    )
                }

            }
        }
    }

    private fun GeoApprovalNotiStatus(notificationId: String, mCustId: String, mIsSeq: String) {
        // Retrieve the notification_id from shared preferences
//        val sharedPreferencesAprStatus =
//            getSharedPreferences("approval_request_id", Context.MODE_PRIVATE)
//        val notificationId = sharedPreferencesAprStatus.getString("notification_id", null)

        if (notificationId != null) {
            // Create the payload
            val payload = JsonObject().apply {
                addProperty("id", notificationId)
            }
            Log.e("Payload", payload.toString())
            // Make the API call
            ApiClient.getClient(this).getApprovalStatusNoti(
                "Bearer ${userInfo!!.data.accessToken}",
                MyApplication.X_DOMAIN,
                payload
            ).enqueue(object : Callback<GeoApprovalStatusModel> {
                override fun onResponse(
                    call: Call<GeoApprovalStatusModel>, response: Response<GeoApprovalStatusModel>
                ) {
                    Log.d("GeoApprovalNotiStatus", "onResponse")
                    dismissDialog()
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && responseBody.status == true) {
                            val approvalStatus = responseBody.data?.firstOrNull()?.approvalStatus
                            if (approvalStatus == "Approve") {

                                db!!.updateCustomerGeoApproveRequest(mCustId, "Approve")
                                getData()

                            } else if (approvalStatus == "Reject") {
                                db!!.updateCustomerGeoApproveRequest(mCustId, "Reject")
                                getData()
                            } else {
                                Utils.displayAlert(
                                    this@JourneyPlanActivity,
                                    "Geo approval Request is already sent. please wait until supervisor is approved."
                                )

                            }
                        } else {
                            Toast.makeText(
                                this@JourneyPlanActivity,
                                "Request Failed",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    } else {
                        // Handle API error
                        Log.e("API Error", "API call failed with code: ${response.code()}")
                        Toast.makeText(
                            this@JourneyPlanActivity,
                            "Request Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<GeoApprovalStatusModel>, error: Throwable) {
                    Log.e("GeoApprovalNotiStatus", "onFailure", error)
                    // Handle network error
                    Log.e("Network Error", "Failed to make API call: ${error.message}")
                    Toast.makeText(this@JourneyPlanActivity, "Network Error", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } else {
            // Handle the case where notification_id is not found
            Log.e("GeoApprovalNotiStatus", "notification_id not found in shared preferences")
            Toast.makeText(
                this@JourneyPlanActivity,
                "Notification ID not found",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onShopItemClick(mPosition: Int, tag: String, type: String) {
        Log.w(TAG, "onShopItemClick: $")

        var position = getCustomerPosition(mPosition, type)
        mSelectedPosition = position

        if (type == "all" && tag == "dialog") {
            mISSeq = "0"
            if (forceCheckIn == "1") {
//                var mGeoEnable =
//                    db!!.getCustomerGeoChecking(allCustomerList[position].user_id.toString())
//                if (mGeoEnable == "1") {
                getCurrentLocation(position, type)
//                } else {
//                    shopDialog(allCustomerList[position], position, "1")
//                }
            } else {
                shopDialog(allCustomerList[position], position, "0")
            }

        } else if (type == "all" && tag == "location") {

            startActivity(Intent(this, LocationActivity::class.java).apply {
                this.putExtra("customer_code", allCustomerList[position].customer_code)
                this.putExtra("Type", "Single")
                this.putExtra("Latitude", allCustomerList[position].latitude!!)
                this.putExtra("Long", allCustomerList[position].longitude!!)
            })
        } else if (type == "all" && tag == "phone") {

            var mAllPhone = db!!.getCustomerPhoneNumber(allCustomerList[position].customer_code)
            val numbers = mutableListOf<String>()
            for (mData in mAllPhone!!) {
                numbers.add(mData)
            }

            showPhoneNumberDialog(numbers)
        } else if (type == "sequence" && tag == "phone") {

            var mAllPhone =
                db!!.getCustomerPhoneNumber(sequenceCustomerList[position].customer_code)
            val numbers = mutableListOf<String>()
            for (mData in mAllPhone!!) {
                numbers.add(mData)
            }

            showPhoneNumberDialog(numbers)
        } else if (type == "sequence" && tag == "dialog") {
            mISSeq = "1"
//            var mCustomerSeq = sequenceCustomerList[position].planSeq.toString()
//            var mLastVisit = db!!.getLastLocusVisitSequnece()
//            var mLastVisitStatusUp = db!!.getLastLocusVisitSequneceStatus()
//            var mLastVisitSTatus =
//                db!!.getLastLocusVisitCustomerSequneceStatus(sequenceCustomerList[position].user_id.toString())
//            Log.e("Last Plan", mLastVisit)
//            Log.e("Customer Plan", mCustomerSeq)
//            Log.e("mLastVisitSTatus", mLastVisitStatusUp)
//            Log.e("mCustVisitSTatus", mLastVisitSTatus)
//
//            if (mLastVisitSTatus == "1") {
//                if (mCustomerSeq.toInt() == 1 && mLastVisit.toInt() == 0) {
//                    mISSeq = "1"
//
//                    if (forceCheckIn == "1") {
//                        getCurrentLocation(position, type)
//                    } else {
//                        shopDialog(sequenceCustomerList[position], position, "1")
//                    }
//
//                } else {
//                    if (mLastVisit.toInt() == (mCustomerSeq.toInt() - 1)) {
//
//                        if (mLastVisit.toInt() == (mCustomerSeq.toInt())) {
//                            Log.e("Last ELSE", mLastVisit)
//                            mISSeq = "1"
//
//                            if (forceCheckIn == "1") {
//                                getCurrentLocation(position, type)
//                            } else {
//                                shopDialog(sequenceCustomerList[position], position, "1")
//                            }
//
//                        } else {
//                            if (mLastVisitSTatus == "1") {
//                                Log.e("Last ELSE", mLastVisit)
//                                mISSeq = "1"
//                                if (forceCheckIn == "1") {
//                                    getCurrentLocation(position, type)
//                                } else {
//                                    shopDialog(sequenceCustomerList[position], position, "1")
//                                }
//
//                            } else {
//                                Log.e("Last ELSE", mLastVisit)
//                                mISSeq = "1"
//                                if (forceCheckIn == "1") {
//                                    getCurrentLocation(position, type)
//                                } else {
//                                    shopDialog(sequenceCustomerList[position], position, "1")
//                                }
//                            }
//                        }
//
//                    } else {
//                        Log.e("Last ELSE", mLastVisit)
//                        if (mLastVisit.toInt() == (mCustomerSeq.toInt())) {
//                            Log.e("Last ELSE", mLastVisit)
//                            mISSeq = "1"
//                            if (forceCheckIn == "1") {
//                                getCurrentLocation(position, type)
//                            } else {
//                                shopDialog(sequenceCustomerList[position], position, "1")
//                            }
//
//                        } else {
//
//                            if (mLastVisit.toInt() > (mCustomerSeq.toInt())) {
//                                mISSeq = "1"
//                                if (forceCheckIn == "1") {
//                                    getCurrentLocation(position, type)
//                                } else {
//                                    shopDialog(sequenceCustomerList[position], position, "1")
//                                }
//
//                            } else {
//                                mISSeq = "1"
//                                if (forceCheckIn == "1") {
//                                    getCurrentLocation(position, type)
//                                } else {
//                                    shopDialog(sequenceCustomerList[position], position, "1")
//                                }
////                                    Toast.makeText(
////                                        this@JourneyPlanActivity,
////                                        "You can't deviate the Sequence",
////                                        Toast.LENGTH_SHORT
////                                    ).show()
//                            }
//                        }
//
//                    }
//                }
//            } else {
//                Toast.makeText(
//                    this@JourneyPlanActivity,
//                    "Kindly please click on the Start Button",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
            if (forceCheckIn == "1") {
                getCurrentLocation(position, type)
            } else {
                shopDialog(sequenceCustomerList[position], position, "1")
            }

            //shopDialog(sequenceCustomerList[position], position, "1")

        } else if (type == "sequence" && tag == "delivery") {

            mISSeq = "1"
            Log.e("Start TR", "Start")
            var mCustomerId = sequenceCustomerList[position].user_id.toString()
            var mDelivery = db!!.getCustomerDeliveryOrder(mCustomerId)
            login(
                MyApplication.LOCUS_CLIENT,
                mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.salesman_code,
                mPreferenceManager!!.getString(AppConstants.LOCUS_PASSWORD).toString(),
                mDelivery,
                sequenceCustomerList[position]
            )
            //login("masafiw-demo", "16955", "337726", mDelivery, sequenceCustomerList[position])

        } else if (type == "sequence" && tag == "location") {

//            var mLastVisit = db!!.getLastLocusVisitSequnece()
//            var mLastVisitSTatus = db!!.getLastLocusVisitSequneceStatus()
//            Log.e("Last Plan", mLastVisit)
//            Log.e("Customer Plan", mLastVisitSTatus)
//
//            if (mLastVisitSTatus == "1") {
//                startActivity(Intent(this, LocationActivity::class.java).apply {
//                    this.putExtra("customer_code", sequenceCustomerList[position].customer_code)
//                    this.putExtra("Type", "Single")
//                    this.putExtra("Latitude", sequenceCustomerList[position].latitude!!)
//                    this.putExtra("Long", sequenceCustomerList[position].longitude!!)
//                })
//            } else {
//                Toast.makeText(
//                    this@JourneyPlanActivity,
//                    "Kindly please click on the Start Button",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }

            startActivity(Intent(this, MultiOrderViewActivity::class.java).apply {
                this.putExtra("customer_code", sequenceCustomerList[position].customer_code)
                this.putExtra("Type", "Single")
                this.putExtra("Latitude", sequenceCustomerList[position].latitude!!)
                this.putExtra("Long", sequenceCustomerList[position].longitude!!)
            })
        }

    }

    fun Context.openDialer(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        startActivity(intent)
    }

    fun Context.showPhoneNumberDialog(
        phoneNumbers: List<String>// false = open dialer, true = direct call
    ) {
        if (phoneNumbers.isEmpty()) return

        AlertDialog.Builder(this)
            .setTitle("Select phone number")
            .setItems(phoneNumbers.toTypedArray()) { _, which ->
                val selectedNumber = phoneNumbers[which]
                openDialer(selectedNumber)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun gpsDialog(
        mCustLat: String,
        mCustLon: String,
        mCustomer: CustomerData.DataCustomer,
        position: Int,
        isSeq: String
    ) {
        try {
            val customerLocation = Location("").apply {
                if (mCustLat.isNotEmpty() && mCustLon.isNotEmpty()) {
                    latitude = mCustLat.toDoubleOrNull() ?: 0.0
                    longitude = mCustLon.toDoubleOrNull() ?: 0.0
                }
            }

            val salesmanLocation = Location("").apply {
                latitude = AppConstants.Latitude.toDoubleOrNull() ?: 0.0
                longitude = AppConstants.Longitude.toDoubleOrNull() ?: 0.0
            }

            val distance = salesmanLocation.distanceTo(customerLocation)
            Log.e("Distance", "$distance")

            val dialog = Dialog(this).apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setCancelable(true)
                setCanceledOnTouchOutside(false)
                setContentView(R.layout.alert_popup_view)
            }

            val tvCusLatitude = dialog.findViewById<TextView>(R.id.tvCusLatitude)
            val tvMerLatitude = dialog.findViewById<TextView>(R.id.tvMerLatitude)
            val tvRadius = dialog.findViewById<TextView>(R.id.tvRadius)
            val btnConfirm = dialog.findViewById<Button>(R.id.btn_confirm)
            val btnNo = dialog.findViewById<Button>(R.id.btn_no)

            tvCusLatitude.text = "Customer Location : $mCustLat, $mCustLon"
            tvMerLatitude.text =
                "Salesman Location : ${AppConstants.Latitude}, ${AppConstants.Longitude}"

            val results = FloatArray(1)
            Location.distanceBetween(
                salesmanLocation.latitude, salesmanLocation.longitude,
                customerLocation.latitude, customerLocation.longitude, results
            )

            val mRadiusDistance = if (results.isNotEmpty()) results[0].roundToInt() else 0
            Log.e("Meter", "$mRadiusDistance")

            tvRadius.text = "Distance Radius : $mRadiusDistance m"

            btnConfirm.setOnClickListener {
                dialog.dismiss()
                sendApprovalRequest(
                    mCustomer.user_id.toString(),
                    mCustLat,
                    mCustLon,
                    mRadiusDistance.toString()
                )
            }

            btnNo.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun reasonDialog(mCostomerId: String) {

        val factory = LayoutInflater.from(this)
        val binding: OrderCommentDialogBinding =
            DataBindingUtil.inflate(factory, R.layout.order_comment_dialog, null, false)

        val deleteDialogView: View = binding.root

        binding.txtName.text = "Enter Reason"

        val deleteDialog: AlertDialog = AlertDialog.Builder(this).create()
        deleteDialog.setView(deleteDialogView)

        binding.btnConfirm.setOnClickListener {

            var comment = binding.edtComment.text.toString()
            deleteDialog.dismiss()

            sendDeviationRequest(comment, mCostomerId)
        }

        deleteDialog.show()
    }

    private fun shopDialog(mCustomer: CustomerData.DataCustomer, position: Int, isSeq: String) {
        try {
            val dialog = Dialog(this).apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setCancelable(true)
                setCanceledOnTouchOutside(false)
                setContentView(R.layout.shop_popup_view)
            }

            val rlClose = dialog.findViewById<RelativeLayout>(R.id.rl_close)
            val rlOpen = dialog.findViewById<RelativeLayout>(R.id.rl_open)

            rlClose.setOnClickListener {
                dialog.dismiss()
                shopCloseDialog(mCustomer, position, isSeq)
            }

            rlOpen.setOnClickListener {
                val lastSeq = mPreferenceManager
                    ?.getString(AppConstants.LAST_VISITED_SEQ)
                    ?.toIntOrNull() ?: 0

                if (lastSeq < position + 1) {
                    mPreferenceManager?.setString(
                        AppConstants.LAST_VISITED_SEQ,
                        (position + 1).toString()
                    )
                }

                try {
                    if (isSeq == "1") {
                        val isVisitExist =
                            db?.isVisitCustomer(mCustomer.userDetail.id.toString(), isSeq) ?: false
                        var count =
                            mPreferenceManager?.getString(AppConstants.TOTAL_VISITED_CUSTOMER_SEQ)
                                .orEmpty()

                        if (!isVisitExist) {
                            val updatedCount = (count.toIntOrNull() ?: 0) + 1
                            mPreferenceManager?.setString(
                                AppConstants.TOTAL_VISITED_CUSTOMER_SEQ,
                                updatedCount.toString()
                            )
                        }

                        Log.d(
                            "VisitedCustomerSeq",
                            "Updated count: ${mPreferenceManager?.getString(AppConstants.TOTAL_VISITED_CUSTOMER_SEQ)}"
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                dialog.dismiss()

                val mVisited = VisitedList().apply {
                    customerId = mCustomer.userDetail.id.toString()
                    customerCode = mCustomer.customer_code
                    status = "open"
                    startTime = Utils.get24CurrentTime()
                    endTime = ""
                    latitude = AppConstants.Latitude
                    longitude = AppConstants.Longitude
                    reason = ""
                    comment = ""
                    isSequence = isSeq
                    completedTask = "0"
                    planId = this@JourneyPlanActivity.planId
                    visitedOffId = Utils.getVisitNo(mCustomer.userDetail.id.toString())
                }

                db?.updateCustomerShopStatus(mCustomer.userDetail.id.toString(), "0")
                db?.insertVisitedCustomer(mVisited, "open")

                val intent = if (mCustomer.isLob == 1) {
                    Intent(this, CustomerDivisionActivity::class.java).apply {
                        putExtra("customer_code", mCustomer.customer_code)
                        putExtra("seq_force", isEnforceFlag)
                    }
                } else {
                    CustomerDetailActivity.completedTask = 0
                    Intent(this, CustomerDetailActivity::class.java).apply {
                        putExtra("customer_code", mCustomer.customer_code)
                        putExtra("seq_force", isEnforceFlag)
                        putExtra("type", "JP")
                    }
                }
                startActivity(intent)
            }

            dialog.show()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun shopCloseDialog(
        mCustomer: CustomerData.DataCustomer,
        position: Int,
        isSeq: String
    ) {
        try {
            val arrReason = db?.getVisitReason() ?: mutableListOf()
            var reason = ""

            val dialog = Dialog(this).apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setCancelable(true)
                setCanceledOnTouchOutside(false)
                setContentView(R.layout.close_popup_view)
            }

            val spinner = dialog.findViewById<Spinner>(R.id.edtReason)
            val commentEditText = dialog.findViewById<EditText>(R.id.edt_comment)
            val btnCancel = dialog.findViewById<Button>(R.id.btn_cancel)
            val btnConfirm = dialog.findViewById<Button>(R.id.btn_confirm)

            val dataReasonAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                arrReason
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

            spinner.adapter = dataReasonAdapter
            spinner.setSelection(0)

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View?, position: Int, id: Long
                ) {
                    reason = arrReason[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            btnConfirm.setOnClickListener {
                val lastSeq =
                    mPreferenceManager?.getString(AppConstants.LAST_VISITED_SEQ)?.toIntOrNull() ?: 0
                if (lastSeq < position + 1) {
                    mPreferenceManager?.setString(
                        AppConstants.LAST_VISITED_SEQ,
                        (position + 1).toString()
                    )
                }

                dialog.dismiss()

                val mVisited = VisitedList().apply {
                    customerId = mCustomer.userDetail.id.toString()
                    customerCode = mCustomer.customer_code
                    status = "close"
                    startTime = Utils.get24CurrentTime()
                    endTime = Utils.get24CurrentTime()
                    latitude = AppConstants.Latitude
                    longitude = AppConstants.Longitude
                    this.reason = reason
                    comment = commentEditText.text.toString()
                    isSequence = isSeq
                    planId = this@JourneyPlanActivity.planId
                    completedTask = "0"
                    visitedOffId = Utils.getVisitNo(mCustomer.userDetail.id.toString())
                }

                db?.updateCustomerShopStatus(mCustomer.userDetail.id.toString(), "1")
                db?.insertVisitedCustomer(mVisited, "close")

                getData()
            }

            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
//        registerReceiver(
//            broadcastReceiver2,
//            IntentFilter(MyFirebaseMessagingService.BROADCAST_ACTION)
//        )
        if (!isFilter) {
            getData()
        }

    }

    override fun onPause() {
        super.onPause()
        // unregisterReceiver(broadcastReceiver2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == filterCode) {
            if (resultCode == Activity.RESULT_OK) {
                if (data!!.getStringExtra("isClear") != null) {
                    if (mTypeCustomer == "All") {
                        allCustomerList.clear()
                        allCustomerList = db!!.getAllCustomer(lobIdList)
                        allData = allCustomerList
                    } else {
                        sequenceCustomerList.clear()
                        sequenceCustomerList =
                            db!!.getSequnceCustomer(day!!, weekNumber!!, lobIdList)
                        allData = sequenceCustomerList
                    }
                    isFilter = true
                    setUpRecycler()
                } else {
                    val strObj = data!!.getStringExtra("itemList")
                    val listOfUomType: Type =
                        object : TypeToken<List<HashMap<String, String>?>?>() {}.type

                    var mList =
                        Gson().fromJson(strObj, listOfUomType) as ArrayList<HashMap<String, String>>

                    var salesOrg: ArrayList<String> = ArrayList()
                    var region: ArrayList<String> = ArrayList()
                    var channel: ArrayList<String> = ArrayList()
                    var paymentTerm: ArrayList<String> = ArrayList()
                    for (mDat in mList) {
                        if (mDat["Type"] == "Sales Org.") {
                            salesOrg.add(mDat["Id"]!!)
                        } else if (mDat["Type"] == "Region") {
                            region.add(mDat["Id"]!!)
                        } else if (mDat["Type"] == "Channel") {
                            channel.add(mDat["Id"]!!)
                        } else if (mDat["Type"] == "Payment Term") {
                            paymentTerm.add(mDat["Id"]!!)
                        }
                    }

                    if (mTypeCustomer == "All") {
                        allData = db!!.getAllCustomer(lobIdList)
                        allCustomerList.clear()
                        allCustomerList = getFilterItem(region, salesOrg, channel, paymentTerm)
                    } else {
                        allData = db!!.getSequnceCustomer(day!!, weekNumber!!, lobIdList)
                        sequenceCustomerList.clear()
                        sequenceCustomerList = getFilterItem(region, salesOrg, channel, paymentTerm)
                    }
                    isFilter = true
                    setUpRecycler()
                }
            }
        }
    }

    fun getFilterItem(
        mRegion: ArrayList<String>,
        mSalesOrg: ArrayList<String>,
        mChannel: ArrayList<String>,
        mPyamentTerm: ArrayList<String>
    ): ArrayList<CustomerData.DataCustomer> {

        var allRecords: ArrayList<CustomerData.DataCustomer> = ArrayList()

        for (mData in allData) {
            if (mRegion.isNotEmpty() && mSalesOrg.isNotEmpty() && mChannel.isNotEmpty() && mPyamentTerm.isNotEmpty()) {
                if (mRegion.contains(mData.region_id.toString()) && mSalesOrg.contains(mData.sales_organisation_id) && mChannel.contains(
                        mData.channel_id
                    ) && mPyamentTerm.contains(mData.payment_term_id.toString())
                ) {
                    allRecords.add(mData)
                }
            } else if (mRegion.isNotEmpty() && mSalesOrg.isNotEmpty()) {
                if (mRegion.contains(mData.region_id.toString()) && mSalesOrg.contains(mData.sales_organisation_id)) {
                    allRecords.add(mData)
                }
            } else if (mRegion.isNotEmpty() && mChannel.isNotEmpty()) {
                if (mRegion.contains(mData.region_id.toString()) && mChannel.contains(mData.channel_id)) {
                    allRecords.add(mData)
                }
            } else if (mRegion.isNotEmpty() && mPyamentTerm.isNotEmpty()) {
                if (mRegion.contains(mData.region_id.toString()) && mPyamentTerm.contains(mData.payment_term_id.toString())) {
                    allRecords.add(mData)
                }
            } else if (mSalesOrg.isNotEmpty() && mChannel.isNotEmpty()) {
                if (mSalesOrg.contains(mData.sales_organisation_id) && mChannel.contains(mData.channel_id)) {
                    allRecords.add(mData)
                }
            } else if (mSalesOrg.isNotEmpty() && mPyamentTerm.isNotEmpty()) {
                if (mSalesOrg.contains(mData.sales_organisation_id) && mPyamentTerm.contains(mData.payment_term_id.toString())) {
                    allRecords.add(mData)
                }
            } else if (mChannel.isNotEmpty() && mPyamentTerm.isNotEmpty()) {
                if (mChannel.contains(mData.channel_id) && mPyamentTerm.contains(mData.payment_term_id.toString())) {
                    allRecords.add(mData)
                }
            } else if (mRegion.isNotEmpty()) {
                if (mRegion.contains(mData.region_id.toString())) {
                    allRecords.add(mData)
                }
            } else if (mSalesOrg.isNotEmpty()) {
                if (mSalesOrg.contains(mData.sales_organisation_id)) {
                    allRecords.add(mData)
                }
            } else if (mChannel.isNotEmpty()) {
                if (mChannel.contains(mData.channel_id)) {
                    allRecords.add(mData)
                }
            } else if (mPyamentTerm.isNotEmpty()) {
                if (mPyamentTerm.contains(mData.payment_term_id.toString())) {
                    allRecords.add(mData)
                }
            }
        }
        return allRecords
    }

    private fun sendDeviationRequest(mReason: String, mCustomerId: String) {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty("route_id", userInfo!!.data.userInfo.salesmanInfo.routeId)
        jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
        jObj.addProperty(
            "supervisor_id",
            mPreferenceManager!!.getString(AppConstants.SUPERVISOR_ID)
        )
        jObj.addProperty("reason", mReason)
        jObj.addProperty("journey_plan_id", planId)
        jObj.addProperty("customer_id", mCustomerId)
        Log.e("Route Request", jObj.toString())

        ApiClient.getClient(this).sendDeviationRequest(
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
                            showWaitDialog()
                        } else {
                            Utils.displayAlert(this@JourneyPlanActivity, "Request Sent fail!!")
                        }
                    } else {
                        Utils.displayAlert(this@JourneyPlanActivity, "Request Sent fail!!")
                    }

                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    dismissDialog()
                    Utils.displayAlert(this@JourneyPlanActivity, "Request Sent fail!!")
                }
            })
    }

    fun showWaitDialog() {
        mProgressDialog = ProgressDialog(this@JourneyPlanActivity);
        mProgressDialog!!.setMessage("Please wait until supervisor approve..."); // Setting Message
        mProgressDialog!!.setTitle(""); // Setting Title
        mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog!!.show(); // Display Progress Dialog
        mProgressDialog!!.setCancelable(false);
    }

//    private val broadcastReceiver2 = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//
//            getData()
//        }
//    }

    private fun getJourneyPlan() {

        showOnlyProgressDialog()

        ApiClient.getClient(this).getJourneyPlan(
            "Bearer ${userInfo!!.data.accessToken}", MyApplication.X_DOMAIN,
            userInfo!!.data.userInfo.salesmanInfo.routeId
        ).enqueue(object : Callback<JourneyPlanData> {
            override fun onResponse(
                call: Call<JourneyPlanData>,
                response: Response<JourneyPlanData>
            ) {
//                Log.e("JourneyPlan",Gson().toJson(response.body()!!.data))
                if (response.isSuccessful) {
                    //Utils.logData(this@DownloadingDataActivity, "Journey Plan Response")
                    Log.e("JourneyPlan", Gson().toJson(response.body()!!.data))
                    if (response.body()!!.status!!) {
                        db!!.deleteJounreyPlan()
                        db!!.insertJourneyPlan(response.body()!!.data!!)
                    }
                }

                getCustomer()

            }

            override fun onFailure(call: Call<JourneyPlanData>, error: Throwable) {
                //Utils.logData(this@DownloadingDataActivity, "Journey Plan Response")
                getCustomer()
            }
        })
    }

    private fun getCustomer() {

        val jObj = JsonObject()
        jObj.addProperty("route_id", userInfo!!.data.userInfo.salesmanInfo.routeId)
        Log.e("CustomerData", jObj.toString())
        ApiClient.getClient(this).getAllCustomer(
            "Bearer ${userInfo!!.data.accessToken}", MyApplication.X_DOMAIN,
            jObj
        )
            .enqueue(object : Callback<CustomerData> {
                override fun onResponse(
                    call: Call<CustomerData>,
                    response: Response<CustomerData>
                ) {

                    if (response.isSuccessful) {
                        //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                        if (response.body()!!.status!!) {
//                            Log.e("CustomerData",Gson().toJson(response.body()!!.data))
                            db!!.insertCustomer(response.body()!!.data, lobIdList)
                        }
                    }

                    dismissDialog()

                    if (mPreferenceManager!!.getString(AppConstants.ROLE) == "3") {
                        getDeliveryCustomer()
                    } else {
                        getData()
                    }


                }

                override fun onFailure(call: Call<CustomerData>, error: Throwable) {
                    Utils.logData(this@JourneyPlanActivity, "Customer Failed")
                    dismissDialog()
                    if (mPreferenceManager!!.getString(AppConstants.ROLE) == "3") {
                        getDeliveryCustomer()
                    } else {
                        getData()
                    }
                }
            })
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
                        val responseBody = response.body()
                        if (responseBody != null && responseBody.status == true) {
                            val uuid = responseBody.data.uuid
                            val notificationId = responseBody.data.notification_id

                            // Store values in shared preferences
                            val sharedPreferencesAprStatus =
                                getSharedPreferences("approval_request_id", Context.MODE_PRIVATE)
                            val editor = sharedPreferencesAprStatus.edit()
                            editor.putString("uuid", uuid)
                            editor.putString("notification_id", notificationId)
                            editor.apply()

                            db!!.updateCustomerGeoRequest(customerId, "Send", notificationId!!)

                            Utils.displayAlert(
                                this@JourneyPlanActivity,
                                "Request Sent Successfully!!. Once supervisor will approve you can visit this customer."
                            )
                            getData()

//                            db!!.updateGeoRequest(customerId, "0")
//                            db!!.insertGeoRequest(customerId, "checkin")
//                            showWaitDialog()
                        }

                    } else {
                        Utils.displayAlert(this@JourneyPlanActivity, "Request Sent fail!!")
                    }
                } else {
                    Utils.displayAlert(this@JourneyPlanActivity, "Request Sent fail!!")
                }

            }

            override fun onFailure(call: Call<GeoApprovalData>, error: Throwable) {
                Log.e("CustomerBlock Fail", "Fail")
                dismissDialog()
                Utils.displayAlert(this@JourneyPlanActivity, "Request Sent fail!!")
            }
        })
    }


    fun dialogPrint(
        mContext: Context?,
        id: Int,
        mCUstLat: String,
        mCustLon: String,
    ) {
        val alertDialog = Dialog(mContext!!)
        alertDialog.setCancelable(false)
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.setContentView(R.layout.dialog_load_request_permission)

        var txtMsg: TextView? = alertDialog.findViewById<Button>(R.id.txt_msg)
        txtMsg!!.text =
            str_alert_message

        var btnConfirm: android.widget.Button? =
            alertDialog.findViewById<Button>(R.id.btnConfirm)

        var btnCancel: android.widget.Button? =
            alertDialog.findViewById<Button>(R.id.btnCancel)

        btnConfirm!!.setOnClickListener {
            alertDialog.dismiss()
            sendUnScheduleApprovalRequest(id.toString(), mCUstLat, mCustLon)
            //sendApprovalRequest(id.toString(), mCUstLat, mCustLon)
        }

        btnCancel!!.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun sendUnScheduleApprovalRequest(
        cust_id: String,
        mCUstLat: String,
        mCustLon: String,
    ) {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty("salesman_id", mPreferenceManager!!.getUserInfo()!!.data.userInfo.id)
        jObj.addProperty("salesman_lat", AppConstants.Latitude)
        jObj.addProperty("salesman_long", AppConstants.Longitude)
        jObj.addProperty("customer_id", cust_id)
        jObj.addProperty("customer_lat", mCUstLat)
        jObj.addProperty("customer_long", mCustLon)
        jObj.addProperty(
            "supervisor_id",
            mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.supervisor.id
        )
        jObj.addProperty("type", str_approval_types)

        Log.e("Van GeoApproval request", jObj.toString())

        ApiClient.getClient(this@JourneyPlanActivity).sendSupervisorGeoApprovalRequest(
            "Bearer ${BaseActivity.userInfo!!.data.accessToken}", MyApplication.X_DOMAIN,
            jObj
        ).enqueue(object : Callback<GeoApprovalData> {
            override fun onResponse(
                call: Call<GeoApprovalData>,
                response: Response<GeoApprovalData>
            ) {
                Log.e("Van GeoApproval Success", "Success")
                Log.e("Van GeoApproval request", response.body().toString())
                dismissDialog()
                if (response.isSuccessful) {
                    if (response.body()!!.status!!) {
                        showWaitDialog()
                    } else {
                        Utils.displayAlert(this@JourneyPlanActivity, "Request Sent fail!!")
                    }
                } else {
                    Utils.displayAlert(this@JourneyPlanActivity, "Request Sent fail!!")
                }

            }

            override fun onFailure(call: Call<GeoApprovalData>, error: Throwable) {
                Log.e("Van GeoApproval Fail", "Fail")
                Utils.dismissDialog(mDialog!!)
                Utils.displayAlert(this@JourneyPlanActivity, "Request Sent fail!!")
            }
        })
    }

    private fun getDeliveryCustomer() {
        Log.e("API CALL", "Customer Call")

        showOnlyProgressDialog()

        var date = mPreferenceManager!!.getString(AppConstants.STARTDATE)
        if (date!!.isEmpty()) {
            date = Utils.getCurrentDate()
        }

        val jObj = JsonObject()
        jObj.addProperty("date", date)
        jObj.addProperty(
            "salesman_id",
            mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.userId
        )
        Log.e("CustomerData", jObj.toString())
        ApiClient.getClient(this).getDeliveryCustomer(
            "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
            MyApplication.X_DOMAIN,
            jObj
        )
            .enqueue(object : Callback<CustomerData> {
                override fun onResponse(
                    call: Call<CustomerData>,
                    response: Response<CustomerData>
                ) {
                    Log.e("API CALL", "CUstomer  Response")
                    if (response.isSuccessful) {
                        //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                        if (response.body()!!.status!!) {
                            Log.e("CustomerData", Gson().toJson(response.body()!!.data))
                            //db!!.deleteCUstomer()
                            db!!.insertCustomer(response.body()!!.data, "")
                        }
                    }

                    getDelivery()

                }

                override fun onFailure(call: Call<CustomerData>, error: Throwable) {
                    Utils.logData(this@JourneyPlanActivity, "Customer Failed")
                    getDelivery()
                }
            })
    }

    private fun getDelivery() {

        var mDelivery: List<DeliveryHeader> = ArrayList()

        var date = mPreferenceManager!!.getString(AppConstants.STARTDATE)
        if (date!!.isEmpty()) {
            date = Utils.getCurrentDate()
        }
        val jObj = JsonObject()
        jObj.addProperty("date", date)
        jObj.addProperty(
            "salesman_id",
            mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.userId
        )
        Log.e("deliveries Request", jObj.toString())
        ApiClient.getClient(this).getDeliveryDate(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN,
            jObj
        ).enqueue(object : Callback<DeliveryData> {
            override fun onResponse(
                call: Call<DeliveryData>,
                response: Response<DeliveryData>
            ) {
                if (response.isSuccessful) {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                    Log.e("deliveries", Gson().toJson(response.body()))
                    if (response.body()!!.status) {
                        mDelivery = response.body()!!.data
                        db!!.insertDelivery(response.body()!!.data)
                    }
                }

                dismissDialog()

                getData()
                removeExtraorder(mDelivery)
            }

            override fun onFailure(call: Call<DeliveryData>, error: Throwable) {
                //Utils.logData(this@DownloadingDataActivity, "Order Type Failed")
                dismissDialog()
                Log.e("deliveriesFail", "Fail")
                getData()
            }
        })
    }

    fun removeExtraorder(mDelivery: List<DeliveryHeader>) {

        var deliveryList: MutableList<DeliveryHeader>? = java.util.ArrayList()

        deliveryList = db!!.getAllDelivery("", "")

        if (deliveryList.size > 0) {
            for (mDExData in deliveryList) {
                var isContacin = false
                for (mExpDa in mDelivery) {
                    if (mDExData.deliveryNumber == mExpDa.deliveryNumber) {
                        isContacin = true
                        break
                    }
                }

                if (!isContacin) {
                    if (mDExData.status == 0) {
                        //   db!!.deleteDelivery(mDExData.deliveryNumber)
                    }
                }
            }
        }
    }

    private fun login(
        clientId: String,
        userId: String,
        password: String,
        mOrder: String,
        mCustomer: CustomerData.DataCustomer
    ) {

        val sdkReadyCallback = object : LotrSdkReadyCallback {
            override fun onAuthenticated() {
                Log.e("Locus Faul", "Success")
                Toast.makeText(
                    this@JourneyPlanActivity,
                    "Locus Login Successfully",
                    Toast.LENGTH_LONG
                ).show()
                updateTaskSTatus(mOrder)
                setLocusVisit(mCustomer)
            }

            override fun onError(error: LotrSdkError) {
                Toast.makeText(this@JourneyPlanActivity, error.message, Toast.LENGTH_LONG).show()
                error.throwable?.printStackTrace()
                Log.e("Locus Faul", "Fail")
                setLocusVisit(mCustomer)
            }
        }

        // App can use either user's or client's login based on BuildConfig
        LocusLotrSdk.init(
            this@JourneyPlanActivity,
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
            status = VisitStatusRequest.StatusEnum.STARTED,
            checklistValues
        )

        asyncUpdatesManager.addVisitStatusUpdate(taskUpdate)
        asyncUpdatesManager.commit()

    }

    private fun setLocusVisit(mCustomer: CustomerData.DataCustomer) {
        val mVisited = VisitedList().apply {
            customerId = mCustomer.userDetail.id.toString()
            customerCode = mCustomer.customer_code
            status = "1"
            startTime = Utils.get24CurrentTime()
            endTime = ""
            latitude = AppConstants.Latitude
            longitude = AppConstants.Longitude
            reason = ""
            comment = ""
            isSequence = mCustomer.planSeq
            completedTask = "0"
            planId = ""
            visitedOffId = Utils.getVisitNo(mCustomer.userDetail.id.toString())
        }

        if (!db!!.isLocusVisitCustomer(mCustomer.userDetail.id.toString())) {
            db?.insertPlanLocusVisitedCustomer(mVisited, mVisited.status!!)
        } else {
            db?.updatePlanLocusVisitedCustomer(mVisited, mVisited.status!!)
        }

    }

}