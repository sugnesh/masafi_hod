package com.engear.vansales.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.R
import com.engear.vansales.adapter.UnloadRequestItemAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.Constants
import com.engear.vansales.common.DeviceListActivity
import com.engear.vansales.databinding.ActivityFreshUnloadBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.interfaces.OnSearchableDialog
import com.engear.vansales.model.*
import com.engear.vansales.printer.BluetoothPrintService
import com.engear.vansales.printer.PrintLog
import com.engear.vansales.printer.WoosimCustomService
import com.engear.vansales.utils.PrinterHelper
import com.engear.vansales.utils.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList

class FreshUnloadActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivityFreshUnloadBinding
    private var type = "Fresh Unload"
    private var countPlus = 0
    private var unloadStep = 0
    private var unloadType = "1"
    private var strUnloadDate = ""
    private val intentCode = 200
    private var arrUnloadFlag: ArrayList<UnloadFlag> = ArrayList()
    private var unloadNumber = ""
    private var jsonArray: JSONArray? = null
    private var unloadBadNumber = ""
    private var unloadVarieanceNumber = ""
    private var unloadMainType = ""
    var arrEndData: java.util.ArrayList<UnloadSummary> = ArrayList()
    var isClick = false

    private val REQUEST_ENABLE_BT = 3
    private var mBluetoothAdapter: BluetoothAdapter? = null

    private val REQUEST_CONNECT_DEVICE_SECURE = 1

    val MESSAGE_DEVICE_NAME = 1
    val MESSAGE_TOAST = 2
    val MESSAGE_READ = 3

    val DEVICE_NAME = "device_name"
    val TOAST = "toast"


    companion object {

        var mActivity: FreshUnloadActivity? = null

        var progressDialog: ProgressDialog? = null
        var itemsList: ArrayList<ItemData.Data>? = ArrayList()
        var badDamageItemList: ArrayList<UomData>? = ArrayList()
        var badExpiryItemList: ArrayList<UomData>? = ArrayList()
        var allBadItemList: ArrayList<UomData>? = ArrayList()
        var returnItemList: ArrayList<Order>? = ArrayList()

        fun callbackFunction() {
            mActivity!!.finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFreshUnloadBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@FreshUnloadActivity,
            "On Fresh Unload Screen "
        )
        mActivity = this
        init()
    }

    private fun init() {
        setToolbarTitle("Fresh Unload")

        progressDialog = ProgressDialog(this)
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()


        itemsList = ArrayList()
        if (intent.getStringExtra("Type") != null) {
            unloadMainType = intent.getStringExtra("Type")!!
        }

        getUnloadData()

        if (arrUnloadFlag.size > 0) {
            type = arrUnloadFlag[0].unloadType!!
            unloadType = arrUnloadFlag[0].unloadTypeCode!!
        }

        binding.btnUnload.setImageResource(R.drawable.ic_check_white)
        itemsList!!.clear()
        itemsList = ArrayList()
        itemsList = db!!.getAllEndSaleItem()
        binding.rvList.visibility = View.VISIBLE
        binding.txtNoContain.visibility = View.GONE
        setUpRecycler()

        binding.btnUnload.setOnClickListener {
            Utils.logData(
                this@FreshUnloadActivity,
                "On Unload buttn click"
            )

            if (!isClick) {
                isClick = true

                val alertDialogBuilder =
                    AlertDialog.Builder(this@FreshUnloadActivity)
                alertDialogBuilder.setTitle("Alert")
                    .setMessage("Are you sure you want to Unload $type?")
                    .setCancelable(false)
                    .setPositiveButton(
                        "Yes"
                    ) { dialog, which ->
                        isClick = false
                        binding.btnUnload.visibility = View.GONE
                        val task = getAsyncTask(this@FreshUnloadActivity)
                        task.execute("")
                    }
                    .setNegativeButton(
                        "No"
                    ) { dialog, which ->
                        isClick = false
                        dialog.dismiss()
                    }

                alertDialogBuilder.show()
            }

        }

        //Bad Item List
        badExpiryItemList = db!!.getBadReturnExpiryPickItem()
        allBadItemList = db!!.getBadReturnExpiryPickItem()
        returnItemList = db!!.getComplteReturn()


    }


    class getAsyncTask internal constructor(context: FreshUnloadActivity) :
        AsyncTask<String, String, String?>() {

        private val activityReference: WeakReference<FreshUnloadActivity> = WeakReference(context)

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            val activity = activityReference.get()
            if (progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
            }

            activity!!.printDialog()

        }

        override fun doInBackground(vararg params: String?): String? {

            val activity = activityReference.get()

            if (itemsList!!.size > 0) {
                activity!!.addVariance()
            }

//            if (badDamageItemList!!.size > 0) {
//                activity!!.addBadUnload(badDamageItemList!!, "1")
//            }

            if (returnItemList!!.size > 0) {
                activity!!.addCustomerBadUnload(returnItemList!!)
            }

//            if (activity!!.unloadVarieanceNumber.isNotEmpty()) {
//                activity!!.insertVarianceUnloadTransaction()
//            }

            activity!!.parseEndInventory()

            activity!!.insertUnloadTransaction()

            return ""
        }

        override fun onPreExecute() {
            super.onPreExecute()
            val activity = activityReference.get()
            if (activity != null) {
                progressDialog!!.setMessage("Please wait...")
                progressDialog!!.show()
            }
        }
    }

    private fun printDialog() {

        Utils.dialogPrint(mActivity!!, object : OnSearchableDialog {

            override fun onItemSelected(selection: String?) {

                if (selection.equals("yes", ignoreCase = true)) {
                    PrintLog.mPrintService = mPrintService
                    callDevice()
                } else {
                    finish()
                }
            }
        })
    }


    private fun parseEndInventory() {

        arrEndData = ArrayList()

        var vanItem = db!!.getAllVanSalesItemList()

        for (mItem in vanItem) {

            var mUnloadSM = UnloadSummary()
            mUnloadSM.itemCode = mItem.itemCode
            var isResale = "0"
            if (mItem.itemIsReSale == "1") {
                isResale = "1"
                mUnloadSM.name = "(Resalable) " + mItem.itemName
            } else {
                mUnloadSM.name = mItem.itemName
            }

            mUnloadSM.uom = mItem.itemDisplayUnitName

            var LQqty = Utils.getUOMQty(mItem.itemDisplayUPC!!, mItem.itemQty.toString()).toDouble()

            mUnloadSM.loadQty = LQqty.toString()

            var saleQty = db!!.getSaleItemQty(mItem.itemId.toString(), isResale)
            var returnQTy = db!!.getAllReturnItemQty(mItem.itemId.toString(), isResale)
            var unloadQTy = db!!.getUnloadItemQty(mItem.itemId.toString(), isResale)
            var uvaraieenceQTy = db!!.getUnloadVarienaceItemQty(mItem.itemId.toString(), isResale)
            var freshQty = db!!.getUnloadFreshItemQty(mItem.itemId.toString(), isResale)

            var qty = Utils.getUOMQty(mItem.itemDisplayUPC!!, saleQty.toString()).toDouble()
            mUnloadSM.salesQty = qty.toString()
//            var displayQty = qty.toInt()
//            var totalBaseQty = displayQty * mItem.itemDisplayUPC!!.toDouble()
//            if (totalBaseQty == saleQty.toDouble()) {
//                mUnloadSM.salesQty = displayQty!!.toString()
//            } else {
//                mUnloadSM.salesQty = saleQty.toString()
//            }

            var Rqty = Utils.getUOMQty(mItem.itemDisplayUPC!!, returnQTy.toString()).toDouble()
            // var displayRQty = Rqty.toInt()
            mUnloadSM.returnQty = Rqty.toString()
//            var totalBaseRQty = displayRQty * mItem.itemDisplayUPC!!.toDouble()
//            if (totalBaseRQty == returnQTy.toDouble()) {
//                mUnloadSM.returnQty = displayRQty!!.toString()
//            } else {
//                mUnloadSM.returnQty = returnQTy.toString()
//            }


            var Uqty = Utils.getUOMQty(mItem.itemDisplayUPC!!, unloadQTy.toString()).toDouble()
            mUnloadSM.unloadQty = Uqty!!.toString()
            //           var displayUQty = Uqty.toInt()
//            var totalBaseUQty = displayUQty * mItem.itemDisplayUPC!!.toDouble()
//            if (totalBaseUQty == unloadQTy.toDouble()) {
//                mUnloadSM.unloadQty = Uqty!!.toString()
//            } else {
//                mUnloadSM.unloadQty = unloadQTy.toString()
//            }

            //var UFqty = Utils.getUOMQty(mItem.itemDisplayUPC!!, freshQty.toString()).toDouble()
            mUnloadSM.freshQty = freshQty!!.toString()
//            var displayUFQty = UFqty.toInt()
//            var totalBaseUFQty = displayUFQty * mItem.itemDisplayUPC!!.toDouble()
//            if (totalBaseUFQty == freshQty.toDouble()) {
//                mUnloadSM.freshQty = displayUFQty!!.toString()
//            } else {
//                mUnloadSM.freshQty = freshQty.toString()
//            }
            mUnloadSM.variance = uvaraieenceQTy.toString()

            arrEndData.add(mUnloadSM)
        }

        if (allBadItemList!!.size > 0) {
            for (mBadItem in allBadItemList!!) {
                var isCOntain = false
                for (mEndItem in arrEndData) {
                    if (mEndItem.itemCode == mBadItem.itemCode) {
                        isCOntain = true
                    }
                }

                if (!isCOntain) {
                    var mUnloadSM = UnloadSummary()
                    mUnloadSM.itemCode = mBadItem.itemCode
                    mUnloadSM.name = mBadItem.itemName
                    mUnloadSM.uom = mBadItem.uomType
                    mUnloadSM.loadQty = "0.0"
                    mUnloadSM.salesQty = "0.0"
                    mUnloadSM.returnQty = mBadItem.uomQty
                    mUnloadSM.unloadQty = "0.0"
                    mUnloadSM.variance = "0.0"
                    mUnloadSM.freshQty = "0.0"
                    arrEndData.add(mUnloadSM)
                }
            }
        }
    }


    private fun insertUnloadTransaction() {

        //get Print Data
        var data: JSONObject? = null
        try {
            jsonArray = createPrintUnloadData(unloadNumber)
            data = JSONObject()
            data.put("data", jsonArray as JSONArray?)
            Log.e("Print", data.toString())
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        //INSERT TRANSACTION
        val transaction = Transaction()
        transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_UNLOAD
        transaction.tr_date_time =
            Utils.getCurrentDate() + " " + Utils.getCurrentTime()
        transaction.tr_customer_num = ""
        transaction.tr_customer_name = ""
        transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
        transaction.tr_invoice_id = ""
        transaction.tr_order_id = unloadNumber
        transaction.tr_collection_id = ""
        transaction.tr_pyament_id = ""
        transaction.tr_is_posted = "No"
        transaction.tr_printData = data.toString()
        db!!.insertTransaction(transaction)

        //set Order Last Number
        //mPreferenceManager!!.setString(AppConstants.UNLOAD_LAST, unloadNumber)

        db!!.markForPostUnload()

        mPreferenceManager!!.setString(Constants.IS_UNLOAD, "1")
        mPreferenceManager!!.setString(Constants.IS_ENDDAY, "1")

        if (mPreferenceManager!!.getString(Constants.IS_ENDDAY) == "1") {
            if (db!!.getPostUnLoadRequestCount() === 0) {
                db!!.updateUnloadTransaction(unloadNumber, "1", "")
                mPreferenceManager!!.setString(Constants.IS_UNLOAD_POST, "1")
            }
        }

    }

    private fun insertBadUnloadTransaction(
        unloadBadNumber: String,
        data: JSONObject?,
        mOrderNo: String,
    ) {

        //INSERT TRANSACTION
        val transaction = Transaction()
        transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_UNLOAD_VAN_RETURN
        transaction.tr_date_time =
            Utils.getCurrentDate() + " " + Utils.getCurrentTime()
        transaction.tr_customer_num = ""
        transaction.tr_customer_name = ""
        transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
        transaction.tr_invoice_id = ""
        transaction.tr_order_id = unloadBadNumber
        transaction.tr_collection_id = ""
        transaction.tr_pyament_id = ""
        transaction.tr_is_posted = "No"
        transaction.tr_printData = data.toString()
        db!!.insertTransaction(transaction)

        //set Order Last Number
        //mPreferenceManager!!.setString(AppConstants.UNLOAD_LAST, mOrderNo)

        db!!.markForPostBadUnload()

    }

    private fun insertVarianceUnloadTransaction() {

        //INSERT TRANSACTION
        val transaction = Transaction()
        transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_UNLOAD_VARIANCE
        transaction.tr_date_time =
            Utils.getCurrentDate() + " " + Utils.getCurrentTime()
        transaction.tr_customer_num = ""
        transaction.tr_customer_name = ""
        transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
        transaction.tr_invoice_id = ""
        transaction.tr_order_id = unloadVarieanceNumber
        transaction.tr_collection_id = ""
        transaction.tr_pyament_id = ""
        transaction.tr_is_posted = "No"
        transaction.tr_printData = ""
        db!!.insertTransaction(transaction)

        db!!.markForPostExpiryUnload()

    }

    private fun addVarianceDamage() {

        //Add Collection
        unloadVarieanceNumber =
            Utils.getLastIndexNew(
                mPreferenceManager!!.getString(AppConstants.UNLOAD_LAST).toString(), "unload"
            )

        // db!!.insertUnloadVarianceDamage(unloadType, unloadNumber, itemsList!!)
        db!!.insertFreshUnload(
            unloadVarieanceNumber,
            "5",
            "5",
            mPreferenceManager!!.getTripId(),
            itemsList!!
        )

        //update VanSTock Qty
        for (mData in itemsList!!) {
            var mQty = 0.0
            for (mUOM in mData.itemSelectUOM) {
                mQty += Utils.getUOMLowerQty(mUOM.uomUPC, mUOM.uomQty).toDouble()
            }

            if (mData.itemWeight == "1") {
                var existQty =
                    db!!.getVanStockExistReSaleItemQty(
                        mData.id.toString(),
                        mData.vanUOM,
                        "Remaining"
                    )
                        .toDouble()

                var mUpdateQty = existQty - mQty

                db!!.updateVanStockReSaleQty(
                    mData.id.toString(),
                    mData.vanUOM,
                    mUpdateQty.toString()
                )
            } else {
                var existQty =
                    db!!.getVanStockExistItemQty(mData.id.toString(), mData.vanUOM, "Remaining")
                        .toDouble()

                var mUpdateQty = existQty - mQty

                db!!.updateVanStockQty(mData.id.toString(), mData.vanUOM, mUpdateQty.toString())
            }

        }

        //set Order Last Number
        mPreferenceManager!!.setString(AppConstants.UNLOAD_LAST, unloadVarieanceNumber)

    }

    private fun addFreshDamage() {

        if (unloadNumber.isEmpty()) {
            //Add Collection
            unloadNumber =
                Utils.getLastIndexNew(
                    mPreferenceManager!!.getString(AppConstants.UNLOAD_LAST).toString(), "unload"
                )
        }

        // db!!.insertUnloadVarianceDamage(unloadType, unloadNumber, itemsList!!)
        db!!.insertFreshUnload(
            unloadNumber,
            "4",
            "1",
            mPreferenceManager!!.getTripId(),
            itemsList!!
        )

        //update VanSTock Qty
        for (mData in itemsList!!) {
            var mQty = 0.0
            for (mUOM in mData.itemSelectUOM) {
                mQty += Utils.getUOMLowerQty(mUOM.uomUPC, mUOM.uomQty).toDouble()
            }

            if (mData.itemWeight == "1") {
                var existQty =
                    db!!.getVanStockExistReSaleItemQty(
                        mData.id.toString(),
                        mData.vanUOM,
                        "Remaining"
                    )
                        .toDouble()

                var mUpdateQty = existQty - mQty

                db!!.updateVanStockReSaleQty(
                    mData.id.toString(),
                    mData.vanUOM,
                    mUpdateQty.toString()
                )
            } else {
                var existQty =
                    db!!.getVanStockExistItemQty(mData.id.toString(), mData.vanUOM, "Remaining")
                        .toDouble()

                var mUpdateQty = existQty - mQty

                db!!.updateVanStockQty(mData.id.toString(), mData.vanUOM, mUpdateQty.toString())
            }
        }

    }

    private fun addVariance() {

        var mOrderNo = ""

        mOrderNo =
            Utils.getLastIndexNew(
                mPreferenceManager!!.getString(AppConstants.UNLOAD_LAST).toString(), "unload"
            )

        if (mPreferenceManager!!.getFTokenId()!! == "2") {
            var routeCode =
                mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode

            unloadNumber = "V${routeCode}UL" +
                    Utils.getLastIndexNew(
                        mPreferenceManager!!.getString(AppConstants.UNLOAD_LAST).toString(),
                        "unload"
                    )
        } else if (mPreferenceManager!!.getFTokenId()!! == "4") {
            var routeCode =
                mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode

            unloadNumber = "D${routeCode}UL" +
                    Utils.getLastIndexNew(
                        mPreferenceManager!!.getString(AppConstants.UNLOAD_LAST).toString(),
                        "unload"
                    )
        } else if (mPreferenceManager!!.getFTokenId()!! == "3") {
            var routeCode =
                mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode

            unloadNumber = "HH${routeCode}UL" +
                    Utils.getLastIndexNew(
                        mPreferenceManager!!.getString(AppConstants.UNLOAD_LAST).toString(),
                        "unload"
                    )
        } else {
            unloadNumber =
                Utils.getLastIndexNew(
                    mPreferenceManager!!.getString(AppConstants.UNLOAD_LAST).toString(), "unload"
                )
        }


        // db!!.insertUnloadVariance(unloadType, unloadNumber, itemsList!!)
        var mType = "1"
        db!!.insertFreshUnload(
            unloadNumber,
            "1",
            mType,
            mPreferenceManager!!.getTripId(),
            itemsList!!
        )
        //update VanSTock Qty
        for (mData in itemsList!!) {
            var mQty = 0.0
            for (mUOM in mData.itemSelectUOM) {
                mQty += Utils.getUOMLowerQty(mUOM.uomUPC, mUOM.uomQty).toDouble()
            }

            if (mData.itemWeight == "1") {
                var existQty =
                    db!!.getVanStockExistReSaleItemQty(
                        mData.id.toString(),
                        mData.vanUOM,
                        "Remaining"
                    )
                        .toDouble()

                var mUpdateQty = existQty - mQty

                db!!.updateVanStockReSaleQty(
                    mData.id.toString(),
                    mData.vanUOM,
                    mUpdateQty.toString()
                )
            } else {
                var existQty =
                    db!!.getVanStockExistItemQty(mData.id.toString(), mData.vanUOM, "Remaining")
                        .toDouble()

                var mUpdateQty = existQty - mQty

                db!!.updateVanStockQty(mData.id.toString(), mData.vanUOM, mUpdateQty.toString())
            }
        }

        //set Order Last Number
        mPreferenceManager!!.setString(AppConstants.UNLOAD_LAST, mOrderNo)

    }

    private fun addCustomerBadUnload(mItemList: List<Order>) {

        for (mCustRe in mItemList!!) {

            var mReItemList = db!!.getReturnDetail(mCustRe.orderNo!!)

            var mType = "2"
            db!!.insertBadUnloadOld(
                mCustRe.orderNo!!,
                mType,
                mType,
                mPreferenceManager!!.getTripId(),
                "1",
                mCustRe.rID!!,
                mReItemList!!
            )

            var jsonArray: JSONArray? = null
            //get Print Data
            var data: JSONObject? = null
            try {
//                if (mIsDamage == "1") {
//                    jsonArray = createPrintBadDamagedData(mCustRe.orderNo!!, mReItemList)
//                    data = JSONObject()
//                    data.put("data", jsonArray as JSONArray?)
//                } else {
                jsonArray = createPrintBadExpiryData(mCustRe.orderNo!!, mReItemList)
                data = JSONObject()
                data.put("data", jsonArray as JSONArray?)
                // }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            insertBadUnloadTransaction(mCustRe.orderNo!!, data, mCustRe.orderNo!!)
        }
    }

    private fun addBadUnload(mItemList: List<UomData>, mIsDamage: String) {

        var mOrderNo = ""

        mOrderNo =
            Utils.getLastIndexNew(
                mPreferenceManager!!.getString(AppConstants.UNLOAD_LAST).toString(), "unload"
            )

        if (mPreferenceManager!!.getFTokenId()!! == "2") {
            var routeCode =
                mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode

            unloadBadNumber = "V${routeCode}UL" +
                    Utils.getLastIndexNew(
                        mPreferenceManager!!.getString(AppConstants.UNLOAD_LAST).toString(),
                        "unload"
                    )
        } else if (mPreferenceManager!!.getFTokenId()!! == "4") {
            var routeCode =
                mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode

            unloadBadNumber = "D${routeCode}UL" +
                    Utils.getLastIndexNew(
                        mPreferenceManager!!.getString(AppConstants.UNLOAD_LAST).toString(),
                        "unload"
                    )
        } else {
            unloadBadNumber =
                Utils.getLastIndexNew(
                    mPreferenceManager!!.getString(AppConstants.UNLOAD_LAST).toString(), "unload"
                )
        }

        // db!!.insertUnloadVariance(unloadType, unloadNumber, itemsList!!)
//        var mType = "2"
//        db!!.insertBadUnloadOld(
//            unloadBadNumber,
//            mType,
//            mType,
//            mPreferenceManager!!.getTripId(),
//            mIsDamage,
//            mItemList!!
//        )

        var jsonArray: JSONArray? = null
        //get Print Data
        var data: JSONObject? = null
        try {
            if (mIsDamage == "1") {
                jsonArray = createPrintBadDamagedData(unloadBadNumber, mItemList)
                data = JSONObject()
                data.put("data", jsonArray as JSONArray?)
            } else {
                jsonArray = createPrintBadExpiryData(unloadBadNumber, mItemList)
                data = JSONObject()
                data.put("data", jsonArray as JSONArray?)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        insertBadUnloadTransaction(unloadBadNumber, data, mOrderNo)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == intentCode) {
            if (resultCode == Activity.RESULT_OK) {
                itemsList = ArrayList()

                val strObj = data!!.getStringExtra("ItemList")
                val listOfUomType: Type =
                    object : TypeToken<List<ItemData.Data?>?>() {}.type

                var mList =
                    Gson().fromJson(strObj, listOfUomType) as ArrayList<ItemData.Data>

                itemsList = mList
                setUpRecycler()
            }
        } else if (requestCode == REQUEST_CONNECT_DEVICE_SECURE) {
            if (resultCode == RESULT_OK) {
                connectDevice(data!!, true)
            }
        }
    }

    private fun updateData(countPlus: Int) {
        if (countPlus == (unloadStep - 1)) {
            type = "End Inventory"
            unloadType = "1"
            binding.btnUnload.setImageResource(R.drawable.ic_check_white)
            itemsList!!.clear()
            itemsList = ArrayList()
            itemsList = db!!.getAllEndSaleItem()
            binding.rvList.visibility = View.VISIBLE
            binding.txtNoContain.visibility = View.GONE
            setUpRecycler()

        }

    }

    private fun setUpRecycler() {
        if (itemsList!!.size > 0) {
            binding.rvList.visibility = View.VISIBLE
            binding.txtNoContain.visibility = View.GONE
        }
        binding.rvList.layoutManager =
            LinearLayoutManager(this@FreshUnloadActivity, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter =
            UnloadRequestItemAdapter(this@FreshUnloadActivity, unloadType, itemsList!!)
    }


    override fun onItemsClick(position: Int, tag: String) {
        Log.e("Tag Position", position.toString())
    }

    private fun getUnloadData() {
        arrUnloadFlag = ArrayList()

        //Insert UnloadFlag
        var mUnFlag = UnloadFlag()

        mUnFlag = UnloadFlag()
        mUnFlag.unloadType = "Fresh Unload"
        mUnFlag.unloadFlag = "1"
        mUnFlag.unloadTypeCode = "1"
        arrUnloadFlag.add(mUnFlag)

    }

    /*****************************************************************************************
     * @ Creating data for print and saving it in DB for future printing
     */
    fun createPrintBadDamagedData(invNum: String?, mItemList: List<UomData>): JSONArray? {
        val jArr = JSONArray()
        try {
            val jInter = JSONArray()
            val jDict = JSONObject()
            jDict.put(AppConstants.REQUEST, AppConstants.UNLOAD_DAMAGED_REQUEST)
            val mainArr = JSONObject()
            mainArr.put("ROUTE", mPreferenceManager!!.getString(AppConstants.ROUTENAME))
            mainArr.put("DOC DATE", Utils.formatDate(Date(), "dd-MM-yyyy"))
            mainArr.put("TIME", Utils.formatTime(Date(), "hh:mm"))
            mainArr.put("SALESMAN", mPreferenceManager!!.getString(AppConstants.DRIVERNAME))
            mainArr.put(
                "SALESMANCode",
                mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.salesman_code
            )
            mainArr.put("CONTACTNO", "-")
            mainArr.put("DOCUMENT NO", invNum) //Load Summary No
            mainArr.put("TRIP START DATE", mPreferenceManager!!.getTripDate())
            mainArr.put("supervisorname", "-")
            mainArr.put("orderNo", invNum) //Invoice No

            val TOTAL = JSONArray()
            val HEADERS = JSONArray()
            HEADERS.put("Sr No")
            HEADERS.put("Description")
            HEADERS.put("Unit")
            HEADERS.put("Qty")

            mainArr.put("HEADERS", HEADERS)

            //SALES INVOICE
            val jData = JSONArray()
            for (mItemData in mItemList!!) {

                //Add Base UOM
                val data = JSONArray()
                data.put("" + (jData.length() + 1))
                data.put(mItemData.itemName) // DESC
                data.put(mItemData.uomType) // UOM
                data.put(mItemData.uomQty) //  QTY
                jData.put(data)

            }
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
    fun createPrintBadExpiryData(invNum: String?, mItemList: List<UomData>): JSONArray? {
        val jArr = JSONArray()
        try {
            val jInter = JSONArray()
            val jDict = JSONObject()
            jDict.put(AppConstants.REQUEST, AppConstants.UNLOAD_EXPIRY_REQUEST)
            val mainArr = JSONObject()
            mainArr.put("ROUTE", mPreferenceManager!!.getString(AppConstants.ROUTENAME))
            mainArr.put("DOC DATE", Utils.formatDate(Date(), "dd-MM-yyyy"))
            mainArr.put("TIME", Utils.formatTime(Date(), "hh:mm"))
            mainArr.put("SALESMAN", mPreferenceManager!!.getString(AppConstants.DRIVERNAME))
            mainArr.put(
                "SALESMANCode",
                mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.salesman_code
            )
            mainArr.put("CONTACTNO", "-")
            mainArr.put("DOCUMENT NO", invNum) //Load Summary No
            mainArr.put("TRIP START DATE", mPreferenceManager!!.getTripDate())
            mainArr.put("supervisorname", "-")
            mainArr.put("orderNo", invNum) //Invoice No

            val HEADERS = JSONArray()
            HEADERS.put("Sr No")
            HEADERS.put("Description")
            HEADERS.put("Unit")
            HEADERS.put("Qty")

            mainArr.put("HEADERS", HEADERS)

            //SALES INVOICE
            val jData = JSONArray()
            for (mItemData in mItemList!!) {

                //Add Base UOM
                val data = JSONArray()
                data.put("" + (jData.length() + 1))
                data.put(mItemData.itemName) // DESC
                data.put(mItemData.uomType) // UOM
                data.put(mItemData.uomQty) //  QTY
                jData.put(data)

            }
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
    fun createPrintUnloadData(invNum: String?): JSONArray? {

        val jArr = JSONArray()
        try {
            val jInter = JSONArray()
            val jDict = JSONObject()
            jDict.put(AppConstants.REQUEST, AppConstants.END_INVENTORY)
            val mainArr = JSONObject()
            mainArr.put("ROUTE", mPreferenceManager!!.getString(AppConstants.ROUTENAME))
            mainArr.put("DOC DATE", Utils.formatDate(Date(), "dd-MM-yyyy"))
            mainArr.put("TIME", Utils.formatTime(Date(), "hh:mm"))
            mainArr.put("SALESMAN", mPreferenceManager!!.getString(AppConstants.DRIVERNAME))
            mainArr.put(
                "SALESMANCode",
                mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.salesman_code
            )
            mainArr.put("CONTACTNO", "-")
            mainArr.put("DOCUMENT NO", invNum) //Load Summary No
            mainArr.put("TRIP START DATE", mPreferenceManager!!.getTripDate())
            mainArr.put("supervisorname", "-")
            mainArr.put("orderNo", invNum) //Invoice No

            val HEADERS = JSONArray()
            HEADERS.put("Sr No")
            HEADERS.put("Description")
            HEADERS.put("Unit")
            HEADERS.put("Load Qty")
            HEADERS.put("Sale Qty")
            HEADERS.put("Return Qty")
            HEADERS.put("Variance Qty")
            HEADERS.put("Fresh Qty")
            HEADERS.put("Unload Qty")

            mainArr.put("HEADERS", HEADERS)

            //SALES INVOICE
            val jData = JSONArray()
            for (obj in arrEndData!!) {

                //Add Base UOM
                val data = JSONArray()
                data.put("" + (jData.length() + 1))
                data.put(obj.name) // DESC
                data.put(obj.uom) // UOM
                data.put(obj.loadQty) //Load Qty
                data.put(obj.salesQty) //Sale Qty
                data.put(obj.returnQty) //UnLoadQty
                data.put(obj.variance) //Bad Qty
                data.put(obj.freshQty) //fresh Qty
                data.put(obj.unloadQty) //VR Qty
                jData.put(data)
            }
            mainArr.put("data", jData)
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

    private class MyHandler internal constructor(activity: FreshUnloadActivity) :
        Handler() {
        private val mActivity: WeakReference<FreshUnloadActivity>

        init {
            mActivity = WeakReference<FreshUnloadActivity>(activity)
        }

        override fun handleMessage(msg: Message) {
            val activity: FreshUnloadActivity? = mActivity.get()
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
                    Toast.makeText(
                        applicationContext,
                        "MSR reading failure",
                        Toast.LENGTH_SHORT
                    )
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
                this@FreshUnloadActivity,
                this@FreshUnloadActivity
            )
            `object`.execute("", jsonArray)
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
                this@FreshUnloadActivity,
                this@FreshUnloadActivity
            )
            `object`.execute("", jsonArray)

        }, 3000)
    }
}