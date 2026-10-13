package com.engear.vansales.activities

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView.*
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.R
import com.engear.vansales.adapter.AddressSelectionAdapter
import com.engear.vansales.adapter.OrderConfirmItemAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.DeviceListActivity
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityOrderConfirmBinding
import com.engear.vansales.databinding.OrderCommentDialogBinding
import com.engear.vansales.databinding.RoundToDialogBinding
import com.engear.vansales.databinding.RowReturnImageDialogBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.interfaces.OnSearchableDialog
import com.engear.vansales.model.*
import com.engear.vansales.printer.BluetoothPrintService
import com.engear.vansales.printer.PrintLog
import com.engear.vansales.printer.WoosimCustomService
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.apache.commons.math3.util.Precision.round
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sh.locus.lotr.model.VisitStatusRequest
import sh.locus.lotr.sdk.LocusLotrSdk
import sh.locus.lotr.sdk.LotrSdkReadyCallback
import sh.locus.lotr.sdk.VisitStatusUpdateParams
import sh.locus.lotr.sdk.auth.UserAuthParams
import sh.locus.lotr.sdk.exception.LotrSdkError
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.ref.WeakReference
import java.lang.reflect.Type
import java.util.*
import kotlin.math.roundToInt

class OrderConfirmActivity : BaseActivity(), OnItemClickListener {
    lateinit var binding: ActivityOrderConfirmBinding
    var lobIdList: ArrayList<String> = ArrayList()
    var lobIdListCheck: ArrayList<String> = ArrayList()
    var orderItemsList: ArrayList<ItemData.Data>? = ArrayList()
    var orderItemsAnarcoList: ArrayList<ItemData.Data>? = ArrayList()
    var orderItemsAdmiralList: ArrayList<ItemData.Data>? = ArrayList()
    var orderItemsList_send: ArrayList<ItemData.Data>? = ArrayList()
    var orderItemsList_lob: ArrayList<ItemData.Data>? = ArrayList()
    var orderItemsBatchList_Return: ArrayList<CustomerItemReturnData.DataCustomerItemReturn>? =
        ArrayList()
    var orderItemsBatchListLob_Return: ArrayList<CustomerItemReturnData.DataCustomerItemReturn>? =
        ArrayList()
    var addressList: List<CustomerHODAddress.CustomerInfoAddress.CustomerHIAddress> =
        ArrayList()
    var totalUomList: MutableList<UomData>? = ArrayList()
    private var orderNo: String = ""
    var mAddressId: String = ""
    private var invoiceNo: String = ""
    var orderDate: String = Utils.getCurrentTimeInPattern("yyyy-MM-dd")
    var deliveryDate: String = Utils.getCurrentTimeInPattern("yyyy-MM-dd")
    var dueDate: String = Utils.getCurrentTimeInPattern("yyyy-MM-dd")
    var customerCode: String? = null
    var customerData: CustomerData.DataCustomer? = null
    var customerId: String? = "null"
    var salesmanId: String? = "null"
    var orderTypeId: String? = "null"
    var invocieType: String = ""
    var orderId: String? = ""
    var deliveryId: String? = ""
    var deliveryNo: String? = "null"
    var paymentTermId: String? = "null"
    var depotId: String? = null
    var isCouponRedeem: String = "0"
    var orderAmt: Float = 0f
    var vatAmt: Float = 0f
    var excise: Float = 0f
    var netAmt: Float = 0f
    var grossAmt: Float = 0f
    var exciseAmt: Float = 0f
    var deliveryChargeAmt: Float = 0f
    var loyalityPoint: Float = 0f
    var itemDiscountAmt: Float = 0f
    var discountAmt: Float = 0f
    var totalAmt: Float = 0f
    var roundTo: Float = 0f
    var roundOff: Float = 0f
    var orderComment: String? = null
    var customerLPO: String = ""
    var type: String? = null
    var collNo: String = ""
    var invoiceId: String = ""
    var invoiceNA: String = ""
    var invoiceAmt: String = ""
    var isWithoutReference: String = ""
    var isAccept: String = ""
    var lobCode: String = ""
    var isRequest: String = ""
    private var jsonArray: JSONArray? = null
    var isCustDiscount = false
    var discountData: DiscountOfferData? = null
    var balance = ""
    var isCommentClick = false
    var isProceedClick = false
    var isDispenserOrder = false
    var dueOn = "1"
    var mProgressDialog: ProgressDialog? = null
    var allLoadBatchItem: ArrayList<BatchdetailsList> = ArrayList()
    var allVanBatchItem: ArrayList<BatchdetailsList>? = ArrayList()
    var allVanBatchLobItem: ArrayList<BatchdetailsList>? = ArrayList()
    var cuslobList: ArrayList<CustomerData.DataCustomer.Customerlob>? = ArrayList()
    //var lobList: ArrayList<CustomerData.DataCustomer.Customerlob>? = null

    var isCouponSales: String = "0"
    var lob_id_send: String = ""
    var lob_id_original: String = ""
    var customer_print_enable: String = ""
    var salesman_route_print_enable: String = ""

    private val pickImage = 100
    private val CAMERA = 200
    private var imageUri: Uri? = null
    var encodedImage: String? = ""
    var miCashBalance = 0.0
    var mCustomerBalance = 0.0
    var mCustomerDispenserBalance = 0.0

    //Imageview 2

    private val pickImage2 = 300
    private val CAMERA_SECOND = 400
    private var imageUri2: Uri? = null
    var encodedImage2: String? = ""

    //Imageview 3
    private val pickImage3 = 500
    private val CAMERA_Three = 600
    private var imageUri3: Uri? = null
    var encodedImage3: String? = ""

    var str_image_count: String = "1"
    var accountNumber: String = ""
    var mMobile: String = ""
    var mEmail: String = ""

    private val REQUEST_CONNECT_DEVICE_SECURE = 1
    private val REQUEST_ENABLE_BT = 3
    private var mBluetoothAdapter: BluetoothAdapter? = null


    val MESSAGE_DEVICE_NAME = 1
    val MESSAGE_TOAST = 2
    val MESSAGE_READ = 3

    val DEVICE_NAME = "device_name"
    val TOAST = "toast"
    var isDraftPrint = false
    var isBatch = "0"
    var mOfferType = ""
    var mOfferValue = ""
    var mIsIcash = ""
    var mIcashAmount = ""
    var mRemainAmt = ""


    companion object {

        var mActivity: OrderConfirmActivity? = null

        fun callbackFunction(mType: String, mCopyType: String) {
            if (mCopyType != "Draft") {
                mActivity!!.callOtherActivity(mType)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderConfirmBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        mActivity = this

        init()
    }

    private fun init() {
        Utils.logData(
            this@OrderConfirmActivity,
            "On order confirm Screen "
        )

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        baseBinding.toolbarTitle.setOnClickListener {
            dateDialog()
        }

        customer_print_enable =
            mPreferenceManager!!.getString(AppConstants.CUSTOMERPRINTENABLE).toString()
        salesman_route_print_enable =
            mPreferenceManager!!.getString(AppConstants.PRINTENABLE).toString()

        customerCode = intent.getStringExtra("customer_code")
        type = intent.getStringExtra("type")
        lobCode = intent.getStringExtra("lob")!!


        if (customerCode != null) {
            if (type == "load") {
                customerId = customerCode
            } else {
                customerData = db!!.getCustomerDetail(customerCode!!)
                if (!lobCode.isNullOrEmpty()) {
                    var lobobj = db!!.getCustomerLob(lobCode, customerData!!.user_id.toString())
                    cuslobList = db!!.getCustomerLobList(customerData!!.userDetail.id.toString())
                    customerData!!.credit_days = lobobj!!.credit_days.toString()
                    customerData!!.credit_limit = lobobj!!.credit_limit
                    customerData!!.payment_term_id = lobobj.payment_term_id!!
                    dueOn = lobobj.due_on.toString()
                    customerData!!.customer_type_id = lobobj.customer_type_id!!.toString()
                    if (lobobj!!.customer_type_id.toString() == "2") {
                        customerData!!.credit_days = "0"
                    }
                } else {
                    dueOn = customerData!!.due_on.toString()
                }
                customerId = customerData!!.id.toString()

            }
        }

        if (type == "order") {
            binding.btnDraft!!.visibility = View.GONE
            binding.lytAddress!!.visibility = View.VISIBLE
            deliveryDate = intent.getStringExtra("delivery_date")!!
            balance = intent.getStringExtra("balance")!!

            setToolbarTitle("Order Summary")

            if (customerData!!.customer_type_id == "2") {
                orderTypeId = "1"
                dueDate =
                    Utils.addDays(
                        "yyyy-MM-dd",
                        deliveryDate,
                        customerData!!.credit_days!!.toInt(),
                        "1"
                    )!!
                Log.e("Due Date", dueDate)
            } else {
                dueDate =
                    Utils.addDays(
                        "yyyy-MM-dd",
                        deliveryDate,
                        customerData!!.credit_days!!.toInt(),
                        "2"
                    )!!
                Log.e("Due Date", dueDate)
                orderTypeId = "2"
            }


            if (intent.getStringExtra("isAccept") != null) {
                isAccept = intent.getStringExtra("isAccept")!!
                orderNo = intent.getStringExtra("orderNo")!!
            }

            getCustomerAddress()

        } else if (type == "load") {
            binding.lytGross!!.visibility = View.GONE
            binding.lytTotal!!.visibility = View.GONE
            binding.btnDraft!!.visibility = View.GONE
            deliveryDate = intent.getStringExtra("delivery_date")!!
            setToolbarTitle("Load Request Summary")

            dueOn = "1"
            dueDate =
                Utils.addDays("yyyy-MM-dd", deliveryDate, 1, dueOn)!!

            orderTypeId = db!!.getOrderTypeId("Depot")

        } else if (type == "goodReturn" || type == "badReturn") {
            binding.btnUploadImage!!.visibility = View.VISIBLE
            binding.ivInfoUploadStamp!!.visibility = View.VISIBLE
            binding.btnDraft!!.visibility = View.GONE
            setToolbarTitle("Return Summary")
            if (intent.getStringExtra("invoice_id") != null) {
                invoiceId = intent.getStringExtra("invoice_id")!!
            }

            if (intent.getStringExtra("reType") != null) {
                isWithoutReference = intent.getStringExtra("reType")!!
            }

            if (intent.getStringExtra("isAccept") != null) {
                isAccept = intent.getStringExtra("isAccept")!!
                orderNo = intent.getStringExtra("orderNo")!!
                if (intent.getStringExtra("isRequest") != null) {
                    isRequest = intent.getStringExtra("isRequest")!!
                }
            }

            if (isRequest == "1") {
                binding.btnUploadImage!!.visibility = View.GONE
                binding.ivInfoUploadStamp!!.visibility = View.GONE
            }

        } else if (type == "sales") {

            binding.btnDraft!!.visibility = View.GONE
            balance = intent.getStringExtra("balance")!!
            deliveryDate = intent.getStringExtra("delivery_date")!!
            isCouponSales = intent.getStringExtra("isCoupon")!!
            if (intent.getStringExtra("isCouponRedeem") != null) {
                isCouponRedeem = intent.getStringExtra("isCouponRedeem")!!
            }
            setToolbarTitle("Invoice Summary")

            if (intent.getStringExtra("iCashBalance") != null) {
                miCashBalance = intent.getStringExtra("iCashBalance")!!.toDouble()
            }

            if (intent.getStringExtra("accountBalance") != null) {
                mCustomerBalance = intent.getStringExtra("accountBalance")!!.toDouble()
            }

            binding.txtICashBalance!!.text = miCashBalance.toString()
            binding.txtOnBalance!!.text = mCustomerBalance.toString()
            //getCurrentBalance()

            if (customerData!!.customer_type_id == "2") {
                orderTypeId = "1"
            } else {
                orderTypeId = "2"
            }

            if (customerData!!.customer_type_id == "2") {
                invocieType = "Cash"
                dueOn = "1"
            } else {
                invocieType = "Credit"
                dueOn = "2"
            }

            dueDate =
                Utils.addDays(
                    "yyyy-MM-dd",
                    deliveryDate,
                    customerData!!.credit_days!!.toInt(),
                    dueOn
                )!!

            if (intent.getStringExtra("isAccept") != null) {
                isAccept = intent.getStringExtra("isAccept")!!
                invoiceNo = intent.getStringExtra("orderNo")!!
            }


        } else if (type == "delivery") {
            binding.btnDraft!!.visibility = View.VISIBLE
            setToolbarTitle("Delivery Summary")
            balance = intent.getStringExtra("balance")!!
            orderId = intent.getStringExtra("order_id")
            deliveryId = intent.getStringExtra("delivery_id")
            deliveryNo = intent.getStringExtra("delivery_date")
            isBatch = intent.getStringExtra("isBatch")!!
            invoiceNo = deliveryNo!!
            deliveryDate = Utils.getCurrentDate()
            if (intent.getStringExtra("isCouponRedeem") != null) {
                isCouponRedeem = intent.getStringExtra("isCouponRedeem")!!
            }

            if (intent.getStringExtra("accountNumber") != null) {
                accountNumber = intent.getStringExtra("accountNumber")!!
            }

            if (intent.getStringExtra("mobile") != null) {
                mMobile = intent.getStringExtra("mobile")!!
            }

            if (intent.getStringExtra("email") != null) {
                mEmail = intent.getStringExtra("email")!!
            }

            if (customerData!!.customer_type_id == "2") {
                orderTypeId = "1"
            } else {
                orderTypeId = "2"
            }

            if (customerData!!.customer_type_id == "2") {
                invocieType = "Cash"
                dueOn = "1"
            } else {
                invocieType = "Credit"
                dueOn = "2"
            }

            dueDate =
                Utils.addDays(
                    "yyyy-MM-dd",
                    deliveryDate,
                    customerData!!.credit_days!!.toInt(),
                    dueOn
                )!!

            if (intent.getStringExtra("iCashBalance") != null) {
                miCashBalance = intent.getStringExtra("iCashBalance")!!.toDouble()
            }

            if (intent.getStringExtra("accountBalance") != null) {
                mCustomerBalance = intent.getStringExtra("accountBalance")!!.toDouble()
            }

            binding.txtICashBalance!!.text = Utils.getCurrencyFormate(this, miCashBalance.toFloat())
            binding.txtOnBalance!!.text = Utils.getCurrencyFormate(this, mCustomerBalance.toFloat())
            //getCurrentBalance()

            var mDetail = db!!.getDeliveryOrderDetail(deliveryId!!)

            mOfferType = mDetail["offer_type"]!!
            mOfferValue = mDetail["offer_value"]!!

            mIsIcash = mDetail["is_icash"]!!
            mIcashAmount = mDetail["icash_amount"]!!

            var mOrderSource = mDetail["OrderSource"]
            var mPaymentType = mDetail["PaymentMethod"]
            var MpaymetRef = mDetail["PaymentRefrence"]
            var mDispenser = mDetail["isDispenser"]
            if (mDispenser == "1") {
                isDispenserOrder = true
            } else {
                isDispenserOrder = false
            }
            Log.e("Source", mOrderSource!!)
            Log.e("PaymentMethod", mPaymentType!!)

            if (customerData!!.channel_id == "1") {
                if (customerData!!.customer_category_id == "85") {
                    binding.btnDraft!!.visibility = View.INVISIBLE
                }
            }
        }

        salesmanId = "0"
        orderComment = "0"

        val strObj = intent!!.getStringExtra("orderItemList")
        val listOfUomType: Type = object : TypeToken<List<ItemData.Data?>?>() {}.type

        orderItemsList = Gson().fromJson(strObj, listOfUomType)

        binding.ivInfoUploadStamp!!.setOnClickListener {
            StampUploadDialog()
        }

        binding.rlCheckout.setOnClickListener {
            //  orderSummaryDialog()
        }

        binding.lytAddress!!.setOnClickListener {
            showSelectionPopup()
        }

        if (type == "order" || type == "sales") {
            var isCUstPromo = db!!.isDiscountHeaderCUst(customerId!!)
            if (isCUstPromo) {
                var mPlanId = db!!.getCustomerHeaderDiscout(customerId!!)
                discountData = db!!.getCustomerDiscountDetail(mPlanId)

                if (discountData != null) {
                    if (discountData!!.discountMainType == "0") {
                        isCustDiscount = true
                    }
                }

            } else {
                if (lobCode.isNotEmpty()) {
                    var mLobDetail =
                        db!!.getCustomerLob(lobCode, customerData!!.user_id.toString())

                    var isOtherPromo = db!!.isOtherHeaderDiscountPDP(
                        if (mLobDetail!!.region_id != null) mLobDetail!!.region_id.toString()
                        else "",
                        if (mLobDetail!!.sales_organisation_id != null) mLobDetail!!.sales_organisation_id.toString()
                        else "",
                        if (mLobDetail!!.channel_id != null) mLobDetail!!.channel_id.toString()
                        else "",
                        if (mLobDetail!!.customer_category_id != null) mLobDetail!!.customer_category_id.toString()
                        else "",
                        if (mLobDetail!!.route_id != null) mLobDetail!!.route_id.toString()
                        else ""
                    )

                    if (isOtherPromo) {
                        var mPlanId = db!!.otherHeaderDiscountPDPPlanId(
                            if (mLobDetail!!.region_id != null) mLobDetail!!.region_id.toString()
                            else "",
                            if (mLobDetail!!.sales_organisation_id != null) mLobDetail!!.sales_organisation_id.toString()
                            else "",
                            if (mLobDetail!!.channel_id != null) mLobDetail!!.channel_id.toString()
                            else "",
                            if (mLobDetail!!.customer_category_id != null) mLobDetail!!.customer_category_id.toString()
                            else "",
                            if (mLobDetail!!.route_id != null) mLobDetail!!.route_id.toString()
                            else ""
                        )
                        discountData = db!!.getCustomerDiscountDetail(mPlanId)
                        if (discountData != null) {
                            if (discountData!!.discountMainType == "0") {
                                isCustDiscount = true
                            }
                        }
                    }
                } else {
                    var isOtherPromo = db!!.isOtherHeaderDiscountPDP(
                        if (customerData!!.region_id != null) customerData!!.region_id.toString()
                        else "",
                        if (customerData!!.sales_organisation_id != null) customerData!!.sales_organisation_id.toString()
                        else "",
                        if (customerData!!.channel_id != null) customerData!!.channel_id.toString()
                        else "",
                        if (customerData!!.customer_category_id != null) customerData!!.customer_category_id.toString()
                        else "",
                        if (customerData!!.route_id != null) customerData!!.route_id.toString()
                        else ""
                    )

                    if (isOtherPromo) {
                        var mPlanId = db!!.otherHeaderDiscountPDPPlanId(
                            if (customerData!!.region_id != null) customerData!!.region_id.toString()
                            else "",
                            if (customerData!!.sales_organisation_id != null) customerData!!.sales_organisation_id.toString()
                            else "",
                            if (customerData!!.channel_id != null) customerData!!.channel_id.toString()
                            else "",
                            if (customerData!!.customer_category_id != null) customerData!!.customer_category_id.toString()
                            else "",
                            if (customerData!!.route_id != null) customerData!!.route_id.toString()
                            else ""
                        )
                        discountData = db!!.getCustomerDiscountDetail(mPlanId)
                        if (discountData != null) {
                            if (discountData!!.discountMainType == "0") {
                                isCustDiscount = true
                            }
                        }
                    }
                }
            }
        }

        calculateOrderPrice()
        setUpRecycler()


        binding.btnProceed.setOnClickListener {
            Utils.logData(
                this@OrderConfirmActivity,
                "On proceed button click Screen "
            )

            val alertDialogBuilder =
                AlertDialog.Builder(this@OrderConfirmActivity)
            alertDialogBuilder.setTitle("Alert")
                .setMessage(
                    "Are you sure you want to proceed?"
                )
                .setCancelable(false)
                .setPositiveButton(
                    "Yes"
                ) { dialog, which ->
                    dialog.dismiss()
                    if (!isCommentClick) {
                        isCommentClick = true
                        binding.btnProceed.visibility = View.INVISIBLE
                        if (type == "sales") {

                            if (customerData!!.customer_type_id == "2" || customerData!!.customer_type_id == "3") {
                                if (totalAmt > 0) {
                                    orderCommentDialog()
                                } else {
                                    val alertDialogBuilder =
                                        AlertDialog.Builder(this@OrderConfirmActivity)
                                    alertDialogBuilder.setTitle("Alert")
                                        .setMessage(
                                            "Amount is 0. You can not proceed with 0 amount"
                                        )
                                        .setCancelable(false)
                                        .setPositiveButton(
                                            "OK"
                                        ) { dialog, which ->
                                            dialog.dismiss()
                                            binding.btnProceed.visibility = View.VISIBLE
                                            //sendApprovalRequest(customerData!!.user_id.toString())
                                        }

                                    alertDialogBuilder.show()
                                }

                            } else {
                                if (totalAmt > balance.toDouble()) {
                                    val alertDialogBuilder =
                                        AlertDialog.Builder(this@OrderConfirmActivity)
                                    alertDialogBuilder.setTitle("Alert")
                                        .setMessage(
                                            "Customer limit is not available. You can not sale to this customer more then available limit."
                                        )
                                        .setCancelable(false)
                                        .setPositiveButton(
                                            "Yes"
                                        ) { dialog, which ->
                                            dialog.dismiss()
                                            //sendApprovalRequest(customerData!!.user_id.toString())
                                        }.setNegativeButton(
                                            "No"
                                        ) { dialog, which ->
                                            dialog.dismiss()
                                            binding.btnProceed.visibility = View.VISIBLE
                                        }

                                    alertDialogBuilder.show()
                                } else {
                                    orderCommentDialog()
                                }
                            }
//
                        } else if (type == "goodReturn" || type == "badReturn") {
                            if (isRequest == "1") {
                                insertReturn()
                            } else {
                                if (encodedImage == "") {
                                    isCommentClick = false
                                    binding.btnProceed.visibility = View.VISIBLE
                                    Toast.makeText(
                                        this@OrderConfirmActivity,
                                        "Choose Image!!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    orderCommentDialog()
                                }
                            }
                        } else if (type == "delivery") {
                            if (isBatch == "1") {
                                if (isBatchValid()) {
                                    orderCommentDialog()
                                } else {
                                    Toast.makeText(
                                        this@OrderConfirmActivity,
                                        "Select batch and expiry for all items",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    isCommentClick = false
                                    binding.btnProceed.visibility = View.VISIBLE
                                }
                            } else {
                                if (totalAmt > 0) {
                                    orderCommentDialog()
                                } else {
                                    val alertDialogBuilder =
                                        AlertDialog.Builder(this@OrderConfirmActivity)
                                    alertDialogBuilder.setTitle("Alert")
                                        .setMessage(
                                            "Amount is 0. You can not proceed with 0 amount"
                                        )
                                        .setCancelable(false)
                                        .setPositiveButton(
                                            "OK"
                                        ) { dialog, which ->
                                            dialog.dismiss()
                                            binding.btnProceed.visibility = View.VISIBLE
                                            //sendApprovalRequest(customerData!!.user_id.toString())
                                        }

                                    alertDialogBuilder.show()
                                }
                            }
                        } else if (type == "order") {

                            if (mAddressId.isEmpty()) {
                                val alertDialogBuilder =
                                    AlertDialog.Builder(this@OrderConfirmActivity)
                                alertDialogBuilder.setTitle("Alert")
                                    .setMessage(
                                        "Please select the delivery address first!.."
                                    )
                                    .setCancelable(false)
                                    .setPositiveButton(
                                        "Ok"
                                    ) { dialog, which ->
                                        dialog.dismiss()
                                        binding.btnProceed.visibility = View.VISIBLE
                                        //sendApprovalRequest(customerData!!.user_id.toString())
                                    }

                                alertDialogBuilder.show()
                            } else {
                                orderCommentDialog()
                            }

                        } else {
                            orderCommentDialog()
                        }
                    }
                }.setNegativeButton(
                    "No"
                ) { dialog, which ->
                    dialog.dismiss()

                }

            alertDialogBuilder.show()

        }

        binding.btnDraft!!.setOnClickListener {
            Utils.logData(
                this@OrderConfirmActivity,
                "On draft button click Screen "
            )

            if (type == "sales") {
                if (invoiceNo.isEmpty())
                    invoiceNo =
                        Utils.getLastIndexNew(
                            mPreferenceManager!!.getString(AppConstants.INVOICE_LAST).toString(),
                            "invoice"
                        )


                //get Print Data
                var data: JSONObject? = null
                try {
                    jsonArray = createPrintInvoiceData(invoiceNo, "Draft", "")
                    data = JSONObject()
                    data.put("data", jsonArray as JSONArray?)
                    Log.e("PrintData", data.toString())
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            } else if (type == "delivery") {

                if (isBatch == "1") {
                    if (isBatchValid()) {
                        if (invoiceNo.isEmpty())
                            invoiceNo = deliveryNo!!

                        isDraftPrint = true
                        //get Print Data
                        var data: JSONObject? = null
                        try {
                            jsonArray = createPrintInvoiceData(invoiceNo, "Draft", "")
                            data = JSONObject()
                            data.put("data", jsonArray as JSONArray?)
                            Log.e("PrintData", data.toString())
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(
                            this@OrderConfirmActivity,
                            "Select batch and expiry for all items",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }
                } else {
                    if (invoiceNo.isEmpty())
                        invoiceNo = deliveryNo!!

                    isDraftPrint = true
                    //get Print Data
                    var data: JSONObject? = null
                    try {
                        jsonArray = createPrintInvoiceData(invoiceNo, "Draft", "")
                        data = JSONObject()
                        data.put("data", jsonArray as JSONArray?)
                        Log.e("PrintData", data.toString())
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }

            } else {
                if (orderNo.isEmpty())
                    orderNo =
                        Utils.getLastIndexNew(
                            mPreferenceManager!!.getString(AppConstants.RETURN_LAST).toString(),
                            "return"
                        )

                //get Print Data
                var data: JSONObject? = null
                try {
                    jsonArray = createPrintReturnData(orderNo, "Draft")
                    data = JSONObject()
                    data.put("data", jsonArray as JSONArray?)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            PrintLog.mPrintService = mPrintService
            callDevice()

        }

        binding.lytRT.setOnClickListener {
            roundToDialog()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_topup, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }

            R.id.action_survey -> {
                startActivity(Intent(me, IcashTopupActivity::class.java).apply {
                    this.putExtra("customer_code", customerCode)
                })
                return true
            }
        }
        return false
    }

    private fun callAccountCopy() {

        try {
            jsonArray!!.getJSONArray(0).getJSONObject(0)
                .getJSONObject("mainArr")
                .put("mType", "Account")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val `object` = PrintLog(
            this@OrderConfirmActivity,
            this@OrderConfirmActivity
        )
        `object`.execute("", jsonArray)

    }

    private fun callOtherActivity(mType: String) {
        if (type == "sales" || type == "delivery") {
            if (!isDraftPrint) {
                callPaymentScreen()
            } else {
                isDraftPrint = false
            }
        } else {
            finish()
        }
    }

    private fun calculateOrderPrice() {
        exciseAmt = 0F
        grossAmt = 0F
        itemDiscountAmt = 0F
        discountAmt = 0F
        netAmt = 0F
        vatAmt = 0F
        totalAmt = 0F
        deliveryChargeAmt = 0F
        loyalityPoint = 0F

        for (i in orderItemsList!!) {
            for (j in i.itemSelectUOM!!) {
                var grossPrice = 0F
                if (j!!.totalAmt.toFloat() > 0) {
                    grossPrice = Utils.twoDigit((j!!.uomQty.toFloat() * j!!.uomPrice.toFloat()))
                } else {
                    grossPrice = 0F
                }

                var mNetAmt = 0f
                var excisePrice = Utils.twoDigit((j!!.uomQty.toFloat() * j!!.item_excise.toFloat()))
                grossAmt = grossAmt.plus(Utils.twoDigit(grossPrice))
                exciseAmt = exciseAmt.plus(Utils.twoDigit(excisePrice))
                if (j!!.totalAmt.toDouble() > 0.0) {
                    itemDiscountAmt =
                        itemDiscountAmt.plus(Utils.twoDigit(j!!.discountAmt.toFloat()))
                    mNetAmt = grossPrice - Utils.twoDigit(j!!.discountAmt.toFloat())

                } else {
                    mNetAmt = grossPrice - Utils.twoDigit(0F)
                }

                netAmt = netAmt.plus(Utils.twoDigit(mNetAmt))
                var mVat =
                    Utils.getVatAmt((mNetAmt.toDouble() + excisePrice.toDouble()), 5.0).toFloat()
                vatAmt = vatAmt.plus(Utils.twoDigit(mVat))
                var mTotal =
                    Utils.twoDigit(mNetAmt) + Utils.twoDigit(mVat) + Utils.twoDigit(excisePrice)
                totalAmt = totalAmt.plus(Utils.twoDigit(mTotal))
                totalUomList!!.add(j)
            }
        }

        if (isCustDiscount) {
            var appalyOn = discountData!!.discountApply
            if (appalyOn == "2") {
                var value = discountData!!.discountValue
                var to = discountData!!.discountTo
                var type = discountData!!.discountType
                if (type == "1") {
                    if (grossAmt >= to!!.toFloat()) {
                        var factor = (grossAmt / to!!.toFloat()).toInt()
                        netAmt = Utils.twoDigit((grossAmt - itemDiscountAmt))
                        discountAmt = (netAmt * factor).toFloat()
                        var mNTotal = Utils.twoDigit((netAmt - discountAmt))
                        vatAmt =
                            Utils.twoDigit(Utils.getVatAmt(mNTotal.toDouble(), 5.0).toFloat())

                        totalAmt = Utils.twoDigit((mNTotal + vatAmt))
                    }
                } else {
                    if (grossAmt >= to!!.toFloat()) {
                        var percet =
                            (discountData!!.discountPercentage!!.toFloat())
                        netAmt = Utils.twoDigit(grossAmt - itemDiscountAmt)
                        discountAmt =
                            Utils.twoDigit(((netAmt * percet!!.toFloat()) / 100))
                        var mNTotal = Utils.twoDigit((netAmt - discountAmt))
                        vatAmt =
                            Utils.twoDigit(Utils.getVatAmt(mNTotal.toDouble(), 5.0).toFloat())

                        totalAmt = Utils.twoDigit((mNTotal + vatAmt))
                    }
                }
            }
        } else {
            if (type == "delivery") {

                if (mOfferType == "1") {
                    loyalityPoint = mOfferValue.toFloat()
                } else if (mOfferType == "2") {
                    loyalityPoint = round((netAmt * mOfferValue.toDouble() / 100), 2).toFloat()
                }

                if (loyalityPoint > 0) {
                    netAmt = Utils.twoDigit((netAmt - loyalityPoint))
                    var mNTotal = Utils.twoDigit((netAmt))
                    vatAmt =
                        Utils.twoDigit(Utils.getVatAmt(mNTotal.toDouble(), 5.0).toFloat())

                    totalAmt = Utils.twoDigit((mNTotal + vatAmt))
                }
            }
        }

        binding.txtGross.text =
            Utils.getCurrency(this) + " " + Utils.getCurrencyFormate(this, grossAmt)
        binding.txtLnDiscount!!.text =
            Utils.getCurrency(this) + " " + Utils.getCurrencyFormate(this, itemDiscountAmt)
        binding.txtNet.text = Utils.getCurrency(this) + " " + Utils.getCurrencyFormate(this, netAmt)
        binding.txtVat.text = Utils.getCurrency(this) + " " + Utils.getCurrencyFormate(this, vatAmt)
        binding.txtDiscount.text =
            Utils.getCurrency(this) + " " + Utils.getCurrencyFormate(this, itemDiscountAmt)
        binding.txtRewards!!.text =
            Utils.getCurrency(this) + " " + Utils.getCurrencyFormate(this, loyalityPoint)
        binding.txtExcise.text =
            Utils.getCurrency(this) + " " + Utils.getCurrencyFormate(this, exciseAmt)

        if (type == "delivery") {
            var mCharges = db!!.getDelvieryCharges(deliveryId!!)
            deliveryChargeAmt = mCharges.toFloat()
            totalAmt += deliveryChargeAmt
            roundTo = totalAmt
        }


        if (mIsIcash == "Yes") {
            binding.lytPaid!!.visibility = View.VISIBLE
            binding.lytRemain!!.visibility = View.VISIBLE
            binding.txtPaid!!.text = mIcashAmount

            var mRemain = totalAmt - mIcashAmount.toDouble()
            binding.txtRemain!!.text = Utils.getCurrencyFormate(this, mRemain.toFloat())

            mRemainAmt = Utils.getCurrencyFormate(this, mRemain.toFloat())
        }
        binding.txtDevCharge!!.text =
            Utils.getCurrency(this) + " " + Utils.getCurrencyFormate(this, deliveryChargeAmt)

        binding.txtGrandTotal.text =
            Utils.getCurrency(this) + " " + Utils.getCurrencyFormate(this, totalAmt)

        if (type == "sales") {
            roundTo = totalAmt
            binding.txtRoundTo.text =
                Utils.getCurrency(this) + " " + Utils.getCurrencyFormate(this, roundTo)
            binding.lytRoundTo.visibility = View.VISIBLE
        }
        binding.tvTotal.text =
            Utils.getCurrencyFormate(this, totalAmt) + " " + Utils.getCurrency(this)

    }

    fun setRoundAmount() {
        roundOff = roundTo - totalAmt
        binding.txtRoundTo.text =
            Utils.getCurrency(this) + " " + Utils.getCurrencyFormate(this, roundTo)
        binding.tvTotal.text =
            Utils.getCurrencyFormate(this, roundTo) + " " + Utils.getCurrency(this)
    }

    private fun roundToDialog() {
        val factory = LayoutInflater.from(this)
        val binding: RoundToDialogBinding =
            DataBindingUtil.inflate(factory, R.layout.round_to_dialog, null, false)

        val deleteDialogView: View = binding.root

        val deleteDialog: AlertDialog = AlertDialog.Builder(this).create()
        deleteDialog.setView(deleteDialogView)

        binding.edtInvAmount.setText("" + Utils.getCurrencyFormate(this, totalAmt))
        binding.edtRoundAmount.setText("" + Utils.getCurrencyFormate(this, roundTo))

        binding.btnConfirm.setOnClickListener {

            var mRoundTo = binding.edtRoundAmount.text.toString().toDouble()

            var difference = totalAmt - mRoundTo
            if (difference > 0.49) {
                Utils.displayAlert(this, "Round of amount should not be greater than 0.49")
            } else if (difference < (-0.49)) {
                Utils.displayAlert(this, "Round of amount should not be greater than 0.49")
            } else {
                roundTo = mRoundTo.toFloat()
                setRoundAmount()
                deleteDialog.dismiss()
            }
        }

        binding.btnCancel.setOnClickListener {

            deleteDialog.dismiss()
        }

        deleteDialog.show()
    }

    private fun orderCommentDialog() {
        val factory = LayoutInflater.from(this)
        val binding: OrderCommentDialogBinding =
            DataBindingUtil.inflate(factory, R.layout.order_comment_dialog, null, false)

        val deleteDialogView: View = binding.root

        binding.txtName.text = "Enter Comment"

        if (type == "sales") {
            binding.lytLPO.visibility = View.VISIBLE
            binding.lytSignature!!.visibility = VISIBLE
        } else if (type == "delivery") {
            binding.lytSignature!!.visibility = VISIBLE
        } else if (type == "order") {
            binding.lytLPO.visibility = View.VISIBLE
        } else if (type == "goodReturn" || type == "badReturn") {
            binding.lytLPO.visibility = View.GONE
            if (isWithoutReference == "1") {
                binding.lytInvoiceNo.visibility = View.GONE
                binding.lytInvoiceAmt.visibility = View.GONE
            } else {
                binding.lytInvoiceNo.visibility = View.GONE
                binding.lytInvoiceAmt.visibility = View.GONE
            }
        }

        val deleteDialog: AlertDialog = AlertDialog.Builder(this).create()
        deleteDialog.setView(deleteDialogView)
        deleteDialog.setCancelable(false)

        binding.btnConfirm.setOnClickListener {
            orderComment = binding.edtComment.text.toString()
            var isValidRe = true
            if (isWithoutReference == "1") {
                if (type == "goodReturn" || type == "badReturn") {
                    isValidRe = true
                }
            }

            if (isValidRe) {
                if (!isProceedClick) {
                    isProceedClick = true
                    if (type == "order") {
                        customerLPO = binding.edtLpoNo.text.toString()
                        insertOrder()
                    } else if (type == "load") {
                        insertLoadRequest()
                    } else if (type == "goodReturn" || type == "badReturn") {
                        if (isWithoutReference == "1") {
                            invoiceNA = binding.edtInvNo.text.toString()
                            invoiceAmt = binding.edtInvAmt.text.toString()
                        }
                        insertReturn()
                    } else if (type == "sales") {
                        customerLPO = binding.edtLpoNo.text.toString()

                        insertInvoice("")
                    } else if (type == "delivery") {
                        var mSignCap = ""
                        if (!binding.signaturePad.isEmpty) {
                            val resized = resizeForWoosim(binding.signaturePad.signatureBitmap)
                            val mono = convertToBW(resized)
                            mSignCap = saveBitmapToFile(this, mono)
                        }

                        insertInvoice(mSignCap)
                    }
                    deleteDialog.dismiss()
                }
            }


        }

        deleteDialog.show()
    }

    fun resizeForWoosim(bitmap: Bitmap): Bitmap {
        val printerWidth = 384
        val ratio = printerWidth.toFloat() / bitmap.width
        val height = (bitmap.height * ratio).toInt()
        return Bitmap.createScaledBitmap(bitmap, printerWidth, height, true)
    }

    fun convertToBW(bitmap: Bitmap): Bitmap {
        val bw = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bw)
        val paint = Paint()

        val cm = ColorMatrix()
        cm.setSaturation(0f)
        paint.colorFilter = ColorMatrixColorFilter(cm)

        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return bw
    }

    fun saveBitmapToFile(context: Context, bitmap: Bitmap): String {

        val storagestate = Environment.getExternalStorageState()
        var documentsFolder: File? = null
        if (storagestate == Environment.MEDIA_MOUNTED) {
            documentsFolder = File(
                Environment.getExternalStorageDirectory(),
                "Download" + File.separator + "Signature"
            )
            if (!documentsFolder.exists()) {
                documentsFolder.mkdirs()
            }
        }

        var filename = ""
        filename = "signature_${System.currentTimeMillis()}.png"
        val dest = File(documentsFolder, filename)
        var encodedImage = dest.absolutePath
        try {
            val out: FileOutputStream = FileOutputStream(dest)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        return encodedImage
    }

    private fun isBatchValid(): Boolean {
        var isallFill = true

        for (mItem in orderItemsList!!) {
            if (mItem.batchNo != null) {
                if (mItem.batchNo.equals("0")) {
                    isallFill = false
                    break
                }
            } else {
                isallFill = false
                break
            }
        }
        return isallFill

    }

    private fun setUpRecycler() {
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = OrderConfirmItemAdapter(this, orderItemsList!!, type!!)
    }

    override fun onItemsClick(position: Int, tag: String) {

        if (isBatch == "1") {
            showBatchDialog(position)
        }
    }

    private fun showBatchDialog(position: Int) {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.batch_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val metrics = resources.displayMetrics
        val screenWidth = (metrics.widthPixels * 0.95).toInt()
        dialog.window!!.setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
        val spBtach = dialog.findViewById(R.id.batchSpinner) as Spinner
        val edtDate = dialog.findViewById(R.id.edt_date) as TextView
        val btnConfirm = dialog.findViewById(R.id.btn_confirm) as Button
        val btnCancel = dialog.findViewById(R.id.btn_cancel) as Button

        var mBatchNo = ""
//        var spUomList: MutableList<String>? = ArrayList()
//        spUomList!!.add("23022024")
//        spUomList!!.add("33022024")
//        val dataAdapter = spUomList?.let {
//            ArrayAdapter<String>(
//                applicationContext,
//                R.layout.my_spinner_item, it
//            )
//        }
//        dataAdapter!!.setDropDownViewResource(R.layout.my_spinner_item)
//        spBtach.adapter = dataAdapter
//
//        spBtach.onItemSelectedListener = object : OnItemSelectedListener {
//            override fun onItemSelected(
//                parentView: AdapterView<*>,
//                selectedItemView: View,
//                position: Int,
//                id: Long
//            ) {
//                mBatchNo = spUomList!![position].toString()
//            }
//
//            override fun onNothingSelected(parentView: AdapterView<*>) {
//                // your code here
//            }
//        }

        edtDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)


            val dpd = android.app.DatePickerDialog(
                this,
                android.app.DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    edtDate.text = "$year-" + String.format(
                        "%02d",
                        monthOfYear + 1
                    ) + "-" + String.format("%02d", dayOfMonth)
                    mBatchNo = "$year" + String.format(
                        "%02d",
                        monthOfYear + 1
                    ) + "" + String.format("%02d", dayOfMonth)

                },
                year,
                month,
                day
            )

            dpd.show()
        }

        btnConfirm.setOnClickListener {
            var mExpDate = edtDate.text.toString()
            if (!mExpDate.isEmpty()) {
                dialog.dismiss()

                orderItemsList!![position].batchNo = mBatchNo
                orderItemsList!![position].expiry = mExpDate

            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun insertInvoice(mSign: String) {

        var mInvNo = ""
        if (invoiceNo.isEmpty()) {
            mInvNo = Utils.getLastIndexNew(
                mPreferenceManager!!.getString(AppConstants.INVOICE_LAST).toString(),
                "invoice"
            )

            if (mPreferenceManager!!.getFTokenId()!! == "3") {
                var routeCode =
                    mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode

                if (customerData!!.channel_id == "1") {
                    invoiceNo = "HH${routeCode}IN" +
                            Utils.getLastIndexNew(
                                mPreferenceManager!!.getString(AppConstants.INVOICE_LAST)
                                    .toString(),
                                "invoice"
                            )
                } else {
                    invoiceNo = "D${routeCode}IN" +
                            Utils.getLastIndexNew(
                                mPreferenceManager!!.getString(AppConstants.INVOICE_LAST)
                                    .toString(),
                                "invoice"
                            )
                }

            } else {
                invoiceNo =
                    Utils.getLastIndexNew(
                        mPreferenceManager!!.getString(AppConstants.INVOICE_LAST).toString(),
                        "invoice"
                    )
            }

        } else {
            if (mPreferenceManager!!.getFTokenId()!! == "4") {
                if (customerData!!.channel_id == "1") {
                    invoiceNo = invoiceNo.replace("S1", "HH")
                }
            } else if (mPreferenceManager!!.getFTokenId()!! == "3") {
                if (customerData!!.channel_id == "1") {
                    invoiceNo = invoiceNo.replace("S1", "HH")
                }
            }
        }


        var mOrder = Invoice()
        mOrder.invoiceNo = invoiceNo
        mOrder.orderType = orderTypeId
        mOrder.invoiceDate = orderDate
        mOrder.invoiceType = orderTypeId
        mOrder.deliveryId = deliveryId
        mOrder.orderId = orderId
        mOrder.dueDate = dueDate
        mOrder.customerId = customerCode.toString()
        mOrder.gross = grossAmt.toString()
        mOrder.discount = itemDiscountAmt.toString()
        mOrder.net = netAmt.toString()
        mOrder.vat = vatAmt.toString()
        mOrder.excAmt = exciseAmt.toString()
        mOrder.total = roundTo.toString()
        mOrder.comment = orderComment.toString()
        mOrder.payemntId = customerData!!.payment_term_id.toString()
        mOrder.lobId = ""
        mOrder.roundTo = roundOff.toString()
        mOrder.isExchange = "0"
        mOrder.exchangeNo = ""
        mOrder.paidAmount = ""
        if (customerData!!.customer_type_id == "2") {
            mOrder.isStamp = "1"
        } else {
            mOrder.isStamp = ""
        }
        mOrder.isStamp = ""
        mOrder.isInvoiceCancel = "0"
        mOrder.isTopup = "0"
        mOrder.isCoupon = isCouponRedeem
        mOrder.isCouponSale = isCouponSales
        mOrder.mDelvieryCharge = deliveryChargeAmt.toString()
        mOrder.mRewards = loyalityPoint.toString()
        mOrder.latitude = AppConstants.Latitude
        mOrder.longitude = AppConstants.Longitude
        mOrder.punchTime = Utils.getCUrrentDateWithTime()

        if (isCouponSales == "1") {
            var couponId = db!!.getCouponId(
                orderItemsList!![0].itemSelectUOM[0]!!.itemId.toString(),
                orderItemsList!![0].itemSelectUOM[0].promotion_Id,
                orderItemsList!![0].itemSelectUOM[0].discount_Id
            )
            mOrder.couponId = couponId
        } else {
            if (isCouponRedeem == "1") {
                var couponId = db!!.getCustomerCouponId(customerData!!.user_id.toString())
                mOrder.couponId = couponId
            } else {
                mOrder.couponId = ""
            }

        }

        db!!.insertInvoice(
            mOrder = mOrder,
            mItemList = orderItemsList!!,
            mLPO = customerLPO
        )

        if (type == "delivery") {

        } else {
            //set Invoice Last Number
            mPreferenceManager!!.setString(AppConstants.INVOICE_LAST, mInvNo)
        }


        //update VanSTock Qty
        for (mData in orderItemsList!!) {

            var mPrimUOM = db!!.getItemPrimaryUOM(mData.id!!.toString())
            var mPRMUPC = ""
            if (mPrimUOM!!.size > 0) {
                mPRMUPC = mPrimUOM[0].uomUPC
            } else {
                mPRMUPC = "1"
            }

            var mQty = 0.0
            for (mUOM in mData.itemSelectUOM) {
                mQty += Utils.getUOMLowerQty(mPRMUPC, mUOM.uomQty).toDouble()
            }

            if (isCouponSales == "1") {

                db!!.updateCouponSalesQty(
                    mOrder.couponId!!
                )

                if (mData.itemCode != "MGWD025") {
                    var fromSerial = mData.itemSelectUOM[0].promotion_Id.toLong()
                    var toSerial = mData.itemSelectUOM[0].discount_Id.toLong()

                    for (i in fromSerial..toSerial) {

                        db!!.insertCustomerCoupon(
                            customerId!!.toString(),
                            i.toString(),
                            mOrder.couponId!!, "0", mData.itemSelectUOM[0].uomPrice.toString()
                        )
                    }
                }

            } else {

                if (isCouponRedeem == "1") {
                    var fromSerial = mData.itemSelectUOM[0].promotion_Id.toLong()
                    if (mData.itemSelectUOM[0].discount_Id.toString().isNotEmpty()) {
                        var toSerial = mData.itemSelectUOM[0].discount_Id.toLong()
                        for (i in fromSerial..toSerial) {

                            db!!.insertCustomerCoupon(
                                customerId!!.toString(),
                                i.toString(),
                                mOrder.couponId!!, "1", mData.itemSelectUOM[0].uomPrice.toString()
                            )
                        }
                    } else {
                        var toSerial = mData.itemSelectUOM[0].promotion_Id.toLong()
                        for (i in fromSerial..toSerial) {

                            db!!.insertCustomerCoupon(
                                customerId!!.toString(),
                                i.toString(),
                                mOrder.couponId!!, "1", mData.itemSelectUOM[0].uomPrice.toString()
                            )
                        }
                    }
                }

                var existQty =
                    db!!.getVanStockExistItemQty(
                        mData.id.toString(),
                        mData.vanUOM,
                        "Remaining"
                    )
                        .toDouble()

                var mUpdateQty = existQty - mQty

                db!!.updateVanStockQty(
                    mData.id.toString(),
                    mData.vanUOM,
                    mUpdateQty.toString()
                )

            }

        }

        insertTransectionSale(invoiceNo, "0", mSign)

    }

    private fun calculateOrderPriceAdmarial(mItemList: ArrayList<ItemData.Data>?) {
        exciseAmt = 0F
        grossAmt = 0F
        itemDiscountAmt = 0F
        discountAmt = 0F
        netAmt = 0F
        vatAmt = 0F
        totalAmt = 0F

        for (i in mItemList!!) {
            for (j in i.itemSelectUOM!!) {
                if (i.itemCode == j.itemCode) {
                    var grossPrice = Utils.twoDigit((j!!.uomQty.toFloat() * j!!.uomPrice.toFloat()))
                    var excisePrice =
                        Utils.twoDigit((j!!.uomQty.toFloat() * j!!.item_excise.toFloat()))
                    grossAmt = grossAmt.plus(Utils.twoDigit(grossPrice))
                    exciseAmt = exciseAmt.plus(Utils.twoDigit(excisePrice))
                    itemDiscountAmt =
                        itemDiscountAmt.plus(Utils.twoDigit(j!!.discountAmt.toFloat()))
                    var mNetAmt = grossPrice - Utils.twoDigit(j!!.discountAmt.toFloat())
                    netAmt = netAmt.plus(Utils.twoDigit(mNetAmt))
                    var mVat =
                        Utils.getVatAmt((mNetAmt.toDouble() + excisePrice.toDouble()), 5.0)
                            .toFloat()
                    vatAmt = vatAmt.plus(Utils.twoDigit(mVat))
                    var mTotal =
                        Utils.twoDigit(mNetAmt) + Utils.twoDigit(mVat) + Utils.twoDigit(excisePrice)
                    totalAmt = totalAmt.plus(Utils.twoDigit(mTotal))
                    totalUomList!!.add(j)
                }

            }
        }

        if (type == "sales") {
            roundTo = totalAmt
        }

    }


    private fun insertTransectionSale(invoiceNo: String, lob_id_send: String, mSign: String) {
        //get Print Data
        var data: JSONObject? = null
        try {
            /*jsonArray = createPrintInvoiceData(this.invoiceNo, "customer")*/
            jsonArray = createPrintInvoiceData(invoiceNo, "customer", mSign)
            data = JSONObject()
            data.put("data", jsonArray as JSONArray?)
            Log.e("Inv Print", data.toString())
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val transaction = Transaction()
        transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_SALES_CREATED
        transaction.tr_date_time = Utils.getCurrentDate() + " " + Utils.getCurrentTime()
        transaction.tr_customer_num = customerCode
        transaction.tr_customer_name =
            customerData!!.userDetail!!.firstname + " " + customerData!!.userDetail!!.lastname
        transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
        /* transaction.tr_invoice_id = this.invoiceNo*/
        transaction.tr_invoice_id = invoiceNo
        transaction.tr_order_id = ""
        transaction.tr_collection_id = ""
        transaction.tr_pyament_id = ""
        transaction.tr_printData = data.toString()
        transaction.tr_is_posted = "No"

        db!!.insertTransaction(transaction)

        db!!.updateCustomerTransaction(customerData!!.userDetail!!.id.toString(), "sale")

        var cashType = "0"
        var mCollNo = ""

        if (customerData!!.channel_id == "1") {
            if (customerData!!.customer_type_id == "2" || customerData!!.customer_type_id == "3") {
                cashType = "1"
                //Add Collection

                mCollNo = Utils.getLastIndexNew(
                    mPreferenceManager!!.getString(AppConstants.COLLECTION_LAST).toString(),
                    "collection"
                )
                if (mPreferenceManager!!.getFTokenId()!! == "3") {
                    var routeCode =
                        mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode

                    collNo = "HH${routeCode}C" +
                            Utils.getLastIndexNew(
                                mPreferenceManager!!.getString(AppConstants.COLLECTION_LAST)
                                    .toString(),
                                "collection"
                            )
                } else {

                    var routeCode =
                        mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode


                    collNo = "P${routeCode}C" +
                            Utils.getLastIndexNew(
                                mPreferenceManager!!.getString(AppConstants.COLLECTION_LAST)
                                    .toString(),
                                "collection"
                            )
                }

            } else {
                cashType = "0"
                mCollNo = Utils.getLastIndexNew(
                    mPreferenceManager!!.getString(AppConstants.COLLECTION_LAST).toString(),
                    "collection"
                )
            }

            if (type == "delivery") {
                var mDetail = db!!.getDeliveryOrderDetail(deliveryId!!)

                var mOrderSource = mDetail["OrderSource"]
                var mPaymentType = mDetail["PaymentMethod"]
                var MpaymetRef = mDetail["PaymentRefrence"]
                var mCardType = mDetail["CardType"]
                var mCardCharge = mDetail["CardCharge"]
                var mPendingAmt = ""
                var mPaid = "0"
                if (mIsIcash == "Yes") {
                    mPendingAmt = mRemainAmt
                    mPaid = Utils.twoDigit(roundTo - mRemainAmt.toFloat()).toString()

                    if (mPaid.toDouble() < 0.30) {
                        mPaid = "0"
                    }
                } else {
                    mPendingAmt = roundTo.toString()
                }
                if (mPaymentType == "Prepaid") {

                    if (customerData!!.customer_type_id != "2" || customerData!!.customer_type_id == "3") {
                        cashType = "1"
                        mCollNo = Utils.getLastIndexNew(
                            mPreferenceManager!!.getString(AppConstants.COLLECTION_LAST).toString(),
                            "collection"
                        )
                        if (mPreferenceManager!!.getFTokenId()!! == "3") {
                            var routeCode =
                                mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode

                            collNo = "HH${routeCode}C" +
                                    Utils.getLastIndexNew(
                                        mPreferenceManager!!.getString(AppConstants.COLLECTION_LAST)
                                            .toString(),
                                        "collection"
                                    )
                        }
                    }

                    db!!.addCollection(
                        invoiceNo,
                        collNo,
                        customerData!!.userDetail.id.toString(),
                        dueDate,
                        orderDate,
                        roundTo.toString(),
                        mPendingAmt,
                        lobCode,
                        if (customerData!!.customer_group_id != null) customerData!!.customer_group_id!!.toString()
                        else "",
                        customerData!!.customer_code,
                        customerData!!.userDetail.firstname,
                        roundTo.toString(),
                        cashType,
                        "Invoice",
                        "Invoice",
                        "0",
                        "0",
                        mOrderSource!!,
                        MpaymetRef!!,
                        mPaymentType!!,
                        "6",
                        "0",
                        mCardType!!,
                        mCardCharge!!
                    )

                    //set Order Last Number
                    if (mCollNo.isNotEmpty())
                        mPreferenceManager!!.setString(AppConstants.COLLECTION_LAST, mCollNo)

                    val transaction = Transaction()
                    transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_PR_COLLECTION
//                    var data: JSONObject? = null
//                    try {
//                        jsonArray = createPrintData(collNo)
//                        data = JSONObject()
//                        data.put("data", jsonArray as JSONArray?)
//                    } catch (e: java.lang.Exception) {
//                        e.printStackTrace()
//                    }

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
                    transaction.tr_printData = ""
                    db!!.insertTransaction(transaction)

                } else {

                    if (isCouponRedeem != "1") {
                        db!!.addCollection(
                            invoiceNo,
                            collNo,
                            customerData!!.userDetail.id.toString(),
                            dueDate,
                            orderDate,
                            roundTo.toString(),
                            mPendingAmt,
                            lobCode,
                            if (customerData!!.customer_group_id != null) customerData!!.customer_group_id!!.toString()
                            else "",
                            customerData!!.customer_code,
                            customerData!!.userDetail.firstname,
                            mPaid,
                            cashType, "Invoice", "Invoice", "0", "0",
                            mOrderSource!!, MpaymetRef!!, mPaymentType!!, "1", "0", "", ""
                        )

                        //if (customerData!!.customer_type_id == "2") {
                        //set Order Last Number
                        if (mCollNo.isNotEmpty())
                            mPreferenceManager!!.setString(AppConstants.COLLECTION_LAST, mCollNo)
                        //}
                    }
                }
            } else {

                if (isCouponRedeem != "1") {
                    db!!.addCollection(
                        invoiceNo,
                        collNo,
                        customerData!!.userDetail.id.toString(),
                        dueDate,
                        orderDate,
                        roundTo.toString(),
                        roundTo.toString(),
                        lobCode,
                        if (customerData!!.customer_group_id != null) customerData!!.customer_group_id!!.toString()
                        else "",
                        customerData!!.customer_code,
                        customerData!!.userDetail.firstname,
                        "0",
                        cashType, "Invoice", "Invoice", "0", "0",
                        "Other", "0", "0", "1", "0", "", ""
                    )

                    // if (customerData!!.customer_type_id == "2") {
                    //set Order Last Number
                    if (mCollNo.isNotEmpty())
                        mPreferenceManager!!.setString(AppConstants.COLLECTION_LAST, mCollNo)
                    //}
                }

            }

        } else {
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
                } else if (mPreferenceManager!!.getFTokenId()!! == "4") {
                    var routeCode =
                        mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode

                    collNo = "D${routeCode}C" +
                            Utils.getLastIndexNew(
                                mPreferenceManager!!.getString(AppConstants.COLLECTION_LAST)
                                    .toString(),
                                "collection"
                            )
                } else if (mPreferenceManager!!.getFTokenId()!! == "4") {
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


                    collNo = "P${routeCode}C" +
                            Utils.getLastIndexNew(
                                mPreferenceManager!!.getString(AppConstants.COLLECTION_LAST)
                                    .toString(),
                                "collection"
                            )
                }
//            collNo =
//                Utils.getLastIndexNew(
//                    mPreferenceManager!!.getString(AppConstants.COLLECTION_LAST).toString(),
//                    "collection"
//                )

            } else {
                cashType = "0"
            }

            db!!.addCollection(
                invoiceNo,
                collNo,
                customerData!!.userDetail.id.toString(),
                dueDate,
                orderDate,
                roundTo.toString(),
                roundTo.toString(),
                lobCode,
                if (customerData!!.customer_group_id != null) customerData!!.customer_group_id!!.toString()
                else "",
                customerData!!.customer_code,
                customerData!!.userDetail.firstname,
                "0",
                cashType, "Invoice", "Invoice", "0", "0", "Other",
                "",
                "", "1", "0", "", ""
            )

            if (customerData!!.customer_type_id == "2" || customerData!!.customer_type_id == "3") {
                //set Order Last Number
                if (mCollNo.isNotEmpty())
                    mPreferenceManager!!.setString(AppConstants.COLLECTION_LAST, mCollNo)
            }
        }

//        if (salesman_route_print_enable == "0" || customer_print_enable == "0") {
//            callPaymentScreen()
//        } else {
        Utils.dialogCustomerPrint(mActivity!!, "Customer", object : OnSearchableDialog {

            override fun onItemSelected(selection: String?) {

                if (selection.equals("Customer", ignoreCase = true)) {
                    try {
                        jsonArray!!.getJSONArray(0).getJSONObject(0).getJSONObject("mainArr")
                            .put("mType", "Customer")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    PrintLog.mPrintService = mPrintService
                    callDevice()

//                        val `object` = PrintLog(
//                            this@OrderConfirmActivity,
//                            this@OrderConfirmActivity
//                        )
//                        `object`.execute("", jsonArray)

                } else {
                    callPaymentScreen()
                }
            }
        })
        // }
    }

    private fun callPaymentScreen() {
        calculateOrderPrice()
        if (customerData!!.customer_type_id == "2") {
            if (type == "delivery") {

                var mInvNo = ""
                if (customerData!!.channel_id == "1") {
                    mInvNo = invoiceNo
                } else {
                    mInvNo = deliveryNo!!
                }
                if (db!!.checkInvoiceExist(mInvNo)) {
                    db!!.updateDelvieryCompleted(deliveryNo!!)
                    db!!.updateDelvieryItemDeletePost(deliveryNo!!)
                    DeliveryDetailActivity.isDeliveryDone = true

                    var mDetail = db!!.getDeliveryOrderDetail(deliveryId!!)
                    login(
                        MyApplication.LOCUS_CLIENT,
                        mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.salesman_code,
                        mPreferenceManager!!.getString(AppConstants.LOCUS_PASSWORD).toString(),
                        mDetail["OrderNo"]!!
                    )

                    if (customerData!!.channel_id == "1") {
                        var mDetail = db!!.getDeliveryOrderDetail(deliveryId!!)

                        var mOrderSource = mDetail["OrderSource"]
                        var mPaymentType = mDetail["PaymentMethod"]
                        var MpaymetRef = mDetail["PaymentRefrence"]
                        Log.e("Source", mOrderSource!!)
                        Log.e("Payment Method", mPaymentType!!)
                        if (mPaymentType != "Prepaid") {

                            if (isCouponRedeem != "1") {

                                if (mIsIcash == "Yes") {
                                    if (mRemainAmt.toDouble() > 0.30) {
                                        PaymentDetailActivity.isOpen = false
                                        if (!PaymentDetailActivity.isOpen) {
                                            PaymentDetailActivity.isOpen = true
                                            val `in` =
                                                Intent(
                                                    this@OrderConfirmActivity,
                                                    PaymentDetailActivity::class.java
                                                )
                                            `in`.putExtra("customer", customerCode.toString())
                                            `in`.putExtra("amount", "" + mRemainAmt)
                                            `in`.putExtra("invDate", orderDate)
                                            `in`.putExtra("dueDate", dueDate)
                                            `in`.putExtra("invNo", invoiceNo)
                                            `in`.putExtra("collNum", collNo)
                                            `in`.putExtra("amtClear", "0")
                                            `in`.putExtra("invAmt", "" + roundTo)
                                            `in`.putExtra("paymentTerm", "0")
                                            `in`.putExtra("Type", "normal")
                                            `in`.putExtra("Source", mOrderSource)
                                            `in`.putExtra("deliveryNo", deliveryId!!)
                                            `in`.putExtra("accountNumber", accountNumber)
                                            `in`.putExtra("mobile", mMobile)
                                            `in`.putExtra("email", mEmail)
                                            if (isDispenserOrder) {
                                                `in`.putExtra("isDispenser", "1")
                                            } else {
                                                `in`.putExtra("isDispenser", "0")
                                            }
                                            `in`.putExtra("isCoupon", isCouponSales)
                                            `in`.putExtra("iCashBalance", miCashBalance.toString())
                                            `in`.putExtra(
                                                "accountBalance",
                                                mCustomerBalance.toString()
                                            )
                                            startActivity(`in`)
                                        }
                                    }
                                } else {
                                    PaymentDetailActivity.isOpen = false
                                    if (!PaymentDetailActivity.isOpen) {
                                        PaymentDetailActivity.isOpen = true
                                        val `in` =
                                            Intent(
                                                this@OrderConfirmActivity,
                                                PaymentDetailActivity::class.java
                                            )
                                        `in`.putExtra("customer", customerCode.toString())
                                        `in`.putExtra("amount", "" + roundTo)
                                        `in`.putExtra("invDate", orderDate)
                                        `in`.putExtra("dueDate", dueDate)
                                        `in`.putExtra("invNo", invoiceNo)
                                        `in`.putExtra("collNum", collNo)
                                        `in`.putExtra("amtClear", "0")
                                        `in`.putExtra("invAmt", "" + roundTo)
                                        `in`.putExtra("paymentTerm", "0")
                                        `in`.putExtra("Type", "normal")
                                        `in`.putExtra("Source", mOrderSource)
                                        `in`.putExtra("deliveryNo", deliveryId!!)
                                        `in`.putExtra("accountNumber", accountNumber)
                                        `in`.putExtra("mobile", mMobile)
                                        `in`.putExtra("email", mEmail)
                                        if (isDispenserOrder) {
                                            `in`.putExtra("isDispenser", "1")
                                        } else {
                                            `in`.putExtra("isDispenser", "0")
                                        }
                                        `in`.putExtra("isCoupon", isCouponSales)
                                        `in`.putExtra("iCashBalance", miCashBalance.toString())
                                        `in`.putExtra("accountBalance", mCustomerBalance.toString())
                                        startActivity(`in`)
                                    }
                                }

                            }
                        }
                    } else {
                        if (isCouponRedeem != "1") {
                            PaymentDetailActivity.isOpen = false
                            if (!PaymentDetailActivity.isOpen) {
                                PaymentDetailActivity.isOpen = true
                                val `in` =
                                    Intent(
                                        this@OrderConfirmActivity,
                                        PaymentDetailActivity::class.java
                                    )
                                `in`.putExtra("customer", customerCode.toString())
                                `in`.putExtra("amount", "" + roundTo)
                                `in`.putExtra("invDate", orderDate)
                                `in`.putExtra("dueDate", dueDate)
                                `in`.putExtra("invNo", invoiceNo)
                                `in`.putExtra("collNum", collNo)
                                `in`.putExtra("amtClear", "0")
                                `in`.putExtra("invAmt", "" + roundTo)
                                `in`.putExtra("paymentTerm", "0")
                                `in`.putExtra("Type", "normal")
                                `in`.putExtra("isCoupon", isCouponSales)
                                `in`.putExtra("deliveryNo", deliveryId!!)
                                `in`.putExtra("accountNumber", accountNumber)
                                `in`.putExtra("mobile", mMobile)
                                `in`.putExtra("email", mEmail)
                                if (isDispenserOrder) {
                                    `in`.putExtra("isDispenser", "1")
                                } else {
                                    `in`.putExtra("isDispenser", "0")
                                }
                                `in`.putExtra("iCashBalance", miCashBalance.toString())
                                `in`.putExtra("accountBalance", mCustomerBalance.toString())
                                startActivity(`in`)
                            }
                        }
                    }

                    finish()
                }
            } else {
                SalesActivity.isSaleCreate = true
                PaymentDetailActivity.isOpen = false

                if (isCouponRedeem != "1") {
                    if (!PaymentDetailActivity.isOpen) {
                        PaymentDetailActivity.isOpen = true
                        val `in` =
                            Intent(this@OrderConfirmActivity, PaymentDetailActivity::class.java)
                        `in`.putExtra("customer", customerCode.toString())
                        `in`.putExtra("amount", "" + roundTo)
                        `in`.putExtra("invDate", orderDate)
                        `in`.putExtra("dueDate", dueDate)
                        `in`.putExtra("invNo", invoiceNo)
                        `in`.putExtra("collNum", collNo)
                        `in`.putExtra("amtClear", "0")
                        `in`.putExtra("invAmt", "" + roundTo)
                        `in`.putExtra("paymentTerm", "0")
                        `in`.putExtra("Type", "normal")
                        `in`.putExtra("isCoupon", isCouponSales)
                        `in`.putExtra("deliveryNo", deliveryId!!)
                        `in`.putExtra("accountNumber", accountNumber)
                        `in`.putExtra("mobile", mMobile)
                        `in`.putExtra("email", mEmail)
                        if (isDispenserOrder) {
                            `in`.putExtra("isDispenser", "1")
                        } else {
                            `in`.putExtra("isDispenser", "0")
                        }
                        `in`.putExtra("iCashBalance", miCashBalance.toString())
                        `in`.putExtra("onBalance", mCustomerBalance.toString())
                        startActivity(`in`)
                    }
                }

                finish()
            }
        } else {
            if (type == "delivery") {

                var mInvNo = ""
                if (customerData!!.channel_id == "1") {
                    mInvNo = invoiceNo
                } else {
                    mInvNo = deliveryNo!!
                }

                if (db!!.checkInvoiceExist(mInvNo)) {
                    db!!.updateDelvieryCompleted(deliveryNo!!)
                    db!!.updateDelvieryItemDeletePost(deliveryNo!!)
                    DeliveryDetailActivity.isDeliveryDone = true
                    var mDetail = db!!.getDeliveryOrderDetail(deliveryId!!)
                    login(
                        MyApplication.LOCUS_CLIENT,
                        mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.salesman_code,
                        mPreferenceManager!!.getString(AppConstants.LOCUS_PASSWORD).toString(),
                        mDetail["OrderNo"]!!
                    )
                    finish()
                }

            } else {
                SalesActivity.isSaleCreate = true
                finish()
            }
        }
    }

    private fun insertOrder() {

        var mOrderNo = ""
        if (orderNo.isEmpty()) {

            mOrderNo =
                Utils.getLastIndex(
                    mPreferenceManager!!.getString(AppConstants.ORDER_LAST).toString(), "order"
                )

            if (mPreferenceManager!!.getFTokenId()!! == "2") {
                var routeCode =
                    mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode

                orderNo = "V${routeCode}OR" +
                        Utils.getLastIndex(
                            mPreferenceManager!!.getString(AppConstants.ORDER_LAST).toString(),
                            "order"
                        )
            } else if (mPreferenceManager!!.getFTokenId()!! == "1") {
                var routeCode =
                    mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode

                orderNo = "P${routeCode}OR" +
                        Utils.getLastIndex(
                            mPreferenceManager!!.getString(AppConstants.ORDER_LAST).toString(),
                            "order"
                        )
            } else if (mPreferenceManager!!.getFTokenId()!! == "3") {
                var routeCode =
                    customerData!!.user_id.toString()

                orderNo = "HH${routeCode}OR" +
                        Utils.getLastIndexNew(
                            mPreferenceManager!!.getString(AppConstants.ORDER_LAST)
                                .toString(),
                            "order"
                        )

            } else if (mPreferenceManager!!.getFTokenId()!! == "6") {
                var routeCode =
                    customerData!!.user_id.toString()
                orderNo = "HH${routeCode}OR" +
                        Utils.getLastIndexNew(
                            mPreferenceManager!!.getString(AppConstants.ORDER_LAST)
                                .toString(),
                            "order"
                        )

            } else {
                orderNo =
                    Utils.getLastIndex(
                        mPreferenceManager!!.getString(AppConstants.ORDER_LAST).toString(), "order"
                    )
            }

        }

        var mOrder = Order()
        mOrder.addressId = mAddressId
        mOrder.orderNo = orderNo
        mOrder.orderType = orderTypeId
        mOrder.orderDate = orderDate
        mOrder.deliveryDate = deliveryDate
        mOrder.dueDate = dueDate
        mOrder.customerId = customerCode.toString()
        mOrder.gross = grossAmt.toString()
        mOrder.discount = itemDiscountAmt.toString()
        mOrder.net = netAmt.toString()
        mOrder.vat = vatAmt.toString()
        mOrder.total = totalAmt.toString()
        mOrder.excAmt = exciseAmt.toString()
        mOrder.comment = orderComment.toString()
        mOrder.lob_id = lobCode
        mOrder.payemntId = customerData!!.payment_term_id.toString()

        if (isAccept == "1") {

            var mDelete = db!!.deleteOrderItem(orderNo)

            db!!.updateFinalOrder(mOrder = mOrder, mItemList = orderItemsList!!)
            OrderPostActivity.isOrderCreated = true
            db!!.markForOrderPost(orderNo)

        } else {

            //Insert Order
            db!!.insertOrder(
                mOrder = mOrder,
                mItemList = orderItemsList!!,
                isAccept = "0",
                mLPO = customerLPO
            )

            //set Order Last Number
            mPreferenceManager!!.setString(AppConstants.ORDER_LAST, mOrderNo)
            OrderRequestActivity.isOrderCreated = true

            for (mData in orderItemsList!!) {

                var mQty = 0.0
                for (mUOM in mData.itemSelectUOM) {
                    mQty += Utils.getUOMLowerQty(mUOM.uomUPC, mUOM.uomQty).toDouble()

                    if (db!!.checkSalesRestItemExist(mData.id.toString())) {
                        var existQty =
                            db!!.getSalesExistItemQty(
                                mData.id.toString()
                            ).toDouble()
                        var mUpdateQty = existQty - mUOM.uomQty.toDouble()

                        db!!.updateSalesExistRestSaleQty(
                            mData.id.toString(),
                            mUpdateQty.toString()
                        )
                    }
                }
            }
        }

        //get Print Data
        var data: JSONObject? = null
        try {
            jsonArray = createPrintOrderData(orderNo)
            data = JSONObject()
            data.put("data", jsonArray as JSONArray?)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }


        val transaction = Transaction()
        transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_OREDER_CREATED
        transaction.tr_date_time = Utils.getCurrentDate() + " " + Utils.getCurrentTime()
        transaction.tr_customer_num = customerCode
        transaction.tr_customer_name =
            customerData!!.userDetail!!.firstname + " " + customerData!!.userDetail!!.lastname
        transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
        transaction.tr_invoice_id = ""
        transaction.tr_order_id = orderNo
        transaction.tr_collection_id = ""
        transaction.tr_pyament_id = ""
        transaction.tr_printData = data.toString()
        transaction.tr_is_posted = "No"
        db!!.insertTransaction(transaction)

        db!!.updateCustomerTransaction(customerData!!.userDetail!!.id.toString(), "order")
        if (salesman_route_print_enable == "0") {
            finish()
        } else {
            Utils.dialogPrint(mActivity!!, object : OnSearchableDialog {

                override fun onItemSelected(selection: String?) {

                    if (selection.equals("yes", ignoreCase = true)) {

                        PrintLog.mPrintService = mPrintService
                        callDevice()

//                        val `object` = PrintLog(
//                            this@OrderConfirmActivity,
//                            this@OrderConfirmActivity
//                        )
//                        `object`.execute("", jsonArray)
                    } else {
                        finish()
                    }
                }
            })
        }


    }

    private fun insertLoadRequest() {

        /* orderNo =
             Utils.getLastIndex(
                 mPreferenceManager!!.getString(AppConstants.ORDER_LAST).toString(),
                 "order"
             )*/

        var mOrderNo = ""
        if (orderNo.isEmpty()) {

            mOrderNo =
                Utils.getLastIndexNew(
                    mPreferenceManager!!.getString(AppConstants.LOAD_LAST).toString(),
                    "load"
                )

            if (mPreferenceManager!!.getFTokenId()!! == "2") {
                var routeCode =
                    mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode

                orderNo = "V${routeCode}LO" +
                        Utils.getLastIndexNew(
                            mPreferenceManager!!.getString(AppConstants.LOAD_LAST).toString(),
                            "load"
                        )
            } else if (mPreferenceManager!!.getFTokenId()!! == "3") {
                var routeCode =
                    mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode

                orderNo = "H${routeCode}LO" +
                        Utils.getLastIndexNew(
                            mPreferenceManager!!.getString(AppConstants.LOAD_LAST).toString(),
                            "load"
                        )
            } else {
                orderNo =
                    Utils.getLastIndexNew(
                        mPreferenceManager!!.getString(AppConstants.LOAD_LAST).toString(),
                        "load"
                    )
            }

        }


        var mOrder = Order()
        mOrder.orderNo = orderNo
        mOrder.orderType = orderTypeId
        mOrder.orderDate = orderDate
        mOrder.deliveryDate = deliveryDate
        mOrder.dueDate = dueDate
        mOrder.customerId = customerCode.toString()
        mOrder.gross = grossAmt.toString()
        mOrder.discount = discountAmt.toString()
        mOrder.net = netAmt.toString()
        mOrder.vat = vatAmt.toString()
        mOrder.total = totalAmt.toString()
        mOrder.comment = orderComment.toString()
        mOrder.payemntId = "1"

        //Insert Load Request Order
        db!!.insertLoadRequestOrder(mOrder = mOrder, mItemList = orderItemsList!!)

        //set Order Last Number
        mPreferenceManager!!.setString(AppConstants.LOAD_LAST, mOrderNo)

        //get Print Data
        var data: JSONObject? = null
        try {
            jsonArray = createPrintLoadRequestData(orderNo)
            data = JSONObject()
            data.put("data", jsonArray as JSONArray?)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val transaction = Transaction()
        transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_LOAD_CREATE
        transaction.tr_date_time = Utils.getCurrentDate() + " " + Utils.getCurrentTime()
        transaction.tr_customer_num = customerCode
        transaction.tr_customer_name = " Depot Order"
        transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
        transaction.tr_invoice_id = ""
        transaction.tr_order_id = orderNo
        transaction.tr_collection_id = ""
        transaction.tr_pyament_id = ""
        transaction.tr_printData = data.toString()
        transaction.tr_is_posted = "No"
        db!!.insertTransaction(transaction)

        OrderRequestActivity.isOrderCreated = true

        if (salesman_route_print_enable == "0") {
            finish()
        } else {
            Utils.dialogPrint(mActivity!!, object : OnSearchableDialog {

                override fun onItemSelected(selection: String?) {

                    if (selection.equals("yes", ignoreCase = true)) {

                        PrintLog.mPrintService = mPrintService
                        callDevice()


//                        val `object` = PrintLog(
//                            this@OrderConfirmActivity,
//                            this@OrderConfirmActivity
//                        )
//                        `object`.execute("", jsonArray)
                    } else {
                        finish()
                    }
                }
            })
        }

    }

    fun callDevice() {

        if (mPrintService!!.getState() !== BluetoothPrintService.STATE_CONNECTED) {
            // Launch the DeviceListActivity to see devices and do scan
            val serverIntent: Intent = Intent(this, DeviceListActivity::class.java)
            startActivityForResult(
                serverIntent,
                REQUEST_CONNECT_DEVICE_SECURE
            )
        } else {
            Log.e("Call FUnc", "yes");
            val `object` = PrintLog(
                this@OrderConfirmActivity,
                this@OrderConfirmActivity
            )
            `object`.execute("", jsonArray)
        }

    }

    //Return
    private fun insertReturn() {


        if (isRequest == "1") {

            db!!.markForRequestReturnPost(orderNo)
            //db!!.markForRequestReturnItemPost(orderNo)

            db!!.insertReturnUpdate(
                mOrder = orderNo,
                mItemList = orderItemsList!!
            )

            insertReturnOrder(orderNo)

            OrderPostActivity.isOrderCreated = true

        } else {
            var mOrderNo = ""
            if (orderNo.isEmpty()) {

                mOrderNo =
                    Utils.getLastIndexNew(
                        mPreferenceManager!!.getString(AppConstants.RETURN_LAST).toString(),
                        "return"
                    )

                if (mPreferenceManager!!.getFTokenId()!! == "2") {
                    var routeCode =
                        mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode

                    orderNo = "V${routeCode}RO" +
                            Utils.getLastIndexNew(
                                mPreferenceManager!!.getString(AppConstants.RETURN_LAST).toString(),
                                "return"
                            )
                } else if (mPreferenceManager!!.getFTokenId()!! == "1") {
                    var routeCode =
                        mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode

                    orderNo = "P${routeCode}RO" +
                            Utils.getLastIndexNew(
                                mPreferenceManager!!.getString(AppConstants.RETURN_LAST).toString(),
                                "return"
                            )
                } else if (mPreferenceManager!!.getFTokenId()!! == "4") {
                    var routeCode =
                        mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode

                    orderNo = "D${routeCode}RO" +
                            Utils.getLastIndexNew(
                                mPreferenceManager!!.getString(AppConstants.RETURN_LAST).toString(),
                                "return"
                            )
                } else {
                    orderNo =
                        Utils.getLastIndexNew(
                            mPreferenceManager!!.getString(AppConstants.RETURN_LAST).toString(),
                            "return"
                        )
                }

            }


            var mOrder = Order()
            mOrder.orderNo = orderNo
            mOrder.orderType = type
            mOrder.orderDate = orderDate
            mOrder.invoiceId = invoiceId
            mOrder.reason = orderItemsList!![0].itemSelectUOM!![0].reason
            mOrder.customerId = customerCode.toString()
            mOrder.gross = grossAmt.toString()
            mOrder.discount = discountAmt.toString()
            mOrder.excAmt = exciseAmt.toString()
            mOrder.net = netAmt.toString()
            mOrder.vat = vatAmt.toString()
            mOrder.total = totalAmt.toString()
            mOrder.comment = orderComment.toString()
            mOrder.lob_id = orderItemsList!![0].lobId
            mOrder.payemntId = customerData!!.payment_term_id.toString()
            mOrder.isExchange = "0"
            mOrder.exchangeNo = ""

            if (isWithoutReference == "1") {
                mOrder.invoiceNo = invoiceNA
                mOrder.invoiceAmt = invoiceAmt
            } else {
                mOrder.invoiceNo = ""
                mOrder.invoiceAmt = ""
            }

            if (mPreferenceManager!!.getString(AppConstants.ROLE) == "1") {
                mOrder.isRequested = "1"
                mOrder.uuid = ""
            } else {
                mOrder.isRequested = "0"
                mOrder.uuid = ""
            }
            mOrder.returnImage = "data:image/jpeg;base64," + encodedImage

            if (encodedImage2 == "") {
                mOrder.returnImage2 = ""
            } else {
                mOrder.returnImage2 = "data:image/jpeg;base64," + encodedImage2
            }
            if (encodedImage3 == "") {
                mOrder.returnImage3 = ""
            } else {
                mOrder.returnImage3 = "data:image/jpeg;base64," + encodedImage3
            }

            db!!.insertCustomerReturn(
                mOrder = mOrder,
                mItemList = orderItemsList!!,
                isAccept = "0",
                isWithOutRef = isWithoutReference
            )
            //set Order Last Number
            mPreferenceManager!!.setString(AppConstants.RETURN_LAST, mOrderNo)


//            if (type == "goodReturn") {
//                for (mData in orderItemsList!!) {
//
//                    var mQty = 0.0
//                    for (mUOM in mData.itemSelectUOM) {
//                        mQty += Utils.getUOMLowerQty(mUOM.uomUPC, mUOM.uomQty).toDouble()
//                    }
//
//                    var existQty =
//                        db!!.getVanStockItemQty(
//                            mData.id.toString(),
//                            mData.vanUOM,
//                            "Remaining"
//                        )
//                            .toDouble()
//
//                    var mUpdateQty = existQty + mQty
//
//                    db!!.updateVanStockItemQty(
//                        mData.id.toString(),
//                        mUpdateQty.toString()
//                    )
//
//                }
//            }


            insertReturnOrder(orderNo)

            if (invoiceId.isNotEmpty()) {
                ReturnRefActivity.isReturnCreated = true
            } else {
                OrderRequestActivity.isOrderCreated = true
            }
        }

    }

    private fun insertReturnOrder(orderNo: String) {
        //get Print Data
        var data: JSONObject? = null
        try {
            jsonArray = createPrintReturnData(this.orderNo, "")
            data = JSONObject()
            data.put("data", jsonArray as JSONArray?)
            Log.e("Return Print", jsonArray.toString())
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        if (isAccept == "1") {
            if (isRequest.isNotEmpty()) {
                val transaction = Transaction()
                transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_RETURN_REQUEST
                transaction.tr_date_time = Utils.getCurrentDate() + " " + Utils.getCurrentTime()
                transaction.tr_customer_num = customerCode
                transaction.tr_customer_name =
                    customerData!!.userDetail!!.firstname + " " + customerData!!.userDetail!!.lastname
                transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
                transaction.tr_invoice_id = ""
                transaction.tr_order_id = isRequest
                transaction.tr_collection_id = ""
                transaction.tr_pyament_id = this.orderNo
                transaction.tr_printData = data.toString()
                transaction.tr_is_posted = "No"
                db!!.insertTransaction(transaction)
            } else {
                val transaction = Transaction()
                transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_RETURN_CREATED
                transaction.tr_date_time = Utils.getCurrentDate() + " " + Utils.getCurrentTime()
                transaction.tr_customer_num = customerCode
                transaction.tr_customer_name =
                    customerData!!.userDetail!!.firstname + " " + customerData!!.userDetail!!.lastname
                transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
                transaction.tr_invoice_id = ""
                transaction.tr_order_id = this.orderNo
                transaction.tr_collection_id = ""
                transaction.tr_pyament_id = ""
                transaction.tr_printData = data.toString()
                transaction.tr_is_posted = "No"
                db!!.insertTransaction(transaction)
            }
        } else {
            val transaction = Transaction()
            transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_RETURN_CREATED
            transaction.tr_date_time = Utils.getCurrentDate() + " " + Utils.getCurrentTime()
            transaction.tr_customer_num = customerCode
            transaction.tr_customer_name =
                customerData!!.userDetail!!.firstname + " " + customerData!!.userDetail!!.lastname
            transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
            transaction.tr_invoice_id = ""
            transaction.tr_order_id = this.orderNo
            transaction.tr_collection_id = ""
            transaction.tr_pyament_id = ""
            transaction.tr_printData = data.toString()
            transaction.tr_is_posted = "No"
            db!!.insertTransaction(transaction)
        }


        db!!.updateCustomerTransaction(customerData!!.userDetail!!.id.toString(), "return")

        if (!isRequest.isNotEmpty()) {
            //Add Collection
            collNo =
                Utils.getLastIndexNew(
                    mPreferenceManager!!.getString(AppConstants.COLLECTION_LAST).toString(),
                    "collection"
                )

            var cashType = "0"
            if (customerData!!.credit_days == "0") {
                cashType = "1"
            } else {
                cashType = "0"
            }

            db!!.addCollection(
                this.orderNo,
                collNo,
                customerData!!.userDetail.id.toString(),
                dueDate,
                orderDate,
                totalAmt.toString(),
                totalAmt.toString(),
                lobCode,
                if (customerData!!.customer_group_id != null) customerData!!.customer_group_id!!.toString()
                else "",
                customerData!!.customer_code,
                customerData!!.userDetail.firstname,
                "0",
                cashType,
                "Credit", "Credit", "0", "0", "Other", "",
                "", "1", "0", "", ""
            )

            //set Order Last Number
            mPreferenceManager!!.setString(AppConstants.COLLECTION_LAST, collNo)

            Utils.dialogCustomerPrint(mActivity!!, "Customer", object : OnSearchableDialog {

                override fun onItemSelected(selection: String?) {

                    if (selection.equals("Customer", ignoreCase = true)) {
                        try {
                            jsonArray!!.getJSONArray(0).getJSONObject(0).getJSONObject("mainArr")
                                .put("mType", "Customer")
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                        PrintLog.mPrintService = mPrintService
                        callDevice()

//                        val `object` = PrintLog(
//                            this@OrderConfirmActivity,
//                            this@OrderConfirmActivity
//                        )
//                        `object`.execute("", jsonArray)
                    } else {
                        finish()
                    }
                }
            })
        } else {
            Utils.dialogCustomerPrint(mActivity!!, "Customer", object : OnSearchableDialog {

                override fun onItemSelected(selection: String?) {

                    if (selection.equals("Customer", ignoreCase = true)) {
                        try {
                            jsonArray!!.getJSONArray(0).getJSONObject(0).getJSONObject("mainArr")
                                .put("mType", "Customer")
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                        PrintLog.mPrintService = mPrintService
                        callDevice()

                    } else {
                        finish()
                    }
                }
            })
        }

    }

    private fun dateDialog() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                baseBinding.toolbarTitle.text = "${dayOfMonth}/${monthOfYear}/${year}"

            },
            year,
            month,
            day
        )

        dpd.show()
    }

    /*****************************************************************************************
     * @ Creating data for print and saving it in DB for future printing
     */
    fun createPrintInvoiceData(invNum: String?, mType: String, mSign: String): JSONArray? {
        val jArr = JSONArray()
        try {
            var totalGrossAmount = 0F
            var totalGrossDiscount = 0F
            var totalExciseAmount = 0F
            var totalDiscount = 0F
            var totalNet = 0F
            var totalVat1 = 0F
            var totalAmount = 0F
            var totalQTy = 0.0
            val jInter = JSONArray()
            val jDict = JSONObject()
            jDict.put(MyApplication.REQUEST, AppConstants.SALES_INVOICE)
            val mainArr = JSONObject()
            var routeCode =
                mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode
            var sCode =
                mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.salesman_code
            // var vanName = routeCode.replace("Zone", "Vansales")
            mainArr.put("ROUTE", routeCode)
            mainArr.put("DOC DATE", Utils.formatDate(Date(), "dd-MM-yyyy"))
            mainArr.put("TIME", Utils.formatTime(Date(), "hh:mm"))
            mainArr.put("SALESMAN_CODE", sCode)
            mainArr.put("SALESMAN", mPreferenceManager!!.getString(AppConstants.DRIVERNAME))
            mainArr.put("CONTACTNO", "-")
            mainArr.put("DOCUMENT NO", invNum) //Load Summary No
            mainArr.put("TRIP START DATE", mPreferenceManager!!.getTripDate())
            mainArr.put("supervisorname", "-")
            mainArr.put("invoicenumber", invNum) //Invoice No
            mainArr.put("exchangeNo", "") //Invoice No
            mainArr.put("mType", mType) //Invoice No
            mainArr.put("mSign", mSign) //Invoice No

            if (customerData!!.channel_id == "1") {
                mainArr.put("isHOD", "1") //Invoice No
            } else {
                mainArr.put("isHOD", "0") //Invoice No
            }

            var isInsta = "0";
            var isExpress = "0";

            if (type == "sales") {
                var mPaentDetail = db!!.getCustomerParentDetail(customerId!!)
                mainArr.put("ParentCode", mPaentDetail["ParentCode"])
                mainArr.put("ParentName", mPaentDetail["Name"])
                mainArr.put("isSales", "1") //Invoice No
                mainArr.put("LPO", "")
                mainArr.put("dType", "")
            } else {
                var mPaentDetail = db!!.getCustomerParentDetail(customerId!!)
                mainArr.put("ParentCode", mPaentDetail["ParentCode"])
                mainArr.put("ParentName", mPaentDetail["Name"])
                var mDetail = db!!.getDeliveryOrderDetail(deliveryId!!)
                var mOrderSource = mDetail["OrderSource"]
                if (mOrderSource == "Express Delivery") {
                    isExpress = "1"
                    mainArr.put("dType", "Express")
                } else if (mOrderSource == "Instashop") {
                    isInsta = "1"
                    mainArr.put("dType", "Instashop")
                } else {
                    mainArr.put("dType", "")
                }
                mainArr.put("isSales", "0") //Invoice No
                mainArr.put("OrderNo", mDetail["OrderNo"]) //Invoice No
                mainArr.put("LPO", mDetail["LPO"])
                mainArr.put("PresellerId", mDetail["PresellerCode"]) //Invoice No
                mainArr.put("PresellerName", mDetail["PresellerName"]) //Invoice No
                mainArr.put("Delivery Date", mDetail["DeliveryDate"]) //Invoice No

            }
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

            if (customerData!!.customer_phone != null) {
                mainArr.put("Mobile", "" + customerData!!.customer_phone)
            } else {
                mainArr.put("Mobile", "")
            }
            mainArr.put("CITY", db!!.getCustomerRegion(customerData!!.user_id.toString()))
            mainArr.put("Country", "UAE")
            if (customerData!!.customer_group_id != null) {
                mainArr.put("GroupId", "" + customerData!!.customer_group_id)
            } else {
                mainArr.put("GroupId", "")
            }
            var trnNo = db!!.getCustomerTRNNo(customerData!!.customer_code)
            mainArr.put("TRN", trnNo)

            mainArr.put("OrderReference", "")
            mainArr.put("CustomerLPO", customerLPO)
            if (customerData!!.customer_type_id == "2") {
                mainArr.put("invoiceType", "CASH") //Invoice No
            } else {
                mainArr.put("invoiceType", "CREDIT") //Invoice No
            }
            mainArr.put("Email", customerData!!.userDetail.email)
            mainArr.put("Cust_Signature", "")
            val TOTAL = JSONArray()
            val HEADERS = JSONArray()
            HEADERS.put("Sr No")
            HEADERS.put("Description")
            HEADERS.put("Unit")
            HEADERS.put("Qty")
            HEADERS.put("Unit Price")
            HEADERS.put("Disc%")
            HEADERS.put("Vat")
            HEADERS.put("Disc Amount")
            HEADERS.put("Value")
            HEADERS.put("Vat Amt")
            HEADERS.put("Price (AED)")

            mainArr.put("HEADERS", HEADERS)

            //SALES INVOICE
            val jData = JSONArray()
            for (i in orderItemsList!!) {

                if (i.majorCategory!! != 2) {
                    for (mUOM in i.itemSelectUOM) {

                        //Add Base UOM
                        val data = JSONArray()

                        if (i.isFree == "0" || i.isFree.isEmpty()) {
                            data.put("" + (jData.length() + 1))
                            data.put(mUOM.itemName) // DESC
                            var mPrimUOM = db!!.getItemPrimaryUOM(mUOM.itemId!!.toString())
                            var mPRMUPC = ""
                            if (mPrimUOM!!.size > 0) {
                                mPRMUPC = mPrimUOM[0].uom.name
                            } else {
                                mPRMUPC = mUOM.uomType
                            }
                            data.put(mPRMUPC) // UOM
                            var mQty = mUOM.uomQty.toFloat()
                            data.put(mUOM.uomQty) //  QTY
                            data.put(mUOM.uomPrice) // Unit Price
                            data.put(Utils.twoDigit((mUOM.grossPrice).toFloat())) // Gross
                            data.put(mUOM.item_excise) // Item Excise
                            //data.put("" + mUOM.grossPrice) //Gross Amount
                            if (mUOM.discountPer.isNotEmpty()) {
                                data.put(mUOM.discountPer) // Discount
                            } else {
                                data.put("0") // Discount
                            }
                            data.put("5") // vat %
                            data.put("" + mUOM.discountAmt) // Discount Amount
                            data.put(Utils.twoDigit((mUOM.netAmt).toFloat())) // Net
                            data.put("" + Utils.twoDigit((mUOM.vatAmt).toFloat())) // Vat Amt
                            data.put("" + Utils.twoDigit((mUOM.totalAmt).toFloat())) // Total
                            data.put("" + i.barcode) // barcode


                            var plu =
                                db!!.getCustomeItemPLU(
                                    customerData!!.user_id.toString(),
                                    i.id.toString()
                                )
                            data.put("" + plu) // barcode

                            if (isBatch == "1") {
                                data.put("" + i.batchNo) // batch
                                data.put("" + i.expiry) // Expiry
                            } else {
                                data.put("") // batch
                                data.put("") // Expiry
                            }

                            jData.put(data)

                            totalQTy += mQty.toFloat()
                        } else {
                            var mQty = mUOM.uomQty.toFloat()
                            var mPrice = db!!.getItemFOCUOMPrice(
                                mUOM.itemId, customerData!!.user_id.toString(), mUOM.uomId,
                                customerData!!.channel_id!!
                            )
                            var mGross = (mPrice.toDouble() * mUOM.uomQty.toDouble())
                            data.put("" + (jData.length() + 1))
                            data.put(mUOM.itemName) // DESC
                            var mPrimUOM = db!!.getItemPrimaryUOM(mUOM.itemId!!.toString())
                            var mPRMUPC = ""
                            if (mPrimUOM!!.size > 0) {
                                mPRMUPC = mPrimUOM[0].uom.name
                            } else {
                                mPRMUPC = mUOM.uomType
                            }
                            data.put(mPRMUPC) // UOM
                            data.put(mUOM.uomQty) //  QTY
                            data.put(mPrice) // Unit Price
                            data.put(Utils.twoDigit(mGross.toFloat())) // Gross
                            data.put(mUOM.item_excise) // Item Excise
                            data.put("100") // Discount
                            data.put("5") // vat %
                            data.put(Utils.twoDigit(mGross.toFloat())) // Discount Amount
                            data.put(Utils.twoDigit((mUOM.netAmt).toFloat())) // Net
                            data.put("" + Utils.twoDigit((mUOM.vatAmt).toFloat())) // Vat Amt
                            data.put("" + Utils.twoDigit((mUOM.totalAmt).toFloat())) // Total
                            data.put("" + i.barcode) // barcode

                            var plu =
                                db!!.getCustomeItemPLU(
                                    customerData!!.user_id.toString(),
                                    i.id.toString()
                                )
                            data.put("" + plu) // barcode
                            if (isBatch == "1") {
                                data.put("" + i.batchNo) // batch
                                data.put("" + i.expiry) // Expiry
                            } else {
                                data.put("") // batch
                                data.put("") // Expiry
                            }
                            jData.put(data)

                            totalQTy += mQty.toFloat()
                        }

                        totalGrossAmount =
                            Utils.twoDigit(totalGrossAmount.plus(Utils.twoDigit(mUOM.grossPrice.toFloat())))
                        totalDiscount =
                            Utils.twoDigit(totalDiscount.plus(Utils.twoDigit(mUOM.discountAmt.toFloat())))
                        totalVat1 =
                            Utils.twoDigit(totalVat1.plus(Utils.twoDigit(mUOM.vatAmt.toFloat())))
                        totalNet =
                            Utils.twoDigit(totalNet.plus(Utils.twoDigit(mUOM.netAmt.toFloat())))
                        totalAmount =
                            Utils.twoDigit(totalAmount.plus(Utils.twoDigit(mUOM.totalAmt.toFloat())))

                        var excisePrice =
                            Utils.twoDigit((mUOM.uomQty.toFloat() * mUOM.item_excise.toFloat()))
                        totalExciseAmount =
                            Utils.twoDigit(totalExciseAmount.plus(excisePrice))

                    }
                }

            }

            val jGrossData = JSONArray()
            val jDisData = JSONArray()
            val jNetData = JSONArray()
            if (isCustDiscount) {
                jGrossData.put("")
                jGrossData.put("Gross Amount (AED)") // DESC
                jGrossData.put("") // UOM
                jGrossData.put("") //  QTY
                jGrossData.put("") // Unit Price
                //data.put("" + mUOM.grossPrice) //Gross Amount
                jGrossData.put("") // Discount
                jGrossData.put("") // vat %
                jGrossData.put("0.00") // Discount Amount
                jGrossData.put("" + totalNet) // Net
                jGrossData.put("" + totalVat1) // Vat Amt
                jGrossData.put("" + totalAmount) // Total

                var vatDscAmt =
                    Utils.twoDigit(Utils.getVatAmt(discountAmt.toDouble(), 5.0).toFloat())
                var totalDis = Utils.twoDigit((discountAmt + vatDscAmt))

                jDisData.put("")
                jDisData.put("Discount Amount (AED)") // DESC
                jDisData.put("") // UOM
                jDisData.put("") //  QTY
                jDisData.put("") // Unit Price
                //data.put("" + mUOM.grossPrice) //Gross Amount
                jDisData.put("") // Discount
                jDisData.put("") // vat %
                jDisData.put("0.00") // Discount Amount
                jDisData.put("" + discountAmt) // Net
                jDisData.put("" + vatDscAmt) // Vat Amt
                jDisData.put("" + totalDis) // Total

                var finalNet = Utils.twoDigit((totalNet - discountAmt))
                var finalVat = Utils.twoDigit((totalVat1 - vatDscAmt))
                var finalAmoount = Utils.twoDigit((totalAmount - totalDis))

                jNetData.put("")
                jNetData.put("Net Amount (AED)") // DESC
                jNetData.put("") // UOM
                jNetData.put("") //  QTY
                jNetData.put("") // Unit Price
                //data.put("" + mUOM.grossPrice) //Gross Amount
                jNetData.put("") // Discount
                jNetData.put("") // vat %
                jNetData.put("0.00") // Discount Amount
                jNetData.put("" + finalNet) // Net
                jNetData.put("" + finalVat) // Vat Amt
                jNetData.put("" + Utils.twoDigit(roundTo)) // Total

                totalAmount = 0F
                totalAmount = Utils.twoDigit(roundTo)

            } else {
                jGrossData.put("")
                jGrossData.put("Gross Amount (AED)") // DESC
                jGrossData.put("") // UOM
                jGrossData.put("") //  QTY
                jGrossData.put("") // Unit Price
                //data.put("" + mUOM.grossPrice) //Gross Amount
                jGrossData.put("") // Discount
                jGrossData.put("") // vat %
                jGrossData.put("" + totalDiscount) // Discount Amount
                jGrossData.put("" + totalNet) // Net
                jGrossData.put("" + totalVat1) // Vat Amt
                jGrossData.put("" + totalAmount) // Total

                jDisData.put("")
                jDisData.put("Discount Amount (AED)") // DESC
                jDisData.put("") // UOM
                jDisData.put("") //  QTY
                jDisData.put("") // Unit Price
                //data.put("" + mUOM.grossPrice) //Gross Amount
                jDisData.put("") // Discount
                jDisData.put("") // vat %
                jDisData.put("" + totalDiscount) // Discount Amount
                jDisData.put("0.00") // Net
                jDisData.put("0.00") // Vat Amt
                jDisData.put("0.00") // Total

                jNetData.put("")
                jNetData.put("Net Amount (AED)") // DESC
                jNetData.put("") // UOM
                jNetData.put("") //  QTY
                jNetData.put("") // Unit Price
                //data.put("" + mUOM.grossPrice) //Gross Amount
                jNetData.put("") // Discount
                jNetData.put("") // vat %
                jNetData.put("" + totalDiscount) // Discount Amount
                jNetData.put("" + totalNet) // Net
                jNetData.put("" + totalVat1) // Vat Amt
                jNetData.put("" + Utils.twoDigit(roundTo)) // Total
            }

            val totalObj = JSONObject()


            if (isExpress == "1") {
                var mCharge = 4.76F
                totalObj.put(
                    "Delivery Charge",
                    Utils.twoDigit(mCharge)
                )
                totalObj.put(
                    "Rewards",
                    Utils.twoDigit(loyalityPoint)
                )
                var finalNet = (totalNet + Utils.twoDigit(mCharge)) - Utils.twoDigit(loyalityPoint)

                var vatDscAmt =
                    Utils.twoDigit(Utils.getVatAmt(finalNet.toDouble(), 5.0).toFloat())
                totalObj.put("VAT", Utils.twoDigit(vatDscAmt))
                totalObj.put("NET", Utils.twoDigit(finalNet))
                totalObj.put("EXCISE", Utils.twoDigit(totalExciseAmount))
                totalObj.put("Gross Total", Utils.twoDigit(totalGrossAmount))
                totalObj.put("Discount", Utils.twoDigit(totalDiscount))
                var mtotalAmount = finalNet + vatDscAmt
                totalObj.put("Total Amount", Utils.twoDigit(mtotalAmount))

            } else if (isInsta == "1") {
                var mCharge = 3.57F
                totalObj.put(
                    "Delivery Charge",
                    Utils.twoDigit(mCharge)
                )
                totalObj.put(
                    "Rewards",
                    Utils.twoDigit(loyalityPoint)
                )
                var finalNet = (totalNet + Utils.twoDigit(mCharge)) - Utils.twoDigit(loyalityPoint)

                var vatDscAmt =
                    Utils.twoDigit(Utils.getVatAmt(finalNet.toDouble(), 5.0).toFloat())
                totalObj.put("VAT", Utils.twoDigit(vatDscAmt))
                totalObj.put("NET", Utils.twoDigit(finalNet))
                totalObj.put("EXCISE", Utils.twoDigit(totalExciseAmount))
                totalObj.put("Gross Total", Utils.twoDigit(totalGrossAmount))
                totalObj.put("Discount", Utils.twoDigit(totalDiscount))
                var mtotalAmount = finalNet + vatDscAmt
                totalObj.put("Total Amount", Utils.twoDigit(mtotalAmount))
            } else {
                totalObj.put(
                    "Delivery Charge",
                    Utils.twoDigit(deliveryChargeAmt)
                )
                totalObj.put(
                    "Rewards",
                    Utils.twoDigit(loyalityPoint)
                )

                var finalNet = (totalNet - Utils.twoDigit(loyalityPoint))

                var vatDscAmt =
                    Utils.twoDigit(Utils.getVatAmt(finalNet.toDouble(), 5.0).toFloat())
                totalObj.put("VAT", Utils.twoDigit(vatDscAmt))
                totalObj.put("NET", Utils.twoDigit(finalNet))
                totalObj.put("EXCISE", Utils.twoDigit(totalExciseAmount))
                totalObj.put("Gross Total", Utils.twoDigit(totalGrossAmount))
                totalObj.put("Discount", Utils.twoDigit(totalDiscount))
                totalAmount = finalNet + vatDscAmt + deliveryChargeAmt
                totalObj.put("Total Amount", Utils.twoDigit(totalAmount))
            }

            totalObj.put("Total Qty", totalQTy)
            totalObj.put(
                "Total Amount(AED)",
                Utils.getCurrencyFormate(this, totalAmount)
            )
            TOTAL.put(totalObj)
            mainArr.put("TOTAL", TOTAL)
            mainArr.put("data", jData)
            mainArr.put("grossTotal", jGrossData)
            mainArr.put("disTotal", jDisData)
            mainArr.put("netTotal", jNetData)
            jDict.put("mainArr", mainArr)
            jInter.put(jDict)
            jArr.put(jInter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jArr
    }

    /*****************************************************************************************
     * @ Creating data for print and saving it in DB for future printing
     */
    fun createPrintOrderData(invNum: String?): JSONArray? {
        val jArr = JSONArray()
        try {
            var totalGrossAmount = 0F
            var totalExciseAmount = 0F
            var totalDiscount = 0F
            var totalNet = 0F
            var totalVat1 = 0F
            var totalAmount = 0F
            val jInter = JSONArray()
            val jDict = JSONObject()
            jDict.put(AppConstants.REQUEST, AppConstants.ORDER)
            val mainArr = JSONObject()
            var routeCode =
                mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode
            var vanName = routeCode.replace("Zone", "Vansales")
            mainArr.put("ROUTE", vanName)
            mainArr.put("DOC DATE", Utils.formatDate(Date(), "dd-MM-yyyy"))
            mainArr.put("TIME", Utils.formatTime(Date(), "hh:mm"))
            mainArr.put("SALESMAN", mPreferenceManager!!.getString(AppConstants.DRIVERNAME))
            mainArr.put("CONTACTNO", "-")
            mainArr.put("DOCUMENT NO", invNum) //Load Summary No
            mainArr.put("TRIP START DATE", mPreferenceManager!!.getTripDate())
            mainArr.put("supervisorname", "-")
            mainArr.put("orderNo", invNum) //Invoice No
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

            var mPaentDetail = db!!.getCustomerParentDetail(customerId!!)
            mainArr.put("ParentCode", mPaentDetail["ParentCode"])
            mainArr.put("ParentName", mPaentDetail["Name"])

            if (customerData!!.customer_phone != null) {
                mainArr.put("Mobile", "" + customerData!!.customer_phone)
            } else {
                mainArr.put("Mobile", "")
            }
            mainArr.put("CITY", db!!.getCustomerRegion(customerData!!.user_id.toString()))
            mainArr.put("Country", "UAE")
            var trnNo = db!!.getCustomerTRNNo(customerData!!.customer_code)
            mainArr.put("TRN", trnNo)
            mainArr.put("LPO", "")
            mainArr.put("OrderReference", "")
            mainArr.put("Email", customerData!!.userDetail.email)
            mainArr.put("Cust_Signature", "")
            if (customerData!!.customer_type_id == "2") {
                mainArr.put("invoiceType", "CASH") //Invoice No
            } else {
                mainArr.put("invoiceType", "CREDIT") //Invoice No
            }
            val TOTAL = JSONArray()
            val HEADERS = JSONArray()
            HEADERS.put("Sr No")
            HEADERS.put("Description")
            HEADERS.put("Unit")
            HEADERS.put("Qty")
            HEADERS.put("Unit Price")
            HEADERS.put("Disc")
            HEADERS.put("Vat")
            HEADERS.put("Disc Amount")
            HEADERS.put("Value")
            HEADERS.put("Vat Amt")
            HEADERS.put("Price (AED)")

            mainArr.put("HEADERS", HEADERS)

            //SALES INVOICE
            val jData = JSONArray()
            for (i in orderItemsList!!) {

                for (mUOM in i.itemSelectUOM) {

                    //Add Base UOM
                    val data = JSONArray()

                    if (i.isFree == "0" || i.isFree.isEmpty()) {
                        data.put("" + (jData.length() + 1))
                        data.put(mUOM.itemName) // DESC
                        data.put(mUOM.uomType) // UOM
                        data.put(mUOM.uomQty) //  QTY
                        data.put(mUOM.uomPrice) // Unit Price
                        data.put(mUOM.grossPrice) // Gross
                        data.put(mUOM.item_excise) // Item Excise
                        //data.put("" + mUOM.grossPrice) //Gross Amount
                        if (mUOM.discountPer.isNotEmpty()) {
                            data.put(mUOM.discountPer) // Discount
                        } else {
                            data.put("0") // Discount
                        }
                        data.put("5") // vat %
                        data.put("" + mUOM.discountAmt) // Discount Amount
                        data.put(mUOM.netAmt) // Net
                        data.put("" + mUOM.vatAmt) // Vat Amt
                        data.put("" + mUOM.totalAmt) // Total
                        data.put("" + i.barcode) // barcode
                        var plu =
                            db!!.getCustomeItemPLU(
                                customerData!!.user_id.toString(),
                                i.id.toString()
                            )
                        data.put("" + plu) // barcode
                        jData.put(data)
                    } else {

                        var mPrice = db!!.getItemFOCUOMPrice(
                            mUOM.itemId, customerData!!.user_id.toString(), mUOM.uomId,
                            customerData!!.channel_id!!
                        )
                        var mGross = (mPrice.toDouble() * mUOM.uomQty.toDouble())
                        data.put("" + (jData.length() + 1))
                        data.put(mUOM.itemName) // DESC
                        data.put(mUOM.uomType) // UOM
                        data.put(mUOM.uomQty) //  QTY
                        data.put(mPrice) // Unit Price
                        data.put(mGross) // Gross
                        data.put(mUOM.item_excise) // Item Excise
                        //data.put("" + mUOM.grossPrice) //Gross Amount
                        data.put("100") // Discount
                        data.put("5") // vat %
                        data.put("" + mGross) // Discount Amount
                        data.put(mUOM.netAmt) // Net
                        data.put("" + mUOM.vatAmt) // Vat Amt
                        data.put("" + mUOM.totalAmt) // Total
                        data.put("" + i.barcode) // barcode
                        var plu =
                            db!!.getCustomeItemPLU(
                                customerData!!.user_id.toString(),
                                i.id.toString()
                            )
                        data.put("" + plu) // barcode
                        jData.put(data)
                    }


                    var excisePrice =
                        Utils.twoDigit((mUOM.uomQty.toFloat() * mUOM.item_excise.toFloat()))

                    totalGrossAmount += mUOM.grossPrice.toFloat()
                    totalDiscount += mUOM.discountAmt.toFloat()
                    totalExciseAmount += excisePrice.toFloat()
                    totalVat1 += mUOM.vatAmt.toFloat()
                    totalNet += mUOM.netAmt.toFloat()
                    totalAmount += mUOM.totalAmt.toFloat()
                }


            }
            val jGrossData = JSONArray()
            val jDisData = JSONArray()
            val jNetData = JSONArray()

            if (isCustDiscount) {
                jGrossData.put("")
                jGrossData.put("Gross Amount (AED)") // DESC
                jGrossData.put("") // UOM
                jGrossData.put("") //  QTY
                jGrossData.put("") // Unit Price
                //data.put("" + mUOM.grossPrice) //Gross Amount
                jGrossData.put("") // Discount
                jGrossData.put("") // vat %
                jGrossData.put("0.00") // Discount Amount
                jGrossData.put("" + totalNet) // Net
                jGrossData.put("" + totalVat1) // Vat Amt
                jGrossData.put("" + totalAmount) // Total


                var vatDscAmt = Utils.getVatAmt(discountAmt.toDouble(), 5.0)
                var totalDis = discountAmt + vatDscAmt

                jDisData.put("")
                jDisData.put("Discount Amount (AED)") // DESC
                jDisData.put("") // UOM
                jDisData.put("") //  QTY
                jDisData.put("") // Unit Price
                //data.put("" + mUOM.grossPrice) //Gross Amount
                jDisData.put("") // Discount
                jDisData.put("") // vat %
                jDisData.put("0.00") // Discount Amount
                jDisData.put("" + discountAmt) // Net
                jDisData.put("" + vatDscAmt) // Vat Amt
                jDisData.put("" + totalDis) // Total

                var finalNet = totalNet - discountAmt
                var finalVat = totalVat1 - vatDscAmt
                var finalAmoount = totalAmount - totalDis

                jNetData.put("")
                jNetData.put("Net Amount (AED)") // DESC
                jNetData.put("") // UOM
                jNetData.put("") //  QTY
                jNetData.put("") // Unit Price
                //data.put("" + mUOM.grossPrice) //Gross Amount
                jNetData.put("") // Discount
                jNetData.put("") // vat %
                jNetData.put("0.00") // Discount Amount
                jNetData.put("" + finalNet) // Net
                jNetData.put("" + finalVat) // Vat Amt
                jNetData.put("" + finalAmoount) // Total

                totalAmount = 0F
                totalAmount = finalAmoount.toFloat()

            } else {
                jGrossData.put("")
                jGrossData.put("Gross Amount (AED)") // DESC
                jGrossData.put("") // UOM
                jGrossData.put("") //  QTY
                jGrossData.put("") // Unit Price
                //data.put("" + mUOM.grossPrice) //Gross Amount
                jGrossData.put("") // Discount
                jGrossData.put("") // vat %
                jGrossData.put("" + totalDiscount) // Discount Amount
                jGrossData.put("" + totalNet) // Net
                jGrossData.put("" + totalVat1) // Vat Amt
                jGrossData.put("" + totalAmount) // Total

                jDisData.put("")
                jDisData.put("Discount Amount (AED)") // DESC
                jDisData.put("") // UOM
                jDisData.put("") //  QTY
                jDisData.put("") // Unit Price
                //data.put("" + mUOM.grossPrice) //Gross Amount
                jDisData.put("") // Discount
                jDisData.put("") // vat %
                jDisData.put("" + totalDiscount) // Discount Amount
                jDisData.put("0.00") // Net
                jDisData.put("0.00") // Vat Amt
                jDisData.put("0.00") // Total

                jNetData.put("")
                jNetData.put("Net Amount (AED)") // DESC
                jNetData.put("") // UOM
                jNetData.put("") //  QTY
                jNetData.put("") // Unit Price
                //data.put("" + mUOM.grossPrice) //Gross Amount
                jNetData.put("") // Discount
                jNetData.put("") // vat %
                jNetData.put("" + totalDiscount) // Discount Amount
                jNetData.put("" + totalNet) // Net
                jNetData.put("" + totalVat1) // Vat Amt
                jNetData.put("" + totalAmount) // Total
            }

            val totalObj = JSONObject()
            totalObj.put("VAT", Utils.twoDigit(totalVat1))
            totalObj.put("NET", Utils.twoDigit(totalNet))
            totalObj.put("EXCISE", Utils.twoDigit(totalExciseAmount))
            totalObj.put("Gross Total", Utils.twoDigit(totalGrossAmount))
            totalObj.put("Discount", Utils.twoDigit(totalDiscount))
            totalObj.put("Total Amount", Utils.twoDigit(totalAmount))
            totalObj.put(
                "Total Amount(AED)",
                Utils.getCurrencyFormate(this, totalAmount)
            )
            TOTAL.put(totalObj)
            mainArr.put("TOTAL", TOTAL)
            mainArr.put("data", jData)
            mainArr.put("grossTotal", jGrossData)
            mainArr.put("disTotal", jDisData)
            mainArr.put("netTotal", jNetData)
            jDict.put("mainArr", mainArr)
            jInter.put(jDict)
            jArr.put(jInter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jArr
    }

    /*****************************************************************************************
     * @ Creating data for print and saving it in DB for future printing
     */
    fun createPrintReturnData(invNum: String?, mType: String): JSONArray? {
        val jArr = JSONArray()
        try {
            var totalGrossAmount = 0F
            var totalDiscount = 0F
            var totalNet = 0F
            var totalVat1 = 0F
            var totalAmount = 0F
            var totalExciseAmount = 0F
            var totalQTy = 0
            val jInter = JSONArray()
            val jDict = JSONObject()
            jDict.put(AppConstants.REQUEST, AppConstants.RETURN_REQUEST)
            val mainArr = JSONObject()
            var routeCode =
                mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode
            var vanName = routeCode.replace("Zone", "Vansales")
            mainArr.put("ROUTE", vanName)
            mainArr.put("DOC DATE", Utils.formatDate(Date(), "dd-MM-yyyy"))
            mainArr.put("TIME", Utils.formatTime(Date(), "hh:mm"))
            mainArr.put("SALESMAN", mPreferenceManager!!.getString(AppConstants.DRIVERNAME))
            mainArr.put("CONTACTNO", "-")
            mainArr.put("DOCUMENT NO", invNum) //Load Summary No
            mainArr.put("TRIP START DATE", mPreferenceManager!!.getTripDate())
            mainArr.put("supervisorname", "-")
            mainArr.put("mType", mType) //Invoice No
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

//            if (customerData!!.customer_city != null) {
//                if (customerData!!.customer_state != null) {
//                    mainArr.put(
//                        "CITY",
//                        "" + customerData!!.customer_city + " " + customerData!!.customer_state
//                    )
//                } else {
//                    mainArr.put("CITY", "" + customerData!!.customer_city)
//                }
//            } else {
//                if (customerData!!.customer_state != null) {
//                    mainArr.put("CITY", customerData!!.customer_state)
//                } else {
//                    mainArr.put("CITY", "")
//                }
//            }

            if (customerData!!.customer_phone != null) {
                mainArr.put("Mobile", "" + customerData!!.customer_phone)
            } else {
                mainArr.put("Mobile", "")
            }
            mainArr.put("CITY", db!!.getCustomerRegion(customerData!!.user_id.toString()))
            mainArr.put("Country", "UAE")
            var trnNo = db!!.getCustomerTRNNo(customerData!!.customer_code)
            mainArr.put("TRN", trnNo)
            mainArr.put("LPO", "")
            mainArr.put("OrderReference", "")
            if (customerData!!.customer_phone != null) {
                mainArr.put("Email", customerData!!.userDetail.email)
            } else {
                mainArr.put("Email", "")
            }

            mainArr.put("Cust_Signature", "")
            val TOTAL = JSONArray()
            val HEADERS = JSONArray()
            HEADERS.put("Sr No")
            HEADERS.put("Description")
            HEADERS.put("Tax Inv.No")
            HEADERS.put("Inv.Amount")
            HEADERS.put("Unit")
            HEADERS.put("Qty")
            HEADERS.put("Unit Price")
            HEADERS.put("Vat %")
            HEADERS.put("Value")
            HEADERS.put("Vat Amt")
            HEADERS.put("Price (AED)")

            mainArr.put("HEADERS", HEADERS)


            val jData = JSONArray()
            for (i in orderItemsList!!) {

                for (mUOM in i.itemSelectUOM) {

                    //Add Base UOM
                    val data = JSONArray()
                    data.put("" + (jData.length() + 1))
                    data.put(mUOM.itemName) // DESC
//                    if (isWithoutReference == "1") {
//                        data.put(invoiceNA) // Inv.No
//                        data.put(invoiceAmt) // Inv Amount
//                    } else {
                    data.put(mUOM.invNo) // Inv.No
                    data.put(mUOM.invAmount) // Inv Amount
                    // }
                    data.put(mUOM.uomType) // UOM
                    var mQty = mUOM.uomQty.toFloat()
                    data.put(mQty) //  QTY
                    data.put(mUOM.uomPrice) // Unit Price
                    data.put(Utils.twoDigit((mUOM.grossPrice).toFloat())) // Gross
                    data.put(mUOM.item_excise) // Item Excise
                    data.put("5") // vat %
//                    data.put("" + mUOM.grossPrice) //Gross Amount
//                    data.put("" + mUOM.discountAmt) // Discount
                    data.put(mUOM.netAmt) // Net
                    data.put("" + mUOM.vatAmt) // Vat Amt
                    data.put("" + mUOM.totalAmt) // Total
                    data.put("" + i.barcode) // barcode
                    var plu =
                        db!!.getCustomeItemPLU(
                            customerData!!.user_id.toString(),
                            i.id.toString()
                        )
                    data.put("" + plu) // barcode

                    if (mQty > 0)
                        jData.put(data)


                    totalQTy += mQty.roundToInt()
                    totalGrossAmount =
                        Utils.twoDigit(totalGrossAmount.plus(Utils.twoDigit(mUOM.grossPrice.toFloat())))
                    totalDiscount =
                        Utils.twoDigit(totalDiscount.plus(Utils.twoDigit(mUOM.discountAmt.toFloat())))
                    totalVat1 =
                        Utils.twoDigit(totalVat1.plus(Utils.twoDigit(mUOM.vatAmt.toFloat())))
                    totalNet =
                        Utils.twoDigit(totalNet.plus(Utils.twoDigit(mUOM.netAmt.toFloat())))
                    totalAmount =
                        Utils.twoDigit(totalAmount.plus(Utils.twoDigit(mUOM.totalAmt.toFloat())))

                    var excisePrice =
                        Utils.twoDigit((mUOM.uomQty.toFloat() * mUOM.item_excise.toFloat()))
                    totalExciseAmount =
                        Utils.twoDigit(totalExciseAmount.plus(excisePrice))

                }
            }

            val jNetData = JSONArray()
            jNetData.put("")
            jNetData.put("Net Amount (AED)") // DESC
            jNetData.put("") // Inv.No
            jNetData.put("") //  Inv Amount
            jNetData.put("") // UOM
            jNetData.put("") // QTY
            jNetData.put("") // Unit Price
            jNetData.put("") // vat
            jNetData.put("" + totalNet) // Net
            jNetData.put("" + totalVat1) // Vat Amt
            jNetData.put("" + totalAmount) // Total

            var finalNet = totalNet - totalExciseAmount
            val totalObj = JSONObject()
            totalObj.put("VAT", Utils.twoDigit(totalVat1))
            totalObj.put("NET", Utils.twoDigit(finalNet))
            totalObj.put("EXCISE", Utils.twoDigit(totalExciseAmount))
            totalObj.put("Gross Total", Utils.twoDigit(totalGrossAmount))
            totalObj.put("Discount", Utils.twoDigit(totalDiscount))
            totalObj.put("Total Amount", Utils.twoDigit(totalAmount))
            totalObj.put("Total Qty", "" + totalQTy)
            totalObj.put(
                "Total Amount(AED)",
                Utils.getCurrencyFormate(this, totalAmount)
            )
            TOTAL.put(totalObj)
            mainArr.put("TOTAL", TOTAL)
            mainArr.put("data", jData)
            mainArr.put("netTotal", jNetData)
            jDict.put("mainArr", mainArr)
            jInter.put(jDict)
            jArr.put(jInter)
        } catch (e: Exception) {
            e.printStackTrace()
            Utils.logData(
                this@OrderConfirmActivity,
                e.message
            )
        }
        return jArr
    }


    /*****************************************************************************************
     * @ Creating data for print and saving it in DB for future printing
     */
    fun createPrintLoadRequestData(invNum: String?): JSONArray? {
        val jArr = JSONArray()
        try {
            var totalGrossAmount = 0F
            var totalDiscount = 0F
            var totalNet = 0F
            var totalQty = 0
            var totalExciseAmount = 0F
            var totalVat1 = 0F
            var totalAmount = 0F
            val jInter = JSONArray()
            val jDict = JSONObject()
            jDict.put(AppConstants.REQUEST, AppConstants.LOAD_REQUEST)
            val mainArr = JSONObject()
            var routeCode =
                mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode
            var vanName = routeCode.replace("Zone", "Vansales")
            mainArr.put("ROUTE", vanName)
            mainArr.put("DOC DATE", Utils.formatDate(Date(), "dd-MM-yyyy"))
            mainArr.put("TIME", Utils.formatTime(Date(), "hh:mm"))
            mainArr.put("SALESMAN", mPreferenceManager!!.getString(AppConstants.DRIVERNAME))
            mainArr.put("CONTACTNO", "-")
            mainArr.put("DOCUMENT NO", invNum) //Load Summary No
            mainArr.put("TRIP START DATE", mPreferenceManager!!.getTripDate())
            mainArr.put("supervisorname", "-")
            mainArr.put("orderNo", invNum) //Invoice No
            mainArr.put("LoadDate", deliveryDate) //Invoice No

            val TOTAL = JSONArray()
            val HEADERS = JSONArray()
            HEADERS.put("Sr No")
            HEADERS.put("Description")
            HEADERS.put("Unit")
            HEADERS.put("Qty")
            HEADERS.put("Unit Price")
            HEADERS.put("Vat")
            HEADERS.put("Value")
            HEADERS.put("Vat Amt")
            HEADERS.put("Price (AED)")

            mainArr.put("HEADERS", HEADERS)

            //SALES INVOICE
            val jData = JSONArray()
            for (i in orderItemsList!!) {

                for (mUOM in i.itemSelectUOM) {

                    val data = JSONArray()
                    data.put("" + (jData.length() + 1))
                    data.put(mUOM.itemName) // DESC
                    data.put(mUOM.uomType) // UOM
                    var mQty = mUOM.uomQty.toFloat()
                    data.put(mQty.roundToInt()) //  QTY
                    data.put(mUOM.uomPrice) // Unit Price
                    data.put(mUOM.grossPrice) // Gross
                    data.put(mUOM.item_excise) // Item Excise
                    //data.put("" + mUOM.grossPrice) //Gross Amount
                    data.put("0") // Discount
                    data.put("5") // vat %
                    data.put("" + mUOM.discountAmt) // Discount Amount
                    data.put(mUOM.netAmt) // Net
                    data.put("" + mUOM.vatAmt) // Vat Amt
                    data.put("" + mUOM.totalAmt) // Total

                    if (mQty.roundToInt() > 0) {
                        jData.put(data)
                    }

                    totalQty += mQty.roundToInt()
                    var excisePrice =
                        Utils.twoDigit((mUOM.uomQty.toFloat() * mUOM.item_excise.toFloat()))

                    totalGrossAmount += mUOM.grossPrice.toFloat()
                    totalDiscount += mUOM.discountAmt.toFloat()
                    totalExciseAmount += excisePrice.toFloat()
                    totalVat1 += mUOM.vatAmt.toFloat()
                    totalNet += mUOM.netAmt.toFloat()
                    totalAmount += mUOM.totalAmt.toFloat()

                }

            }
            val jNetData = JSONArray()

            jNetData.put("")
            jNetData.put("Net Amount (AED)") // DESC
            jNetData.put("") // UOM
            jNetData.put("") //  QTY
            jNetData.put("") // Unit Price
            jNetData.put("") // vat %
            jNetData.put("" + totalNet) // Net
            jNetData.put("" + totalVat1) // Vat Amt
            jNetData.put("" + totalAmount) // Total

            val totalObj = JSONObject()
            totalObj.put("VAT", Utils.twoDigit(totalVat1))
            totalObj.put("NET", Utils.twoDigit(totalNet))
            totalObj.put("EXCISE", Utils.twoDigit(totalExciseAmount))
            totalObj.put("Gross Total", Utils.twoDigit(totalGrossAmount))
            totalObj.put("Discount", Utils.twoDigit(totalDiscount))
            totalObj.put("Total Amount", Utils.twoDigit(totalAmount))
            totalObj.put("Total Qty", "" + totalQty)
            totalObj.put(
                "Total Amount(AED)",
                Utils.getCurrencyFormate(this, totalAmount)
            )
            TOTAL.put(totalObj)
            mainArr.put("TOTAL", TOTAL)
            mainArr.put("data", jData)
            mainArr.put("netTotal", jNetData)
            jDict.put("mainArr", mainArr)
            jInter.put(jDict)
            jArr.put(jInter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jArr
    }


    private fun StampUploadDialog() {

        val factory = LayoutInflater.from(this)
        val binding: RowReturnImageDialogBinding =
            DataBindingUtil.inflate(
                factory,
                com.engear.vansales.R.layout.row_return_image_dialog,
                null,
                false
            )

        val deleteDialogView: View = binding.root

        val deleteDialog: android.app.AlertDialog = android.app.AlertDialog.Builder(this).create()
        deleteDialog.setView(deleteDialogView)
        deleteDialog.setCancelable(false)

        binding.txtInvoice.text = "Upload Image"

        imgStamp = deleteDialogView.findViewById(com.engear.vansales.R.id.imgStamp)
        imgStamp2 = deleteDialogView.findViewById(com.engear.vansales.R.id.imgStamp2)
        imgStamp3 = deleteDialogView.findViewById(com.engear.vansales.R.id.imgStamp3)

        imgStamp.setOnClickListener {
            str_image_count = "1"
            showPictureDialog(binding.imgStamp)
        }

        imgStamp2.setOnClickListener {
            str_image_count = "2"
            showPictureDialog(imgStamp2)
        }

        imgStamp3.setOnClickListener {
            str_image_count = "3"
            showPictureDialog(imgStamp3)
        }

        binding.btnUpload.setOnClickListener {

            if (encodedImage == "") {
                Toast.makeText(
                    this@OrderConfirmActivity,
                    "Choose Image!!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // getStampUpload(invoiceNo,deleteDialog.dismiss())
                deleteDialog.dismiss()
            }

        }

        deleteDialog.show()
    }


    private fun showPictureDialog(imgStamp: ImageView) {
        val pictureDialog = android.app.AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Capture photo from camera")
        pictureDialog.setItems(
            pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }


    fun choosePhotoFromGallary() {
        if (str_image_count == "1") {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        } else if (str_image_count == "2") {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage2)
        } else if (str_image_count == "3") {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage3)
        }

    }

    private fun takePhotoFromCamera() {
        if (str_image_count == "1") {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA)
        } else if (str_image_count == "2") {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA_SECOND)
        } else if (str_image_count == "3") {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA_Three)
        }

    }

    lateinit var imgStamp: ImageView
    lateinit var imgStamp2: ImageView
    lateinit var imgStamp3: ImageView

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
                this@OrderConfirmActivity,
                this@OrderConfirmActivity
            )
            `object`.execute("", jsonArray)

        }, 3000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            imgStamp.setImageURI(imageUri)
            val imageStream: InputStream? = contentResolver.openInputStream(imageUri!!)
            val selectedImage = BitmapFactory.decodeStream(imageStream)
            encodedImage = encodeImage(selectedImage)
            imgStamp2.visibility = View.VISIBLE
        } else if (resultCode == RESULT_OK && requestCode == CAMERA) {
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            imgStamp.setImageBitmap(thumbnail)
            encodedImage = encodeImage(thumbnail)
            imgStamp2.visibility = View.VISIBLE
        } else if (resultCode == RESULT_OK && requestCode == pickImage2) {
            imageUri = data?.data
            imgStamp2.setImageURI(imageUri)
            val imageStream: InputStream? = contentResolver.openInputStream(imageUri!!)
            val selectedImage = BitmapFactory.decodeStream(imageStream)
            encodedImage2 = encodeImage(selectedImage)
            imgStamp3.visibility = View.VISIBLE
        } else if (resultCode == RESULT_OK && requestCode == CAMERA_SECOND) {
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            imgStamp2.setImageBitmap(thumbnail)
            encodedImage2 = encodeImage(thumbnail)
            imgStamp3.visibility = View.VISIBLE
        } else if (resultCode == RESULT_OK && requestCode == pickImage3) {
            imageUri3 = data?.data
            imgStamp3.setImageURI(imageUri3)
            val imageStream: InputStream? = contentResolver.openInputStream(imageUri3!!)
            val selectedImage = BitmapFactory.decodeStream(imageStream)
            encodedImage3 = encodeImage(selectedImage)
        } else if (resultCode == RESULT_OK && requestCode == CAMERA_Three) {
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            imgStamp3.setImageBitmap(thumbnail)
            encodedImage3 = encodeImage(thumbnail)
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CONNECT_DEVICE_SECURE) {
            connectDevice(data!!, true)
        } else if (requestCode == REQUEST_CONNECT_DEVICE_SECURE && resultCode == RESULT_CANCELED) {
            callOtherActivity(type!!)
        }
    }

    private fun encodeImage(bm: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    override fun onStart() {
        super.onStart()

        // If BT is not on, request that it be enabled.
        // setupPrint() will then be called during onActivityResult
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter!!.isEnabled) {
                val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT)
                // Otherwise, setup the chat session
            } else {
                if (mPrintService == null) setupPrint()
            }
        }

    }

    private fun setupPrint() {
        mPrintService = BluetoothPrintService(mHandler)
        mWoosim = WoosimCustomService(mHandler)
    }

    private val mHandler = MyHandler(this)

    private class MyHandler internal constructor(activity: OrderConfirmActivity) :
        Handler() {
        private val mActivity: WeakReference<OrderConfirmActivity>

        init {
            mActivity = WeakReference<OrderConfirmActivity>(activity)
        }

        override fun handleMessage(msg: Message) {
            val activity: OrderConfirmActivity? = mActivity.get()
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
                        }
                    }

                    binding.txtICashBalance!!.text = miCashBalance.toString()
                    getCurrentOnAccountBalance()
                }

            })
    }

    fun getCurrentOnAccountBalance() {

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
                    getCurrentOnDispenserBalance()

                }

                override fun onResponse(
                    p0: Call<IcashDetail>,
                    response: Response<IcashDetail>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { apiResponse ->

                            if (apiResponse.data.customer_data.balance != null) {
                                val originalString =
                                    (apiResponse.data.customer_data.balance.toDouble())
                                mCustomerBalance = originalString
                            } else {
                                mCustomerBalance = 0.00
                            }
                        }
                    }

                    binding.txtOnBalance!!.text = mCustomerBalance.toString()
                    getCurrentOnDispenserBalance()
                }

            })
    }


    fun getCurrentOnDispenserBalance() {

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
                                mCustomerDispenserBalance = originalString
                            } else {
                                mCustomerDispenserBalance = 0.00
                            }
                        }
                    } else {
                        dismissDialog()
                    }

                    if (isDispenserOrder) {
                        binding.llOnDispencerBalance!!.visibility = View.VISIBLE
                        binding.txtOnDispenscerBalance!!.text = mCustomerDispenserBalance.toString()
                    }


                }

            })
    }

    override fun onResume() {
        super.onResume()

//        if (type != "load") {
//            getCurrentBalance()
//        }

    }

    private fun login(clientId: String, userId: String, password: String, mOrder: String) {

        val sdkReadyCallback = object : LotrSdkReadyCallback {
            override fun onAuthenticated() {
                //Toast.makeText(this@JourneyPlanActivity,"Locus Login Successfully", Toast.LENGTH_LONG).show()
                updateTaskSTatus(mOrder)
                //updateTourSTatus()
            }

            override fun onError(error: LotrSdkError) {
                Toast.makeText(this@OrderConfirmActivity, error.message, Toast.LENGTH_LONG).show()
                error.throwable?.printStackTrace()
            }
        }

        // App can use either user's or client's login based on BuildConfig
        LocusLotrSdk.init(
            this@OrderConfirmActivity,
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
            status = VisitStatusRequest.StatusEnum.COMPLETED,
            checklistValues
        )

        asyncUpdatesManager.addVisitStatusUpdate(taskUpdate)
        asyncUpdatesManager.commit()

    }

    private fun getCustomerAddress() {

        val jObj = JsonObject()
        jObj.addProperty(
            "customer_id",
            customerData!!.user_id.toString()
        )

        showOnlyProgressDialog()

        ApiClient.getClient(this).getCustomerHAddress(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN,
            jObj
        ).enqueue(object : Callback<CustomerHODAddress> {
            override fun onResponse(
                call: Call<CustomerHODAddress>,
                response: Response<CustomerHODAddress>
            ) {

                dismissDialog()
                addressList =
                    ArrayList()

                if (response.body()!!.status!!) {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                    Log.e("deliveries", Gson().toJson(response.body()))
                    if (response.body()!!.data!!.customer_address.isNotEmpty()) {
                        addressList = response.body()!!.data!!.customer_address!!
                    }
                }

            }

            override fun onFailure(call: Call<CustomerHODAddress>, error: Throwable) {
                dismissDialog()
            }
        })
    }

    fun showSelectionPopup() {

        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_address_list, null)

        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recyclerViewPopup)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Select Delivery Address")
            .create()

        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = AddressSelectionAdapter(this, addressList) { selectedItem ->

            mAddressId = selectedItem.id
            binding.txtAddress!!.text = selectedItem.address_1
            binding.txtLandamrk!!.text = "Landmark:" + selectedItem.landmarks.name

            dialog.dismiss()
        }

        dialog.show()
    }

}