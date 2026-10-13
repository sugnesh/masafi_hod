package com.engear.vansales.activities

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.engear.vansales.R
import com.engear.vansales.adapter.ViewPagerAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.Constants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityDashboardBinding
import com.engear.vansales.databinding.OdometerDialogBinding
import com.engear.vansales.fragments.LoadFragment
import com.engear.vansales.fragments.SalesSummaryFragment
import com.engear.vansales.fragments.TargetFragment
import com.engear.vansales.fragments.TargetVMFragment
import com.engear.vansales.fragments.UnloadFragment
import com.engear.vansales.fragments.VanStockFragment
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.MTDdata
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.sync.BackgroundSync
import com.engear.vansales.utils.Utils
import com.google.android.gms.common.api.ResolvableApiException
import com.google.gson.JsonObject
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import okhttp3.ResponseBody
import org.slf4j.Logger
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sh.locus.lotr.sdk.LocusLocation
import sh.locus.lotr.sdk.LocusLotrSdk
import sh.locus.lotr.sdk.LotrSdkReadyCallback
import sh.locus.lotr.sdk.TrackingListener
import sh.locus.lotr.sdk.TrackingRequestParams
import sh.locus.lotr.sdk.auth.UserAuthParams
import sh.locus.lotr.sdk.exception.LotrSdkError
import java.text.SimpleDateFormat
import java.util.*


class DashboardActivity : BaseActivity(), OnItemClickListener, TrackingListener {
    lateinit var binding: ActivityDashboardBinding

    private val maxNumberOfLines = 3
    private val numberOfPoints = 3

    private var biginDate = ""
    private var biginTime = ""

    var day: String? = null
    var weekNumber: String? = null
    var totalCustomer = 0
    var visitedCustomer = 0
    private lateinit var logger: Logger
    var pagerAdapter: ViewPagerAdapter? = null
    private var currentPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@DashboardActivity,
            "On Dashboard Screen "
        )
        init()

    }

    private fun init() {
        setToolbarTitle("Dashboard")
        setNavigationView(mPreferenceManager!!.getFTokenId()!!)
        // tabClick();
        showNotification()

        checkPermision()
//        var isScanVi = db!!.getIsValidCoupon("298234", "8764046842")
//        Log.e("Isvalid",isScanVi)
//        var mFrom = "8764046842"
//        var mTo = "8764046844"
//        var fromserail = mFrom.toLong()
//        var toSerial = mTo.toLong()
//        var diff = (toSerial - fromserail) + 1
//        Log.e("IsDiff",diff.toString())
        userInfo = mPreferenceManager!!.getUserInfo()
        Log.e("token", userInfo!!.data.accessToken)

        //showTutorial();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this@DashboardActivity, BackgroundSync::class.java))
        } else {
            startService(Intent(this@DashboardActivity, BackgroundSync::class.java))
        }

        setStatePageAdapter()

        //mPreferenceManager!!.setString(AppConstants.COLLECTION_LAST, "575645")

        binding.btnBeginTrip.setOnClickListener {
            Utils.logData(
                this@DashboardActivity,
                "On Bigin trip button click"
            )

//            val calendar = Calendar.getInstance()
//            biginDate = Utils.getCurrentDate()
//
//            mPreferenceManager!!.setString(AppConstants.STARTDATE, biginDate)
////            val dfs = SimpleDateFormat("hh:mm")
////            biginTime = dfs.format(calendar.time)
//            biginTime = Utils.get24CurrentTime()
            //           odometerDialog()
            dateDialog()
        }

        if (mPreferenceManager!!.getString(AppConstants.IS_STARTDAY)
                .equals("1", ignoreCase = true)
        ) {
            binding.lytTrip.visibility = View.GONE
            binding.btnBeginTrip.visibility = View.GONE
        }

        startLocation()

        login(
            MyApplication.LOCUS_CLIENT,
            mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.salesman_code,
            mPreferenceManager!!.getString(AppConstants.LOCUS_PASSWORD).toString()
        )
    }

    private fun getCustomerBlockUnBlock() {

        db!!.deleteBlockCustomer()
        var allCustomer = db!!.getAllCustomer("")
        for (mCustData in allCustomer) {

            var mOverdueCollection =
                db!!.getCustomerOverDueCollection(mCustData.user_id!!.toString())
            if (mOverdueCollection.size > 0) {
                var mApproveDays = db!!.getCustomerApproveDays(mCustData.user_id!!.toString())
                if (mApproveDays.toDouble() > 0) {
                    var mApprveDate = db!!.getCustomerApproveDayDate(mCustData.user_id!!.toString())
                    if (!mApprveDate.isEmpty()) {
                        if (Utils.passEndDate(Utils.getCurrentDate(), mApprveDate)) {
                            db!!.insertCustomerBlobk(mCustData.user_id!!.toString(), "1")
                        }
                    } else {
                        db!!.insertCustomerBlobk(mCustData.user_id!!.toString(), "1")
                    }
                } else {
                    db!!.insertCustomerBlobk(mCustData.user_id!!.toString(), "1")
                }
            }

            var customerLimit = db!!.getCustomerCreditLimit(mCustData.user_id!!.toString())
            var customerOutstanding =
                db!!.getCustomerFinalBalance(mCustData.user_id!!.toString())

            var balance = (customerLimit) - customerOutstanding
            if (balance < 0) {

                var mApproveLimit = db!!.getCustomerApproveLimit(mCustData.user_id!!.toString())
                if (mApproveLimit.toDouble() > 0) {
                    var mApprveDate = db!!.getCustomerApproveDate(mCustData.user_id!!.toString())
                    if (!mApprveDate.isEmpty()) {
                        if (Utils.passEndDate(Utils.getCurrentDate(), mApprveDate)) {
                            db!!.insertCustomerBlobk(mCustData.user_id!!.toString(), "2")
                        } else {
                            var mFinalAPLimit =
                                db!!.getCustomerApproveLimit(mCustData.user_id!!.toString())
                            if (mFinalAPLimit.toDouble() > 0) {
                                var mApprveDate =
                                    db!!.getCustomerApproveDate(mCustData.user_id!!.toString())
                                if (mApprveDate.isEmpty()) {
                                    if (Utils.passEndDate(Utils.getCurrentDate(), mApprveDate)) {
                                        mFinalAPLimit = "0"
                                    }
                                }
                            }

                            var mFInalBalance =
                                (customerLimit + mFinalAPLimit.toDouble()) - customerOutstanding
                            if (mFInalBalance < 0) {
                                db!!.insertCustomerBlobk(mCustData.user_id!!.toString(), "2")
                            }
                        }
                    } else {
                        var mFinalAPLimit =
                            db!!.getCustomerApproveLimit(mCustData.user_id!!.toString())
                        if (mFinalAPLimit.toDouble() > 0) {
                            var mApprveDate =
                                db!!.getCustomerApproveDate(mCustData.user_id!!.toString())
                            if (mApprveDate.isEmpty()) {
                                if (Utils.passEndDate(Utils.getCurrentDate(), mApprveDate)) {
                                    mFinalAPLimit = "0"
                                }
                            }
                        }
                        var mFInalBalance =
                            (customerLimit + mFinalAPLimit.toDouble()) - customerOutstanding
                        if (mFInalBalance < 0) {
                            db!!.insertCustomerBlobk(mCustData.user_id!!.toString(), "2")
                        }
                    }
                } else {
                    db!!.insertCustomerBlobk(mCustData.user_id!!.toString(), "2")
                }

            }
        }


        dismissDialog()
    }

    private fun startLocation() {

        Utils.initialiseLocationVariables(
            this@DashboardActivity,
            object : Utils.LocationUpdate {
                override fun onLocationDetect(isDetect: Boolean, mLocation: Location) {
                    if (isDetect) {
                        AppConstants.Latitude = mLocation.latitude.toString()
                        AppConstants.Longitude = mLocation.longitude.toString()
                    }
                }
            })

        Utils.startLocationUpdates(this@DashboardActivity)
    }

    private fun setStatePageAdapter() {
        pagerAdapter = ViewPagerAdapter(supportFragmentManager)
        pagerAdapter!!.addFragment(SalesSummaryFragment(), "Sales")
        pagerAdapter!!.addFragment(TargetFragment(), "Target")
        pagerAdapter!!.addFragment(TargetVMFragment(), "Target Volume")
        binding.viewPager!!.adapter = pagerAdapter
        binding.dotsIndicator!!.setViewPager(binding.viewPager)
    }

    private fun dateDialog() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//              biginDate = "${dayOfMonth}/${monthOfYear}/${year}"

                val calendar = Calendar.getInstance()
                calendar[year, monthOfYear] = dayOfMonth

                val df = SimpleDateFormat("yyyy-MM-dd")
                biginDate = df.format(calendar.time)

                mPreferenceManager!!.setString(AppConstants.STARTDATE, biginDate)
                val dfs = SimpleDateFormat("hh:mm")
                biginTime = Utils.get24CurrentTime()
                odometerDialog()
            },
            year,
            month,
            day
        )
        dpd.datePicker.minDate = c.timeInMillis;//get the current day
        dpd.show()
    }

    private fun startTrip(mReading: String) {

        showOnlyProgressDialog()

        mPreferenceManager!!.setString(AppConstants.STARTTIME, biginTime)

        val jObj = JsonObject()
        jObj.addProperty("route_id", userInfo!!.data.userInfo.salesmanInfo.routeId)
        jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
        jObj.addProperty("trip_start", biginDate + " " + biginTime)
        jObj.addProperty("trip_start_date", biginDate)
        jObj.addProperty("trip_start_time", biginTime)
        jObj.addProperty("trip_end", biginDate + " " + biginTime)
        jObj.addProperty("trip_end_date", biginDate)
        jObj.addProperty("trip_end_time", biginTime)
        jObj.addProperty("trip_status", "1")
        jObj.addProperty("trip_from", "1")
        jObj.addProperty("van_id", mPreferenceManager!!.getString(AppConstants.VAN_ID))
        jObj.addProperty("start_odometer", mReading)

        ApiClient.getClient(this)
            .postBeginDay(
                "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
                MyApplication.X_DOMAIN, jObj
            )
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {

                    dismissDialog()

                    if (response.isSuccessful) {
                        if (response.body()!!["status"].asBoolean) {
                            var mJobj = response.body()!!["data"].asJsonObject
                            if (mJobj.has("id")) {
                                mPreferenceManager!!.setTripId(
                                    mJobj.get("id").asInt.toString()
                                )
                            } else {
                                mPreferenceManager!!.setTripId("0")
                            }
                            mPreferenceManager!!.setString(AppConstants.IS_STARTDAY, "1")
                            mPreferenceManager!!.setString(
                                AppConstants.DAY_START,
                                "1"
                            )
                            mPreferenceManager!!.setString(Constants.tripDate, biginDate)
                            if (mPreferenceManager!!.getFTokenId()!! == "2" || mPreferenceManager!!.getFTokenId()!! == "3" || mPreferenceManager!!.getFTokenId()!! == "4") {
                                startActivity(
                                    Intent(
                                        me,
                                        DriverCheckListActivity::class.java
                                    ).putExtra("Time", biginDate + " " + biginTime)
                                        .putExtra("Reading", mReading)
                                )
                                finish()
                            } else {
                                startActivity(Intent(me, JourneyPlanActivity::class.java))
                                finish()
                                binding.lytTrip.visibility = View.GONE
                                binding.btnBeginTrip.visibility = View.GONE
                            }

                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Error while begin trip",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    dismissDialog()
                    //Utils.logData(this@DownloadingDataActivity, "Journey Plan Response")
                    Toast.makeText(applicationContext, "Error while begin trip", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }


    override fun onItemsClick(position: Int, tag: String) {

    }


    fun checkPermision() {
        if (!Utils.canGetLocation) {
            Utils.initialiseLocationVariables(
                this@DashboardActivity,
                object : Utils.LocationUpdate {
                    override fun onLocationDetect(isDetect: Boolean, mLocation: Location) {
                        if (isDetect) {
                            Utils.logData(
                                this@DashboardActivity,
                                "Location Fetch:" + AppConstants.Latitude.toString() + " ," +
                                        AppConstants.Longitude
                            )
                        }
                    }
                })
            Utils.startLocationUpdates(this@DashboardActivity)
        }
    }

    private fun odometerDialog() {

        val factory = LayoutInflater.from(this)
        val binding: OdometerDialogBinding =
            DataBindingUtil.inflate(factory, R.layout.odometer_dialog, null, false)

        val deleteDialogView: View = binding.root

        val deleteDialog: AlertDialog = AlertDialog.Builder(this).create()
        deleteDialog.setView(deleteDialogView)
        deleteDialog.setCancelable(false)

        var lastReading = mPreferenceManager!!.getString(AppConstants.LAST_ODOMETER)
        if (lastReading!!.isEmpty()) {
            binding.edtVehicle.text = "0"
            lastReading = "0"
        } else {
            binding.edtVehicle.text = mPreferenceManager!!.getString(AppConstants.LAST_ODOMETER)
        }


        binding.btnConfirm.setOnClickListener {

            var startmeter = binding.edtComment.text.toString()

            if (startmeter.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Please enter odometer reading",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else if (startmeter.toDouble() < lastReading!!.toDouble()) {
                Toast.makeText(
                    applicationContext,
                    "Start odometer is more than last odometer",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                mPreferenceManager!!.setString(AppConstants.START_ODOMETER, startmeter)
                startTrip(startmeter)
            }
        }

        deleteDialog.show()
    }

    private fun login(clientId: String, userId: String, password: String) {

        val sdkReadyCallback = object : LotrSdkReadyCallback {
            override fun onAuthenticated() {
                Toast.makeText(
                    this@DashboardActivity,
                    "Locus Login Successfully",
                    Toast.LENGTH_LONG
                ).show()
                //updateTourSTatus()
                startLocationUpdate()
            }

            override fun onError(error: LotrSdkError) {
                Toast.makeText(this@DashboardActivity, error.message, Toast.LENGTH_LONG).show()
                error.throwable?.printStackTrace()
            }
        }

        // App can use either user's or client's login based on BuildConfig
        LocusLotrSdk.init(
            this@DashboardActivity,
            UserAuthParams(clientId, userId, password),
            true,
            sdkReadyCallback
        )

    }

    private fun startLocationUpdate() {
        // if (isTracking) {

        if (mPreferenceManager!!.getString(AppConstants.LOCUS_TRACKING) == "0") {
            mPreferenceManager!!.setString(AppConstants.LOCUS_TRACKING, "1")
            val requestParams = TrackingRequestParams.Builder()
                .setNotificationTitleText(R.string.app_name) // Customize the notification text
                //.setNotificationChannelId("") // Customize channel Id if required
                //.setNotificationChannelName("") // Customize channel name if required
                .build()
            LocusLotrSdk.startTracking(this, requestParams)
        }
    }

    override fun onLocationError(lotrSdkError: LotrSdkError) {
        if (lotrSdkError.code == LotrSdkError.Code.RESOLVABLE_API_EXCEPTION) {
            try {
                val resolvableApiException = lotrSdkError.throwable as ResolvableApiException
                // Show the dialog by calling startResolutionForResult() and check the result in onActivityResult().
                resolvableApiException.startResolutionForResult(this@DashboardActivity, 1001)
            } catch (sendEx: IntentSender.SendIntentException) {
                // Ignore the error.
            }
            return
        }
    }

    override fun onLocationUpdated(location: LocusLocation) {
        val locationString = location.toIndentedString()
        logger.info(locationString.replace('\n', ' '))
        Log.e("Location updateLocus", locationString)
    }

    override fun onLocationUploaded(location: LocusLocation) {
        TODO("Not yet implemented")
    }
}

