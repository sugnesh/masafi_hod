package com.engear.vansales.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.SearchManager
import android.bluetooth.BluetoothAdapter
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.MenuItemCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitsecure.dunncpplugin.handlers.DunnPay
import com.digitsecure.dunncpplugin.models.DunnBilling
import com.digitsecure.dunncpplugin.models.DunnPayRequest
import com.digitsecure.dunncpplugin.models.DunnPayResponse
import com.digitsecure.dunncpplugin.models.ErrorResponse
import com.digitsecure.dunncpplugin.result.IPayResult
import com.engear.vansales.CardFailActivity
import com.engear.vansales.R
import com.engear.vansales.activities.CollectionActivity.Companion
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.DeviceListActivity
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityPaymentDetailBinding
import com.engear.vansales.databinding.OrderCommentDialogBinding
import com.engear.vansales.databinding.PaymentLinkDialogBinding
import com.engear.vansales.databinding.RowItemDistributionBinding
import com.engear.vansales.databinding.RowItemPaymentTypeBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.interfaces.OnSearchableDialog
import com.engear.vansales.merchandiser.StoreCheckActivity
import com.engear.vansales.model.BlockData
import com.engear.vansales.model.CardFail
import com.engear.vansales.model.CustomerData
import com.engear.vansales.model.DeliveryData
import com.engear.vansales.model.DistributionItem
import com.engear.vansales.model.IcashDetail
import com.engear.vansales.model.Invoice
import com.engear.vansales.model.ItemData
import com.engear.vansales.model.OutstandingData
import com.engear.vansales.model.PaymentLinkData
import com.engear.vansales.model.Transaction
import com.engear.vansales.model.VisitedList
import com.engear.vansales.printer.BluetoothPrintService
import com.engear.vansales.printer.PrintLog
import com.engear.vansales.printer.WoosimCustomService
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.opencv.android.OpenCVLoader
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference
import java.security.SecureRandom
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class PaymentDetailActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivityPaymentDetailBinding
    private var isRecursive = false
    var customerId: String = ""
    var invAmt: String = ""
    var invDate: String = ""
    var dueDate: String = ""
    var invNo: String = ""
    var collNo: String = ""
    var amtClear: String = ""
    var paymentTearm: String = ""
    var grandTotal: String = ""
    var enterAmt: String = ""
    var type: String = ""
    var chequeDate = ""
    var chequeNumber = "1"
    var bankName = ""
    var collectionType = "3"
    var paymentPriority = "3"
    var paidAmount: String = "0.0"
    var mBonus: String = "0.0"
    var isPartial = "0"
    var isCoupon = "0"
    var mDeliveryNo = ""
    var customerData: CustomerData.DataCustomer? = null
    var visitId: String? = null
    var activityId: String? = null
    var mCollection: OutstandingData.CollectionData? = null
    var isCash = true
    private var jsonArray: JSONArray? = null
    var isClick = false
    var customer_print_enable: String = ""
    var salesman_route_print_enable: String = ""

    private val REQUEST_CONNECT_DEVICE_SECURE = 1
    private val REQUEST_ENABLE_BT = 3
    private var mBluetoothAdapter: BluetoothAdapter? = null


    val MESSAGE_DEVICE_NAME = 1
    val MESSAGE_TOAST = 2
    val MESSAGE_READ = 3

    val DEVICE_NAME = "device_name"
    val TOAST = "toast"
    private val random: Random = SecureRandom()
    private var mSource = ""
    private var prevRefId = ""
    private var cardRefrenceId = ""
    private var merchantId = ""
    private var transactionid = ""
    private var terminalId = ""
    private var lastFour = ""
    private var cardType = ""
    private var mBankCharge = ""
    private var mBankTaxAmt = ""
    private var isDispenser = "0"
    private var enterAmount = 0F
    private var iCashAmount = 0f
    private var onAccountAmount = 0f
    private var onDispenserAmount = 0f

    private val scanCode = 100
    private val cardCode = 160

    private var mMobile = ""
    private var mEmail = ""
    private var iCashAccountNo = ""
    private var mCurentBalance = "0.00"

    open var dunnPayy: DunnPay? = null

    companion object {

        var mActivity: PaymentDetailActivity? = null
        var mPartialArray: ArrayList<HashMap<String, String>> = ArrayList()

        var isOpen = false

        fun callbackFunction(mCopyType: String) {
            mActivity!!.callOtherActivity()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPaymentDetailBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@PaymentDetailActivity,
            "On payment details Screen "
        )

        if (!OpenCVLoader.initDebug()) {
            Log.e(ContentValues.TAG, "OpenCV initialization failed.")
        } else {
            Log.d(ContentValues.TAG, "OpenCV initialization succeeded.")
        }

        init()
        initDunnPay()
    }


    private fun initDunnPay() {
        //val myApplication = application as MyApplication
        dunnPayy = DunnPay.getInstance()
        dunnPayy?.setContext(this)
        dunnPayy?.setDoInitAsync(false)
    }

    private fun init() {
        setToolbarTitle("Payment Detail")


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        mActivity = this
        type = intent.getStringExtra("Type")!!
        customer_print_enable =
            mPreferenceManager!!.getString(AppConstants.CUSTOMERPRINTENABLE).toString()
        salesman_route_print_enable =
            mPreferenceManager!!.getString(AppConstants.PRINTENABLE).toString()


        if (type == "overDue") {
            customerId = intent.getStringExtra("customer")!!
            grandTotal = intent.getStringExtra("amount")!!
            invNo = intent.getStringExtra("invNo")!!

            mCollection = db!!.getCollectionData(invNo)
            customerData = db!!.getCustomerDetailFromCustId(customerId!!)
        } else if (type == "topup") {
            mPartialArray = ArrayList()
            binding.titleDue!!.text = "TopUp\nAmount"
            customerId = intent.getStringExtra("customer")!!
            customerData = db!!.getCustomerDetail(customerId!!)
            var mTopupValue = intent.getStringExtra("topupValue")
            mBonus = intent.getStringExtra("mBonus")!!
            amtClear = "0.0"
            grandTotal = Utils.twoDigit(mTopupValue!!.toFloat()).toString()

            iCashAccountNo = intent.getStringExtra("iCashAccount")!!
            mCurentBalance = intent.getStringExtra("CurrentBalance")!!

            if (intent.getStringExtra("mobile") != null) {
                mMobile = intent.getStringExtra("mobile")!!
            } else {
                mMobile = ""
            }

            if (intent.getStringExtra("email") != null) {
                mEmail = intent.getStringExtra("email")!!
            } else {
                mEmail = ""
            }

            binding.lytSwich.visibility = View.VISIBLE
            binding.lytCheque.visibility = View.GONE
            binding.edtCashAmt.isEnabled = false
            binding.edtCashAmt.isClickable = false
            binding.rbICash!!.visibility = View.GONE
            binding.rbOnBalance!!.visibility = View.GONE
            binding.rbCash!!.visibility = View.GONE
            binding.rbPaymentLink!!.visibility = View.VISIBLE
            if (customerData!!.customer_category_id == "87") {
                binding.rbCash!!.visibility = View.VISIBLE
                binding.rbCash!!.isChecked = true
                collectionType = "1"
            } else {
                binding.lytAuthCode!!.visibility = View.GONE
                collectionType = "3"
            }
        } else {
            mPartialArray = ArrayList()
            customerId = intent.getStringExtra("customer")!!
            var mGRArandTotal = intent.getStringExtra("amount")
            grandTotal = Utils.twoDigit(mGRArandTotal!!.toFloat()).toString()
            invDate = intent.getStringExtra("invDate")!!
            dueDate = intent.getStringExtra("dueDate")!!
            invNo = intent.getStringExtra("invNo")!!
            collNo = intent.getStringExtra("collNum")!!
            amtClear = intent.getStringExtra("amtClear")!!
            invAmt = intent.getStringExtra("invAmt")!!
            paymentTearm = intent.getStringExtra("paymentTerm")!!
            if (intent.getStringExtra("deliveryNo") != null) {
                mDeliveryNo = intent.getStringExtra("deliveryNo")!!
            } else {
                mDeliveryNo = ""
            }
            if (intent.getStringExtra("iCashBalance") != null) {
                iCashAmount = intent.getStringExtra("iCashBalance")!!.toFloat()
            } else {
                iCashAmount = 0F
            }

            if (intent.getStringExtra("accountNumber") != null) {
                iCashAccountNo = intent.getStringExtra("accountNumber")!!
            } else {
                iCashAccountNo = ""
            }

            if (intent.getStringExtra("mobile") != null) {
                mMobile = intent.getStringExtra("mobile")!!
            } else {
                mMobile = ""
            }

            if (intent.getStringExtra("email") != null) {
                mEmail = intent.getStringExtra("email")!!
            } else {
                mEmail = ""
            }

            if (intent.getStringExtra("isDispenser") != null) {
                isDispenser = intent.getStringExtra("isDispenser")!!
            } else {
                isDispenser = "0"
            }

            if (intent.getStringExtra("onBalance") != null) {
                onAccountAmount = intent.getStringExtra("onBalance")!!.toFloat()
            } else {
                onAccountAmount = 0F
            }

            if (intent.getStringExtra("Source") != null) {
                mSource = intent.getStringExtra("Source")!!
            } else {
                mSource = "Other"
            }

            if (intent.getStringExtra("isCoupon") != null) {
                isCoupon = intent.getStringExtra("isCoupon")!!
            }

            binding.lytICash!!.visibility = View.GONE
            binding.lytSwich.visibility = View.VISIBLE
            binding.lytCheque.visibility = View.GONE
            binding.edtCashAmt.isEnabled = false
            binding.edtCashAmt.isClickable = false
            binding.rbCash!!.visibility = View.GONE
            binding.txtICashBalance!!.setText("ICash Balance: $iCashAmount")

            if (mSource == "Instashop" || mSource == "Amazon DF" || mSource == "Lifey" || mSource == "Swan" || mSource == "Now now"
                || mSource == "amazon" || mSource == "Express Delivery"
            ) {
                binding.rbICash!!.visibility = View.GONE
                binding.rbOnBalance!!.visibility = View.GONE
            } else {
                if (isCoupon == "1") {
                    binding.rbICash!!.visibility = View.GONE
                    binding.rbOnBalance!!.visibility = View.GONE
                }
            }
            customerData = db!!.getCustomerDetail(customerId!!)

//            if (isDispenser == "1") {
//                collectionType = "8"
//                binding.rbCash!!.visibility = View.GONE
//                binding.rbCard!!.visibility = View.GONE
//                binding.rbICash!!.visibility = View.GONE
//                binding.rbOnBalance!!.visibility = View.GONE
//                binding.rbOnDispenser!!.visibility = View.VISIBLE
//                binding.rbOnDispenser!!.isChecked = true
//                binding.lytICash!!.visibility = View.VISIBLE
//
//                getCurrentOnDispenseBalance()
//            }

            if (customerData!!.customer_category_id == "87") {
                binding.rbCash!!.visibility = View.VISIBLE
                binding.rbCash!!.isChecked = true
                collectionType = "1"
            } else {
                binding.rbCash!!.visibility = View.GONE
                binding.lytAuthCode!!.visibility = View.GONE
                binding.rbCard!!.isChecked = true
                collectionType = "3"
            }
        }

        customerId = customerData!!.id.toString()
        setActivity()
        setAdapter()

//        if (isDispenser == "1") {
//            getCurrentOnDispenseBalance()
//        }else{
//            getCurrentBalance()
//        }

        //  getCurrentBalance()

        binding.rgPaymentMode!!.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbCash -> {
                    collectionType = "1"
                    isCash = true
                    binding.lytCheque.visibility = View.GONE
                    binding.lytCash.visibility = View.VISIBLE
                    binding.edtCashAmt.setText("" + grandTotal)
                    binding.edtChequeAmt.setText("")
                    binding.lytICash!!.visibility = View.GONE
                    binding.lytAuthCode!!.visibility = View.GONE
                }

                R.id.rbICash -> {
                    collectionType = "5"
                    binding.lytCheque.visibility = View.GONE
                    binding.lytCash.visibility = View.VISIBLE
                    binding.edtCashAmt.setText("" + grandTotal)
                    binding.edtChequeAmt.setText("")
                    binding.lytICash!!.visibility = View.VISIBLE

                    getCurrentBalance()

                    //binding.txtICashBalance!!.text = "iCash Balance: " + iCashAmount
                    binding.lytAuthCode!!.visibility = View.GONE
                }

                R.id.rbOnBalance -> {
                    collectionType = "7"
                    binding.lytCheque.visibility = View.GONE
                    binding.lytCash.visibility = View.VISIBLE
                    binding.edtCashAmt.setText("" + grandTotal)
                    binding.edtChequeAmt.setText("")
                    binding.lytICash!!.visibility = View.VISIBLE

                    getCurrentOnAccountBalance()

                    //binding.txtICashBalance!!.text = "iCash Balance: " + iCashAmount
                    binding.lytAuthCode!!.visibility = View.GONE
                }

                R.id.rbCard -> {
                    collectionType = "3"
                    isCash = false
                    binding.lytCheque.visibility = View.GONE
                    binding.lytCash.visibility = View.VISIBLE
                    binding.edtCashAmt.setText("" + grandTotal)
                    binding.edtChequeAmt.setText("")
                    binding.lytICash!!.visibility = View.GONE
                    binding.lytAuthCode!!.visibility = View.GONE
                }

                R.id.rbPaymentLink -> {
                    collectionType = "6"
                    binding.lytCheque.visibility = View.GONE
                    binding.lytCash.visibility = View.VISIBLE
                    binding.edtCashAmt.setText("" + grandTotal)
                    binding.edtChequeAmt.setText("")
                    binding.lytICash!!.visibility = View.GONE


                    //binding.txtICashBalance!!.text = "iCash Balance: " + iCashAmount
                    binding.lytAuthCode!!.visibility = View.GONE
                }

                else -> {

                }
            }
        }

        var mValue = (grandTotal.toFloat() - amtClear.toFloat())
        paidAmount = Utils.twoDigit(mValue).toString()
        binding.edtCashAmt.setText("" + paidAmount)

        binding.tvCustNo.text = customerData!!.customer_code
        binding.tvCustName.text =
            customerData!!.userDetail.firstname + " " + customerData!!.userDetail.lastname
        binding.txtAmtDue.text = "" + Utils.getCurrencyFormate(this, paidAmount.toFloat())

        binding.edtChequeDate.setOnClickListener {
            dateDialog()
        }

        binding.lytPartial!!.setOnClickListener {

            var mPaidAMount = getRequiredPaidAmount()
            var MRequeured =
                Utils.twoDigit((grandTotal.toDouble() - mPaidAMount.toDouble()).toFloat())
            val intent =
                Intent(this@PaymentDetailActivity, ParitalPaymentActivity::class.java)
            intent.putExtra("mRequiredAmt", MRequeured.toString())
            //intent.putExtra("customerCode", MRequeured.toString())
            intent.putExtra("customer", customerId)
            intent.putExtra("type", type)
            startActivityForResult(intent, scanCode)
        }

        binding.btnRefresh!!.setOnClickListener {
            getCurrentBalance()
        }

        binding.btnPayment.setOnClickListener {
            Utils.logData(
                this@PaymentDetailActivity,
                "On payment button click"
            )
            if (isValid()) {

                if (!isClick) {
                    isClick = true

                    if (type == "topup") {

                        if (mPartialArray.size > 0) {
                            isPartial = "1"
                        }

                        if (isPartial == "1") {

                            if (Utils.twoDigit(paidAmount.toFloat()) < Utils.twoDigit(grandTotal.toFloat())) {
                                val builder = MaterialAlertDialogBuilder(this)
                                builder.setTitle("Alert")
                                builder.setMessage("Kindly pay full Amount?")
                                builder.setCancelable(false)
                                builder.setPositiveButton(
                                    "OK"
                                ) { dialog, _ ->
                                    dialog.cancel()
                                    isClick = false
                                }

                                builder.show()
                            } else {
                                postTopupInvoice()
                            }
                        } else {

                            if (collectionType == "3") {
                                enterAmount = getCashAmount()
//                                cardRefrenceId = binding.edtAuthCode!!.text.toString()
//                                postTopupInvoice()
                                callCardPayment()
                            } else if (collectionType == "6") {
                                enterAmount = getCashAmount()

                                paymentLinkDialog("topup")

                            } else {
                                postTopupInvoice()
                            }

                        }


                    } else {

                        if (mPartialArray.size > 0) {
                            isPartial = "1"
                        }

                        if (isPartial == "1") {

                            if (Utils.twoDigit(paidAmount.toFloat()) < Utils.twoDigit(grandTotal.toFloat())) {
                                val builder = MaterialAlertDialogBuilder(this)
                                builder.setTitle("Alert")
                                builder.setMessage("Kindly pay full Amount?")
                                builder.setCancelable(false)
                                builder.setPositiveButton(
                                    "OK"
                                ) { dialog, _ ->
                                    dialog.cancel()
                                    isClick = false
                                }

                                builder.show()
                            } else {
                                postPartialReceipt()
                            }
                        } else {
                            if (collectionType == "3") {
                                val builder = MaterialAlertDialogBuilder(this)
                                builder.setTitle("Alert")
                                builder.setMessage("Are you sure you want to collect this payment?")
                                builder.setCancelable(false)
                                builder.setPositiveButton(
                                    "Yes"
                                ) { dialog, _ ->
                                    dialog.cancel()
                                    enterAmount = getCashAmount()
                                    paidAmount = enterAmount.toString()

//                                    cardRefrenceId = binding.edtAuthCode!!.text.toString()
//                                    addCollection()

                                    callCardPayment()
                                }
                                builder.setNegativeButton(
                                    "No"
                                ) { dialog, _ ->
                                    dialog.cancel()
                                    isClick = false
                                }
                                builder.show()
                            } else if (collectionType == "6") {

                                enterAmount = enterAmt.toFloat()
                                paidAmount = enterAmt

                                paymentLinkDialog("Invoice")

                            } else if (collectionType == "5") {
                                if (Utils.twoDigit(iCashAmount.toFloat()) >= Utils.twoDigit(enterAmt.toFloat())) {
                                    isOpen = false
                                    paidAmount = enterAmt
                                    db!!.updateCollection(
                                        invNo,
                                        collNo,
                                        enterAmt,
                                        MyApplication.INVOICE_COMPLETE,
                                        "5",
                                        customerId
                                    )

                                    db!!.updateInvoiceType(
                                        invNo,
                                        "iCash"
                                    )

                                    val transaction = Transaction()
                                    transaction.tr_type =
                                        AppConstants.TRANSACTION_TYPES.TT_ICCASH_COLLECTION

                                    SalesActivity.isSaleCreate = true

                                    var data: JSONObject? = null
                                    try {
                                        jsonArray = createPrintData(collNo)
                                        data = JSONObject()
                                        data.put("data", jsonArray as JSONArray?)
                                        Log.e("Print Data", data.toString())
                                    } catch (e: java.lang.Exception) {
                                        e.printStackTrace()
                                    }

                                    transaction.tr_date_time =
                                        Utils.getCurrentDate() + " " + Utils.getCurrentTime()
                                    transaction.tr_customer_num = customerData!!.customer_code
                                    transaction.tr_customer_name =
                                        customerData!!.userDetail!!.firstname + " " + customerData!!.userDetail!!.lastname
                                    transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
                                    transaction.tr_invoice_id = ""
                                    transaction.tr_order_id = ""
                                    transaction.tr_collection_id = collNo
                                    transaction.tr_pyament_id = ""
                                    transaction.tr_is_posted = "No"
                                    transaction.tr_printData = data.toString()
                                    db!!.insertTransaction(transaction)

                                    db!!.updateCustomerTransaction(
                                        customerData!!.userDetail!!.id.toString(),
                                        "collection"
                                    )

                                    redeamICashBalance(collNo, grandTotal, "0")


                                } else {
                                    val builder = MaterialAlertDialogBuilder(this)
                                    builder.setTitle("Alert")
                                    builder.setMessage("iCash balance is low. Kindly topup balance and redeam?")
                                    builder.setCancelable(false)
                                    builder.setPositiveButton(
                                        "OK"
                                    ) { dialog, _ ->
                                        dialog.cancel()
                                        isClick = false
                                    }
                                    builder.show()
                                }

                            } else if (collectionType == "7") {
                                if (Utils.twoDigit(onAccountAmount.toFloat()) >= Utils.twoDigit(
                                        enterAmt.toFloat()
                                    )
                                ) {
                                    isOpen = false
                                    paidAmount = enterAmt
                                    db!!.updateCollection(
                                        invNo,
                                        collNo,
                                        enterAmt,
                                        MyApplication.INVOICE_COMPLETE,
                                        "7",
                                        customerId
                                    )
                                    db!!.updateInvoiceType(
                                        invNo,
                                        "iCash"
                                    )
                                    val transaction = Transaction()
                                    transaction.tr_type =
                                        AppConstants.TRANSACTION_TYPES.TT_ONBALANCECASH_COLLECTION

                                    SalesActivity.isSaleCreate = true

                                    var data: JSONObject? = null
                                    try {
                                        jsonArray = createPrintData(collNo)
                                        data = JSONObject()
                                        data.put("data", jsonArray as JSONArray?)
                                        Log.e("Print Data", data.toString())
                                    } catch (e: java.lang.Exception) {
                                        e.printStackTrace()
                                    }

                                    transaction.tr_date_time =
                                        Utils.getCurrentDate() + " " + Utils.getCurrentTime()
                                    transaction.tr_customer_num = customerData!!.customer_code
                                    transaction.tr_customer_name =
                                        customerData!!.userDetail!!.firstname + " " + customerData!!.userDetail!!.lastname
                                    transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
                                    transaction.tr_invoice_id = ""
                                    transaction.tr_order_id = ""
                                    transaction.tr_collection_id = collNo
                                    transaction.tr_pyament_id = ""
                                    transaction.tr_is_posted = "No"
                                    transaction.tr_printData = data.toString()
                                    db!!.insertTransaction(transaction)

                                    db!!.updateCustomerTransaction(
                                        customerData!!.userDetail!!.id.toString(),
                                        "collection"
                                    )

                                    redeamonAccountCashBalance(collNo, grandTotal, "0")


                                } else {
                                    val builder = MaterialAlertDialogBuilder(this)
                                    builder.setTitle("Alert")
                                    builder.setMessage("Account balance is low?")
                                    builder.setCancelable(false)
                                    builder.setPositiveButton(
                                        "OK"
                                    ) { dialog, _ ->
                                        dialog.cancel()
                                        isClick = false
                                    }
                                    builder.show()
                                }

                            } else if (collectionType == "8") {
                                if (Utils.twoDigit(onDispenserAmount.toFloat()) >= Utils.twoDigit(
                                        enterAmt.toFloat()
                                    )
                                ) {
                                    isOpen = false
                                    paidAmount = enterAmt
                                    db!!.updateCollection(
                                        invNo,
                                        collNo,
                                        enterAmt,
                                        MyApplication.INVOICE_COMPLETE,
                                        "8",
                                        customerId
                                    )
                                    val transaction = Transaction()
                                    transaction.tr_type =
                                        AppConstants.TRANSACTION_TYPES.TT_ONDISPENSERECASH_COLLECTION

                                    SalesActivity.isSaleCreate = true

                                    var data: JSONObject? = null
                                    try {
                                        jsonArray = createPrintData(collNo)
                                        data = JSONObject()
                                        data.put("data", jsonArray as JSONArray?)
                                        Log.e("Print Data", data.toString())
                                    } catch (e: java.lang.Exception) {
                                        e.printStackTrace()
                                    }

                                    transaction.tr_date_time =
                                        Utils.getCurrentDate() + " " + Utils.getCurrentTime()
                                    transaction.tr_customer_num = customerData!!.customer_code
                                    transaction.tr_customer_name =
                                        customerData!!.userDetail!!.firstname + " " + customerData!!.userDetail!!.lastname
                                    transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
                                    transaction.tr_invoice_id = ""
                                    transaction.tr_order_id = ""
                                    transaction.tr_collection_id = collNo
                                    transaction.tr_pyament_id = ""
                                    transaction.tr_is_posted = "No"
                                    transaction.tr_printData = data.toString()
                                    db!!.insertTransaction(transaction)

                                    db!!.updateCustomerTransaction(
                                        customerData!!.userDetail!!.id.toString(),
                                        "collection"
                                    )

                                    redeamonDispenserCashBalance(collNo, grandTotal, "0")


                                } else {
                                    val builder = MaterialAlertDialogBuilder(this)
                                    builder.setTitle("Alert")
                                    builder.setMessage("Account balance is low?")
                                    builder.setCancelable(false)
                                    builder.setPositiveButton(
                                        "OK"
                                    ) { dialog, _ ->
                                        dialog.cancel()
                                        isClick = false
                                    }
                                    builder.show()
                                }

                            } else {
                                isOpen = false

                                paidAmount = enterAmt
                                db!!.updateCollection(
                                    invNo,
                                    collNo,
                                    enterAmt,
                                    MyApplication.INVOICE_COMPLETE,
                                    "1",
                                    customerId
                                )

                                db!!.updateInvoiceType(
                                    invNo,
                                    "Cash"
                                )

                                val transaction = Transaction()
                                transaction.tr_type =
                                    AppConstants.TRANSACTION_TYPES.TT_CASH_COLLECTION

                                SalesActivity.isSaleCreate = true

                                var data: JSONObject? = null
                                try {
                                    jsonArray = createPrintData(collNo)
                                    data = JSONObject()
                                    data.put("data", jsonArray as JSONArray?)
                                    Log.e("Print Data", data.toString())
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                }

                                transaction.tr_date_time =
                                    Utils.getCurrentDate() + " " + Utils.getCurrentTime()
                                transaction.tr_customer_num = customerData!!.customer_code
                                transaction.tr_customer_name =
                                    customerData!!.userDetail!!.firstname + " " + customerData!!.userDetail!!.lastname
                                transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
                                transaction.tr_invoice_id = ""
                                transaction.tr_order_id = ""
                                transaction.tr_collection_id = collNo
                                transaction.tr_pyament_id = ""
                                transaction.tr_is_posted = "No"
                                transaction.tr_printData = data.toString()
                                db!!.insertTransaction(transaction)

                                db!!.updateCustomerTransaction(
                                    customerData!!.userDetail!!.id.toString(),
                                    "collection"
                                )

                                callOtherActivity()

//                    if(salesman_route_print_enable=="0" || customer_print_enable=="0"){
//                        callOtherActivity()
//                    }else{
//                           // Utils.dialogPrint(this, object : OnSearchableDialog {
//
//                                override fun onItemSelected(selection: String?) {
//
//                                    if (selection.equals("yes", ignoreCase = true)) {
//                                        try {
//                                            jsonArray!!.getJSONArray(0).getJSONObject(0)
//                                                .getJSONObject("mainArr")
//                                                .put("mType", "Customer")
//                                        } catch (e: JSONException) {
//                                            e.printStackTrace()
//                                        }
//                                        PrintLog.mPrintService = mPrintService
//                                        callDevice()
//
//                                    } else if (selection.equals(
//                                            "customer",
//                                            ignoreCase = true
//                                        )
//                                    ) {
//                                        try {
//                                            jsonArray!!.getJSONArray(0).getJSONObject(0)
//                                                .getJSONObject("mainArr")
//                                                .put("mType", "Customer")
//                                        } catch (e: JSONException) {
//                                            e.printStackTrace()
//                                        }
//                                        PrintLog.mPrintService = mPrintService
//                                        callDevice()
//                                    } else {
//                                        callOtherActivity()
//                                    }
//                                }
//                            })
                            }
                        }

                    }
                }

            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_transaction, menu)

        return super.onCreateOptionsMenu(menu)
    }


    fun getRequiredPaidAmount(): Double {

        var mAmount = 0.0

        if (mPartialArray.size > 0) {
            for (mData in mPartialArray) {
                mAmount += mData["Amount"]!!.toDouble()
            }
        }

        return mAmount
    }

    fun getFillPaidAmount() {

        var mAmount = 0.0

        if (mPartialArray.size > 0) {
            for (mData in mPartialArray) {
                mAmount += mData["Amount"]!!.toDouble()
            }
        }
        paidAmount = Utils.twoDigit(mAmount.toFloat()).toString()
        binding.edtPaidAmt!!.setText(mAmount.toString())
    }

    fun postTopupInvoice() {

        var mInvNo = ""
        var invoiceNo = ""
        if (mPreferenceManager!!.getFTokenId()!! == "4" || mPreferenceManager!!.getFTokenId()!! == "3" || mPreferenceManager!!.getFTokenId()!! == "6") {
            var routeCode =
                mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode
            mInvNo = Utils.getLastIndexNew(
                mPreferenceManager!!.getString(AppConstants.INVOICE_LAST).toString(),
                "invoice"
            )
            invoiceNo = "HH${routeCode}IN" +
                    Utils.getLastIndexNew(
                        mPreferenceManager!!.getString(AppConstants.INVOICE_LAST).toString(),
                        "invoice"
                    )
        } else {
            mInvNo =
                Utils.getLastIndexNew(
                    mPreferenceManager!!.getString(AppConstants.INVOICE_LAST).toString(),
                    "invoice"
                )
        }

        var mOrder = Invoice()
        mOrder.invoiceNo = invoiceNo
        mOrder.orderType = "1"
        mOrder.invoiceDate = Utils.getCurrentTimeInPattern("yyyy-MM-dd")
        mOrder.invoiceType = "1"
        mOrder.deliveryId = ""
        mOrder.orderId = ""
        mOrder.dueDate = Utils.getCurrentTimeInPattern("yyyy-MM-dd")
        mOrder.customerId = customerData!!.customer_code
        mOrder.gross = grandTotal
        mOrder.discount = "0.0"
        mOrder.net = grandTotal
        mOrder.vat = "0.0"
        mOrder.excAmt = "0.0"
        mOrder.total = grandTotal
        mOrder.comment = ""
        mOrder.payemntId = customerData!!.payment_term_id.toString()
        mOrder.lobId = ""
        mOrder.roundTo = grandTotal
        mOrder.isExchange = "0"
        mOrder.exchangeNo = ""
        mOrder.paidAmount = ""
        mOrder.isStamp = ""
        mOrder.isInvoiceCancel = "0"
        mOrder.isTopup = "1"
        mOrder.mDelvieryCharge = "0"
        mOrder.mRewards = "0"
        mOrder.latitude = ""
        mOrder.longitude = ""
        mOrder.punchTime = Utils.getCUrrentDateWithTime()
        var orderItemsList: ArrayList<ItemData.Data>? = ArrayList()
        db!!.insertInvoice(
            mOrder = mOrder,
            mItemList = orderItemsList!!,
            mLPO = ""
        )

        val transaction = Transaction()
        transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_SALES_CREATED
        transaction.tr_date_time = Utils.getCurrentDate() + " " + Utils.getCurrentTime()
        transaction.tr_customer_num = customerData!!.customer_code
        transaction.tr_customer_name =
            customerData!!.userDetail!!.firstname + " " + customerData!!.userDetail!!.lastname
        transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
        /* transaction.tr_invoice_id = this.invoiceNo*/
        transaction.tr_invoice_id = invoiceNo
        transaction.tr_order_id = ""
        transaction.tr_collection_id = ""
        transaction.tr_pyament_id = ""
        transaction.tr_printData = ""
        transaction.tr_is_posted = "No"

        db!!.insertTransaction(transaction)

        mPreferenceManager!!.setString(AppConstants.INVOICE_LAST, mInvNo)

        if (isPartial == "0") {

            var cashType = "0"
            var mCollNo = ""
            if (customerData!!.customer_type_id == "2" || customerData!!.customer_type_id == "3") {
                cashType = "1"
                mCollNo = Utils.getLastIndexNew(
                    mPreferenceManager!!.getString(AppConstants.COLLECTION_LAST).toString(),
                    "collection"
                )
                if (mPreferenceManager!!.getFTokenId()!! == "3" || mPreferenceManager!!.getFTokenId()!! == "6") {
                    var routeCode =
                        mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode

                    collNo = "HH${routeCode}C" +
                            Utils.getLastIndexNew(
                                mPreferenceManager!!.getString(AppConstants.COLLECTION_LAST)
                                    .toString(),
                                "collection"
                            )
                }

            } else {
                cashType = "0"
            }

            db!!.addCollection(
                invoiceNo,
                collNo,
                customerData!!.userDetail.id.toString(),
                dueDate,
                Utils.getCurrentTimeInPattern("yyyy-MM-dd"),
                grandTotal,
                grandTotal,
                "",
                if (customerData!!.customer_group_id != null) customerData!!.customer_group_id!!.toString()
                else "",
                customerData!!.customer_code,
                customerData!!.userDetail.firstname,
                grandTotal,
                cashType,
                "Invoice",
                "Invoice",
                mBonus,
                "1",
                "",
                cardRefrenceId,
                "",
                collectionType,
                isPartial,
                "",
                ""
            )

            val transaction = Transaction()
            if (collectionType == "3") {
                transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_PAYMENT_BY_CARD
            } else {
                transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_CASH_COLLECTION
            }

            var data: JSONObject? = null
            try {
                jsonArray = createPrintData(collNo)
                data = JSONObject()
                data.put("data", jsonArray as JSONArray?)
                Log.e("Print Data", data.toString())
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            transaction.tr_date_time =
                Utils.getCurrentDate() + " " + Utils.getCurrentTime()
            transaction.tr_customer_num = customerData!!.customer_code
            transaction.tr_customer_name =
                customerData!!.userDetail!!.firstname + " " + customerData!!.userDetail!!.lastname
            transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
            transaction.tr_invoice_id = ""
            transaction.tr_order_id = ""
            transaction.tr_collection_id = collNo
            transaction.tr_pyament_id = ""
            transaction.tr_is_posted = "No"
            transaction.tr_printData = data.toString()
            db!!.insertTransaction(transaction)

            if (mCollNo.isNotEmpty())
                mPreferenceManager!!.setString(AppConstants.COLLECTION_LAST, mCollNo)

            paidTopupBalance(collNo)


        } else {

            var i = 0
            for (mData in mPartialArray) {
                var mColType = ""
                var mBankCharge = ""
                var mTaxAmt = ""
                var mAuthCode = ""
                var mPaidAmt = mData["Amount"]
                if (mData["PaymentType"] == "Cash") {
                    mColType = "1"
                } else {
                    mColType = "3"
                    mAuthCode = mData["AuthCode"]!!
                }

                var cashType = "0"
                var mCollNo = ""
                if (customerData!!.customer_type_id == "2" || customerData!!.customer_type_id == "3") {
                    cashType = "1"
                    //Add Collection
                    mCollNo = Utils.getLastIndexNew(
                        mPreferenceManager!!.getString(AppConstants.COLLECTION_LAST).toString(),
                        "collection"
                    )
                    if (mPreferenceManager!!.getFTokenId()!! == "2") {
                        var routeCode =
                            mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode

                        collNo = "V${routeCode}C" +
                                Utils.getLastIndexNew(
                                    mPreferenceManager!!.getString(AppConstants.COLLECTION_LAST)
                                        .toString(),
                                    "collection"
                                )
                    } else if (mPreferenceManager!!.getFTokenId()!! == "4" || mPreferenceManager!!.getFTokenId()!! == "6") {
                        var routeCode =
                            mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode

                        collNo = "D${routeCode}C" +
                                Utils.getLastIndexNew(
                                    mPreferenceManager!!.getString(AppConstants.COLLECTION_LAST)
                                        .toString(),
                                    "collection"
                                )
                    } else {

                        var routeCode =
                            mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode


                        collNo = "HH${routeCode}C" +
                                Utils.getLastIndexNew(
                                    mPreferenceManager!!.getString(AppConstants.COLLECTION_LAST)
                                        .toString(),
                                    "collection"
                                )
                    }


                } else {
                    cashType = "0"
                }

                var mBononsAmt = "0"
                if (i == 0) {
                    mBononsAmt = mBonus
                }
                db!!.addCollection(
                    invoiceNo,
                    collNo,
                    customerData!!.userDetail.id.toString(),
                    dueDate,
                    Utils.getCurrentTimeInPattern("yyyy-MM-dd"),
                    mPaidAmt!!,
                    mPaidAmt!!,
                    "",
                    if (customerData!!.customer_group_id != null) customerData!!.customer_group_id!!.toString()
                    else "",
                    customerData!!.customer_code,
                    customerData!!.userDetail.firstname,
                    mPaidAmt!!,
                    cashType, "Invoice", "Invoice", mBononsAmt, "1", "", mAuthCode,
                    "",
                    mColType,
                    isPartial,
                    "",
                    ""
                )

                val transaction = Transaction()
                if (mColType == "3") {
                    transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_PAYMENT_BY_CARD
                } else {
                    transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_CASH_COLLECTION
                }

                var data: JSONObject? = null
                try {
                    jsonArray = createPrintData(collNo)
                    data = JSONObject()
                    data.put("data", jsonArray as JSONArray?)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

                transaction.tr_date_time =
                    Utils.getCurrentDate() + " " + Utils.getCurrentTime()
                transaction.tr_customer_num = customerData!!.customer_code
                transaction.tr_customer_name =
                    customerData!!.userDetail!!.firstname + " " + customerData!!.userDetail!!.lastname
                transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
                transaction.tr_invoice_id = ""
                transaction.tr_order_id = ""
                transaction.tr_collection_id = collNo
                transaction.tr_pyament_id = ""
                transaction.tr_is_posted = "No"
                transaction.tr_printData = data.toString()
                db!!.insertTransaction(transaction)

                if (mCollNo.isNotEmpty())
                    mPreferenceManager!!.setString(AppConstants.COLLECTION_LAST, mCollNo)
                i += 1
            }

            paidTopupBalance(collNo)
        }

    }

    fun postPartialReceipt() {
        var i = 0
        var mTotalInAmt = grandTotal.toDouble()
        for (mData in mPartialArray) {
            var mColType = ""
            var mBankCharge = ""
            var mTaxAmt = ""
            cardRefrenceId = ""
            var transaction = Transaction()
            var mPaidAmt = mData["Amount"]
            if (mData["PaymentType"] == "Cash") {
                mColType = "1"
                transaction = Transaction()
                transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_CASH_COLLECTION
            } else if (mData["PaymentType"] == "ICash") {
                mColType = "5"
                transaction = Transaction()
                transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_ICCASH_COLLECTION
            } else if (mData["PaymentType"] == "onBalance") {
                mColType = "7"
                transaction = Transaction()
                transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_ONBALANCECASH_COLLECTION
            } else {
                mColType = "3"
                transaction = Transaction()
                transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_PAYMENT_BY_CARD
                cardRefrenceId = mData["AuthCode"]!!
                transactionid = mData["transactionId"]!!
            }

            var cashType = "0"
            var mCollNo = ""
            if (i != 0) {
                mCollNo = Utils.getLastIndexNew(
                    mPreferenceManager!!.getString(AppConstants.COLLECTION_LAST).toString(),
                    "collection"
                )

                var routeCode =
                    mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode


                collNo = "HH${routeCode}C" +
                        Utils.getLastIndexNew(
                            mPreferenceManager!!.getString(AppConstants.COLLECTION_LAST)
                                .toString(),
                            "collection"
                        )
            }

            if (i == 0) {
                db!!.updatePartialCollection(
                    invNo,
                    collNo,
                    mPaidAmt!!,
                    MyApplication.INVOICE_COMPLETE,
                    mColType,
                    customerId,
                    "1",
                    grandTotal,
                    customerData!!.userDetail.id.toString(),
                    cardRefrenceId,
                    transactionid
                )

            } else {
                db!!.updatePartialCollection(
                    invNo,
                    collNo,
                    mPaidAmt!!,
                    MyApplication.INVOICE_COMPLETE,
                    mColType,
                    customerId,
                    "0",
                    grandTotal,
                    customerData!!.userDetail.id.toString(),
                    cardRefrenceId,
                    transactionid
                )
            }

            var data: JSONObject? = null
            try {

                if (i == 0) {
                    var mRemainamt = mTotalInAmt.toString()
                    mTotalInAmt -= mPaidAmt.toDouble()
                    jsonArray = createPartialPrintData(collNo, mColType, mPaidAmt!!, mRemainamt)
                } else {
                    var mRemainamt = mTotalInAmt.toString()
                    jsonArray = createPartialPrintData(collNo, mColType, mPaidAmt!!, mRemainamt)
                }

                data = JSONObject()
                data.put("data", jsonArray as JSONArray?)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            transaction.tr_date_time =
                Utils.getCurrentDate() + " " + Utils.getCurrentTime()
            transaction.tr_customer_num = customerData!!.customer_code
            transaction.tr_customer_name =
                customerData!!.userDetail!!.firstname + " " + customerData!!.userDetail!!.lastname
            transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
            transaction.tr_invoice_id = ""
            transaction.tr_order_id = ""
            transaction.tr_collection_id = collNo
            transaction.tr_pyament_id = ""
            transaction.tr_is_posted = "No"
            transaction.tr_printData = data.toString()
            db!!.insertTransaction(transaction)

            if (i != 0) {
                mPreferenceManager!!.setString(AppConstants.COLLECTION_LAST, mCollNo)
            }

            db!!.markForCollectionPost(collNo)

            if (mColType == "5") {
                redeamICashBalance(collNo, mPaidAmt!!, "1")
            } else if (mColType == "7") {
                redeamonAccountCashBalance(collNo, mPaidAmt!!, "1")
            }
            i += 1
        }

        callOtherActivity()

        //callPrintDialog()

    }


    fun getCashAmount(): Float {
        var amount = 0F
        if (binding.edtCashAmt.text.isNotEmpty()) {
            amount = binding.edtCashAmt.text.toString().toFloat()
        }
        return amount
    }

    fun addCollection() {
        isOpen = false
        val transaction = Transaction()
        db!!.updateCollectionCard(
            invNo,
            collNo,
            enterAmt,
            MyApplication.INVOICE_COMPLETE,
            "3",
            customerId,
            mBankCharge,
            mBankTaxAmt,
            cardRefrenceId,
            transactionid
        )

        db!!.updateInvoiceType(
            invNo,
            "Card"
        )

        transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_PAYMENT_BY_CARD

        SalesActivity.isSaleCreate = true

        var data: JSONObject? = null
        try {
            jsonArray = createPrintData(collNo)
            data = JSONObject()
            data.put("data", jsonArray as JSONArray?)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        transaction.tr_date_time =
            Utils.getCurrentDate() + " " + Utils.getCurrentTime()
        transaction.tr_customer_num = customerData!!.customer_code
        transaction.tr_customer_name =
            customerData!!.userDetail!!.firstname + " " + customerData!!.userDetail!!.lastname
        transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
        transaction.tr_invoice_id = ""
        transaction.tr_order_id = ""
        transaction.tr_collection_id = collNo
        transaction.tr_pyament_id = ""
        transaction.tr_is_posted = "No"
        transaction.tr_printData = data.toString()
        db!!.insertTransaction(transaction)

        db!!.updateCustomerTransaction(
            customerData!!.userDetail!!.id.toString(),
            "collection"
        )


        callOtherActivity()

//        Utils.dialogPrint(this, object : OnSearchableDialog {
//
//            override fun onItemSelected(selection: String?) {
//
//                if (selection.equals("yes", ignoreCase = true)) {
//                    try {
//                        jsonArray!!.getJSONArray(0).getJSONObject(0)
//                            .getJSONObject("mainArr")
//                            .put("mType", "Customer")
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                    }
//                    PrintLog.mPrintService = mPrintService
//                    callDevice()
//
//                } else if (selection.equals("customer", ignoreCase = true)) {
//                    try {
//                        jsonArray!!.getJSONArray(0).getJSONObject(0)
//                            .getJSONObject("mainArr")
//                            .put("mType", "Customer")
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                    }
//                    PrintLog.mPrintService = mPrintService
//                    callDevice()
//                } else {
//                    callOtherActivity()
//                }
//            }
//        })
    }


    fun callDevice() {

        if (mPrintService!!.getState() !== BluetoothPrintService.STATE_CONNECTED) {
            // Launch the DeviceListActivity to see devices and do scan
            val serverIntent: Intent = Intent(this, DeviceListActivity::class.java)
            startActivityForResult(
                serverIntent,
                REQUEST_CONNECT_DEVICE_SECURE
            )
            isClick = false
        } else {
            isClick = false
            Log.e("Call FUnc", "yes");
            val `object` = PrintLog(
                this@PaymentDetailActivity,
                this@PaymentDetailActivity
            )
            `object`.execute("", jsonArray)
        }

    }

    fun getRandomNum(length: Int): String? {
        val letters = "1234567890"
        val pw = StringBuilder()
        for (i in 0 until length) {
            val index = (random.nextDouble() * letters.length).toInt()
            pw.append(letters[index])
        }
        if (pw.toString().startsWith("0")) {
            pw.setLength(0)
            pw.append(getRandomNum(length))
        }
        prevRefId = pw.toString()
        return pw.toString()
    }

    private fun callOtherActivity() {
        var action = ""
        action = "Payment collected."
        var endTime = Utils.get24CurrentTime()
        db!!.updateCustomerVisitActivity(endTime, action, visitId!!, activityId!!)

        isClick = false
        isOpen = false
        finish()
    }

    private fun setActivity() {

        val mVisited = VisitedList()
        mVisited.customerId = customerData!!.userDetail.id.toString()
        mVisited.customerCode = customerData!!.customer_code
        mVisited.activityName = "payment"
        mVisited.activityAction = ""
        mVisited.startTime = Utils.get24CurrentTime()
        mVisited.endTime = ""
        activityId = Utils.getVisitActivityNo(customerData!!.userDetail.id.toString(), "4")
        mVisited.activityId = activityId
        visitId = db!!.getCustomerVisitId(customerData!!.userDetail.id.toString())
        mVisited.visitedOffId = visitId

        db!!.insertVisitedCustomerActivity(mVisited)
    }

    private fun isValid(): Boolean {
        if (binding.edtCashAmt.text.toString().isNotEmpty()) {
            enterAmt = binding.edtCashAmt.getText().toString()
            if (Utils.twoDigit(enterAmt.toFloat()) > Utils.twoDigit(grandTotal.toFloat())) {
                Toast.makeText(
                    this@PaymentDetailActivity,
                    "Please don't pay more",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            } else {
//                if (collectionType == "3") {
//                    if (binding.edtAuthCode!!.text.toString().isEmpty()) {
//                        Toast.makeText(
//                            this@PaymentDetailActivity,
//                            "Please enter authorization code for MPOS",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        return false
//                    }
//                }
            }
        } else {


            Toast.makeText(getApplicationContext(), "Please enter amount", Toast.LENGTH_SHORT)
                .show()
            return false
        }
        return true
    }

    private fun dateDialog() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                binding.edtChequeDate.text = "${dayOfMonth}/${monthOfYear}/${year}"
            },
            year,
            month,
            day
        )

        dpd.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            android.R.id.home -> {
                onBackPressed()
                return true
            }

            R.id.action_survey -> {

                val intent =
                    Intent(this@PaymentDetailActivity, CardFailActivity::class.java)
                intent.putExtra("customer_code", customerData!!.customer_code)
                startActivityForResult(intent, cardCode)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (type == "overDue") {
            finish()
        } else if (type == "topup") {
            finish()
        } else {
            Toast.makeText(
                this@PaymentDetailActivity,
                "Please complete the transaction",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /*****************************************************************************************
     * @ Creating data for print and saving it in DB for future printing
     */
    fun createPrintData(collectionNumber: String?): JSONArray? {
        val jArr = JSONArray()
        try {
            val jInter = JSONArray()
            val jDict = JSONObject()
            jDict.put(AppConstants.REQUEST, AppConstants.COLLECTION)
            val mainArr = JSONObject()
            var routeCode =
                mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode
            var vanName = routeCode.replace("Zone", "Van sales")
            mainArr.put("ROUTE", vanName)
            mainArr.put("DOC DATE", Utils.formatDate(Date(), "dd-MM-yyyy"))
            mainArr.put("TIME", Utils.formatTime(Date(), "hh:mm"))
            mainArr.put("SALESMAN", mPreferenceManager!!.getString(AppConstants.DRIVERNAME))
            mainArr.put("CONTACTNO", "-")
            mainArr.put("DOCUMENT NO", collectionNumber) //Load Summary No
            mainArr.put("TRIP START DATE", mPreferenceManager!!.getTripDate())
            mainArr.put("supervisorname", "-")
            mainArr.put("orderNo", collectionNumber) //Invoice No
            mainArr.put("Type", "Single") //Invoice No
            mainArr.put("Comment", "") //Invoice No
            mainArr.put("OnAccount", "No")
            if (collectionType == "2") {
                mainArr.put("collectionType", "Cheque") //Invoice No
                mainArr.put("chequeNo", chequeNumber) //Invoice No
                mainArr.put("chequeDate", chequeDate) //Invoice No
                mainArr.put("Bank", bankName) //Invoice No
                mainArr.put("cardRefrenceId", "") //Invoice No
            } else if (collectionType == "3") {
                mainArr.put("collectionType", "Card") //Invoice No
                mainArr.put("cardRefrenceId", cardRefrenceId) //Invoice No
//                mainArr.put("merchantId", merchantId) //Invoice No
//                mainArr.put("transactionId", transactionid) //Invoice No
//                mainArr.put("terminalId", terminalId) //Invoice No
//                mainArr.put("lastFour", lastFour) //Invoice No
//                mainArr.put("CTYPE", cardType) //Invoice No
            } else if (collectionType == "5") {
                mainArr.put("collectionType", "iCash") //Invoice No
                mainArr.put("chequeNo", "") //Invoice No
                mainArr.put("chequeDate", "") //Invoice No
                mainArr.put("Bank", "") //Invoice No
                mainArr.put("cardRefrenceId", "") //Invoice No
            } else if (collectionType == "7") {
                mainArr.put("collectionType", "AccountBalance") //Invoice No
                mainArr.put("chequeNo", "") //Invoice No
                mainArr.put("chequeDate", "") //Invoice No
                mainArr.put("Bank", "") //Invoice No
                mainArr.put("cardRefrenceId", "") //Invoice No
            } else {
                mainArr.put("collectionType", "Cash") //Invoice No
                mainArr.put("chequeNo", "") //Invoice No
                mainArr.put("chequeDate", "") //Invoice No
                mainArr.put("Bank", "") //Invoice No
                mainArr.put("cardRefrenceId", "") //Invoice No
            }

            if (type == "topup") {
                mainArr.put("mPtype", type)
                mainArr.put("iCashAccount", iCashAccountNo)
                var mtotalBalance =
                    mCurentBalance.toDouble() + grandTotal.toDouble() + mBonus.toDouble()
                mainArr.put("totalBalance", "" + mtotalBalance)
                mainArr.put("bonus", "" + mBonus)
                mainArr.put("Topupvalue", "" + grandTotal)
            } else {
                mainArr.put("mPtype", "Normal")
                mainArr.put("iCashAccount", "")
                mainArr.put("totalBalance", "0.0")
                mainArr.put("bonus", "")
                mainArr.put("Topupvalue", "")
            }


            var mPaentDetail = db!!.getCustomerParentDetail(customerId!!)
            mainArr.put("ParentCode", mPaentDetail["ParentCode"])
            mainArr.put("ParentName", mPaentDetail["Name"])
            mainArr.put(
                "CUSTOMER",
                customerData!!.userDetail.firstname
            )
            mainArr.put("CUSTOMERID", customerData!!.customer_code)
            mainArr.put("CUSTOMER LAST", customerData!!.userDetail.lastname)
            if (customerData!!.customer_address_1 != null) {
                mainArr.put("ADDRESS", "" + customerData!!.customer_address_1)
            } else {
                mainArr.put("ADDRESS", "")
            }
            if (customerData!!.customer_address_2 != null) {
                mainArr.put("ADDRESS2", "" + customerData!!.customer_address_2)
            } else {
                mainArr.put("ADDRESS2", "")
            }

            mainArr.put("CITY", db!!.getCustomerRegion(customerData!!.user_id.toString()))
            mainArr.put("Country", "UAE")

            if (customerData!!.customer_phone != null) {
                mainArr.put("Mobile", "" + customerData!!.customer_phone)
            } else {
                mainArr.put("Mobile", "")
            }
            var trnNo = db!!.getCustomerTRNNo(customerData!!.customer_code)
            mainArr.put("TRN", trnNo)

            val TOTAL = JSONArray()
            val HEADERS = JSONArray()
            HEADERS.put("Date")
            HEADERS.put("Invoice No")
            HEADERS.put("Amount")
            HEADERS.put("O/s Amount")
            HEADERS.put("Recvd Amount")

            mainArr.put("HEADERS", HEADERS)

            //SALES INVOICE

            val jData = JSONArray()
            val data = JSONArray()
            data.put(invDate) // Date
            data.put(invNo) // No
            data.put(grandTotal) //  Amount
            data.put(paidAmount) // O/s Amount
            data.put(paidAmount) // Recvd Amount
            jData.put(data)

            val jNetData = JSONArray()
            val jNetDData = JSONArray()

            jNetData.put("")
            jNetData.put("")
            jNetData.put("")
            jNetData.put("Total :")
            jNetData.put("" + paidAmount)

            val totalObj = JSONObject()
            totalObj.put(
                "Total Amount", paidAmount
            )
            totalObj.put(
                "Total Amount(AED)",
                Utils.getCurrencyFormate(this, paidAmount.toFloat())
            )
            TOTAL.put(totalObj)
            mainArr.put("TOTAL", TOTAL)
            mainArr.put("data", jData)
            mainArr.put("netTotal", jNetData)
            mainArr.put("shelfTotal", jNetDData)
            mainArr.put("MDTotal", jNetDData)
            jDict.put("mainArr", mainArr)
            jInter.put(jDict)
            jArr.put(jInter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jArr
    }


    fun createPartialPrintData(
        collectionNumber: String?,
        mType: String,
        mPaidAmount: String,
        mRemainAmnt: String
    ): JSONArray? {
        val jArr = JSONArray()
        try {
            val jInter = JSONArray()
            val jDict = JSONObject()
            jDict.put(AppConstants.REQUEST, AppConstants.COLLECTION)
            val mainArr = JSONObject()
            var routeCode =
                mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode
            var vanName = routeCode.replace("Zone", "Van sales")
            mainArr.put("ROUTE", vanName)
            mainArr.put("DOC DATE", Utils.formatDate(Date(), "dd-MM-yyyy"))
            mainArr.put("TIME", Utils.formatTime(Date(), "hh:mm"))
            mainArr.put("SALESMAN", mPreferenceManager!!.getString(AppConstants.DRIVERNAME))
            mainArr.put("CONTACTNO", "-")
            mainArr.put("DOCUMENT NO", collectionNumber) //Load Summary No
            mainArr.put("TRIP START DATE", mPreferenceManager!!.getTripDate())
            mainArr.put("supervisorname", "-")
            mainArr.put("orderNo", collectionNumber) //Invoice No
            mainArr.put("Type", "Single") //Invoice No
            mainArr.put("Comment", "") //Invoice No
            mainArr.put("OnAccount", "No")
            if (mType == "2") {
                mainArr.put("collectionType", "Cheque") //Invoice No
                mainArr.put("chequeNo", chequeNumber) //Invoice No
                mainArr.put("chequeDate", chequeDate) //Invoice No
                mainArr.put("Bank", bankName) //Invoice No
                mainArr.put("cardRefrenceId", "") //Invoice No
            } else if (mType == "3") {
                mainArr.put("collectionType", "Card") //Invoice No
                mainArr.put("cardRefrenceId", cardRefrenceId) //Invoice No
            } else if (mType == "5") {
                mainArr.put("collectionType", "iCash") //Invoice No
                mainArr.put("chequeNo", "") //Invoice No
                mainArr.put("chequeDate", "") //Invoice No
                mainArr.put("Bank", "") //Invoice No
                mainArr.put("cardRefrenceId", "") //Invoice No
            } else {
                mainArr.put("collectionType", "Cash") //Invoice No
                mainArr.put("chequeNo", "") //Invoice No
                mainArr.put("chequeDate", "") //Invoice No
                mainArr.put("Bank", "") //Invoice No
                mainArr.put("cardRefrenceId", "") //Invoice No
            }

            if (type == "topup") {
                mainArr.put("mPtype", type)
                mainArr.put("iCashAccount", iCashAccountNo)
                var mtotalBalance =
                    (mCurentBalance.toDouble() + paidAmount.toDouble() + mBonus.toDouble())
                mainArr.put("totalBalance", "" + mtotalBalance)
                mainArr.put("bonus", "" + mBonus)
                mainArr.put("Topupvalue", "" + paidAmount)
            } else {
                mainArr.put("mPtype", "Normal")
                mainArr.put("iCashAccount", "")
                mainArr.put("totalBalance", "0.0")
                mainArr.put("bonus", "")
                mainArr.put("Topupvalue", "")
            }


            var mPaentDetail = db!!.getCustomerParentDetail(customerId!!)
            mainArr.put("ParentCode", mPaentDetail["ParentCode"])
            mainArr.put("ParentName", mPaentDetail["Name"])
            mainArr.put(
                "CUSTOMER",
                customerData!!.userDetail.firstname
            )
            mainArr.put("CUSTOMERID", customerData!!.customer_code)
            mainArr.put("CUSTOMER LAST", customerData!!.userDetail.lastname)
            if (customerData!!.customer_address_1 != null) {
                mainArr.put("ADDRESS", "" + customerData!!.customer_address_1)
            } else {
                mainArr.put("ADDRESS", "")
            }
            if (customerData!!.customer_address_2 != null) {
                mainArr.put("ADDRESS2", "" + customerData!!.customer_address_2)
            } else {
                mainArr.put("ADDRESS2", "")
            }

            mainArr.put("CITY", db!!.getCustomerRegion(customerData!!.user_id.toString()))
            mainArr.put("Country", "UAE")

            if (customerData!!.customer_phone != null) {
                mainArr.put("Mobile", "" + customerData!!.customer_phone)
            } else {
                mainArr.put("Mobile", "")
            }
            var trnNo = db!!.getCustomerTRNNo(customerData!!.customer_code)
            mainArr.put("TRN", trnNo)

            val TOTAL = JSONArray()
            val HEADERS = JSONArray()
            HEADERS.put("Date")
            HEADERS.put("Invoice No")
            HEADERS.put("Amount")
            HEADERS.put("O/s Amount")
            HEADERS.put("Recvd Amount")

            mainArr.put("HEADERS", HEADERS)

            //SALES INVOICE

            val jData = JSONArray()
            val data = JSONArray()
            data.put(invDate) // Date
            data.put(invNo) // No
            data.put(grandTotal) //  Amount
            data.put(mRemainAmnt) // O/s Amount
            data.put(mPaidAmount) // Recvd Amount
            jData.put(data)

            val jNetData = JSONArray()
            val jNetDData = JSONArray()

            jNetData.put("")
            jNetData.put("")
            jNetData.put("")
            jNetData.put("Total :")
            jNetData.put("" + mPaidAmount)

            val totalObj = JSONObject()
            totalObj.put(
                "Total Amount", mPaidAmount
            )
            totalObj.put(
                "Total Amount(AED)",
                Utils.getCurrencyFormate(this, mPaidAmount.toFloat())
            )
            TOTAL.put(totalObj)
            mainArr.put("TOTAL", TOTAL)
            mainArr.put("data", jData)
            mainArr.put("netTotal", jNetData)
            mainArr.put("shelfTotal", jNetDData)
            mainArr.put("MDTotal", jNetDData)
            jDict.put("mainArr", mainArr)
            jInter.put(jDict)
            jArr.put(jInter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jArr
    }

    override fun onResume() {
        super.onResume()
        setAdapter()
        getFillPaidAmount()
    }

    override fun onStart() {
        super.onStart()

        // If BT is not on, request that it be enabled.
        // setupPrint() will then be called during onActivityResult
        if (!mBluetoothAdapter!!.isEnabled) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableIntent!!, REQUEST_ENABLE_BT)
            // Otherwise, setup the chat session
        } else {
            if (mPrintService == null) setupPrint()
        }
    }

    private fun setupPrint() {
        mPrintService = BluetoothPrintService(mHandler)
        mWoosim = WoosimCustomService(mHandler)
    }

    private val mHandler = MyHandler(this)

    private class MyHandler internal constructor(activity: PaymentDetailActivity) :
        Handler() {
        private val mActivity: WeakReference<PaymentDetailActivity>

        init {
            mActivity = WeakReference<PaymentDetailActivity>(activity)
        }

        override fun handleMessage(msg: Message) {
            val activity: PaymentDetailActivity? = mActivity.get()
            if (activity != null) {
                activity.handleMessage(msg)
            }
        }
    }

    private fun handleMessage(msg: Message) {
        when (msg.what) {
            MESSAGE_DEVICE_NAME -> {
                // save the connected device's name
                val mConnectedDeviceName =
                    msg.data.getString(DEVICE_NAME)
                Toast.makeText(
                    applicationContext,
                    "Connected to $mConnectedDeviceName",
                    Toast.LENGTH_SHORT
                ).show()
            }

            MESSAGE_TOAST -> Toast.makeText(
                applicationContext,
                msg.data.getInt(TOAST),
                Toast.LENGTH_SHORT
            ).show()

            MESSAGE_READ -> mWoosim!!.processRcvData(
                msg.obj as ByteArray,
                msg.arg1
            )

            WoosimCustomService.MESSAGE_PRINTER -> if (msg.arg1 == WoosimCustomService.MSR) {
                if (msg.arg2 == 0) {
                    Toast.makeText(applicationContext, "MSR reading failure", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val track = msg.obj as Array<ByteArray?>
                    if (track[0] != null) {
                        val str = track[0]
                        //      mTrack1View.setText(str);
                    }
                    if (track[1] != null) {
                        val str = track[1]
                        //     mTrack2View.setText(str);
                    }
                    if (track[2] != null) {
                        val str = track[2]
                        //     mTrack3View.setText(str);
                    }
                }
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CONNECT_DEVICE_SECURE ->                 // When DeviceListActivity returns with a device to connect
                if (resultCode == RESULT_OK) {
                    connectDevice(data!!, true)
                }

            scanCode ->                 // When DeviceListActivity returns with a device to connect
                if (resultCode == RESULT_OK) {
                    setAdapter()
                    getFillPaidAmount()
                }

            cardCode ->                 // When DeviceListActivity returns with a device to connect
                if (resultCode == RESULT_OK) {
                    var mCSType = ""
                    if (data!!.hasExtra("mType")) {
                        mCSType = data!!.getStringExtra("mType")!!
                    }

                    if (mCSType == "Success") {
                        cardRefrenceId = data!!.getStringExtra("cardRefrenceId")!!
                        merchantId = data!!.getStringExtra("merchantId")!!
                        transactionid = data!!.getStringExtra("transactionid")!!
                        terminalId = "mashreq POS"
                        lastFour = data!!.getStringExtra("lastFour")!!
                        cardType = data!!.getStringExtra("cardType")!!

                        //Utils.displayAlert(this@PaymentDetailActivity, "Payment success")

                        if (type == "topup") {
                            postTopupInvoice()
                        } else {
                            addCollection()
                        }
                    } else {
                        Utils.displayAlert(this@PaymentDetailActivity, "Payment not success")
                    }

                }
        }
    }

    private fun connectDevice(data: Intent, secure: Boolean) {
        var address: String? = null
        // Get the device MAC address
        if (data.extras != null) address =
            data.extras!!.getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS)
        // Get the BLuetoothDevice object
        val device = mBluetoothAdapter!!.getRemoteDevice(address)
        // Attempt to connect to the device
        mPrintService!!.connect(device, secure)

        Handler().postDelayed({

            Log.e("Call FUnc", "yes");
            val `object` = PrintLog(
                this@PaymentDetailActivity,
                this@PaymentDetailActivity
            )
            `object`.execute("", jsonArray)

        }, 3000)
    }

    fun setAdapter() {

        binding.rvList!!.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        var mAdapter = PaymentItemAdapter(
            context = this,
            allList = mPartialArray
        )
        binding.rvList!!.adapter = mAdapter

        if (mPartialArray.size > 0) {
            binding.rvList!!.visibility = View.VISIBLE
        }
    }

    class PaymentItemAdapter(
        var context: Activity,
        var allList: ArrayList<HashMap<String, String>>
    ) : RecyclerView.Adapter<PaymentItemAdapter.ViewHolder>() {

        val onItemClickListener = context as OnItemClickListener

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding =
                RowItemPaymentTypeBinding.inflate(LayoutInflater.from(context), parent, false)
            return ViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return mPartialArray.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.binding.txtType.setText(mPartialArray[holder.adapterPosition]["PaymentType"])
            holder.binding.txtAmount.setText(mPartialArray[holder.adapterPosition]["Amount"])

            holder.binding.lytDelete.setOnClickListener {
                onItemClickListener.onItemsClick(position, "Delete")
            }

            holder.binding.executePendingBindings()

        }


        inner class ViewHolder(val binding: RowItemPaymentTypeBinding) :
            RecyclerView.ViewHolder(binding.root) {
        }

    }

    override fun onItemsClick(position: Int, tag: String) {
        if (tag == "Delete") {

            mPartialArray.removeAt(position)
            getFillPaidAmount()
            setAdapter()
        }
    }

    fun paidTopupBalance(mCollNo: String) {

        val jObj = JsonObject()
        jObj.addProperty(
            "customer_id",
            customerData!!.user_id.toString()
        )
        jObj.addProperty(
            "type",
            "Topup"
        )
        jObj.addProperty(
            "amount",
            grandTotal
        )
        jObj.addProperty(
            "bonus",
            mBonus
        )
        jObj.addProperty(
            "transaction_number",
            mCollNo
        )

        ApiClient.getClient(this).postTopup(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            jObj,
        )

            .enqueue(object : Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    //dismissDialog()
                    callOtherActivity()
                }

                override fun onResponse(
                    p0: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { apiResponse ->
                            //dismissDialog()
                            if (response.body()!!["status"].asBoolean) {
                                //callPrintDialog()
                                callOtherActivity()
                            }

                        }
                    } else {
                        callOtherActivity()
                    }
                }

            })
    }

    fun redeamICashBalance(mCollNo: String, mAmount: String, isPartial: String) {

        val jObj = JsonObject()
        jObj.addProperty(
            "customer_id",
            customerData!!.user_id.toString()
        )
        jObj.addProperty(
            "type",
            "Payment"
        )
        jObj.addProperty(
            "amount",
            mAmount
        )
        jObj.addProperty(
            "bonus",
            ""
        )
        jObj.addProperty(
            "transaction_number",
            mCollNo
        )

        ApiClient.getClient(this).postTopup(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            jObj,
        )

            .enqueue(object : Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    if (isPartial != "1") {
                        dismissDialog()
                    }

                }

                override fun onResponse(
                    p0: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { apiResponse ->

                            if (response.body()!!["status"].asBoolean) {
                                if (isPartial != "1") {
                                    dismissDialog()
                                    ICashPrintDialog()
                                }
                            }

                        }
                    }
                }

            })
    }

    fun redeamonAccountCashBalance(mCollNo: String, mAmount: String, isPartial: String) {

        val jObj = JsonObject()
        jObj.addProperty(
            "customer_id",
            customerData!!.user_id.toString()
        )
        jObj.addProperty(
            "type",
            "Payment"
        )
        jObj.addProperty(
            "amount",
            mAmount
        )
        jObj.addProperty(
            "bonus",
            ""
        )
        jObj.addProperty(
            "transaction_number",
            mCollNo
        )

        ApiClient.getClient(this).postBalanceRedium(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            jObj,
        )

            .enqueue(object : Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    if (isPartial != "1") {
                        dismissDialog()
                    }

                }

                override fun onResponse(
                    p0: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { apiResponse ->

                            if (response.body()!!["status"].asBoolean) {
                                if (isPartial != "1") {
                                    dismissDialog()
                                    ICashPrintDialog()
                                }
                            }

                        }
                    }
                }

            })
    }

    fun redeamonDispenserCashBalance(mCollNo: String, mAmount: String, isPartial: String) {

        val jObj = JsonObject()
        jObj.addProperty(
            "customer_id",
            customerData!!.user_id.toString()
        )
        jObj.addProperty(
            "type",
            "Payment"
        )
        jObj.addProperty(
            "amount",
            mAmount
        )
        jObj.addProperty(
            "bonus",
            ""
        )
        jObj.addProperty(
            "transaction_number",
            mCollNo
        )

        ApiClient.getClient(this).postDispenserBalanceRedium(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            jObj,
        )

            .enqueue(object : Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    if (isPartial != "1") {
                        dismissDialog()
                    }

                }

                override fun onResponse(
                    p0: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { apiResponse ->

                            if (response.body()!!["status"].asBoolean) {
                                if (isPartial != "1") {
                                    dismissDialog()
                                    ICashPrintDialog()
                                }
                            }

                        }
                    }
                }

            })
    }

    fun callPrintDialog() {
        Utils.dialogPrint(this, object : OnSearchableDialog {

            override fun onItemSelected(selection: String?) {

                if (selection.equals("yes", ignoreCase = true)) {
                    try {
                        jsonArray!!.getJSONArray(0).getJSONObject(0)
                            .getJSONObject("mainArr")
                            .put("mType", "Customer")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    PrintLog.mPrintService = mPrintService
                    callDevice()

                } else if (selection.equals("customer", ignoreCase = true)) {
                    try {
                        jsonArray!!.getJSONArray(0).getJSONObject(0)
                            .getJSONObject("mainArr")
                            .put("mType", "Customer")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    PrintLog.mPrintService = mPrintService
                    callDevice()
                } else {
                    callOtherActivity()
                }
            }
        })
    }

    fun ICashPrintDialog() {
        Utils.dialogPrint(this, object : OnSearchableDialog {

            override fun onItemSelected(selection: String?) {

                if (selection.equals("yes", ignoreCase = true)) {
                    try {
                        jsonArray!!.getJSONArray(0).getJSONObject(0)
                            .getJSONObject("mainArr")
                            .put("mType", "Customer")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    PrintLog.mPrintService = mPrintService
                    callDevice()

                } else if (selection.equals(
                        "customer",
                        ignoreCase = true
                    )
                ) {
                    try {
                        jsonArray!!.getJSONArray(0).getJSONObject(0)
                            .getJSONObject("mainArr")
                            .put("mType", "Customer")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    PrintLog.mPrintService = mPrintService
                    callDevice()
                } else {
                    callOtherActivity()
                }
            }
        })
    }

    public fun getCurrentOnAccountBalance() {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty(
            "customer_id",
            customerData!!.userDetail.id.toString()
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
                                onAccountAmount = originalString.toFloat()
                            } else {
                                onAccountAmount = 0F
                            }
                        }
                    } else {
                        dismissDialog()
                    }

                    binding.txtICashBalance!!.setText("Account Balance: $onAccountAmount")

                }

            })
    }

    public fun getCurrentOnDispenseBalance() {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty(
            "customer_id",
            customerData!!.userDetail.id.toString()
        )

        ApiClient.getClient(this).getDispenserBalanceDetail(
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
                                onDispenserAmount = originalString.toFloat()
                            } else {
                                onDispenserAmount = 0F
                            }
                        }
                    } else {
                        dismissDialog()
                    }

                    binding.txtICashBalance!!.setText("Dispenser Balance: $onDispenserAmount")

                }

            })
    }

    public fun getCurrentBalance() {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty(
            "customer_id",
            customerData!!.userDetail.id.toString()
        )

        ApiClient.getClient(this).getICashDetail(
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
                                    (apiResponse.data.customer_data.balance.toDouble() + apiResponse.data.customer_data.bonus.toDouble())
                                iCashAmount = originalString.toFloat()
                            } else {
                                iCashAmount = 0F
                            }
                        }
                    } else {
                        dismissDialog()
                    }

                    binding.txtICashBalance!!.setText("ICash Balance: $iCashAmount")

                }

            })
    }

    private fun callCardPayment() {
        isRecursive = false

        dunnPayy?.dunnStartPayment(
            getPaymentRequest(),

            object : IPayResult {
                override fun onSuccess(payResponse: DunnPayResponse) {
                    getTRansactionSuceessCallBack(payResponse)
                }

                override fun onError(errorResponse: ErrorResponse) {
                    cardFailPayment(errorResponse)
                    //insertCardRecord("Fail")
                    //Log.e("Card Pay", errorResponse.responseMessage)
                }
            })

    }

    private fun insertCardRecord(mType: String) {
        var mCardData = CardFail()
        mCardData.transactionId = prevRefId
        mCardData.customer_id = customerData!!.user_id.toString()
        mCardData.amount = enterAmount.toString()
        mCardData.mTYpe = mType
        mCardData.mStatus = mType

        db!!.insertCardTransaction(mCardData)


//        val transaction = Transaction()
//        transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_CARD_FAIL_COLLECTION
//        transaction.tr_date_time = Utils.getCurrentDate() + " " + Utils.getCurrentTime()
//        transaction.tr_customer_num = customerData!!.customer_code
//        transaction.tr_customer_name =
//            customerData!!.userDetail!!.firstname + " " + customerData!!.userDetail!!.lastname
//        transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
//        transaction.tr_invoice_id = ""
//        transaction.tr_order_id = prevRefId
//        transaction.tr_collection_id = ""
//        transaction.tr_pyament_id = ""
//        transaction.tr_is_posted = "No"
//        transaction.tr_printData = ""
//        db!!.insertTransaction(transaction)
    }

    private fun cardFailPayment(errorResponse: ErrorResponse) {
        var message = ""

        if (errorResponse.responseMessage != null) {
            message = errorResponse.responseMessage.toString()
        }
        isClick = false
        isOpen = false
        Toast.makeText(this, "Card Payment Fail : $message", Toast.LENGTH_SHORT).show()
    }

    private fun getTRansactionSuceessCallBack(payResponse: DunnPayResponse) {
        var rrnNumber = payResponse.dunnPaymentReceipt.rrn
        cardRefrenceId = payResponse.approvalCode
        merchantId = payResponse.merchantId
        transactionid = payResponse.transactionId
        terminalId = "mashreq POS"
        lastFour = payResponse.lastFour
        cardType = payResponse.cardBrand


        //update status
        db!!.updateCardFailTranacPost(prevRefId, "Success")

        if (type == "topup") {
            postTopupInvoice()
        } else {
            addCollection()
        }

        //insertCardRecord("Success")
    }

    @SuppressLint("SetTextI18n")
    private fun getPaymentRequest(): DunnPayRequest? {
        val DUNNPayRequest = DunnPayRequest()
        DUNNPayRequest.authId = "6000203"
        DUNNPayRequest.authKey = "G4NOQ814JCH7TV1ZU13PZP2K"

        if (mMobile.isEmpty()) {
            DUNNPayRequest.phoneNo = "0000000000"
        } else {
            var mobile = mMobile
            var number = mobile
            number = number.trimStart('0')

            if (!number.startsWith("971")) {
                number = "971$number"
            }
            mMobile = number
            DUNNPayRequest.phoneNo = mMobile
        }

        if (mEmail.isEmpty()) {
            DUNNPayRequest.emailId = "test@gmail.com"
        } else {
            DUNNPayRequest.emailId = mEmail
        }
        DUNNPayRequest.merchantType = "MID"
        DUNNPayRequest.displayDetails = "N"
        DUNNPayRequest.fullReceipt = "Y"

        val dunnBillingList: MutableList<DunnBilling> = java.util.ArrayList()
        val dUNNBilling = DunnBilling()

        var bankcharge = 0.0
//        var mTaxAmount = Utils.getVatAmt(bankcharge.toDouble(), 5.0)
//        var mOtherAmount = Utils.twoDigit((bankcharge + mTaxAmount).toFloat())
        var mFinalAmount = enterAmount

        mBankCharge = "" + bankcharge
        mBankTaxAmt = "0.0"

        dUNNBilling.amount = "" + enterAmount
        dUNNBilling.otherAmount = "0"
        prevRefId = System.currentTimeMillis().toString()
        //prevRefId = getRandomNum(15)!!
        dUNNBilling.refId = prevRefId
        dUNNBilling.keyField1 = "SAMPLE"
        dunnBillingList.add(dUNNBilling)
        DUNNPayRequest.totalBillingAmount = "" + mFinalAmount
        DUNNPayRequest.dunnBillings = dunnBillingList


        insertCardRecord("Request")

        return DUNNPayRequest
    }

    private fun paymentLinkDialog(mType: String) {
        val factory = LayoutInflater.from(this)
        val binding: PaymentLinkDialogBinding =
            DataBindingUtil.inflate(factory, R.layout.payment_link_dialog, null, false)

        val deleteDialogView: View = binding.root

        var mobile = mMobile
        var number = mobile
        number = number.trimStart('0')

        if (!number.startsWith("971")) {
            number = "971$number"
        }
        mMobile = number

        binding.edtMobile.setText(mMobile)
        binding.edtEmail.setText(mEmail)
        var mRemaingAmt = 0.0
        if (mType == "Invoice") {
            binding.lytICashView.visibility = View.VISIBLE
            mRemaingAmt = enterAmount.toDouble()
            binding.tvInvAmt.text = "Invoice Amount: $enterAmt"
            binding.tviCashAmt.text = "iCash Redemption: $iCashAmount"
            if (iCashAmount > enterAmt.toFloat()) {
                binding.tvRMAmount.text = "Remaining Amount: $mRemaingAmt"
            } else {
                mRemaingAmt = Utils.twoDigit((enterAmt.toFloat() - iCashAmount)).toDouble()
                binding.tvRMAmount.text = "Remaining Amount: $mRemaingAmt"
            }

        } else {
            binding.lytICashView.visibility = View.GONE
        }
        val deleteDialog: AlertDialog = AlertDialog.Builder(this).create()
        deleteDialog.setView(deleteDialogView)
        deleteDialog.setCancelable(false)

        binding.btnConfirm.setOnClickListener {
            var mobileS = binding.edtMobile.text.toString()
            var mobile = mobileS
            var number = mobile
            number = number.trimStart('0')

            if (!number.startsWith("971")) {
                number = "971$number"
            }
            mobile = number
            var email = binding.edtEmail.text.toString()

            if (mobile.isEmpty()) {
                Toast.makeText(
                    this@PaymentDetailActivity,
                    "Please enter mobile number",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                deleteDialog.dismiss()

                if (mType == "topup") {
                    getPaymentLinkBitCashURL(mobile, email)
                } else {
                    getPaymentLinkAdjustmentURL(mobile, email, mRemaingAmt.toString())
                }
            }


        }

        binding.btnCancel.setOnClickListener {
            deleteDialog.dismiss()
            isClick = false
        }
        deleteDialog.show()
    }


    private fun getPaymentLinkBitCashURL(mMobile: String, mEmail: String) {
        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty("webId", customerData!!.user_id.toString())
        jObj.addProperty("iCashNumber", iCashAccountNo)
        jObj.addProperty("amount", enterAmount.toString())
        jObj.addProperty("email", mEmail)
        jObj.addProperty("telephone", mMobile)
        jObj.addProperty("address", customerData!!.customer_address_1)
        jObj.addProperty("type", "bitcash")

        Log.e("Payment request", jObj.toString())

        val username = "mobiato"
        val password = "d2cyYkVKLAd26Z"
        val credentials = "$username:$password"
        val authHeader =
            "Basic " + android.util.Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)


        ApiClient.getClient(this@PaymentDetailActivity).getPaymentLink(
            MyApplication.PAYMENT_LINK_BASE_URL,
            authHeader,
            jObj
        ).enqueue(object : Callback<JsonObject> {
            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                Log.e("Cancel request", response.body().toString())

                if (response.isSuccessful) {
                    if (response.body()!!["status"].asString == "success") {
                        var mPaymentLink = response.body()!!["link"].asString

                        sendPaymentLinkToBitcashCustomer(mMobile, mEmail, mPaymentLink)
                    } else {
                        dismissDialog()
                        isClick = false
                        Utils.displayAlert(
                            this@PaymentDetailActivity,
                            "Payment Link creation fail! Please try again"
                        )
                    }
                } else {
                    dismissDialog()
                    isClick = false
                    Utils.displayAlert(
                        this@PaymentDetailActivity,
                        "Payment Link fail"
                    )
                }

            }

            override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                Log.e("Cancel Fail", "Fail")
                dismissDialog()
                isClick = false
                Utils.displayAlert(this@PaymentDetailActivity, "Request Sent fail!!")
            }
        })
    }

    private fun getPaymentLinkAdjustmentURL(mMobile: String, mEmail: String, mAmount: String) {
        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty("webId", customerData!!.user_id.toString())
        jObj.addProperty("iCashNumber", iCashAccountNo)
        jObj.addProperty("amount", mAmount)
        jObj.addProperty("email", mEmail)
        jObj.addProperty("telephone", mMobile)
        jObj.addProperty("address", customerData!!.customer_address_1)
        jObj.addProperty("type", "adjustment")

        Log.e("Payment request", jObj.toString())

        val username = "mobiato"
        val password = "d2cyYkVKLAd26Z"
        val credentials = "$username:$password"
        val authHeader =
            "Basic " + android.util.Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)


        ApiClient.getClient(this@PaymentDetailActivity).getPaymentLink(
            MyApplication.PAYMENT_LINK_BASE_URL,
            authHeader,
            jObj
        ).enqueue(object : Callback<JsonObject> {
            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                Log.e("Cancel request", response.body().toString())

                if (response.isSuccessful) {
                    if (response.body()!!["status"].asString == "success") {
                        var mPaymentLink = response.body()!!["link"].asString

                        sendPaymentLinkToInvocieCustomer(mMobile, mEmail, mPaymentLink)
                    } else {
                        dismissDialog()
                        isClick = false
                        Utils.displayAlert(
                            this@PaymentDetailActivity,
                            "Payment Link creation fail! Please try again"
                        )
                    }
                } else {
                    dismissDialog()
                    isClick = false
                    Utils.displayAlert(
                        this@PaymentDetailActivity,
                        "Payment Link fail"
                    )
                }

            }

            override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                Log.e("Cancel Fail", "Fail")
                dismissDialog()
                isClick = false
                Utils.displayAlert(this@PaymentDetailActivity, "Request Sent fail!!")
            }
        })
    }

    private fun sendPaymentLinkToBitcashCustomer(mMobile: String, mEmail: String, mLink: String) {

        val jObj = JsonObject()
        jObj.addProperty("mobile_number", mMobile)
        jObj.addProperty("email", mEmail)
        jObj.addProperty("account_number", iCashAccountNo)
        jObj.addProperty("customer_id", customerData!!.user_id.toString())
        jObj.addProperty("order_no", "")
        jObj.addProperty("recharge_amount", enterAmount)
        jObj.addProperty("bonus_amount", mBonus)
        var mTotal = enterAmount + mBonus.toFloat()
        jObj.addProperty("total_amount", mTotal.toString())
        jObj.addProperty(
            "pay_by",
            mLink
        )

        Log.e("Mobiato Payment", jObj.toString())
        ApiClient.getClient(this).sendPaymentBitCashLink(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN,
            jObj
        ).enqueue(object : Callback<PaymentLinkData> {
            override fun onResponse(
                call: Call<PaymentLinkData>,
                response: Response<PaymentLinkData>
            ) {
                dismissDialog()
                if (response.isSuccessful) {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                    // Log.e("deliveries", Gson().toJson(response.body()))
                    if (response.body()!!.status!!) {
                        displayAlert(
                            this@PaymentDetailActivity,
                            "The payment link has been created successfully. Please check your WhatsApp and email for the same."
                        )
                    } else {
                        displayFailAlert(
                            this@PaymentDetailActivity,
                            "Payment Link fail"
                        )
                    }
                } else {
                    displayFailAlert(
                        this@PaymentDetailActivity,
                        "Payment Link fail"
                    )
                }
            }

            override fun onFailure(call: Call<PaymentLinkData>, error: Throwable) {
                dismissDialog()
                displayFailAlert(
                    this@PaymentDetailActivity,
                    "Payment Link fail"
                )
            }
        })
    }

    private fun sendPaymentLinkToInvocieCustomer(mMobile: String, mEmail: String, mLink: String) {

        val jObj = JsonObject()
        jObj.addProperty("mobile_number", mMobile)
        jObj.addProperty("email", mEmail)
        jObj.addProperty("customer_id", customerData!!.user_id.toString())

        var mDetail = db!!.getDeliveryOrderDetail(mDeliveryNo!!)
        jObj.addProperty("order_no", mDetail["OrderNo"])
        jObj.addProperty("amount", enterAmount)
        jObj.addProperty(
            "payment_link",
            mLink
        )

        Log.e("Mobiato Payment", jObj.toString())
        ApiClient.getClient(this).sendPaymentLink(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN,
            jObj
        ).enqueue(object : Callback<PaymentLinkData> {
            override fun onResponse(
                call: Call<PaymentLinkData>,
                response: Response<PaymentLinkData>
            ) {
                dismissDialog()
                if (response.isSuccessful) {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                    // Log.e("deliveries", Gson().toJson(response.body()))
                    if (response.body()!!.status!!) {
                        displayFailAlert(
                            this@PaymentDetailActivity,
                            "The payment link has been created successfully. Please check your WhatsApp and email for the same."
                        )
                    } else {
                        displayFailAlert(
                            this@PaymentDetailActivity,
                            "Payment Link fail"
                        )
                    }
                } else {
                    displayFailAlert(
                        this@PaymentDetailActivity,
                        "Payment Link fail"
                    )
                }
            }

            override fun onFailure(call: Call<PaymentLinkData>, error: Throwable) {
                dismissDialog()
                displayFailAlert(
                    this@PaymentDetailActivity,
                    "Payment Link fail"
                )
            }
        })
    }

    fun displayAlert(mContext: Context, message: String) {
        val alertDialogBuilder = AlertDialog.Builder(mContext)
        alertDialogBuilder.setTitle("Alert")
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(
                "Ok"
            ) { dialog, which ->
                isClick = false
                finish()
                dialog.dismiss()
            }
        // create alert dialog
        val alertDialog = alertDialogBuilder.create()
        // show it
        alertDialog.show()
    }

    fun displayFailAlert(mContext: Context, message: String) {
        val alertDialogBuilder = AlertDialog.Builder(mContext)
        alertDialogBuilder.setTitle("Alert")
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(
                "Ok"
            ) { dialog, which ->
                isClick = false
                dialog.dismiss()
            }
        // create alert dialog
        val alertDialog = alertDialogBuilder.create()
        // show it
        alertDialog.show()
    }

}