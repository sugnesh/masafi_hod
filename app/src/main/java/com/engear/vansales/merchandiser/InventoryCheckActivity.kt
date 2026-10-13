package com.engear.vansales.merchandiser

import android.app.Activity
import android.app.Dialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.R
import com.engear.vansales.adapter.RowInventoryExpiryAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityInventoryCheckBinding
import com.engear.vansales.databinding.RowItemInventoryBinding
import com.engear.vansales.interfaces.OnShopItemClickListener
import com.engear.vansales.model.*
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class InventoryCheckActivity : BaseActivity(), OnShopItemClickListener {

    lateinit var binding: ActivityInventoryCheckBinding
    var brandId: String = ""
    var categoryId: String = ""
    var customerCode: String? = null
    var customerObj: CustomerData.DataCustomer? = null
    var mAdapter: InventoryAdapter? = null
    private val filterCode = 200
    var visitId: String? = null
    var activityId: String? = null
    var headbrandId: String = ""
    var headcategoryId: String = ""

    companion object {
        var allInventory: ArrayList<CustomerInventory> = ArrayList()
        var isCompleted = false
        var isView: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryCheckBinding.inflate(
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
            "On Stock IN Store Screen"
        )
        setToolbarTitle("Inventory Check")

        brandId = intent.getStringExtra("BrandId")!!
        categoryId = intent.getStringExtra("CategoryId")!!
        headbrandId = intent.getStringExtra("HeadBrandId")!!
        headcategoryId = intent.getStringExtra("HeadCategoryId")!!
        customerCode = intent.getStringExtra("customer_code")
        customerObj = db!!.getCustomerDetail(customerCode!!)

        getData()

        if (db!!.checkCustomerInventoryPosted(
                customerObj!!.userDetail.id.toString(),
                brandId,
                categoryId
            )
        ) {
            isView = true
            binding.btnSubmit.visibility = View.GONE
        } else {
            isView = false
            binding.btnSubmit.visibility = View.VISIBLE
        }

    }

    private fun getData() {
        allInventory = ArrayList()
        allInventory = db!!.getAllCustomerInventory(
            customerObj!!.userDetail!!.id.toString(),
            brandId,
            categoryId
        )
        setAdapter()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_inventory, menu)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (isView) {
                    finish()
                } else {
                    val alertDialogBuilder =
                        android.app.AlertDialog.Builder(this@InventoryCheckActivity)
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

    private fun setClickEvent() {
        binding.btnSubmit.setOnClickListener {

            if (isValid()) {
                val alertDialogBuilder =
                    android.app.AlertDialog.Builder(this@InventoryCheckActivity)
                alertDialogBuilder.setTitle("Alert")
                    .setMessage("Are you sure you want to submit this inventory?")
                    .setCancelable(false)
                    .setPositiveButton(
                        "Yes"
                    ) { dialog, which ->

                        addInventory()

                    }.setNegativeButton(
                        "No"
                    ) { dialog, which -> dialog.dismiss() }

                alertDialogBuilder.show()
            } else {
                Toast.makeText(this, "Please add inventory detail!", Toast.LENGTH_SHORT)
                    .show()
            }

        }

        binding.btnRefresh.setOnClickListener {
            getInventoryDetail()
        }

    }

    fun setAdapter() {
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = InventoryAdapter(context = this, allList = allInventory)
        binding.rvList.adapter = mAdapter
    }

    private fun isValid(): Boolean {
        for (mData in allInventory) {
            if (mData.totalFillQty != null) {
                if (mData.totalFillQty!!.isNotEmpty()) {
                    return true
                    break
                }
            }
        }
        return false
    }

    private fun addInventory() {

        var uniqId = "INVT" + Utils.getTimeStamp()!!
            .replace(":", "") + allInventory[0].inventoryId + customerObj!!.userDetail.id.toString()
        //add in databse
        db!!.insertInventoryPost(
            uniqId,
            allInventory,
            brandId,
            categoryId,
            headbrandId,
            headcategoryId
        )

        addOrder()

        val transaction = Transaction()
        transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_INVENTORY
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

        //db!!.updateCustomerTransaction(customerObj!!.userDetail!!.id.toString(), "inventory")

        isCompleted = true
//        var action = ""
//        action = "Inventory Captured"
//        var endTime = Utils.getCurrentTime()
//        db!!.updateCustomerVisitActivity(endTime, action, visitId!!, activityId!!)

        finish()

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

        for (mItem in allInventory) {

            if (mItem.reorderQty != null && mItem.reorderQty!!.isNotEmpty()) {
                if (mItem.reorderQty!!.toDouble() > 0) {

                    var mItemData =
                        db!!.getItemDetailFromId(mItem.itemId!!)

                    var mUOM = db!!.getLowerUOM(mItem.uomId!!, mItem.itemId!!)

                    if (mUOM != null) {
                        var uomDiscountAmt = 0
                        var mIGrossPrice = 0.0
                        if (mUOM != null) {
                            if (mUOM!!.uomPrice != null) {
                                mIGrossPrice =
                                    mItem.reorderQty!!.toDouble() * mUOM!!.uomPrice.toDouble()
                            } else {
                                mIGrossPrice = mItem.reorderQty!!.toDouble() * 0
                            }
                        } else {
                            mIGrossPrice = mItem.reorderQty!!.toDouble() * 0
                        }

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
                                mItem.reorderQty!!,
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

            if (itemsList!!.size > 0) {
                var orderNo =
                    Utils.getLastIndex(
                        mPreferenceManager!!.getString(AppConstants.ORDER_LAST).toString()
                    ,"order")
                var orderTypeId = if (customerObj!!.credit_days == "0") {
                    db!!.getOrderTypeId("Cash")
                } else {
                    db!!.getOrderTypeId("Credit")
                }

                val c = Calendar.getInstance().getTime()
                println("Current time => $c")
                val df = SimpleDateFormat("yyyy-MM-dd")
                val orderDate = df.format(c)

                var dueDate = ""
                if (customerObj!!.credit_days != null) {
                    dueDate =
                        Utils.addDays(
                            "yyyy-MM-dd",
                            orderDate,
                            customerObj!!.credit_days!!.toInt()
                        ,"1")!!
                } else {
                    dueDate =
                        Utils.addDays("yyyy-MM-dd", orderDate, 1,"1")!!
                }

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
                db!!.insertOrder(
                    mOrder = mOrder,
                    mItemList = itemsList!!,
                    isAccept = "1",
                    mLPO = ""
                )

                //set Order Last Number
                mPreferenceManager!!.setString(AppConstants.ORDER_LAST, orderNo)
            }
        }

    }

    private fun showDERDialog(position: Int, mItem: CustomerInventory) {

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
            layoutAdd.isEnabled = false
            layoutAdd.isClickable = false
            layoutAdd.alpha = 0.5f
        }
        layoutAdd.setOnClickListener {
            if (edtDamaged.text.toString().isEmpty() && edtExpiry.text.toString()
                    .isEmpty() && edtSaleableItem.text.toString().isEmpty()
            ) {
                Toast.makeText(
                    this@InventoryCheckActivity,
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
                allInventory[position] = mItem
                mAdapter!!.notifyDataSetChanged()
                //mAdapter!!.notifyDataSetChanged()

                Toast.makeText(
                    this@InventoryCheckActivity,
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

    private fun showExpiryDialog(position: Int, mItem: CustomerInventory) {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_expiray)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val metrics = getResources().getDisplayMetrics()
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

        var arrExpiry = mItem.arrExpiry

        btnAdd_Expry.setOnClickListener {
            layoutAdd.isEnabled = true
            layoutAdd.alpha = 1f
            frameExpiry.visibility = View.GONE
            layoutShowItem.visibility = View.VISIBLE
        }

        if (isView) {
            layoutAdd.isEnabled = false
            layoutAdd.isClickable = false
            layoutAdd.alpha = 0.5f
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
                Toast.makeText(this@InventoryCheckActivity, "Please insert Qty", Toast.LENGTH_SHORT)
                    .show()
            } else {
                var mData = HashMap<String, String>()
                mData.put("UOMId", mItem.uomId!!)
                mData.put("UOM", mItem.uomName!!)
                mData.put("Qty", edtPC.text.toString())
                mData.put("Expiry", edtExpiry.text.toString())
                arrExpiry!!.add(mData)

                mItem.arrExpiry = arrExpiry
                allInventory[position] = mItem
                mAdapter!!.notifyDataSetChanged()

                Toast.makeText(
                    this@InventoryCheckActivity,
                    "Stock added successfully!",
                    Toast.LENGTH_SHORT
                ).show()
                edtPC.setText("")
                edtExpiry.text = ""
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == filterCode) {
            if (resultCode == Activity.RESULT_OK) {

                if (data!!.getStringExtra("isClear") != null) {
                    allInventory.clear()
                    allInventory =
                        db!!.getAllCustomerInventory(
                            customerObj!!.userDetail!!.id.toString(),
                            brandId,
                            categoryId
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

                    allInventory.clear()
                    allInventory = getFilterItem(brand, category, itemGroup)
                    setAdapter()
                }
            }
        }
    }

    fun getFilterItem(
        mBrand: ArrayList<String>,
        mCategory: ArrayList<String>,
        mGroup: ArrayList<String>
    ): ArrayList<CustomerInventory> {

        var allData: ArrayList<CustomerInventory> = ArrayList()

        for (mData in allInventory) {
            if (mCategory.isNotEmpty() && mBrand.isNotEmpty() && mGroup.isNotEmpty()) {
                if (mCategory.contains(mData.itemCategory) && mBrand.contains(mData.itemBrand) && mGroup.contains(
                        mData.itemGroup
                    )
                ) {
                    allData.add(mData)
                }
            } else if (mCategory.isNotEmpty() && mBrand.isNotEmpty()) {
                if (mCategory.contains(mData.itemCategory) && mBrand.contains(mData.itemBrand)) {
                    allData.add(mData)
                }
            } else if (mCategory.isNotEmpty() && mGroup.isNotEmpty()) {
                if (mCategory.contains(mData.itemCategory) && mGroup.contains(mData.itemGroup)) {
                    allData.add(mData)
                }
            } else if (mBrand.isNotEmpty() && mGroup.isNotEmpty()) {
                if (mBrand.contains(mData.itemBrand) && mGroup.contains(mData.itemGroup)) {
                    allData.add(mData)
                }
            } else if (mGroup.isNotEmpty()) {
                if (mGroup.contains(mData.itemGroup)) {
                    allData.add(mData)
                }
            } else if (mCategory.isNotEmpty()) {
                if (mCategory.contains(mData.itemCategory)) {
                    allData.add(mData)
                }
            } else if (mBrand.isNotEmpty()) {
                if (mBrand.contains(mData.itemBrand)) {
                    allData.add(mData)
                }
            }
        }
        return allData
    }

    override fun onBackPressed() {
//        var action = ""
//        action = "Stock Captured"
//        var endTime = Utils.getCurrentTime()
//        db!!.updateCustomerVisitActivity(endTime, action, visitId!!, activityId!!)
        super.onBackPressed()
    }

    private fun getInventoryDetail() {

        showOnlyProgressDialog()

        ApiClient.getClient(this).getCustomerInventory(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN,
            mPreferenceManager!!.getUserId()!!.toInt()
        ).enqueue(object : Callback<InventoryData> {
            override fun onResponse(
                call: Call<InventoryData>,
                response: Response<InventoryData>
            ) {
                if (response.isSuccessful) {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                    if (response.body()!!.status!!) {
                        db!!.insertCustomerInventory(response.body()!!.data)
                    }
                }

                dismissDialog()
                getData()

            }

            override fun onFailure(call: Call<InventoryData>, error: Throwable) {
                //Utils.logData(this@DownloadingDataActivity, "Order Type Failed")
                dismissDialog()
            }
        })
    }

    override fun onShopItemClick(position: Int, tag: String, type: String) {

        if (tag == "DER") {
            var mData = allInventory[position]
            showDERDialog(position, mData)
        } else {
            var mData = allInventory[position]
            mData.totalFillQty = tag
            var mId = db!!.getUOMId(type)
            mData.uomId = mId
            mData.uomName = type
            allInventory[position] = mData
            mAdapter!!.notifyDataSetChanged()
            showExpiryDialog(position, allInventory[position])
        }
    }

    class InventoryAdapter(
        var context: Context,
        var allList: ArrayList<CustomerInventory>
    ) : RecyclerView.Adapter<InventoryAdapter.ViewHolder>(), Filterable {

        //private var contactListFiltered: MutableList<CustomerInventory> = allInventory

        val onShopItemClickListener = context as OnShopItemClickListener

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding =
                RowItemInventoryBinding.inflate(LayoutInflater.from(context), parent, false)
            return ViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return allInventory.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

//            val mData = allInventory[holder.adapterPosition]
            holder.binding.item = allInventory[holder.adapterPosition]
//        val dataAdapter = mData.mUOMList.let {
//            ArrayAdapter<String>(
//                context,
//                R.layout.simple_spinner_item, it
//            )
//        }
//        dataAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // holder.binding.edtUom.adapter = dataAdapter
            //var index = mData.mUOMList.indexOf(mData.uomName!!)
            holder.binding.edtUom.text = allInventory[holder.adapterPosition].uomName!!
            //holder.binding.edtUom.setSelection(index)

            if (isView) {
                holder.binding.chkOutStock!!.isClickable = false
                holder.binding.edtCapacity!!.isClickable = false
                holder.binding.edtCapacity!!.isEnabled = false
                holder.binding.edtSaleable!!.isClickable = false
                holder.binding.edtSaleable!!.isEnabled = false
                holder.binding.edtRefillQty!!.isClickable = false
                holder.binding.edtRefillQty!!.isEnabled = false
            }

            holder.binding.chkOutStock!!.isChecked =
                allInventory[holder.adapterPosition].outOfStock == "1"
            holder.binding.chkOutStock!!.setOnCheckedChangeListener { buttonView, isChecked ->
                if (buttonView.isPressed) {
                    if (isChecked) {
                        allInventory[holder.adapterPosition].totalFillQty = "0"
                        allInventory[holder.adapterPosition].outOfStock = "1"
                        //  var index = allInventory.indexOf(contactListFiltered[holder.adapterPosition])
                        allInventory[holder.adapterPosition] = allInventory[holder.adapterPosition]
                        holder.binding.edtSaleable.setText("0")
                        //  allInventory[index] = mData
                    } else {
                        allInventory[holder.adapterPosition].totalFillQty = "0"
                        allInventory[holder.adapterPosition].outOfStock = "0"
                        // var index = allInventory.indexOf(contactListFiltered[holder.adapterPosition])
                        allInventory[holder.adapterPosition] = allInventory[holder.adapterPosition]
                        holder.binding.edtSaleable.setText("")
                        //  allInventory[index] = mData
                    }
                }
            }

            holder.binding.edtCapacity!!.addTextChangedListener(
                object : TextWatcher {

                    override fun afterTextChanged(s: Editable) {
                        if (s.toString().isNotEmpty()) {
                            allInventory[holder.adapterPosition].capacity = s.toString()
                            //var index = allInventory.indexOf(contactListFiltered[holder.adapterPosition])
                            allInventory[holder.adapterPosition] =
                                allInventory[holder.adapterPosition]
                            //  allInventory[index] = mData
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
                        try {

                            var modQty = allInventory[holder.adapterPosition].capacity!!.toDouble()

                            if (s.toString().isNotEmpty()) {
                                if (s.toString() == ".") {
                                    s.clear()
                                    Toast.makeText(
                                        context,
                                        " '.' not allow at starting character",
                                        Toast.LENGTH_SHORT
                                    ).show();
                                    var mFill = ""

                                    allInventory[holder.adapterPosition].totalFillQty = ""
                                    allInventory[holder.adapterPosition].reorderQty = mFill
//                                    allInventory[holder.adapterPosition] = mData
                                    holder.binding.edtReOrder!!.text = mFill.toString()
                                } else {
                                    if (s.toString().toDouble() > modQty) {
                                        s.clear()
                                        Toast.makeText(
                                            context,
                                            "You can not enter qty more than model stock",
                                            Toast.LENGTH_SHORT
                                        ).show();
                                        var mFill = ""

                                        allInventory[holder.adapterPosition].totalFillQty = ""
                                        allInventory[holder.adapterPosition].reorderQty = mFill
//                                    allInventory[holder.adapterPosition] = mData
                                        holder.binding.edtReOrder!!.text = mFill.toString()
                                    } else {
                                        //var index = allItem.indexOf(mData)
                                        var mFill =
                                            (modQty + holder.binding.edtRefillQty!!.text.toString()
                                                .toInt()) - s.toString().toDouble()

                                        allInventory[holder.adapterPosition].totalFillQty =
                                            s.toString()
                                        allInventory[holder.adapterPosition].reorderQty =
                                            mFill.toString()
//                                    allInventory[holder.adapterPosition] = mData
                                        holder.binding.edtReOrder!!.text = mFill.toString()
                                        // allInventory[index] = mData
                                    }
                                }
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
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

            holder.binding.edtRefillQty!!.addTextChangedListener(
                object : TextWatcher {

                    override fun afterTextChanged(s: Editable) {
                        try {

                            var modQty = allInventory[holder.adapterPosition].capacity!!.toDouble()

                            if (s.toString().isNotEmpty()) {
                                if (s.toString().toDouble() > modQty) {

                                } else {
                                    //var index = allItem.indexOf(mData)
                                    var salable = 0.0
                                    if (holder.binding.edtSaleable.text.toString().isNotEmpty()) {
                                        salable =
                                            holder.binding.edtSaleable.text.toString().toDouble()
                                    }
                                    var mFill = (modQty + s.toString().toDouble()
                                        .toInt()) - salable
                                    allInventory[holder.adapterPosition].refillQty = s.toString()
                                    allInventory[holder.adapterPosition].reorderQty =
                                        mFill.toString()
                                    allInventory[holder.adapterPosition].fillQty = s.toString()
                                    //var index = allInventory.indexOf(contactListFiltered[holder.adapterPosition])

                                    holder.binding.edtReOrder!!.text = mFill.toString()
                                    holder.binding.edtFiller!!.text = s.toString()
                                    // allInventory[index] = mData
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
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


            holder.binding.executePendingBindings()

            holder.binding.lytUom!!.setOnClickListener {

            }

            holder.binding.layoutExeDt.setOnClickListener {
                onShopItemClickListener.onShopItemClick(
                    position,
                    holder.binding.edtSaleable.text.toString(),
                    holder.binding.edtUom.text.toString()
                )
            }

            holder.binding.layoutDER!!.setOnClickListener {
                onShopItemClickListener.onShopItemClick(
                    position,
                    "DER",
                    holder.binding.edtUom.text.toString()
                )
            }

        }


        inner class ViewHolder(val binding: RowItemInventoryBinding) :
            RecyclerView.ViewHolder(binding.root) {
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(charSequence: CharSequence): FilterResults {
                    val charString = charSequence.toString()
                    if (charString.isEmpty()) {
                        allInventory = allList
                    } else {
                        val filteredList: MutableList<CustomerInventory> =
                            java.util.ArrayList<CustomerInventory>()
                        for (row in allList) {

                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match
                            if (row.itemName!!.toLowerCase().contains(charString.toLowerCase())
                                || row.itemCode!!.toLowerCase().contains(charString.toLowerCase())
                            ) {
                                filteredList.add(row)
                            }
                        }
                        allInventory = filteredList as ArrayList<CustomerInventory>
                    }
                    val filterResults = FilterResults()
                    filterResults.values = allInventory
                    return filterResults
                }

                override fun publishResults(
                    charSequence: CharSequence,
                    filterResults: FilterResults
                ) {
                    allInventory = filterResults.values as ArrayList<CustomerInventory>
                    notifyDataSetChanged()
                }
            }
        }
    }
}