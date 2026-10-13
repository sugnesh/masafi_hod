package com.engear.vansales.activities

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.R
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.DeviceListActivity
import com.engear.vansales.databinding.ActivityDataPostingBinding
import com.engear.vansales.databinding.RowItemPostingBinding
import com.engear.vansales.interfaces.OnSearchableDialog
import com.engear.vansales.model.DailySummary
import com.engear.vansales.model.DepositeSummary
import com.engear.vansales.model.Invoice
import com.engear.vansales.model.Transaction
import com.engear.vansales.model.UserData
import com.engear.vansales.printer.BluetoothPrintService
import com.engear.vansales.printer.PrintLog
import com.engear.vansales.printer.WoosimCustomService
import com.engear.vansales.sync.SyncData
import com.engear.vansales.utils.Utils
import com.itextpdf.text.Chunk
import com.itextpdf.text.Document
import com.itextpdf.text.DocumentException
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.FontFactory
import com.itextpdf.text.Image
import com.itextpdf.text.PageSize
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Phrase
import com.itextpdf.text.Rectangle
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.LineSeparator
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class DataPostingActivity : BaseActivity() {

    lateinit var binding: ActivityDataPostingBinding
    private var arrData: ArrayList<Transaction> = ArrayList<Transaction>()
    private var dir: File? = null
    private var invoiceList: ArrayList<Invoice>? = ArrayList()
    private var returnList: ArrayList<Invoice>? = ArrayList()
    private var cashInvoiceList: ArrayList<Invoice>? = ArrayList()
    private var creditInvoiceList: ArrayList<Invoice>? = ArrayList()
    private var creditReturnList: ArrayList<Invoice>? = ArrayList()
    private var cashReturnList: ArrayList<Invoice>? = ArrayList()
    private var collectionList: ArrayList<DepositeSummary>? = ArrayList()
    private var jsonArray: JSONArray? = null
    private var customerVisitList: ArrayList<DailySummary>? = ArrayList()
    private var lobName = ""
    var salesman_route_print_enable = ""

    private val REQUEST_ENABLE_BT = 3
    private var mBluetoothAdapter: BluetoothAdapter? = null

    // Member object for the print services

    private val REQUEST_CONNECT_DEVICE_SECURE = 1

    val MESSAGE_DEVICE_NAME = 1
    val MESSAGE_TOAST = 2
    val MESSAGE_READ = 3

    val DEVICE_NAME = "device_name"
    val TOAST = "toast"

    private val mEmphasis = false
    private val mUnderline = false
    private val mCharsize = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDataPostingBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@DataPostingActivity,
            "On DataPosting Screen "
        )
        init()
    }

    private fun init() {
        setToolbarTitle("Print Transactions")
        setNavigationView(mPreferenceManager!!.getFTokenId()!!)

        registerReceiver(broadcastReceiver, IntentFilter(SyncData.BROADCAST_ACTION))

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        arrData = db!!.getAllTransactions()
        dir = File(
            Environment.getExternalStorageDirectory(),
            "EANGEAR" + File.separator + "PDF"
        )
        if (!dir!!.exists()) {
            dir!!.mkdirs()
        }
        setAdapter()


        var salesLOB: List<UserData.Data.UserInfo.SalesmanInfo.SalesmanLob>? = null
        if (mPreferenceManager!!.getUserInfo() != null) {
            if (mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.lobList != null) {
                salesLOB = mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.lobList
                if (salesLOB.size > 0) {
                    try {
                        lobName = salesLOB[0].lobDetail.name
                    } catch (e: Exception) {

                    }

                }
            }
        }

        binding.chk.setOnCheckedChangeListener { buttonView, isChecked ->
            if (arrData.size > 0) {
                var type = ""
                if (isChecked) {
                    type = "true"
                } else {
                    type = "false"
                }
                for (i in 0 until arrData.size) {
                    val item = arrData[i]
                    if ((item.tr_is_posted == "Fail" || item.tr_is_posted ==
                                "No" || item.tr_is_posted == "")
                    ) {
                        arrData[i].tr_isCheck = type
                    }
                }
                setAdapter()
            }
        }

        binding.btnSync.setOnClickListener {
            Utils.logData(
                this@DataPostingActivity,
                "On Sync Button Screen "
            )
            for (i in 0 until arrData.size) {
                val item = arrData[i]
               // if (item.tr_isCheck == "true") {
                    if ((item.tr_is_posted == "Fail" || item.tr_is_posted ==
                                "No" || item.tr_is_posted == "")
                    ) {
                        when (item.tr_type) {
                            AppConstants.LoadConfirmation_TR -> db!!.markForLoadPost(item.tr_invoice_id!!)
                            AppConstants.TRANSACTION_TYPES.TT_OREDER_CREATED -> db!!.markForOrderPost(
                                item.tr_order_id!!
                            )

                            AppConstants.TRANSACTION_TYPES.TT_LOAD_VARIANCE -> db!!.markForLoadVariancePost(
                                item.tr_order_id!!
                            )

                            AppConstants.TRANSACTION_TYPES.TT_LOAD_CREATE -> db!!.markForLoadOrderPost(
                                item.tr_order_id!!
                            )

                            AppConstants.TRANSACTION_TYPES.TT_SALES_CREATED -> db!!.markForInvoicePost(
                                item.tr_invoice_id!!
                            )

                            AppConstants.TRANSACTION_TYPES.TT_CARD_FAIL_COLLECTION -> db!!.markForCardFailPost(
                                item.tr_order_id!!
                            )

                            AppConstants.TRANSACTION_TYPES.TT_DELIVERY_CANCEL -> db!!.markForDelvieryPost(
                                item.tr_invoice_id!!
                            )

                            AppConstants.TRANSACTION_TYPES.TT_RETURN_CREATED -> db!!.markForReturnPost(
                                item.tr_order_id!!
                            )

                            AppConstants.TRANSACTION_TYPES.TT_RETURN_REQUEST -> db!!.markForRequestReturnUUIDPost(
                                item.tr_pyament_id!!
                            )

                            AppConstants.TRANSACTION_TYPES.TT_CASH_COLLECTION -> db!!.markForCollectionPost(
                                item.tr_collection_id!!
                            )

                            AppConstants.TRANSACTION_TYPES.TT_ICCASH_COLLECTION -> db!!.markForCollectionPost(
                                item.tr_collection_id!!
                            )

                            AppConstants.TRANSACTION_TYPES.TT_ONBALANCECASH_COLLECTION -> db!!.markForCollectionPost(
                                item.tr_collection_id!!
                            )

                            AppConstants.TRANSACTION_TYPES.TT_ONDISPENSERECASH_COLLECTION -> db!!.markForCollectionPost(
                                item.tr_collection_id!!
                            )

                            AppConstants.TRANSACTION_TYPES.TT_PR_COLLECTION -> db!!.markForCollectionPost(
                                item.tr_collection_id!!
                            )

                            AppConstants.TRANSACTION_TYPES.TT_STAMP_INVOICE -> db!!.markForStampInvoicePost(
                                item.tr_collection_id!!
                            )

                            AppConstants.TRANSACTION_TYPES.TT_PALLET_RETURN -> db!!.markForRequestReturnPalletPost(
                                item.tr_order_id!!
                            )

                            AppConstants.TRANSACTION_TYPES.TT_PAYMENT_BY_CHEQUE -> db!!.markForCollectionPost(
                                item.tr_collection_id!!
                            )

                            AppConstants.TRANSACTION_TYPES.TT_PAYMENT_BY_CARD -> db!!.markForCollectionPost(
                                item.tr_collection_id!!
                            )

                            AppConstants.TRANSACTION_TYPES.TT_UNLOAD -> db!!.markForUnloadPost(
                                item.tr_order_id!!
                            )

                            AppConstants.TRANSACTION_TYPES.TT_UNLOAD_EXPIRY -> db!!.markForUnloadPost(
                                item.tr_order_id!!
                            )

                            AppConstants.TRANSACTION_TYPES.TT_UNLOAD_VARIANCE -> db!!.markForUnloadPost(
                                item.tr_order_id!!
                            )

                            AppConstants.TRANSACTION_TYPES.TT_UNLOAD_VAN_RETURN -> db!!.markForUnloadPost(
                                item.tr_order_id!!
                            )

                            AppConstants.TRANSACTION_TYPES.TT_COMPLAINT_FEEDBACK -> db!!.markForComplaintPost(
                                item.tr_order_id!!
                            )

                            AppConstants.TRANSACTION_TYPES.TT_COMPAIGN_FEEDBACK -> db!!.markForCampaignPost(
                                item.tr_order_id!!
                            )

                            AppConstants.TRANSACTION_TYPES.TT_COMPATITOR -> db!!.markForCompetitorPost(
                                item.tr_order_id!!
                            )

                            AppConstants.TRANSACTION_TYPES.TT_INVENTORY -> db!!.markForInventoryPost(
                                item.tr_order_id!!
                            )

                            AppConstants.TRANSACTION_TYPES.TT_DISPLAY_TOOL -> db!!.markForToolPost(
                                item.tr_order_id!!
                            )

                            AppConstants.TRANSACTION_TYPES.TT_PLANOGRAM -> db!!.markForPlanogramPost(
                                item.tr_order_id!!
                            )

                            AppConstants.TRANSACTION_TYPES.TT_ASSETS -> db!!.markForAssetsPost(
                                item.tr_order_id!!
                            )

                            AppConstants.TRANSACTION_TYPES.TT_CONS_SURVEY -> db!!.markForSurveyPost(
                                item.tr_order_id!!
                            )

                            AppConstants.TRANSACTION_TYPES.TT_ASSETS_SURVEY -> db!!.markForSurveyPost(
                                item.tr_order_id!!
                            )

                            AppConstants.TRANSACTION_TYPES.TT_DISTRIBUTION_SURVEY -> db!!.markForSurveyPost(
                                item.tr_order_id!!
                            )

                            AppConstants.TRANSACTION_TYPES.TT_SENSURY_SURVEY -> db!!.markForSurveyPost(
                                item.tr_order_id!!
                            )

                            AppConstants.TRANSACTION_TYPES.TT_DISPLAY_TOOL_IMAGE -> db!!.markForDistributionImagePost(
                                item.tr_order_id!!
                            )
                        }
                    }
                //}
            }
            if (Utils.isNetworkAvailable(applicationContext)) {
                Utils.createBackgroundJob(applicationContext)
            }
        }
        salesman_route_print_enable =
            mPreferenceManager!!.getString(AppConstants.PRINTENABLE).toString()
//        if (salesman_route_print_enable == "0") {
//            binding.imgSaleSummary.visibility = View.GONE
//            binding.imgDepositeSummary.visibility = View.GONE
//            binding.imgDaily.visibility = View.GONE
//        }
        binding.imgSaleSummary.setOnClickListener {

            invoiceList = ArrayList()
            if (mPreferenceManager!!.getString(AppConstants.ROLE) == "1") {
                invoiceList = db!!.getAllOrderInvoiceList()

                cashInvoiceList = ArrayList()
                creditInvoiceList = ArrayList()

                for (mInv in invoiceList!!) {
                    cashInvoiceList!!.add(mInv)
                }

                returnList = ArrayList()
                returnList = db!!.getAllReturnList()

                creditReturnList = ArrayList()
                cashReturnList = ArrayList()
                for (mInv in returnList!!) {
                    cashReturnList!!.add(mInv)
                }

            } else {
                invoiceList = db!!.getAllInvoiceList()

                cashInvoiceList = ArrayList()
                creditInvoiceList = ArrayList()

                for (mInv in invoiceList!!) {
                    if (mInv.invoiceType == "1") {
                        cashInvoiceList!!.add(mInv)
                    } else {
                        creditInvoiceList!!.add(mInv)
                    }
                }

                returnList = ArrayList()

                if (mPreferenceManager!!.getString(AppConstants.ROLE) == "4") {
                    returnList = db!!.getAllDeliveryReturnList()
                } else {
                    returnList = db!!.getAllReturnList()
                }

                creditReturnList = ArrayList()
                cashReturnList = ArrayList()
                for (mInv in returnList!!) {
                    var cusType = db!!.getCustomerTYpe(mInv.customerId!!)
                    if (cusType == "1") {
                        creditReturnList!!.add(mInv)
                    } else if (cusType == "2") {
                        cashReturnList!!.add(mInv)
                    } else if (cusType == "3") {
                        creditReturnList!!.add(mInv)
                    }
                }

            }


            if (mPreferenceManager!!.getString(AppConstants.ROLE) == "1") {
                try {
                    jsonArray = createPrintOrderSummaryData()
                    Log.e("Print", jsonArray.toString())
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            } else {
                try {
                    jsonArray = createPrintSalesSummaryData()
                    Log.e("Print", jsonArray.toString())
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }


            Utils.dialogPrint(this@DataPostingActivity, object : OnSearchableDialog {

                override fun onItemSelected(selection: String?) {

                    if (selection.equals("yes", ignoreCase = true)) {
                        PrintLog.mPrintService = mPrintService
                        callDevice()
                    }
                }
            })
        }

        binding.imgDepositeSummary.setOnClickListener {

            collectionList = ArrayList()
            collectionList = db!!.getAllFinalDepositeCollection()

            try {
                jsonArray = createPrintDepositeData()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            Utils.dialogPrint(this@DataPostingActivity, object : OnSearchableDialog {

                override fun onItemSelected(selection: String?) {

                    if (selection.equals("yes", ignoreCase = true)) {
                        PrintLog.mPrintService = mPrintService
                        callDevice()
                    }
                }
            })
        }

        binding.imgDaily.setOnClickListener {

            showOnlyProgressDialog()

            getAllData()

        }

        binding.lytSalesSummary!!.setOnClickListener {
            if (mPreferenceManager!!.getString(AppConstants.ROLE) == "1") {
                startActivity(Intent(me, OrderSummaryActivity::class.java))
            } else {
                startActivity(Intent(me, SalesSummaryViewActivity::class.java))
            }

        }

        binding.lytDepositeSummary!!.setOnClickListener {
            startActivity(Intent(me, DepositeSummaryActivity::class.java))
        }

    }

    fun getAllData() {

        customerVisitList = ArrayList()
        customerVisitList = db!!.getAllVisitedCustomer()

        for (mCus in customerVisitList!!) {
            var index = customerVisitList!!.indexOf(mCus)
            if (mCus.isPlann == "1") {
                mCus.isPlann = "P"
            } else {
                mCus.isPlann = "UP"
            }
            mCus.revCNT = db!!.getCustomerReturnCount(mCus.customerCode!!).toString()
            mCus.conCNT = db!!.getCustomerCollectionCount(mCus.customerId!!).toString()
            mCus.invCNT = db!!.getCustomerInvoiceCount(mCus.customerCode!!).toString()
            mCus.netSale = db!!.getCustomerInvoiceNet(mCus.customerCode!!).toString()
            mCus.totalSale = db!!.getCustomerInvoiceTotal(mCus.customerCode!!).toString()
            mCus.returnAmount = db!!.getCustomerReturnTotal(mCus.customerCode!!).toString()
            mCus.cashAmount = db!!.getCustomerCashCollection(mCus.customerId!!).toString()
            mCus.chaqueAmount = db!!.getCustomerChequeCollection(mCus.customerId!!).toString()
            mCus.focAmt = "0"
            customerVisitList!![index] = mCus
        }

        dismissDialog()

        try {
            jsonArray = createPrintDailyOtherData()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        Utils.dialogPrint(this@DataPostingActivity, object : OnSearchableDialog {

            override fun onItemSelected(selection: String?) {

                if (selection.equals("yes", ignoreCase = true)) {
                    PrintLog.mPrintService = mPrintService
                    callDevice()
                }
            }
        })
    }

    inner class TransactionAdapter(
        var context: Context,
        var mList: ArrayList<Transaction>?
    ) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding =
                RowItemPostingBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
            return ViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return mList!!.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val item = mList!![holder.adapterPosition]


            when (item.tr_type) {
                AppConstants.LoadConfirmation_TR -> {
                    (holder.binding).tvType.setText("LC")
                    (holder.binding).tvTransaction.setText(item.tr_invoice_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_SALES_CREATED -> {
                    (holder.binding).tvType.setText("SI")
                    (holder.binding).tvTransaction.setText(item.tr_invoice_id + "\nSite:" + item.tr_customer_num)
                }

                AppConstants.TRANSACTION_TYPES.TT_RETURN_CREATED -> {
                    val returnType = db!!.getReturnType(item.tr_order_id!!)
                    if (returnType.equals("badReturn", ignoreCase = true)) {
                        (holder.binding).tvType.setText("BR")
                    } else {
                        (holder.binding).tvType.setText("GR")
                    }
                    (holder.binding).tvTransaction.setText(item.tr_order_id + "\nSite:" + item.tr_customer_num)
                }

                AppConstants.TRANSACTION_TYPES.TT_CARD_FAIL_COLLECTION -> {
                    (holder.binding).tvType.setText("CRF")
                    (holder.binding).tvTransaction.setText(item.tr_order_id + "\nSite:" + item.tr_customer_num)
                    (holder.binding).ivPrint.setVisibility(View.GONE)
                }


                AppConstants.TRANSACTION_TYPES.TT_RETURN_REQUEST -> {
                    (holder.binding).tvType.setText("RRC")
                    (holder.binding).tvTransaction.setText(item.tr_pyament_id + "\nSite:" + item.tr_customer_num)
                }

                AppConstants.TRANSACTION_TYPES.TT_OREDER_CREATED -> {
                    (holder.binding).tvType.setText("OR")
                    (holder.binding).tvTransaction.setText(item.tr_order_id + "\nSite:" + item.tr_customer_num)
                }

                AppConstants.TRANSACTION_TYPES.TT_LOAD_VARIANCE -> {
                    (holder.binding).tvType.setText("LCV")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_CASH_COLLECTION -> {
                    (holder.binding).tvType.setText("COL")
                    (holder.binding).tvTransaction.setText(item.tr_collection_id + "\nSite:" + item.tr_customer_num)
                }

                AppConstants.TRANSACTION_TYPES.TT_PR_COLLECTION -> {
                    (holder.binding).tvType.setText("CLPR")
                    (holder.binding).tvTransaction.setText(item.tr_collection_id + "\nSite:" + item.tr_customer_num)
                }

                AppConstants.TRANSACTION_TYPES.TT_ICCASH_COLLECTION -> {
                    (holder.binding).tvType.setText("ICOL")
                    (holder.binding).tvTransaction.setText(item.tr_collection_id + "\nSite:" + item.tr_customer_num)
                }

                AppConstants.TRANSACTION_TYPES.TT_ONBALANCECASH_COLLECTION -> {
                    (holder.binding).tvType.setText("ACOL")
                    (holder.binding).tvTransaction.setText(item.tr_collection_id + "\nSite:" + item.tr_customer_num)
                }

                AppConstants.TRANSACTION_TYPES.TT_ONDISPENSERECASH_COLLECTION -> {
                    (holder.binding).tvType.setText("DSCOL")
                    (holder.binding).tvTransaction.setText(item.tr_collection_id + "\nSite:" + item.tr_customer_num)
                }


                AppConstants.TRANSACTION_TYPES.TT_STAMP_INVOICE -> {
                    (holder.binding).tvType.setText("STMP")
                    (holder.binding).tvTransaction.setText(item.tr_collection_id)
                    (holder.binding).ivPrint.setVisibility(View.GONE)
                }

                AppConstants.TRANSACTION_TYPES.TT_PAYMENT_BY_CHEQUE -> {
                    (holder.binding).tvType.setText("COL")
                    (holder.binding).tvTransaction.setText(item.tr_collection_id + "\nSite:" + item.tr_customer_num)
                }

                AppConstants.TRANSACTION_TYPES.TT_PAYMENT_BY_CARD -> {
                    (holder.binding).tvType.setText("COL")
                    (holder.binding).tvTransaction.setText(item.tr_collection_id + "\nSite:" + item.tr_customer_num)
                }

                AppConstants.TRANSACTION_TYPES.TT_PALLET_RETURN -> {
                    (holder.binding).tvType.setText("PLR")
                    (holder.binding).tvTransaction.setText(item.tr_order_id + "\nSite:" + item.tr_customer_num)
                    (holder.binding).ivPrint.setVisibility(View.VISIBLE)
                }


                AppConstants.TRANSACTION_TYPES.TT_LOAD_CREATE -> {
                    (holder.binding).tvType.setText("LCR")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_DELIVERY_CANCEL -> {
                    (holder.binding).tvType.setText("DLC")
                    (holder.binding).tvTransaction.setText(item.tr_invoice_id)
                    (holder.binding).ivPrint.setVisibility(View.GONE)
                }

                AppConstants.TRANSACTION_TYPES.TT_UNLOAD -> {
                    (holder.binding).tvType.setText("UL")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_UNLOAD_EXPIRY -> {
                    (holder.binding).tvType.setText("VEXP")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_UNLOAD_VARIANCE -> {
                    (holder.binding).tvType.setText("ULV")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_UNLOAD_VAN_RETURN -> {
                    (holder.binding).tvType.setText("ULR")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_EXCHANGE_CREATED -> {
                    (holder.binding).tvType.setText("EXCH")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_ADD_CUSTOEMR_CREATED -> {
                    (holder.binding).tvType.setText("CUS")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                    (holder.binding).ivPrint.setVisibility(View.GONE)
                }

                AppConstants.TRANSACTION_TYPES.TT_SENSURY_SURVEY -> {
                    (holder.binding).tvType.setText("SEN_SRV")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_COMPLAINT_FEEDBACK -> {
                    (holder.binding).tvType.setText("FEED")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_COMPAIGN_FEEDBACK -> {
                    (holder.binding).tvType.setText("CAMP")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_COMPATITOR -> {
                    (holder.binding).tvType.setText("COMP")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_INVENTORY -> {
                    (holder.binding).tvType.setText("INV")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_DISPLAY_TOOL -> {
                    (holder.binding).tvType.setText("TOOL")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_PLANOGRAM -> {
                    (holder.binding).tvType.setText("PLNG")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_ASSETS -> {
                    (holder.binding).tvType.setText("ASST")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_CONS_SURVEY -> {
                    (holder.binding).tvType.setText("C_SRV")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_ASSETS_SURVEY -> {
                    (holder.binding).tvType.setText("A_SRV")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_DISTRIBUTION_SURVEY -> {
                    (holder.binding).tvType.setText("D_SRV")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_SENSURY_SURVEY -> {
                    (holder.binding).tvType.setText("E_SRV")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_DISPLAY_TOOL_IMAGE -> {
                    (holder.binding).tvType.setText("D-IMG")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_INVOICE_CANCEL -> {
                    (holder.binding).tvType.setText("INCL")
                    (holder.binding).tvTransaction.setText(item.tr_invoice_id)
                }
            }
            if (item.tr_is_posted.equals("Yes")) {
                (holder.binding).ivSync.setImageResource(R.drawable.ic_icon_verified_sel)
                (holder.binding).ivInfo.visibility = View.GONE
            } else if (item.tr_is_posted.equals("Fail")) {
                (holder.binding).ivSync.setImageResource(R.drawable.ic_action_sync)
                (holder.binding).ivInfo.visibility = View.VISIBLE
            } else {
                (holder.binding).ivSync.setImageResource(R.drawable.ic_action_sync)
                (holder.binding).ivInfo.visibility = View.GONE
            }
            (holder.binding).chkRow.isChecked = item.tr_isCheck.equals("true")
            holder.binding.executePendingBindings()

            holder.binding.ivInfo.setOnClickListener {
                Utils.displayAlert(this@DataPostingActivity, item.tr_message!!)
            }

            holder.binding.ivPrint.setOnClickListener {
                try {
                    var jsonString = item.tr_printData
//                    jsonString = Utils.decodeString(jsonString!!.replace("%".toRegex(), "123ABC"))
//                    val jsonObject = JSONObject(jsonString.replace("123ABC".toRegex(), "%"))
                    val jsonObject = JSONObject(jsonString)
                    jsonArray = jsonObject.getJSONArray("data")
                    Log.e("Print Data",jsonArray.toString())
                    if (item.tr_type == AppConstants.TRANSACTION_TYPES.TT_SALES_CREATED ||
                        item.tr_type == AppConstants.TRANSACTION_TYPES.TT_RETURN_CREATED
                        || item.tr_type == AppConstants.TRANSACTION_TYPES.TT_CASH_COLLECTION
                        || item.tr_type == AppConstants.TRANSACTION_TYPES.TT_PAYMENT_BY_CHEQUE
                        || item.tr_type == AppConstants.TRANSACTION_TYPES.TT_PR_COLLECTION
                        || item.tr_type == AppConstants.TRANSACTION_TYPES.TT_ICCASH_COLLECTION
                    ) {
                        Utils.dialogCustomerPrint(this@DataPostingActivity, "Customer", object :
                            OnSearchableDialog {

                            override fun onItemSelected(selection: String?) {

                                if (selection.equals("yes", ignoreCase = true)) {

                                    try {
                                        jsonArray!!.getJSONArray(0).getJSONObject(0)
                                            .getJSONObject("mainArr")
                                            .put("mType", "Account")
                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                    }
                                    PrintLog.mPrintService = mPrintService
                                    callDevice()

                                    //callFunction(jsonArray)
                                    //callDevice()


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
                                    //callFunction(jsonArray)
//                                    val `object` = PrintLog(
//                                        this@DataPostingActivity,
//                                        this@DataPostingActivity
//                                    )
//                                    `object`.execute("", jsonArray)
                                }
                            }
                        })
                    } else {
                        Utils.dialogPrint(this@DataPostingActivity, object :
                            OnSearchableDialog {

                            override fun onItemSelected(selection: String?) {

                                if (selection.equals("yes", ignoreCase = true)) {

                                    PrintLog.mPrintService = mPrintService
                                    callDevice()
                                    //callFunction(jsonArray)

//                                    val `object` = PrintLog(
//                                        this@DataPostingActivity,
//                                        this@DataPostingActivity
//                                    )
//                                    `object`.execute("", jsonArray)
                                }
                            }
                        })
                    }


                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            holder.binding.ivSync.setOnClickListener {
                if ((item.tr_is_posted == "Fail") || item.tr_is_posted ==
                    "No"
                ) {
                    when (item.tr_type) {
                        AppConstants.LoadConfirmation_TR -> db!!.markForLoadPost(item.tr_invoice_id!!)
                        AppConstants.TRANSACTION_TYPES.TT_OREDER_CREATED -> db!!.markForOrderPost(
                            item.tr_order_id!!
                        )

                        AppConstants.TRANSACTION_TYPES.TT_LOAD_VARIANCE -> db!!.markForLoadVariancePost(
                            item.tr_order_id!!
                        )

                        AppConstants.TRANSACTION_TYPES.TT_LOAD_CREATE -> db!!.markForLoadOrderPost(
                            item.tr_order_id!!
                        )

                        AppConstants.TRANSACTION_TYPES.TT_SALES_CREATED -> db!!.markForInvoicePost(
                            item.tr_invoice_id!!
                        )

                        AppConstants.TRANSACTION_TYPES.TT_DELIVERY_CANCEL -> db!!.markForDelvieryPost(
                            item.tr_invoice_id!!
                        )

                        AppConstants.TRANSACTION_TYPES.TT_RETURN_CREATED -> db!!.markForReturnPost(
                            item.tr_order_id!!
                        )

                        AppConstants.TRANSACTION_TYPES.TT_RETURN_REQUEST -> db!!.markForRequestReturnUUIDPost(
                            item.tr_order_id!!
                        )

                        AppConstants.TRANSACTION_TYPES.TT_PALLET_RETURN -> db!!.markForRequestReturnPalletPost(
                            item.tr_order_id!!
                        )

                        AppConstants.TRANSACTION_TYPES.TT_CASH_COLLECTION -> db!!.markForCollectionPost(
                            item.tr_collection_id!!
                        )

                        AppConstants.TRANSACTION_TYPES.TT_ICCASH_COLLECTION -> db!!.markForCollectionPost(
                            item.tr_collection_id!!
                        )

                        AppConstants.TRANSACTION_TYPES.TT_ONBALANCECASH_COLLECTION -> db!!.markForCollectionPost(
                            item.tr_collection_id!!
                        )

                        AppConstants.TRANSACTION_TYPES.TT_ONDISPENSERECASH_COLLECTION -> db!!.markForCollectionPost(
                            item.tr_collection_id!!
                        )

                        AppConstants.TRANSACTION_TYPES.TT_PR_COLLECTION -> db!!.markForCollectionPost(
                            item.tr_collection_id!!
                        )

                        AppConstants.TRANSACTION_TYPES.TT_STAMP_INVOICE -> db!!.markForStampInvoicePost(
                            item.tr_collection_id!!
                        )

                        AppConstants.TRANSACTION_TYPES.TT_PAYMENT_BY_CHEQUE -> db!!.markForCollectionPost(
                            item.tr_collection_id!!
                        )

                        AppConstants.TRANSACTION_TYPES.TT_UNLOAD -> db!!.markForUnloadPost(
                            item.tr_order_id!!
                        )

                        AppConstants.TRANSACTION_TYPES.TT_CARD_FAIL_COLLECTION -> db!!.markForCardFailPost(
                            item.tr_order_id!!
                        )

                        AppConstants.TRANSACTION_TYPES.TT_UNLOAD_EXPIRY -> db!!.markForUnloadPost(
                            item.tr_order_id!!
                        )

                        AppConstants.TRANSACTION_TYPES.TT_UNLOAD_VARIANCE -> db!!.markForUnloadPost(
                            item.tr_order_id!!
                        )

                        AppConstants.TRANSACTION_TYPES.TT_UNLOAD_VAN_RETURN -> db!!.markForUnloadPost(
                            item.tr_order_id!!
                        )

                        AppConstants.TRANSACTION_TYPES.TT_COMPLAINT_FEEDBACK -> db!!.markForComplaintPost(
                            item.tr_order_id!!
                        )

                        AppConstants.TRANSACTION_TYPES.TT_COMPAIGN_FEEDBACK -> db!!.markForCampaignPost(
                            item.tr_order_id!!
                        )

                        AppConstants.TRANSACTION_TYPES.TT_COMPATITOR -> db!!.markForCompetitorPost(
                            item.tr_order_id!!
                        )

                        AppConstants.TRANSACTION_TYPES.TT_INVENTORY -> db!!.markForInventoryPost(
                            item.tr_order_id!!
                        )

                        AppConstants.TRANSACTION_TYPES.TT_DISPLAY_TOOL -> db!!.markForToolPost(
                            item.tr_order_id!!
                        )

                        AppConstants.TRANSACTION_TYPES.TT_PLANOGRAM -> db!!.markForPlanogramPost(
                            item.tr_order_id!!
                        )

                        AppConstants.TRANSACTION_TYPES.TT_ASSETS -> db!!.markForAssetsPost(
                            item.tr_order_id!!
                        )

                        AppConstants.TRANSACTION_TYPES.TT_CONS_SURVEY -> db!!.markForSurveyPost(
                            item.tr_order_id!!
                        )

                        AppConstants.TRANSACTION_TYPES.TT_ASSETS_SURVEY -> db!!.markForSurveyPost(
                            item.tr_order_id!!
                        )

                        AppConstants.TRANSACTION_TYPES.TT_DISTRIBUTION_SURVEY -> db!!.markForSurveyPost(
                            item.tr_order_id!!
                        )

                        AppConstants.TRANSACTION_TYPES.TT_SENSURY_SURVEY -> db!!.markForSurveyPost(
                            item.tr_order_id!!
                        )

                        AppConstants.TRANSACTION_TYPES.TT_DISPLAY_TOOL_IMAGE -> db!!.markForDistributionImagePost(
                            item.tr_order_id!!
                        )

                        AppConstants.TRANSACTION_TYPES.TT_INVOICE_CANCEL -> db!!.markForInvoicePost(
                            item.tr_invoice_id!!
                        )
                    }
                    if (Utils.isNetworkAvailable(applicationContext)) {
                        Utils.createBackgroundJob(applicationContext)
                    }
                }
            }

        }


        inner class ViewHolder(val binding: RowItemPostingBinding) :
            RecyclerView.ViewHolder(binding.root) {
        }
    }

    fun setAdapter() {
        var mAdapter = TransactionAdapter(this, arrData)
        binding.rvList.adapter = mAdapter
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            arrData.clear()
            arrData = db!!.getAllTransactions()
            if (binding.rvList != null) {
                setAdapter()
            }
        }
    }

    private fun sendMail(jsonArray: JSONArray, tvType: String) {
        for (j in 0 until (if (jsonArray.length() > 1) 1 else jsonArray.length())) {
            var jInner: JSONArray? = null
            try {
                jInner = jsonArray.getJSONArray(j)
                for (i in 0 until jInner.length()) {
                    val jDict = jInner.getJSONObject(i)
                    val jsnData = jDict.getJSONObject("mainArr")

                    if (tvType.equals("BR", ignoreCase = true)) {
                        sendReturnMail(jsnData)
                    } else if (tvType.equals("GR", ignoreCase = true)) {
                        sendReturnMail(jsnData)
                    } else if (tvType.equals("OR", ignoreCase = true)) {
                        sendOrderMail(jsnData)
                    } else {
                        sendInvoiceMBMail(jsnData)
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: DocumentException) {
                e.printStackTrace()
            }
        }
        //finish()
    }

    @Throws(FileNotFoundException::class, DocumentException::class)
    fun sendInvoiceMBMail(`object`: JSONObject) {
        val bgImage: Image
        val doc = Document(PageSize.A4, 10f, 10f, 10f, 10f)
        val fntSize: Float = 11f
        val lineSpacing: Float = 13f
        val headerFont: Float = 14f
        val fntTitleSize: Float = 12f
        try {

            var mSalesmanData = mPreferenceManager!!.getUserInfo()!!.data.userInfo
            val mSalesman = mSalesmanData.firstname + " " + mSalesmanData.lastname
            val salesmanNo = mSalesmanData.id.toString()
            var mOrganization = mSalesmanData.organisation.name
            var poBox =
                "PO Box: " + mSalesmanData.organisation.postal + ", " + mSalesmanData.organisation.city + ","
            var country =
                mSalesmanData.organisation.state + ", " + mSalesmanData.organisation.countryInfo!!.name

            val invoiceNo = `object`.getString("invoicenumber")
            val invoiceType = `object`.getString("invoiceType")
            val date = `object`.getString("DOC DATE")
            val time = `object`.getString("TIME")
            val custEmail = `object`.getString("Email")
            val sdf = SimpleDateFormat("ddMMyyyy")
            val fileName = "Invoice_" + sdf.format(Calendar.getInstance().getTime()) + ".pdf"
            val file = File(
                (dir!!.path + File.separator
                        + fileName)
            )
            if (!file.exists()) {
                try {
                    file.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            val fOut = FileOutputStream(file)
            val writer = PdfWriter.getInstance(doc, fOut)
            //open the document
            doc.open()
            try {
                // Create Table into Document with 1 Row
                var myTable = PdfPTable(1)
                // 100.0f mean width of table is same as Document size
                // 100.0f mean width of table is same as Document size
                myTable.widthPercentage = 100.0f

                var cell: PdfPCell
                var mPara: Paragraph
                val myImage = getResources().getDrawable(R.mipmap.ic_launcher_invoice)
                val bitmap = (myImage as BitmapDrawable).getBitmap()
                val m = Matrix()
                m.setRectToRect(
                    RectF(
                        0F, 0F, bitmap.getWidth().toFloat(),
                        bitmap.getHeight().toFloat()
                    ), RectF(0F, 0F, 170F, 40F), Matrix.ScaleToFit.CENTER
                )
                val bmp = Bitmap.createBitmap(
                    bitmap,
                    0,
                    0,
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    m,
                    true
                )
                val stream = ByteArrayOutputStream()
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val bitmapdata = stream.toByteArray()
                bgImage = Image.getInstance(bitmapdata)
                bgImage.setAlignment(Element.ALIGN_CENTER)
                doc.add(bgImage)

                cell = PdfPCell(
                    Paragraph(
                        Phrase(
                            lineSpacing, mOrganization,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                )
                cell.border = Rectangle.NO_BORDER
                myTable.addCell(cell)

                cell = PdfPCell(
                    Paragraph(
                        Phrase(
                            lineSpacing, poBox,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                )
                cell.border = Rectangle.NO_BORDER
                myTable.addCell(cell)

                cell = PdfPCell(
                    Paragraph(
                        Phrase(
                            lineSpacing, country,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                )
                cell.border = Rectangle.NO_BORDER
                myTable.addCell(cell)

                doc.add(myTable)

                // add a couple of blank lines
                doc.add(Chunk.NEWLINE)
                // add a couple of blank lines
                doc.add(Chunk.NEWLINE)
                val mTitle = Paragraph(
                    Phrase(
                        lineSpacing, "TAX INVOICE",
                        FontFactory.getFont(FontFactory.COURIER, headerFont, Font.BOLD)
                    )
                )
                mTitle.setAlignment(Element.ALIGN_CENTER)
                doc.add(mTitle)
                // add a couple of blank lines
                doc.add(Chunk.NEWLINE)
                myTable = PdfPTable(2)
                // 100.0f mean width of table is same as Document size
                myTable.setWidthPercentage(100.0f)
                // Create New Cell into Table
                val columnWidth1 = floatArrayOf(100f, 100f)
                myTable.setWidths(columnWidth1)
                cell = PdfPCell(
                    Paragraph(
                        Phrase(
                            lineSpacing, "Customer: " + `object`.getString("CUSTOMERID"),
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                )
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Invoice No: " + invoiceNo,
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                cell = PdfPCell(mPara)
                cell.setBorder(Rectangle.NO_BORDER)
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
                myTable.addCell(cell)
                cell = PdfPCell(
                    Paragraph(
                        Phrase(
                            lineSpacing, `object`.getString("CUSTOMER"),
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                )
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Date: $date $time",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                cell = PdfPCell(mPara)
                cell.setBorder(Rectangle.NO_BORDER)
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
                myTable.addCell(cell)
                cell = PdfPCell(
                    Paragraph(
                        Phrase(
                            lineSpacing, "Address: " + `object`.getString("ADDRESS"),
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                )
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Invoice Type: $invoiceType",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                cell = PdfPCell(mPara)
                cell.setBorder(Rectangle.NO_BORDER)
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
                myTable.addCell(cell)
                cell = PdfPCell(
                    Paragraph(
                        Phrase(
                            lineSpacing, "TRN No: " + `object`.getString("TRN"),
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                )
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                cell = PdfPCell(mPara)
                cell.setBorder(Rectangle.NO_BORDER)
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
                myTable.addCell(cell)
                doc.add(myTable)
                // add a couple of blank lines
                doc.add(Chunk.NEWLINE)
                val mSalesmanDetail = Paragraph(
                    Phrase(
                        lineSpacing, "Salesman: $salesmanNo-$mSalesman",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                doc.add(mSalesmanDetail)
                // add a couple of blank lines
                doc.add(Chunk.NEWLINE)
                var linesep = LineSeparator()
                linesep.setLineWidth(0.5f)
                linesep.setPercentage(100F)
                doc.add(linesep)
                myTable = PdfPTable(10)
                // 100.0f mean width of table is same as Document size
                myTable.setWidthPercentage(100.0f)
                // Create New Cell into Table
                var columnWidth2 = floatArrayOf(33f, 10f, 8f, 12f, 13f, 13f, 13f, 10f, 10f, 13f)
                myTable.setWidths(columnWidth2)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Item Description",
                        FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_LEFT)
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "UOM",
                        FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Qty",
                        FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Unit\nPrice",
                        FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Gross\nValue",
                        FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Disc.",
                        FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Net\nValue",
                        FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Vat\n(%)",
                        FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Vat",
                        FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Total",
                        FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                doc.add(myTable)
                linesep = LineSeparator()
                linesep.setLineWidth(0.5f)
                linesep.setPercentage(100F)
                doc.add(linesep)
                myTable = PdfPTable(10)
                myTable.setWidthPercentage(100.0f)
                columnWidth2 = floatArrayOf(33f, 10f, 8f, 12f, 13f, 13f, 13f, 10f, 10f, 13f)
                myTable.setWidths(columnWidth2)
                val jData = `object`.getJSONArray("data")
                for (i in 0 until jData.length()) {
                    val jArr = jData.getJSONArray(i)
                    val code = jArr.getString(0)
                    val name = jArr.getString(1)
                    val qty = jArr.getString(3)
                    val UOM = jArr.getString(2)
                    val price = jArr.getString(4)
                    val discount = jArr.getString(6)
                    val gross = jArr.getString(5)
                    val net = jArr.getString(7)
                    val vatPer = jArr.getString(8)
                    val vat = jArr.getString(9)
                    val total = jArr.getString(10)
                    val itemDesc = code + "\n" + name
                    mPara = Paragraph(
                        Phrase(
                            lineSpacing, itemDesc,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                    cell = PdfPCell(mPara)
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT)
                    cell.setBorder(Rectangle.NO_BORDER)
                    myTable.addCell(cell)
                    mPara = Paragraph(
                        Phrase(
                            lineSpacing, UOM,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                    mPara.setAlignment(Element.ALIGN_CENTER)
                    cell = PdfPCell(mPara)
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                    cell.setBorder(Rectangle.NO_BORDER)
                    myTable.addCell(cell)
                    mPara = Paragraph(
                        Phrase(
                            lineSpacing, qty,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                    mPara.setAlignment(Element.ALIGN_CENTER)
                    cell = PdfPCell(mPara)
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                    cell.setBorder(Rectangle.NO_BORDER)
                    myTable.addCell(cell)
                    mPara = Paragraph(
                        Phrase(
                            lineSpacing, price,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                    mPara.setAlignment(Element.ALIGN_CENTER)
                    cell = PdfPCell(mPara)
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                    cell.setBorder(Rectangle.NO_BORDER)
                    myTable.addCell(cell)
                    mPara = Paragraph(
                        Phrase(
                            lineSpacing, gross,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                    mPara.setAlignment(Element.ALIGN_CENTER)
                    cell = PdfPCell(mPara)
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                    cell.setBorder(Rectangle.NO_BORDER)
                    myTable.addCell(cell)
                    mPara = Paragraph(
                        Phrase(
                            lineSpacing, discount,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                    mPara.setAlignment(Element.ALIGN_CENTER)
                    cell = PdfPCell(mPara)
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                    cell.setBorder(Rectangle.NO_BORDER)
                    myTable.addCell(cell)
                    mPara = Paragraph(
                        Phrase(
                            lineSpacing, net,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                    mPara.setAlignment(Element.ALIGN_CENTER)
                    cell = PdfPCell(mPara)
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                    cell.setBorder(Rectangle.NO_BORDER)
                    myTable.addCell(cell)
                    mPara = Paragraph(
                        Phrase(
                            lineSpacing, vatPer,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                    mPara.setAlignment(Element.ALIGN_CENTER)
                    cell = PdfPCell(mPara)
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                    cell.setBorder(Rectangle.NO_BORDER)
                    myTable.addCell(cell)
                    mPara = Paragraph(
                        Phrase(
                            lineSpacing, vat,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                    mPara.setAlignment(Element.ALIGN_CENTER)
                    cell = PdfPCell(mPara)
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                    cell.setBorder(Rectangle.NO_BORDER)
                    myTable.addCell(cell)
                    mPara = Paragraph(
                        Phrase(
                            lineSpacing, total,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                    mPara.setAlignment(Element.ALIGN_CENTER)
                    cell = PdfPCell(mPara)
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                    cell.setBorder(Rectangle.NO_BORDER)
                    myTable.addCell(cell)
                }
                doc.add(myTable)
                linesep = LineSeparator()
                linesep.setLineWidth(0.5f)
                linesep.setPercentage(100F)
                doc.add(Chunk(linesep))
                val jTotal = `object`.getJSONArray("TOTAL")
                val jTOBject = jTotal.getJSONObject(0)
                val datatablebottom = PdfPTable(3)
                datatablebottom.setWidthPercentage(100.0f)
                columnWidth2 = floatArrayOf(92f, 30f, 13f)
                datatablebottom.setWidths(columnWidth2)
                //datatablebottom.setTableEvent(new MyTableEvent());
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Gross Amount :",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_LEFT)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, jTOBject.getString("Gross Total"),
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Discount :",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_LEFT)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, jTOBject.getString("Discount"),
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Net Total :",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_LEFT)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, jTOBject.getString("Net Total"),
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Vat Value :",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_LEFT)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, jTOBject.getString("VAT"),
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Total Amount :",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_LEFT)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, jTOBject.getString("Total Amount"),
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                doc.add(datatablebottom)
            } catch (de: DocumentException) {
                Log.e("PDFCreator", "DocumentException:" + de)
            } catch (e: IOException) {
                Log.e("PDFCreator", "ioException:" + e)
            } finally {
                doc.close()
            }
            //Send Mail
            sentMail(fileName, custEmail, "Invoice Receipt")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(FileNotFoundException::class, DocumentException::class)
    fun sendOrderMail(`object`: JSONObject) {
        val bgImage: Image
        val doc = Document(PageSize.A4, 10f, 10f, 10f, 10f)
        val fntSize: Float = 11f
        val lineSpacing: Float = 13f
        val headerFont: Float = 14f
        val fntTitleSize: Float = 12f
        try {

            var mSalesmanData = mPreferenceManager!!.getUserInfo()!!.data.userInfo
            val mSalesman = mSalesmanData.firstname + " " + mSalesmanData.lastname
            val salesmanNo = mSalesmanData.id.toString()
            var mOrganization = mSalesmanData.organisation.name
            var poBox =
                "PO Box: " + mSalesmanData.organisation.postal + ", " + mSalesmanData.organisation.city + ","
            var country =
                mSalesmanData.organisation.state + ", " + mSalesmanData.organisation.countryInfo!!.name

            val invoiceNo = `object`.getString("ordernumber")
            val date = `object`.getString("DOC DATE")
            val time = `object`.getString("TIME")
            val custEmail = `object`.getString("Email")
            val sdf = SimpleDateFormat("ddMMyyyy")
            val fileName = "Order_" + sdf.format(Calendar.getInstance().getTime()) + ".pdf"
            val file = File(
                (dir!!.path + File.separator
                        + fileName)
            )
            if (!file.exists()) {
                try {
                    file.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            val fOut = FileOutputStream(file)
            val writer = PdfWriter.getInstance(doc, fOut)
            //open the document
            doc.open()
            try {
                // Create Table into Document with 1 Row
                var myTable = PdfPTable(1)
                // 100.0f mean width of table is same as Document size
                // 100.0f mean width of table is same as Document size
                myTable.widthPercentage = 100.0f

                var cell: PdfPCell
                var mPara: Paragraph
                val myImage = getResources().getDrawable(R.mipmap.ic_launcher_invoice)
                val bitmap = (myImage as BitmapDrawable).getBitmap()
                val m = Matrix()
                m.setRectToRect(
                    RectF(
                        0F, 0F, bitmap.getWidth().toFloat(),
                        bitmap.getHeight().toFloat()
                    ), RectF(0F, 0F, 170F, 40F), Matrix.ScaleToFit.CENTER
                )
                val bmp = Bitmap.createBitmap(
                    bitmap,
                    0,
                    0,
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    m,
                    true
                )
                val stream = ByteArrayOutputStream()
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val bitmapdata = stream.toByteArray()
                bgImage = Image.getInstance(bitmapdata)
                bgImage.setAlignment(Element.ALIGN_CENTER)
                doc.add(bgImage)

                cell = PdfPCell(
                    Paragraph(
                        Phrase(
                            lineSpacing, mOrganization,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                )
                cell.border = Rectangle.NO_BORDER
                myTable.addCell(cell)

                cell = PdfPCell(
                    Paragraph(
                        Phrase(
                            lineSpacing, poBox,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                )
                cell.border = Rectangle.NO_BORDER
                myTable.addCell(cell)

                cell = PdfPCell(
                    Paragraph(
                        Phrase(
                            lineSpacing, country,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                )
                cell.border = Rectangle.NO_BORDER
                myTable.addCell(cell)

                doc.add(myTable)

                // add a couple of blank lines
                doc.add(Chunk.NEWLINE)
                // add a couple of blank lines
                doc.add(Chunk.NEWLINE)
                val mTitle = Paragraph(
                    Phrase(
                        lineSpacing, "ORDER REQUEST",
                        FontFactory.getFont(FontFactory.COURIER, headerFont, Font.BOLD)
                    )
                )
                mTitle.setAlignment(Element.ALIGN_CENTER)
                doc.add(mTitle)
                // add a couple of blank lines
                doc.add(Chunk.NEWLINE)
                myTable = PdfPTable(2)
                // 100.0f mean width of table is same as Document size
                myTable.setWidthPercentage(100.0f)
                // Create New Cell into Table
                val columnWidth1 = floatArrayOf(100f, 100f)
                myTable.setWidths(columnWidth1)
                cell = PdfPCell(
                    Paragraph(
                        Phrase(
                            lineSpacing, "Customer: " + `object`.getString("CUSTOMERID"),
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                )
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Order No: " + invoiceNo,
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                cell = PdfPCell(mPara)
                cell.setBorder(Rectangle.NO_BORDER)
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
                myTable.addCell(cell)
                cell = PdfPCell(
                    Paragraph(
                        Phrase(
                            lineSpacing, `object`.getString("CUSTOMER"),
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                )
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Date: $date $time",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                cell = PdfPCell(mPara)
                cell.setBorder(Rectangle.NO_BORDER)
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
                myTable.addCell(cell)
                cell = PdfPCell(
                    Paragraph(
                        Phrase(
                            lineSpacing, "Address: " + `object`.getString("ADDRESS"),
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                )
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                cell = PdfPCell(mPara)
                cell.setBorder(Rectangle.NO_BORDER)
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
                myTable.addCell(cell)
                cell = PdfPCell(
                    Paragraph(
                        Phrase(
                            lineSpacing, "TRN No: " + `object`.getString("TRN"),
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                )
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                cell = PdfPCell(mPara)
                cell.setBorder(Rectangle.NO_BORDER)
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
                myTable.addCell(cell)
                doc.add(myTable)
                // add a couple of blank lines
                doc.add(Chunk.NEWLINE)
                val mSalesmanDetail = Paragraph(
                    Phrase(
                        lineSpacing, "Salesman: $salesmanNo-$mSalesman",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                doc.add(mSalesmanDetail)
                // add a couple of blank lines
                doc.add(Chunk.NEWLINE)
                var linesep = LineSeparator()
                linesep.setLineWidth(0.5f)
                linesep.setPercentage(100F)
                doc.add(linesep)
                myTable = PdfPTable(10)
                // 100.0f mean width of table is same as Document size
                myTable.setWidthPercentage(100.0f)
                // Create New Cell into Table
                var columnWidth2 = floatArrayOf(33f, 10f, 8f, 12f, 13f, 13f, 13f, 10f, 10f, 13f)
                myTable.setWidths(columnWidth2)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Item Description",
                        FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_LEFT)
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "UOM",
                        FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Qty",
                        FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Unit\nPrice",
                        FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Gross\nValue",
                        FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Disc.",
                        FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Net\nValue",
                        FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Vat\n(%)",
                        FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Vat",
                        FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Total",
                        FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                doc.add(myTable)
                linesep = LineSeparator()
                linesep.setLineWidth(0.5f)
                linesep.setPercentage(100F)
                doc.add(linesep)
                myTable = PdfPTable(10)
                myTable.setWidthPercentage(100.0f)
                columnWidth2 = floatArrayOf(33f, 10f, 8f, 12f, 13f, 13f, 13f, 10f, 10f, 13f)
                myTable.setWidths(columnWidth2)
                val jData = `object`.getJSONArray("data")
                for (i in 0 until jData.length()) {
                    val jArr = jData.getJSONArray(i)
                    val code = jArr.getString(0)
                    val name = jArr.getString(1)
                    val qty = jArr.getString(3)
                    val UOM = jArr.getString(2)
                    val price = jArr.getString(4)
                    val discount = jArr.getString(6)
                    val gross = jArr.getString(5)
                    val net = jArr.getString(7)
                    val vatPer = jArr.getString(8)
                    val vat = jArr.getString(9)
                    val total = jArr.getString(10)
                    val itemDesc = code + "\n" + name
                    mPara = Paragraph(
                        Phrase(
                            lineSpacing, itemDesc,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                    cell = PdfPCell(mPara)
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT)
                    cell.setBorder(Rectangle.NO_BORDER)
                    myTable.addCell(cell)
                    mPara = Paragraph(
                        Phrase(
                            lineSpacing, UOM,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                    mPara.setAlignment(Element.ALIGN_CENTER)
                    cell = PdfPCell(mPara)
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                    cell.setBorder(Rectangle.NO_BORDER)
                    myTable.addCell(cell)
                    mPara = Paragraph(
                        Phrase(
                            lineSpacing, qty,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                    mPara.setAlignment(Element.ALIGN_CENTER)
                    cell = PdfPCell(mPara)
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                    cell.setBorder(Rectangle.NO_BORDER)
                    myTable.addCell(cell)
                    mPara = Paragraph(
                        Phrase(
                            lineSpacing, price,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                    mPara.setAlignment(Element.ALIGN_CENTER)
                    cell = PdfPCell(mPara)
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                    cell.setBorder(Rectangle.NO_BORDER)
                    myTable.addCell(cell)
                    mPara = Paragraph(
                        Phrase(
                            lineSpacing, gross,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                    mPara.setAlignment(Element.ALIGN_CENTER)
                    cell = PdfPCell(mPara)
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                    cell.setBorder(Rectangle.NO_BORDER)
                    myTable.addCell(cell)
                    mPara = Paragraph(
                        Phrase(
                            lineSpacing, discount,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                    mPara.setAlignment(Element.ALIGN_CENTER)
                    cell = PdfPCell(mPara)
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                    cell.setBorder(Rectangle.NO_BORDER)
                    myTable.addCell(cell)
                    mPara = Paragraph(
                        Phrase(
                            lineSpacing, net,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                    mPara.setAlignment(Element.ALIGN_CENTER)
                    cell = PdfPCell(mPara)
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                    cell.setBorder(Rectangle.NO_BORDER)
                    myTable.addCell(cell)
                    mPara = Paragraph(
                        Phrase(
                            lineSpacing, vatPer,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                    mPara.setAlignment(Element.ALIGN_CENTER)
                    cell = PdfPCell(mPara)
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                    cell.setBorder(Rectangle.NO_BORDER)
                    myTable.addCell(cell)
                    mPara = Paragraph(
                        Phrase(
                            lineSpacing, vat,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                    mPara.setAlignment(Element.ALIGN_CENTER)
                    cell = PdfPCell(mPara)
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                    cell.setBorder(Rectangle.NO_BORDER)
                    myTable.addCell(cell)
                    mPara = Paragraph(
                        Phrase(
                            lineSpacing, total,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                    mPara.setAlignment(Element.ALIGN_CENTER)
                    cell = PdfPCell(mPara)
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                    cell.setBorder(Rectangle.NO_BORDER)
                    myTable.addCell(cell)
                }
                doc.add(myTable)
                linesep = LineSeparator()
                linesep.setLineWidth(0.5f)
                linesep.setPercentage(100F)
                doc.add(Chunk(linesep))
                val jTotal = `object`.getJSONArray("TOTAL")
                val jTOBject = jTotal.getJSONObject(0)
                val datatablebottom = PdfPTable(3)
                datatablebottom.setWidthPercentage(100.0f)
                columnWidth2 = floatArrayOf(92f, 30f, 13f)
                datatablebottom.setWidths(columnWidth2)
                //datatablebottom.setTableEvent(new MyTableEvent());
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Gross Amount :",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_LEFT)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, jTOBject.getString("Gross Total"),
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Discount :",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_LEFT)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, jTOBject.getString("Discount"),
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Net Total :",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_LEFT)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, jTOBject.getString("Net Total"),
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Vat Value :",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_LEFT)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, jTOBject.getString("VAT"),
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Total Amount :",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_LEFT)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, jTOBject.getString("Total Amount"),
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                doc.add(datatablebottom)
            } catch (de: DocumentException) {
                Log.e("PDFCreator", "DocumentException:" + de)
            } catch (e: IOException) {
                Log.e("PDFCreator", "ioException:" + e)
            } finally {
                doc.close()
            }
            //Send Mail
            sentMail(fileName, custEmail, "Order Receipt")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(FileNotFoundException::class, DocumentException::class)
    fun sendReturnMail(`object`: JSONObject) {
        val bgImage: Image
        val doc = Document(PageSize.A4, 10f, 10f, 10f, 10f)
        val fntSize: Float = 11f
        val lineSpacing: Float = 13f
        val headerFont: Float = 14f
        val fntTitleSize: Float = 12f
        try {

            var mSalesmanData = mPreferenceManager!!.getUserInfo()!!.data.userInfo
            val mSalesman = mSalesmanData.firstname + " " + mSalesmanData.lastname
            val salesmanNo = mSalesmanData.id.toString()
            var mOrganization = mSalesmanData.organisation.name
            var poBox =
                "PO Box: " + mSalesmanData.organisation.postal + ", " + mSalesmanData.organisation.city + ","
            var country =
                mSalesmanData.organisation.state + ", " + mSalesmanData.organisation.countryInfo!!.name

            val invoiceNo = `object`.getString("returnnumber")
            val date = `object`.getString("DOC DATE")
            val time = `object`.getString("TIME")
            val custEmail = `object`.getString("Email")
            val sdf = SimpleDateFormat("ddMMyyyy")
            val fileName = "Order_" + sdf.format(Calendar.getInstance().getTime()) + ".pdf"
            val file = File(
                (dir!!.path + File.separator
                        + fileName)
            )
            if (!file.exists()) {
                try {
                    file.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            val fOut = FileOutputStream(file)
            val writer = PdfWriter.getInstance(doc, fOut)
            //open the document
            doc.open()
            try {
                // Create Table into Document with 1 Row
                var myTable = PdfPTable(1)
                // 100.0f mean width of table is same as Document size
                // 100.0f mean width of table is same as Document size
                myTable.widthPercentage = 100.0f

                var cell: PdfPCell
                var mPara: Paragraph
                val myImage = getResources().getDrawable(R.mipmap.ic_launcher_invoice)
                val bitmap = (myImage as BitmapDrawable).getBitmap()
                val m = Matrix()
                m.setRectToRect(
                    RectF(
                        0F, 0F, bitmap.getWidth().toFloat(),
                        bitmap.getHeight().toFloat()
                    ), RectF(0F, 0F, 170F, 40F), Matrix.ScaleToFit.CENTER
                )
                val bmp = Bitmap.createBitmap(
                    bitmap,
                    0,
                    0,
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    m,
                    true
                )
                val stream = ByteArrayOutputStream()
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val bitmapdata = stream.toByteArray()
                bgImage = Image.getInstance(bitmapdata)
                bgImage.setAlignment(Element.ALIGN_CENTER)
                doc.add(bgImage)

                cell = PdfPCell(
                    Paragraph(
                        Phrase(
                            lineSpacing, mOrganization,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                )
                cell.border = Rectangle.NO_BORDER
                myTable.addCell(cell)

                cell = PdfPCell(
                    Paragraph(
                        Phrase(
                            lineSpacing, poBox,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                )
                cell.border = Rectangle.NO_BORDER
                myTable.addCell(cell)

                cell = PdfPCell(
                    Paragraph(
                        Phrase(
                            lineSpacing, country,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                )
                cell.border = Rectangle.NO_BORDER
                myTable.addCell(cell)

                doc.add(myTable)

                // add a couple of blank lines
                doc.add(Chunk.NEWLINE)
                // add a couple of blank lines
                doc.add(Chunk.NEWLINE)
                val mTitle = Paragraph(
                    Phrase(
                        lineSpacing, "TAX CREDIT NOTE",
                        FontFactory.getFont(FontFactory.COURIER, headerFont, Font.BOLD)
                    )
                )
                mTitle.setAlignment(Element.ALIGN_CENTER)
                doc.add(mTitle)
                // add a couple of blank lines
                doc.add(Chunk.NEWLINE)
                myTable = PdfPTable(2)
                // 100.0f mean width of table is same as Document size
                myTable.setWidthPercentage(100.0f)
                // Create New Cell into Table
                val columnWidth1 = floatArrayOf(100f, 100f)
                myTable.setWidths(columnWidth1)
                cell = PdfPCell(
                    Paragraph(
                        Phrase(
                            lineSpacing, "Customer: " + `object`.getString("CUSTOMERID"),
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                )
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Credit No: $invoiceNo",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                cell = PdfPCell(mPara)
                cell.setBorder(Rectangle.NO_BORDER)
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
                myTable.addCell(cell)
                cell = PdfPCell(
                    Paragraph(
                        Phrase(
                            lineSpacing, `object`.getString("CUSTOMER"),
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                )
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Date: $date $time",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                cell = PdfPCell(mPara)
                cell.setBorder(Rectangle.NO_BORDER)
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
                myTable.addCell(cell)
                cell = PdfPCell(
                    Paragraph(
                        Phrase(
                            lineSpacing, "Address: " + `object`.getString("ADDRESS"),
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                )
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                cell = PdfPCell(mPara)
                cell.setBorder(Rectangle.NO_BORDER)
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
                myTable.addCell(cell)
                cell = PdfPCell(
                    Paragraph(
                        Phrase(
                            lineSpacing, "TRN No: " + `object`.getString("TRN"),
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                )
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                cell = PdfPCell(mPara)
                cell.setBorder(Rectangle.NO_BORDER)
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
                myTable.addCell(cell)
                doc.add(myTable)
                // add a couple of blank lines
                doc.add(Chunk.NEWLINE)
                val mSalesmanDetail = Paragraph(
                    Phrase(
                        lineSpacing, "Salesman: $salesmanNo-$mSalesman",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                doc.add(mSalesmanDetail)
                // add a couple of blank lines
                doc.add(Chunk.NEWLINE)
                var linesep = LineSeparator()
                linesep.setLineWidth(0.5f)
                linesep.setPercentage(100F)
                doc.add(linesep)
                myTable = PdfPTable(10)
                // 100.0f mean width of table is same as Document size
                myTable.setWidthPercentage(100.0f)
                // Create New Cell into Table
                var columnWidth2 = floatArrayOf(33f, 10f, 8f, 12f, 13f, 13f, 13f, 10f, 10f, 13f)
                myTable.setWidths(columnWidth2)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Item Description",
                        FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_LEFT)
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "UOM",
                        FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Qty",
                        FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Unit\nPrice",
                        FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Gross\nValue",
                        FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Disc.",
                        FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Net\nValue",
                        FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Vat\n(%)",
                        FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Vat",
                        FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Total",
                        FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                myTable.addCell(cell)
                doc.add(myTable)
                linesep = LineSeparator()
                linesep.setLineWidth(0.5f)
                linesep.setPercentage(100F)
                doc.add(linesep)
                myTable = PdfPTable(10)
                myTable.setWidthPercentage(100.0f)
                columnWidth2 = floatArrayOf(33f, 10f, 8f, 12f, 13f, 13f, 13f, 10f, 10f, 13f)
                myTable.setWidths(columnWidth2)
                val jData = `object`.getJSONArray("data")
                for (i in 0 until jData.length()) {
                    val jArr = jData.getJSONArray(i)
                    val code = jArr.getString(0)
                    val name = jArr.getString(1)
                    val qty = jArr.getString(3)
                    val UOM = jArr.getString(2)
                    val price = jArr.getString(4)
                    val discount = jArr.getString(6)
                    val gross = jArr.getString(5)
                    val net = jArr.getString(7)
                    val vatPer = jArr.getString(8)
                    val vat = jArr.getString(9)
                    val total = jArr.getString(10)
                    val itemDesc = code + "\n" + name
                    mPara = Paragraph(
                        Phrase(
                            lineSpacing, itemDesc,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                    cell = PdfPCell(mPara)
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT)
                    cell.setBorder(Rectangle.NO_BORDER)
                    myTable.addCell(cell)
                    mPara = Paragraph(
                        Phrase(
                            lineSpacing, UOM,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                    mPara.setAlignment(Element.ALIGN_CENTER)
                    cell = PdfPCell(mPara)
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                    cell.setBorder(Rectangle.NO_BORDER)
                    myTable.addCell(cell)
                    mPara = Paragraph(
                        Phrase(
                            lineSpacing, qty,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                    mPara.setAlignment(Element.ALIGN_CENTER)
                    cell = PdfPCell(mPara)
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                    cell.setBorder(Rectangle.NO_BORDER)
                    myTable.addCell(cell)
                    mPara = Paragraph(
                        Phrase(
                            lineSpacing, price,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                    mPara.setAlignment(Element.ALIGN_CENTER)
                    cell = PdfPCell(mPara)
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                    cell.setBorder(Rectangle.NO_BORDER)
                    myTable.addCell(cell)
                    mPara = Paragraph(
                        Phrase(
                            lineSpacing, gross,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                    mPara.setAlignment(Element.ALIGN_CENTER)
                    cell = PdfPCell(mPara)
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                    cell.setBorder(Rectangle.NO_BORDER)
                    myTable.addCell(cell)
                    mPara = Paragraph(
                        Phrase(
                            lineSpacing, discount,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                    mPara.setAlignment(Element.ALIGN_CENTER)
                    cell = PdfPCell(mPara)
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                    cell.setBorder(Rectangle.NO_BORDER)
                    myTable.addCell(cell)
                    mPara = Paragraph(
                        Phrase(
                            lineSpacing, net,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                    mPara.setAlignment(Element.ALIGN_CENTER)
                    cell = PdfPCell(mPara)
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                    cell.setBorder(Rectangle.NO_BORDER)
                    myTable.addCell(cell)
                    mPara = Paragraph(
                        Phrase(
                            lineSpacing, vatPer,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                    mPara.setAlignment(Element.ALIGN_CENTER)
                    cell = PdfPCell(mPara)
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                    cell.setBorder(Rectangle.NO_BORDER)
                    myTable.addCell(cell)
                    mPara = Paragraph(
                        Phrase(
                            lineSpacing, vat,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                    mPara.setAlignment(Element.ALIGN_CENTER)
                    cell = PdfPCell(mPara)
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                    cell.setBorder(Rectangle.NO_BORDER)
                    myTable.addCell(cell)
                    mPara = Paragraph(
                        Phrase(
                            lineSpacing, total,
                            FontFactory.getFont(FontFactory.COURIER, fntSize)
                        )
                    )
                    mPara.setAlignment(Element.ALIGN_CENTER)
                    cell = PdfPCell(mPara)
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                    cell.setBorder(Rectangle.NO_BORDER)
                    myTable.addCell(cell)
                }
                doc.add(myTable)
                linesep = LineSeparator()
                linesep.setLineWidth(0.5f)
                linesep.setPercentage(100F)
                doc.add(Chunk(linesep))
                val jTotal = `object`.getJSONArray("TOTAL")
                val jTOBject = jTotal.getJSONObject(0)
                val datatablebottom = PdfPTable(3)
                datatablebottom.setWidthPercentage(100.0f)
                columnWidth2 = floatArrayOf(92f, 30f, 13f)
                datatablebottom.setWidths(columnWidth2)
                //datatablebottom.setTableEvent(new MyTableEvent());
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Gross Amount :",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_LEFT)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, jTOBject.getString("Gross Total"),
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Discount :",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_LEFT)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, jTOBject.getString("Discount"),
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Net Total :",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_LEFT)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, jTOBject.getString("Net Total"),
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Vat Value :",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_LEFT)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, jTOBject.getString("VAT"),
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_CENTER)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, "Total Amount :",
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_LEFT)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                mPara = Paragraph(
                    Phrase(
                        lineSpacing, jTOBject.getString("Total Amount"),
                        FontFactory.getFont(FontFactory.COURIER, fntSize)
                    )
                )
                mPara.setAlignment(Element.ALIGN_CENTER)
                cell = PdfPCell(mPara)
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT)
                cell.setBorder(Rectangle.NO_BORDER)
                datatablebottom.addCell(cell)
                doc.add(datatablebottom)
            } catch (de: DocumentException) {
                Log.e("PDFCreator", "DocumentException:" + de)
            } catch (e: IOException) {
                Log.e("PDFCreator", "ioException:" + e)
            } finally {
                doc.close()
            }
            //Send Mail
            sentMail(fileName, custEmail, "Credit Note Receipt")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sentMail(fileName: String, custEmail: String, subject: String) {

//        Thread {
//            try {
//                val sender = GMailSender(
//                    "mobiato.vns1@gmail.com",
//                    "Mobiato@1234"
//                )
//                sender.addAttachment(dir!!.path + "/" + fileName)
//                sender.sendMail(
//                    subject, "This mail has been sent from android app along with attachment",
//                    "mobiato.vns1@gmail.com",
//                    custEmail
//                )
//            } catch (e: java.lang.Exception) {
//                Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG).show()
//            }
//        }.start()
    }

    /*****************************************************************************************
     * @ Creating data for print and saving it in DB for future printing
     */
    fun createPrintSalesSummaryData(): JSONArray? {
        val jArr = JSONArray()
        try {
            //HEADERS.put("T.C");
            var TotalCase = 0.0
            var TotalCasePaid = 0.0
            var TotalCredit = 0.0
            var TotalCreditPaid = 0.0
            var TotalRCredit = 0.0
            var TotalRCreditPaid = 0.0
            var TotalRCase = 0.0
            var TotalRCasePaid = 0.0
            var TotalSales = 0.0
            var TotalSalesPaid = 0.0
            val jInter = JSONArray()
            val jDict = JSONObject()
            jDict.put(AppConstants.REQUEST, AppConstants.SALES_SUMMARY)
            val mainArr = JSONObject()
            var routeCode =
                mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode
            var vanName = routeCode.replace("Zone", "Vansales")
            mainArr.put("ROUTE", vanName)
            mainArr.put("DOC DATE", Utils.formatDate(Date(), "dd-MM-yyyy"))
            mainArr.put("TIME", Utils.formatTime(Date(), "hh:mm"))
            mainArr.put("SALESMAN", mPreferenceManager!!.getString(AppConstants.DRIVERNAME))
            mainArr.put("CONTACTNO", "-")
            mainArr.put("DOCUMENT NO", "") //Load Summary No
            mainArr.put("TRIP START DATE", mPreferenceManager!!.getTripDate())
            mainArr.put("supervisorname", "-")
            mainArr.put("orderNo", "") //Invoice No
            mainArr.put("mType", "Invoice") //Invoice No

            val TOTAL = JSONArray()
            val HEADERS = JSONArray()
            val HEADERSCREDIT = JSONArray()
            HEADERS.put("Sr No")
            HEADERS.put("Transaction No.")
            HEADERS.put("Cust. No.")
            HEADERS.put("Customer")
            HEADERS.put("Sales")
            HEADERS.put("Amount Paid")

            HEADERSCREDIT.put("Sr No")
            HEADERSCREDIT.put("Transaction No.")
            HEADERSCREDIT.put("Cust. No.")
            HEADERSCREDIT.put("Customer")
            HEADERSCREDIT.put("Returns")
            HEADERSCREDIT.put("Amount Paid")

            mainArr.put("HEADERS", HEADERS)
            mainArr.put("HEADERSCreditNote", HEADERSCREDIT)

            //SALES INVOICE
            val jCashData = JSONArray()
            val jCreditData = JSONArray()
            val jRCashData = JSONArray()
            val jRCreditData = JSONArray()
            for (csInvoice in cashInvoiceList!!) {

                val data = JSONArray()
                data.put("" + (jCashData.length() + 1))
                data.put(csInvoice.invoiceNo) // Transaction No
                data.put(csInvoice.customerCode) // CUstomer No
                data.put(csInvoice.customerName) //  Customer
                data.put(Utils.twoDigit(csInvoice.total!!.toFloat())) // Sales
                data.put(Utils.twoDigit(csInvoice.paidAmount!!.toFloat())) // Amt Paid
                jCashData.put(data)

                TotalCase += csInvoice.total!!.toDouble()
                TotalCasePaid += csInvoice.paidAmount!!.toDouble()
            }

            for (crInvoice in creditInvoiceList!!) {

                val data = JSONArray()
                data.put("" + (jCreditData.length() + 1))
                data.put(crInvoice.invoiceNo) // Transaction No
                data.put(crInvoice.customerCode) // CUstomer No
                data.put(crInvoice.customerName) //  Customer
                data.put(Utils.twoDigit(crInvoice.total!!.toFloat())) // Sales
                data.put(Utils.twoDigit(crInvoice.paidAmount!!.toFloat())) // Amt Paid
                jCreditData.put(data)

                TotalCredit += crInvoice.total!!.toDouble()
                TotalCreditPaid += crInvoice.paidAmount!!.toDouble()
            }

            for (crRInvoice in creditReturnList!!) {

                val data = JSONArray()
                data.put("" + (jRCreditData.length() + 1))
                data.put(crRInvoice.invoiceNo) // Transaction No
                data.put(crRInvoice.customerCode) // CUstomer No
                data.put(crRInvoice.customerName) //  Customer
                data.put(Utils.twoDigit(crRInvoice.total!!.toFloat())) // Sales
                data.put("0.0") // Amt Paid
                jRCreditData.put(data)

                TotalRCredit += crRInvoice.total!!.toDouble()
            }

            for (cRInvoice in cashReturnList!!) {

                val data = JSONArray()
                data.put("" + (jCreditData.length() + 1))
                data.put(cRInvoice.invoiceNo) // Transaction No
                data.put(cRInvoice.customerCode) // CUstomer No
                data.put(cRInvoice.customerName) //  Customer
                data.put(Utils.twoDigit(cRInvoice.total!!.toFloat())) // Sales
                data.put(Utils.twoDigit(cRInvoice.total!!.toFloat())) // Amt Paid
                jRCashData.put(data)

                TotalRCase += cRInvoice.total!!.toDouble()
                TotalRCasePaid += cRInvoice.total!!.toDouble()
            }

            TotalSales = (TotalCase + TotalCredit) - TotalRCase - TotalRCredit
            TotalSalesPaid = (TotalCasePaid + TotalCreditPaid) - TotalRCasePaid - TotalRCreditPaid

            val totalObj = JSONObject()
            totalObj.put("Total_Case", "" + String.format("%.2f", TotalCase))
            totalObj.put("Total_Credit", "" + String.format("%.2f", TotalCredit))
            // totalObj.put("Total_TC", "" + String.format("%.2f", TotalTC))
            //  totalObj.put("Total_CreditNote", "" + String.format("%.2f", TotalCreditNote))
            totalObj.put("Total_Sales", "" + String.format("%.2f", TotalSales))
            totalObj.put("Total_CasePaid", "" + String.format("%.2f", TotalCasePaid))
            totalObj.put("Total_CreditPaid", "" + String.format("%.2f", TotalCreditPaid))
            totalObj.put("Total_RCasePaid", "" + String.format("%.2f", TotalRCasePaid))
            totalObj.put("Total_RCreditPaid", "" + String.format("%.2f", TotalRCreditPaid))
            totalObj.put("Total_RCase", "" + String.format("%.2f", TotalRCase))
            totalObj.put("Total_RCredit", "" + String.format("%.2f", TotalRCredit))
            //  totalObj.put("Total_TCPaid", "" + String.format("%.2f", TotalTCPaid))
            // totalObj.put("Total_CreditNotePaid", "" + String.format("%.2f", TotalCreditNotePaid))
            totalObj.put("Total_SalesPaid", "" + String.format("%.2f", TotalSalesPaid))
            TOTAL.put(totalObj)
            mainArr.put("TOTAL", TOTAL)
            mainArr.put("data", jCashData)
            mainArr.put("creditdata", jCreditData)
            mainArr.put("cashRdata", jRCashData)
            mainArr.put("creditRdata", jRCreditData)
            jDict.put("mainArr", mainArr)
            jInter.put(jDict)
            jArr.put(jInter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jArr
    }

    fun createPrintOrderSummaryData(): JSONArray? {
        val jArr = JSONArray()
        try {
            //HEADERS.put("T.C");
            var TotalCase = 0.0
            var TotalCasePaid = 0.0
            var TotalCredit = 0.0
            var TotalCreditPaid = 0.0
            var TotalRCredit = 0.0
            var TotalRCreditPaid = 0.0
            var TotalRCase = 0.0
            var TotalRCasePaid = 0.0
            var TotalSales = 0.0
            var TotalSalesPaid = 0.0
            val jInter = JSONArray()
            val jDict = JSONObject()
            jDict.put(AppConstants.REQUEST, AppConstants.SALES_SUMMARY)
            val mainArr = JSONObject()
            var routeCode =
                mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode
            var vanName = routeCode.replace("Zone", "Vansales")
            mainArr.put("ROUTE", vanName)
            mainArr.put("DOC DATE", Utils.formatDate(Date(), "dd-MM-yyyy"))
            mainArr.put("TIME", Utils.formatTime(Date(), "hh:mm"))
            mainArr.put("SALESMAN", mPreferenceManager!!.getString(AppConstants.DRIVERNAME))
            mainArr.put("CONTACTNO", "-")
            mainArr.put("DOCUMENT NO", "") //Load Summary No
            mainArr.put("TRIP START DATE", mPreferenceManager!!.getTripDate())
            mainArr.put("supervisorname", "-")
            mainArr.put("orderNo", "") //Invoice No
            mainArr.put("mType", "Order") //Invoice No

            val TOTAL = JSONArray()
            val HEADERS = JSONArray()
            val HEADERSCREDIT = JSONArray()
            HEADERS.put("Sr No")
            HEADERS.put("Transaction No.")
            HEADERS.put("Cust. No.")
            HEADERS.put("Customer")
            HEADERS.put("Sales")
            HEADERS.put("Amount Paid")

            HEADERSCREDIT.put("Sr No")
            HEADERSCREDIT.put("Transaction No.")
            HEADERSCREDIT.put("Cust. No.")
            HEADERSCREDIT.put("Customer")
            HEADERSCREDIT.put("Returns")
            HEADERSCREDIT.put("Amount Paid")

            mainArr.put("HEADERS", HEADERS)
            mainArr.put("HEADERSCreditNote", HEADERSCREDIT)

            //SALES INVOICE
            val jCashData = JSONArray()
            val jCreditData = JSONArray()
            val jRCashData = JSONArray()
            val jRCreditData = JSONArray()
            for (csInvoice in cashInvoiceList!!) {

                val data = JSONArray()
                data.put("" + (jCashData.length() + 1))
                data.put(csInvoice.invoiceNo) // Transaction No
                data.put(csInvoice.customerCode) // CUstomer No
                data.put(csInvoice.customerName) //  Customer
                data.put(Utils.twoDigit(csInvoice.total!!.toFloat())) // Sales
                data.put(Utils.twoDigit(csInvoice.paidAmount!!.toFloat())) // Amt Paid
                jCashData.put(data)

                TotalCase += csInvoice.total!!.toDouble()
                TotalCasePaid += csInvoice.paidAmount!!.toDouble()
            }

            for (cRInvoice in cashReturnList!!) {

                val data = JSONArray()
                data.put("" + (jCreditData.length() + 1))
                data.put(cRInvoice.invoiceNo) // Transaction No
                data.put(cRInvoice.customerCode) // CUstomer No
                data.put(cRInvoice.customerName) //  Customer
                data.put(Utils.twoDigit(cRInvoice.total!!.toFloat())) // Sales
                data.put(Utils.twoDigit(cRInvoice.total!!.toFloat())) // Amt Paid
                jRCashData.put(data)

                TotalRCase += cRInvoice.total!!.toDouble()
                TotalRCasePaid += cRInvoice.total!!.toDouble()
            }

            TotalSales = (TotalCase + TotalCredit) - TotalRCase - TotalRCredit
            TotalSalesPaid = (TotalCasePaid + TotalCreditPaid) - TotalRCasePaid - TotalRCreditPaid

            val totalObj = JSONObject()
            totalObj.put("Total_Case", "" + String.format("%.2f", TotalCase))
            totalObj.put("Total_Credit", "" + String.format("%.2f", TotalCredit))
            // totalObj.put("Total_TC", "" + String.format("%.2f", TotalTC))
            //  totalObj.put("Total_CreditNote", "" + String.format("%.2f", TotalCreditNote))
            totalObj.put("Total_Sales", "" + String.format("%.2f", TotalSales))
            totalObj.put("Total_CasePaid", "" + String.format("%.2f", TotalCasePaid))
            totalObj.put("Total_CreditPaid", "" + String.format("%.2f", TotalCreditPaid))
            totalObj.put("Total_RCasePaid", "" + String.format("%.2f", TotalRCasePaid))
            totalObj.put("Total_RCreditPaid", "" + String.format("%.2f", TotalRCreditPaid))
            totalObj.put("Total_RCase", "" + String.format("%.2f", TotalRCase))
            totalObj.put("Total_RCredit", "" + String.format("%.2f", TotalRCredit))
            //  totalObj.put("Total_TCPaid", "" + String.format("%.2f", TotalTCPaid))
            // totalObj.put("Total_CreditNotePaid", "" + String.format("%.2f", TotalCreditNotePaid))
            totalObj.put("Total_SalesPaid", "" + String.format("%.2f", TotalSalesPaid))
            TOTAL.put(totalObj)
            mainArr.put("TOTAL", TOTAL)
            mainArr.put("data", jCashData)
            mainArr.put("creditdata", jCreditData)
            mainArr.put("cashRdata", jRCashData)
            mainArr.put("creditRdata", jRCreditData)
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
    fun createPrintDepositeData(): JSONArray? {
        val jArr = JSONArray()
        try {
            //HEADERS.put("T.C");
            var TotalCase = 0.0
            var TotalCasePaid = 0.0
            var TotalCard = 0.0
            var TotalCredit = 0.0
            var TotaliCash = 0.0
            var TotalBalanceCash = 0.0
            var TotalCreditPaid = 0.0
            var TotalSalesPaid = 0.0
            val jInter = JSONArray()
            val jDict = JSONObject()
            jDict.put(AppConstants.REQUEST, AppConstants.DEPOSITE_SUMMARY)
            val mainArr = JSONObject()
            var routeCode =
                mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode
            var vanName = routeCode.replace("Zone", "Vansales")
            mainArr.put("ROUTE", vanName)
            mainArr.put("DOC DATE", Utils.formatDate(Date(), "dd-MM-yyyy"))
            mainArr.put("TIME", Utils.formatTime(Date(), "hh:mm"))
            mainArr.put("SALESMAN", mPreferenceManager!!.getString(AppConstants.DRIVERNAME))
            mainArr.put("CONTACTNO", "-")
            mainArr.put("DOCUMENT NO", "") //Load Summary No
            mainArr.put("TRIP START DATE", mPreferenceManager!!.getTripDate())
            mainArr.put("supervisorname", "-")
            mainArr.put("orderNo", "") //Invoice No

            val TOTAL = JSONArray()
            val HEADERS = JSONArray()
            HEADERS.put("Sr No")
            HEADERS.put("Receipt No")
            HEADERS.put("Customer Code")
            HEADERS.put("Customer Name")
            HEADERS.put("Cheque No")
            HEADERS.put("Cheque Amount")
            HEADERS.put("Cash Amount")

            mainArr.put("HEADERS", HEADERS)

            //SALES INVOICE
            val jCashData = JSONArray()
            val jCreditData = JSONArray()
            for (csInvoice in collectionList!!) {

                val data = JSONArray()
                data.put("" + (jCashData.length() + 1))
                data.put(csInvoice.collectionNo) // Transaction No
                data.put(csInvoice.customerCode) // Sales
                data.put(csInvoice.customerName) // Amt Paid
                if (csInvoice.collectionType == "2") {
                    data.put(csInvoice.chequeNo) // Amt Paid
                    data.put(csInvoice.amountPaid) // Amt Paid
                    data.put("0.00") // Amt Paid
                    data.put("0.00") // Card Paid
                    data.put("0.00") // Card Paid
                    TotalCredit += csInvoice.amountPaid!!.toDouble()
                }else if (csInvoice.collectionType == "5") {
                    data.put("-") // Amt Paid
                    data.put("0.00") // Amt Paid
                    data.put("0.00") // Amt Paid
                    data.put("0.00") // Card Paid
                    data.put(csInvoice.amountPaid) // Card Paid
                    TotaliCash += csInvoice.amountPaid!!.toDouble()
                }else if (csInvoice.collectionType == "7") {
                    data.put("-") // Amt Paid
                    data.put(csInvoice.amountPaid) // Amt Paid
                    data.put("0.00") // Amt Paid
                    data.put("0.00") // Card Paid
                    data.put("0.0") // Card Paid
                    TotalBalanceCash += csInvoice.amountPaid!!.toDouble()
                }else if (csInvoice.collectionType == "6") {
                    data.put("-") // Amt Paid
                    data.put("0.00") // Amt Paid
                    data.put("0.00") // Amt Paid
                    data.put("0.00") // Card Paid
                    data.put(csInvoice.amountPaid) // Card Paid
                    TotaliCash += csInvoice.amountPaid!!.toDouble()
                } else if (csInvoice.collectionType == "3") {
                    data.put("-") // Amt Paid
                    data.put("0.00") // Amt Paid
                    data.put("0.00") // Amt Paid
                    data.put(csInvoice.amountPaid) // Amt Paid
                    data.put("0.00") // Card Paid
                    TotalCard += csInvoice.amountPaid!!.toDouble()
                } else {
                    data.put("-") // Amt Paid
                    data.put("0.00") // Amt Paid
                    data.put(csInvoice.amountPaid) // Amt Paid
                    data.put("0.00") // Card Paid
                    data.put("0.00") // Card Paid
                    TotalCase += csInvoice.amountPaid!!.toDouble()
                }

                jCashData.put(data)

            }

            TotalSalesPaid = TotalCasePaid + TotalCreditPaid

            val totalObj = JSONObject()
            totalObj.put("Total_Case", "" + String.format("%.2f", TotalCase))
            totalObj.put("Total_Credit", "" + String.format("%.2f", TotalCredit))
            totalObj.put("Total_Card", "" + String.format("%.2f", TotalCard))
            totalObj.put("Total_iCash", "" + String.format("%.2f", TotaliCash))
            totalObj.put("Total_balanceCash", "" + String.format("%.2f", TotalBalanceCash))
            totalObj.put("Total_Sales", "" + String.format("%.2f", TotalSalesPaid))
            TOTAL.put(totalObj)
            mainArr.put("TOTAL", TOTAL)
            mainArr.put("data", jCashData)
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
    fun createPrintDailyOtherData(): JSONArray? {
        val jArr = JSONArray()
        try {
            //HEADERS.put("T.C");
            var TotalSalesPaid = 0F
            var TotalSale = 0F
            var TotalReturn = 0F
            var TotalNet = 0F
            var TotalCash = 0F
            var TotalCheque = 0F
            val jInter = JSONArray()
            val jDict = JSONObject()
            jDict.put(AppConstants.REQUEST, AppConstants.DAILY_OTHER_SUMMARY)
            val mainArr = JSONObject()
            var routeCode =
                mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode
            var vanName = routeCode.replace("Zone", "Vansales")
            mainArr.put("ROUTE", vanName)
            mainArr.put("DOC DATE", Utils.formatDate(Date(), "dd-MM-yyyy"))
            mainArr.put("TIME", Utils.formatTime(Date(), "hh:mm"))
            mainArr.put("SALESMAN", mPreferenceManager!!.getString(AppConstants.DRIVERNAME))
            mainArr.put("CONTACTNO", "-")
            mainArr.put("DOCUMENT NO", "") //Load Summary No
            mainArr.put("TRIP START DATE", mPreferenceManager!!.getTripDate())
            mainArr.put("helperName", "")
            mainArr.put("startTime", mPreferenceManager!!.getString(AppConstants.STARTTIME))
            mainArr.put("endTime", "")
            mainArr.put("orderNo", "") //Invoice No
            mainArr.put("invTime", "") //Invoice No
            mainArr.put("ivEndTime", "") //Invoice No

            val TOTAL = JSONArray()
            val HEADERS = JSONArray()
            HEADERS.put("Sr No")
            HEADERS.put("Cus. No")
            HEADERS.put("Name")
            HEADERS.put("P.O.B")
            HEADERS.put("Delivered Amount")
            HEADERS.put("Cash")
            HEADERS.put("Cheque")
            HEADERS.put("GRV")

            mainArr.put("HEADERS", HEADERS)

            //SALES INVOICE
            val jCashData = JSONArray()
            var planCNT = 0
            var upCNT = 0
            var strikeCNT = 0
            for (csInvoice in customerVisitList!!) {

                if (csInvoice.isPlann == "P") {
                    planCNT++
                } else {
                    upCNT++
                }

                if (csInvoice.invCNT!!.toInt() > 0) {
                    strikeCNT++
                }

                val data = JSONArray()
                data.put(csInvoice.isPlann) // DR NO
                data.put(csInvoice.customerCode) // DR NO
                data.put(csInvoice.customerName) // Name
                data.put(csInvoice.invCNT) //  P.O.B
                data.put(csInvoice.totalSale) // Amount
                data.put(csInvoice.revCNT) // Cash
                data.put(csInvoice.returnAmount) // Cheque
                data.put(csInvoice.conCNT) // GRV
                var mPaid = csInvoice.cashAmount!!.toFloat() + csInvoice.chaqueAmount!!.toFloat()
                data.put("" + Utils.twoDigit(mPaid.toFloat())) // GRV

                TotalSale += csInvoice.totalSale!!.toFloat()
                TotalReturn += csInvoice.returnAmount!!.toFloat()
                TotalCash += mPaid.toFloat()
                jCashData.put(data)
            }


            var day = Utils.getCurrentTimeInPattern("EEEE")
            var weekNumber = "week${Utils.getWeekNumberOfMonth()}"
            Log.e("WeekNumber", weekNumber)

            if (weekNumber == "week6") {
                weekNumber = "week4"
            }

            var sequenceCustomerList = db!!.getSequnceCustomer(day!!, weekNumber!!, "")
            var totalVisitedCus = customerVisitList!!.size.toString()
            var JPCustomer = sequenceCustomerList.size.toString()
            var JPPerc = 0
            var unplann = 0
            var strikePer = 0
            if (planCNT > 0) {
                JPPerc = ((planCNT / JPCustomer.toFloat()) * 100).toInt()
            }

            if (upCNT > 0) {
                unplann = ((upCNT / JPCustomer.toFloat()) * 100).toInt()
            }

            if (strikeCNT > 0) {
                strikePer = ((strikeCNT / JPCustomer.toFloat()) * 100).toInt()
            }

            var totalInvoice = db!!.getTotalInvoiceCount()
            var totalVisit = customerVisitList!!.size

            val jPData = JSONArray()
            val dataP = JSONArray()
            dataP.put(JPCustomer) // DR NO
            dataP.put(planCNT.toString()) // DR NO
            dataP.put("$JPPerc%") // Name
            dataP.put(strikeCNT.toString()) //  P.O.B
            dataP.put("$strikePer%") // Amount
            dataP.put(upCNT.toString()) // Cash
            dataP.put("$unplann%") // Cheque
            dataP.put(totalVisitedCus) // GRV
            jPData.put(dataP)

            val totalObj = JSONObject()
            totalObj.put("No of Invoice", totalInvoice)
            totalObj.put("No of Visit", totalVisit)
            totalObj.put("Total_Sales", "" + String.format("%.2f", TotalSale))
            totalObj.put("Total_Return", "" + String.format("%.2f", TotalReturn))
            totalObj.put("Total_Cash", "" + String.format("%.2f", TotalCash))
            totalObj.put("MTD_SALES", "0.0")
            TOTAL.put(totalObj)
            mainArr.put("TOTAL", TOTAL)
            mainArr.put("data", jCashData)
            mainArr.put("jpData", jPData)
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
    fun createPrintDailyRTCData(): JSONArray? {
        val jArr = JSONArray()
        try {
            //HEADERS.put("T.C");
            var TotalSalesPaid = 0F
            var TotalSales = 0F
            var TotalReturn = 0F
            var TotalCash = 0F
            var TotalI1 = 0F
            var TotalI2 = 0F
            var TotalI3 = 0F
            var TotalI4 = 0F
            var TotalI5 = 0F
            var TotalI6 = 0F
            var TotalI7 = 0F
            var TotalI8 = 0F
            var TotalI9 = 0F
            var TotalI10 = 0F
            var TotalI11 = 0F
            var TotalI12 = 0F
            var TotalI13 = 0F
            var TotalI14 = 0F
            var TotalI15 = 0F
            val jInter = JSONArray()
            val jDict = JSONObject()
            jDict.put(AppConstants.REQUEST, AppConstants.DAILY_RTC_SUMMARY)
            val mainArr = JSONObject()
            mainArr.put("ROUTE", mPreferenceManager!!.getString(AppConstants.ROUTENAME))
            mainArr.put("DOC DATE", Utils.formatDate(Date(), "dd-MM-yyyy"))
            mainArr.put("TIME", Utils.formatTime(Date(), "hh:mm"))
            mainArr.put("SALESMAN", mPreferenceManager!!.getString(AppConstants.DRIVERNAME))
            mainArr.put("CONTACTNO", "-")
            mainArr.put("DOCUMENT NO", "") //Load Summary No
            mainArr.put("TRIP START DATE", mPreferenceManager!!.getTripDate())
            mainArr.put("helperName", "-")
            mainArr.put("startTime", mPreferenceManager!!.getString(AppConstants.STARTTIME))
            mainArr.put("endTime", "-")
            mainArr.put("orderNo", "") //Invoice No

            val TOTAL = JSONArray()
            val HEADERS = JSONArray()
            HEADERS.put("Sr No")
            HEADERS.put("DR No")
            HEADERS.put("Customer Name")
            HEADERS.put("D/I")
            HEADERS.put("D/I (B)")
            HEADERS.put("APP")
            HEADERS.put("APP (B)")
            HEADERS.put("WOD")
            HEADERS.put("M/P")
            HEADERS.put("M/P/C")
            HEADERS.put("W/P")
            HEADERS.put("W/P/C")
            HEADERS.put("CHAP")
            HEADERS.put("CHAP (C)")
            HEADERS.put("ID/AP")
            HEADERS.put("PRI")
            HEADERS.put("PATH")
            HEADERS.put("VADA")
            HEADERS.put("GRV")
            HEADERS.put("Total Sales")
            HEADERS.put("Cash/Cheque")

            mainArr.put("HEADERS", HEADERS)

            //SALES INVOICE
            val jCashData = JSONArray()

            for (csInvoice in customerVisitList!!) {

                val data = JSONArray()
                data.put("" + (jCashData.length() + 1))
                data.put(csInvoice.customerCode) // DR NO
                data.put(csInvoice.customerName) // Name
                data.put(csInvoice.DI) //  P.O.B
                data.put(csInvoice.DIB) //  P.O.B
                data.put(csInvoice.APP) //  P.O.B
                data.put(csInvoice.APPB) //  P.O.B
                data.put(csInvoice.WOD) //  P.O.B
                data.put(csInvoice.MP) //  P.O.B
                data.put(csInvoice.MPC) //  P.O.B
                data.put(csInvoice.WP) //  P.O.B
                data.put(csInvoice.WPC) //  P.O.B
                data.put(csInvoice.CHAP) //  P.O.B
                data.put(csInvoice.CHAPC) //  P.O.B
                data.put(csInvoice.IDAP) //  P.O.B
                data.put(csInvoice.PRI) //  P.O.B
                data.put(csInvoice.PATH) //  P.O.B
                data.put(csInvoice.VADA) //  P.O.B
                data.put(csInvoice.returnAmount) //  P.O.B
                data.put(csInvoice.totalSale) //  P.O.B
                data.put(csInvoice.cashAmount) //  P.O.B

                TotalSales += csInvoice.totalSale!!.toFloat()
                TotalReturn += csInvoice.returnAmount!!.toFloat()
                TotalCash += csInvoice.cashAmount!!.toFloat()
                TotalI1 += csInvoice.DI!!.toFloat()
                TotalI2 += csInvoice.DIB!!.toFloat()
                TotalI3 += csInvoice.APP!!.toFloat()
                TotalI4 += csInvoice.APPB!!.toFloat()
                TotalI5 += csInvoice.WOD!!.toFloat()
                TotalI6 += csInvoice.MP!!.toFloat()
                TotalI7 += csInvoice.MPC!!.toFloat()
                TotalI8 += csInvoice.WP!!.toFloat()
                TotalI9 += csInvoice.WPC!!.toFloat()
                TotalI10 += csInvoice.CHAP!!.toFloat()
                TotalI11 += csInvoice.CHAPC!!.toFloat()
                TotalI12 += csInvoice.IDAP!!.toFloat()
                TotalI13 += csInvoice.PRI!!.toFloat()
                TotalI14 += csInvoice.PATH!!.toFloat()
                TotalI15 += csInvoice.VADA!!.toFloat()

                jCashData.put(data)

            }

            var totalInvoice = db!!.getTotalInvoiceCount()
            var totalVisit = customerVisitList!!.size

            val jNetData = JSONArray()

            jNetData.put("")
            jNetData.put("") // DESC
            jNetData.put("") // UOM
            jNetData.put(String.format("%.2f", TotalI1)) //  QTY
            jNetData.put(String.format("%.2f", TotalI2)) // Unit Price
            jNetData.put(String.format("%.2f", TotalI3)) // vat %
            jNetData.put(String.format("%.2f", TotalI4)) // Net
            jNetData.put(String.format("%.2f", TotalI5)) // Vat Amt
            jNetData.put(String.format("%.2f", TotalI6)) //  QTY
            jNetData.put(String.format("%.2f", TotalI7)) //  QTY
            jNetData.put(String.format("%.2f", TotalI8)) //  QTY
            jNetData.put(String.format("%.2f", TotalI9)) //  QTY
            jNetData.put(String.format("%.2f", TotalI10)) //  QTY
            jNetData.put(String.format("%.2f", TotalI11)) //  QTY
            jNetData.put(String.format("%.2f", TotalI12)) //  QTY
            jNetData.put(String.format("%.2f", TotalI13)) //  QTY
            jNetData.put(String.format("%.2f", TotalI14)) //  QTY
            jNetData.put(String.format("%.2f", TotalI15)) //  QTY
            jNetData.put(String.format("%.2f", TotalReturn)) //  QTY
            jNetData.put(String.format("%.2f", TotalSales)) //  QTY
            jNetData.put(String.format("%.2f", TotalCash)) //  QTY

            TotalSalesPaid = TotalSales - TotalReturn
            val totalObj = JSONObject()
            totalObj.put("No of Invoice", totalInvoice)
            totalObj.put("No of Visit", totalVisit)
            totalObj.put("Total_Sales", "" + String.format("%.2f", TotalSalesPaid))
            totalObj.put("MTD_SALES", "0.0")
            TOTAL.put(totalObj)
            mainArr.put("TOTAL", TOTAL)
            mainArr.put("data", jCashData)
            mainArr.put("netTotal", jNetData)
            jDict.put("mainArr", mainArr)
            jInter.put(jDict)
            jArr.put(jInter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jArr
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

    private class MyHandler internal constructor(activity: DataPostingActivity) :
        Handler() {
        private val mActivity: WeakReference<DataPostingActivity>

        init {
            mActivity = WeakReference<DataPostingActivity>(activity)
        }

        override fun handleMessage(msg: Message) {
            val activity: DataPostingActivity? = mActivity.get()
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
                this@DataPostingActivity,
                this@DataPostingActivity
            )
            `object`.execute("", jsonArray)
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
                this@DataPostingActivity,
                this@DataPostingActivity
            )
            `object`.execute("", jsonArray)

        }, 3000)
    }

    private fun sendData(data: ByteArray) {
        // Check that we're actually connected before trying printing
        if (mPrintService!!.getState() !== BluetoothPrintService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show()
            return
        }
        // Check that there's actually something to send
        if (data.isNotEmpty()) mPrintService!!.write(data)
    }


}