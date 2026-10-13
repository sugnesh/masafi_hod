package com.engear.vansales.fragments

import android.app.ActionBar
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.engear.vansales.R
import com.engear.vansales.activities.*
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.Constants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.common.SharedPreferenceManager
import com.engear.vansales.databinding.FragmentUnloadBinding
import com.engear.vansales.firebase.MyFirebaseMessagingService
import com.engear.vansales.interfaces.OnSearchableDialog
import com.engear.vansales.localDB.DatabaseHandler
import com.engear.vansales.model.EODData
import com.engear.vansales.model.GeoApprovalData
import com.engear.vansales.model.GeoApprovalStatusModel
import com.engear.vansales.model.Invoice
import com.engear.vansales.model.PLoadResponse
import com.engear.vansales.printer.PrintLog
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 * Use the [UnloadFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UnloadFragment : Fragment() {

    var binding: FragmentUnloadBinding? = null
    var mPreferenceManager: SharedPreferenceManager? = null
    var db: DatabaseHandler? = null
    private var mDialog: Dialog? = null
    var mProgressDialog: ProgressDialog? = null
    private var invoiceList: ArrayList<Invoice>? = ArrayList()
    var jcpcustomer: String = ""
    var skippJPList = JsonArray()
    var cancelDeliveryList = JsonArray()
    private var waitDialog: AlertDialog? = null
    private var isGeoApproved = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_unload, container, false
        )
        val view = binding!!.root
        mPreferenceManager = SharedPreferenceManager(activity!!)
        db = DatabaseHandler(activity!!)

        jcpcustomer =
            mPreferenceManager!!.getString(AppConstants.JCPCUSTOMERENABLE).toString()

        setClickEvent()

        enableDisable()

        return view
    }

    public fun enableDisable() {
        if (mPreferenceManager!!.getString(AppConstants.IS_ENDDAY) == "1") {
            binding!!.lytEndInventory.isClickable = true
            binding!!.lytEndInventory.isEnabled = true
            binding!!.lytEndInventory.alpha = 1.0f

            binding!!.lytVariance.isClickable = true
            binding!!.lytVariance.isEnabled = true
            binding!!.lytVariance.alpha = 1.0f
        } else {

            binding!!.lytEndInventory.isClickable = false
            binding!!.lytEndInventory.isEnabled = false
            binding!!.lytEndInventory.alpha = 0.5f

            binding!!.lytVariance.isClickable = false
            binding!!.lytVariance.isEnabled = false
            binding!!.lytVariance.alpha = 0.5f
        }
    }

    private fun setClickEvent() {
        binding!!.lytBadReturn.setOnClickListener {
            startActivity(
                Intent(activity, EndStockActivity::class.java)
                    .putExtra("Type", "Bad Return")
                    .putExtra("mEType", "Expiry")
            )
        }

        binding!!.lytFreshUnload.setOnClickListener {
            if (mPreferenceManager!!.getString(AppConstants.IS_ENDDAY) == "1") {
                startActivity(
                    Intent(activity, EndStockActivity::class.java)
                        .putExtra("Type", "Fresh")
                )
            } else {

                if (mPreferenceManager!!.getString(AppConstants.ROLE) == "3") {
                    if (db!!.isDeliveryCompleteTransaction()) {
                        var mListNo = db!!.getAllPendingDelivery()
                        if (mListNo.isEmpty()) {
                            var mSiteCode = ""
                            invoiceList = db!!.getAllCashInvoiceList()
                            for (mInv in invoiceList!!) {
                                if (db!!.checkCollectionPiadCount(mInv.invoiceNo.toString())) {
                                    if (mSiteCode.isEmpty()) {
                                        //mSiteCode = db!!.getCustomerCode(mInv.customerId!!)
                                        mSiteCode = mInv.customerCode!!
                                    } else {
                                        mSiteCode =
                                            mSiteCode + "," + mInv.customerCode!!
                                    }
                                }
                            }

                            if (mSiteCode.isNotEmpty()) {
                                Utils.displayAlert(
                                    activity!!,
                                    "Please complete all pending cash invoice collection for Below Site Code : $mSiteCode"
                                )
                            } else {
                                getPendingLoad()
                            }
                        } else {
                            Utils.displayAlert(
                                activity!!,
                                "Please complete all pending delivery first to do checkout for Below Site Code : $mListNo"
                            )
                        }

                    } else {
                        var mSiteCode = ""
                        invoiceList = db!!.getAllCashInvoiceList()
                        for (mInv in invoiceList!!) {
                            if (db!!.checkCollectionPiadCount(mInv.invoiceNo.toString())) {
                                if (mSiteCode.isEmpty()) {
                                    //mSiteCode = db!!.getCustomerCode(mInv.customerId!!)
                                    mSiteCode = mInv.customerCode!!
                                } else {
                                    mSiteCode =
                                        mSiteCode + "," + mInv.customerCode!!
                                }
                            }
                        }

                        if (mSiteCode.isNotEmpty()) {
                            Utils.displayAlert(
                                activity!!,
                                "Please complete all pending cash invoice collection for Below Site Code : $mSiteCode"
                            )
                        } else {
                            if (jcpcustomer == "1") {
                                if (db!!.getCancelDelveiryCount() > 0) {
                                    var isAprvoaltaken =
                                        db!!.getIsGeoApproval(
                                            mPreferenceManager!!.getUserId().toString(),
                                            "jcpCall"
                                        )

                                    if (isAprvoaltaken == "1") {
                                        getPendingLoad()
                                    } else {
                                        cancelDeliveryList = db!!.getAllCancelDelveiry()
                                        dialogPrint(activity!!)
                                    }
                                } else {
                                    getPendingLoad()
                                }
                            } else {
                                getPendingLoad()
                            }
                        }
                    }
                } else {
                    getPendingLoad()
                }
            }
        }

        binding!!.lytVan.setOnClickListener {
            startActivity(
                Intent(activity, EndStockActivity::class.java).putExtra(
                    "Type",
                    "Van Return"
                )
            )
        }

        binding!!.lytVariance.setOnClickListener {
            startActivity(
                Intent(activity, EndStockActivity::class.java)
                    .putExtra("Type", "Variance")
            )
        }

        binding!!.lytEndInventory.setOnClickListener {
            if (mPreferenceManager!!.getString(AppConstants.IS_ENDDAY) == "1") {
                startActivity(
                    Intent(activity, EndStockActivity::class.java)
                        .putExtra("Type", "End Inventory")
                )
            }
//            else {
//                startActivity(
//                    Intent(activity, FreshUnloadActivity::class.java)
//                        .putExtra("Type", "End Inventory")
//                )
//            }
        }
    }

    private fun getPendingLoad(
    ) {

        showOnlyProgressDialog()

        var date = mPreferenceManager!!.getString(AppConstants.STARTDATE)
        if (date!!.isEmpty()) {
            date = Utils.getCurrentDate()
        }

        val jObj = JsonObject()
        jObj.addProperty("route_id", mPreferenceManager!!.getString(AppConstants.ROUTEID))
        jObj.addProperty("date", date)

        Log.e("LoadPendingRe", jObj.toString())

        ApiClient.getClient(activity!!).getPendingLoad(
            "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}", MyApplication.X_DOMAIN,
            jObj
        ).enqueue(object : Callback<PLoadResponse> {
            override fun onResponse(
                call: Call<PLoadResponse>,
                response: Response<PLoadResponse>
            ) {
                Log.e("LoadPendingRe request", response.body().toString())
                Utils.dismissDialog(mDialog!!)
                if (response.isSuccessful) {
                    if (response.body()!!.status!!) {
                        checkAllInvoicePosted()
                    } else {
                        Utils.displayAlert(activity!!, "Kindly please confirm pending Load!.")
                    }
                } else {
                    checkAllInvoicePosted()
                }

            }

            override fun onFailure(call: Call<PLoadResponse>, error: Throwable) {
                Log.e("Order Fail", "Fail" + error.message!!.toString())
                Utils.dismissDialog(mDialog!!)
                checkAllInvoicePosted()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        enableDisable()

    }

    companion object {
        fun newInstance(): UnloadFragment {
            return UnloadFragment()
        }

        fun callbackFunction() {
            UnloadFragment().enableDisable()
        }
    }

    fun dialogPrint(
        mContext: Context?,
    ) {
        val alertDialog = Dialog(mContext!!)
        alertDialog.setCancelable(false)
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.setContentView(R.layout.dialog_load_request_permission)

        var txtMsg: TextView? = alertDialog.findViewById<Button>(R.id.txt_msg)
        txtMsg!!.text =
            "You did not visited or completed all customer.Click on Yes you send request to the supervisor for approval."

        var btnConfirm: android.widget.Button? =
            alertDialog.findViewById<Button>(R.id.btnConfirm)

        var btnCancel: android.widget.Button? =
            alertDialog.findViewById<Button>(R.id.btnCancel)

        btnConfirm!!.setOnClickListener {
            alertDialog.dismiss()
            sendApprovalRequest()
        }

        btnCancel!!.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun sendApprovalRequest() {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty("salesman_id", mPreferenceManager!!.getUserInfo()!!.data.userInfo.id)
        jObj.addProperty("salesman_lat", AppConstants.Latitude)
        jObj.addProperty("salesman_long", AppConstants.Longitude)
        jObj.addProperty(
            "supervisor_id",
            mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.supervisor.id
        )
        jObj.addProperty("type", "Closing journey with unvisited JCP customers")
        jObj.add("skip_customers", skippJPList)
        jObj.add("cancel_delivery", cancelDeliveryList)

        Log.e("Van GeoApproval request", jObj.toString())

        ApiClient.getClient(activity!!).sendSupervisorGeoApprovalRequest(
            "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
            MyApplication.X_DOMAIN,
            jObj
        ).enqueue(object : Callback<GeoApprovalData> {
            override fun onResponse(
                call: Call<GeoApprovalData>,
                response: Response<GeoApprovalData>
            ) {
                Log.e("Van GeoApproval Success", "Success")
                Log.e("Van GeoApproval request", response.body().toString())
                Utils.dismissDialog(mDialog!!)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && responseBody.status == true) {
                        // Extract values from response
                        val uuid = responseBody.data.uuid
                        val notificationId = responseBody.data.notification_id

                        // Store values in shared preferences
                        val sharedPreferencesAprStatus = context!!.getSharedPreferences(
                            "approval_request_id",
                            Context.MODE_PRIVATE
                        )
                        val editor = sharedPreferencesAprStatus.edit()
                        editor.putString("uuid", uuid)
                        editor.putString("notification_id", notificationId)
                        editor.apply()

                        db!!.insertGeoRequest(
                            mPreferenceManager!!.getUserId()!!.toString(),
                            "jcpCall"
                        )
                        showWaitDialog()
                    } else {
                        Utils.displayAlert(activity!!, "Request Sent fail!!")
                    }
                } else {
                    Utils.displayAlert(activity!!, "Request Sent fail!!")
                }

            }

            override fun onFailure(call: Call<GeoApprovalData>, error: Throwable) {
                Log.e("Van GeoApproval Fail", "Fail")
                Utils.dismissDialog(mDialog!!)
                Utils.displayAlert(activity!!, "Request Sent fail!!")
            }
        })
    }

    private fun checkAllInvoicePosted() {

        showOnlyProgressDialog()

        var invoiceList = db!!.getAllInvoiceList()

        val jArr = JsonArray()
        for (min in invoiceList!!) {
            jArr.add(min.invoiceNo!!)
        }
        val jObj = JsonObject()
        jObj.add("invoice_numbers", jArr)

        Log.e("Invoice List", jObj.toString())

        ApiClient.getClient(activity!!).checkInvoicePost(
            "Bearer ${BaseActivity.userInfo!!.data.accessToken}", MyApplication.X_DOMAIN,
            jObj
        ).enqueue(object : Callback<EODData> {
            override fun onResponse(
                call: Call<EODData>,
                response: Response<EODData>
            ) {
                Utils.dismissDialog(mDialog!!)
                if (response.isSuccessful) {
                    if (response.body()!!.status!!) {
                        checkAllCollectionPosted()
                    } else {
                        Utils.displayAlert(
                            activity!!,
                            "Below Invoice not found in portal.Kindly contact IT Admin" + "\n" + response.body()!!.data.toString()
                        )
                    }
                } else {
                    checkAllCollectionPosted()
                }

            }

            override fun onFailure(call: Call<EODData>, error: Throwable) {
                Log.e("Receipt Fail", "Fail")
                Utils.dismissDialog(mDialog!!)
                Utils.displayAlert(activity!!, "Request Sent fail!!")
            }
        })
    }

    private fun checkAllCollectionPosted() {

        showOnlyProgressDialog()

        var invoiceList = db!!.getAllFinalDepositeCollection()

        val jArr = JsonArray()
        for (min in invoiceList!!) {
            jArr.add(min.collectionNo!!)
        }
        val jObj = JsonObject()
        jObj.add("collection_numbers", jArr)

        Log.e("Invoice List", jObj.toString())

        ApiClient.getClient(activity!!).checkCollectionPost(
            "Bearer ${BaseActivity.userInfo!!.data.accessToken}", MyApplication.X_DOMAIN,
            jObj
        ).enqueue(object : Callback<EODData> {
            override fun onResponse(
                call: Call<EODData>,
                response: Response<EODData>
            ) {
                Utils.dismissDialog(mDialog!!)
                if (response.isSuccessful) {
                    if (response.body()!!.status!!) {
                        startActivity(
                            Intent(activity, FreshUnloadActivity::class.java)
                                .putExtra("Type", "End Inventory")
                        )
                    } else {
                        Utils.displayAlert(
                            activity!!,
                            "Below Receipt not found in portal.Kindly contact IT Admin" + "\n" + response.body()!!.data.toString()
                        )
                    }
                } else {
                    startActivity(
                        Intent(activity, FreshUnloadActivity::class.java)
                            .putExtra("Type", "End Inventory")
                    )
                }

            }

            override fun onFailure(call: Call<EODData>, error: Throwable) {
                Log.e("Receipt Fail", "Fail")
                Utils.dismissDialog(mDialog!!)
                Utils.displayAlert(activity!!, "Request Sent fail!!")
            }
        })
    }

    fun showOnlyProgressDialog() {
        try {
            if (mDialog != null && mDialog!!.isShowing) {
                return
            }
            mDialog = Dialog(activity!!)
            mDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            mDialog!!.setCancelable(false)
            mDialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            mDialog!!.setContentView(R.layout.custom_only_progress_dialog)
            mDialog!!.window!!.setLayout(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT
            )
            mDialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun showWaitDialog() {
        val alertDialogBuilder = AlertDialog.Builder(activity!!)

        // Set up the progress bar
        val progressBar = ProgressBar(activity!!)
        progressBar.isIndeterminate = true

        // Set up the message
        val message = TextView(activity!!)
        message.text = "Please wait until supervisor approves..."
        message.setPadding(40, 20, 40, 20)
        message.gravity = Gravity.CENTER
        message.textSize = 18f  // Adjust the text size

        val button = Button(activity!!)
        button.text = "Check Approval (20s)"
        button.setPadding(40, 20, 40, 20)  // Add padding to the button
        button.setBackgroundColor(
            ContextCompat.getColor(
                activity!!,
                R.color.colorAccent
            )
        )  // Set button background color to blue
        button.setTextColor(
            ContextCompat.getColor(
                activity!!,
                R.color.white
            )
        )  // Set button text color to white
        button.isEnabled = false  // Disable the button initially

        // Set up the CountDownTimer
        val timer =
            object : CountDownTimer(20000, 1000) {  // 20 seconds timer with 1 second intervals
                override fun onTick(millisUntilFinished: Long) {
                    val secondsRemaining = millisUntilFinished / 1000
                    button.text = "Check Approval Status (${secondsRemaining}s)"
                }

                override fun onFinish() {
                    button.text = "Check Approval Status"
                    button.isEnabled = true  // Enable the button when the timer finishes
                    button.setBackgroundColor(
                        ContextCompat.getColor(
                            activity!!,
                            R.color.colorAccent
                        )
                    )  // Set button background color to blue
                }
            }
        timer.start()

        button.setOnClickListener {
            // Handle button click
            Toast.makeText(activity!!, "Checking approval status...Please wait", Toast.LENGTH_SHORT)
                .show()
            GeoApprovalNotiStatus()
            button.isEnabled = false  // Disable the button again
            button.setBackgroundColor(
                ContextCompat.getColor(
                    activity!!,
                    R.color.colorGrey
                )
            )  // Change button background color to gray
            timer.start()  // Restart the timer
        }

        val layout = LinearLayout(activity!!)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 20, 40, 20)
        layout.addView(progressBar)
        layout.addView(message)
        layout.addView(button)

        // Set the custom layout to the AlertDialog builder
        alertDialogBuilder.setView(layout)

        // Set the dialog properties
        alertDialogBuilder.setCancelable(false)

        // Create and show the dialog
        waitDialog = alertDialogBuilder.create()
        waitDialog?.show()
    }

    private fun GeoApprovalNotiStatus() {
        // Retrieve the notification_id from shared preferences
        val sharedPreferencesAprStatus =
            context!!.getSharedPreferences("approval_request_id", Context.MODE_PRIVATE)
        val notificationId = sharedPreferencesAprStatus.getString("notification_id", null)

        if (notificationId != null) {
            // Create the payload
            val payload = JsonObject().apply {
                addProperty("id", notificationId)
            }

            // Make the API call
            ApiClient.getClient(context!!).getApprovalStatusNoti(
                "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
                MyApplication.X_DOMAIN,
                payload
            ).enqueue(object : Callback<GeoApprovalStatusModel> {
                override fun onResponse(
                    call: Call<GeoApprovalStatusModel>, response: Response<GeoApprovalStatusModel>
                ) {
                    Log.d("GeoApprovalNotiStatus", "onResponse")
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && responseBody.status == true) {
                            val approvalStatus = responseBody.data?.firstOrNull()?.approvalStatus
                            if (approvalStatus == "Approve") {
                                Toast.makeText(
                                    context,
                                    "Approved by id: $notificationId",
                                    Toast.LENGTH_SHORT
                                ).show()
                                waitDialog?.dismiss()
                                if (mProgressDialog != null) {
                                    if (mProgressDialog!!.isShowing) mProgressDialog!!.dismiss()
                                }
                                isGeoApproved = true
                                stopDilogSupervisorRequest()
                                val editor = sharedPreferencesAprStatus.edit()
                                editor.clear()
                                editor.apply()

                            } else if (approvalStatus == "Reject") {
                                waitDialog?.dismiss()
                                isGeoApproved = false
                                stopDilogSupervisorRequest()
                                val editor = sharedPreferencesAprStatus.edit()
                                editor.clear()
                                editor.apply()
                                context?.let {
                                    Utils.displayAlert(
                                        it, "Your request has been rejected by supervisor!"
                                    )
                                }

                            } else {
                                Toast.makeText(
                                    context,
                                    "Notification ID: $notificationId not yet approved. Please wait.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(context, "Request Failed", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Handle API error
                        Log.e("API Error", "API call failed with code: ${response.code()}")
                        Toast.makeText(context, "Request Failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<GeoApprovalStatusModel>, error: Throwable) {
                    Log.e("GeoApprovalNotiStatus", "onFailure", error)
                    // Handle network error
                    Log.e("Network Error", "Failed to make API call: ${error.message}")
                    Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            // Handle the case where notification_id is not found
            Log.e("GeoApprovalNotiStatus", "notification_id not found in shared preferences")
            Toast.makeText(context, "Notification ID not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopDilogSupervisorRequest() {
        waitDialog?.dismiss()
        if (mProgressDialog != null) {

            if (mProgressDialog!!.isShowing) mProgressDialog!!.dismiss()
        }
        if (isGeoApproved) {
            try {
                acceptUnloadDialog()
            } catch (e: Exception) {
                e.toString()
            }
        } else {
            db!!.updateGeoTypeRequest(
                mPreferenceManager!!.getUserId()!!.toString(),
                "jcpCall",
                "2"
            )
            Utils.displayAlert(
                context!!, "Your request has been rejected by supervisor!"
            )
        }

    }


    val broadcastReceiver_unload = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.getStringExtra("status") != null) {
                if (intent.getStringExtra("status") == "Approve") {
                    if (intent.hasExtra("status")) {
                        if (mProgressDialog != null) {
                            if (mProgressDialog!!.isShowing)
                                mProgressDialog!!.dismiss()
                        }
                        try {
                            acceptUnloadDialog()
                        } catch (e: Exception) {
                            e.toString()
                        }

                    } else {
                        Utils.rejectReasonDialog(activity!!)
                    }
                } else {
                    if (intent.hasExtra("reason")) {
                        if (mProgressDialog != null) {
                            if (mProgressDialog!!.isShowing)
                                mProgressDialog!!.dismiss()
                        }
                        Utils.rejectReasonDialog(activity!!)
                    }

                }
            } else {
                db!!.updateGeoTypeRequest(
                    mPreferenceManager!!.getUserId()!!.toString(),
                    "jcpCall",
                    "2"
                )
                Utils.displayAlert(
                    context,
                    "Your request has been rejected by supervisor!"
                )
            }
        }
    }

    private fun acceptUnloadDialog() {
        val alertDialogBuilder = AlertDialog.Builder(activity!!)
        alertDialogBuilder.setTitle("Alert")
            .setMessage("Your request has been Approve by supervisor!")
            .setCancelable(false)
            .setPositiveButton(
                "Ok"
            ) { dialog, which ->
                dialog.dismiss()

                startActivity(
                    Intent(activity, FreshUnloadActivity::class.java)
                        .putExtra("Type", "End Inventory")
                )
            }
        // create alert dialog
        val alertDialog = alertDialogBuilder.create()
        // show it
        alertDialog.show()

    }

    override fun onPause() {
        super.onPause()
        try {
            activity!!.unregisterReceiver(broadcastReceiver_unload)
        } catch (e: java.lang.Exception) {

        }
    }
}