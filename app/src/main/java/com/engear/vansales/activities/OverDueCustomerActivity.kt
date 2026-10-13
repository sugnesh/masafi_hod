package com.engear.vansales.activities

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.R
import com.engear.vansales.adapter.CustomerAdapter
import com.engear.vansales.adapter.CustomerOverDueAdapter
import com.engear.vansales.adapter.VisitedCustomerAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityJourneyPlanBinding
import com.engear.vansales.databinding.ActivityOverDueCustomerBinding
import com.engear.vansales.databinding.CreditLimitDialogBinding
import com.engear.vansales.databinding.OrderCommentDialogBinding
import com.engear.vansales.firebase.MyFirebaseMessagingService
import com.engear.vansales.interfaces.OnDialog
import com.engear.vansales.interfaces.OnShopItemClickListener
import com.engear.vansales.model.CustomerData
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OverDueCustomerActivity : BaseActivity(), OnShopItemClickListener {

    lateinit var binding: ActivityOverDueCustomerBinding
    var allCustomerList: ArrayList<CustomerData.DataCustomer> = ArrayList()
    var mAllAdapter: CustomerOverDueAdapter? = null
    var mSelectedPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOverDueCustomerBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )

        registerReceiver(
            broadcastReceiver2,
            IntentFilter(MyFirebaseMessagingService.BROADCAST_ACTIONOVERDUE)
        )

        Utils.logData(
            this@OverDueCustomerActivity,
            "On Journey plan Screen "
        )
        init()
    }

    private fun init() {
        setToolbarTitle("Overdue Customer")

        setNavigationView(mPreferenceManager!!.getFTokenId()!!)

        allCustomerList = ArrayList();
        allCustomerList = db!!.getTotalOverDueCustomer()

        setUpRecycler()
    }

    fun getData() {
        allCustomerList = ArrayList();
        allCustomerList = db!!.getTotalOverDueCustomer()

        setUpRecycler()
    }

    private fun setUpRecycler() {

        binding.rvSequenceList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAllAdapter = CustomerOverDueAdapter(this, allCustomerList, "all")
        binding.rvSequenceList.adapter = mAllAdapter
    }

    override fun onShopItemClick(mPosition: Int, tag: String, type: String) {
        var position = getCustomerPosition(mPosition, type)
        mSelectedPosition = position

        CustomerDetailActivity.completedTask = 0
        startActivity(Intent(this, CustomerDetailActivity::class.java).apply {
            this.putExtra("customer_code", allCustomerList!![mSelectedPosition].customer_code)
            this.putExtra("seq_force", "0")
            this.putExtra("type", "OverDue")
        })

        //orderCommentDialog(mSelectedPosition)
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
        }
        return position
    }

    override fun onResume() {
        super.onResume()

        getData()
    }

    private val broadcastReceiver2 = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            if (intent.getStringExtra("status") != null) {
                var mStatus = ""
                var mApproveLimit = "0"
                var approveDays = "0"
                var customerId = ""
                var comment = ""
                if (intent.getStringExtra("status") == "Approve") {
                    mStatus = "2"
                    if (intent.hasExtra("approve")) {
                        mApproveLimit = intent.getStringExtra("approve")!!
                    }
                    if (intent.hasExtra("nodays")) {
                        approveDays = intent.getStringExtra("nodays")!!
                    }
                    if (intent.hasExtra("customerId")) {
                        customerId = intent.getStringExtra("customerId")!!
                    }

                    if (intent.hasExtra("comment")) {
                        comment = intent.getStringExtra("comment")!!
                    }
                } else {
                    mStatus = "3"
                    if (intent.hasExtra("customerId")) {
                        customerId = intent.getStringExtra("customerId")!!
                    }

                    if (intent.hasExtra("comment")) {
                        comment = intent.getStringExtra("comment")!!
                    }
                }

                //db!!.updateCustomerStatusOverDue(customerId)
                db!!.updateCustomerRequestStatusSend(customerId, mStatus)
                db!!.updateOverDueStatus(customerId, mApproveLimit, approveDays, mStatus, comment)

                getData()

            }
        }
    }
}