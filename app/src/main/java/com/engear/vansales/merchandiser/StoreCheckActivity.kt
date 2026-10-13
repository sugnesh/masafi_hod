package com.engear.vansales.merchandiser

import android.app.Activity
import android.app.Dialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.R
import com.engear.vansales.activities.SimpleScannerActivity
import com.engear.vansales.adapter.RowInventoryExpiryAdapter
import com.engear.vansales.adapter.RowSOSAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.databinding.ActivityStoreCheckBinding
import com.engear.vansales.databinding.RowItemDistributionBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.*
import com.engear.vansales.utils.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.symbol.emdk.EMDKManager
import com.symbol.emdk.EMDKResults
import com.symbol.emdk.barcode.*
import com.symbol.emdk.barcode.Scanner
import java.lang.ref.WeakReference
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class StoreCheckActivity : BaseActivity(), OnItemClickListener, EMDKManager.EMDKListener,
    Scanner.DataListener, Scanner.StatusListener {

    lateinit var binding: ActivityStoreCheckBinding
    var customerCode: String? = null
    private var displayToolId: String? = null
    private var displayToolName: String? = null
    private var customerObj: CustomerData.DataCustomer? = null
    private val filterCode = 200
    private val scanCode = 100
    var mAdapter: DistributionItemAdapter? = null
    var brandId: String = ""
    var categoryId: String = ""
    var headbrandId: String = ""
    var searchView: SearchView? = null
    var headcategoryId: String = ""
    var dueOn = "1"

    // Declare a variable to store EMDKManager object
    private var emdkManager: EMDKManager? = null

    // Declare a variable to store Barcode Manager object
    private var barcodeManager: BarcodeManager? = null

    // Declare a variable to hold scanner device to scan
    private var scanner: Scanner? = null

    private var dataLength = 0
    private var statusString = ""
    private val bContinuousMode = true

    companion object {
        var allItem: ArrayList<DistributionItem> = ArrayList()
        var itemList: ArrayList<DistributionItem> = ArrayList()
        var isView: Boolean = false
        var itemPositionId = ""
        var isScan = false

        class MyAsyncTask internal constructor(context: StoreCheckActivity) :
            AsyncTask<Int, String, String?>() {

            private var resp: String? = null
            private val activityReference: WeakReference<StoreCheckActivity> =
                WeakReference(context)

            override fun onPreExecute() {
                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return
                activity.showOnlyProgressDialog()
            }

            override fun doInBackground(vararg params: Int?): String? {
                val activity = activityReference.get()

                allItem = activity!!.db!!.getAllDistributionItemList(
                    activity!!.customerObj!!.user_id.toString(),
                    activity!!.displayToolId!!
                )
                itemList = activity!!.db!!.getAllDistributionItemList(
                    activity!!.customerObj!!.user_id.toString(),
                    activity!!.displayToolId!!
                )

                return resp
            }


            override fun onPostExecute(result: String?) {

                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return

                activity.dismissDialog()
                activity.setAdapter()
            }

        }

        class AsyncDataUpdate internal constructor(context: StoreCheckActivity) :
            AsyncTask<String?, Void?, String?>() {

            private var resp: String? = null
            private val activityReference: WeakReference<StoreCheckActivity> =
                WeakReference(context)

            override fun onPostExecute(result: String?) {
                val activity = activityReference.get()
                if (result != null) {
                    if (activity!!.dataLength++ > 100) { //Clear the cache after 100 scans
                        activity!!.dataLength = 0
                    }
                    if (!result.equals("", ignoreCase = true)) {
                        activity!!.openScanItem(result)
                    }
                }
            }

            override fun doInBackground(vararg params: String?): String? {
                return params[0]
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreCheckBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
        setClickEvent()
    }

    private fun init() {
        Utils.logData(
            me,
            "On Display Tool Item Screen"
        )
//        brandId = intent.getStringExtra("BrandId")
//        categoryId = intent.getStringExtra("CategoryId")
        customerCode = intent.getStringExtra("customer_code")
        displayToolId = intent.getStringExtra("display_tool_id")
        displayToolName = intent.getStringExtra("name")
//        headbrandId = intent.getStringExtra("HeadBrandId")
//        headcategoryId = intent.getStringExtra("HeadCategoryId")
        customerObj = db!!.getCustomerDetail(customerCode!!)
        dueOn = db!!.getCustomerDueOne(customerObj!!.user_id.toString())

        setToolbarTitle(displayToolName!!)

        // The EMDKManager object will be created and returned in the callback .
        val results: EMDKResults = EMDKManager.getEMDKManager(
            applicationContext, this@StoreCheckActivity
        )
        // Check the return status of getEMDKManager and update the status Text
        // View accordingly
        // Check the return status of getEMDKManager and update the status Text
        // View accordingly
        if (results.statusCode !== EMDKResults.STATUS_CODE.SUCCESS) {
            Log.i("EMDK Scan", "EMDKManager Request Failed")
        }

        if (db!!.checkDistributionCustomerPosted(
                customerObj!!.userDetail.id.toString(),
                displayToolId!!
            )
        ) {
            isView = true
            binding.btnSubmit.visibility = View.GONE
        } else {
            isView = false
            binding.btnSubmit.visibility = View.VISIBLE
        }

        val task = MyAsyncTask(this@StoreCheckActivity)
        task.execute(10)

    }

    private fun setClickEvent() {
        binding.btnSubmit.setOnClickListener {
            val alertDialogBuilder =
                android.app.AlertDialog.Builder(this@StoreCheckActivity)
            alertDialogBuilder.setTitle("Alert")
                .setMessage("Are you sure you want to submit this display tool check?")
                .setCancelable(false)
                .setPositiveButton(
                    "Yes"
                ) { dialog, which ->
                    addToolCheck()
                }.setNegativeButton(
                    "No"
                ) { dialog, which -> dialog.dismiss() }

            alertDialogBuilder.show()
        }
    }

    fun setAdapter() {
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = DistributionItemAdapter(context = this, allList = itemList)
        binding.rvList.adapter = mAdapter
    }

    private fun addToolCheck() {

        var uniqId = "SHLF" + Utils.getTimeStamp()!!
            .replace(":", "") + allItem[0].distributionId + customerObj!!.userDetail.id.toString()
        db!!.insertDisplayPost(uniqId, itemList, brandId, categoryId, headbrandId, headcategoryId)

        addInvoice()

        addReturn()

        val transaction = Transaction()
        transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_DISPLAY_TOOL
        transaction.tr_date_time = Utils.getCurrentDate() + " " + Utils.getCurrentTime()
        transaction.tr_customer_num = customerCode
        transaction.tr_customer_name =
            customerObj!!.userDetail!!.firstname + " " + customerObj!!.userDetail!!.lastname
        transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
        transaction.tr_invoice_id = ""
        transaction.tr_order_id = uniqId
        transaction.tr_collection_id = ""
        transaction.tr_pyament_id = ""
        transaction.tr_printData = ""
        transaction.tr_is_posted = "No"
        db!!.insertTransaction(transaction)

        // db!!.updateCustomerTransaction(customerObj!!.userDetail!!.id.toString(), "shelf")

        DistributionListActivity.isCompleted = true
        finish()
    }

    public fun addInvoice() {

        var itemsList: ArrayList<ItemData.Data>? = ArrayList()
        var orderAmt: Float = 0f
        var vatAmt: Float = 0f
        var exciseAmt: Float = 0f
        var netAmt: Float = 0f
        var grossAmt: Float = 0f
        var discountAmt: Float = 0f
        var totalAmt: Float = 0f
        var orderComment: String = ""

        for (mItem in itemList) {

            if (mItem.toFill!!.isNotEmpty()) {
                var mItemData =
                    db!!.getItemDetailFromId(mItem.itemId!!)
                var isExcise = mItemData!!.is_item_excise.toString()
                var mSelectUOMExcise = mItemData!!.item_excise

                var mUOM = db!!.getLowerUOM(mItem.uomId!!, mItem.itemId!!)
                if (mUOM != null) {

                    var uomPrice = "0"
                    uomPrice = db!!.getItemCustUOMPrice(
                        mItem.itemId!!,
                        customerObj!!.user_id!!.toString(),
                        mUOM.uomId,
                        mUOM.uomPrice
                    )

                    var discountIAmt = 0F
                    var mUOMPrice = 0F
                    var netIAmt = 0F
                    var vatIAmt = 0F
                    var totalIAmt = 0F
                    if (isExcise == "1") {
                        mUOMPrice = Utils.twoDigit(uomPrice.toFloat() + mSelectUOMExcise.toFloat())
                    } else {
                        mUOMPrice = uomPrice.toFloat()
                    }
                    var grossPrice = mItem.toFill!!.toFloat() * mUOMPrice

                    netIAmt = Utils.twoDigit((grossPrice - discountIAmt))
                    var mVat =
                        Utils.getVatAmt(netIAmt.toDouble(), mItemData!!.vatPercentage.toDouble())
                    vatIAmt = Utils.twoDigit(mVat.toFloat())
                    totalIAmt = Utils.twoDigit((netIAmt + vatIAmt))


                    var mSelectUOMList: ArrayList<UomData> = ArrayList()
                    mSelectUOMList.add(
                        UomData(
                            mItemData!!.id.toString(),
                            mItemData!!.itemCode,
                            mItemData!!.itemName,
                            mUOM!!.uomId,
                            mUOM!!.uom.name,
                            mUOM!!.uomUPC,
                            mItem.toFill!!,
                            uomPrice,
                            grossPrice.toString(),
                            "",
                            discountAmt = discountIAmt.toString(),
                            netAmt = netIAmt.toString(),
                            vatAmt = vatIAmt.toString(),
                            totalAmt = totalIAmt.toString(),
                            promotion_Id = "",
                            discount_Id = "",
                            reason = "",
                            expiryDate = "",
                            invAmount = "",
                            invNo = "",
                            isBad = "",
                            item_excise = mSelectUOMExcise
                        )
                    )
                    var grossMPrice =
                        Utils.twoDigit((mItem.toFill!!.toFloat() * uomPrice.toFloat()))
                    var excisePrice =
                        Utils.twoDigit((mItem.toFill!!.toFloat() * mSelectUOMExcise.toFloat()))
                    grossAmt = grossAmt.plus(Utils.twoDigit(grossMPrice))
                    exciseAmt = exciseAmt.plus(Utils.twoDigit(excisePrice))
                    //itemDiscountAmt = itemDiscountAmt.plus(Utils.twoDigit(j!!.discountAmt.toFloat()))
                    var mNetAmt = grossMPrice - Utils.twoDigit(discountIAmt.toFloat())
                    netAmt = netAmt.plus(Utils.twoDigit(mNetAmt))
                    var mHVat = Utils.getVatAmt((mNetAmt.toDouble() + excisePrice.toDouble()), 5.0)
                        .toFloat()
                    vatAmt = vatAmt.plus(Utils.twoDigit(mHVat))
                    var mTotal =
                        Utils.twoDigit(mNetAmt) + Utils.twoDigit(mHVat) + Utils.twoDigit(excisePrice)
                    totalAmt = totalAmt.plus(Utils.twoDigit(mTotal))

                    mItemData!!.itemSelectUOM = mSelectUOMList
                    itemsList!!.add(mItemData!!)
                }
            }

        }


        if (itemsList!!.size > 0) {

//            if (db!!.checkIsInvoiceCustExist(customerCode!!)) {
//
//                val orderNo = db!!.getInvoiceCustExist(customerCode!!)
//
//                val mOrderEx = db!!.getAllInvoiceCustMR(customerCode!!)
//
//                grossAmt = grossAmt.plus(mOrderEx.gross!!.toFloat())
//                discountAmt = discountAmt.plus(mOrderEx.discount!!.toFloat())
//                netAmt = netAmt.plus(mOrderEx.net!!.toFloat())
//                vatAmt = vatAmt.plus(mOrderEx.vat!!.toFloat())
//                exciseAmt = exciseAmt.plus(mOrderEx.excAmt!!.toFloat())
//                totalAmt = totalAmt.plus(mOrderEx.total!!.toFloat())
//
//                var mOrder = Invoice()
//                mOrder.invoiceNo = orderNo
//                mOrder.customerId = customerObj!!.customer_code
//                mOrder.gross = grossAmt.toString()
//                mOrder.discount = discountAmt.toString()
//                mOrder.net = netAmt.toString()
//                mOrder.excAmt = exciseAmt.toString()
//                mOrder.vat = vatAmt.toString()
//                mOrder.total = totalAmt.toString()
//                mOrder.comment = orderComment.toString()
//
//                //Update Retrun
//                db!!.updateInvoice(mOrder = mOrder, mItemList = itemsList!!)
//
//            } else {
            var orderNo =
                Utils.getLastIndex(
                    mPreferenceManager!!.getString(AppConstants.INVOICE_LAST).toString(),
                    "invoice"
                )

            val c = Calendar.getInstance().getTime()
            println("Current time => $c")
            val df = SimpleDateFormat("yyyy-MM-dd")
            val orderDate = df.format(c)

            if (customerObj!!.customer_type_id.toString() == "2") {
                customerObj!!.credit_days = "0"
            }

            var dueDate =
                Utils.addDays(
                    "yyyy-MM-dd",
                    orderDate,
                    customerObj!!.credit_days!!.toInt(),
                    dueOn
                )!!

            var orderTypeId = "1"
            if (customerObj!!.credit_days == "0") {
                orderTypeId = "1"
            } else {
                orderTypeId = "2"
            }


            var mOrder = Invoice()
            mOrder.invoiceNo = orderNo
            mOrder.orderType = orderTypeId
            mOrder.invoiceDate = orderDate
            mOrder.invoiceType = orderTypeId
            mOrder.deliveryId = ""
            mOrder.orderId = ""
            mOrder.dueDate = dueDate
            mOrder.customerId = customerObj!!.customer_code
            mOrder.gross = grossAmt.toString()
            mOrder.discount = discountAmt.toString()
            mOrder.net = netAmt.toString()
            mOrder.vat = vatAmt.toString()
            mOrder.excAmt = exciseAmt.toString()
            mOrder.total = totalAmt.toString()
            mOrder.comment = orderComment.toString()
            mOrder.payemntId = customerObj!!.payment_term_id.toString()
            mOrder.lobId = ""
            mOrder.roundTo = "0"
            mOrder.isExchange = "0"
            mOrder.exchangeNo = ""
            mOrder.paidAmount = ""
            mOrder.isAccept = "1"

            //Insert Sales
            db!!.insertInvoice(mOrder = mOrder, mItemList = itemsList!!, mLPO = "")

            //set Invoice Last Number
            mPreferenceManager!!.setString(AppConstants.INVOICE_LAST, orderNo)
            // }
        }
    }

    public fun addReturn() {

        var itemsList: ArrayList<ItemData.Data>? = ArrayList()
        var orderAmt: Float = 0f
        var vatAmt: Float = 0f
        var exciseAmt: Float = 0f
        var netAmt: Float = 0f
        var grossAmt: Float = 0f
        var discountAmt: Float = 0f
        var totalAmt: Float = 0f
        var orderComment: String = ""

        for (mItem in itemList) {

            if (mItem.arrDer!!.size > 0) {
                for (mDER in mItem.arrDer!!) {

                    if (mDER["Damage"]!!.toDouble() > 0) {

                        var mItemData =
                            db!!.getItemDetailFromId(mItem.itemId!!)
                        var isExcise = mItemData!!.is_item_excise.toString()
                        var mSelectUOMExcise = mItemData!!.item_excise

                        var mUOM = db!!.getLowerUOM(mItem.uomId!!, mItem.itemId!!)

                        if (mUOM != null) {

                            var uomPrice = "0"
                            uomPrice = db!!.getItemCustUOMPrice(
                                mItem.itemId!!,
                                customerObj!!.user_id!!.toString(),
                                mUOM.uomId,
                                mUOM.uomPrice
                            )

                            var discountIAmt = 0F
                            var mUOMPrice = 0F
                            var netIAmt = 0F
                            var vatIAmt = 0F
                            var totalIAmt = 0F
                            if (isExcise == "1") {
                                mUOMPrice =
                                    Utils.twoDigit(uomPrice.toFloat() + mSelectUOMExcise.toFloat())
                            } else {
                                mUOMPrice = uomPrice.toFloat()
                            }
                            var grossPrice = mDER["Damage"]!!.toFloat() * mUOMPrice

                            netIAmt = Utils.twoDigit((grossPrice - discountIAmt))
                            var mVat =
                                Utils.getVatAmt(
                                    netIAmt.toDouble(),
                                    mItemData!!.vatPercentage.toDouble()
                                )
                            vatIAmt = Utils.twoDigit(mVat.toFloat())
                            totalIAmt = Utils.twoDigit((netIAmt + vatIAmt))


                            var mSelectUOMList: ArrayList<UomData> = ArrayList()
                            mSelectUOMList.add(
                                UomData(
                                    mItemData!!.id.toString(),
                                    mItemData!!.itemCode,
                                    mItemData!!.itemName,
                                    mUOM!!.uomId,
                                    mUOM!!.uom.name,
                                    mUOM!!.uomUPC,
                                    mDER["Damage"]!!.toString(),
                                    uomPrice,
                                    grossPrice.toString(),
                                    "",
                                    discountAmt = discountIAmt.toString(),
                                    netAmt = netIAmt.toString(),
                                    vatAmt = vatIAmt.toString(),
                                    totalAmt = totalIAmt.toString(),
                                    promotion_Id = "",
                                    discount_Id = "",
                                    reason = "Damage",
                                    expiryDate = Utils.getCurrentDate(),
                                    invAmount = "",
                                    invNo = "",
                                    isBad = "1",
                                    item_excise = mSelectUOMExcise
                                )
                            )
                            var grossMPrice =
                                Utils.twoDigit((mDER["Damage"]!!.toFloat() * uomPrice.toFloat()))
                            var excisePrice =
                                Utils.twoDigit((mDER["Damage"]!!.toFloat() * mSelectUOMExcise.toFloat()))
                            grossAmt = grossAmt.plus(Utils.twoDigit(grossMPrice))
                            exciseAmt = exciseAmt.plus(Utils.twoDigit(excisePrice))
                            //itemDiscountAmt = itemDiscountAmt.plus(Utils.twoDigit(j!!.discountAmt.toFloat()))
                            var mNetAmt = grossMPrice - Utils.twoDigit(discountIAmt.toFloat())
                            netAmt = netAmt.plus(Utils.twoDigit(mNetAmt))
                            var mHVat =
                                Utils.getVatAmt((mNetAmt.toDouble() + excisePrice.toDouble()), 5.0)
                                    .toFloat()
                            vatAmt = vatAmt.plus(Utils.twoDigit(mHVat))
                            var mTotal =
                                Utils.twoDigit(mNetAmt) + Utils.twoDigit(mHVat) + Utils.twoDigit(
                                    excisePrice
                                )
                            totalAmt = totalAmt.plus(Utils.twoDigit(mTotal))

                            mItemData!!.itemSelectUOM = mSelectUOMList
                            itemsList!!.add(mItemData!!)

                        }
                    }

                    if (mDER["Expiry"]!!.toDouble() > 0) {

                        var mItemData =
                            db!!.getItemDetailFromId(mItem.itemId!!)
                        var isExcise = mItemData!!.is_item_excise.toString()
                        var mSelectUOMExcise = mItemData!!.item_excise

                        var mUOM = db!!.getLowerUOM(mItem.uomId!!, mItem.itemId!!)

                        if (mUOM != null) {

                            var uomPrice = "0"
                            uomPrice = db!!.getItemCustUOMPrice(
                                mItem.itemId!!,
                                customerObj!!.user_id!!.toString(),
                                mUOM.uomId,
                                mUOM.uomPrice
                            )

                            var discountIAmt = 0F
                            var mUOMPrice = 0F
                            var netIAmt = 0F
                            var vatIAmt = 0F
                            var totalIAmt = 0F
                            if (isExcise == "1") {
                                mUOMPrice =
                                    Utils.twoDigit(uomPrice.toFloat() + mSelectUOMExcise.toFloat())
                            } else {
                                mUOMPrice = uomPrice.toFloat()
                            }
                            var grossPrice = mDER["Expiry"]!!.toFloat() * mUOMPrice

                            netIAmt = Utils.twoDigit((grossPrice - discountIAmt))
                            var mVat =
                                Utils.getVatAmt(
                                    netIAmt.toDouble(),
                                    mItemData!!.vatPercentage.toDouble()
                                )
                            vatIAmt = Utils.twoDigit(mVat.toFloat())
                            totalIAmt = Utils.twoDigit((netIAmt + vatIAmt))


                            var mSelectUOMList: ArrayList<UomData> = ArrayList()
                            mSelectUOMList.add(
                                UomData(
                                    mItemData!!.id.toString(),
                                    mItemData!!.itemCode,
                                    mItemData!!.itemName,
                                    mUOM!!.uomId,
                                    mUOM!!.uom.name,
                                    mUOM!!.uomUPC,
                                    mDER["Expiry"]!!.toString(),
                                    uomPrice,
                                    grossPrice.toString(),
                                    "",
                                    discountAmt = discountIAmt.toString(),
                                    netAmt = netIAmt.toString(),
                                    vatAmt = vatIAmt.toString(),
                                    totalAmt = totalIAmt.toString(),
                                    promotion_Id = "",
                                    discount_Id = "",
                                    reason = "Expiry",
                                    expiryDate = Utils.getCurrentDate(),
                                    invAmount = "",
                                    invNo = "",
                                    isBad = "0",
                                    item_excise = mSelectUOMExcise
                                )
                            )
                            var grossMPrice =
                                Utils.twoDigit((mDER["Expiry"]!!.toFloat() * uomPrice.toFloat()))
                            var excisePrice =
                                Utils.twoDigit((mDER["Expiry"]!!.toFloat() * mSelectUOMExcise.toFloat()))
                            grossAmt = grossAmt.plus(Utils.twoDigit(grossMPrice))
                            exciseAmt = exciseAmt.plus(Utils.twoDigit(excisePrice))
                            //itemDiscountAmt = itemDiscountAmt.plus(Utils.twoDigit(j!!.discountAmt.toFloat()))
                            var mNetAmt = grossMPrice - Utils.twoDigit(discountIAmt.toFloat())
                            netAmt = netAmt.plus(Utils.twoDigit(mNetAmt))
                            var mHVat =
                                Utils.getVatAmt((mNetAmt.toDouble() + excisePrice.toDouble()), 5.0)
                                    .toFloat()
                            vatAmt = vatAmt.plus(Utils.twoDigit(mHVat))
                            var mTotal =
                                Utils.twoDigit(mNetAmt) + Utils.twoDigit(mHVat) + Utils.twoDigit(
                                    excisePrice
                                )
                            totalAmt = totalAmt.plus(Utils.twoDigit(mTotal))

                            mItemData!!.itemSelectUOM = mSelectUOMList
                            itemsList!!.add(mItemData!!)
                        }
                    }
                }
            }
        }


        if (itemsList!!.size > 0) {

//            if (db!!.checkIsReturnCustExist(customerCode!!)) {
//
//                val orderNo = db!!.getReturnCustExist(customerCode!!)
//
//                val mOrderEx = db!!.getAllReturnCustMR(customerCode!!)
//
//                grossAmt = grossAmt.plus(mOrderEx.gross!!.toFloat())
//                discountAmt = discountAmt.plus(mOrderEx.discount!!.toFloat())
//                netAmt = netAmt.plus(mOrderEx.net!!.toFloat())
//                exciseAmt = exciseAmt.plus(mOrderEx.excAmt!!.toFloat())
//                vatAmt = vatAmt.plus(mOrderEx.vat!!.toFloat())
//                totalAmt = totalAmt.plus(mOrderEx.total!!.toFloat())
//
//                var mOrder = Order()
//                mOrder.orderNo = orderNo
//                mOrder.customerId = customerCode.toString()
//                mOrder.gross = grossAmt.toString()
//                mOrder.discount = discountAmt.toString()
//                mOrder.net = netAmt.toString()
//                mOrder.vat = vatAmt.toString()
//                mOrder.excAmt = exciseAmt.toString()
//                mOrder.total = totalAmt.toString()
//                mOrder.comment = orderComment.toString()
//
//                //Update Retrun
//                db!!.updateReturn(mOrder = mOrder, mItemList = itemsList!!)
//
//            } else {
            var orderNo =
                Utils.getLastIndex(
                    mPreferenceManager!!.getString(AppConstants.RETURN_LAST).toString(),
                    "return"
                )

            val c = Calendar.getInstance().getTime()
            println("Current time => $c")
            val df = SimpleDateFormat("yyyy-MM-dd")
            val orderDate = df.format(c)

            var mOrder = Order()
            mOrder.orderNo = orderNo
            mOrder.orderType = "badReturn"
            mOrder.orderDate = orderDate
            mOrder.invoiceId = ""
            mOrder.reason = "Damage"
            mOrder.customerId = customerCode.toString()
            mOrder.gross = grossAmt.toString()
            mOrder.discount = discountAmt.toString()
            mOrder.net = netAmt.toString()
            mOrder.vat = vatAmt.toString()
            mOrder.excAmt = exciseAmt.toString()
            mOrder.total = totalAmt.toString()
            mOrder.comment = orderComment.toString()
            mOrder.payemntId = customerObj!!.payment_term_id.toString()
            mOrder.isRequested = ""

            //Insert Return
            db!!.insertReturn(
                mOrder = mOrder,
                mItemList = itemsList!!,
                isAccept = "1",
                isWithOutRef = ""
            )

            //set Return Last Number
            mPreferenceManager!!.setString(AppConstants.RETURN_LAST, orderNo)
            //}
        }
    }

    public fun addOrder() {

        var itemsList: ArrayList<ItemData.Data>? = ArrayList()
        var orderAmt: Float = 0f
        var vatAmt: Float = 0f
        var excise: Float = 0f
        var netAmt: Float = 0f
        var grossAmt: Float = 0f
        var discountAmt: Float = 0f
        var totalAmt: Float = 0f
        var orderComment: String? = null

        for (mItem in itemList) {

            if (mItem.toFill != null && mItem.toFill!!.isNotEmpty()) {
                if (mItem.toFill!!.toDouble() > 0) {
                    var mItemData =
                        db!!.getItemDetailFromId(mItem.itemId!!)

                    var mUOM = db!!.getLowerUOM(mItem.uomId!!, mItem.itemId!!)

                    var uomDiscountAmt = 0
                    var mIGrossPrice = mItem.toFill!!.toDouble() * mUOM!!.uomPrice.toDouble()
                    var mInetAmt = mIGrossPrice - uomDiscountAmt
                    var mIvatAmt =
                        Utils.getVatAmt(mIGrossPrice, mItemData!!.vatPercentage.toDouble())
                    var mItotalAmt = mInetAmt + mIvatAmt

                    grossAmt = grossAmt.plus(mIGrossPrice.toFloat())
                    discountAmt = discountAmt.plus(uomDiscountAmt.toFloat())
                    netAmt = netAmt.plus(mInetAmt.toFloat())
                    vatAmt = vatAmt.plus(mIvatAmt.toFloat())
                    totalAmt = totalAmt.plus(mItotalAmt.toFloat())

                    var mSelectUOMList: ArrayList<UomData> = ArrayList()
                    mSelectUOMList.add(
                        UomData(
                            mItemData!!.id.toString(),
                            mItemData!!.itemCode,
                            mItemData!!.itemName,
                            mUOM!!.uomId,
                            mUOM!!.uom.name,
                            mUOM!!.uomUPC,
                            mItem.toFill!!,
                            mUOM!!.uomPrice,
                            mIGrossPrice.toString(),
                            "",
                            discountAmt = uomDiscountAmt.toString(),
                            netAmt = mInetAmt.toString(),
                            vatAmt = mIvatAmt.toString(),
                            totalAmt = mItotalAmt.toString(),
                            promotion_Id = "",
                            discount_Id = "",
                            reason = ""
                        )
                    )
                    mItemData!!.itemSelectUOM = mSelectUOMList
                    itemsList!!.add(mItemData!!)
                }
            }
        }

        if (db!!.checkIsOrderCustExist(customerCode!!)) {

            val orderNo = db!!.getOrderCustExist(customerCode!!)

            val mOrderEx = db!!.getAllOrderCustMR(customerCode!!)

            grossAmt = grossAmt.plus(mOrderEx.gross!!.toFloat())
            discountAmt = discountAmt.plus(mOrderEx.discount!!.toFloat())
            netAmt = netAmt.plus(mOrderEx.net!!.toFloat())
            vatAmt = vatAmt.plus(mOrderEx.vat!!.toFloat())
            totalAmt = totalAmt.plus(mOrderEx.total!!.toFloat())

            var mOrder = Order()
            mOrder.orderNo = orderNo
            mOrder.customerId = customerCode.toString()
            mOrder.gross = grossAmt.toString()
            mOrder.discount = discountAmt.toString()
            mOrder.net = netAmt.toString()
            mOrder.vat = vatAmt.toString()
            mOrder.total = totalAmt.toString()
            mOrder.comment = orderComment.toString()

            //Update Order
            db!!.updateOrder(mOrder = mOrder, mItemList = itemsList!!)

        } else {
            var orderNo =
                Utils.getLastIndex(
                    mPreferenceManager!!.getString(AppConstants.ORDER_LAST).toString(), "order"
                )
            var orderTypeId = if (customerObj!!.credit_days == "0") {
                db!!.getOrderTypeId("Cash")
            } else {
                db!!.getOrderTypeId("Credit")
            }

            val c = Calendar.getInstance().getTime()
            println("Current time => $c")
            val df = SimpleDateFormat("yyyy-MM-dd")
            val orderDate = df.format(c)

            var dueDate =
                Utils.addDays("yyyy-MM-dd", orderDate, customerObj!!.credit_days!!.toInt(), "1")!!

            var mOrder = Order()
            mOrder.orderNo = orderNo
            mOrder.orderType = orderTypeId
            mOrder.orderDate = orderDate
            mOrder.deliveryDate = orderDate
            mOrder.dueDate = dueDate
            mOrder.customerId = customerCode.toString()
            mOrder.gross = grossAmt.toString()
            mOrder.discount = discountAmt.toString()
            mOrder.net = netAmt.toString()
            mOrder.vat = vatAmt.toString()
            mOrder.total = totalAmt.toString()
            mOrder.comment = orderComment.toString()
            mOrder.payemntId = customerObj!!.payment_term_id.toString()

            //Insert Order
            db!!.insertOrder(mOrder = mOrder, mItemList = itemsList!!, isAccept = "1", mLPO = "")

            //set Order Last Number
            mPreferenceManager!!.setString(AppConstants.ORDER_LAST, orderNo)
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_display_tool, menu)
        val color: Int
        color = resources.getColor(R.color.colorWhite)

        var drawable =
            menu!!.findItem(R.id.action_search).icon

        drawable = DrawableCompat.wrap(drawable!!)
        DrawableCompat.setTint(drawable, color)
        menu!!.findItem(R.id.action_search).icon = drawable

        searchView =
            MenuItemCompat.getActionView(menu!!.findItem(R.id.action_search)) as SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView!!.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView!!.setIconifiedByDefault(true)
        val searchEditText =
            searchView!!.findViewById(androidx.appcompat.R.id.search_src_text) as EditText
        searchEditText.hint = "Search"

        searchEditText.setTextColor(color)
        searchEditText.setHintTextColor(color)

        val searchIcon =
            searchView!!.findViewById(androidx.appcompat.R.id.search_button) as ImageView
        searchIcon
            .setImageResource(R.drawable.ic_search)

        val searchClose =
            searchView!!.findViewById(androidx.appcompat.R.id.search_close_btn) as ImageView
        searchClose.imageTintList =
            ColorStateList.valueOf(color)

        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String?): Boolean {

                mAdapter!!.filter!!.filter(s)
                //       isScan = false


                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_capture -> {
                startActivity(Intent(me, DistributionImageActivity::class.java).apply {
                    this.putExtra("customer_code", customerCode)
                    this.putExtra("display_tool_id", displayToolId)
                    this.putExtra("name", displayToolName)
                    this.putExtra("brandId", brandId)
                    this.putExtra("categoryId", categoryId)
                })
                return true
            }
            /* R.id.action_filter -> {
                 startActivityForResult(
                     Intent(this, FilterActivity::class.java)
                         .putExtra("Type", "Item"), filterCode
                 )
                 return true
             }*/
            R.id.action_survey -> {
                startActivity(Intent(me, DistributionSurveyActivity::class.java).apply {
                    this.putExtra("customer_code", customerCode)
                    this.putExtra("tool_id", displayToolId)
                    this.putExtra("tool_name", displayToolName)
                })
                return true
            }
            R.id.action_scan -> {
                var intent = Intent(applicationContext, SimpleScannerActivity::class.java)
                startActivityForResult(intent, scanCode)
                return true
            }
            android.R.id.home -> {
                if (isView) {
                    finish()
                } else {
                    val alertDialogBuilder =
                        android.app.AlertDialog.Builder(this@StoreCheckActivity)
                    alertDialogBuilder.setTitle("Alert")
                        .setMessage("Are you sure you want to go Back. Your save data will be lost?")
                        .setCancelable(false)
                        .setPositiveButton(
                            "Yes"
                        ) { dialog, which ->

                            finish()

                        }.setNegativeButton(
                            "No"
                        ) { dialog, which -> dialog.dismiss() }

                    alertDialogBuilder.show()
                }

                return true
            }
        }
        return false
    }

    class DistributionItemAdapter(
        var context: Activity,
        var allList: ArrayList<DistributionItem>
    ) : RecyclerView.Adapter<DistributionItemAdapter.ViewHolder>(), Filterable {

        val onItemClickListener = context as OnItemClickListener

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding =
                RowItemDistributionBinding.inflate(LayoutInflater.from(context), parent, false)
            return ViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return itemList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

//            val mData = itemList[holder.adapterPosition]
            holder.binding.item = itemList[holder.adapterPosition]
            holder.binding.edtSaleable.setText(itemList[holder.adapterPosition].saleable)
            holder.binding.edtFill.setText(itemList[holder.adapterPosition].toFill)

            if (isView) {
                holder.binding.chkOutStock.isClickable = false
                holder.binding.edtModel.isClickable = false
                holder.binding.edtModel.isEnabled = false
                holder.binding.edtSaleable.isClickable = false
                holder.binding.edtSaleable.isEnabled = false
            }
            holder.binding.chkOutStock.isChecked =
                itemList[holder.adapterPosition].outOfStock == "1"

            holder.binding.chkOutStock.setOnCheckedChangeListener { buttonView, isChecked ->
                if (buttonView.isPressed) {
                    if (isChecked) {
                        var modQty = itemList[holder.adapterPosition].capacity!!.toDouble()
                        var mFill = modQty
                        itemList[holder.adapterPosition].toFill = mFill.toString()
                        itemList[holder.adapterPosition].saleable = "0"
                        itemList[holder.adapterPosition].outOfStock = "1"
//                        itemList[holder.adapterPosition] = mData
                        // allItem[index] = mData
                        holder.binding.edtSaleable.setText("0")
                        holder.binding.edtFill.setText(itemList[holder.adapterPosition].toFill)
                    } else {
                        var mFill = 0
                        itemList[holder.adapterPosition].toFill = mFill.toString()
                        itemList[holder.adapterPosition].saleable = "0"
                        itemList[holder.adapterPosition].outOfStock = "0"
//                        itemList[holder.adapterPosition] = mData
                        holder.binding.edtFill.setText(itemList[holder.adapterPosition].toFill)
                        holder.binding.edtSaleable.setText("")
                    }
                }
            }

            holder.binding.edtModel.addTextChangedListener(
                object : TextWatcher {

                    override fun afterTextChanged(s: Editable) {
                        if (s.toString().isNotEmpty()) {
                            itemList[holder.adapterPosition].capacity = s.toString()
//                            itemList[holder.adapterPosition] = mData
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



            holder.binding.edtSaleable.addTextChangedListener(
                object : TextWatcher {

                    override fun afterTextChanged(s: Editable) {
                        var modQty = itemList[holder.adapterPosition].capacity!!.toDouble()

                        if (s.toString().isNotEmpty()) {
                            if (s.toString() == ".") {
                                s.clear()
                                Toast.makeText(
                                    context,
                                    "'.' not allow at starting character",
                                    Toast.LENGTH_SHORT
                                ).show();
                                itemList[holder.adapterPosition].toFill = ""
                                itemList[holder.adapterPosition].saleable = ""
//                                itemList[holder.adapterPosition] = mData
                                // allItem[index] = mData
                                holder.binding.edtFill.setText(itemList[holder.adapterPosition].toFill)
                            } else {
                                if (s.toString().toDouble() > modQty) {
                                    s.clear()
                                    Toast.makeText(
                                        context,
                                        "You can not enter qty more than model stock",
                                        Toast.LENGTH_SHORT
                                    ).show();
                                    itemList[holder.adapterPosition].toFill = ""
                                    itemList[holder.adapterPosition].saleable = ""
//                                itemList[holder.adapterPosition] = mData
                                    // allItem[index] = mData
                                    holder.binding.edtFill.setText(itemList[holder.adapterPosition].toFill)
                                } else {
                                    //var index = allItem.indexOf(mData)
                                    var mFill = modQty - s.toString().toDouble()
                                    itemList[holder.adapterPosition].toFill = mFill.toString()
                                    itemList[holder.adapterPosition].saleable = s.toString()
//                                itemList[holder.adapterPosition] = mData
                                    // allItem[index] = mData
                                    holder.binding.edtFill.setText(itemList[holder.adapterPosition].toFill)
                                }
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

            holder.binding.edtFill.addTextChangedListener(
                object : TextWatcher {

                    override fun afterTextChanged(s: Editable) {

                        if (s.toString().isNotEmpty()) {
                            if (s.toString() == ".") {
                                s.clear()
                                Toast.makeText(
                                    context,
                                    "'.' not allow at starting character",
                                    Toast.LENGTH_SHORT
                                ).show();
                                itemList[holder.adapterPosition].toFill = ""
                            } else {
                                //var index = allItem.indexOf(mData)
                                var mFill = s.toString().toDouble()
                                itemList[holder.adapterPosition].toFill = mFill.toString()
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


            holder.binding.layoutExeDt.setOnClickListener {
                onItemClickListener.onItemsClick(position, "Exp")
            }

            holder.binding.layoutDer.setOnClickListener {
                onItemClickListener.onItemsClick(position, "DER")
            }

            holder.binding.layoutSOS!!.setOnClickListener {
                onItemClickListener.onItemsClick(position, "SOS")
            }

            holder.binding.executePendingBindings()

        }


        inner class ViewHolder(val binding: RowItemDistributionBinding) :
            RecyclerView.ViewHolder(binding.root) {
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(charSequence: CharSequence): FilterResults {
                    val charString = charSequence.toString()
                    if (charString.isEmpty()) {
                        itemList = allList

                    } else {
                        val filteredList: MutableList<DistributionItem> =
                            java.util.ArrayList<DistributionItem>()
                        for (row in allList) {

                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match
                            if (row.itemName!!.toLowerCase()
                                    .contains(charString.toLowerCase()) || row.itemCode!!.toLowerCase()
                                    .contains(charString.toLowerCase())
                            ) {
                                filteredList.add(row)
                            }
                        }
                        itemList = filteredList as ArrayList<DistributionItem>
                    }
                    val filterResults = FilterResults()
                    filterResults.values = itemList
                    return filterResults
                }

                override fun publishResults(
                    charSequence: CharSequence,
                    filterResults: FilterResults
                ) {

                    itemList = filterResults.values as ArrayList<DistributionItem>


                    if (isScan && itemList.size > 1) {

                        var itemPos = 0
                        for (i in 0 until itemList!!.size) {
                            if (itemList!![i].itemId.equals(itemPositionId)) {
                                Log.e(
                                    "itemPositionId2",
                                    i.toString() + "-" + itemPositionId + "  " + itemList.size
                                )
                                itemPos = i
                                break
                            }
                        }
                        if (itemPos > 0) {
//                            itemList.removeAt(itemPos)
                            Collections.swap(itemList, itemPos, 0);
//                            val toMove = itemList!![itemPos]
//                            itemList!!.set(itemPos, itemList!![itemPos - 1])
//                            itemList!!.set(itemPos - 1, toMove)
                            Log.e("itemlistsize", Gson().toJson(itemList))
                        }

                        notifyDataSetChanged()
                        isScan = false
                    }
                    notifyDataSetChanged()
                }
            }
        }

    }

    private fun showExpiryDialog(position: Int, mItem: DistributionItem) {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_expiray)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val metrics = resources.displayMetrics
        val screenWidth = (metrics.widthPixels * 0.95).toInt()
        dialog.window!!.setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
        val layoutAdd = dialog.findViewById(R.id.layoutAdd) as LinearLayout
        val layoutCancel = dialog.findViewById(R.id.layoutCancel) as LinearLayout
        val layoutShow = dialog.findViewById(R.id.layoutShow) as LinearLayout
        val edtUom = dialog.findViewById(R.id.edtUom) as TextView
        val txtTitleId = dialog.findViewById(R.id.txtTitleId) as TextView
        val txtTitle = dialog.findViewById(R.id.txtTitle) as TextView
        val edtPC = dialog.findViewById(R.id.edtPC) as EditText
        val edtExpiry = dialog.findViewById(R.id.edtExpiry) as TextView
        val layoutExp = dialog.findViewById(R.id.lytExpiry) as LinearLayout
        val rvExpryList = dialog.findViewById(R.id.rvExpiryList) as RecyclerView
        val layoutShowItem = dialog.findViewById(R.id.layoutShowItem) as LinearLayout
        val frameExpiry = dialog.findViewById(R.id.frameExpiry) as FrameLayout
        val btnAdd_Expry = dialog.findViewById(R.id.btnAddExpiry) as TextView
        edtUom.setText(mItem.uomName)
        txtTitleId.setText(mItem.itemCode)
        txtTitle.setText(mItem.itemName)

        if (isView) {
            layoutAdd.isClickable = false
            layoutAdd.isEnabled = false
            layoutAdd.alpha = 0.5f
        }

        var arrExpiry = mItem.arrExp

        var salaQTy = 0.0
        if (mItem.saleable!!.isNotEmpty()) {
            salaQTy = mItem.saleable!!.toDouble()
            for (mExp in arrExpiry!!) {
                var mQty = mExp["Qty"]!!.toDouble()
                salaQTy -= mQty
            }
        }


        btnAdd_Expry.setOnClickListener {
            layoutAdd.isEnabled = true
            layoutAdd.alpha = 1f
            frameExpiry.visibility = View.GONE
            layoutShowItem.visibility = View.VISIBLE
        }

        layoutShow.setOnClickListener {
            layoutShowItem.visibility = View.GONE
            rvExpryList.visibility = View.VISIBLE
            frameExpiry.visibility = View.VISIBLE
            layoutAdd.isEnabled = false
            layoutAdd.alpha = 0.5f

            rvExpryList.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            var mAdapter = RowInventoryExpiryAdapter(context = this, allList = arrExpiry!!)
            rvExpryList.adapter = mAdapter

        }

        layoutAdd.setOnClickListener {
            frameExpiry.visibility = View.GONE
            layoutShowItem.visibility = View.VISIBLE
            if (edtPC.text.toString() == "") {
                Toast.makeText(this@StoreCheckActivity, "Please insert Qty", Toast.LENGTH_SHORT)
                    .show()
            } else if (edtPC.text.toString().toDouble() > salaQTy) {
                Toast.makeText(
                    this@StoreCheckActivity,
                    "You can not eneter Qty more than saleable",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                var mData = HashMap<String, String>()
                mData.put("UOMId", mItem.uomId!!)
                mData.put("UOM", mItem.uomName!!)
                mData.put("Qty", edtPC.text.toString())
                mData.put("Expiry", edtExpiry.text.toString())
                arrExpiry!!.add(mData)

                mItem.arrExp = arrExpiry
                allItem[position] = mItem
                itemList[position] = mItem

                //mAdapter!!.notifyDataSetChanged()

                Toast.makeText(
                    this@StoreCheckActivity,
                    "Stock added successfully!",
                    Toast.LENGTH_SHORT
                ).show()
                edtPC.setText("")
                edtExpiry.text = ""

                salaQTy = 0.0
                if (mItem.saleable!!.isNotEmpty()) {
                    salaQTy = mItem.saleable!!.toDouble()
                    for (mExp in arrExpiry!!) {
                        var mQty = mExp["Qty"]!!.toDouble()
                        salaQTy -= mQty
                    }
                }
            }
        }

        layoutExp.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)


            val dpd = android.app.DatePickerDialog(
                this,
                android.app.DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    edtExpiry.text = "$year-" + String.format(
                        "%02d",
                        monthOfYear + 1
                    ) + "-" + String.format("%02d", dayOfMonth)

                },
                year,
                month,
                day
            )

            dpd.show()
        }

        layoutCancel.setOnClickListener {
            dialog.dismiss()
        }


        dialog.show()
    }

    private fun showSOSDialog(position: Int, mItem: DistributionItem) {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_sos)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val metrics = resources.displayMetrics
        val screenWidth = (metrics.widthPixels * 0.95).toInt()
        dialog.window!!.setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
        val layoutAdd = dialog.findViewById(R.id.layoutAdd) as LinearLayout
        val layoutCancel = dialog.findViewById(R.id.layoutCancel) as LinearLayout
        val edtScore = dialog.findViewById(R.id.edtScore) as TextView
        val txtTitleId = dialog.findViewById(R.id.txtTitleId) as TextView
        val txtTitle = dialog.findViewById(R.id.txtTitle) as TextView
        val edtTotal = dialog.findViewById(R.id.edtTotal) as EditText
        val edtActual = dialog.findViewById(R.id.edtActual) as TextView
        txtTitleId.setText(mItem.itemCode)
        txtTitle.setText(mItem.itemName)


        if (mItem.totalFacing!!.isNotEmpty()) {
            edtTotal.setText(mItem.totalFacing)
        }

        if (mItem.actualFacing!!.isNotEmpty()) {
            edtActual.text = mItem.totalFacing
        }

        if (mItem.score!!.isNotEmpty()) {
            var mPerc = ((edtActual.text.toString().toInt() * 100) / edtTotal.text.toString()
                .toInt()).toFloat()
            edtScore.text = "$mPerc%"
            layoutAdd.isEnabled = false
            layoutAdd.isClickable = false
            layoutAdd.alpha = 0.5f
        }

        if (isView) {
            layoutAdd.isClickable = false
            layoutAdd.isEnabled = false
            layoutAdd.alpha = 0.5f
        }

        edtActual.addTextChangedListener(
            object : TextWatcher {

                override fun afterTextChanged(s: Editable) {

                    if (s.toString().isNotEmpty()) {
                        //var index = allItem.indexOf(mData)
                        if (edtTotal.text.toString().isNotEmpty()) {
                            var mPerc = ((edtActual.text.toString()
                                .toInt() * 100) / edtTotal.text.toString()
                                .toInt()).toFloat()
                            edtScore.text = "$mPerc%"
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


        layoutAdd.setOnClickListener {

            if (edtTotal.text.toString().isEmpty()) {
                Toast.makeText(
                    this@StoreCheckActivity,
                    "Please enter total facings",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else if (edtActual.text.toString().isEmpty()) {
                Toast.makeText(
                    this@StoreCheckActivity,
                    "Please enter actual facings",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                var mPerc = ((edtActual.text.toString().toInt() * 100) / edtTotal.text.toString()
                    .toInt()).toFloat()
                edtScore.text = "$mPerc%"

                mItem.totalFacing = edtTotal.text.toString()
                mItem.actualFacing = edtActual.text.toString()
                mItem.score = mPerc.toString()
                allItem[position] = mItem
                itemList[position] = mItem
                //mAdapter!!.notifyDataSetChanged()

                Toast.makeText(
                    this@StoreCheckActivity,
                    "SOS added successfully!",
                    Toast.LENGTH_SHORT
                ).show()
                layoutAdd.isEnabled = false
                layoutAdd.isClickable = false
                layoutAdd.alpha = 0.5f
            }
        }

        layoutCancel.setOnClickListener {
            dialog.dismiss()
        }


        dialog.show()
    }

    private fun showAvarageSOSDialog() {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_avarage_sos)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val metrics = resources.displayMetrics
        val screenWidth = (metrics.widthPixels * 0.95).toInt()
        dialog.window!!.setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
        val layoutCancel = dialog.findViewById(R.id.layoutCancel) as LinearLayout
        val edtScore = dialog.findViewById(R.id.edtScore) as TextView
        val txtTitle = dialog.findViewById(R.id.txtTitle) as TextView
        val rvExpryList = dialog.findViewById(R.id.rvList) as RecyclerView
        txtTitle.text = displayToolName!!


        var arrDer: MutableList<HashMap<String, String>> = ArrayList()
        var totalValue = 0.0
        for (mItem in allItem) {
            var mData = HashMap<String, String>()
            if (mItem.actualFacing!!.isNotEmpty()) {
                mData.put("Item", mItem.itemName!!)
                mData.put("Score", mItem.score!!)
                mData.put("Total", mItem.totalFacing.toString())
                mData.put("Actual", mItem.actualFacing.toString())
                arrDer!!.add(mData)
                totalValue += mItem.score!!.toFloat()
            }
        }

        var avarage = (totalValue / (arrDer.size * 100)) * 100
        edtScore.text = "$avarage%"

        rvExpryList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        var mAdapter = RowSOSAdapter(context = this, allList = arrDer!!)
        rvExpryList.adapter = mAdapter

        layoutCancel.setOnClickListener {
            dialog.dismiss()
        }


        dialog.show()
    }


    private fun showDERDialog(position: Int, mItem: DistributionItem) {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.view_der_tool)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val metrics = resources.displayMetrics
        val screenWidth = (metrics.widthPixels * 0.95).toInt()
        dialog.window!!.setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
        val layoutAdd = dialog.findViewById(R.id.layoutAdd) as LinearLayout
        val layoutCancel = dialog.findViewById(R.id.layoutCancel) as LinearLayout
        val layoutShow = dialog.findViewById(R.id.layoutShow) as LinearLayout
        val txtTitleId =
            dialog.findViewById<View>(R.id.txtTitleId) as TextView
        val txtTitle = dialog.findViewById<View>(R.id.txtTitle) as TextView
        val edtDamaged =
            dialog.findViewById<View>(R.id.edtDamegedItem) as EditText
        val edtExpiry =
            dialog.findViewById<View>(R.id.edtExpiredItem) as EditText
        val edtSaleableItem =
            dialog.findViewById<View>(R.id.edtSaleableItem) as EditText
        txtTitleId.setText(mItem.itemCode)
        txtTitle.setText(mItem.itemName)
        layoutShow.visibility = View.GONE

        var arrDer = mItem.arrDer

        if (arrDer!!.size > 0) {
            layoutAdd.isEnabled = false
            layoutAdd.isClickable = false
            layoutAdd.alpha = 0.5f

            edtDamaged.setText(arrDer[0]["Damage"])
            edtExpiry.setText(arrDer[0]["Expiry"])
            edtSaleableItem.setText(arrDer[0]["Saleble"])
        }

        if (isView) {
            layoutAdd.isClickable = false
            layoutAdd.isEnabled = false
            layoutAdd.alpha = 0.5f
        }

        layoutAdd.setOnClickListener {

            if (edtDamaged.text.toString().isEmpty() && edtExpiry.text.toString()
                    .isEmpty() && edtSaleableItem.text.toString().isEmpty()
            ) {
                Toast.makeText(
                    this@StoreCheckActivity,
                    "Please enter damaged, expiry or salable qty",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {

                var damg = "0"
                var exp = "0"
                var salb = "0"
                if (edtDamaged.text.toString().isNotEmpty()) {
                    damg = edtDamaged.text.toString()
                }

                if (edtExpiry.text.toString().isNotEmpty()) {
                    exp = edtExpiry.text.toString()
                }

                if (edtSaleableItem.text.toString().isNotEmpty()) {
                    salb = edtSaleableItem.text.toString()
                }

                var mData = HashMap<String, String>()
                mData.put("UOMId", mItem.uomId!!)
                mData.put("UOM", mItem.uomName!!)
                mData.put("Damage", damg)
                mData.put("Expiry", exp)
                mData.put("Saleble", salb)
                arrDer!!.add(mData)

                mItem.arrDer = arrDer
                allItem[position] = mItem
                itemList[position] = mItem
                //mAdapter!!.notifyDataSetChanged()

                Toast.makeText(
                    this@StoreCheckActivity,
                    "DER added successfully!",
                    Toast.LENGTH_SHORT
                ).show()
                edtDamaged.setText("")
                edtExpiry.setText("")
                edtSaleableItem.setText("")
            }
        }


        layoutCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onItemsClick(position: Int, tag: String) {
        if (tag == "Exp") {
            showExpiryDialog(position, itemList[position])
        } else if (tag == "SOS") {
            showSOSDialog(position, itemList[position])
        } else {
            showDERDialog(position, itemList[position])
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == scanCode) {
            if (resultCode == Activity.RESULT_OK) {

                var id = data!!.getStringExtra("scanid")
                Log.e("scanid", id!!)
                for (i in 0 until itemList!!.size) {
                    if (itemList!![i].itembarcode.equals(id)) {
                        itemPositionId = itemList!![i].itemId!!
                    }
                }
                searchView!!.setQuery(id, true)
//                mAdapter!!.filter!!.filter("400896")
                isScan = true


            }
        }
        if (requestCode == filterCode) {
            if (resultCode == Activity.RESULT_OK) {

                if (data!!.getStringExtra("isClear") != null) {
                    itemList.clear()
                    itemList = db!!.getAllDistributionItemList(
                        customerObj!!.user_id.toString(),
                        displayToolId!!
                    )
                    setAdapter()
                } else {
                    val strObj = data!!.getStringExtra("itemList")
                    val listOfUomType: Type =
                        object : TypeToken<List<java.util.HashMap<String, String>?>?>() {}.type

                    var mList =
                        Gson().fromJson(
                            strObj,
                            listOfUomType
                        ) as ArrayList<java.util.HashMap<String, String>>

                    var brand: ArrayList<String> = ArrayList()
                    var category: ArrayList<String> = ArrayList()
                    var itemGroup: ArrayList<String> = ArrayList()
                    for (mDat in mList) {
                        if (mDat["Type"] == "Brand") {
                            brand.add(mDat["Id"]!!)
                        } else if (mDat["Type"] == "Category") {
                            category.add(mDat["Id"]!!)
                        } else if (mDat["Type"] == "Item Group") {
                            itemGroup.add(mDat["Id"]!!)
                        }
                    }

                    itemList.clear()
                    itemList = getFilterItem(brand, category, itemGroup)
                    setAdapter()
                }
            }
        }
    }

    fun getFilterItem(
        mBrand: ArrayList<String>,
        mCategory: ArrayList<String>,
        mGroup: ArrayList<String>
    ): ArrayList<DistributionItem> {

        var allData: ArrayList<DistributionItem> = ArrayList()

        for (mData in allItem) {
            if (mCategory.isNotEmpty() && mBrand.isNotEmpty() && mGroup.isNotEmpty()) {
                if (mCategory.contains(mData.categoryId) && mBrand.contains(mData.brand) && mGroup.contains(
                        mData.group
                    )
                ) {
                    allData.add(mData)
                }
            } else if (mCategory.isNotEmpty() && mBrand.isNotEmpty()) {
                if (mCategory.contains(mData.categoryId) && mBrand.contains(mData.brand)) {
                    allData.add(mData)
                }
            } else if (mCategory.isNotEmpty() && mGroup.isNotEmpty()) {
                if (mCategory.contains(mData.categoryId) && mGroup.contains(mData.group)) {
                    allData.add(mData)
                }
            } else if (mBrand.isNotEmpty() && mGroup.isNotEmpty()) {
                if (mBrand.contains(mData.brand) && mGroup.contains(mData.group)) {
                    allData.add(mData)
                }
            } else if (mGroup.isNotEmpty()) {
                if (mGroup.contains(mData.group)) {
                    allData.add(mData)
                }
            } else if (mCategory.isNotEmpty()) {
                if (mCategory.contains(mData.categoryId)) {
                    allData.add(mData)
                }
            } else if (mBrand.isNotEmpty()) {
                if (mBrand.contains(mData.brand)) {
                    allData.add(mData)
                }
            }
        }
        return allData
    }

    override fun onOpened(p0: EMDKManager?) {
        emdkManager = p0

        try {
            // Call this method to enable Scanner and its listeners
            initializeScanner()
        } catch (e: ScannerException) {
            e.printStackTrace()
        }

    }

    override fun onClosed() {
        if (emdkManager != null) {

            // Release all the resources
            emdkManager!!.release()
            emdkManager = null
        }
    }

    // Method to initialize and enable Scanner and its listeners
    @Throws(ScannerException::class)
    private fun initializeScanner() {
        if (scanner == null) {
            // Get the Barcode Manager object
            barcodeManager = emdkManager!!
                .getInstance(EMDKManager.FEATURE_TYPE.BARCODE) as BarcodeManager
            // Get default scanner defined on the device
            scanner = barcodeManager!!.getDevice(BarcodeManager.DeviceIdentifier.DEFAULT)
            // Add data and status listeners
            scanner!!.addDataListener(this)
            scanner!!.addStatusListener(this)
            // Hard trigger. When this mode is set, the user has to manually
            // press the trigger on the device after issuing the read call.
            scanner!!.triggerType = Scanner.TriggerType.HARD
            // Enable the scanner
            scanner!!.enable()
            // Starts an asynchronous Scan. The method will not turn ON the
            // scanner. It will, however, put the scanner in a state in which
            // the scanner can be turned ON either by pressing a hardware
            // trigger or can be turned ON automatically.
            scanner!!.read()
        }
    }

    override fun onStop() {
        // TODO Auto-generated method stub
        super.onStop()
        try {
            if (scanner != null) {
                // releases the scanner hardware resources for other application
                // to use. You must call this as soon as you're done with the
                // scanning.
                scanner!!.removeDataListener(this)
                scanner!!.removeStatusListener(this)
                scanner!!.disable()
                scanner = null
            }
        } catch (e: ScannerException) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (emdkManager != null) {

            // Clean up the objects created by EMDK manager
            emdkManager!!.release()
            emdkManager = null
        }
    }

    override fun onData(scanDataCollection: ScanDataCollection?) {
        if (scanDataCollection != null && scanDataCollection.result == ScannerResults.SUCCESS) {
            val scanData: java.util.ArrayList<ScanDataCollection.ScanData> =
                scanDataCollection.scanData
            for (data in scanData) {
                val dataString = data.data
                AsyncDataUpdate(this@StoreCheckActivity)
                    .execute(dataString)
            }
        }
    }

    override fun onStatus(statusData: StatusData?) {
        val state: StatusData.ScannerStates = statusData!!.state
        when (state) {
            StatusData.ScannerStates.IDLE -> {
                statusString = statusData!!.friendlyName + " is enabled and idle..."
                AsyncStatusUpdate()
                    .execute(statusString)
                if (bContinuousMode) {
                    try {
                        // An attempt to use the scanner continuously and rapidly (with a delay < 100 ms between scans)
                        // may cause the scanner to pause momentarily before resuming the scanning.
                        // Hence add some delay (>= 100ms) before submitting the next read.
                        try {
                            Thread.sleep(100)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                        scanner!!.read()
                    } catch (e: ScannerException) {
                        statusString = e.message!!
                        AsyncStatusUpdate()
                            .execute(statusString)
                    }
                }
            }
            StatusData.ScannerStates.WAITING -> {
                statusString = "Scanner is waiting for trigger press..."
                AsyncStatusUpdate()
                    .execute(statusString)
            }
            StatusData.ScannerStates.SCANNING -> {
                statusString = "Scanning..."
                AsyncStatusUpdate()
                    .execute(statusString)
            }
            StatusData.ScannerStates.DISABLED -> {
                statusString = statusData!!.friendlyName + " is disabled."
                AsyncStatusUpdate()
                    .execute(statusString)
            }
            StatusData.ScannerStates.ERROR -> {
                statusString = "An error has occurred."
                AsyncStatusUpdate()
                    .execute(statusString)
            }
            else -> {
            }
        }

    }

    private class AsyncStatusUpdate :
        AsyncTask<String?, Void?, String>() {

        override fun onPostExecute(result: String) {}
        override fun doInBackground(vararg params: String?): String {
            return params[0]!!
        }
    }

    fun openScanItem(barcode: String) {

        if (barcode.isNotEmpty()) {
            var itemId = getItemId(barcode)
            var itemPosition = getItemPosition(itemId)
            //openQuantityDialog()
            //searchView!!.performClick()
            searchView!!.isIconified = false
            //searchView!!.onActionViewExpanded()
            searchView!!.setQuery(itemPosition, false)
            //mAdapter!!.filter!!.filter(itemPosition)
        } else {
            Toast.makeText(
                applicationContext,
                "Item not available for given barcode!!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun getItemId(barcode: String): Int {

        var itemId = 0
        for (i in allItem!!) {
            if (i.itembarcode == barcode) {
                itemId = i.itemId!!.toInt()
                break
            }
        }

        return itemId
    }

    fun getItemPosition(itemId: Int): String {
        var mName = ""
        for (mItem in allItem!!) {
            if (mItem.itemId == itemId.toString()) {
                mName = mItem.itemCode!!
                break
            }
        }
        return mName
    }

}