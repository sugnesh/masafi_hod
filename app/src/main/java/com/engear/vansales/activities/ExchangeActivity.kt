package com.engear.vansales.activities

import android.R
import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.CategoryAdapter
import com.engear.vansales.adapter.OrderRequestItemAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityExchangeBinding
import com.engear.vansales.interfaces.OnCategoryClickListener
import com.engear.vansales.interfaces.OnConfirmationDialogClick
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.*
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList

class ExchangeActivity : BaseActivity(), OnItemClickListener, OnCategoryClickListener {

    lateinit var binding: ActivityExchangeBinding
    var customerCode: String? = null
    var allItemsList: ArrayList<ItemData.Data>? = ArrayList()
    var saleItemsList: ArrayList<ItemData.Data>? = ArrayList()
    var allExchangeItemsList: ArrayList<ItemData.Data>? = ArrayList()
    var exchangeItemsList: ArrayList<ItemData.Data>? = ArrayList()
    var itemPosition = -1
    private val intentCode = 100
    private val filterCode = 200
    var mAdapter: OrderRequestItemAdapter? = null
    var catItemList: ArrayList<ItemData.Data>? = ArrayList()
    var categoryList: ArrayList<CategoryData>? = ArrayList()
    var mCatAdapter: CategoryAdapter? = null
    var customerObj: CustomerData.DataCustomer? = null
    var visitId: String? = null
    var activityId: String? = null
    var isCustPromotion = false
    var promoOfferData: PromoOfferData? = null
    var arrPromoOfferData: ArrayList<PromoOfferData> = ArrayList()
    var orderType = ""
    var offerType = ""
    var customerId = ""
    var lobCode = ""
    var catPosition = 0
    var selectCatId = ""
    var selectedType = "Exchange"
    var lobIdList = ""
    var exchangeAmt = 0F
    var saleAmt = 0F
    var invoiceList: List<InvoiceData.Invoices> = ArrayList()
    var spInvList: MutableList<String>? = ArrayList()
    var spInvIdList: MutableList<String>? = ArrayList()
    var spInvAmtList: MutableList<String>? = ArrayList()
    var invoiceId: String = ""
    var invoiceNo: String = ""
    var orderNo: String = ""
    var saleNo: String = ""
    var invoiceAmt: String = "0.0"
    var balance = 0F
    var orderTypeId: String? = ""
    var orderDate: String = Utils.getCurrentTimeInPattern("yyyy-MM-dd")
    var dueDate: String = Utils.getCurrentTimeInPattern("yyyy-MM-dd")
    var mExchanNo = ""
    var offerQty = "0"
    var offerUOMId = ""
    var isRefType = "0"
    var isReSaleCust = false
    private var jsonArray: JSONArray? = null

    companion object {
        var isExchangeCreate = false

        class MyAsyncTask internal constructor(context: ExchangeActivity) :
            AsyncTask<Int, String, String?>() {

            private var resp: String? = null
            private val activityReference: WeakReference<ExchangeActivity> =
                WeakReference(context)

            override fun onPreExecute() {
                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return
                activity.showOnlyProgressDialog()
            }

            override fun doInBackground(vararg params: Int?): String? {
                val activity = activityReference.get()
                if (activity!!.lobIdList.isEmpty()) {
                    activity!!.allExchangeItemsList = activity!!.db!!.getAllItem()
                } else {
                    activity!!.allExchangeItemsList =
                        activity!!.db!!.getAllSalesmanItem(activity!!.lobIdList)
                }

                return resp
            }


            override fun onPostExecute(result: String?) {

                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return

                activity.dismissDialog()
                activity.setExchangeAdapter()
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExchangeBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@ExchangeActivity,
            "On Exchange Screen "
        )
        init()
    }

    private fun init() {
        setToolbarTitle("Exchange")

        customerCode = intent.getStringExtra("customer_code")
        customerObj = db!!.getCustomerDetail(customerCode!!)
        customerId = customerObj!!.userDetail.id!!.toString()

        if (intent.getStringExtra("lob_code") != null) {
            lobCode = intent.getStringExtra("lob_code")!!
        }

        if (intent.getStringExtra("reType") != null) {
            isRefType = intent.getStringExtra("reType")!!
        }

        isReSaleCust = db!!.isCustReSalePrice(customerId!!)


        var salesLOB: List<UserData.Data.UserInfo.SalesmanInfo.SalesmanLob>? = null
        if (mPreferenceManager!!.getUserInfo() != null) {
            if (mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.lobList != null) {
                salesLOB = mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.lobList

                for (mLobId in salesLOB) {
                    if (lobIdList.isEmpty()) {
                        lobIdList = mLobId.lob_id.toString()
                    } else {
                        lobIdList = lobIdList + "," + mLobId.lob_id.toString()
                    }
                }
            }
        }

        if (customerCode != null) {
            customerObj = db!!.getCustomerDetail(customerCode!!)
            if (!lobCode.isNullOrEmpty()) {
                var lobobj = db!!.getCustomerLob(lobCode, customerObj!!.user_id.toString())
                customerObj!!.credit_days = lobobj!!.credit_days.toString()
                customerObj!!.credit_limit = lobobj!!.credit_limit
                customerObj!!.payment_term_id = lobobj.payment_term_id!!
                if (lobobj!!.customer_type_id.toString() == "2") {
                    customerObj!!.credit_days = "0"
                }
            }
            customerId = customerObj!!.id.toString()
        }

        if (customerObj!!.credit_days == "0") {
            orderTypeId = "1"
        } else {
            orderTypeId = "2"
        }

        dueDate =
            Utils.addDays("yyyy-MM-dd", orderDate, customerObj!!.credit_days!!.toInt(), "1")!!

        var isCUstPromo = db!!.isPromotoCUst(customerId!!)
        if (isCUstPromo) {
            isCustPromotion = true
            arrPromoOfferData = db!!.getCustomerPromotion(customerId!!)
        } else {

            if (lobCode.isNotEmpty()) {
                var mLobDetail =
                    db!!.getCustomerLob(lobCode, customerObj!!.user_id.toString())

                if (customerObj!!.credit_days == "0") {
                    orderTypeId = "1"
                } else {
                    orderTypeId = "2"
                }

                var isOtherPromo = db!!.isOtherPromoPDP(
                    if (mLobDetail!!.region_id != null) mLobDetail!!.region_id.toString()
                    else "",
                    if (mLobDetail!!.sales_organisation_id != null) mLobDetail!!.sales_organisation_id.toString()
                    else "",
                    if (mLobDetail!!.channel_id != null) mLobDetail!!.channel_id.toString()
                    else "",
                    if (mLobDetail!!.customer_category_id != null) mLobDetail!!.customer_category_id.toString()
                    else "",
                    if (mLobDetail!!.route_id != null) mLobDetail!!.route_id.toString()
                    else "",
                    "",""
                )
                if (isOtherPromo) {
                    isCustPromotion = true
                    arrPromoOfferData = db!!.getAllOtherPromotion(
                        if (mLobDetail!!.region_id != null) mLobDetail!!.region_id.toString()
                        else "",
                        if (mLobDetail!!.sales_organisation_id != null) mLobDetail!!.sales_organisation_id.toString()
                        else "",
                        if (mLobDetail!!.channel_id != null) mLobDetail!!.channel_id.toString()
                        else "",
                        if (mLobDetail!!.customer_category_id != null) mLobDetail!!.customer_category_id.toString()
                        else "",
                        if (mLobDetail!!.route_id != null) mLobDetail!!.route_id.toString()
                        else "",
                        "","",""
                    )
//                    orderType = promoOfferData!!.orderType.toString()
//                    offerType = promoOfferData!!.orderType.toString()
                }

            } else {

                if (customerObj!!.credit_days == "0") {
                    orderTypeId = "1"
                } else {
                    orderTypeId = "2"
                }

                var isOtherPromo = db!!.isOtherPromoPDP(
                    if (customerObj!!.region_id != null) customerObj!!.region_id.toString()
                    else "",
                    if (customerObj!!.sales_organisation_id != null) customerObj!!.sales_organisation_id.toString()
                    else "",
                    if (customerObj!!.channel_id != null) customerObj!!.channel_id.toString()
                    else "",
                    if (customerObj!!.customer_category_id != null) customerObj!!.customer_category_id.toString()
                    else "",
                    if (customerObj!!.route_id != null) customerObj!!.route_id.toString()
                    else "","",""
                )
                if (isOtherPromo) {
                    isCustPromotion = true
                    arrPromoOfferData = db!!.getAllOtherPromotion(
                        if (customerObj!!.region_id != null) customerObj!!.region_id.toString()
                        else "",
                        if (customerObj!!.sales_organisation_id != null) customerObj!!.sales_organisation_id.toString()
                        else "",
                        if (customerObj!!.channel_id != null) customerObj!!.channel_id.toString()
                        else "",
                        if (customerObj!!.customer_category_id != null) customerObj!!.customer_category_id.toString()
                        else "",
                        if (customerObj!!.route_id != null) customerObj!!.route_id.toString()
                        else "","","",""
                    )
//                    orderType = promoOfferData!!.orderType.toString()
//                    offerType = promoOfferData!!.orderType.toString()
                }
            }
        }

        if (selectedType == "Exchange") {

            if (isRefType == "1") {
                binding.llInvoice.visibility = View.GONE

                binding.lytCategory.visibility = View.GONE

                val task = MyAsyncTask(this@ExchangeActivity)
                task.execute(10)

            } else {
                binding.llInvoice.visibility = View.VISIBLE
                getOutstandingInvoice()
                binding.lytCategory.visibility = View.GONE

                catItemList = ArrayList()
                catItemList = allExchangeItemsList
                setUpRecycler()
            }
        } else {
            binding.llInvoice.visibility = View.GONE
            binding.lytCategory.visibility = View.GONE
            if (isReSaleCust) {
                allItemsList = db!!.getAllReSaleItem()
            } else {
                allItemsList = db!!.getAllFreshSaleItem()
            }
            setUpRecycler()
        }

        setActivity()

        binding.fabNext.setOnClickListener {
            Utils.logData(
                this@ExchangeActivity,
                "On Next Fab click "
            )
            if (exchangeAmt == 0F) {
                Utils.displayAlert(this@ExchangeActivity, "Please select any item!")
            } else {
                var isValid = true
                if (isRefType == "1") {
                    isValid = isValidExchange()
                } else {
                    isValid = isValidReExchange()
                }

                if (isValid) {
                    setToolbarTitle("Sale")
                    if (isRefType == "1") {
                    } else {
                        binding.llInvoice.visibility = View.GONE
                    }
                    binding.lytCategory.visibility = View.GONE
                    selectedType = "Sale"
                    binding.fabPrevious.visibility = View.VISIBLE
                    binding.fab.visibility = View.VISIBLE
                    binding.fabNext.visibility = View.GONE
//                    if (isReSaleCust) {
//                        allItemsList = db!!.getAllReSaleItem()
//                    } else {
//                        allItemsList = db!!.getAllSaleItem()
//                    }
                    setUpRecycler()
                    binding.titleAmount.text = "Balance"
                    balance = exchangeAmt
                    binding.tvAmount.setText(
                        Utils.getCurrency(this) + " " + Utils.getCurrencyFormate(
                            this,
                            balance.toFloat()
                        )
                    )
                }
            }
        }

        binding.fabPrevious.setOnClickListener {
            Utils.logData(
                this@ExchangeActivity,
                "On Previous fab click "
            )
            setToolbarTitle("Exchange")
            selectedType = "Exchange"
            if (isRefType == "1") {
            } else {
                binding.llInvoice.visibility = View.GONE
            }
            binding.lytCategory.visibility = View.GONE
            catItemList = ArrayList()
            catItemList = allExchangeItemsList

            setUpRecycler()
            binding.fab.visibility = View.GONE
            binding.fabNext.visibility = View.VISIBLE
            binding.fabPrevious.visibility = View.GONE
            binding.titleAmount.text = "Total Exchange"
            binding.tvAmount.setText(
                Utils.getCurrency(this) + " " + Utils.getCurrencyFormate(
                    this,
                    exchangeAmt.toFloat()
                )
            )
        }

        binding.fab.setOnClickListener {
            Utils.logData(
                this@ExchangeActivity,
                "On proceed fab click "
            )
            if (saleItemsList!!.size > 0) {

                if (isValid()) {
                    showConfirmAlertDialog("Are you sure you want to proceed?",
                        object : OnConfirmationDialogClick {
                            override fun onOk() {
                                generateExchange()
                            }

                            override fun onCancel() {
                            }
                        })
                }
            } else {
                Utils.displayAlert(this, "Please select sale items!")
            }

        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(com.engear.vansales.R.menu.menu_exchange, menu)
        val color: Int
        color = resources.getColor(com.engear.vansales.R.color.colorWhite)

        /* var drawable =
             menu!!.findItem(R.id.action_search).icon

         drawable = DrawableCompat.wrap(drawable)
         DrawableCompat.setTint(drawable, color)
         menu!!.findItem(R.id.action_search).icon = drawable*/

        val searchView: SearchView =
            MenuItemCompat.getActionView(menu!!.findItem(com.engear.vansales.R.id.action_search)) as SearchView
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
            .setImageResource(androidx.appcompat.R.drawable.abc_ic_menu_share_mtrl_alpha)

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
                finish()
                return true
            }
        }
        return false
    }


    fun setExchangeAdapter() {
        catItemList = ArrayList()
        catItemList = allExchangeItemsList
        setUpRecycler()
    }

    private fun isValidExchange(): Boolean {

        var vanStockItem: ArrayList<ItemData.Data>
        if (isReSaleCust) {
            vanStockItem = db!!.getAllReSaleItem()
        } else {
            vanStockItem = db!!.getAllFreshSaleItem()
        }
        allItemsList = ArrayList()
        for (i in exchangeItemsList!!) {
            for (van in vanStockItem) {
                if (van.itemCode == i.itemCode) {
                    van.itemUOM = i.itemUOM
                    allItemsList!!.add(van)
                }
            }
        }

        if (allItemsList!!.size != exchangeItemsList!!.size) {
            Toast.makeText(
                this,
                "Exchange item is not available in vanstock",
                Toast.LENGTH_SHORT
            )
                .show()
            return false
        }

        return true
    }

    private fun isValidReExchange(): Boolean {

        var vanStockItem: ArrayList<ItemData.Data>
        if (isReSaleCust) {
            vanStockItem = db!!.getAllReSaleItem()
        } else {
            vanStockItem = db!!.getAllFreshSaleItem()
        }
        allItemsList = ArrayList()
        for (i in exchangeItemsList!!) {
            for (van in vanStockItem) {
                if (van.itemCode == i.itemCode) {
                    van.itemUOM = i.itemUOM
                    allItemsList!!.add(van)
                }
            }
        }

        if (allItemsList!!.size != exchangeItemsList!!.size) {
            Toast.makeText(
                this,
                "Exchange item is not available in vanstock",
                Toast.LENGTH_SHORT
            )
                .show()
            return false
        } else {
            return true
        }
    }

    private fun isValid(): Boolean {

        var mExcAmt = 0.0
        var mSAIAmt = 0.0
        var grossAmt = 0.0
        var discountAmt = 0.0
        var exciseAmt = 0.0
        var netAmt = 0.0
        var vatAmt = 0.0
        var totalAmt = 0.0
        for (i in exchangeItemsList!!) {
            for (j in i.itemSelectUOM!!) {
                grossAmt = grossAmt.plus(j!!.grossPrice.toFloat())
                discountAmt = discountAmt.plus(j!!.discountAmt.toFloat())
                netAmt = netAmt.plus(j!!.netAmt.toFloat())
                vatAmt = vatAmt.plus(j!!.vatAmt.toFloat())
                totalAmt = totalAmt.plus(j!!.totalAmt.toFloat())
                mExcAmt += totalAmt
            }
        }

        var grossSIAmt = 0.0
        var discountSIAmt = 0.0
        var netSIAmt = 0.0
        var vatSIAmt = 0.0
        var totalSIAmt = 0.0
        for (i in saleItemsList!!) {
            for (j in i.itemSelectUOM!!) {
                grossSIAmt = grossSIAmt.plus(j!!.grossPrice.toFloat())
                discountSIAmt = discountSIAmt.plus(j!!.discountAmt.toFloat())
                netSIAmt = netSIAmt.plus(j!!.netAmt.toFloat())
                vatSIAmt = vatSIAmt.plus(j!!.vatAmt.toFloat())
                totalSIAmt = totalSIAmt.plus(j!!.totalAmt.toFloat())
                mSAIAmt += totalSIAmt
            }
        }

        if (balance != 0F) {
            Toast.makeText(
                this,
                "Sales amount not be less then exchange amount",
                Toast.LENGTH_SHORT
            )
                .show()
            return false
        }

        return true
    }

    private fun generateExchange() {

        showOnlyProgressDialog()

        mExchanNo = Utils.getLastIndexNew(
            mPreferenceManager!!.getString(AppConstants.EXCHANGE_LAST).toString(), "Exchange"
        )

        if (orderNo.isEmpty())
            orderNo =
                Utils.getLastIndexNew(
                    mPreferenceManager!!.getString(AppConstants.RETURN_LAST).toString(), "return"
                )

        var retAmt = 0.0
        var grossAmt = 0.0
        var discountAmt = 0.0
        var exciseAmt = 0.0
        var netAmt = 0.0
        var vatAmt = 0.0
        var totalAmt = 0.0
        var totalUomList: MutableList<UomData>? = ArrayList()

        for (i in exchangeItemsList!!) {
            for (j in i.itemSelectUOM!!) {
                var grossPrice = Utils.twoDigit((j!!.uomQty.toFloat() * j!!.uomPrice.toFloat()))
                var excisePrice = Utils.twoDigit((j!!.uomQty.toFloat() * j!!.item_excise.toFloat()))
                grossAmt = grossAmt.plus(Utils.twoDigit(grossPrice))
                exciseAmt = exciseAmt.plus(Utils.twoDigit(excisePrice))
                discountAmt = discountAmt.plus(j!!.discountAmt.toFloat())
                var mNetAmt = grossPrice - Utils.twoDigit(j!!.discountAmt.toFloat())
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

        retAmt = totalAmt

        if (isRefType == "1") {
            invoiceId = ""
            invoiceAmt = ""
            invoiceNo = ""
        }

        var mOrder = Order()
        mOrder.orderNo = orderNo
        mOrder.orderType = "goodReturn"
        mOrder.orderDate = orderDate
        mOrder.invoiceId = invoiceId
        mOrder.reason = exchangeItemsList!![0].itemSelectUOM!![0].reason
        mOrder.customerId = customerCode.toString()
        mOrder.gross = Utils.twoDigit(grossAmt.toFloat()).toString()
        mOrder.discount = Utils.twoDigit(discountAmt.toFloat()).toString()
        mOrder.net = Utils.twoDigit(netAmt.toFloat()).toString()
        mOrder.vat = Utils.twoDigit(vatAmt.toFloat()).toString()
        mOrder.excAmt = Utils.twoDigit(exciseAmt.toFloat()).toString()
        mOrder.total = Utils.twoDigit(totalAmt.toFloat()).toString()
        mOrder.comment = "Exchange"
        mOrder.lob_id = lobCode
        mOrder.isExchange = "1"
        mOrder.exchangeNo = mExchanNo
        mOrder.payemntId = customerObj!!.payment_term_id.toString()

        if (isRefType == "1") {
            mOrder.invoiceNo = invoiceNo
            mOrder.invoiceAmt = invoiceAmt
        } else {
            mOrder.invoiceNo = ""
            mOrder.invoiceAmt = ""
        }

        //Insert Return
        db!!.insertReturn(
            mOrder = mOrder,
            mItemList = exchangeItemsList!!,
            isAccept = "0",
            isWithOutRef = isRefType
        )

        //set Order Last Number
        mPreferenceManager!!.setString(AppConstants.RETURN_LAST, orderNo)


        for (mItemData in exchangeItemsList!!) {

            for (mUOM in mItemData.itemSelectUOM) {

                if (mItemData.itemWeight == "1") {
                    if (db!!.checkVanItemReSaleExist(mUOM.itemId)) {
                        db!!.updateGoodReSalesItemVanStock(mUOM)
                    } else {
                        db!!.insertGoodItemVanStock(mUOM, "1")
                    }
                } else {
                    if (db!!.checkVanItemExist(mUOM.itemId)) {
                        db!!.updateGoodItemVanStock(mUOM)
                    } else {
                        db!!.insertGoodItemVanStock(mUOM, "0")
                    }
                }
            }
        }

        //get Print Data
        var data: JSONObject? = null
        try {
            jsonArray = createPrintReturnData(orderNo)
            data = JSONObject()
            data.put("data", jsonArray as JSONArray?)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        var transaction = Transaction()
        transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_RETURN_CREATED
        transaction.tr_date_time = Utils.getCurrentDate() + " " + Utils.getCurrentTime()
        transaction.tr_customer_num = customerCode
        transaction.tr_customer_name =
            customerObj!!.userDetail!!.firstname + " " + customerObj!!.userDetail!!.lastname
        transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
        transaction.tr_invoice_id = ""
        transaction.tr_order_id = orderNo
        transaction.tr_collection_id = ""
        transaction.tr_pyament_id = ""
        transaction.tr_printData = data.toString()
        transaction.tr_is_posted = "No"
        db!!.insertTransaction(transaction)

        db!!.updateCustomerTransaction(customerObj!!.userDetail!!.id.toString(), "return")

        saleNo =
            Utils.getLastIndex(
                mPreferenceManager!!.getString(AppConstants.INVOICE_LAST).toString(),
                "invoice"
            )

        var grossSIAmt = 0.0
        var discountSIAmt = 0.0
        var netSIAmt = 0.0
        var vatSIAmt = 0.0
        var exciseSIAmt = 0.0
        var totalSIAmt = 0.0
        var totalSIUomList: MutableList<UomData>? = ArrayList()

        for (i in saleItemsList!!) {
            for (j in i.itemSelectUOM!!) {
                var grossPrice = Utils.twoDigit((j!!.uomQty.toFloat() * j!!.uomPrice.toFloat()))
                var excisePrice = Utils.twoDigit((j!!.uomQty.toFloat() * j!!.item_excise.toFloat()))
                grossSIAmt = grossSIAmt.plus(Utils.twoDigit(grossPrice))
                exciseSIAmt = exciseSIAmt.plus(Utils.twoDigit(excisePrice))

                discountSIAmt = discountSIAmt.plus(j!!.discountAmt.toFloat())

                var mNetAmt = grossPrice - Utils.twoDigit(j!!.discountAmt.toFloat())
                netSIAmt = netSIAmt.plus(Utils.twoDigit(mNetAmt))
                var mVat =
                    Utils.getVatAmt((mNetAmt.toDouble() + excisePrice.toDouble()), 5.0).toFloat()
                vatSIAmt = vatSIAmt.plus(Utils.twoDigit(mVat))
                var mTotal =
                    Utils.twoDigit(mNetAmt) + Utils.twoDigit(mVat) + Utils.twoDigit(excisePrice)
                totalSIAmt = totalSIAmt.plus(Utils.twoDigit(mTotal))

                totalSIUomList!!.add(j)
            }
        }

        var mOrderInv = Invoice()
        mOrderInv.invoiceNo = saleNo
        mOrderInv.orderType = orderTypeId
        mOrderInv.invoiceDate = orderDate
        mOrderInv.invoiceType = orderTypeId
        mOrderInv.deliveryId = ""
        mOrderInv.orderId = ""
        mOrderInv.dueDate = dueDate
        mOrderInv.customerId = customerCode.toString()
        mOrderInv.gross = Utils.twoDigit(grossSIAmt.toFloat()).toString()
        mOrderInv.discount = Utils.twoDigit(discountSIAmt.toFloat()).toString()
        mOrderInv.net = Utils.twoDigit(netSIAmt.toFloat()).toString()
        mOrderInv.vat = Utils.twoDigit(vatSIAmt.toFloat()).toString()
        mOrderInv.excAmt = Utils.twoDigit(exciseSIAmt.toFloat()).toString()
        mOrderInv.total = Utils.twoDigit(totalSIAmt.toFloat()).toString()
        mOrderInv.comment = "Exchange"
        mOrderInv.isExchange = "1"
        mOrderInv.exchangeNo = mExchanNo
        mOrderInv.payemntId = customerObj!!.payment_term_id.toString()
        mOrderInv.lobId = lobCode
        mOrderInv.paidAmount = "0.0"

        //Insert Sales
        db!!.insertInvoice(
            mOrder = mOrderInv, mItemList = saleItemsList!!, mLPO = "")

        //update VanSTock Qty
        for (mData in saleItemsList!!) {
            var mQty = 0.0
            for (mUOM in mData.itemSelectUOM) {
                mQty += Utils.getUOMLowerQty(mUOM.uomUPC, mUOM.uomQty).toDouble()
            }

            var existQty =
                db!!.getVanStockExistItemQty(mData.id.toString(), mData.vanUOM, "Remaining")
                    .toDouble()

            var mUpdateQty = existQty - mQty

            db!!.updateVanStockQty(mData.id.toString(), mData.vanUOM, mUpdateQty.toString())
        }

        //set Invoice Last Number
        mPreferenceManager!!.setString(AppConstants.INVOICE_LAST, saleNo)

        //get Print Data
        try {
            jsonArray = JSONArray()
            jsonArray = createPrintInvoiceData(saleNo)
            data = JSONObject()
            data.put("data", jsonArray as JSONArray?)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        transaction = Transaction()
        transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_SALES_CREATED
        transaction.tr_date_time = Utils.getCurrentDate() + " " + Utils.getCurrentTime()
        transaction.tr_customer_num = customerCode
        transaction.tr_customer_name =
            customerObj!!.userDetail!!.firstname + " " + customerObj!!.userDetail!!.lastname
        transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
        transaction.tr_invoice_id = saleNo
        transaction.tr_order_id = ""
        transaction.tr_collection_id = ""
        transaction.tr_pyament_id = ""
        transaction.tr_printData = data.toString()
        transaction.tr_is_posted = "No"
        db!!.insertTransaction(transaction)

        db!!.updateCustomerTransaction(customerObj!!.userDetail!!.id.toString(), "sale")

        //Add Collection
        var collNo =
            Utils.getLastIndex(
                mPreferenceManager!!.getString(AppConstants.COLLECTION_LAST).toString(),
                "collection"
            )

        var cashType = "0"
        if (customerObj!!.credit_days == "0") {
            cashType = "1"
        } else {
            cashType = "0"
        }

        db!!.addCollection(
            saleNo,
            collNo,
            customerObj!!.userDetail.id.toString(),
            dueDate,
            orderDate,
            totalSIAmt.toString(),
            totalSIAmt.toString(),
            lobCode,
            if (customerObj!!.customer_group_id != null) customerObj!!.customer_group_id!!.toString()
            else "",
            customerObj!!.customer_code,
            customerObj!!.userDetail.firstname,
            retAmt.toString(),
            cashType,
            "Invoice",
            "Invoice",
            "0",
            "0",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
        )

        //set Order Last Number
        mPreferenceManager!!.setString(AppConstants.COLLECTION_LAST, collNo)
        //set Order Last Number
        mPreferenceManager!!.setString(AppConstants.EXCHANGE_LAST, mExchanNo)

        dismissDialog()

        if (customerObj!!.credit_days == "0") {
            var mDiff = totalSIAmt - retAmt
            if (mDiff > 0) {
                val `in` = Intent(this@ExchangeActivity, PaymentDetailActivity::class.java)
                `in`.putExtra("customer", customerCode.toString())
                `in`.putExtra("amount", "" + totalSIAmt)
                `in`.putExtra("invDate", orderDate)
                `in`.putExtra("dueDate", dueDate)
                `in`.putExtra("invNo", invoiceNo)
                `in`.putExtra("collNum", collNo)
                `in`.putExtra("amtClear", "" + retAmt)
                `in`.putExtra("invAmt", "" + totalSIAmt)
                `in`.putExtra("paymentTerm", "0")
                `in`.putExtra("Type", "normal")
                startActivity(`in`)
                finish()
            } else {
                finish()
            }
        } else {
            finish()
        }

    }

    private fun setUpCategoryRecycler() {
        binding.rvCategory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mCatAdapter = CategoryAdapter(this, categoryList!!)
        binding.rvCategory.adapter = mCatAdapter
    }

    private fun setUpRecycler() {
        if (selectedType == "Exchange") {
            binding.rvSalesList.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            mAdapter = OrderRequestItemAdapter(this, allExchangeItemsList!!,"")
            binding.rvSalesList.adapter = mAdapter
        } else {
            binding.rvSalesList.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            mAdapter = OrderRequestItemAdapter(this, allItemsList!!,"")
            binding.rvSalesList.adapter = mAdapter
        }
    }

    private fun setActivity() {

        val mVisited = VisitedList()
        mVisited.customerId = customerObj!!.userDetail.id.toString()
        mVisited.customerCode = customerCode
        mVisited.activityName = "exchange"
        mVisited.activityAction = ""
        mVisited.startTime = Utils.getCurrentTime()
        mVisited.endTime = ""
        activityId = Utils.getVisitActivityNo(customerObj!!.userDetail.id.toString(), "1")
        mVisited.activityId = activityId
        visitId = db!!.getCustomerVisitId(customerObj!!.userDetail.id.toString())
        mVisited.visitedOffId = visitId

        db!!.insertVisitedCustomerActivity(mVisited)
    }

    private fun getCategoryItem(catId: String, mType: String): ArrayList<ItemData.Data>? {
        val arrMItem = ArrayList<ItemData.Data>()

        if (mType == "Exchange") {
            for (i in allExchangeItemsList!!) {
                if (i.majorCategory.toString() == catId) {
                    arrMItem.add(i)
                }
            }
        } else {
            for (i in allItemsList!!) {
                if (i.majorCategory.toString() == catId) {
                    arrMItem.add(i)
                }
            }
        }

        return arrMItem
    }

    override fun onItemsClick(position: Int, tag: String) {
        if (tag == "dialog") {
            itemPosition = getItemPosition(position)
            openQuantityDialog()
        }
    }

    fun getItemPosition(itemId: Int): Int {
        var position = 0
        if (selectedType == "Exchange") {
            for (mItem in allExchangeItemsList!!) {
                if (mItem.id == itemId) {
                    position = allExchangeItemsList!!.indexOf(mItem)
                    break
                }
            }
        } else {
            for (mItem in allItemsList!!) {
                if (mItem.id == itemId) {
                    position = allItemsList!!.indexOf(mItem)
                    break
                }
            }
        }

        return position
    }

    private fun openQuantityDialog() {

        if (selectedType == "Exchange") {
            if (allExchangeItemsList!![itemPosition].itemUOM == null)
                allExchangeItemsList!![itemPosition].itemUOM = ArrayList()

            startActivityForResult(
                Intent(this, InputOrderQtyDialogActivity::class.java).apply {
                    this.putExtra("item_code", allExchangeItemsList!![itemPosition].itemCode)
                    this.putExtra("item_id", allExchangeItemsList!![itemPosition].id.toString())
                    this.putExtra("cust_id", customerId)
                    this.putExtra(
                        "uom_list",
                        Gson().toJson(allExchangeItemsList!![itemPosition].itemSelectUOM)
                    )
                    this.putExtra(
                        "REuom_list",
                        Gson().toJson(allExchangeItemsList!![itemPosition].itemUOM)
                    )
                    this.putExtra(
                        "available_qty",
                        allExchangeItemsList!![itemPosition].barcode
                    )
                    this.putExtra(
                        "available_uom",
                        ""
                    )
                    this.putExtra(
                        "invNo",
                        invoiceNo
                    )
                    this.putExtra(
                        "invAmt",
                        invoiceAmt
                    )
                    this.putExtra("type", "goodReturn")
                    this.putExtra("lobCode", lobCode)
                },
                intentCode
            )
        } else {
            if (allItemsList!![itemPosition].itemUOM == null)
                allItemsList!![itemPosition].itemUOM = ArrayList()

            startActivityForResult(
                Intent(this, InputOrderQtyDialogActivity::class.java).apply {
                    this.putExtra("item_code", allItemsList!![itemPosition].itemCode)
                    this.putExtra("item_id", allItemsList!![itemPosition].id.toString())
                    this.putExtra("cust_id", customerObj!!.userDetail.id.toString())
                    this.putExtra(
                        "uom_list",
                        Gson().toJson(allItemsList!![itemPosition].itemSelectUOM)
                    )
                    this.putExtra(
                        "available_qty",
                        "0"
                    )
                    this.putExtra(
                        "available_uom",
                        allItemsList!![itemPosition].vanUOM
                    )
                    this.putExtra(
                        "display_uom",
                        allItemsList!![itemPosition].displayUnitId
                    )
                    this.putExtra(
                        "REuom_list",
                        Gson().toJson(allItemsList!![itemPosition].itemUOM)
                    )
                    this.putExtra("type", "sales")
                    this.putExtra("isExchange", "1")
                    this.putExtra("lobCode", lobCode)
                },
                intentCode
            )
        }

    }

    override fun onCategoryClick(position: Int, tag: String) {
        categoryList!![position].isClick = "1"
        categoryList!![catPosition].isClick = "0"
        catPosition = position
        mCatAdapter!!.notifyDataSetChanged()
        selectCatId = categoryList!![catPosition].catId.toString()

        catItemList = ArrayList()
        catItemList = getCategoryItem(selectCatId, selectedType)
        setUpRecycler()
    }

    override fun onBackPressed() {
        if (!isExchangeCreate) {
            db!!.deleteVisitActivity(activityId!!)
        }
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()

        if (isExchangeCreate) {
            isExchangeCreate = false
            CustomerDetailActivity.completedTask++
            var endTime = Utils.getCurrentTime()
            var action = "Exchange created"
            db!!.updateCustomerVisitActivity(endTime, action, visitId!!, activityId!!)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == intentCode) {
            if (resultCode == Activity.RESULT_OK) {

                if (selectedType == "Exchange") {
                    allExchangeItemsList!![itemPosition].itemSelectUOM = emptyList()

                    val strObj = data!!.getStringExtra("uomList")
                    val isReSale = data!!.getStringExtra("isReSale")
                    val listOfUomType: Type =
                        object : TypeToken<List<UomData?>?>() {}.type

                    var mList =
                        Gson().fromJson(strObj, listOfUomType) as ArrayList<UomData>

                    allExchangeItemsList!![itemPosition].itemSelectUOM = mList
                    allExchangeItemsList!![itemPosition].itemWeight = isReSale!!
                    if (isReSale == "1") {
                        allExchangeItemsList!![itemPosition].itemShellife = "1"
                    }
                    fillExchangePrice()

                    binding.rvSalesList.adapter!!.notifyDataSetChanged()
                } else {
                    allItemsList!![itemPosition].itemSelectUOM = emptyList()

                    val strObj = data!!.getStringExtra("uomList")
                    val isReSale = data!!.getStringExtra("isReSale")
                    val listOfUomType: Type =
                        object : TypeToken<List<UomData?>?>() {}.type

                    var mList =
                        Gson().fromJson(strObj, listOfUomType) as ArrayList<UomData>

                    allItemsList!![itemPosition].itemSelectUOM = mList
                    allItemsList!![itemPosition].itemWeight = isReSale!!
                    if (isReSale == "1") {
                        allItemsList!![itemPosition].itemShellife = "1"
                    }
                    binding.rvSalesList.adapter!!.notifyDataSetChanged()

                    fillSalePrice()
                }

            }
        }
    }

    fun fillExchangePrice() {

        exchangeAmt = 0F
        exchangeItemsList = ArrayList()

        for (mItemEx in allExchangeItemsList!!) {
            if (mItemEx.itemSelectUOM.isNotEmpty()) {
                exchangeItemsList!!.add(mItemEx)

                for (mUom in mItemEx.itemSelectUOM) {
                    exchangeAmt += Utils.twoDigit(mUom.grossPrice.toFloat())
                }
            }
        }

        binding.titleAmount.text = "Total Exchange"
        binding.tvAmount.setText(
            Utils.getCurrency(this) + " " + Utils.getCurrencyFormate(this, exchangeAmt.toFloat())
        )
    }

    fun fillSalePrice() {

        saleAmt = 0F
        balance = 0F
        saleItemsList = ArrayList()
        var saleAmt = 0F
        for (mItemEx in allItemsList!!) {
            if (mItemEx.itemSelectUOM.isNotEmpty()) {
                saleItemsList!!.add(mItemEx)

                for (mUom in mItemEx.itemSelectUOM) {
                    saleAmt += Utils.twoDigit(mUom.grossPrice.toFloat())
                }
            }
        }

        binding.titleAmount.text = "Balance"
        balance = Utils.twoDigit((exchangeAmt - saleAmt))
        binding.tvAmount.setText(Utils.getCurrency(this) + " " + balance)
    }

    private fun getOutstandingInvoice() {
        showOnlyProgressDialog()

        ApiClient.getClient(this).getCustomerInvoice(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN,
            customerObj!!.id
        ).enqueue(object : Callback<InvoiceData> {
            override fun onResponse(
                call: Call<InvoiceData>,
                response: Response<InvoiceData>
            ) {
                if (response.isSuccessful) {
                    if (response.body()!!.status!!) {
                        invoiceList = response.body()!!.data!!
                        setInvoiceSpinner()
                    }
                }
                dismissDialog()
            }

            override fun onFailure(call: Call<InvoiceData>, error: Throwable) {
                //Utils.logData(this@DownloadingDataActivity, "Order Type Failed")
                dismissDialog()
            }
        })
    }

    fun setInvoiceSpinner() {

        exchangeAmt = 0F
        spInvList!!.add("Select Invoice")
        spInvIdList!!.add("0")
        spInvAmtList!!.add("0")
        for (mInvoice in invoiceList) {
            spInvList!!.add(mInvoice.invoice_number)
            spInvIdList!!.add(mInvoice.invoice_id)
            spInvAmtList!!.add(mInvoice.grand_total)
        }

        val dataAdapter = spInvList?.let {
            ArrayAdapter<String>(
                applicationContext,
                R.layout.simple_spinner_item, it
            )
        }
        dataAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spInvoice.adapter = dataAdapter

        binding.spInvoice.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    invoiceId = spInvIdList!![position]
                    invoiceAmt = spInvAmtList!![position]
                    invoiceNo = spInvList!![position]
                    getInvoiceItem()
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // your code here
            }
        }
    }

    private fun getInvoiceItem() {

        showOnlyProgressDialog()

        ApiClient.getClient(this).getInvoiceItem(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN,
            invoiceId.toInt()
        ).enqueue(object : Callback<InvoiceItemData> {
            override fun onResponse(
                call: Call<InvoiceItemData>,
                response: Response<InvoiceItemData>
            ) {
                if (response.isSuccessful) {
                    if (response.body()!!.status!!) {
                        if (response.body()!!.data.isNotEmpty()) {
                            allExchangeItemsList!!.clear()
                            for (mItemData in response.body()!!.data) {
                                var mItem = db!!.getItemDetailFromId(mItemData.item_id)

                                if (mItem != null) {
                                    var mQty = "0"
                                    var mList: ArrayList<ItemData.Data.ItemMainUOM> = ArrayList()
                                    for (mUom in mItem!!.itemUOM) {
                                        if (mUom.uomId == mItemData.item_uom_id) {
                                            if (mItemData.item_discount_amount.toFloat() > 0) {
                                                var mPrice =
                                                    (mItemData.item_net.toFloat() / mItemData.item_qty.toFloat())
                                                mUom.uomPrice = Utils.twoDigit(mPrice).toString()
                                            } else {
                                                mUom.uomPrice = mItemData.item_price
                                            }
                                            mUom.exciseAmt = mItem.item_excise
                                            mList.add(mUom)
                                            val mLowerId =
                                                db!!.getItemLowerUnit(mItemId = mItem.id.toString())
                                            if (mLowerId == mUom.uomId) {
                                                mQty = mItemData.item_qty
                                            } else {
                                                var mUpc = db!!.getItemUOMUPC(
                                                    mItem.id.toString(),
                                                    mUom.uomId
                                                )
                                                mQty =
                                                    (mUpc.toDouble() * mItemData.item_qty.toDouble()).toString()
                                            }
                                        } else {
                                            mList.add(mUom)
                                        }
                                    }
                                    mItem.barcode = mQty
                                    mItem.itemUOM = mList
                                    allExchangeItemsList!!.add(mItem!!)
                                }
                            }
                        }
                    }
                }
                dismissDialog()
                catItemList = ArrayList()
                catItemList = allExchangeItemsList
                setUpRecycler()
            }

            override fun onFailure(call: Call<InvoiceItemData>, error: Throwable) {
                //Utils.logData(this@DownloadingDataActivity, "Order Type Failed")
                dismissDialog()
                catItemList = ArrayList()
                setUpRecycler()
            }
        })
    }

    /*****************************************************************************************
     * @ Creating data for print and saving it in DB for future printing
     */
    fun createPrintReturnData(invNum: String?): JSONArray? {
        val jArr = JSONArray()
        try {
            var totalGrossAmount = 0F
            var totalDiscount = 0F
            var totalNet = 0F
            var totalExciseAmount = 0F
            var totalVat1 = 0F
            var totalAmount = 0F
            val jInter = JSONArray()
            val jDict = JSONObject()
            jDict.put(AppConstants.REQUEST, AppConstants.RETURN_REQUEST)
            val mainArr = JSONObject()
            mainArr.put("ROUTE", mPreferenceManager!!.getString(AppConstants.ROUTENAME))
            mainArr.put("DOC DATE", Utils.formatDate(Date(), "dd-MM-yyyy"))
            mainArr.put("TIME", Utils.formatTime(Date(), "hh:mm"))
            mainArr.put("SALESMAN", mPreferenceManager!!.getString(AppConstants.DRIVERNAME))
            mainArr.put("CONTACTNO", "-")
            mainArr.put("DOCUMENT NO", invNum) //Load Summary No
            mainArr.put("TRIP START DATE", mPreferenceManager!!.getTripDate())
            mainArr.put("supervisorname", "-")
            mainArr.put("exchangeNo", mExchanNo) //Invoice No
            mainArr.put(
                "CUSTOMER",
                customerObj!!.userDetail.firstname + " " + customerObj!!.userDetail.lastname
            )
            mainArr.put("CUSTOMERID", customerObj!!.customer_code)
            if (customerObj!!.customer_address_1 != null) {
                mainArr.put("ADDRESS", "" + customerObj!!.customer_address_1)
            } else {
                mainArr.put("ADDRESS", "")
            }
            if (customerObj!!.customer_address_2 != null) {
                mainArr.put("ADDRESS2", "" + customerObj!!.customer_address_2)
            } else {
                mainArr.put("ADDRESS2", "")
            }

            if (customerObj!!.customer_city != null) {
                if (customerObj!!.customer_state != null) {
                    mainArr.put(
                        "CITY",
                        "" + customerObj!!.customer_city + " " + customerObj!!.customer_state
                    )
                } else {
                    mainArr.put("CITY", "" + customerObj!!.customer_city)
                }
            } else {
                if (customerObj!!.customer_state != null) {
                    mainArr.put("CITY", customerObj!!.customer_state)
                } else {
                    mainArr.put("CITY", "")
                }
            }

            if (customerObj!!.customer_phone != null) {
                mainArr.put("Mobile", "" + customerObj!!.customer_phone)
            } else {
                mainArr.put("Mobile", "")
            }
            mainArr.put("Country", "UNITED ARAB EMIRATES")
            var trnNo = db!!.getCustomerTRNNo(customerObj!!.customer_code)
            mainArr.put("TRN", trnNo)
            mainArr.put("LPO", "")
            mainArr.put("OrderReference", "")
            mainArr.put("Email", customerObj!!.userDetail.email)
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
            for (i in exchangeItemsList!!) {

                for (mUOM in i.itemSelectUOM) {

                    //Add Base UOM
                    val data = JSONArray()
                    data.put("" + (jData.length() + 1))
                    data.put(mUOM.itemName) // DESC
                    data.put(mUOM.invNo) // Inv.No
                    data.put(mUOM.invAmount) // Inv Amount
                    data.put(mUOM.uomType) // UOM
                    data.put(mUOM.uomQty) //  QTY
                    data.put(mUOM.uomPrice) // Unit Price
                    data.put(mUOM.grossPrice) // Gross
                    data.put(mUOM.item_excise) // Item Excise
                    data.put("5") // vat %
//                    data.put("" + mUOM.grossPrice) //Gross Amount
//                    data.put("" + mUOM.discountAmt) // Discount
                    data.put(mUOM.netAmt) // Net
                    data.put("" + mUOM.vatAmt) // Vat Amt
                    data.put("" + mUOM.totalAmt) // Total
                    data.put("" + i.barcode) // Total
                    var plu =
                        db!!.getCustomeItemPLU(customerObj!!.user_id.toString(), i.id.toString())
                    data.put("" + plu) // barcode
                    jData.put(data)

                    var excisePrice =
                        Utils.twoDigit((mUOM.uomQty.toFloat() * mUOM.item_excise.toFloat()))

                    totalExciseAmount += excisePrice.toFloat()
                    totalGrossAmount += mUOM.grossPrice.toFloat()
                    totalVat1 += mUOM.vatAmt.toFloat()
                    totalNet += mUOM.netAmt.toFloat()
                    totalAmount += mUOM.totalAmt.toFloat()
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
    fun createPrintInvoiceData(invNum: String?): JSONArray? {
        val jArr = JSONArray()
        try {
            var totalGrossAmount = 0F
            var totalGrossDiscount = 0F
            var totalDiscount = 0F
            var totalExciseAmount = 0F
            var totalNet = 0F
            var totalVat1 = 0F
            var totalAmount = 0F
            var totalQTy = 0F
            val jInter = JSONArray()
            val jDict = JSONObject()
            jDict.put(MyApplication.REQUEST, AppConstants.SALES_INVOICE)
            val mainArr = JSONObject()
            mainArr.put("ROUTE", mPreferenceManager!!.getString(AppConstants.ROUTENAME))
            mainArr.put("DOC DATE", Utils.formatDate(Date(), "dd-MM-yyyy"))
            mainArr.put("TIME", Utils.formatTime(Date(), "hh:mm"))
            mainArr.put("SALESMAN", mPreferenceManager!!.getString(AppConstants.DRIVERNAME))
            mainArr.put("CONTACTNO", "-")
            mainArr.put("DOCUMENT NO", invNum) //Load Summary No
            mainArr.put("TRIP START DATE", mPreferenceManager!!.getTripDate())
            mainArr.put("supervisorname", "-")
            mainArr.put("invoicenumber", invNum) //Invoice No
            mainArr.put("exchangeNo", mExchanNo) //Invoice No
            mainArr.put(
                "CUSTOMER",
                customerObj!!.userDetail.firstname + " " + customerObj!!.userDetail.lastname
            )
            mainArr.put("CUSTOMERID", customerObj!!.customer_code)
            if (customerObj!!.customer_address_1 != null) {
                mainArr.put("ADDRESS", "" + customerObj!!.customer_address_1)
            } else {
                mainArr.put("ADDRESS", "")
            }
            if (customerObj!!.customer_address_2 != null) {
                mainArr.put("ADDRESS2", "" + customerObj!!.customer_address_2)
            } else {
                mainArr.put("ADDRESS2", "")
            }

            if (customerObj!!.customer_city != null) {
                if (customerObj!!.customer_state != null) {
                    mainArr.put(
                        "CITY",
                        "" + customerObj!!.customer_city + " " + customerObj!!.customer_state
                    )
                } else {
                    mainArr.put("CITY", "" + customerObj!!.customer_city)
                }
            } else {
                if (customerObj!!.customer_state != null) {
                    mainArr.put("CITY", customerObj!!.customer_state)
                } else {
                    mainArr.put("CITY", "")
                }
            }

            if (customerObj!!.customer_phone != null) {
                mainArr.put("Mobile", "" + customerObj!!.customer_phone)
            } else {
                mainArr.put("Mobile", "")
            }
            mainArr.put("Country", "UNITED ARAB EMIRATES")
            var trnNo = db!!.getCustomerTRNNo(customerObj!!.customer_code)
            mainArr.put("TRN", trnNo)
            mainArr.put("LPO", "")
            mainArr.put("OrderReference", "")
            mainArr.put("CustomerLPO", "")
            if (customerObj!!.credit_days == "0") {
                mainArr.put("invoiceType", "CASH") //Invoice No
            } else {
                mainArr.put("invoiceType", "CREDIT") //Invoice No
            }
            mainArr.put("Email", customerObj!!.userDetail.email)
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
            for (i in saleItemsList!!) {

                for (mUOM in i.itemSelectUOM) {

                    //Add Base UOM
                    val data = JSONArray()
                    data.put("" + (jData.length() + 1))
                    data.put(mUOM.itemName) // DESC
                    data.put(mUOM.uomType) // UOM
                    data.put(mUOM.uomQty) //  QTY
                    data.put(mUOM.uomPrice) // Unit Price
                    //data.put("" + mUOM.grossPrice) //Gross Amount
                    data.put(mUOM.grossPrice) // Gross
                    data.put(mUOM.item_excise) // Item Excise
                    data.put("0") // Discount
                    data.put("5") // vat %
                    data.put("" + mUOM.discountAmt) // Discount Amount
                    data.put(mUOM.netAmt) // Net
                    data.put("" + mUOM.vatAmt) // Vat Amt
                    data.put("" + mUOM.totalAmt) // Total
                    data.put("" + i.barcode) // Total
                    var plu =
                        db!!.getCustomeItemPLU(customerObj!!.user_id.toString(), i.id.toString())
                    data.put("" + plu) // barcode
                    jData.put(data)

                    var excisePrice =
                        Utils.twoDigit((mUOM.uomQty.toFloat() * mUOM.item_excise.toFloat()))

                    totalQTy += mUOM.uomQty.toFloat()

                    totalExciseAmount += excisePrice.toFloat()
                    totalGrossAmount += mUOM.grossPrice.toFloat()
                    totalDiscount += mUOM.discountAmt.toFloat()
                    totalVat1 += mUOM.vatAmt.toFloat()
                    totalNet += mUOM.netAmt.toFloat()
                    totalAmount += mUOM.totalAmt.toFloat()
                }
            }

            val jGrossData = JSONArray()
            val jDisData = JSONArray()
            val jNetData = JSONArray()
//            if (isCustDiscount) {
//                jGrossData.put("")
//                jGrossData.put("Gross Amount (AED)") // DESC
//                jGrossData.put("") // UOM
//                jGrossData.put("") //  QTY
//                jGrossData.put("") // Unit Price
//                //data.put("" + mUOM.grossPrice) //Gross Amount
//                jGrossData.put("") // Discount
//                jGrossData.put("") // vat %
//                jGrossData.put("0.00") // Discount Amount
//                jGrossData.put("" + totalNet) // Net
//                jGrossData.put("" + totalVat1) // Vat Amt
//                jGrossData.put("" + totalAmount) // Total
//
//                var vatDscAmt = Utils.getVatAmt(discountAmt.toDouble(), 5.0)
//                var totalDis = discountAmt + vatDscAmt
//
//                jDisData.put("")
//                jDisData.put("Discount Amount (AED)") // DESC
//                jDisData.put("") // UOM
//                jDisData.put("") //  QTY
//                jDisData.put("") // Unit Price
//                //data.put("" + mUOM.grossPrice) //Gross Amount
//                jDisData.put("") // Discount
//                jDisData.put("") // vat %
//                jDisData.put("0.00") // Discount Amount
//                jDisData.put("" + discountAmt) // Net
//                jDisData.put("" + vatDscAmt) // Vat Amt
//                jDisData.put("" + totalDis) // Total
//
//                var finalNet = totalNet - discountAmt
//                var finalVat = totalVat1 - vatDscAmt
//                var finalAmoount = totalAmount - totalDis
//
//                jNetData.put("")
//                jNetData.put("Net Amount (AED)") // DESC
//                jNetData.put("") // UOM
//                jNetData.put("") //  QTY
//                jNetData.put("") // Unit Price
//                //data.put("" + mUOM.grossPrice) //Gross Amount
//                jNetData.put("") // Discount
//                jNetData.put("") // vat %
//                jNetData.put("0.00") // Discount Amount
//                jNetData.put("" + finalNet) // Net
//                jNetData.put("" + finalVat) // Vat Amt
//                jNetData.put("" + finalAmoount) // Total
//
//                totalAmount = 0F
//                totalAmount = finalAmoount.toFloat()
//
//            } else {
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
            // }

            val totalObj = JSONObject()
            totalObj.put("VAT", Utils.twoDigit(totalVat1))
            totalObj.put("NET", Utils.twoDigit(totalNet))
            totalObj.put("EXCISE", Utils.twoDigit(totalExciseAmount))
            totalObj.put("Gross Total", Utils.twoDigit(totalGrossAmount))
            totalObj.put("Discount", Utils.twoDigit(totalDiscount))
            totalObj.put("Total Amount", Utils.twoDigit(totalAmount))
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
}