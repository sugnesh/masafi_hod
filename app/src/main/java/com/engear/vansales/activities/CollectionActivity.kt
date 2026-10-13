package com.engear.vansales.activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.digitsecure.dunncpplugin.handlers.DunnPay
import com.digitsecure.dunncpplugin.models.DUNNStatusResponse
import com.digitsecure.dunncpplugin.models.DunnBilling
import com.digitsecure.dunncpplugin.models.DunnPayRequest
import com.digitsecure.dunncpplugin.models.DunnPayResponse
import com.digitsecure.dunncpplugin.models.DunnStatusRequest
import com.digitsecure.dunncpplugin.models.ErrorResponse
import com.digitsecure.dunncpplugin.result.IPayResult
import com.digitsecure.dunncpplugin.result.ITransactionStatusResult
import com.engear.vansales.CardFailActivity
import com.engear.vansales.R
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.DeviceListActivity
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityCollectionBinding
import com.engear.vansales.databinding.CollectionAllocateDialogBinding
import com.engear.vansales.databinding.RowCollectionItemBinding
import com.engear.vansales.databinding.RowReturnImageDialogBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.interfaces.OnSearchableDialog
import com.engear.vansales.model.CardFail
import com.engear.vansales.model.CustomerData
import com.engear.vansales.model.IcashDetail
import com.engear.vansales.model.OutstandingData
import com.engear.vansales.model.Transaction
import com.engear.vansales.model.VisitedList
import com.engear.vansales.printer.BluetoothPrintService
import com.engear.vansales.printer.PrintLog
import com.engear.vansales.printer.WoosimCustomService
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.JsonObject
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.opencv.android.OpenCVLoader
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.ref.WeakReference
import java.security.SecureRandom
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class CollectionActivity : BaseActivity(), OnItemClickListener {
    lateinit var binding: ActivityCollectionBinding
    var priorityDialog: AlertDialog? = null
    private var isRecursive = false
    var paymentPriority = "3"
    var collectionType = "3"
    var chequeDate = ""
    var chequeNumber = "1"
    var bankName = ""
    var lobCode = ""
    var paymentDays = ""
    var cashType = "0"
    var customerCode: String? = null
    var collectionNumber: String = ""
    var customerData: CustomerData.DataCustomer? = null
    var visitId: String? = null
    var activityId: String? = null
    var selectedCollection: ArrayList<OutstandingData.CollectionData> = ArrayList()

    private var jsonArray: JSONArray? = null
    var mAdapter: CollectionAdapter? = null
    var isBlockCustomer: String = "0"
    var isOverDue = "0"
    var discountComment = ""
    var discountReason = ""
    var discountAmt = ""
    var customer_print_enable: String = ""
    var salesman_route_print_enable: String = ""
    var str_on_account: String = "0"

    private val pickImage = 100
    private val CAMERA = 200
    private var imageUri: Uri? = null
    var encodedImage: String? = ""
    var encodedImage2: String? = ""

    private val REQUEST_CONNECT_DEVICE_SECURE = 1
    private val REQUEST_ENABLE_BT = 3
    private var mBluetoothAdapter: BluetoothAdapter? = null


    val MESSAGE_DEVICE_NAME = 1
    val MESSAGE_TOAST = 2
    val MESSAGE_READ = 3

    val DEVICE_NAME = "device_name"
    val TOAST = "toast"
    var customerId: String? = "null"
    lateinit var imgStamp: ImageView
    private val random: Random = SecureRandom()
    private var prevRefId = ""
    private var cardRefrenceId = ""
    private var merchantId = ""
    private var terminalId = ""
    private var transactionid = ""
    private var lastFour = ""
    private var cardType = ""
    private var mBankCharge = ""
    private var mBankTaxAmt = ""
    private var is2ndClick = false
    private var image_uri: Uri? = null
    private var binarizedBitmap: Bitmap? = null
    private var iCashAmount = 0f
    private var onAccountAmount = 0f

    open var dunnCPayy: DunnPay? = null

    val arrBank = arrayOf<String>(
        "United Emirates Bank",
        "Abu Dhabi Commercial Bank",
        "Abu Dhabi Islamic Bank",
        "Arab Bank plc",
        "Bank of Baroda",
        "Arab Emirates Investment Bank",
        "Bank of Sharjah",
        "Commercial Bank International",
        "Commercial Bank of Dubai",
        "Dubai Islamic Bank",
        "Emirates Islamic Bank",
        "First Gulf Bank",
        "Habib Bank AG Zurich",
        "HSBC Bank Middle East Limited",
        "Invest Bank",
        "National Bank of Abu Dhabi",
        "Emirates NBD",
        "National Bank of Fujairah",
        "National Bank of Umm Al-Qaiwain",
        "Noor Bank",
        "RAKBANK",
        "Sharjah Islamic Bank",
        "Union National Bank",
        "United Arab Bank",
        "Citibank UAE",
        "Mashreqbank",
        "Standard Chartered Bank",
        "Al Masraf Arab Bank",
        "Arab African International Bank",
        "First Abu Dhabi Bank"
    )

    companion object {

        var isCollectionDone = false
        var collectionList: ArrayList<OutstandingData.CollectionData> = ArrayList()
        var overDueCollection: ArrayList<OutstandingData.CollectionData> = ArrayList()
        var splitEnterAmt = 0F
        var enterAmount = 0F
        var paymentChecked = 2
        var allocatedAmt = 0F
        var clearedAmt = 0F
        var shelfdAmt = 0F
        var invAMt = 0F
        var creditAmt = 0F
        var discAmt = 0F
        var rebateAmt = 0F
        var rebateVatAmt = 0F

        var mActivity: CollectionActivity? = null

        fun callbackFunction(mType: String) {
            if (mType != "Draft") {
                mActivity!!.callOtherActivity()
            }
        }

        fun callAllocated(mData: OutstandingData.CollectionData, position: Int) {
            mActivity!!.allocatedDialog(mData, position)
        }

        fun calculateAllocate() {
            mActivity!!.getAllocatedAmt()
        }

        fun clearAll() {
            collectionList = ArrayList()
            splitEnterAmt = 0F
            enterAmount = 0F
            paymentChecked = 2
            allocatedAmt = 0F
            clearedAmt = 0F
            shelfdAmt = 0F
            invAMt = 0F
            creditAmt = 0F
            discAmt = 0F
            rebateAmt = 0F
            rebateVatAmt = 0F
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCollectionBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@CollectionActivity,
            "On Collection Screen "
        )

        mActivity = this

        init()
        initDunnPay()
    }

    private fun initDunnPay() {
        val myApplication = application as MyApplication
        dunnCPayy = DunnPay.getInstance()
        dunnCPayy?.setContext(this)
        dunnCPayy?.setDoInitAsync(false)
    }

    private fun init() {
        setToolbarTitle("Collection")

        if (!OpenCVLoader.initDebug()) {
            Log.e(ContentValues.TAG, "OpenCV initialization failed.")
        } else {
            Log.d(ContentValues.TAG, "OpenCV initialization succeeded.")
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        customerCode = intent.getStringExtra("customer_code")
        customerData = db!!.getCustomerDetail(customerCode!!)
        isBlockCustomer = customerData!!.isOverDue
        customerId = customerData!!.id.toString()

        customer_print_enable =
            mPreferenceManager!!.getString(AppConstants.CUSTOMERPRINTENABLE).toString()
        salesman_route_print_enable =
            mPreferenceManager!!.getString(AppConstants.PRINTENABLE).toString()


        setActivity()
        if (intent.getStringExtra("lob_code") != null) {

            lobCode = intent.getStringExtra("lob_code")!!

            if (lobCode.isNotEmpty()) {
                var lobobj = db!!.getCustomerLob(lobCode!!, customerData!!.user_id.toString())

                paymentDays = lobobj!!.credit_days.toString()
                if (lobobj!!.customer_type_id.toString() == "2") {
                    paymentDays = "0"
                    cashType = "1"
                }
            } else {
                if (customerData!!.customer_type_id.toString() == "2") {
                    paymentDays = "0"
                    cashType = "1"
                } else {
                    paymentDays = customerData!!.credit_days.toString()
                    cashType = "0"
                }
            }
        } else {
            if (customerData!!.customer_type_id.toString() == "2") {
                paymentDays = "0"
                cashType = "1"
            } else {
                paymentDays = customerData!!.credit_days.toString()
                cashType = "0"
            }
        }

        binding.txtAllocated!!.text = "Allocated Amt : " + allocatedAmt
        binding.txtClearAmt!!.text = "Cleared Amt : " + clearedAmt


        overDueCollection = ArrayList()
        overDueCollection =
            db!!.getCustomerOverDueCollection(customerData!!.userDetail.id.toString())
        collectionList = db!!.getCustomerCollection(customerData!!.userDetail.id.toString())

        Collections.sort(collectionList, object : Comparator<OutstandingData.CollectionData> {
            override fun compare(
                o1: OutstandingData.CollectionData?,
                o2: OutstandingData.CollectionData?
            ): Int {
                val format = SimpleDateFormat("yyyy-MM-dd")
                var mm: Date? = null
                var nn: Date? = null
                try {
                    mm = format.parse(o1!!.invoice_date)
                    nn = format.parse(o2!!.invoice_date)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                return mm!!.compareTo(nn)
            }
        })

        Collections.sort(overDueCollection, object : Comparator<OutstandingData.CollectionData> {
            override fun compare(
                o1: OutstandingData.CollectionData?,
                o2: OutstandingData.CollectionData?
            ): Int {
                val format = SimpleDateFormat("yyyy-MM-dd")
                var mm: Date? = null
                var nn: Date? = null
                try {
                    mm = format.parse(o1!!.invoice_date)
                    nn = format.parse(o2!!.invoice_date)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                return mm!!.compareTo(nn)
            }
        })

        binding.llCheque.visibility = View.GONE
        binding.lytChequeNo!!.visibility = View.INVISIBLE
        binding.layImage!!.visibility = View.GONE
        binding.lytAuthCode!!.visibility = View.GONE
        binding.txtICashBalance!!.visibility = View.GONE
        binding.rgOnaccount!!.visibility =View.GONE
        binding.lytOnAcount!!.visibility =View.GONE

        binding.rgPaymentMode.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbCash -> {
                    collectionType = "1"
                    binding.llCheque.visibility = View.GONE
                    binding.lytChequeNo!!.visibility = View.INVISIBLE
                    binding.layImage!!.visibility = View.GONE
                    binding.lytAuthCode!!.visibility = View.GONE
                    binding.txtICashBalance!!.visibility = View.GONE
                    binding.rgOnaccount!!.visibility =View.GONE
                    binding.lytOnAcount!!.visibility =View.GONE
                }

                R.id.rbCheque -> {
                    collectionType = "2"
                    binding.llCheque.visibility = View.VISIBLE
                    binding.lytChequeNo!!.visibility = View.VISIBLE
                    binding.layImage!!.visibility = View.VISIBLE
                    binding.lytAuthCode!!.visibility = View.GONE
                    binding.txtICashBalance!!.visibility = View.GONE
                    binding.rgOnaccount!!.visibility =View.GONE
                    binding.lytOnAcount!!.visibility =View.GONE
                }

                R.id.rbCard -> {
                    collectionType = "3"
                    binding.llCheque.visibility = View.GONE
                    binding.lytChequeNo!!.visibility = View.INVISIBLE
                    binding.layImage!!.visibility = View.GONE
                    binding.lytAuthCode!!.visibility = View.GONE
                    binding.txtICashBalance!!.visibility = View.GONE
                    binding.rgOnaccount!!.visibility =View.GONE
                    binding.lytOnAcount!!.visibility =View.GONE
                }

                R.id.rbICash -> {
                    collectionType = "5"
                    binding.llCheque.visibility = View.GONE
                    binding.lytChequeNo!!.visibility = View.INVISIBLE
                    binding.layImage!!.visibility = View.GONE
                    binding.lytAuthCode!!.visibility = View.GONE
                    binding.txtICashBalance!!.visibility = View.VISIBLE
                    binding.rgOnaccount!!.visibility =View.GONE
                    binding.lytOnAcount!!.visibility =View.GONE

                    getCurrentBalance()
                }

                R.id.rbACCash -> {
                    collectionType = "7"
                    binding.llCheque.visibility = View.GONE
                    binding.lytChequeNo!!.visibility = View.INVISIBLE
                    binding.layImage!!.visibility = View.GONE
                    binding.lytAuthCode!!.visibility = View.GONE
                    binding.txtICashBalance!!.visibility = View.VISIBLE
                    binding.rgOnaccount!!.visibility =View.GONE
                    binding.lytOnAcount!!.visibility =View.GONE

                    getCurrentOnAccountBalance()
                }

                else -> {

                }
            }
        }

        if (str_on_account == "1") {
            binding.rvCollectionList.visibility = View.GONE
        } else {
            binding.rvCollectionList.visibility = View.VISIBLE
        }

        binding.rgOnaccount!!.setOnCheckedChangeListener { radioGroup, i ->
            val text = if (R.id.rbaccountyes == i) "Yes" else "No"
            if (text == "Yes") {
                str_on_account = "1"
                binding.rvCollectionList.visibility = View.GONE
            } else {
                str_on_account = "0"
                binding.rvCollectionList.visibility = View.VISIBLE
            }
        }


        /*baseBinding.btnPaymentPriority.setOnClickListener {
            paymentPriorityDialog()
        }*/

        binding.etDate.setOnClickListener {
            dateDialog()
        }

        setUpRecycler()
        getCurrentBalance()

        binding.btnSubmit.setOnClickListener {
//            if (paymentChecked == 2) {
            Utils.logData(
                this@CollectionActivity,
                "On Submit button click"
            )
            if (str_on_account == "1") {
                if (collectionType == "2") {

                    if (isCheckValid()) {
                        val builder = MaterialAlertDialogBuilder(this)
                        builder.setTitle("Alert")
                        builder.setMessage("Are you sure you want to collect this payment?")
                        builder.setPositiveButton(
                            "Yes"
                        ) { dialog, _ ->
                            dialog.cancel()
                            //showDiscountDialog()
                            binding.btnSubmit.visibility = View.GONE
                            addCollection()
                        }
                        builder.setNegativeButton(
                            "No"
                        ) { dialog, _ ->
                            dialog.cancel()
                        }
                        builder.show()
                    }
                } else if (collectionType == "3") {
                    if (binding.etAmount.text.toString() == "") {
                        Toast.makeText(this, "Please enter amount first", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        val builder = MaterialAlertDialogBuilder(this)
                        builder.setTitle("Alert")
                        builder.setMessage("Are you sure you want to collect this payment?")
                        builder.setPositiveButton(
                            "Yes"
                        ) { dialog, _ ->
                            dialog.cancel()
                            binding.btnSubmit.visibility = View.GONE
                            callCardPayment()
//                            cardRefrenceId = binding.edtAuthCode!!.text.toString()
//                            addCollection()
                        }
                        builder.setNegativeButton(
                            "No"
                        ) { dialog, _ ->
                            dialog.cancel()
                        }
                        builder.show()
                    }
                } else if (collectionType == "5") {
                    var enteramt = getCashAmount()
                    if (binding.etAmount.text.toString() == "") {
                        Toast.makeText(this, "Please enter amount first", Toast.LENGTH_SHORT)
                            .show()
                    } else if (Utils.twoDigit(iCashAmount.toFloat()) < Utils.twoDigit(enteramt.toFloat())) {
                        Toast.makeText(
                            this,
                            "iCash balance is low. Kindly topup balance and redeam?",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        val builder = MaterialAlertDialogBuilder(this)
                        builder.setTitle("Alert")
                        builder.setMessage("Are you sure you want to collect this payment?")
                        builder.setPositiveButton(
                            "Yes"
                        ) { dialog, _ ->
                            dialog.cancel()
                            binding.btnSubmit.visibility = View.GONE
                            addCollection()
                        }
                        builder.setNegativeButton(
                            "No"
                        ) { dialog, _ ->
                            dialog.cancel()
                        }
                        builder.show()
                    }
                } else if (collectionType == "7") {
                    var enteramt = getCashAmount()
                    if (binding.etAmount.text.toString() == "") {
                        Toast.makeText(this, "Please enter amount first", Toast.LENGTH_SHORT)
                            .show()
                    } else if (Utils.twoDigit(onAccountAmount.toFloat()) < Utils.twoDigit(enteramt.toFloat())) {
                        Toast.makeText(
                            this,
                            "Account Balance is low!!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        val builder = MaterialAlertDialogBuilder(this)
                        builder.setTitle("Alert")
                        builder.setMessage("Are you sure you want to collect this payment?")
                        builder.setPositiveButton(
                            "Yes"
                        ) { dialog, _ ->
                            dialog.cancel()
                            binding.btnSubmit.visibility = View.GONE
                            addCollection()
                        }
                        builder.setNegativeButton(
                            "No"
                        ) { dialog, _ ->
                            dialog.cancel()
                        }
                        builder.show()
                    }
                } else {
                    if (binding.etAmount.text.toString() == "") {
                        Toast.makeText(this, "Please enter amount first", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        val builder = MaterialAlertDialogBuilder(this)
                        builder.setTitle("Alert")
                        builder.setMessage("Are you sure you want to collect this payment?")
                        builder.setPositiveButton(
                            "Yes"
                        ) { dialog, _ ->
                            dialog.cancel()
                            binding.btnSubmit.visibility = View.GONE
                            addCollection()
                        }
                        builder.setNegativeButton(
                            "No"
                        ) { dialog, _ ->
                            dialog.cancel()
                        }
                        builder.show()
                    }
                }

            } else {
                if (isValid()) {
                    if (collectionType == "2") {

                        if (isCheckValid()) {
                            val builder = MaterialAlertDialogBuilder(this)
                            builder.setTitle("Alert")
                            builder.setMessage("Are you sure you want to collect this payment?")
                            builder.setPositiveButton(
                                "Yes"
                            ) { dialog, _ ->
                                dialog.cancel()
                                //showDiscountDialog()
                                binding.btnSubmit.visibility = View.GONE
                                addCollection()
                            }
                            builder.setNegativeButton(
                                "No"
                            ) { dialog, _ ->
                                dialog.cancel()
                            }
                            builder.show()
                        }
                    } else if (collectionType == "3") {
                        if (binding.etAmount.text.toString() == "") {
                            Toast.makeText(this, "Please enter amount first", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            val builder = MaterialAlertDialogBuilder(this)
                            builder.setTitle("Alert")
                            builder.setMessage("Are you sure you want to collect this payment?")
                            builder.setPositiveButton(
                                "Yes"
                            ) { dialog, _ ->
                                dialog.cancel()
                                binding.btnSubmit.visibility = View.GONE
//                                cardRefrenceId = binding.edtAuthCode!!.text.toString()
//                                addCollection()

                                callCardPayment()
                            }
                            builder.setNegativeButton(
                                "No"
                            ) { dialog, _ ->
                                dialog.cancel()
                            }
                            builder.show()
                        }
                    } else if (collectionType == "5") {
                        var enteramt = getCashAmount()
                        if (binding.etAmount.text.toString() == "") {
                            Toast.makeText(this, "Please enter amount first", Toast.LENGTH_SHORT)
                                .show()
                        } else if (Utils.twoDigit(iCashAmount.toFloat()) <= Utils.twoDigit(enteramt.toFloat())) {
                            Toast.makeText(
                                this,
                                "iCash balance is low. Kindly topup balance and redeam?",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        } else {
                            val builder = MaterialAlertDialogBuilder(this)
                            builder.setTitle("Alert")
                            builder.setMessage("Are you sure you want to collect this payment?")
                            builder.setPositiveButton(
                                "Yes"
                            ) { dialog, _ ->
                                dialog.cancel()
                                binding.btnSubmit.visibility = View.GONE
                                addCollection()
                            }
                            builder.setNegativeButton(
                                "No"
                            ) { dialog, _ ->
                                dialog.cancel()
                            }
                            builder.show()
                        }
                    } else if (collectionType == "7") {
                        var enteramt = getCashAmount()
                        if (binding.etAmount.text.toString() == "") {
                            Toast.makeText(this, "Please enter amount first", Toast.LENGTH_SHORT)
                                .show()
                        } else if (Utils.twoDigit(onAccountAmount.toFloat()) <= Utils.twoDigit(
                                enteramt.toFloat()
                            )
                        ) {
                            Toast.makeText(
                                this,
                                "Account balance is low!",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        } else {
                            val builder = MaterialAlertDialogBuilder(this)
                            builder.setTitle("Alert")
                            builder.setMessage("Are you sure you want to collect this payment?")
                            builder.setPositiveButton(
                                "Yes"
                            ) { dialog, _ ->
                                dialog.cancel()
                                binding.btnSubmit.visibility = View.GONE
                                addCollection()
                            }
                            builder.setNegativeButton(
                                "No"
                            ) { dialog, _ ->
                                dialog.cancel()
                            }
                            builder.show()
                        }
                    } else {
                        if (binding.etAmount.text.toString() == "") {
                            Toast.makeText(this, "Please enter amount first", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            val builder = MaterialAlertDialogBuilder(this)
                            builder.setTitle("Alert")
                            builder.setMessage("Are you sure you want to collect this payment?")
                            builder.setPositiveButton(
                                "Yes"
                            ) { dialog, _ ->
                                dialog.cancel()
                                binding.btnSubmit.visibility = View.GONE
                                addCollection()
                            }
                            builder.setNegativeButton(
                                "No"
                            ) { dialog, _ ->
                                dialog.cancel()
                            }
                            builder.show()
                        }
                    }


                }
            }

        }

        binding.btnStampImage!!.setOnClickListener {
            if (encodedImage!!.isEmpty()) {
                is2ndClick = false
            } else {
                is2ndClick = true
            }

            if (encodedImage!!.isNotEmpty() && encodedImage2!!.isNotEmpty()) {
                Toast.makeText(this, "You can upload max 2 images!", Toast.LENGTH_SHORT).show()
            } else {
                StampUploadDialog()
            }

        }

        binding.etBank.setOnClickListener {

            val mBuilder = android.app.AlertDialog.Builder(this@CollectionActivity)
            mBuilder.setTitle("Select an option")
            mBuilder.setSingleChoiceItems(arrBank, -1) { dialogInterface, i ->
                bankName = arrBank.get(i)
                binding.etBank.text = bankName
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


        binding.etAmount.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                enterAmount = getCashAmount()
                clearedAmt = enterAmount
                notifyAdapter()
                getAllocatedAmt()
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
            }
        })
    }

    private fun showToast(message: String) {
        runOnUiThread {
            AlertDialog.Builder(this@CollectionActivity)
                .setTitle("Response")
                .setMessage("" + message)
                .setPositiveButton(
                    android.R.string.yes
                ) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_collection, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_priority -> {
                startActivity(Intent(me, CardFailActivity::class.java).apply {
                    this.putExtra("customer_code", customerCode)
                })
                return true
            }

            R.id.action_print -> {
                openDIalog()
                return true
            }

            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return false
    }

    private fun openDIalog() {
        Utils.dialogPrint(this, object : OnSearchableDialog {

            override fun onItemSelected(selection: String?) {

                if (selection.equals("yes", ignoreCase = true)) {

                    try {
                        jsonArray = createPrintSOAData("")

                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                    Log.e("Print", jsonArray.toString())
                    PrintLog.mPrintService = mPrintService
                    callDevice()
                } else {
                    finish()
                }
            }
        })
    }

    private fun setActivity() {

        val mVisited = VisitedList()
        mVisited.customerId = customerData!!.userDetail.id.toString()
        mVisited.customerCode = customerCode
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

    private fun addCollection() {

        //Add Collection
        var mCollNo = ""
        mCollNo = Utils.getLastIndexNew(
            mPreferenceManager!!.getString(AppConstants.COLLECTION_LAST).toString(),
            "collection"
        )
        if (mPreferenceManager!!.getFTokenId()!! == "2") {
            var routeCode =
                mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode

            collectionNumber = "V${routeCode}C" +
                    Utils.getLastIndexNew(
                        mPreferenceManager!!.getString(AppConstants.COLLECTION_LAST).toString(),
                        "collection"
                    )
        } else if (mPreferenceManager!!.getFTokenId()!! == "4") {
            var routeCode =
                mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode

            collectionNumber = "D${routeCode}C" +
                    Utils.getLastIndexNew(
                        mPreferenceManager!!.getString(AppConstants.COLLECTION_LAST).toString(),
                        "collection"
                    )
        } else if (mPreferenceManager!!.getFTokenId()!! == "3" || mPreferenceManager!!.getFTokenId()!! == "6") {
            var routeCode =
                mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode

            collectionNumber = "HH${routeCode}C" +
                    Utils.getLastIndexNew(
                        mPreferenceManager!!.getString(AppConstants.COLLECTION_LAST).toString(),
                        "collection"
                    )
        } else {

            var routeCode =
                mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode


            collectionNumber = "P${routeCode}C" +
                    Utils.getLastIndexNew(
                        mPreferenceManager!!.getString(AppConstants.COLLECTION_LAST).toString(),
                        "collection"
                    )
        }

//        collectionNumber =
//            Utils.getLastIndexNew(
//                mPreferenceManager!!.getString(AppConstants.COLLECTION_LAST).toString(),
//                "collection"
//            )

        selectedCollection = ArrayList()
        var selectCollection: ArrayList<OutstandingData.CollectionData> = ArrayList()
        var selectOverDue: ArrayList<OutstandingData.CollectionData> = ArrayList()
        var isMultiCustomer = false
        for (mData in collectionList) {
            if (mData.isSplitChecked) {
                if (mData.amountEnter.toFloat() > 0) {
                    selectCollection.add(mData)
                    selectedCollection.add(mData)
                    if (mData.customer_code != customerCode!!) {
                        isMultiCustomer = true
                    }

                    if (isBlockCustomer == "1") {
                        if (mData.itype == "Invoice") {
                            if (mData.isOverDue == "1") {
                                selectOverDue.add(mData)
                            }
                        }
                    }
                }
            }
        }

        if (collectionType == "2") {
            chequeDate = binding.etDate.text.toString()
            chequeNumber = binding.etCheque.text.toString()
            if (isBlockCustomer == "1") {
                if (selectOverDue.size == overDueCollection.size) {
                    var isClearAll = true
                    for (mSelecColl in selectOverDue) {
                        var mPendin =
                            Utils.twoDigit((mSelecColl.pendingAmount.toFloat() - mSelecColl.amountEnter.toFloat()))
                        if (mPendin > 1) {
                            isClearAll = false
                            break
                        }
                    }

                    if (isClearAll) {

                        Collections.sort(
                            selectOverDue,
                            object : Comparator<OutstandingData.CollectionData> {
                                override fun compare(
                                    o1: OutstandingData.CollectionData?,
                                    o2: OutstandingData.CollectionData?
                                ): Int {
                                    val format = SimpleDateFormat("yyyy-MM-dd")
                                    var mm: Date? = null
                                    var nn: Date? = null
                                    try {
                                        mm = format.parse(o1!!.invoice_date)
                                        nn = format.parse(o2!!.invoice_date)
                                    } catch (e: ParseException) {
                                        e.printStackTrace()
                                    }
                                    return nn!!.compareTo(mm)
                                }
                            })

                        var invDate = selectOverDue[0].invoice_date
                        var days = Utils.getDayDifferenceTwoDate(Utils.getCurrentDate(), chequeDate)

                        if (days < 1) {
                            db!!.updateCustomerUnBlock(customerData!!.userDetail.id.toString())
                        }
                    }
                }
            }
        } else if (collectionType == "3") {
            if (isBlockCustomer == "1") {

                if (selectOverDue.size == overDueCollection.size) {
                    var isClearAll = true
                    for (mSelecColl in selectOverDue) {
                        var mPendin =
                            Utils.twoDigit((mSelecColl.pendingAmount.toFloat() - mSelecColl.amountEnter.toFloat()))
                        if (mPendin > 1) {
                            isClearAll = false
                            break
                        }
                    }

                    if (isClearAll) {
                        db!!.updateCustomerUnBlock(customerData!!.userDetail.id.toString())
                    }
                }
            }
        } else {
            if (isBlockCustomer == "1") {

                if (selectOverDue.size == overDueCollection.size) {
                    var isClearAll = true
                    for (mSelecColl in selectOverDue) {
                        var mPendin =
                            Utils.twoDigit((mSelecColl.pendingAmount.toFloat() - mSelecColl.amountEnter.toFloat()))
                        if (mPendin > 1) {
                            isClearAll = false
                            break
                        }
                    }

                    if (isClearAll) {
                        db!!.updateCustomerUnBlock(customerData!!.userDetail.id.toString())
                    }
                }
            }
        }

        var mAllocatAmt = 0f
        if (str_on_account == "1") {
            mAllocatAmt = getCashAmount()
        } else {
            mAllocatAmt = invAMt - creditAmt
        }

        db!!.updateMultiCollection(
            mAllocatAmt.toString(),
            shelfdAmt.toString(),
            discAmt.toString(),
            rebateAmt.toString(),
            cardRefrenceId,
            transactionid,
            lobCode,
            collectionNumber,
            paymentPriority,
            collectionType,
            chequeDate,
            chequeNumber,
            bankName,
            selectCollection,
            customerData!!.userDetail.id.toString(),
            discountComment,
            str_on_account,
            encodedImage!!,
            encodedImage2!!,
            mBankCharge,
            mBankTaxAmt
        )

        var data: JSONObject? = null

        if (isMultiCustomer) {
            Collections.sort(
                selectedCollection,
                object : Comparator<OutstandingData.CollectionData> {
                    override fun compare(
                        o1: OutstandingData.CollectionData?,
                        o2: OutstandingData.CollectionData?
                    ): Int {
                        return o1!!.customer_id!!.compareTo(o1!!.customer_id!!)
                    }
                })

            //get Print Data
            try {
                jsonArray = createPrintMultipleData(collectionNumber)
                data = JSONObject()
                data.put("data", jsonArray as JSONArray?)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

        } else {
            //get Print Data
            try {
                jsonArray = createPrintData(collectionNumber)
                data = JSONObject()
                data.put("data", jsonArray as JSONArray?)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        val transaction = Transaction()
        if (collectionType == "2") {
            transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_PAYMENT_BY_CHEQUE
        } else if (collectionType == "3") {
            transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_PAYMENT_BY_CARD
        } else if (collectionType == "5") {
            transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_ICCASH_COLLECTION
        } else if (collectionType == "7") {
            transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_ONBALANCECASH_COLLECTION
        } else {
            transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_CASH_COLLECTION
        }

        transaction.tr_date_time = Utils.getCurrentDate() + " " + Utils.getCurrentTime()
        transaction.tr_customer_num = customerData!!.customer_code
        transaction.tr_customer_name =
            customerData!!.userDetail!!.firstname + " " + customerData!!.userDetail!!.lastname
        transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
        transaction.tr_invoice_id = ""
        transaction.tr_order_id = ""
        transaction.tr_collection_id = collectionNumber
        transaction.tr_pyament_id = ""
        transaction.tr_is_posted = "No"
        transaction.tr_printData = data.toString()
        db!!.insertTransaction(transaction)

        //set Order Last Number
        mPreferenceManager!!.setString(AppConstants.COLLECTION_LAST, mCollNo)

        db!!.updateCustomerTransaction(customerData!!.userDetail!!.id.toString(), "collection")
        CustomerDetailActivity.completedTask++

        if (collectionType == "5") {
            redeamICashBalance(collectionNumber, mAllocatAmt.toString(), "0")
        } else if (collectionType == "7") {
            redeamonAccountCashBalance(collectionNumber, mAllocatAmt.toString(), "0")
        }

//        if (salesman_route_print_enable == "0" || customer_print_enable == "0") {
//            callOtherActivity()
//        } else {
        Utils.dialogCustomerPrint(this, "Customer", object : OnSearchableDialog {

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
                    callOtherActivity()
                }
            }
        })

        //  }

    }

    fun callDevice() {

        if (mPrintService == null) {
            val serverIntent: Intent = Intent(this, DeviceListActivity::class.java)
            startActivityForResult(
                serverIntent,
                REQUEST_CONNECT_DEVICE_SECURE
            )

        } else {
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
                    this@CollectionActivity,
                    this@CollectionActivity
                )
                `object`.execute("", jsonArray)
            }
        }


    }

    private fun callOtherActivity() {
        var action = ""
        action = "Payment collected."
        var endTime = Utils.getCurrentTime()
        db!!.updateCustomerVisitActivity(endTime, action, visitId!!, activityId!!)

        isCollectionDone = true
        Toast.makeText(this, "Payment created Successfully!!", Toast.LENGTH_SHORT).show()
        finish()
    }

    fun isValid(): Boolean {

//        var enterAmount = 0.0
//        for (mColl in collectionList) {
//            enterAmount += mColl.amountEnter.toDouble()
//        }

        if (str_on_account == "0") {
            if (Utils.twoDigit(allocatedAmt.toFloat()) > clearedAmt) {
                Toast.makeText(
                    this,
                    "You can not pay more than amount enter",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            } else if (Utils.twoDigit(allocatedAmt.toFloat()) < clearedAmt) {
                Toast.makeText(
                    this,
                    "Please select invoices at least enter amount",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
        }


        return true
    }

    fun isCheckValid(): Boolean {

//        var enterAmount = 0.0
//        for (mColl in collectionList) {
//            enterAmount += mColl.amountEnter.toDouble()
//        }

        if (binding.etDate.text.isEmpty()) {
            Toast.makeText(
                this,
                "Please enter cheque date",
                Toast.LENGTH_SHORT
            ).show()
            return false
        } else if (binding.etCheque.text.isEmpty()) {
            Toast.makeText(
                this,
                "Please enter cheque number",
                Toast.LENGTH_SHORT
            ).show()
            return false
        } else if (bankName.isEmpty()) {
            Toast.makeText(
                this,
                "Please enter bank name",
                Toast.LENGTH_SHORT
            ).show()
            return false
        } else if (encodedImage!!.isEmpty()) {
            Toast.makeText(
                this,
                "Please upload cheque Image",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }

    fun getCashAmount(): Float {
        var amount = 0F
        if (binding.etAmount.text.isNotEmpty()) {
            amount = binding.etAmount.text.toString().toFloat()
        }
        return amount
    }

    fun getAllocatedAmt() {
        allocatedAmt = 0F
        shelfdAmt = 0F
        invAMt = 0F
        creditAmt = 0F
        rebateAmt = 0F
        rebateVatAmt = 0F
        for (mColl in collectionList) {
            if (mColl.isSplitChecked) {

                if (mColl.amountEnter.isNotEmpty()) {
                    if (mColl.itype == "Credit") {
                        allocatedAmt -= Utils.twoDigit(mColl.amountEnter.toFloat())
                        var allMt = Utils.twoDigit(mColl.amountEnter.toFloat())
                        creditAmt += allMt
                    } else if (mColl.itype == "Debit") {
                        allocatedAmt -= Utils.twoDigit(mColl.amountEnter.toFloat())
                        //allocatedAmt -= mColl.amountEnter.toFloat()
                        if (mColl.debittype == "rebate_discount") {
                            rebateAmt += Utils.twoDigit(mColl.netamount!!.toFloat())
                            rebateVatAmt += Utils.twoDigit(mColl.vatamount!!.toFloat())
                        } else {
                            shelfdAmt += Utils.twoDigit(mColl.amountEnter.toFloat())
                        }
                    } else {
                        allocatedAmt += Utils.twoDigit(mColl.amountEnter.toFloat())
                        invAMt += Utils.twoDigit(mColl.amountEnter.toFloat())
                    }
                }
            }
        }

        binding.txtAllocated!!.text = "Allocated Amt : " + Utils.twoDigit(allocatedAmt.toFloat())
        binding.txtClearAmt!!.text = "Cleared Amt : $clearedAmt"

    }

    fun notifyAdapter() {
        if (enterAmount > 0) {
            splitEnterAmt = 0f
            splitEnterAmt = enterAmount
            if (paymentChecked == 0) {
                for (mColl in collectionList) {

                    var index = collectionList.indexOf(mColl)

                    if (mColl.pendingAmount.toDouble() > 0) {
                        if (mColl.pendingAmount.toDouble() < enterAmount) {
                            mColl.isInvoiceSplit = true
                            mColl.isSplitChecked = true
                            mColl.amountEnter = mColl.pendingAmount
                            val temPend = 0
                            mColl.tempAmountDue = temPend.toString()
                            splitEnterAmt -= mColl.pendingAmount.toFloat()
                            collectionList[index] = mColl
                        } else {
                            mColl.isInvoiceSplit = true
                            mColl.isSplitChecked = true
                            mColl.amountEnter = splitEnterAmt.toString()
                            val temPend = mColl.pendingAmount.toDouble() - Utils.round(
                                splitEnterAmt.toFloat(),
                                2
                            ).toDouble()
                            mColl.tempAmountDue = temPend.toString()
                            splitEnterAmt -= splitEnterAmt
                            collectionList[index] = mColl
                        }

                    }

                    if (splitEnterAmt == 0F) {
                        break
                    }
                }
            } else if (paymentChecked == 1) {
                for (mColl in collectionList.reversed()) {
                    var index = collectionList.indexOf(mColl)
                    if (mColl.pendingAmount.toDouble() > 0) {
                        if (mColl.pendingAmount.toDouble() < enterAmount) {
                            mColl.isInvoiceSplit = true
                            mColl.isSplitChecked = true
                            mColl.amountEnter = mColl.pendingAmount
                            val temPend = 0
                            mColl.tempAmountDue = temPend.toString()
                            splitEnterAmt -= mColl.pendingAmount.toFloat()
                            collectionList[index] = mColl
                        } else {
                            mColl.isInvoiceSplit = true
                            mColl.isSplitChecked = true
                            mColl.amountEnter = splitEnterAmt.toString()
                            val temPend = mColl.pendingAmount.toDouble() - splitEnterAmt
                            mColl.tempAmountDue = temPend.toString()
                            splitEnterAmt -= splitEnterAmt
                            collectionList[index] = mColl
                        }
                    }
                    if (splitEnterAmt == 0F) {
                        break
                    }
                }
            }
        } else {
            for (mColl in collectionList) {
                var index = collectionList.indexOf(mColl)
                mColl.isInvoiceSplit = false
                mColl.isSplitChecked = false
                mColl.amountEnter = "0"
                mColl.tempAmountDue = mColl.pendingAmount
                collectionList[index] = mColl
            }
        }

        binding.rvCollectionList.adapter = null
        setUpRecycler()
    }

    private fun dateDialog() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                binding.etDate.setText(
                    "$year-" + String.format(
                        "%02d",
                        monthOfYear + 1
                    ) + "-" + String.format("%02d", dayOfMonth)
                )
            },
            year,
            month,
            day
        )

        dpd.show()
    }

    private fun paymentPriorityDialog() {
        val listItems = arrayOf("FIFO", "LIFO", "Manual")

        priorityDialog = MaterialAlertDialogBuilder(this)
            .setTitle("Payment Priority")
            .setSingleChoiceItems(listItems, paymentChecked) { dialogInterface, i ->
                paymentChecked = i
                when (i) {
                    0 -> {
                        priorityDialog!!.dismiss()
                        paymentPriority = "1"
                        setUpRecycler()
                    }

                    1 -> {
                        priorityDialog!!.dismiss()
                        paymentPriority = "2"
                        setUpRecycler()
                    }

                    2 -> {
                        priorityDialog!!.dismiss()
                        paymentPriority = "3"
                        setUpRecycler()
                    }

                    else -> {

                    }
                }
            }
            .create()

        priorityDialog!!.show()

    }

    private fun setUpRecycler() {
        binding.rvCollectionList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = CollectionAdapter(this, collectionList!!, paymentPriority)
        binding.rvCollectionList.adapter = mAdapter
    }

    override fun onItemsClick(position: Int, tag: String) {

    }

    class CollectionAdapter(
        var context: Context,
        var collectionList: ArrayList<OutstandingData.CollectionData>,
        var paymentPriority: String
    ) : RecyclerView.Adapter<CollectionAdapter.ViewHolder>() {

        val onItemClickListener = context as OnItemClickListener

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding =
                RowCollectionItemBinding.inflate(LayoutInflater.from(context), parent, false)
            return ViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return collectionList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val mCollectionData = collectionList[holder.adapterPosition]
            if (mCollectionData.itype == "Debit") {
                holder.binding.tvInvNo.text = "Deb. No: " + mCollectionData.invoice_number
                holder.binding.tvInvDate.text = "Deb. Date: " + mCollectionData.invoice_date
                holder.binding.tvDueDate.text = "Days: " + mCollectionData.days
                holder.binding.tvDueDate.setTextColor(Color.parseColor("#4A4A4A"))
                if (mCollectionData.tempAmountDue.toFloat() > 0) {
                    holder.binding.tvAmtDue.text = "O/S Amt: -" + Utils.getCurrencyFormate(
                        context!!,
                        mCollectionData.tempAmountDue.toFloat()
                    )
                } else {
                    holder.binding.tvAmtDue.text = "O/S Amt: " + Utils.getCurrencyFormate(
                        context!!,
                        mCollectionData.tempAmountDue.toFloat()
                    )
                }
                if (mCollectionData.amountEnter.toDouble() > 0) {
                    holder.binding.tvAllocated.text = "-" + mCollectionData.amountEnter
                } else {
                    holder.binding.tvAllocated.text = mCollectionData.amountEnter
                }
            } else if (mCollectionData.itype == "Credit") {
                holder.binding.tvInvNo.text = "Cred. No: " + mCollectionData.invoice_number
                holder.binding.tvInvDate.text = "Ced. Date: " + mCollectionData.invoice_date
                holder.binding.tvDueDate.text = "Days: " + mCollectionData.days
                holder.binding.tvDueDate.setTextColor(Color.parseColor("#4A4A4A"))
                if (mCollectionData.tempAmountDue.toFloat() > 0) {
                    holder.binding.tvAmtDue.text = "O/S Amt: -" + Utils.getCurrencyFormate(
                        context!!,
                        mCollectionData.tempAmountDue.toFloat()
                    )
                } else {
                    holder.binding.tvAmtDue.text = "O/S Amt: " + Utils.getCurrencyFormate(
                        context!!,
                        mCollectionData.tempAmountDue.toFloat()
                    )
                }
                if (mCollectionData.amountEnter.toDouble() > 0) {
                    holder.binding.tvAllocated.text = "-" + mCollectionData.amountEnter
                } else {
                    holder.binding.tvAllocated.text = mCollectionData.amountEnter
                }

            } else {
                holder.binding.tvInvNo.text = "Inv. No: " + mCollectionData.invoice_number
                holder.binding.tvInvDate.text = "Inv. Date: " + mCollectionData.invoice_date
                holder.binding.tvDueDate.text = "Days: " + mCollectionData.days
                if (mCollectionData.isOverDue == "1") {
                    holder.binding.tvDueDate.setTextColor(Color.RED)
                } else {
                    holder.binding.tvDueDate.setTextColor(Color.parseColor("#4A4A4A"))
                }
                holder.binding.tvAmtDue.text = "O/S Amt: " + Utils.getCurrencyFormate(
                    context!!,
                    mCollectionData.tempAmountDue.toFloat()
                )
                if (mCollectionData.amountEnter.toFloat() > 0) {
                    holder.binding.tvAllocated.text =
                        String.format("%.2f", (mCollectionData.amountEnter.toFloat()))
                } else {
                    holder.binding.tvAllocated.text = mCollectionData.amountEnter
                }
            }

            //holder.binding.edtColAmt.setText(mCollectionData.amountPay)
            holder.binding.tvCustCode.text = "Site Code: " + mCollectionData.customer_code

            //holder.binding.tvName.text = "Name: " + mCollectionData.firstname
            holder.binding.checkBox.isChecked = mCollectionData.isSplitChecked
            holder.binding.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (enterAmount > 0) {
                    if (buttonView.isPressed) {
                        if (isChecked) {

                            if (mCollectionData.itype == "Credit") {
                                if (collectionList[holder.adapterPosition].tempAmountDue.toDouble() > 0)
                                    collectionList[holder.adapterPosition].amountEnter =
                                        collectionList[holder.adapterPosition].tempAmountDue
                            } else if (mCollectionData.itype == "Debit") {
                                if (collectionList[holder.adapterPosition].tempAmountDue.toDouble() > 0)
                                    collectionList[holder.adapterPosition].amountEnter =
                                        collectionList[holder.adapterPosition].tempAmountDue
                            } else {
                                if (collectionList[holder.adapterPosition].tempAmountDue.toDouble() > 0) {
                                    var diff = clearedAmt - allocatedAmt
                                    if (diff > collectionList[holder.adapterPosition].tempAmountDue.toFloat()) {
                                        collectionList[holder.adapterPosition].amountEnter =
                                            String.format(
                                                "%.2f",
                                                collectionList[holder.adapterPosition].tempAmountDue.toFloat()
                                            )
                                    } else {
                                        collectionList[holder.adapterPosition].amountEnter =
                                            String.format("%.2f", diff)
                                    }
                                }

                            }

                            var due =
                                collectionList[holder.adapterPosition].tempAmountDue.toFloat() - collectionList[holder.adapterPosition].amountEnter.toFloat()

                            collectionList[holder.adapterPosition].tempAmountDue =
                                String.format("%.2f", due)
                            collectionList[holder.adapterPosition].isSplitChecked = true
                            collectionList[holder.adapterPosition].isInvoiceSplit = true
                            collectionList[holder.adapterPosition] =
                                collectionList[holder.adapterPosition]

                            if (mCollectionData.itype == "Credit") {
                                holder.binding.tvAllocated.text = "-" +
                                        collectionList[holder.adapterPosition].amountEnter
                            } else if (mCollectionData.itype == "Debit") {
                                holder.binding.tvAllocated.text = "-" +
                                        collectionList[holder.adapterPosition].amountEnter
                            } else {
                                holder.binding.tvAllocated.text =
                                    collectionList[holder.adapterPosition].amountEnter
                            }

                            holder.binding.tvAmtDue.text = "O/S Amt: " + Utils.getCurrencyFormate(
                                context!!,
                                collectionList[holder.adapterPosition].tempAmountDue.toFloat()
                            )
                        } else {
                            var due =
                                collectionList[holder.adapterPosition].tempAmountDue.toFloat() + collectionList[holder.adapterPosition].amountEnter.toFloat()
                            collectionList[holder.adapterPosition].tempAmountDue = due.toString()
                            collectionList[holder.adapterPosition].amountEnter = "0"
                            collectionList[holder.adapterPosition].isSplitChecked = true
                            collectionList[holder.adapterPosition].isInvoiceSplit = true
                            collectionList[holder.adapterPosition] =
                                collectionList[holder.adapterPosition]
                            holder.binding.tvAllocated.text =
                                collectionList[holder.adapterPosition].amountEnter

                            if (mCollectionData.itype == "Debit") {
                                holder.binding.tvAmtDue.text =
                                    "O/S Amt: -" + Utils.getCurrencyFormate(
                                        context!!,
                                        collectionList[holder.adapterPosition].tempAmountDue.toFloat()
                                    )
                            } else if (mCollectionData.itype == "Credit") {
                                holder.binding.tvAmtDue.text =
                                    "O/S Amt: -" + Utils.getCurrencyFormate(
                                        context!!,
                                        collectionList[holder.adapterPosition].tempAmountDue.toFloat()
                                    )
                            } else {
                                holder.binding.tvAmtDue.text =
                                    "O/S Amt: " + Utils.getCurrencyFormate(
                                        context!!,
                                        collectionList[holder.adapterPosition].tempAmountDue.toFloat()
                                    )
                            }
                        }

                        calculateAllocate()
                    }

                } else {
                    Toast.makeText(context, "Please enter Amount first!!", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            holder.binding.tvAllocated.setOnClickListener {
                callAllocated(collectionList[holder.adapterPosition], holder.adapterPosition)
            }

            holder.binding.executePendingBindings()

        }


        inner class ViewHolder(val binding: RowCollectionItemBinding) :
            RecyclerView.ViewHolder(binding.root) {
        }
    }

    fun allocatedDialog(mColl: OutstandingData.CollectionData, position: Int) {
        val factory = LayoutInflater.from(this)
        val binding: CollectionAllocateDialogBinding =
            DataBindingUtil.inflate(factory, R.layout.collection_allocate_dialog, null, false)

        val deleteDialogView: View = binding.root

        val deleteDialog: android.app.AlertDialog = android.app.AlertDialog.Builder(this).create()
        deleteDialog.setView(deleteDialogView)

        binding.edtPending.text = mColl.pendingAmount
        binding.edtAllocated.setText(String.format("%.2f", (mColl.amountEnter.toFloat())))
        var balance = mColl.pendingAmount.toFloat() - mColl.amountEnter.toFloat()
        binding.edtBalance.text = "" + String.format("%.2f", balance)

        binding.edtAllocated.addTextChangedListener(
            object : TextWatcher {

                override fun afterTextChanged(s: Editable) {
                    var modQty = mColl.pendingAmount.toDouble()

                    if (s.toString().isNotEmpty()) {
                        if (s.toString().toDouble() > modQty) {

                        } else {
                            var due = modQty - s.toString().toDouble()
                            binding.edtBalance.text = "" + String.format("%.2f", due)
                        }
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int
                ) {

                }
            })

        binding.btnConfirm.setOnClickListener {
            collectionList[position].amountEnter =
                binding.edtAllocated.text.toString()
            var due =
                collectionList[position].pendingAmount.toFloat() - binding.edtAllocated.text.toString()
                    .toFloat()
            collectionList[position].tempAmountDue = due.toString()
            collectionList[position].isSplitChecked = true
            collectionList[position].isInvoiceSplit = true
            mAdapter!!.notifyDataSetChanged()
            deleteDialog.dismiss()
            getAllocatedAmt()
        }

        binding.btnCancel.setOnClickListener {

            deleteDialog.dismiss()
        }

        deleteDialog.show()
    }

    override fun onBackPressed() {
        var action = ""
        action = "Payment not collected"
        var endTime = Utils.get24CurrentTime()
        db!!.updateCustomerVisitActivity(endTime, action, visitId!!, activityId!!)
        super.onBackPressed()
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
            mainArr.put("Comment", discountComment)

            if (str_on_account == "1") {
                mainArr.put("OnAccount", "Yes")
            } else {
                mainArr.put("OnAccount", "No")
            }

            mainArr.put("mPtype", "Normal")
            mainArr.put("iCashAccount", "")
            mainArr.put("totalBalance", "0.0")
            mainArr.put("bonus", "")
            mainArr.put("Topupvalue", "")

            var mPaentDetail = db!!.getCustomerParentDetail(customerId!!)
            mainArr.put("ParentCode", mPaentDetail["ParentCode"])
            mainArr.put("ParentName", mPaentDetail["Name"])

            if (collectionType == "2") {
                if (chequeDate == Utils.getCurrentDate()) {
                    mainArr.put("collectionType", "CDC") //Invoice No
                } else if (chequeDate < Utils.getCurrentDate()) {
                    mainArr.put("collectionType", "CDC") //Invoice No
                } else {
                    mainArr.put("collectionType", "PDC") //Invoice No
                }
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
            } else {
                mainArr.put("collectionType", "Cash") //Invoice No
                mainArr.put("cardRefrenceId", "") //Invoice No
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
            for (mCollection in selectedCollection!!) {

                val data = JSONArray()

                if (mCollection.itype == "Debit") {
                    data.put(mCollection.invoice_date) // Date
                    data.put(mCollection.invoice_number) // No
                    data.put("-" + mCollection.grandTotal) //  Amount
                    data.put("-" + mCollection.pendingAmount) // O/s Amount
                    data.put("-" + mCollection.amountEnter) // Recvd Amount

                } else if (mCollection.itype == "Credit") {
                    data.put(mCollection.invoice_date) // Date
                    data.put(mCollection.invoice_number) // No
                    data.put("-" + mCollection.grandTotal) //  Amount
                    data.put("-" + mCollection.pendingAmount) // O/s Amount
                    data.put("-" + mCollection.amountEnter) // Recvd Amount

                } else {
                    data.put(mCollection.invoice_date) // Date
                    data.put(mCollection.invoice_number) // No
                    data.put(mCollection.grandTotal) //  Amount
                    data.put(mCollection.pendingAmount) // O/s Amount
                    data.put(mCollection.amountEnter) // Recvd Amount
                }

                jData.put(data)
            }
            val jNetData = JSONArray()

            jNetData.put("")
            jNetData.put("")
            jNetData.put("")
            jNetData.put("Total :")
            jNetData.put("" + enterAmount)

            val jNetDData = JSONArray()

            jNetDData.put("")
            jNetDData.put("")
            jNetDData.put("")
            jNetDData.put("$discountReason :")
            jNetDData.put("" + discountAmt)

            val jNetDTData = JSONArray()
            if (discountComment != "") {
                var mTital = Utils.twoDigit((enterAmount + discountAmt.toFloat()))
                jNetDTData.put("")
                jNetDTData.put("")
                jNetDTData.put("")
                jNetDTData.put("Total Collection :")
                jNetDTData.put("" + mTital)
            }

            val totalObj = JSONObject()
            totalObj.put(
                "Total Amount", enterAmount
            )
            totalObj.put(
                "Total Amount(AED)",
                Utils.getCurrencyFormate(this, enterAmount.toFloat())
            )
            TOTAL.put(totalObj)
            mainArr.put("TOTAL", TOTAL)
            mainArr.put("data", jData)
            mainArr.put("netTotal", jNetData)
            mainArr.put("shelfTotal", jNetDData)
            mainArr.put("MDTotal", jNetDTData)
            jDict.put("mainArr", mainArr)
            jInter.put(jDict)
            jArr.put(jInter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jArr
    }

    fun createPrintSOAData(collectionNumber: String?): JSONArray? {
        val jArr = JSONArray()
        try {
            val jInter = JSONArray()
            val jDict = JSONObject()
            jDict.put(AppConstants.REQUEST, AppConstants.SOA_COLLECTION)
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
            mainArr.put("Comment", discountComment)

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

            if (customerData!!.customer_phone != null) {
                mainArr.put("Mobile", "" + customerData!!.customer_phone)
            } else {
                mainArr.put("Mobile", "")
            }
            mainArr.put("CITY", db!!.getCustomerRegion(customerData!!.user_id.toString()))
            mainArr.put("Country", "UAE")
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
            var mTotal = 0.0
            val jData = JSONArray()
            for (mCollection in collectionList!!) {

                val data = JSONArray()

                data.put(mCollection.invoice_date) // Date
                data.put(mCollection.invoice_number) // No
                data.put(String.format("%.2f", mCollection.grandTotal.toDouble())) // Amount
                data.put(String.format("%.2f", mCollection.pendingAmount.toDouble())) // O/s Amount
                mTotal += mCollection.pendingAmount.toDouble()
                jData.put(data)
            }


            val totalObj = JSONObject()
            totalObj.put(
                "Total Amount", String.format("%.2f", (mTotal))
            )
            totalObj.put(
                "Total Amount(AED)",
                Utils.getCurrencyFormate(this, mTotal.toFloat())
            )
            TOTAL.put(totalObj)
            mainArr.put("TOTAL", TOTAL)
            mainArr.put("data", jData)
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
    fun createPrintMultipleData(collectionNumber: String?): JSONArray? {
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
            mainArr.put("Type", "Multi") //Invoice No
            mainArr.put("Comment", discountComment)

            var mPaentDetail = db!!.getCustomerParentDetail(customerId!!)
            mainArr.put("ParentCode", mPaentDetail["ParentCode"])
            mainArr.put("ParentName", mPaentDetail["Name"])

            if (collectionType == "2") {
                if (chequeDate == Utils.getCurrentDate()) {
                    mainArr.put("collectionType", "CDC") //Invoice No
                } else if (chequeDate < Utils.getCurrentDate()) {
                    mainArr.put("collectionType", "CDC") //Invoice No
                } else {
                    mainArr.put("collectionType", "PDC") //Invoice No
                }
                mainArr.put("chequeNo", chequeNumber) //Invoice No
                mainArr.put("chequeDate", chequeDate) //Invoice No
                mainArr.put("Bank", bankName) //Invoice No
            } else {
                mainArr.put("collectionType", "Cash") //Invoice No
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
            var jMultiData = JSONObject()
            var totalIAmount = 0F
            var totalOSAmount = 0F
            var totalRAmount = 0F
            var i = 0
            var customerName = ""
            var lastCode = ""
            var jCustData = JSONArray()
            for (mCollection in selectedCollection!!) {
                if (i == 0) {
                    lastCode = mCollection.customer_code!!.toString()
                    customerName =
                        mCollection.customer_code!!.toString() + " - " + mCollection.firstname
                    val data = JSONArray()
                    data.put(mCollection.invoice_date) // Date
                    data.put(mCollection.invoice_number) // No
                    data.put(mCollection.grandTotal) //  Amount
                    data.put(mCollection.pendingAmount) // O/s Amount
                    data.put(mCollection.amountEnter) // Recvd Amount
                    jCustData.put(data)
                    totalIAmount += mCollection.grandTotal.toFloat()
                    totalOSAmount += mCollection.pendingAmount.toFloat()
                    totalRAmount += mCollection.amountEnter.toFloat()
                } else {
                    if (lastCode != mCollection.customer_code!!.toString()) {
                        val data = JSONArray()
                        data.put("Sub Total:") // Date
                        data.put("") // No
                        data.put("" + totalIAmount) //  Amount
                        data.put("" + totalOSAmount) // O/s Amount
                        data.put("" + totalRAmount) // Recvd Amount
                        jCustData.put(data)
                        jMultiData.put("Customer", customerName)
                        jMultiData.put("data", jCustData)
                        jData.put(jMultiData)

                        //New Customer
                        jMultiData = JSONObject()
                        jCustData = JSONArray()
                        lastCode = mCollection.customer_code!!.toString()
                        customerName =
                            mCollection.customer_code!!.toString() + " - " + mCollection.firstname
                        val datas = JSONArray()
                        datas.put(mCollection.invoice_date) // Date
                        datas.put(mCollection.invoice_number) // No
                        datas.put(mCollection.grandTotal) //  Amount
                        datas.put(mCollection.pendingAmount) // O/s Amount
                        datas.put(mCollection.amountEnter) // Recvd Amount
                        jCustData.put(datas)
                        totalIAmount += mCollection.grandTotal.toFloat()
                        totalOSAmount += mCollection.pendingAmount.toFloat()
                        totalRAmount += mCollection.amountEnter.toFloat()

                        if (i == (selectedCollection.size - 1)) {
                            val data = JSONArray()
                            data.put("Sub Total:") // Date
                            data.put("") // No
                            data.put("" + totalIAmount) //  Amount
                            data.put("" + totalOSAmount) // O/s Amount
                            data.put("" + totalRAmount) // Recvd Amount
                            jCustData.put(data)
                            jMultiData.put("Customer", customerName)
                            jMultiData.put("data", jCustData)
                            jData.put(jMultiData)
                        }
                    } else {
                        val data = JSONArray()
                        data.put(mCollection.invoice_date) // Date
                        data.put(mCollection.invoice_number) // No
                        data.put(mCollection.grandTotal) //  Amount
                        data.put(mCollection.pendingAmount) // O/s Amount
                        data.put(mCollection.amountEnter) // Recvd Amount
                        jCustData.put(data)
                        totalIAmount += mCollection.grandTotal.toFloat()
                        totalOSAmount += mCollection.pendingAmount.toFloat()
                        totalRAmount += mCollection.amountEnter.toFloat()

                        if (i == (selectedCollection.size - 1)) {
                            val data = JSONArray()
                            data.put("Sub Total:") // Date
                            data.put("") // No
                            data.put("" + totalIAmount) //  Amount
                            data.put("" + totalOSAmount) // O/s Amount
                            data.put("" + totalRAmount) // Recvd Amount
                            jCustData.put(data)
                            jMultiData.put("Customer", customerName)
                            jMultiData.put("data", jCustData)
                            jData.put(jMultiData)
                        }
                    }
                }
                i++

            }
            val jNetData = JSONArray()

            jNetData.put("")
            jNetData.put("")
            jNetData.put("")
            jNetData.put("Total :")
            jNetData.put("" + enterAmount)

            val jNetDData = JSONArray()

            jNetDData.put("")
            jNetDData.put("")
            jNetDData.put("")
            jNetDData.put("$discountReason :")
            jNetDData.put("" + discountAmt)

            val jNetDTData = JSONArray()
            if (discountComment != "") {
                var mTital = Utils.twoDigit((enterAmount + discountAmt.toFloat()))
                jNetDTData.put("")
                jNetDTData.put("")
                jNetDTData.put("")
                jNetDTData.put("Total Collection :")
                jNetDTData.put("" + mTital)
            }

            val totalObj = JSONObject()
            totalObj.put(
                "Total Amount", enterAmount
            )
            totalObj.put(
                "Total Amount(AED)",
                Utils.getCurrencyFormate(this, enterAmount.toFloat())
            )
            TOTAL.put(totalObj)
            mainArr.put("TOTAL", TOTAL)
            mainArr.put("data", jData)
            mainArr.put("netTotal", jNetData)
            mainArr.put("shelfTotal", jNetDData)
            mainArr.put("MDTotal", jNetDTData)
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

        imgStamp = deleteDialogView.findViewById(R.id.imgStamp)

        imgStamp.setOnClickListener {
            showPictureDialog(binding.imgStamp)
        }

        binding.btnUpload.setOnClickListener {

            if (encodedImage == "") {
                Toast.makeText(
                    this@CollectionActivity,
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

    fun insertStampSync(invNo: String, stamp: String) {

    }

    private fun showPictureDialog(imgStamp: ImageView) {
        val pictureDialog = android.app.AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Capture photo from camera")
        pictureDialog.setItems(
            pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> pickFromCamera()
                // 1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    private fun pickFromCamera() {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description")
        image_uri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(
            intent,
            CAMERA
        )
    }

    fun choosePhotoFromGallary() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, pickImage)

    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            imgStamp.setImageURI(imageUri)
            val imageStream: InputStream? = contentResolver.openInputStream(imageUri!!)
            val selectedImage = BitmapFactory.decodeStream(imageStream)
            binding.tvImageCount!!.visibility = View.VISIBLE
            if (is2ndClick) {
                encodedImage2 = encodeImage(selectedImage)
                binding.tvImageCount!!.text = "2"
            } else {
                encodedImage = encodeImage(selectedImage)
                binding.tvImageCount!!.text = "1"
            }

        } else if (resultCode == RESULT_OK && requestCode == CAMERA) {
            // For camera images, use the stored image_uri
            val originalBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, image_uri)

            if (originalBitmap != null) {
                // Start the image cropping activity
                startImageCropActivity(image_uri!!)
            } else {
                Toast.makeText(this, "Failed to retrieve the image", Toast.LENGTH_SHORT).show()
            }
            //val thumbnail = data!!.extras!!.get("data") as Bitmap
            //imgStamp.setImageBitmap(thumbnail)

        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            // Handle the result of the cropping activity
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                // Get the cropped image URI
                val croppedUri = result.uri
                try {
                    // Load the cropped image
                    val croppedBitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, croppedUri)

                    // Convert the cropped image to a binarized image
                    binarizedBitmap = binarizeImage(croppedBitmap)

                    // Set the binarized image to the ImageView
                    Glide.with(this)
                        .load(binarizedBitmap)
                        .into(imgStamp)

                    saveImage(binarizedBitmap!!)

                    //encodedImage = encodeImage(binarizedBitmap!!)
                    binding.tvImageCount!!.visibility = View.VISIBLE
                    if (is2ndClick) {
                        //encodedImage2 = encodeImage(binarizedBitmap!!)
                        binding.tvImageCount!!.text = "2"
                    } else {
                        //encodedImage = encodeImage(binarizedBitmap!!)
                        binding.tvImageCount!!.text = "1"
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                // Handle cropping error
                val error = result.error
                Toast.makeText(this, "Cropping failed: " + error.message, Toast.LENGTH_SHORT).show()
            }
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CONNECT_DEVICE_SECURE) {
            connectDevice(data!!, true)
        }
    }


    private fun saveImage(binarizedBitmap: Bitmap) {

        var filename = ""
        val sd: File = Environment.getExternalStorageDirectory()


        if (is2ndClick) {
            filename = customerCode!! + "cheque.png"
        } else {
            filename = customerCode!! + "control.png"
        }

        val dest = File(sd, filename)
        if (is2ndClick) {
            encodedImage2 = dest.absolutePath
        } else {
            encodedImage = dest.absolutePath
        }
        try {
            val out: FileOutputStream = FileOutputStream(dest)
            binarizedBitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.flush()
            out.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun startImageCropActivity(sourceUri: Uri) {
        CropImage.activity(sourceUri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(this)
    }

    private fun binarizeImage(originalBitmap: Bitmap): Bitmap? {
        // Convert the input Bitmap to a Mat (OpenCV format)
        val srcMat = Mat(originalBitmap.width, originalBitmap.height, CvType.CV_8UC1)
        org.opencv.android.Utils.bitmapToMat(originalBitmap, srcMat)

        // Convert the Mat to grayscale
        val grayMat = Mat()
        Imgproc.cvtColor(srcMat, grayMat, Imgproc.COLOR_BGR2GRAY)

        // Apply Gaussian blur to reduce noise
        Imgproc.GaussianBlur(grayMat, grayMat, Size(5.0, 5.0), 0.0)

        // Apply adaptive thresholding
        val binaryMat = Mat()
        Imgproc.adaptiveThreshold(
            grayMat,
            binaryMat,
            255.0,
            Imgproc.ADAPTIVE_THRESH_MEAN_C,
            Imgproc.THRESH_BINARY,
            11,  // Block size (adjust as needed)
            2.0 // Constant subtracted from the mean
        )

        // Convert the binary Mat back to a Bitmap
        val binarizedBitmap =
            Bitmap.createBitmap(binaryMat.cols(), binaryMat.rows(), Bitmap.Config.ARGB_8888)
        org.opencv.android.Utils.matToBitmap(binaryMat, binarizedBitmap)

        // Release Mats to free up memory
        srcMat.release()
        grayMat.release()
        binaryMat.release()
        return binarizedBitmap
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
                this@CollectionActivity,
                this@CollectionActivity
            )
            `object`.execute("", jsonArray)

        }, 3000)
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

    private class MyHandler internal constructor(activity: CollectionActivity) :
        Handler() {
        private val mActivity: WeakReference<CollectionActivity>

        init {
            mActivity = WeakReference<CollectionActivity>(activity)
        }

        override fun handleMessage(msg: Message) {
            val activity: CollectionActivity? = mActivity.get()
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
                                }
                            }

                        }
                    }
                }

            })
    }

    private fun callCardPayment() {
        isRecursive = false

        dunnCPayy?.dunnStartPayment(
            getPaymentRequest(),

            object : IPayResult {
                override fun onSuccess(payResponse: DunnPayResponse) {
                    getTRansactionSuceessCallBack(payResponse)
                }

                override fun onError(errorResponse: ErrorResponse) {
                    cardFailPayment(errorResponse)
                   // insertCardRecord("Fail")
                    // getTransactionStatus()
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
        mCardData.mStatus = ""

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
        Toast.makeText(this, "Card Payment Fail : $message", Toast.LENGTH_SHORT).show()
    }

    private fun getTRansactionSuceessCallBack(payResponse: DunnPayResponse) {
        // cardRefrenceId = payResponse.dunnPaymentReceipt.rrn
        cardRefrenceId = payResponse.approvalCode
        merchantId = payResponse.merchantId
        transactionid = payResponse.transactionId
        terminalId = "mashreq POS"
        lastFour = payResponse.lastFour
        cardType = payResponse.cardBrand
        addCollection()

        //insertCardRecord("Success")
    }

    private fun getTransactionStatus() {

        dunnPay!!.dunnGetStatus(getStatuRequest(), object : ITransactionStatusResult {
            override fun onSuccess(payResponse: DUNNStatusResponse) {
                showToast(payResponse.toString())
            }

            override fun onError(errorResponse: ErrorResponse) {
                showToast(errorResponse.toString())
            }
        })
    }

    private fun getStatuRequest(): DunnStatusRequest? {
        val DUNNStatusRequest = DunnStatusRequest()
        DUNNStatusRequest.merchantType = "MID"
        DUNNStatusRequest.flag = "M"
        DUNNStatusRequest.authId = "6000203"
        DUNNStatusRequest.searchField = prevRefId
        DUNNStatusRequest.fullReceipt = "Y"
        return DUNNStatusRequest
    }


    @SuppressLint("SetTextI18n")
    private fun getPaymentRequest(): DunnPayRequest? {
        val DUNNPayRequest = DunnPayRequest()
        DUNNPayRequest.authId = "6000203"
        DUNNPayRequest.authKey = "G4NOQ814JCH7TV1ZU13PZP2K"
        DUNNPayRequest.phoneNo = "0000000000"
        DUNNPayRequest.emailId = "test@gmail.com"
        DUNNPayRequest.merchantType = "MID"
        DUNNPayRequest.displayDetails = "N"
        DUNNPayRequest.fullReceipt = "Y"

        val dunnBillingList: MutableList<DunnBilling> = java.util.ArrayList()
        val dUNNBilling = DunnBilling()

//        var bankcharge = Utils.twoDigit((enterAmount * 2) / 100)
//        var mTaxAmount = Utils.getVatAmt(bankcharge.toDouble(), 5.0)
//        var mOtherAmount = Utils.twoDigit((bankcharge + mTaxAmount).toFloat())
        var mFinalAmount = enterAmount

        mBankCharge = "0.0"
        mBankTaxAmt = "0.0"

        dUNNBilling.amount = "" + enterAmount
        dUNNBilling.otherAmount = "0"
        prevRefId = System.currentTimeMillis().toString()
       // prevRefId = getRandomNum(15)!!
        dUNNBilling.refId = prevRefId
        dUNNBilling.keyField1 = "SAMPLE"
        dunnBillingList.add(dUNNBilling)
        DUNNPayRequest.totalBillingAmount = "" + mFinalAmount
        DUNNPayRequest.dunnBillings = dunnBillingList


        insertCardRecord("Request")

        return DUNNPayRequest
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

}