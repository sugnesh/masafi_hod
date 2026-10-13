package com.engear.vansales.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.R
import com.engear.vansales.adapter.CustomerStatusAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.AppConstants.TRANSACTION_TYPES
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityCustomerDetailBinding
import com.engear.vansales.databinding.AlertPopupViewBinding
import com.engear.vansales.databinding.CreditLimitDialogBinding
import com.engear.vansales.firebase.MyFirebaseMessagingService
import com.engear.vansales.interfaces.OnDialog
import com.engear.vansales.merchandiser.CustomerProfileActivity
import com.engear.vansales.model.*
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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class CustomerDetailActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding: ActivityCustomerDetailBinding
    var customerObj: CustomerData.DataCustomer? = null
    var customerCode: String? = null
    var lobCode: String = ""
    var lobInfoId: String = ""
    var transactionList: ArrayList<Transaction> = ArrayList()
    var feedList: ArrayList<Feed> = ArrayList()
    var balance = 0.0
    var creditLimit = 0.0
    private var statusAdapter: ArrayAdapter<String>? = null
    var arrReason: ArrayList<String> = ArrayList()
    var forceCheckout: String = ""
    var noTransectionEnable: String = ""
    var isEnforceFlag: String? = null
    var mProgressDialog: ProgressDialog? = null
    var str_alert_message: String = ""
    var str_approval_types: String = ""
    private var mDialog: Dialog? = null
    var availbalance: Float = 0f
    var credit_limt: String = "0"
    var isOverDue: String = "0"
    var overDueType: String = "1"
    var isDayBlaock: String = "0"
    var isLimitBlaock: String = "0"
    var miCashBalance = 0.00

    var miCashAccountNumber = ""
    var mMobile = ""
    var mEmail = ""
    var mCustomerBalance = 0.00

    companion object {
        var completedTask = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCustomerDetailBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@CustomerDetailActivity,
            "On Customer details Screen "
        )
        init()
    }

    private fun init() {
        setData()
        //setMenu()
        binding.fabSale.setOnClickListener(this)
        binding.fabOrder.setOnClickListener(this)
        binding.fabEditCus!!.setOnClickListener(this)
        binding.fabLimit!!.setOnClickListener(this)

        forceCheckout = mPreferenceManager!!.getString(AppConstants.FORCECHECKOUTENABLE).toString()
        noTransectionEnable =
            mPreferenceManager!!.getString(AppConstants.NOTRANCETIONENABLE).toString()
        /*baseBinding.btnTransaction.setOnClickListener {
            startActivity(Intent(this, TransactionsActivity::class.java))
        }*/

        startLocation()
    }

    @SuppressLint("SetTextI18n")
    private fun setData() {

        customerCode = intent.getStringExtra("customer_code")
        customerObj = db!!.getCustomerDetail(customerCode!!)
        isEnforceFlag = intent.getStringExtra("seq_force")
        Log.e("customer", Gson().toJson(customerObj))

        lobCode = ""

        // getCurrentBalance()

        isDayBlaock = db!!.isCustomerDayBlock(customerObj!!.userDetail.id.toString())
        isLimitBlaock = db!!.isCustomerLimitBlock(customerObj!!.userDetail.id.toString())

        setToolbarTitle(customerObj!!.userDetail.firstname + " " + customerObj!!.userDetail.lastname)
        if (customerObj!!.customer_type_id == "2" || customerObj!!.customer_type_id == "3") {
            binding.txtCreditDays.text = "0"
            binding.txtCreditLimit.text =
                Utils.getCurrencyFormate(this!!, customerObj!!.credit_limit.toFloat())
            binding.txtAvailableBal.text = "0"
        } else {
            binding.txtCreditDays.text = customerObj!!.credit_days
            var mLimit = customerObj!!.credit_limit.toFloat()
            var approveLimit = db!!.getCustomerApproveLimit(customerObj!!.userDetail.id.toString())

            if (approveLimit.toFloat() > 0) {
                var approveOutstad =
                    db!!.getCustomerApproveLimitOutstanding(customerObj!!.userDetail.id.toString())
                var mApproveOutLimit = approveLimit.toFloat() + approveOutstad.toFloat()
                var mFinalLimit = mLimit + approveLimit.toFloat()
                binding.txtCreditLimit.text =
                    Utils.getCurrencyFormate(this!!, mFinalLimit.toFloat())
                creditLimit = mFinalLimit.toDouble()
                var amtDue = 0.0
                if (lobCode.isEmpty()) {
                    amtDue = db!!.getCustomerBalance(customerObj!!.userDetail.id.toString())
                } else {
                    amtDue =
                        db!!.getCustomerLOBBalance(customerObj!!.userDetail.id.toString(), lobCode)
                }
                Log.e("cresitmilit", creditLimit.toString() + "--" + amtDue)
                balance = mApproveOutLimit - amtDue
            } else {
                binding.txtCreditLimit.text =
                    Utils.getCurrencyFormate(this!!, mLimit.toFloat())
                creditLimit = mLimit.toDouble()
                var amtDue = 0.0
                if (lobCode.isEmpty()) {
                    amtDue = db!!.getCustomerBalance(customerObj!!.userDetail.id.toString())
                } else {
                    amtDue =
                        db!!.getCustomerLOBBalance(customerObj!!.userDetail.id.toString(), lobCode)
                }
                Log.e("cresitmilit", creditLimit.toString() + "--" + amtDue)
                balance = mLimit - amtDue
            }

            binding.txtAvailableBal.text = Utils.getCurrencyFormate(this!!, balance.toFloat())

            if (isDayBlaock == "1" && isLimitBlaock == "1") {
                var mOverDue =
                    db!!.getCustomerOverDueBalance(customerObj!!.userDetail.id.toString(), "1")
                binding.txtOverdue!!.text =
                    "Overdue Amount: " + Utils.getCurrencyFormate(this!!, mOverDue.toFloat())
            } else if (isDayBlaock == "1") {
                var mOverDue =
                    db!!.getCustomerOverDueBalance(customerObj!!.userDetail.id.toString(), "1")
                binding.txtOverdue!!.text =
                    "Overdue Amount: " + Utils.getCurrencyFormate(this!!, mOverDue.toFloat())
            } else if (isLimitBlaock == "1") {
                var mOverDue =
                    db!!.getCustomerOverDueBalanceLimitDay(
                        customerObj!!.userDetail.id.toString(),
                        "0"
                    )
                binding.txtOverdue!!.text =
                    "Outstanding Amount: " + Utils.getCurrencyFormate(this!!, mOverDue.toFloat())
            } else {
                var mOverDue = "0"
                binding.txtOverdue!!.text =
                    "Overdue Amount: " + Utils.getCurrencyFormate(this!!, mOverDue.toFloat())
            }


//            if (customerObj!!.isOverDue == "1") {
//                var mOverDue =
//                    db!!.getCustomerOverDueBalance(customerObj!!.userDetail.id.toString(), "1")
//                binding.txtOverdue!!.text =
//                    "Overdue Amount: " + Utils.getCurrencyFormate(this!!, mOverDue.toFloat())
//            } else if (customerObj!!.isOverDue == "2") {
//                var mOverDue =
//                    db!!.getCustomerOverDueBalanceLimitDay(
//                        customerObj!!.userDetail.id.toString(),
//                        "0"
//                    )
//                binding.txtOverdue!!.text =
//                    "Outstanding Amount: " + Utils.getCurrencyFormate(this!!, mOverDue.toFloat())
//            } else {
//                // var mOverDue = db!!.getCustomerOverDueBalance(customerObj!!.userDetail.id.toString(),"0")
//                var mOverDue = "0"
//                binding.txtOverdue!!.text =
//                    "Overdue Amount: " + Utils.getCurrencyFormate(this!!, mOverDue.toFloat())
//            }
        }

        if (isDayBlaock == "1") {
            binding.fabOrder.visibility = View.VISIBLE
        } else {
            binding.fabOrder.visibility = View.GONE
        }

        if (isLimitBlaock == "1") {
            binding.fabLimit!!.visibility = View.VISIBLE
        }

//        if (customerObj!!.isOverDue == "1") {
//            overDueType = "1"
//            binding.fabOrder.visibility = View.VISIBLE
//        } else if (customerObj!!.isOverDue == "2") {
//            overDueType = "2"
//            binding.fabOrder.visibility = View.VISIBLE
//        } else {
//            overDueType = "2"
//            binding.fabOrder.visibility = View.VISIBLE
//        }
        mPreferenceManager!!.setString(
            AppConstants.CUSTOMER_CREDIT,
            "0"
        )

        mPreferenceManager!!.setString(
            AppConstants.CUSTOMER_AVAIL_BALANCE,
            "0.0"
        )

        setTransactionData()
        setUpRecycler()

        if (customerObj!!.channel_id == "1") {
            binding.fabOrder.visibility = View.GONE
            binding.fabLimit!!.visibility = View.GONE
        }

        binding.btnRefresh!!.setOnClickListener {
            getCurrentBalance()
        }

        var mDelivery = db!!.getCustomerDeliveryOrder(customerObj!!.user_id.toString())
        login(
            MyApplication.LOCUS_CLIENT,
            mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.salesman_code,
            mPreferenceManager!!.getString(AppConstants.LOCUS_PASSWORD).toString(), mDelivery
        )

    }

    private fun setUpRecycler() {
        binding.rvOperation.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvOperation.adapter = FeedAdapter(this, feedList!!)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_operation, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_store -> {
                startActivity(
                    Intent(this, CustomerProfileActivity::class.java).apply {
                        this.putExtra("customer_code", customerCode)

                    })
                return true
            }

            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return false
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.fabSale -> {
                Utils.logData(
                    this@CustomerDetailActivity,
                    "On Sale Fab click"
                )

                startActivity(Intent(me, CustomerShopActivity::class.java).apply {
                    this.putExtra("customer_code", customerCode)
                    this.putExtra("iCashBalance", miCashBalance.toString())
                    this.putExtra("accountBalance", mCustomerBalance.toString())
                    this.putExtra("icashAccount", miCashAccountNumber)
                    this.putExtra("mobile", mMobile)
                    this.putExtra("email", mEmail)
                    this.putExtra("lob_code", lobCode)
                })
            }

            R.id.fabOrder -> {
                Utils.logData(
                    this@CustomerDetailActivity,
                    "On Order Fab click"
                )
                binding.fabMenu.close(true)
                orderCommentDialog("1")
            }

            R.id.fabLimit -> {
                Utils.logData(
                    this@CustomerDetailActivity,
                    "On Order Fab click"
                )
                binding.fabMenu.close(true)
                orderCommentDialog("2")
            }

            R.id.fabEditCus -> {
                Utils.logData(
                    this@CustomerDetailActivity,
                    "On Customer Edit click"
                )

                startActivity(Intent(me, VCustomerEditAcivity::class.java).apply {
                    this.putExtra("customer_code", customerCode)
                })
            }

        }
    }

    private fun showDialog() {
        val arrUData = arrayOf<String>("With Reference", "Without Reference")

        val mBuilder = AlertDialog.Builder(this@CustomerDetailActivity)
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

    fun setTransactionData() {
        transactionList = ArrayList()
        feedList = ArrayList()
        transactionList = db!!.getAllTransactionsForCustomer(customerCode!!, lobCode)

        Collections.sort(transactionList, object : Comparator<Transaction> {
            override fun compare(o1: Transaction?, o2: Transaction?): Int {
                val format = SimpleDateFormat("yyyy-MM-dd hh:mm")
                var mm: Date? = null
                var nn: Date? = null
                try {
                    mm = format.parse(o1!!.tr_date_time)
                    nn = format.parse(o2!!.tr_date_time)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                return nn!!.compareTo(mm)
            }
        })

        for (i in 0 until transactionList.size) {
            val feed = Feed()
            if (i == 0) {
                val date = transactionList[i].tr_date_time!!.substring(0, 10)
                val time = Utils.getStringDate(date)
                feed.type = 1
                feed.date = time
                feedList.add(feed)
                addFeedData(transactionList[i])
            } else {
                val date = transactionList[i].tr_date_time!!.substring(0, 10)
                val prdate = transactionList[(i - 1)].tr_date_time!!.substring(0, 10)
                var isSame = false
                isSame = prdate.equals(date, ignoreCase = true)
                if (isSame) {
                    addFeedData(transactionList[i])
                } else {
                    val mDate = transactionList[i].tr_date_time!!.substring(0, 10)
                    val time = Utils.getStringDate(mDate)
                    feed.type = 1
                    feed.date = time
                    feedList.add(feed)
                    addFeedData(transactionList[i])
                }
            }
        }
    }

    private fun addFeedData(mTransaction: Transaction) {
        val feed = Feed()
        feed.type = 2
        feed.desc = mTransaction.tr_date_time
        feed.inv_type = mTransaction.tr_type
        when (mTransaction.tr_type) {
            TRANSACTION_TYPES.TT_CASH_COLLECTION -> {
                feed.name = "Cash collection created"
                feed.inv_no = "No.: " + mTransaction.tr_collection_id
            }

            TRANSACTION_TYPES.TT_CREDIT_COLLECTION -> {
                feed.name = "Credit collection created"
                feed.inv_no = "No.: " + mTransaction.tr_collection_id
            }

            TRANSACTION_TYPES.TT_PAYMENT_BY_CASH -> {
                feed.name = "Payment done by cash."
                feed.inv_no = "Payment id: " + mTransaction.tr_pyament_id
            }

            TRANSACTION_TYPES.TT_PR_COLLECTION -> {
                feed.name = "Payment done by online."
                feed.inv_no = "Payment id: " + mTransaction.tr_pyament_id
            }

            TRANSACTION_TYPES.TT_PAYMENT_BY_CHEQUE -> {
                feed.name = "Payment done by cheque."
                feed.inv_no = "Payment id: " + mTransaction.tr_pyament_id
            }

            TRANSACTION_TYPES.TT_ICCASH_COLLECTION -> {
                feed.name = "Payment done by ICash."
                feed.inv_no = "Payment id: " + mTransaction.tr_pyament_id
            }

            TRANSACTION_TYPES.TT_ONBALANCECASH_COLLECTION -> {
                feed.name = "Payment done by Account Balance."
                feed.inv_no = "Payment id: " + mTransaction.tr_pyament_id
            }

            TRANSACTION_TYPES.TT_ONDISPENSERECASH_COLLECTION -> {
                feed.name = "Payment done by Dispenser Balance."
                feed.inv_no = "Payment id: " + mTransaction.tr_pyament_id
            }

            TRANSACTION_TYPES.TT_PAYMENT_BY_CARD -> {
                feed.name = "Payment done by card."
                feed.inv_no = "Payment id: " + mTransaction.tr_pyament_id
            }

            TRANSACTION_TYPES.TT_STOCK_CAP -> {
                feed.name = "Stock captured."
                feed.inv_no = ""
            }

            TRANSACTION_TYPES.TT_SALES_CREATED -> {
                feed.name = "Sales created."
                feed.inv_no = "No.: " + mTransaction.tr_invoice_id
            }

            TRANSACTION_TYPES.TT_DELIVERY_CANCEL -> {
                feed.name = "Delivery Canceled."
                feed.inv_no = "No.: " + mTransaction.tr_invoice_id
            }

            TRANSACTION_TYPES.TT_OREDER_CREATED -> {
                feed.name = "Order created."
                feed.inv_no = "No.: " + mTransaction.tr_order_id
            }

            TRANSACTION_TYPES.TT_RETURN_CREATED -> {
                feed.name = "Return created."
                feed.inv_no = "No.: " + mTransaction.tr_order_id
            }

            TRANSACTION_TYPES.TT_PALLET_RETURN -> {
                feed.name = "Pallet Return created."
                feed.inv_no = "No.: " + mTransaction.tr_order_id
            }

            TRANSACTION_TYPES.TT_RETURN_REQUEST -> {
                feed.name = "Return created."
                feed.inv_no = "No.: " + mTransaction.tr_pyament_id
            }

            TRANSACTION_TYPES.TT_EXCHANGE_CREATED -> {
                feed.name = "Exchange created."
                feed.inv_no = "No.: " + mTransaction.tr_order_id
            }

            TRANSACTION_TYPES.TT_CUST_CREATED -> {
                feed.name = "Customer created."
                feed.inv_no = "No.: " + mTransaction.tr_order_id
            }

            TRANSACTION_TYPES.TT_COMPLAINT_FEEDBACK -> {
                feed.name = "Complaint created."
                feed.inv_no = "No.: " + mTransaction.tr_order_id
            }

            TRANSACTION_TYPES.TT_COMPAIGN_FEEDBACK -> {
                feed.name = "Campaign information capture."
                feed.inv_no = "No.: " + mTransaction.tr_order_id
            }

            TRANSACTION_TYPES.TT_INVENTORY -> {
                feed.name = "Inventory check."
                feed.inv_no = "No.: " + mTransaction.tr_order_id
            }

            TRANSACTION_TYPES.TT_DISPLAY_TOOL -> {
                feed.name = "Display Tool check."
                feed.inv_no = "No.: " + mTransaction.tr_order_id
            }

            TRANSACTION_TYPES.TT_PLANOGRAM -> {
                feed.name = "Planogram check."
                feed.inv_no = "No.: " + mTransaction.tr_order_id
            }

            TRANSACTION_TYPES.TT_ASSETS -> {
                feed.name = "Assets Tracking."
                feed.inv_no = "No.: " + mTransaction.tr_order_id
            }

            TRANSACTION_TYPES.TT_ASSETS_SURVEY -> {
                feed.name = "Capture assets survey."
                feed.inv_no = "No.: " + mTransaction.tr_order_id
            }

            TRANSACTION_TYPES.TT_CONS_SURVEY -> {
                feed.name = "Capture Consumer survey."
                feed.inv_no = "No.: " + mTransaction.tr_order_id
            }

            TRANSACTION_TYPES.TT_DISTRIBUTION_SURVEY -> {
                feed.name = "Capture Distribution survey."
                feed.inv_no = "No.: " + mTransaction.tr_order_id
            }

            TRANSACTION_TYPES.TT_DISPLAY_TOOL_IMAGE -> {
                feed.name = "Capture Distribution image."
                feed.inv_no = "No.: " + mTransaction.tr_order_id
            }
        }
        feedList.add(feed)
    }

    override fun onPause() {
        super.onPause()
        binding.fabMenu.close(false)
        unregisterReceiver(broadcastReceiver_Detail)
    }

    inner class FeedAdapter(
        val context: Context,
        var mList: ArrayList<Feed>
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun getItemViewType(position: Int): Int {
            return mList[position].type
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RecyclerView.ViewHolder {

            if (viewType == 2) {
                return View1ViewHolder(
                    LayoutInflater.from(context)
                        .inflate(R.layout.row_item_feed_progress, parent, false)
                )
            }
            return View2ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.row_feed_header, parent, false)
            )
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (mList[position].type === 2) {
                (holder as View1ViewHolder).bind(position)
            } else {
                (holder as View2ViewHolder).bind(position)
            }
        }

        override fun getItemCount() = mList.size

        private inner class View1ViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
            var txtTime: TextView = itemView.findViewById(R.id.txtTime)
            var txtInvoiceNo: TextView = itemView.findViewById(R.id.txtInvoiceNo)
            var txtActivity: TextView = itemView.findViewById(R.id.txtActivity)
            var ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)
            fun bind(position: Int) {
                val recyclerViewModel = mList[position]
                txtTime.text = recyclerViewModel.desc
                txtInvoiceNo.text = recyclerViewModel.inv_no
                txtActivity.text = recyclerViewModel.name

                when (recyclerViewModel.inv_type) {
                    TRANSACTION_TYPES.TT_CASH_COLLECTION -> ivIcon.setImageResource(
                        R.drawable.icon_fan_shop_set
                    )

                    TRANSACTION_TYPES.TT_ICCASH_COLLECTION -> ivIcon.setImageResource(
                        R.drawable.icon_fan_shop_set
                    )

                    TRANSACTION_TYPES.TT_PR_COLLECTION -> ivIcon.setImageResource(
                        R.drawable.icon_fan_shop_set
                    )

                    TRANSACTION_TYPES.TT_CREDIT_COLLECTION -> ivIcon.setImageResource(
                        R.drawable.icon_fan_shop_set
                    )

                    TRANSACTION_TYPES.TT_SALES_CREATED -> ivIcon.setImageResource(
                        R.drawable.icon_fan_discount_sel
                    )

                    TRANSACTION_TYPES.TT_OREDER_CREATED -> ivIcon.setImageResource(
                        R.drawable.icon_fan_order_sel
                    )

                    TRANSACTION_TYPES.TT_RETURN_CREATED -> ivIcon.setImageResource(
                        R.drawable.icon_fan_discount_sel
                    )

                    TRANSACTION_TYPES.TT_EXCHANGE_CREATED -> ivIcon.setImageResource(
                        R.drawable.icon_fan_discount_sel
                    )

                    TRANSACTION_TYPES.TT_COMPLAINT_FEEDBACK -> ivIcon.setImageResource(
                        R.drawable.icon_fan_order_sel
                    )

                    TRANSACTION_TYPES.TT_COMPAIGN_FEEDBACK -> ivIcon.setImageResource(
                        R.drawable.icon_fan_order_sel
                    )

                    TRANSACTION_TYPES.TT_INVENTORY -> ivIcon.setImageResource(
                        R.drawable.icon_fan_order_sel
                    )

                    TRANSACTION_TYPES.TT_DISPLAY_TOOL -> ivIcon.setImageResource(
                        R.drawable.icon_fan_order_sel
                    )

                    TRANSACTION_TYPES.TT_PLANOGRAM -> ivIcon.setImageResource(
                        R.drawable.icon_fan_order_sel
                    )

                    TRANSACTION_TYPES.TT_ASSETS -> ivIcon.setImageResource(
                        R.drawable.icon_fan_order_sel
                    )

                    TRANSACTION_TYPES.TT_ASSETS_SURVEY -> ivIcon.setImageResource(
                        R.drawable.icon_fan_order_sel
                    )

                    TRANSACTION_TYPES.TT_CONS_SURVEY -> ivIcon.setImageResource(
                        R.drawable.icon_fan_order_sel
                    )

                    TRANSACTION_TYPES.TT_DISTRIBUTION_SURVEY -> ivIcon.setImageResource(
                        R.drawable.icon_fan_order_sel
                    )

                    TRANSACTION_TYPES.TT_DISPLAY_TOOL_IMAGE -> ivIcon.setImageResource(
                        R.drawable.icon_fan_order_sel
                    )

                    TRANSACTION_TYPES.TT_PALLET_RETURN -> ivIcon.setImageResource(
                        R.drawable.icon_fan_order_sel
                    )

                    else -> {
                    }
                }
            }
        }

        private inner class View2ViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
            var tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
            fun bind(position: Int) {
                val recyclerViewModel = mList[position]
                tvTitle.text = recyclerViewModel.date
            }
        }

    }

    override fun onResume() {
        super.onResume()
        setTransactionData()
        setUpRecycler()

        registerReceiver(
            broadcastReceiver_Detail,
            IntentFilter(MyFirebaseMessagingService.BROADCAST_ACTION)
        )

        if (customerCode != null) {
            customerObj = db!!.getCustomerDetail(customerCode!!)

            if (lobCode.isNotEmpty()) {
                var lobobj = db!!.getCustomerLob(lobCode!!, customerObj!!.user_id.toString())
                lobInfoId = lobobj!!.id.toString()
                customerObj!!.credit_days = lobobj!!.credit_days.toString()
                customerObj!!.credit_limit = lobobj!!.credit_limit
                customerObj!!.customer_type_id = lobobj!!.customer_type_id.toString()
                if (lobobj!!.customer_type_id.toString() == "2") {
                    customerObj!!.credit_days = "0"
                    customerObj!!.credit_limit = "0.0"
                }
            }
        }

        if (customerObj != null) {

            if (customerObj!!.customer_type_id == "2") {
                binding.txtCreditDays.text = "0"
                binding.txtCreditLimit.text =
                    Utils.getCurrencyFormate(this!!, customerObj!!.credit_limit.toFloat())
                binding.txtAvailableBal.text = "0"
            } else {
                binding.txtCreditDays.text = customerObj!!.credit_days
                var mLimit = customerObj!!.credit_limit.toFloat()

                var approveLimit =
                    db!!.getCustomerApproveLimit(customerObj!!.userDetail.id.toString())
                if (approveLimit.toFloat() > 0) {
                    var approveOutstad =
                        db!!.getCustomerApproveLimitOutstanding(customerObj!!.userDetail.id.toString())
                    var mApproveOutLimit = approveLimit.toFloat() + approveOutstad.toFloat()
                    var mFinalLimit = mLimit + approveLimit.toFloat()
                    binding.txtCreditLimit.text =
                        Utils.getCurrencyFormate(this!!, mFinalLimit.toFloat())
                    creditLimit = mFinalLimit.toDouble()
                    var amtDue = 0.0
                    if (lobCode.isEmpty()) {
                        amtDue = db!!.getCustomerBalance(customerObj!!.userDetail.id.toString())
                    } else {
                        amtDue =
                            db!!.getCustomerLOBBalance(
                                customerObj!!.userDetail.id.toString(),
                                lobCode
                            )
                    }
                    Log.e("cresitmilit", creditLimit.toString() + "--" + amtDue)
                    balance = mApproveOutLimit - amtDue
                } else {
                    binding.txtCreditLimit.text =
                        Utils.getCurrencyFormate(this!!, mLimit.toFloat())
                    creditLimit = mLimit.toDouble()
                    var amtDue = 0.0
                    if (lobCode.isEmpty()) {
                        amtDue = db!!.getCustomerBalance(customerObj!!.userDetail.id.toString())
                    } else {
                        amtDue =
                            db!!.getCustomerLOBBalance(
                                customerObj!!.userDetail.id.toString(),
                                lobCode
                            )
                    }
                    Log.e("cresitmilit", creditLimit.toString() + "--" + amtDue)
                    balance = mLimit - amtDue
                }

                // balance = 1000.0
                binding.txtAvailableBal.text = Utils.getCurrencyFormate(this!!, balance.toFloat())


                if (isDayBlaock == "1" && isLimitBlaock == "1") {
                    var mOverDue =
                        db!!.getCustomerOverDueBalance(customerObj!!.userDetail.id.toString(), "1")
                    binding.txtOverdue!!.text =
                        "Overdue Amount: " + Utils.getCurrencyFormate(this!!, mOverDue.toFloat())
                } else if (isDayBlaock == "1") {
                    var mOverDue =
                        db!!.getCustomerOverDueBalance(customerObj!!.userDetail.id.toString(), "1")
                    binding.txtOverdue!!.text =
                        "Overdue Amount: " + Utils.getCurrencyFormate(this!!, mOverDue.toFloat())
                } else if (isLimitBlaock == "1") {
                    var mOverDue =
                        db!!.getCustomerOverDueBalanceLimitDay(
                            customerObj!!.userDetail.id.toString(),
                            "0"
                        )
                    binding.txtOverdue!!.text =
                        "Outstanding Amount: " + Utils.getCurrencyFormate(
                            this!!,
                            mOverDue.toFloat()
                        )
                } else {
                    var mOverDue = "0"
                    binding.txtOverdue!!.text =
                        "Overdue Amount: " + Utils.getCurrencyFormate(this!!, mOverDue.toFloat())
                }

//                if (customerObj!!.isOverDue == "1") {
//                    var mOverDue =
//                        db!!.getCustomerOverDueBalance(customerObj!!.userDetail.id.toString(), "1")
//                    binding.txtOverdue!!.text =
//                        "Overdue Amount: " + Utils.getCurrencyFormate(this!!, mOverDue.toFloat())
//                } else if (customerObj!!.isOverDue == "2") {
//                    var mOverDue =
//                        db!!.getCustomerOverDueBalanceLimitDay(
//                            customerObj!!.userDetail.id.toString(),
//                            "0"
//                        )
//                    binding.txtOverdue!!.text =
//                        "Outstanding Amount: " + Utils.getCurrencyFormate(
//                            this!!,
//                            mOverDue.toFloat()
//                        )
//                } else {
//                    // var mOverDue = db!!.getCustomerOverDueBalance(customerObj!!.userDetail.id.toString(),"0")
//                    var mOverDue = "0"
//                    binding.txtOverdue!!.text =
//                        "Overdue Amount: " + Utils.getCurrencyFormate(this!!, mOverDue.toFloat())
//                }
            }

        }

        getCurrentBalance()

        if (arrReason != null) {
            arrReason.clear()
            arrReason = ArrayList()
            arrReason = db!!.getNonServiceReason()
            statusAdapter = CustomerStatusAdapter(this, arrReason)
        }
    }

    private val broadcastReceiver_Detail = object : BroadcastReceiver() {
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
                                    if (str_approval_types == "No Transaction") {
                                        completedTask = 0
                                        finish()
                                    } else {
                                        var endTime = Utils.get24CurrentTime()
                                        db!!.updateCustomerVisit(
                                            endTime,
                                            customerObj!!.userDetail.id.toString(),
                                            "",
                                            completedTask = completedTask.toString()
                                        )

                                        completedTask = 0
                                        finish()
                                    }

                                }
                            })
                    }
                } else {
                    Utils.displayAlert(
                        this@CustomerDetailActivity,
                        "Your request has been rejected by supervisor!"
                    )
                }
            } else {
                Utils.displayAlert(
                    this@CustomerDetailActivity,
                    "Your request has been rejected by supervisor!"
                )
            }
        }
    }


    override fun onBackPressed() {

        if (mPreferenceManager!!.getString(AppConstants.ROLE) == "4") {
            if (db!!.checkDeliveryPending(customerObj!!.user_id.toString())) {
                if (completedTask > 0) {
                    checkOutDialog()
                } else {
                    var endTime = Utils.get24CurrentTime()
                    db!!.updateCustomerVisit(
                        endTime,
                        customerObj!!.userDetail.id.toString(),
                        "",
                        completedTask = completedTask.toString()
                    )
                    finish()
                }
            } else {
                if (db!!.checkReturnPickPending(customerObj!!.customer_code)) {
                    Utils.displayAlert(
                        this@CustomerDetailActivity,
                        "Kindly pick GRV first. To check out from customer"
                    )
                } else {
                    if (completedTask > 0) {
                        checkOutDialog()
                    } else {
                        var endTime = Utils.get24CurrentTime()
                        db!!.updateCustomerVisit(
                            endTime,
                            customerObj!!.userDetail.id.toString(),
                            "",
                            completedTask = completedTask.toString()
                        )
                        finish()
                    }
                }
            }
        } else {
            if (completedTask > 0) {
                checkOutDialog()
            } else {
                var endTime = Utils.get24CurrentTime()
                db!!.updateCustomerVisit(
                    endTime,
                    customerObj!!.userDetail.id.toString(),
                    "",
                    completedTask = completedTask.toString()
                )
                finish()
            }
        }

    }

    fun getCurrentLocation() {

        var isOpen = false

        showOnlyProgressDialog()

        Utils.initialiseLocationVariables(
            this@CustomerDetailActivity,
            object : Utils.LocationUpdate {
                override fun onLocationDetect(isDetect: Boolean, mLocation: Location) {
                    if (isDetect) {
                        dismissDialog()
                        if (!isOpen) {
                            isOpen = true
                            closeCustomer(
                                mLocation.latitude.toString(),
                                mLocation.longitude.toString()
                            )
                        }
                        Utils.stopLocationUpdates()

                    } else {
                        dismissDialog()
                        if (!isOpen) {
                            isOpen = true
                            closeCustomer("", "")
                        }
                        Utils.stopLocationUpdates()
                    }
                }
            })

        Utils.startLocationUpdates(this@CustomerDetailActivity)
    }

    fun closeCustomer(mLatitude: String, mLongitude: String) {
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
            var endTime = Utils.get24CurrentTime()
            db!!.updateCustomerVisit(
                endTime,
                customerObj!!.userDetail.id.toString(),
                "",
                completedTask = completedTask.toString()
            )

            completedTask = 0
            super.onBackPressed()
        } else {
            AppConstants.Latitude = mLatitude
            AppConstants.Longitude = mLongitude
            gpsDialog(
                customerObj!!.latitude!!.toString(),
                customerObj!!.longitude!!.toString(),
                customerObj!!
            )

        }
    }

    private fun checkOutDialog() {
        var mGeoEnable = db!!.getCustomerGeoChecking(customerObj!!.user_id.toString())
        if (mGeoEnable == "1") {
            getCurrentLocation()
        } else {
            var endTime = Utils.get24CurrentTime()
            db!!.updateCustomerVisit(
                endTime,
                customerObj!!.userDetail.id.toString(),
                "",
                completedTask = completedTask.toString()
            )

            completedTask = 0
            super.onBackPressed()
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
        mCustomer: CustomerData.DataCustomer
    ) {
        try {
            val custLat = mCUstLat.toDoubleOrNull()
            val custLon = mCustLon.toDoubleOrNull()
            val salesLat = AppConstants.Latitude.toDoubleOrNull()
            val salesLon = AppConstants.Longitude.toDoubleOrNull()

            if (custLat == null || custLon == null || salesLat == null || salesLon == null) {
                Toast.makeText(this, "Invalid location coordinates", Toast.LENGTH_SHORT).show()
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

            val distance: Float = salesmanLocation.distanceTo(customerLocation)
            Log.e("Distance", "$distance")

            val dialogBinding = AlertPopupViewBinding.inflate(LayoutInflater.from(this))
            val dialogView = dialogBinding.root

            val alertDialog = AlertDialog.Builder(this).create().apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setView(dialogView)
            }

            val results = FloatArray(1)
            Location.distanceBetween(
                salesLat, salesLon,
                custLat, custLon, results
            )
            val mRadiusDistance = results.getOrNull(0)?.roundToInt() ?: 0
            Log.e("Meter", "$mRadiusDistance")

            dialogBinding.tvCusLatitude.text = "Customer Location : $mCUstLat, $mCustLon"
            dialogBinding.tvMerLatitude.text = "Salesman Location : $salesLat, $salesLon"
            dialogBinding.tvRadius.text = "Distance Radius : $mRadiusDistance m"

            dialogBinding.btnConfirm.setOnClickListener {
                alertDialog.dismiss()
                str_approval_types = "checkout"
                sendUnScheduleApprovalRequest(
                    mCustomer.user_id.toString(),
                    mCUstLat,
                    mCustLon,
                    mRadiusDistance.toString()
                )
            }

            dialogBinding.btnNo.setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error showing dialog", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startLocation() {

        Utils.initialiseLocationVariables(
            this@CustomerDetailActivity,
            object : Utils.LocationUpdate {
                override fun onLocationDetect(isDetect: Boolean, mLocation: Location) {
                    if (isDetect) {
                        AppConstants.Latitude = mLocation.latitude.toString()
                        AppConstants.Longitude = mLocation.longitude.toString()
                    }
                }
            })

        Utils.startLocationUpdates(this@CustomerDetailActivity)
    }


    private fun showStatusDialog() {
        try {
            val dialog = Dialog(this@CustomerDetailActivity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val view: View =
                layoutInflater.inflate(R.layout.dialog_non_service, null)
            val lv =
                view.findViewById<View>(R.id.statusList) as ListView
            val cancel =
                view.findViewById<View>(R.id.btnCancel) as Button
            cancel.setOnClickListener { dialog.dismiss() }

            arrReason = ArrayList()
            arrReason = db!!.getNonServiceReason()
            statusAdapter = CustomerStatusAdapter(this, arrReason)
            lv.adapter = statusAdapter
            lv.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->

                    if (dialog != null) {
                        if (dialog.isShowing()) {
                            dialog.dismiss()
                        }
                    }
                    var endTime = Utils.get24CurrentTime()
                    db!!.updateCustomerVisit(
                        endTime,
                        customerObj!!.userDetail.id.toString(),
                        arrReason.get(position),
                        completedTask = completedTask.toString()
                    )
                    finish()
                }
            dialog.setContentView(view)
            dialog.setCancelable(false)
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getBlockCustomer() {

        val jObj = JsonObject()
        jObj.addProperty(
            "customer_id",
            db!!.getCustomerInfoId(customerObj!!.userDetail.id.toString())
        )
        jObj.addProperty("customer_lob_id", lobInfoId)
        jObj.addProperty("type", "Credit Limit")
        jObj.addProperty("is_block", "1")
        Log.e("Block request", jObj.toString())

        ApiClient.getClient(this).customerBlock(
            "Bearer ${userInfo!!.data.accessToken}", MyApplication.X_DOMAIN,
            jObj
        ).enqueue(object : Callback<BlockData> {
            override fun onResponse(
                call: Call<BlockData>,
                response: Response<BlockData>
            ) {
                Log.e("CustomerBlock Success", "Success")
                if (response.isSuccessful) {
                    //Utils.logData(this@DownloadingDataActivity, "Journey Plan Response")
                    Utils.logData(this@CustomerDetailActivity, "Customer Block Success")
                } else {
                    Utils.logData(this@CustomerDetailActivity, "Customer Block Fail")
                }

            }

            override fun onFailure(call: Call<BlockData>, error: Throwable) {
                Log.e("CustomerBlock Fail", "Fail")
                Utils.logData(this@CustomerDetailActivity, "Fail Customer Block")
            }
        })
    }

    //

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
            sendUnScheduleApprovalRequest(id.toString(), mCUstLat, mCustLon, "")
            //sendApprovalRequest(id.toString(), mCUstLat, mCustLon)
        }

        btnCancel!!.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }


    fun showWaitDialog() {
        mProgressDialog = ProgressDialog(this@CustomerDetailActivity);
        mProgressDialog!!.setMessage("Please wait until supervisor approve..."); // Setting Message
        mProgressDialog!!.setTitle(""); // Setting Title
        mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog!!.show(); // Display Progress Dialog
        mProgressDialog!!.setCancelable(false);
    }


    private fun sendUnScheduleApprovalRequest(
        cust_id: String,
        mCUstLat: String,
        mCustLon: String,
        mRadisu: String
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
        jObj.addProperty("radius", mRadisu)

        Log.e("Van GeoApproval request", jObj.toString())

        ApiClient.getClient(this@CustomerDetailActivity).sendSupervisorGeoApprovalRequest(
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
                        Utils.displayAlert(this@CustomerDetailActivity, "Request Sent fail!!")
                    }
                } else {
                    Utils.displayAlert(this@CustomerDetailActivity, "Request Sent fail!!")
                }

            }

            override fun onFailure(call: Call<GeoApprovalData>, error: Throwable) {
                Log.e("Van GeoApproval Fail", "Fail")
                dismissDialog()
                Utils.displayAlert(this@CustomerDetailActivity, "Request Sent fail!!")
            }
        })
    }

    private fun orderCommentDialog(moverDUe: String) {
        val factory = LayoutInflater.from(this)
        val binding: CreditLimitDialogBinding =
            DataBindingUtil.inflate(factory, R.layout.credit_limit_dialog, null, false)

        val deleteDialogView: View = binding.root

        val deleteDialog: AlertDialog = AlertDialog.Builder(this).create()
        deleteDialog.setView(deleteDialogView)
        deleteDialog.setCancelable(false)

        if (moverDUe == "1") {
            binding.lytInvoiceNo.visibility = View.GONE
        } else {
            binding.lytInvoiceAmt.visibility = View.GONE
        }

        binding.btnConfirm.setOnClickListener {

            var mLimit = binding.edtLimit.text.toString()
            var mDays = binding.edtDays.text.toString()
            var mComment = binding.edtComment.text.toString()

            if (moverDUe == "1") {
                if (mDays.isEmpty()) {
                    Toast.makeText(this, "Please enter credit days", Toast.LENGTH_SHORT).show()
                } else {
                    deleteDialog.dismiss()
                    var mOverDue =
                        Utils.twoDigit(
                            db!!.getCustomerOverDueBalance(
                                customerObj!!.userDetail.id.toString(),
                                "1"
                            ).toFloat()
                        )

                    var mOverDay = db!!.getCustomerOverDueBalanceDay(
                        customerObj!!.userDetail.id.toString(),
                        "1"
                    )

                    sendUnblockRequest(
                        "0",
                        mDays,
                        customerObj!!.user_id.toString(),
                        mOverDue.toString(),
                        mComment,
                        "Account Release", mOverDay.toString()
                    )
                }
            } else {
                if (mLimit.isEmpty()) {
                    Toast.makeText(this, "Please enter credit Limit", Toast.LENGTH_SHORT).show()
                } else {
                    mDays = "0"
                    var mOverDue = Utils.twoDigit(
                        db!!.getCustomerOverDueBalanceLimitDay(
                            customerObj!!.userDetail.id.toString(),
                            "0"
                        ).toFloat()
                    )
                    deleteDialog.dismiss()
                    sendUnblockRequest(
                        mLimit,
                        mDays,
                        customerObj!!.user_id.toString(),
                        mOverDue.toString(),
                        mComment,
                        "Credit Limit", "0"
                    )
                }
            }

        }

        binding.btnCancel.setOnClickListener {
            deleteDialog.dismiss()
        }
        deleteDialog.show()
    }

    private fun sendUnblockRequest(
        mLimit: String,
        mDays: String,
        mCustomerId: String,
        moverDueAmt: String,
        mComment: String,
        mType: String,
        mDaysOverDue: String
    ) {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty("route_id", userInfo!!.data.userInfo.salesmanInfo.routeId)
        jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
        jObj.addProperty(
            "supervisor_id",
            mPreferenceManager!!.getString(AppConstants.SUPERVISOR_ID)
        )
        jObj.addProperty("salesman_lat", AppConstants.Latitude)
        jObj.addProperty("salesman_long", AppConstants.Longitude)
        jObj.addProperty("type", mType)
        jObj.addProperty("overdue_amount", moverDueAmt)
        jObj.addProperty("comment", mComment)
        jObj.addProperty("requested_amount", mLimit)
        jObj.addProperty("no_of_days_requested", mDays)
        jObj.addProperty("customer_id", mCustomerId)
        jObj.addProperty("overdue_day", mDaysOverDue)
        Log.e("Unblock Request", jObj.toString())

        ApiClient.getClient(this).sendUnblockRequest(
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
                            db!!.updateCustomerRequestSend(mCustomerId)
                            db!!.insertOverDueRequest(mCustomerId, mLimit, mDays)
                            Toast.makeText(
                                this@CustomerDetailActivity,
                                "Request sent Successfully!!",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        } else {
                            Utils.displayAlert(this@CustomerDetailActivity, "Request Sent fail!!")
                        }
                    } else {
                        Utils.displayAlert(this@CustomerDetailActivity, "Request Sent fail!!")
                    }

                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    dismissDialog()
                    Utils.displayAlert(this@CustomerDetailActivity, "Request Sent fail!!")
                }
            })
    }

    public fun getCurrentBalance() {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty(
            "customer_id",
            customerObj!!.userDetail.id.toString()
        )

        ApiClient.getClient(this).getICashDetail(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            jObj,
        )

            .enqueue(object : Callback<IcashDetail> {
                override fun onFailure(call: Call<IcashDetail>, t: Throwable) {
                    getCurrentOnAccountBalance()
                }

                override fun onResponse(
                    p0: Call<IcashDetail>,
                    response: Response<IcashDetail>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { apiResponse ->

                            if (apiResponse.data.customer_data.balance != null) {
                                val originalString =
                                    (apiResponse.data.customer_data.balance.toDouble() + apiResponse.data.customer_data.bonus.toDouble())
                                miCashBalance = originalString
                            } else {
                                miCashBalance = 0.00
                            }

                            if (apiResponse.data.customer_data.account_number != null) {
                                miCashAccountNumber =
                                    apiResponse.data.customer_data.account_number!!
                            } else {
                                miCashAccountNumber = ""
                            }

                            if (apiResponse.data.customer_data.mobile != null) {
                                mMobile =
                                    apiResponse.data.customer_data.mobile!!
                            } else {
                                mMobile = ""
                            }

                            if (apiResponse.data.customer_data.email != null) {
                                mEmail =
                                    apiResponse.data.customer_data.email!!
                            } else {
                                mEmail = ""
                            }
                        }
                    }

                    binding.txtICash!!.text = miCashBalance.toString()

                    getCurrentOnAccountBalance()
                }

            })
    }

    public fun getCurrentOnAccountBalance() {

        val jObj = JsonObject()
        jObj.addProperty(
            "customer_id",
            customerObj!!.userDetail.id.toString()
        )

        ApiClient.getClient(this).getOnAccountBalanceDetail(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            jObj,
        )

            .enqueue(object : Callback<IcashDetail> {
                override fun onFailure(call: Call<IcashDetail>, t: Throwable) {
                    dismissDialog()

                }

                override fun onResponse(
                    p0: Call<IcashDetail>,
                    response: Response<IcashDetail>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { apiResponse ->
                            dismissDialog()

                            if (apiResponse.data.customer_data.balance != null) {
                                val originalString =
                                    (apiResponse.data.customer_data.balance.toDouble())
                                mCustomerBalance = originalString
                            } else {
                                mCustomerBalance = 0.00
                            }
                        }
                    } else {
                        dismissDialog()
                    }

                    binding.txtOnBalance!!.text = mCustomerBalance.toString()

                }

            })
    }

    private fun login(clientId: String, userId: String, password: String, mOrder: String) {

        val sdkReadyCallback = object : LotrSdkReadyCallback {
            override fun onAuthenticated() {
                //Toast.makeText(this@JourneyPlanActivity,"Locus Login Successfully", Toast.LENGTH_LONG).show()
                updateTaskSTatus(mOrder)
                //updateTourSTatus()
            }

            override fun onError(error: LotrSdkError) {
                Toast.makeText(this@CustomerDetailActivity, error.message, Toast.LENGTH_LONG).show()
                error.throwable?.printStackTrace()
            }
        }

        // App can use either user's or client's login based on BuildConfig
        LocusLotrSdk.init(
            this@CustomerDetailActivity,
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
            status = VisitStatusRequest.StatusEnum.ARRIVED,
            checklistValues
        )

        asyncUpdatesManager.addVisitStatusUpdate(taskUpdate)
        asyncUpdatesManager.commit()

    }

}