package com.engear.vansales.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.engear.vansales.R
import com.engear.vansales.adapter.DeliveryItemAdapter
import com.engear.vansales.adapter.ReturnItemAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityReturnBinding
import com.engear.vansales.databinding.DeliveryReasonDialogBinding
import com.engear.vansales.interfaces.OnConfirmationDialogClick
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.CreditData
import com.engear.vansales.model.CustomerData
import com.engear.vansales.model.Order
import com.engear.vansales.model.Transaction
import com.engear.vansales.model.VisitedList
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReturnActivity : BaseActivity(), OnItemClickListener {
    lateinit var binding: ActivityReturnBinding
    var customerCode: String? = null
    var returnList: ArrayList<Order>? = ArrayList()
    var customerObj: CustomerData.DataCustomer? = null
    var visitId: String? = null
    var activityId: String? = null
    var lobCode: String = ""
    var mAdapter: ReturnItemAdapter? = null

    companion object {
        var isReturnDone = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReturnBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {
        Utils.logData(
            this@ReturnActivity,
            "On return Screen "
        )
        setToolbarTitle("Return")

        customerCode = intent.getStringExtra("customer_code")
        customerObj = db!!.getCustomerDetail(customerCode!!)
        setActivity()
        if (intent.getStringExtra("lob_code") != null) {
            lobCode = intent.getStringExtra("lob_code")!!
        }


        binding.fabGoodReturn.setOnClickListener {
            Utils.logData(
                this@ReturnActivity,
                "On good return button click"
            )
            binding.fabMenu.close(true)
            showDialog("goodReturn")
            //showReturnDialog("goodReturn")
        }


        binding.fabBadReturn.setOnClickListener {
            Utils.logData(
                this@ReturnActivity,
                "On bad return button click"
            )
            binding.fabMenu.close(true)
            showDialog("badReturn")
            //showReturnDialog("badReturn")

        }

        binding.swipeRefreshLayout!!.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            getReturn()
        })

        if (mPreferenceManager!!.getString(AppConstants.ROLE) == "4") {
            binding.btnAdd!!.visibility = View.VISIBLE
            binding.fabMenu.visibility = View.GONE
        } else {
            binding.btnAdd!!.visibility = View.GONE
            binding.fabMenu.visibility = View.VISIBLE
        }

        binding.btnAdd!!.setOnClickListener {
            startActivity(Intent(this, PalletReturnActivity::class.java).apply {
                this.putExtra("customer_code", customerCode)
            })
        }


    }

    private fun showDialog(mType: String) {

//        startActivity(Intent(this, OrderRequestActivity::class.java).apply {
//            this.putExtra("customer_code", customerCode)
//            this.putExtra("type", mType)
//            this.putExtra("lob", lobCode)
//        })

        val arrUData = arrayOf<String>("With Reference")

        val mBuilder = AlertDialog.Builder(this@ReturnActivity)
        mBuilder.setTitle("Select an option")
        mBuilder.setSingleChoiceItems(arrUData, -1) { dialogInterface, i ->
            if (i == 0) {
                startActivity(Intent(this, OrderRequestActivity::class.java).apply {
                    this.putExtra("customer_code", customerCode)
                    this.putExtra("type", mType)
                    this.putExtra("isReference", "1")
                    this.putExtra("lob", lobCode)
                })

            } else {
                startActivity(Intent(this, OrderRequestActivity::class.java).apply {
                    this.putExtra("customer_code", customerCode)
                    this.putExtra("type", mType)
                    this.putExtra("isReference", "0")
                    this.putExtra("lob", lobCode)
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

    private fun showReturnDialog(mType: String) {
        startActivity(Intent(this, OrderRequestActivity::class.java).apply {
            this.putExtra("customer_code", customerCode)
            this.putExtra("type", mType)
            this.putExtra("lob", lobCode)
        })
    }

    private fun setUpRecycler() {
        binding.rvDeliveryList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = ReturnItemAdapter(this, returnList)
        binding.rvDeliveryList.adapter = mAdapter
    }

    override fun onItemsClick(position: Int, tag: String) {
        if (tag == "click") {
            if (returnList!![position].status == "")
                deleteDialog(position)
        } else {
            if (returnList!![position].isDamaged == "1") {
                startActivity(Intent(this, OrderPostActivity::class.java).apply {
                    this.putExtra("order_no", returnList!![position].orderNo)
                    this.putExtra("type", returnList!![position].orderType)
                    this.putExtra("isRequested", "1")
                    this.putExtra("customer_code", customerCode)
                })
            } else {
                startActivity(Intent(this, OrderDetailActivity::class.java).apply {
                    this.putExtra("order_no", returnList!![position].orderNo)
                    this.putExtra("type", "return")
                })
            }
        }

    }

    private fun deleteDialog(position: Int) {
        showConfirmAlertDialog("Are you sure you want to delete this Return?",
            object : OnConfirmationDialogClick {
                override fun onOk() {

                    deliveryCommentDialog(position)
//                    db!!.markForRequestDeleteReturnPost(returnList!![position].orderNo.toString())
//                    returnList!![position].status = "2"
//                    mAdapter!!.notifyDataSetChanged()

                    /* val transaction = Transaction()
                     transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_DELIVERY_CANCEL
                     transaction.tr_date_time = Utils.getCurrentDate() + " " + Utils.getCurrentTime()
                     transaction.tr_customer_num = customerId
                     transaction.tr_customer_name =
                         customerObj!!.userDetail!!.firstname + " " + customerObj!!.userDetail!!.lastname
                     transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
                     transaction.tr_invoice_id = ""
                     transaction.tr_order_id = returnList!![position].deliveryNumber
                     transaction.tr_collection_id = ""
                     transaction.tr_pyament_id = ""
                     transaction.tr_printData = ""
                     transaction.tr_is_posted = "No"
                     db!!.insertTransaction(transaction)*/

                }

                override fun onCancel() {
                }
            })
    }

    private fun getCustomerOrder() {
        returnList = ArrayList()
        returnList = db!!.getCustomerReturn(customerCode!!)

        setUpRecycler()
    }

    override fun onResume() {
        super.onResume()
        getCustomerOrder()
    }

    private fun setActivity() {

        val mVisited = VisitedList()
        mVisited.customerId = customerObj!!.userDetail.id.toString()
        mVisited.customerCode = customerCode
        mVisited.activityName = "return"
        mVisited.activityAction = ""
        mVisited.startTime = Utils.get24CurrentTime()
        mVisited.endTime = ""
        activityId = Utils.getVisitActivityNo(customerObj!!.userDetail.id.toString(), "3")
        mVisited.activityId = activityId
        visitId = db!!.getCustomerVisitId(customerObj!!.userDetail.id.toString())
        mVisited.visitedOffId = visitId

        db!!.insertVisitedCustomerActivity(mVisited)
    }

    override fun onBackPressed() {
        var action = ""
        if (isReturnDone) {
            isReturnDone = false
            action = "Return created"
            CustomerDetailActivity.completedTask++
            var endTime = Utils.get24CurrentTime()
            db!!.updateCustomerVisitActivity(endTime, action, visitId!!, activityId!!)
        } else {
            db!!.deleteVisitActivity(activityId!!)
        }

        super.onBackPressed()
    }

    private fun deliveryCommentDialog(position: Int) {
        val factory = LayoutInflater.from(this)
        val binding: DeliveryReasonDialogBinding =
            DataBindingUtil.inflate(factory, R.layout.delivery_reason_dialog, null, false)

        val deleteDialogView: View = binding.root

        binding.txtName.text = "Enter Reason"

        val deleteDialog: AlertDialog = AlertDialog.Builder(this).create()
        deleteDialog.setView(deleteDialogView)

        binding.btnConfirm.setOnClickListener {

            var orderComment = binding.edtComment.text.toString()

            db!!.markForRequestDeleteReturnPost(returnList!![position].orderNo.toString())
            returnList!![position].status = "2"
            mAdapter!!.notifyDataSetChanged()

            deleteDialog.dismiss()
        }

        deleteDialog.show()
    }

    private fun getReturn() {

        ApiClient.getClient(this).getReturnData(
            "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
            MyApplication.X_DOMAIN,
            mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.userId
        ).enqueue(object : Callback<CreditData> {
            override fun onResponse(
                call: Call<CreditData>,
                response: Response<CreditData>
            ) {
                if (response.isSuccessful) {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                    if (response.body()!!.status) {
                        db!!.insertReturnData(response.body()!!.data)
                    }
                } else {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Blank")
                }
                getCustomerOrder()
                binding.swipeRefreshLayout.isRefreshing = false

            }

            override fun onFailure(call: Call<CreditData>, error: Throwable) {
                //Utils.logData(this@DownloadingDataActivity, "Order Type Failed")
                getCustomerOrder()
                binding.swipeRefreshLayout.isRefreshing = false
            }
        })
    }

}