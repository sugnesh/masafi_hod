package com.engear.vansales.activities

import android.annotation.SuppressLint
import android.app.SearchManager
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.MenuItemCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.R
import com.engear.vansales.adapter.CategoryAdapter
import com.engear.vansales.adapter.LoadItemAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.DeviceListActivity
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityLoadConfirmBinding
import com.engear.vansales.databinding.DialogInputLoadVarianceBinding
import com.engear.vansales.interfaces.OnCategoryClickListener
import com.engear.vansales.interfaces.OnConfirmationDialogClick
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.interfaces.OnSearchableDialog
import com.engear.vansales.model.*
import com.engear.vansales.printer.BluetoothPrintService
import com.engear.vansales.printer.PrintLog
import com.engear.vansales.printer.WoosimCustomService
import com.engear.vansales.utils.Utils
import com.google.android.gms.common.api.Batch
import org.json.JSONArray
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class LoadConfirmActivity : BaseActivity(), OnItemClickListener, OnCategoryClickListener {

    lateinit var binding: ActivityLoadConfirmBinding
    var allItemsList: ArrayList<ItemData.Data>? = ArrayList()
    var allLoadItem: MutableList<SalesmanLoadDetail> = mutableListOf()
    var allLoadVanItem: MutableList<SalesmanLoadDetail> = mutableListOf()
    var allLoadCategoryItem: MutableList<SalesmanLoadDetail> = mutableListOf()
    var allLoadBatchItem: MutableList<BatchdetailsList> = mutableListOf()
    var allVarienceBatchItem: MutableList<BatchdetailsList> = mutableListOf()

    var loadId: String = ""
    var loadNo: String = ""
    var loadType: String = ""
    var mAdapter: LoadItemAdapter? = null
    private var jsonArray: JSONArray? = null
    var isClick = false
    var isPeformClick: Boolean = false
    var mCatAdapter: CategoryAdapter? = null
    var categoryList: ArrayList<CategoryData>? = ArrayList()
    var lobIdList = ""


    private val REQUEST_CONNECT_DEVICE_SECURE = 1
    private val REQUEST_ENABLE_BT = 3
    private var mBluetoothAdapter: BluetoothAdapter? = null


    val MESSAGE_DEVICE_NAME = 1
    val MESSAGE_TOAST = 2
    val MESSAGE_READ = 3

    val DEVICE_NAME = "device_name"
    val TOAST = "toast"

    companion object {

        var mActivity: LoadConfirmActivity? = null

        fun callbackFunction() {
            mActivity!!.callOtherActivity()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadConfirmBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@LoadConfirmActivity,
            "On Load confirm Screen "
        )
        mActivity = this
        init()
    }

    private fun init() {
        setToolbarTitle("Load Confirm")

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        loadId = intent.getStringExtra("loadID")!!
        loadNo = intent.getStringExtra("loadNo")!!
        loadType = intent.getStringExtra("loadType")!!

        binding.btnConfirm.setOnClickListener {
            Utils.logData(
                this@LoadConfirmActivity,
                "On confirm button click"
            )

            binding.btnConfirm.visibility = View.GONE

            showConfirmAlertDialog("Are you sure you want to confirm this Load?",
                object : OnConfirmationDialogClick {
                    override fun onOk() {


                        if (!isClick) {
                            isClick = true

                            //Add Stock In VanStock
                            db!!.insertVanStock(
                                allLoadItem
                            )
                            db!!.insertLadConfirm(allLoadItem, loadNo!!)
                            db!!.updateLoadStatus(loadNo)

                            mPreferenceManager!!.setString(AppConstants.IS_LOAD_VERIFY, "1")

                            //get Print Data
                            var data: JSONObject? = null
                            try {
                                jsonArray = createPrintData(loadNo)
                                data = JSONObject()
                                data.put("data", jsonArray as JSONArray?)
                                Log.e("Print Dta",data.toString())
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }

                            val transaction = Transaction()
                            transaction.tr_type = AppConstants.LoadConfirmation_TR
                            transaction.tr_date_time =
                                Utils.getCurrentDate() + " " + Utils.getCurrentTime()
                            transaction.tr_customer_num = ""
                            transaction.tr_customer_name = ""
                            transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
                            transaction.tr_invoice_id = loadNo
                            transaction.tr_order_id = ""
                            transaction.tr_collection_id = ""
                            transaction.tr_pyament_id = ""
                            transaction.tr_is_posted = "No"
                            transaction.tr_printData = data.toString()

                            db!!.insertTransaction(transaction)
                            var salesman_route_print_enable =
                                mPreferenceManager!!.getString(AppConstants.PRINTENABLE)
                                    .toString()

                            Utils.dialogPrint(mActivity!!, object : OnSearchableDialog {

                                override fun onItemSelected(selection: String?) {

                                    if (selection.equals("yes", ignoreCase = true)) {

                                        PrintLog.mPrintService = mPrintService
                                        callDevice()

                                    } else {
                                        callOtherActivity()
                                    }
                                }
                            })

                        }
                    }

                    override fun onCancel() {
                        binding.btnConfirm.visibility - View.VISIBLE
                    }
                })

        }

        getAllLoadItem()
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
                this@LoadConfirmActivity,
                this@LoadConfirmActivity
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
                this@LoadConfirmActivity,
                this@LoadConfirmActivity
            )
            `object`.execute("", jsonArray)

        }, 3000)
    }

    private fun callOtherActivity() {
        if (db!!.checkIfLoadExists()) {
            isClick = false
            finish()
        } else {
            isClick = false
            mPreferenceManager!!.setString(AppConstants.IS_LOAD_VERIFY, "1")
            val mIntent =
                Intent(this@LoadConfirmActivity, JourneyPlanActivity::class.java)
            mIntent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(mIntent)
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_scan_search, menu)
        val color: Int
        color = resources.getColor(R.color.colorWhite)

        var drawable =
            menu!!.findItem(R.id.action_search).icon

        drawable = DrawableCompat.wrap(drawable!!)
        DrawableCompat.setTint(drawable, color)
        menu!!.findItem(R.id.action_search).icon = drawable

        val searchView: SearchView =
            MenuItemCompat.getActionView(menu!!.findItem(R.id.action_search)) as SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setIconifiedByDefault(true)
        val searchEditText =
            searchView.findViewById(androidx.appcompat.R.id.search_src_text) as EditText
        searchEditText.hint = "Search"

        searchEditText.setTextColor(color)
        searchEditText.setHintTextColor(color)

        val searchIcon =
            searchView.findViewById(androidx.appcompat.R.id.search_button) as ImageView
        searchIcon
            .setImageResource(R.drawable.ic_search)

        val searchClose =
            searchView.findViewById(androidx.appcompat.R.id.search_close_btn) as ImageView
        searchClose.imageTintList =
            ColorStateList.valueOf(color)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String?): Boolean {
                mAdapter!!.filter!!.filter(s)
                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    var item_size: Int = 0
    private fun getAllLoadItem() {

        allLoadItem = db!!.getAllLoadItem(mLoadNo = loadNo)
        allLoadBatchItem = db!!.getAllLoadBatchItem(mLoadNo = loadNo)
        item_size = allLoadItem.size
        //callAdaapter()
        setUpRecycler()

    }

//    private fun setUpCategoryRecycler() {
//        binding.rvCategory!!.layoutManager =
//            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        mCatAdapter = CategoryAdapter(this, categoryList!!)
//        binding.rvCategory!!.adapter = mCatAdapter
//        //setUpRecycler()
//        categoryList!![catPosition].isClick = "1"
//        selectCatId = categoryList!![catPosition].catId.toString()
//
//        catItemList = ArrayList()
//        catItemList = getCategoryItem(selectCatId)
//    }

    private fun setUpRecycler() {
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = LoadItemAdapter(this, allLoadItem, loadType, true)
        binding.rvList.adapter = mAdapter
    }

//    fun callAdaapter() {
//        for (mLobId in allLoadCategoryItem) {
//            if (lobIdList.isEmpty()) {
//                lobIdList = mLobId.itemId.toString()
//            } else {
//                lobIdList = lobIdList + "," + mLobId.itemId.toString()
//            }
//        }
//
//        categoryList = ArrayList()
//        categoryList = db!!.getAllLoadConfirmCategory(lobIdList)
//        itemsList = db!!.getAllLoadCategoryItem(lobIdList)
//
//        setUpCategoryRecycler()
//    }

    override fun onItemsClick(position: Int, tag: String) {
        makeDilog(this, allLoadItem[position], position)

    }

    private fun makeDilog(context: Context, item: SalesmanLoadDetail, position: Int) {
        val dialogBinding = DialogInputLoadVarianceBinding.inflate(LayoutInflater.from(context))
        val dialogView = dialogBinding.root

        val list = listOf("Variance")
        val dataAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, list).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        dialogBinding.spReason.adapter = dataAdapter
        dialogBinding.txtName.text = item.itemName
        dialogBinding.edtSaleAlter.setText(item.uomQty)

        val alertDialog = AlertDialog.Builder(context).create().apply {
            setView(dialogView)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        val mUOm = db?.getItemUOMDetail(item.itemId, item.displayUomId)

        dialogBinding.btnConfirm.setOnClickListener {
            val enteredQtyStr = dialogBinding.edtActAlter.text.toString().trim()
            val systemQty = item.uomQty.toDoubleOrNull()
            val enteredQty = enteredQtyStr.toDoubleOrNull()

            if (enteredQtyStr.isEmpty() || enteredQty == null || systemQty == null) {
                Toast.makeText(context, "Please enter a valid quantity", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (enteredQty > systemQty) {
                Toast.makeText(context, "You can not enter qty more than system Qty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val difference = systemQty - enteredQty
            val loadQty = mUOm?.uomUPC?.let {
                Utils.getUOMLowerQty(it, enteredQtyStr)
            } ?: enteredQtyStr

            val updatedItem = allLoadItem[position].apply {
                actualQty = difference.toString()
                enterQty = enteredQtyStr
                this.loadQty = loadQty
                batch = dialogBinding.spReason.selectedItem.toString()

                var varianceQty = (warehouseApproveQty.toDoubleOrNull() ?: 0.0) - enteredQty
                varianceQty = varianceQty.toDouble()
            }

            // Optional: Batch variance logic can be refactored and reused separately if needed
            /*
            val vanBatch = updatedItem.vanBatch ?: arrayListOf()
            val varianceBatch = mutableListOf<BatchdetailsList>()

            // If varianceQty > 0, allocate batch from soonest-expiring stock
            */

            // Maintain unique items in allLoadVanItem
            val index = allLoadVanItem.indexOfFirst { it.itemId == updatedItem.itemId }
            if (index != -1) {
                allLoadVanItem[index] = updatedItem
            } else {
                allLoadVanItem.add(updatedItem)
            }

            alertDialog.dismiss()
            mAdapter?.notifyDataSetChanged()
        }

        dialogBinding.btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    /*****************************************************************************************
    @ Creating data for print and saving it in DB for future printing
     ****************************************************************************************/
    fun createPrintData(orderNo: String): JSONArray {
        val jArr = JSONArray()
        try {
            var totalAmount = 0.0
            var totalPcs = 0.0
            var totalQty = 0F
            var totalRecvQty = 0F
            var totalConQty = 0F
            var totalVarQty = 0F
            val jInter = JSONArray()
            val jDict = JSONObject()
            jDict.put(MyApplication.REQUEST, MyApplication.LOAD_SUMMARY_REQUEST)
            val mainArr = JSONObject()
            var routeCode =
                mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode
            var vanName = routeCode.replace("Zone", "Vansales")
            mainArr.put("ROUTE", vanName)
            mainArr.put("DOC DATE", Utils.formatDate(Date(), "dd-MM-yyyy"))
            mainArr.put("TIME", Utils.formatTime(Date(), "hh:mm"))
            mainArr.put("SALESMAN", mPreferenceManager!!.getString(AppConstants.DRIVERNAME))
            mainArr.put("CONTACTNO", "-")
            mainArr.put("DOCUMENT NO", orderNo) //Load Summary No
            mainArr.put("TRIP START DATE", mPreferenceManager!!.getTripDate())
            mainArr.put("supervisorname", "-")
            mainArr.put("TripID", mPreferenceManager!!.getTripId())
            mainArr.put("Load Number", "0")
            val HEADERS = JSONArray()
            val TOTAL = JSONArray()
            HEADERS.put("S.NO")
            HEADERS.put("ITEM#")
            HEADERS.put("DESCRIPTION")
            HEADERS.put("RECEIVED UOM")
            HEADERS.put("LOAD_RECEIVED QTY")
            HEADERS.put("CONFIRMED QTY")
            HEADERS.put("VARIANCE QTY")

            //HEADERS.put(obj1);
            // HEADERS.put(obj2);
            mainArr.put("HEADERS", HEADERS)
            val jData = JSONArray()

            for (mItem in allLoadItem) {
                val data = JSONArray()
                data.put("" + (jData.length() + 1))
                data.put(mItem.itemCode)
                data.put(mItem.itemName)
                data.put(mItem.displayUOMName)
                var varience = "0"
                if (mItem.actualQty.isEmpty()) {
                    varience = "0"
                } else {
                    varience = mItem.actualQty
                }
                var receiveQty = (mItem.enterQty.toFloat() + varience.toFloat())
                data.put(receiveQty.toFloat())
                var mQty = mItem.enterQty.toFloat()
                data.put(mQty.toFloat())
                data.put(varience)
                jData.put(data)

                totalQty += mQty.toFloat()
                totalRecvQty += receiveQty.toFloat()
                totalVarQty += varience.toFloat()

            }

            val totalObj = JSONObject()
            //            totalObj.put("TOTAL UNITS","+" + String.format("%.2f",totalPcs));
//            totalObj.put("UNIT PRICE","");
            totalObj.put("Total Amount(AED)", String.format("%.2f", totalAmount))
            totalObj.put("Total RecvQty", "" + totalRecvQty)
            totalObj.put("Total ConQty", "" + totalQty)
            totalObj.put("Total VariQty", "" + totalVarQty)
            TOTAL.put(totalObj)
            mainArr.put("TOTAL", TOTAL)
            mainArr.put("data", jData)
            jDict.put("mainArr", mainArr)
            jInter.put(jDict)
            jArr.put(jInter)

            // jArr.put(HEADERS);
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jArr
    }

    private fun getVariance() {


        var unloadNumber =
            Utils.getLastIndexNew(
                mPreferenceManager!!.getString(AppConstants.UNLOAD_LAST).toString(), "unload"
            )

        var allVariance: MutableList<UomData> = mutableListOf()
        for (mLoadItem in allLoadVanItem) {
            if (mLoadItem.varianceQty.isNotEmpty()) {
                if (mLoadItem.varianceQty.toDouble() > 0) {

                    var diff = (mLoadItem.warehouseApproveQty.toInt() - mLoadItem.enterQty.toInt())

                    if (mLoadItem.varianceBatch!!.size > 1) {

                        for (mItemBatch in mLoadItem.varianceBatch!!) {
                            val mItem = UomData()
                            mItem.itemId = mLoadItem.itemId
                            mItem.itemName = mLoadItem.itemName
                            mItem.itemCode = mLoadItem.itemCode
                            mItem.itemCode = mLoadItem.itemCode
                            mItem.uomQty = mItemBatch.qty
                            mItem.uomType = mLoadItem.uomName
                            mItem.reason = mLoadItem.batch
                            mItem.uomId = mLoadItem.itemUom
                            /*try {
                                mItem.batchNo = mItemBatch.batchNumber
                            }catch (e:Exception){

                            }*/
                            allVariance.add(mItem)
                        }
                    } else {
                        val mItem = UomData()
                        mItem.itemId = mLoadItem.itemId
                        mItem.itemName = mLoadItem.itemName
                        mItem.itemCode = mLoadItem.itemCode
                        mItem.itemCode = mLoadItem.itemCode
                        //mItem.uomQty = mLoadItem.actualQty
                        mItem.uomQty = diff.toString()
                        mItem.uomType = mLoadItem.displayUOMName
                        mItem.reason = mLoadItem.batch
                        mItem.uomId = mLoadItem.displayUomId
                        /*  try {
                              mItem.batchNo = mLoadItem.varianceBatch!![0].batchNumber
                          }catch (e:Exception){

                          }*/
                        allVariance.add(mItem)
                    }

                }
            }
        }

        if (allVariance.size > 0) {

            db!!.insertLoadVaR(
                unloadNumber,
                "6",
                "6",
                mPreferenceManager!!.getTripId(),
                allVariance
            )

            //INSERT TRANSACTION
            val transaction = Transaction()
            transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_LOAD_VARIANCE
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
            transaction.tr_printData = ""
            db!!.insertTransaction(transaction)

            //set Order Last Number
            mPreferenceManager!!.setString(AppConstants.UNLOAD_LAST, unloadNumber)
        }

    }

    var catItemList: ArrayList<ItemData.Data>? = ArrayList()
    var itemsList: ArrayList<ItemData.Data>? = ArrayList()

    var catPosition = 0
    var selectCatId = ""
    override fun onCategoryClick(position: Int, tag: String) {
        categoryList!![position].isClick = "1"
        categoryList!![catPosition].isClick = "0"
        catPosition = position
        mCatAdapter!!.notifyDataSetChanged()
        selectCatId = categoryList!![catPosition].catId.toString()

        catItemList = ArrayList()
        catItemList = getCategoryItem(selectCatId)

    }

    val arrMItem = ArrayList<ItemData.Data>()
    private fun getCategoryItem(catId: String): ArrayList<ItemData.Data>? {
        allLoadItem.clear()

        for (allitem in allLoadCategoryItem) {
            for (i in itemsList!!) {
                if (i.majorCategory.toString() == catId) {
                    if (allitem.itemName == i.itemName) {
                        if (allLoadItem.contains(allitem)) {

                        } else {
                            arrMItem.add(i)
                            allLoadItem.add(allitem)
                        }

                    }
                }

            }
        }
        setUpRecycler()
        return arrMItem
    }


    override fun onStart() {
        super.onStart()

        // If BT is not on, request that it be enabled.
        // setupPrint() will then be called during onActivityResult
        if(mBluetoothAdapter != null)
        {
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

    private class MyHandler internal constructor(activity: LoadConfirmActivity) :
        Handler() {
        private val mActivity: WeakReference<LoadConfirmActivity>

        init {
            mActivity = WeakReference<LoadConfirmActivity>(activity)
        }

        override fun handleMessage(msg: Message) {
            val activity: LoadConfirmActivity? = mActivity.get()
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

}