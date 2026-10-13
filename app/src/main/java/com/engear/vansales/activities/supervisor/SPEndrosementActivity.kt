package com.engear.vansales.activities.supervisor

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.CallEndroseAdapter
import com.engear.vansales.adapter.CarryOverEndroseAdapter
import com.engear.vansales.adapter.FOCEndroseAdapter
import com.engear.vansales.adapter.InvoiceEndroseAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.common.SharedPreferenceManager
import com.engear.vansales.databinding.ActivitySPEndrosementBinding
import com.engear.vansales.model.CallEndrose
import com.engear.vansales.model.CarryOverEndrose
import com.engear.vansales.model.FocEndrose
import com.engear.vansales.model.InvoiceEndrose
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SPEndrosementActivity : BaseActivity() {

    lateinit var binding: ActivitySPEndrosementBinding
    private var countPlus = 0
    private var invoicelist = ArrayList<InvoiceEndrose.Data>()
    private var list = ArrayList<CallEndrose.Data>()
    private var focList = ArrayList<FocEndrose.Data>()
    private var covList = ArrayList<CarryOverEndrose.Data.Transactiondetail>()
    private var uuID: String = ""
    private var routeId: String = ""

    companion object {
        var isEndoCompleted = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySPEndrosementBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {
        setToolbarTitle("Session Endrosement")

        uuID = intent.getStringExtra("uuID")!!
        routeId = intent.getStringExtra("route")!!

        binding.stepView.setStepsNumber(4)
        binding.stepView.go(countPlus, true)

        setInvoiceText()

        getInvoiceData()


        binding.btnNext.setOnClickListener {

            countPlus++
            binding.stepView.go(countPlus, true)

            if (countPlus == 3) {
                setCOVText()
                binding.lytCOV.visibility = View.VISIBLE
                binding.lytFOC.visibility = View.GONE
                binding.btnEndrose.visibility = View.VISIBLE
                binding.btnNext.visibility = View.GONE
                if (covList.isEmpty()) {
                    getCOVData()
                }
            } else if (countPlus == 2) {
                setFOCText()
                binding.lytCall.visibility = View.GONE
                binding.lytFOC.visibility = View.VISIBLE
                if (focList.isEmpty()) {
                    getFOCData()
                }
            } else if (countPlus == 1) {
                setCallText()
                binding.lytCall.visibility = View.VISIBLE
                binding.lytInvoice.visibility = View.GONE

                if (list.isEmpty()) {
                    getCallData()
                }
            }

            binding.btnPrevious.visibility = View.VISIBLE
        }

        binding.btnPrevious.setOnClickListener {

            countPlus--
            binding.stepView.go(countPlus, true)

            if (countPlus == 0) {
                setInvoiceText()
                binding.lytCall.visibility = View.GONE
                binding.lytInvoice.visibility = View.VISIBLE
                binding.btnPrevious.visibility = View.GONE
                if (invoicelist.isEmpty()) {
                    getInvoiceData()
                }
            } else {
                binding.btnNext.visibility = View.VISIBLE
                binding.btnEndrose.visibility = View.GONE
            }

            if (countPlus == 1) {
                setCallText()
                binding.lytCall.visibility = View.VISIBLE
                binding.lytFOC.visibility = View.GONE
                if (list.isEmpty()) {
                    getCallData()
                }
            } else if (countPlus == 2) {
                setFOCText()
                binding.lytCOV.visibility = View.GONE
                binding.lytFOC.visibility = View.VISIBLE
                if (focList.isEmpty()) {
                    getFOCData()
                }
            }
        }

        binding.btnEndrose.setOnClickListener {
            val alertDialogBuilder =
                AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Alert")
                .setMessage("Are you sure you want to Endrose?")
                .setCancelable(false)
                .setPositiveButton(
                    "Yes"
                ) { dialog, which ->

                    dialog.dismiss()
                    postEndross()

                }.setNegativeButton(
                    "No"
                ) { dialog, which -> dialog.dismiss() }

            alertDialogBuilder.show()
        }

    }

    private fun getInvoiceData() {

        showOnlyProgressDialog()

        ApiClient.getClient(this)
            .getInvoiceVisitItemEndrose(
                "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
                MyApplication.X_DOMAIN,
                routeId, "invoice"
            )
            .enqueue(object : Callback<InvoiceEndrose> {
                override fun onResponse(
                    call: Call<InvoiceEndrose>,
                    response: Response<InvoiceEndrose>
                ) {
//                    Log.e("foc", response.toString())
//                    Log.e("foc", response.body()!!.string())
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Utils.logData(
                                this@SPEndrosementActivity,
                                "Item Invoice  Response ${response.body().toString()}"
                            )
                            invoicelist = response.body()!!.data as ArrayList<InvoiceEndrose.Data>
                            setRouteAdapter()
                        }
                    }

                    dismissDialog()
                }

                override fun onFailure(call: Call<InvoiceEndrose>, error: Throwable) {
                    // Fail to Post
                    dismissDialog()
                    if (error.message != null) {
                        Utils.logData(
                            this@SPEndrosementActivity,
                            "Item Invoice  Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SPEndrosementActivity,
                            "Item Invoice  Response API Fail"
                        )
                    }
                }
            })
    }

    private fun getCallData() {

        showOnlyProgressDialog()

        ApiClient.getClient(this)
            .getInvoiceVisitItemCall(
                "Bearer ${SharedPreferenceManager(this)!!.getSupervisorUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN,
                routeId, "call"
            )
            .enqueue(object :
                Callback<CallEndrose> {
                override fun onResponse(
                    call: Call<CallEndrose>,
                    response: Response<CallEndrose>
                ) {
                    Log.e("calls", response.toString())
//                    Log.e("foc", response.body()!!.string())
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Utils.logData(
                                this@SPEndrosementActivity,
                                "Item Call  Response ${response.body().toString()}"
                            )
                            list = response.body()!!.data as ArrayList<CallEndrose.Data>
                            setCallAdapter()
                        }
                    }

                    dismissDialog()
                }

                override fun onFailure(call: Call<CallEndrose>, error: Throwable) {
                    // Fail to Post
                    dismissDialog()

                    if (error.message != null) {
                        Utils.logData(
                            this@SPEndrosementActivity,
                            "Item Call  Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SPEndrosementActivity,
                            "Item Call  Response API Fail"
                        )
                    }
                }
            })
    }

    private fun getFOCData() {

        showOnlyProgressDialog()

        ApiClient.getClient(this)
            .getInvoiceVisitItemDetails(
                "Bearer ${SharedPreferenceManager(this)!!.getSupervisorUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN,
                routeId, "foc"
            )
            .enqueue(object :
                Callback<FocEndrose> {
                override fun onResponse(
                    call: Call<FocEndrose>,
                    response: Response<FocEndrose>
                ) {
                    Log.e("foc", response.toString())
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Utils.logData(
                                this@SPEndrosementActivity,
                                "Item foc  Response ${response.body().toString()}"
                            )
                            focList = response.body()!!.data as ArrayList<FocEndrose.Data>
                            setFOCAdapter()
                        }
                    }

                    dismissDialog()

                }

                override fun onFailure(call: Call<FocEndrose>, error: Throwable) {
                    // Fail to Post
                    dismissDialog()
                    if (error.message != null) {
                        Utils.logData(
                            this@SPEndrosementActivity,
                            "Item foc  Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SPEndrosementActivity,
                            "Item foc  Response API Fail"
                        )
                    }
                }
            })
    }

    private fun getCOVData() {

        showOnlyProgressDialog()

        ApiClient.getClient(this)
            .getInvoiceVisitItemCarryOver(
                "Bearer ${SharedPreferenceManager(this)!!.getSupervisorUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN,
                routeId, "cov"
            )
            .enqueue(object :
                Callback<CarryOverEndrose> {
                override fun onResponse(
                    call: Call<CarryOverEndrose>,
                    response: Response<CarryOverEndrose>
                ) {
                    Log.e("foc", response.toString())
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Utils.logData(
                                this@SPEndrosementActivity,
                                "Item COV  Response ${response.body().toString()}"
                            )
                            if (response.body()!!.data.isNotEmpty()) {
                                covList = ArrayList()
                                if (response.body()!!.data.isNotEmpty()) {

                                    for (mCData in response.body()!!.data) {

                                        if (mCData.transactiondetail.isNotEmpty()) {

                                            for (mItemData in mCData.transactiondetail) {
                                                if (mItemData.unload_type == "4") {
                                                    var mTranDeta =
                                                        CarryOverEndrose.Data.Transactiondetail()
                                                    mTranDeta = mItemData
                                                    mTranDeta.code = mCData.code
                                                    covList.add(mTranDeta)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            setCOVAdapter()

                        }
                    }

                    dismissDialog()

                }

                override fun onFailure(call: Call<CarryOverEndrose>, error: Throwable) {
                    // Fail to Post
                    dismissDialog()
                    if (error.message != null) {
                        Utils.logData(
                            this@SPEndrosementActivity,
                            "Item cov  Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SPEndrosementActivity,
                            "Item cov  Response API Fail"
                        )
                    }
                }
            })
    }

    private fun setInvoiceText() {
        binding.tvTitle.text = "Invoices"
        binding.tvcutomercode.text = "Customer Code"
        binding.tvCustomerName.text = "Customer Name"
        binding.tvInvoceNo.text = "Invoice No"
        binding.tvNetSale.text = "Net Sale"
        binding.tvTotalsale.text = "Total Sale"
    }

    private fun setCallText() {
        binding.tvTitle.text = "Call"
        binding.tvcutomercode.text = "Schedule Call"
        binding.tvCustomerName.text = "Total Visit"
        binding.tvInvoceNo.text = "Unschedule Call"
        binding.tvNetSale.text = "Schedule Sale"
        binding.tvTotalsale.text = "Unschedule Sale"
        binding.tvTotalsale.visibility = View.VISIBLE
    }

    private fun setFOCText() {
        binding.tvTitle.text = "FOC"
        binding.tvcutomercode.text = "Invoice No"
        binding.tvCustomerName.text = "Customer Name"
        binding.tvInvoceNo.text = "FOC Qty"
        binding.tvNetSale.text = "FOC UOM"
        binding.tvTotalsale.visibility = View.GONE
    }

    private fun setCOVText() {
        binding.tvTitle.text = "Carry over"
        binding.tvcutomercode.text = "Item Code"
        binding.tvCustomerName.text = "Item Name"
        binding.tvInvoceNo.text = "UOM"
        binding.tvNetSale.text = "QTY"
        binding.tvTotalsale.text = "Carry No"
        binding.tvTotalsale.visibility = View.VISIBLE
    }

    fun setRouteAdapter() {
        var adapterRoute =
            InvoiceEndroseAdapter(invoicelist)
        val layoutManager = LinearLayoutManager(this)
        binding.rvInvoice.layoutManager = layoutManager
        binding.rvInvoice.itemAnimator = DefaultItemAnimator()
        binding.rvInvoice.adapter = adapterRoute

    }

    fun setFOCAdapter() {
        var adapterRoute =
            FOCEndroseAdapter(focList)
        val layoutManager = LinearLayoutManager(this)
        binding.rvFOC.layoutManager = layoutManager
        binding.rvFOC.itemAnimator = DefaultItemAnimator()
        binding.rvFOC.adapter = adapterRoute

    }

    fun setCallAdapter() {
        var adapterRoute =
            CallEndroseAdapter(list)
        val layoutManager = LinearLayoutManager(this)
        binding.rvCall.layoutManager = layoutManager
        binding.rvCall.itemAnimator = DefaultItemAnimator()
        binding.rvCall.adapter = adapterRoute

    }

    fun setCOVAdapter() {
        var adapterRoute =
            CarryOverEndroseAdapter(covList)
        val layoutManager = LinearLayoutManager(this)
        binding.rvCOV.layoutManager = layoutManager
        binding.rvCOV.itemAnimator = DefaultItemAnimator()
        binding.rvCOV.adapter = adapterRoute

    }

    private fun postEndross() {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty("uuid", uuID)
        jObj.addProperty("status", "Approved")

        ApiClient.getClient(this!!)
            .requestEndross(
                "Bearer ${SharedPreferenceManager(this!!)!!.getSupervisorUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN,
                jObj
            )
            .enqueue(object :
                Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    dismissDialog()

                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Toast.makeText(
                                this@SPEndrosementActivity,
                                "Session Endrosement completed!!",
                                Toast.LENGTH_SHORT
                            ).show()
                            Utils.logData(
                                this@SPEndrosementActivity,
                                "Session endrosement  Response ${response.body().toString()}"
                            )
                            isEndoCompleted = true
                            finish()
                        }
                    } else {
                        Log.e("endrosse", response.errorBody()!!.string())
                        Toast.makeText(
                            this@SPEndrosementActivity,
                            "Session Endrosement failed!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, error: Throwable) {
                    // Fail to Post
                    dismissDialog()
                    Toast.makeText(
                        this@SPEndrosementActivity,
                        "Session Endrosement failed!!",
                        Toast.LENGTH_SHORT
                    ).show()
                    if (error.message != null) {
                        Utils.logData(
                            this@SPEndrosementActivity,
                            "Session Endrosement  Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SPEndrosementActivity,
                            "Session Endrosement  Response API Fail"
                        )
                    }
                }
            })
    }
}