package com.engear.vansales.activities

import android.app.Activity
import android.app.AlertDialog
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
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.R
import com.engear.vansales.adapter.CategoryAdapter
import com.engear.vansales.adapter.OrderRequestItemAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityOrderRequestBinding
import com.engear.vansales.databinding.RowFocItemBinding
import com.engear.vansales.interfaces.OnCategoryClickListener
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.*
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.symbol.emdk.EMDKManager
import com.symbol.emdk.EMDKManager.FEATURE_TYPE
import com.symbol.emdk.barcode.*
import com.symbol.emdk.barcode.ScanDataCollection.ScanData
import com.symbol.emdk.barcode.Scanner
import com.symbol.emdk.barcode.StatusData.ScannerStates
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class OrderRequestActivity : BaseActivity(), OnItemClickListener, OnCategoryClickListener,
    EMDKManager.EMDKListener, Scanner.DataListener,
    Scanner.StatusListener {
    lateinit var binding: ActivityOrderRequestBinding
    private val intentCode = 100
    private val filterCode = 200
    private val scanCode = 300
    var customerId = ""
    var customerCode: String = ""
    var customerData: CustomerData.DataCustomer? = null
    var categoryList: ArrayList<CategoryData>? = ArrayList()
    var itemsList: ArrayList<ItemData.Data>? = ArrayList()
    var itemsSelectedList: ArrayList<ItemData.Data>? = ArrayList()
    var itemsCheckList: ArrayList<ItemData.Data>? = ArrayList()
    var itemsListTwo: ArrayList<ItemData.Data>? = ArrayList()
    var catItemList: ArrayList<ItemData.Data>? = ArrayList()
    var orderItemsList: ArrayList<ItemData.Data>? = ArrayList()
    var mFOCAdapter: FocItemAdapter? = null
    var itemPosition = -1
    var itemPositionId = 0
    var itemType: String? = null
    private var arrList: List<StockData.Data.StockList> = ArrayList()
    var mAdapter: OrderRequestItemAdapter? = null
    var mCatAdapter: CategoryAdapter? = null
    var isCustPromotion = false
    var isScan = false
    var promoOfferData: PromoOfferData? = null
    var arrPromoOfferData: ArrayList<PromoOfferData> = ArrayList()
    var arrApplyPromo: ArrayList<PromoOfferData> = ArrayList()
    var slabData: ArrayList<Slab> = ArrayList()
    var orderType = ""
    var offerType = ""
    var lobCode = ""
    var lobIdList = ""
    var catPosition = 0
    var selectCatId = ""
    var offerQty = "0"
    var offerUOMId = ""
    var mRefernece = ""
    var mBalance = 0
    var routeList: ArrayList<VanTransferRouteListData.DataVan.RouteDetail>? = ArrayList()
    var vanItemList: ArrayList<VanTransferItemListData.DataItemVan.ItemDetail>? = ArrayList()

    // Declare a variable to store EMDKManager object
    private var emdkManager: EMDKManager? = null

    // Declare a variable to store Barcode Manager object
    private var barcodeManager: BarcodeManager? = null

    // Declare a variable to hold scanner device to scan
    private var scanner: Scanner? = null

    private var dataLength = 0
    private var statusString = ""
    private val bContinuousMode = true
    private var balance = "0"

    companion object {
        var isOrderCreated = false

        class AsyncDataUpdate internal constructor(context: OrderRequestActivity) :
            AsyncTask<String?, Void?, String?>() {

            private var resp: String? = null
            private val activityReference: WeakReference<OrderRequestActivity> =
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


        class MyAsyncTask internal constructor(context: OrderRequestActivity) :
            AsyncTask<Int, String, String?>() {

            private var resp: String? = null
            private val activityReference: WeakReference<OrderRequestActivity> =
                WeakReference(context)

            override fun onPreExecute() {
                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return
                activity.showOnlyProgressDialog()
            }

            override fun doInBackground(vararg params: Int?): String? {
                val activity = activityReference.get()

                if (activity!!.itemType == "load") {
                    activity!!.itemsList =
                        activity!!.db!!.getAllRouteItem(activity!!.mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.routeId.toString())

                    if (activity!!.itemsList!!.isEmpty()) {
                        activity!!.itemsList = activity!!.db!!.getAllItem()
                    }
                } else if (activity!!.itemType == "goodReturn" || activity!!.itemType == "badReturn") {
                    activity!!.itemsCheckList =
                        activity!!.db!!.getAllRouteItem(activity!!.mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.routeId.toString())

                    if (activity!!.itemsCheckList!!.isEmpty()) {
                        activity!!.itemsCheckList = activity!!.db!!.getAllItem()
                    }

                    for (allItemListCheck in activity!!.itemsCheckList!!) {
                        var returnEnable =
                            activity!!.db!!.getCustomeItemRetrun(allItemListCheck.id.toString())
                        if (returnEnable != "0") {
                            activity!!.itemsList!!.add(allItemListCheck)
                        }
                    }

                } else if (activity!!.itemType == "order") {
                    var mCustomerItem =
                        activity!!.db!!.getAllCustomerPriceItem(activity!!.customerData!!.user_id.toString())
                    activity!!.itemsList = activity!!.db!!.getAllCustomerItem(mCustomerItem)
                } else {
                    if (activity!!.lobIdList.isEmpty()) {
                        activity!!.itemsList = activity!!.db!!.getAllItem()
                        // activity!!.itemsListTwo = activity!!.db!!.getAllItem()
                    } else {
                        activity!!.itemsList =
                            activity!!.db!!.getAllSalesmanItem(activity!!.lobIdList)
                    }
                }

                return resp
            }


            override fun onPostExecute(result: String?) {

                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return

                activity.dismissDialog()
                activity.callAdaapter()
            }

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderRequestBinding.inflate(
            LayoutInflater.from(this@OrderRequestActivity),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {
        //setWareHouse(true)
        Utils.logData(
            this@OrderRequestActivity,
            "On order request Screen "
        )

        //   The EMDKManager object will be created and returned in the callback.

        // The EMDKManager object will be created and returned in the callback.
        /* val results: EMDKResults = EMDKManager.getEMDKManager(
             applicationContext, this@OrderRequestActivity
         )*/
        // Check the return status of getEMDKManager and update the status Text
        // View accordingly
        // Check the return status of getEMDKManager and update the status Text
        // View accordingly
        /* if (results.statusCode !== EMDKResults.STATUS_CODE.SUCCESS) {
             Log.i("EMDK Scan", "EMDKManager Request Failed")
         }*/

        if (intent.getStringExtra("customer_code") != null) {
            customerCode = intent.getStringExtra("customer_code")!!
        }

        userInfo = mPreferenceManager!!.getUserInfo()

        itemType = intent.getStringExtra("type")
        if (itemType == "order") {
            customerData = db!!.getCustomerDetail(customerCode!!)
        } else if (itemType == "goodReturn") {
            mRefernece = intent.getStringExtra("isReference")!!
            customerData = db!!.getCustomerDetail(customerCode!!)
        } else if (itemType == "badReturn") {
            mRefernece = intent.getStringExtra("isReference")!!
            customerData = db!!.getCustomerDetail(customerCode!!)
        }

        if (intent.getStringExtra("lob") != null) {
            lobCode = intent.getStringExtra("lob")!!
        }

        if (customerData != null) {
            customerId = customerData!!.userDetail.id!!.toString()
            if (itemType == "order") {

                if (customerData!!.customer_type_id.toString() == "2") {

                } else {
                    var mLimit = customerData!!.credit_limit.toFloat()
                    var approveLimit =
                        db!!.getCustomerApproveLimit(customerData!!.userDetail.id.toString())
                    if (approveLimit.toFloat() > 0) {
                        var approveOutstad =
                            db!!.getCustomerApproveLimitOutstanding(customerData!!.userDetail.id.toString())
                        var mApproveOutLimit = approveLimit.toFloat() + approveOutstad.toFloat()
                        /// var mFinalLimit = mLimit + approveLimit.toFloat()
                        var amtDue = 0.0
                        amtDue = db!!.getCustomerBalance(customerData!!.userDetail.id.toString())
                        Log.e("cresitmilit", mApproveOutLimit.toString() + "--" + amtDue)
                        balance = Utils.twoDigit((mApproveOutLimit - amtDue.toFloat())).toString()
                    } else {
                        var mFinalLimit = mLimit
                        var amtDue = 0.0
                        amtDue = db!!.getCustomerBalance(customerData!!.userDetail.id.toString())
                        Log.e("cresitmilit", mFinalLimit.toString() + "--" + amtDue)
                        balance = Utils.twoDigit((mFinalLimit - amtDue.toFloat())).toString()
                    }
                }
                Log.e("CustomerId", customerId!!)
                var isCUstPromo = db!!.isPromotoCUst(customerId!!)
                if (isCUstPromo) {
                    isCustPromotion = true
                    arrPromoOfferData = db!!.getCustomerPromotion(customerId!!)
                }

                var cat = ""
                if (customerData!!.customer_category_id != null) {
                    cat = customerData!!.customer_category_id!!.toString()
                }

                var mParent = ""
                mParent = db!!.getCustomerParentCode(customerId)

                var isParentPromo = db!!.isPromotoParent(mParent)
                if (isParentPromo) {
                    isCustPromotion = true
                    var arrPromoParentData = db!!.getParentPromotion(mParent, customerId)

                    for (mData in arrPromoParentData) {
                        arrPromoOfferData.add(mData)
                    }
                }

                var isOtherPromo = db!!.isOtherPromoPDP(
                    customerData!!.region_id.toString(),
                    customerData!!.sales_organisation_id!!,
                    customerData!!.channel_id!!,
                    cat,
                    userInfo!!.data.userInfo.salesmanInfo.routeId!!.toString(),
                    customerData!!.sub_channel!!,
                    mParent
                )
                if (isOtherPromo) {
                    isCustPromotion = true
                    var cat = ""
                    if (customerData!!.customer_category_id != null) {
                        cat = customerData!!.customer_category_id!!.toString()
                    }
                    var arrPromoOtherData = db!!.getAllOtherPromotion(
                        customerData!!.region_id.toString(),
                        customerData!!.sales_organisation_id!!,
                        customerData!!.channel_id!!,
                        cat,
                        userInfo!!.data.userInfo.salesmanInfo.routeId!!.toString(),
                        customerData!!.sub_channel!!,
                        mParent,
                        customerId
                    )

                    for (mData in arrPromoOtherData) {
                        arrPromoOfferData.add(mData)
                    }
                }
            }
        }

        val task = MyAsyncTask(this@OrderRequestActivity)
        task.execute(10)


        if (itemType == "goodReturn") {
            setToolbarTitle("Return")
        } else if (itemType == "badReturn") {
            setToolbarTitle("Return")
        } else if (itemType == "order" || itemType == "load") {
            isOrderCreated = false

            if (itemType == "load") {
                binding.tvBottom!!.visibility = View.GONE
            }

            var formattedDate = ""
            val c = Calendar.getInstance()
            var datePattern = "yyyy-MM-dd"
            var dateFormat = SimpleDateFormat(datePattern)
            dateFormat = SimpleDateFormat(datePattern)
            val newDate = Date(c.timeInMillis)
            formattedDate = dateFormat.format(newDate)
            setToolbarTitle(formattedDate)
        }

        baseBinding.toolbarTitle.setOnClickListener {
            showDateDialog()
        }

        binding.btnSubmit.setOnClickListener {
            Utils.logData(
                this@OrderRequestActivity,
                "On sumbit button click"
            )
            orderItemsList!!.clear()
            orderItemsList = ArrayList()

            for (i in itemsSelectedList!!) {
                if (i.itemSelectUOM != null) {
                    if (i.itemSelectUOM!!.isNotEmpty()) {

                        if (i.barcode == "apply") {
                            var mGross = i.itemSelectUOM[0].grossPrice.toDouble()
                            var mExDisPer = 0.0
                            var mDiscntPerc = 0.0
                            var discountAmt = 0
                            var mExcise = i.itemSelectUOM[0].item_excise.toDouble()
                            var mNet = mGross - discountAmt + mExcise
                            var mVat = Utils.getVatAmt(mNet.toDouble(), 5.0).toDouble()
                            var vatAmt = Utils.twoDigit(mVat.toFloat())
                            var totalAmt = Utils.twoDigit((mNet + vatAmt).toFloat())

                            i.itemSelectUOM[0].discountPer = "" + mDiscntPerc
                            i.itemSelectUOM[0].discountAmt = "" + discountAmt
                            i.itemSelectUOM[0].netAmt = "" + mNet
                            i.itemSelectUOM[0].vatAmt = "" + vatAmt
                            i.itemSelectUOM[0].totalAmt = "" + totalAmt
                            i.barcode = ""
                        }
                        orderItemsList!!.add(i)
                    }
                }
            }

            if (orderItemsList!!.isNotEmpty()) {

//                if (isCustPromotion) {
//                    if (isPromoApplay()) {
//                        showApplyPromoDialog(0)
//                    } else {
//                        openConfrimActivity()
//                    }
//                } else {
//                    openConfrimActivity()
//                }

                if (itemType == "order") {
                    getCustomerPromotion()
                } else {
                    if (isCustPromotion) {
                        if (isPromoApplay()) {
                            showApplyPromoDialog(0)
                        } else {
                            openConfrimActivity()
                        }
                    } else {
                        openConfrimActivity()
                    }
                }
            } else {
                Toast.makeText(this, "Please Add Item!", Toast.LENGTH_SHORT).show()
            }

        }
    }


    private fun getCustomerPromotion() {

        showOnlyProgressDialog()
        val jItem = JsonArray()

        for (i in orderItemsList!!) {
            if (i.itemSelectUOM != null) {
                var jIObj = JsonObject()
                jIObj.addProperty(
                    "item_id",
                    i.itemSelectUOM[0].itemId
                )
                jIObj.addProperty(
                    "item_qty",
                    i.itemSelectUOM[0].uomQty
                )
                jIObj.addProperty(
                    "item_uom_id",
                    i.itemSelectUOM[0].uomId
                )
                jItem.add(jIObj)
            }
        }
        val jObj = JsonObject()
        jObj.addProperty(
            "customer_id",
            db!!.getCustomerCustId(customerData!!.customer_code)
        )
        jObj.addProperty(
            "delivery_date",
            baseBinding.toolbarTitle.text.toString()
        )
        jObj.addProperty(
            "is_dispanser",
            "0"
        )
        jObj.addProperty(
            "is_gallon",
            "0"
        )
        jObj.add(
            "items",
            jItem
        )

        Log.e("Promot Req", jObj.toString())
        ApiClient.getClient(this).getCustomerHODPromotion(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN,
            jObj!!
        ).enqueue(object : Callback<CustomerPromotionData> {
            override fun onResponse(
                call: Call<CustomerPromotionData>,
                response: Response<CustomerPromotionData>
            ) {

                dismissDialog()
                Log.e("success", "succ")
                Log.e("deliveries", Gson().toJson(response.body()))
                var promoList: List<CustomerPromotionData.CustomerPromotion.ItemPromoInfo>? =
                    ArrayList()

                if (response.body()!!.status!!) {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                    Log.e("deliveries", Gson().toJson(response.body()))
                    if (response.body()!!.data!!.itemPromoInfo.isNotEmpty()) {
                        promoList = response.body()!!.data.itemPromoInfo!!
                    }
                }

                if (promoList!!.isNotEmpty()) {
                    customerHODPROMO(promoList)
                } else {
                    openConfrimActivity()
                }
            }

            override fun onFailure(call: Call<CustomerPromotionData>, error: Throwable) {
                dismissDialog()
                Log.e("fails", error.message!!)
                openConfrimActivity()
            }
        })
    }

    fun customerHODPROMO(promoList: List<CustomerPromotionData.CustomerPromotion.ItemPromoInfo>) {

        for (i in promoList!!) {
            if (i.type == "Item") {
                if (i.discount_type == "1" && i.discount_apply == "1") {

                    for (k in i.promotion_items!!) {

                        var mDItem = db!!.getItemDetailFromId(k.id)

                        val mUOMList = db!!.getItemUOM(mDItem!!.id.toString())

                        var mOfferUom = ""
                        if (mUOMList.size > 1) {
                            mOfferUom = mUOMList[1].uomId
                        } else {
                            mOfferUom = mUOMList[0].uomId
                        }

                        var arrSelectedBacth: ArrayList<BatchdetailsList> = ArrayList()
                        var mSelectUOMList: ArrayList<UomData> = ArrayList()

                        mSelectUOMList!!.add(
                            UomData(
                                mDItem!!.id.toString(),
                                mDItem!!.itemCode,
                                mDItem!!.itemName,
                                mOfferUom,
                                db!!.getUOMName(mOfferUom),
                                db!!.getItemUOMUPC(
                                    mDItem!!.id.toString(),
                                    mOfferUom
                                ),
                                i.FocQty,
                                "0",
                                "0",
                                "",
                                discountAmt = "0",
                                netAmt = "0",
                                vatAmt = "0",
                                totalAmt = "0",
                                promotion_Id = i.id,
                                discount_Id = "",
                                reason = "",
                                expiryDate = "",
                                invAmount = "",
                                invNo = "",
                                isBad = "0",
                                item_excise = "0.0",
                                batchs = arrSelectedBacth
                            )
                        )
                        mDItem.itemSelectUOM = emptyList()
                        mDItem.itemSelectUOM = mSelectUOMList
                        mDItem.isFree = "1"

                        orderItemsList!!.add(mDItem)
                    }
                }
            }

        }

        openConfrimActivity()

    }

    fun showApplyPromoDialog(mPosition: Int) {

        Log.e("PromoSize", arrApplyPromo.size.toString())
        Log.e("Select Size", mPosition.toString())

        var mPromoApplySize = mPosition

        if (mPromoApplySize == (arrApplyPromo.size)) {
            openConfrimActivity()
        } else {
            if (arrApplyPromo[mPosition].isApply == "0") {

                showFOCDialog(
                    mPosition,
                    arrApplyPromo[mPosition].promotionName!!.toString(),
                    arrApplyPromo[mPosition].offerQty!!.toString(),
                    arrApplyPromo[mPosition].offerUom!!.toString()
                )

            } else {
                var mNext = mPosition + 1
                showApplyPromoDialog(mNext)
            }
        }
    }

    private fun showFOCDialog(
        mPosition: Int,
        mPlanName: String,
        mOfferQty: String,
        mOfferUom: String
    ) {

        var mPlanId = arrApplyPromo[mPosition].promotionId
        var mPromoType = arrApplyPromo[mPosition].type!!.toString()
        var mDiscType = arrApplyPromo[mPosition].discType!!.toString()

        var arrOfferItem = ArrayList<Item>()
        if (arrApplyPromo[mPosition].offerPromo[0].type == "Item") {
            arrOfferItem = db!!.getPromoOfferItemIdData(mPlanId!!)
        } else {
            var mOfferGroup = arrApplyPromo[mPosition].offerPromo[0].promoGroupId
            arrOfferItem = db!!.getPromoGroupItemData(mOfferGroup!!)
        }

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_promotion)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val metrics = resources.displayMetrics
        val screenWidth = (metrics.widthPixels * 0.95).toInt()
        dialog.window!!.setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT)

        val txtName = dialog.findViewById(R.id.txtPromotionName) as TextView
        txtName.setText(mPlanName)

        val txtFocQty = dialog.findViewById(R.id.txtFocQty) as TextView
        if (mDiscType == "1") {
            txtFocQty.text = "Foc Qty: $mOfferQty"
        } else {
            txtFocQty.text = "Foc Discount: $mOfferQty"
        }

        val txtEntQty = dialog.findViewById(R.id.txtEntQty) as TextView
        txtEntQty.visibility = View.GONE
        txtEntQty.text = "Enter Qty: "

        val rvList = dialog.findViewById(R.id.rvList) as RecyclerView
        rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mFOCAdapter = FocItemAdapter(
            context = this,
            allList = arrOfferItem,
            mPromoType,
            mDiscType
        )
        rvList.adapter = mFOCAdapter

        val btnSumbit = dialog.findViewById(R.id.btnAdd) as LinearLayout
        btnSumbit.setOnClickListener {

            var mAllItem = mFOCAdapter!!.getItems()

            if (mDiscType == "2") {

                for (mFOCITem in mAllItem!!) {
                    for (mDataItem in orderItemsList!!) {

                        if (mDataItem.id!!.toString() == mFOCITem!!.itemId) {

                            if (mDataItem.isFree != "1") {
                                var mGross = mDataItem.itemSelectUOM[0].grossPrice.toDouble()
                                var mExDisPer = mDataItem.itemSelectUOM[0].discountPer.toDouble()
                                var mExDisAmt = mDataItem.itemSelectUOM[0].discountAmt.toDouble()
                                var mDNet = mDataItem.itemSelectUOM[0].netAmt.toDouble()
                                var mDiscntPerc = mOfferQty.toFloat()
                                var discountAmt =
                                    Utils.twoDigit(((mDNet * mDiscntPerc!!.toFloat()) / 100).toFloat())
                                var mExcise = mDataItem.itemSelectUOM[0].item_excise.toDouble()
                                var mNet = mDNet - discountAmt + mExcise
                                var mVat = Utils.getVatAmt(mNet.toDouble(), 5.0).toDouble()
                                var vatAmt = Utils.twoDigit(mVat.toFloat())
                                var totalAmt = Utils.twoDigit((mNet + vatAmt).toFloat())

                                var mFInalDisAmt = mExDisAmt + discountAmt
                                var mDisPer = mExDisPer + mDiscntPerc
                                mDataItem.itemSelectUOM[0].discountPer = "" + mDisPer
                                mDataItem.itemSelectUOM[0].discountAmt = "" + mFInalDisAmt
                                mDataItem.itemSelectUOM[0].netAmt = "" + mNet
                                mDataItem.itemSelectUOM[0].vatAmt = "" + vatAmt
                                mDataItem.itemSelectUOM[0].totalAmt = "" + totalAmt
                                mDataItem
                                    .barcode = "apply"
                            }
                        }
                    }
                }

                arrApplyPromo[mPosition].isApply = "1"
                dialog.dismiss()
                var mNext = mPosition + 1
                showApplyPromoDialog(mNext)

            } else {
                var mSelectQty = 0.0
                for (mItem in mAllItem!!) {
                    if (!mItem!!.itemQty!!.isEmpty()) {
                        mSelectQty += mItem!!.itemQty!!.toDouble()
                    }
                }

                if (mSelectQty < mOfferQty.toDouble() || mSelectQty > mOfferQty.toDouble()) {
                    if (mDiscType == "1") {
                        Toast.makeText(
                            applicationContext,
                            "You can not enter qty less or more then Foc Qty",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "You can not enter qty less or more then Foc Discount",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {

                    var mFOCItemDetail = getFOCItemDetail(mAllItem!!, mPlanId!!)
                    orderItemsList!!.addAll(mFOCItemDetail)

                    arrApplyPromo[mPosition].isApply = "1"
                    dialog.dismiss()
                    var mNext = mPosition + 1
                    showApplyPromoDialog(mNext)

                }
            }
        }

        dialog.show()
    }

    fun getFOCItemDetail(
        arrItem: List<Item?>,
        mPromotionId: String
    ): kotlin.collections.ArrayList<ItemData.Data> {
        var itemsListTwo: ArrayList<ItemData.Data>? = ArrayList()

        for (mDataItem in arrItem) {
            if (!mDataItem!!.itemQty!!.isEmpty()) {
                if (mDataItem!!.itemQty!!.toDouble() > 0) {
                    var mDItem = db!!.getItemDetailFromId(mDataItem!!.itemId!!)

                    val mUOMList = db!!.getItemUOM(mDItem!!.id.toString())

                    var mOfferUom = ""
                    if (mUOMList.size > 1) {
                        mOfferUom = mUOMList[1].uomId
                    } else {
                        mOfferUom = mUOMList[0].uomId
                    }

                    var arrSelectedBacth: ArrayList<BatchdetailsList> = ArrayList()
                    var mSelectUOMList: ArrayList<UomData> = ArrayList()

                    mSelectUOMList!!.add(
                        UomData(
                            mDItem!!.id.toString(),
                            mDItem!!.itemCode,
                            mDItem!!.itemName,
                            mOfferUom,
                            db!!.getUOMName(mOfferUom),
                            db!!.getItemUOMUPC(
                                mDItem!!.id.toString(),
                                mOfferUom
                            ),
                            mDataItem!!.itemQty!!,
                            "0",
                            "0",
                            "",
                            discountAmt = "0",
                            netAmt = "0",
                            vatAmt = "0",
                            totalAmt = "0",
                            promotion_Id = mPromotionId,
                            discount_Id = "",
                            reason = "",
                            expiryDate = "",
                            invAmount = "",
                            invNo = "",
                            isBad = "0",
                            item_excise = "0.0",
                            batchs = arrSelectedBacth
                        )
                    )
                    mDItem.itemSelectUOM = emptyList()
                    mDItem.itemSelectUOM = mSelectUOMList
                    mDItem.isFree = "1"

                    itemsListTwo!!.add(mDItem)
                }
            }

        }
        return itemsListTwo!!
    }

    class FocItemAdapter(
        var context: Activity,
        var allList: ArrayList<Item>,
        var mPromoType: String,
        var mDiscountType: String,
    ) : RecyclerView.Adapter<FocItemAdapter.ViewHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): FocItemAdapter.ViewHolder {
            val binding =
                RowFocItemBinding.inflate(LayoutInflater.from(context), parent, false)
            return ViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return allList.size
        }

        fun getItems(): List<Item?>? {
            return allList
        }

        override fun onBindViewHolder(
            holder: FocItemAdapter.ViewHolder,
            position: Int
        ) {
            holder.binding.tvName.text = allList[holder.adapterPosition].itemName
            holder.binding.tvCode.text = allList[holder.adapterPosition].itemCode

            if (mDiscountType == "2") {
                holder.binding.etQty.visibility = View.INVISIBLE
                holder.binding.etQty.setText(allList[holder.adapterPosition].itemQty)
            } else {
                holder.binding.etQty.setText(allList[holder.adapterPosition].itemQty)
            }

            holder.binding.etQty.addTextChangedListener(
                object : TextWatcher {

                    override fun afterTextChanged(s: Editable) {
                        if (s.toString().isNotEmpty()) {
                            allList[holder.adapterPosition].itemQty = s.toString()
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

        }

        inner class ViewHolder(val binding: RowFocItemBinding) :
            RecyclerView.ViewHolder(binding.root) {
        }

    }

    private fun singleChoicePromoForCombi(
        mPosition: Int,
        mPlanName: String,
        mOfferQty: String,
        mOfferUom: String,
        offerItem: ArrayList<ItemData.Data>
    ) {

        val array = arrayOfNulls<String>(offerItem.size)
        var i = 0
        for (mItem in offerItem) {
            array[i] = mItem.itemName
            i++
        }

        val mBuilder = AlertDialog.Builder(this@OrderRequestActivity)
        mBuilder.setTitle("Select offer item for $mPlanName Promotion")
        mBuilder.setSingleChoiceItems(array, -1) { dialogInterface, i ->

            var mItemData = offerItem[i]

            var mSelectUOMList: ArrayList<UomData> = ArrayList()
            mSelectUOMList!!.add(
                UomData(
                    mItemData.id.toString(),
                    mItemData.itemCode,
                    mItemData.itemName,
                    mOfferUom,
                    db!!.getUOMName(mOfferUom),
                    db!!.getItemUOMUPC(
                        mItemData.id.toString(),
                        mOfferUom
                    ),
                    mOfferQty,
                    "0",
                    "0",
                    "",
                    discountAmt = "0",
                    netAmt = "0",
                    vatAmt = "0",
                    totalAmt = "0",
                    promotion_Id = "",
                    discount_Id = "",
                    reason = ""
                )
            )
            mItemData.itemSelectUOM = emptyList()
            mItemData.itemSelectUOM = mSelectUOMList

            for (mUOM in mItemData.itemSelectUOM) {
                var index = mItemData.itemSelectUOM.indexOf(mUOM)
                var price = db!!.getItemPrice(
                    mItemData.id.toString(),
                    mUOM.uomId
                )
                mItemData.itemSelectUOM[index].uomPrice = price
                var mQty = mItemData.itemSelectUOM[index].uomQty
                var totalPrice = (mQty.toDouble() * (price.toDouble()))
                mItemData.itemSelectUOM[index].grossPrice = totalPrice.toString()
                mItemData.itemSelectUOM[index].discountAmt = totalPrice.toString()
                mItemData.itemSelectUOM[index].netAmt = "0"
                mItemData.itemSelectUOM[index].discountPer = "100%"
            }

            arrApplyPromo[mPosition].isApply = "1"
            orderItemsList!!.add(mItemData)
            dialogInterface.dismiss()
            var mNext = mPosition + 1
            showApplyPromoDialog(mNext)
        }
        // Set the neutral/cancel button click listener
        mBuilder.setNeutralButton("Cancel") { dialog, which ->
            // Do something when click the neutral button
            dialog.cancel()
        }

        val mDialog = mBuilder.create()
        mDialog.show()
    }

    fun callAdaapter() {
//        categoryList = ArrayList()
//        categoryList = db!!.getAllSalesmanItemCategory(lobIdList)
//        catPosition = 0
//        if (categoryList!!.isNotEmpty()) {
//            selectCatId = categoryList!![0].catId.toString()
//        }
//        setUpCategoryRecycler()
//        catItemList = ArrayList()
//        catItemList = getCategoryItem(selectCatId)
        setUpRecycler()
    }

    private fun openConfrimActivity() {
        if (itemType == "order") {
            startActivity(Intent(this, OrderConfirmActivity::class.java).apply {
                this.putExtra("orderItemList", Gson().toJson(orderItemsList))
                this.putExtra("customer_code", customerCode)
                this.putExtra("delivery_date", baseBinding.toolbarTitle.text.toString())
                this.putExtra("type", itemType)
                this.putExtra("lob", lobCode)
                this.putExtra("balance", balance)
            })
        } else if (itemType == "load") {
            startActivity(Intent(this, OrderConfirmActivity::class.java).apply {
                this.putExtra("orderItemList", Gson().toJson(orderItemsList))
                this.putExtra("customer_code", customerCode)
                this.putExtra("delivery_date", baseBinding.toolbarTitle.text.toString())
                this.putExtra("type", itemType)
                this.putExtra("lob", lobCode)
            })
        } else {
            startActivity(Intent(this, OrderConfirmActivity::class.java).apply {
                this.putExtra("orderItemList", Gson().toJson(orderItemsList))
                this.putExtra("customer_code", customerCode)
                this.putExtra("type", itemType)
                this.putExtra("lob", lobCode)
                this.putExtra("reType", "1")
            })
        }
    }

    private fun singleChoicePromo() {

        val array = arrayOfNulls<String>(promoOfferData!!.offerItem.size)
        var i = 0
        for (mItem in promoOfferData!!.offerItem) {
            array[i] = mItem.itemName
            i++
        }

        val mBuilder = AlertDialog.Builder(this@OrderRequestActivity)
        mBuilder.setTitle("Select offer item")
        mBuilder.setSingleChoiceItems(array, -1) { dialogInterface, i ->
            var mItemData = promoOfferData!!.offerItem[i]

            if (promoOfferData!!.isCombination!!.toString() == "1") {
                var mSelectUOMList: ArrayList<UomData> = ArrayList()
                mSelectUOMList!!.add(
                    UomData(
                        mItemData.id.toString(),
                        mItemData.itemCode,
                        mItemData.itemName,
                        offerUOMId,
                        db!!.getUOMName(offerUOMId),
                        db!!.getItemUOMUPC(
                            mItemData.id.toString(),
                            offerUOMId
                        ),
                        offerQty,
                        "0",
                        "0",
                        "",
                        discountAmt = "0",
                        netAmt = "0",
                        vatAmt = "0",
                        totalAmt = "0",
                        promotion_Id = "",
                        discount_Id = "",
                        reason = ""
                    )
                )
                mItemData.itemSelectUOM = emptyList()
                mItemData.itemSelectUOM = mSelectUOMList
            }

            for (mUOM in mItemData.itemSelectUOM) {
                var index = mItemData.itemSelectUOM.indexOf(mUOM)
                var price = db!!.getItemPrice(
                    mItemData.id.toString(),
                    mUOM.uomId
                )
                mItemData.itemSelectUOM[index].uomPrice = price
                var mQty = mItemData.itemSelectUOM[index].uomQty
                var totalPrice = (mQty.toDouble() * (price.toDouble()))
                mItemData.itemSelectUOM[index].grossPrice = totalPrice.toString()
                mItemData.itemSelectUOM[index].discountAmt = totalPrice.toString()
                mItemData.itemSelectUOM[index].netAmt = totalPrice.toString()
                mItemData.itemSelectUOM[index].discountPer = "100%"
            }

            orderItemsList!!.add(mItemData)
            dialogInterface.dismiss()
            openConfrimActivity()
        }
        // Set the neutral/cancel button click listener
        mBuilder.setNeutralButton("Cancel") { dialog, which ->
            // Do something when click the neutral button
            dialog.cancel()
        }

        val mDialog = mBuilder.create()
        mDialog.show()
    }


    private fun isPromoApplay(): Boolean {

        var isPromo = false

        arrApplyPromo = ArrayList()

        Log.e("Promo OfferSize", arrPromoOfferData.size.toString())
        for (mPromo in arrPromoOfferData) {
            Log.e("Promo Id", mPromo.promotionId!!)

            if (mPromo.type == "2") {

                if (mPromo.orderType == "Any") {
                    var mIsApply = false

                    var mFOCQty = 0.0
                    var mOrderQTy = 0.0
                    var mRemainQty = 0.0

                    if (mPromo!!.orderPromo[0].type == "Item") {
                        for (i in orderItemsList!!) {
                            if (i.id.toString() == mPromo!!.orderPromo[0].itemId) {

                            }
                        }
                    } else {
                        var mItemArr: kotlin.collections.ArrayList<String> = ArrayList()
                        mItemArr = db!!.getPromoGroupItem(mPromo!!.orderPromo[0].promoGroupId!!)

                        for (i in orderItemsList!!) {

                            for (mItem in mItemArr) {
                                if (i.id.toString() == mItem) {
                                    for (mUom in i.itemSelectUOM) {
                                        mOrderQTy += mUom.uomQty.toDouble()
                                    }
                                }
                            }
                        }

                        if (mOrderQTy > 0) {
                            mRemainQty = mOrderQTy

                            var mData =
                                getFOCQty(
                                    mRemainQty,
                                    mFOCQty,
                                    mPromo!!.orderPromo,
                                    mPromo!!.promotionId!!
                                )

                            mFOCQty += mData.get("FocQty")!!.toDouble()

                            if (mFOCQty > 0) {
                                mIsApply = true
                            }
                        }
                    }


                    if (mIsApply) {
                        var mApPromo = mPromo
                        mApPromo.isApply = "0"
                        mApPromo.offerQty = mFOCQty.toString()
                        mApPromo.offerUom = ""
                        arrApplyPromo.add(mApPromo)
                    }

                } else {
                    var mIsApply = false

                    var mFOCQty = 0.0
                    var mOrderQTy = 0.0
                    var mRemainQty = 0.0

                    if (mPromo!!.orderPromo[0].type == "Item") {
                        var mItemArr: kotlin.collections.ArrayList<String> = ArrayList()
                        mItemArr = db!!.getPromoOrderItemId(mPromo!!.promotionId!!)

                        var mAllCOntain = true
                        for (mItem in mItemArr) {

                            var isContain = false
                            for (i in orderItemsList!!) {
                                if (i.isFree.isEmpty()) {
                                    if (mItem == i.id.toString()) {
                                        for (mUom in i.itemSelectUOM) {
                                            mOrderQTy += mUom.uomQty.toDouble()
                                        }
                                        isContain = true
                                        break
                                    }
                                }
                            }

                            if (!isContain) {
                                mAllCOntain = false
                            }
                        }
//                        for (i in orderItemsList!!) {
//
//                            for (mItem in mItemArr) {
//                                if (i.id.toString() == mItem) {
//                                    for (mUom in i.itemSelectUOM) {
//                                        mOrderQTy += mUom.uomQty.toDouble()
//                                    }
//                                } else {
//                                    mAllCOntain = false
//                                }
//                            }
//                        }

                        if (mAllCOntain) {
                            if (mOrderQTy > 0) {
                                mRemainQty = mOrderQTy

                                var mData =
                                    getFOCQty(
                                        mRemainQty,
                                        mFOCQty,
                                        mPromo!!.orderPromo,
                                        mPromo!!.promotionId!!
                                    )

                                mFOCQty += mData.get("FocQty")!!.toDouble()

                                if (mFOCQty > 0) {
                                    mIsApply = true
                                }
                            }
                        }


                    } else {
                        var mItemArr: kotlin.collections.ArrayList<String> = ArrayList()
                        mItemArr = db!!.getPromoGroupItem(mPromo!!.orderPromo[0].promoGroupId!!)

                        for (i in orderItemsList!!) {

                            for (mItem in mItemArr) {
                                if (i.id.toString() == mItem) {
                                    for (mUom in i.itemSelectUOM) {
                                        mOrderQTy += mUom.uomQty.toDouble()
                                    }
                                }
                            }
                        }

                        if (mOrderQTy > 0) {
                            mRemainQty = mOrderQTy

                            var mData =
                                getFOCQty(
                                    mRemainQty,
                                    mFOCQty,
                                    mPromo!!.orderPromo,
                                    mPromo!!.promotionId!!
                                )

                            mFOCQty += mData.get("FocQty")!!.toDouble()

                            if (mFOCQty > 0) {
                                mIsApply = true
                            }
                        }
                    }


                    if (mIsApply) {
                        var mApPromo = mPromo
                        mApPromo.isApply = "0"
                        mApPromo.offerQty = mFOCQty.toString()
                        mApPromo.offerUom = ""
                        arrApplyPromo.add(mApPromo)
                    }
                }
            } else if (mPromo.type == "3") {

                if (mPromo.orderType == "All") {
                    var mIsApply = false

                    var mFOCQty = 0.0
                    var mOrderQTy = 0.0

                    if (mPromo!!.orderPromo[0].type == "Item") {

                        var mItemArr: kotlin.collections.ArrayList<String> = ArrayList()
                        mItemArr = db!!.getPromoOrderItemId(mPromo!!.promotionId!!)

                        var mAllCOntain = true

                        for (mItem in mItemArr) {

                            var isContain = false
                            for (i in orderItemsList!!) {
                                if (i.isFree.isEmpty()) {
                                    if (mItem == i.id.toString()) {
                                        for (mUom in i.itemSelectUOM) {
                                            mOrderQTy += mUom.uomQty.toDouble()
                                        }
                                        isContain = true
                                        break
                                    }
                                }
                            }

                            if (!isContain) {
                                mAllCOntain = false
                            }
                        }

//                        for (i in orderItemsList!!) {
//
//                            for (mItem in mItemArr) {
//                                if (i.id.toString() == mItem) {
//                                    for (mUom in i.itemSelectUOM) {
//                                        mOrderQTy += mUom.uomQty.toDouble()
//                                    }
//                                } else {
//                                    mAllCOntain = false
//                                }
//                            }
//                        }

                        if (mAllCOntain) {
                            if (mOrderQTy > 0) {

                                var mData =
                                    getRangeFOCQty(
                                        mOrderQTy,
                                        mPromo!!.orderPromo,
                                        mPromo!!.promotionId!!
                                    )

                                mFOCQty += mData.get("FocQty")!!.toDouble()

                                if (mFOCQty > 0) {
                                    mIsApply = true
                                }
                            }
                        }

                        if (mIsApply) {
                            var mApPromo = mPromo
                            mApPromo.isApply = "0"
                            mApPromo.offerQty = mFOCQty.toString()
                            mApPromo.offerUom = ""
                            arrApplyPromo.add(mApPromo)
                        }

                    } else {

                        var mItemArr: kotlin.collections.ArrayList<String> = ArrayList()
                        mItemArr = db!!.getPromoGroupItem(mPromo!!.orderPromo[0].promoGroupId!!)

                        for (i in orderItemsList!!) {

                            for (mItem in mItemArr) {
                                if (i.id.toString() == mItem) {
                                    for (mUom in i.itemSelectUOM) {
                                        mOrderQTy += mUom.uomQty.toDouble()
                                    }
                                }
                            }
                        }

                        if (mOrderQTy > 0) {

                            var mData =
                                getRangeFOCQty(
                                    mOrderQTy,
                                    mPromo!!.orderPromo,
                                    mPromo!!.promotionId!!
                                )

                            mFOCQty += mData.get("FocQty")!!.toDouble()

                            if (mFOCQty > 0) {
                                mIsApply = true
                            }
                        }

                        if (mIsApply) {
                            var mApPromo = mPromo
                            mApPromo.isApply = "0"
                            mApPromo.offerQty = mFOCQty.toString()
                            mApPromo.offerUom = ""
                            arrApplyPromo.add(mApPromo)
                        }

                    }
                } else if (mPromo.orderType == "Any") {
                    var mIsApply = false

                    var mFOCQty = 0.0
                    var mOrderQTy = 0.0
                    var mRemainQty = 0.0

                    if (mPromo!!.orderPromo[0].type == "Item") {

                    } else {
                        var mItemArr: kotlin.collections.ArrayList<String> = ArrayList()
                        mItemArr = db!!.getPromoGroupItem(mPromo!!.orderPromo[0].promoGroupId!!)

                        for (i in orderItemsList!!) {

                            for (mItem in mItemArr) {
                                if (i.id.toString() == mItem) {
                                    for (mUom in i.itemSelectUOM) {
                                        mOrderQTy += mUom.uomQty.toDouble()
                                    }
                                }
                            }
                        }

                        if (mOrderQTy > 0) {

                            var mData =
                                getRangeFOCQty(
                                    mOrderQTy,
                                    mPromo!!.orderPromo,
                                    mPromo!!.promotionId!!
                                )

                            mFOCQty += mData.get("FocQty")!!.toDouble()

                            if (mFOCQty > 0) {
                                mIsApply = true
                            }

                        }

                        if (mIsApply) {
                            var mApPromo = mPromo
                            mApPromo.isApply = "0"
                            mApPromo.offerQty = mFOCQty.toString()
                            mApPromo.offerUom = ""
                            arrApplyPromo.add(mApPromo)
                        }
                    }
                }
            }

        }

        if (arrApplyPromo.size > 0) {
            isPromo = true
        }
        return isPromo
    }

    fun getRangeFOCQty(
        mQty: Double,
        morderItem: ArrayList<PromoItemData>,
        mPromotionId: String
    ): HashMap<String, String> {

        var mData = kotlin.collections.HashMap<String, String>()

        var mFOCQQty = 0.0

        for (mOrderItem in morderItem) {

            var mFromQty = mOrderItem.fromqty!!
            var mToQty = mOrderItem.toqty!!

            if (mQty >= mFromQty.toDouble() && mQty <= mToQty.toDouble()) {
                mFOCQQty = db!!.getPromoOfferQtySlab(
                    mPromotionId!!,
                    mOrderItem.promoseq!!
                ).toDouble()
            }
        }

        mData.put("FocQty", mFOCQQty.toString())

        return mData;
    }

    fun getFOCQty(
        mQty: Double,
        mFOCQty: Double,
        morderItem: ArrayList<PromoItemData>,
        mPromotionId: String
    ): HashMap<String, String> {
        var mData = kotlin.collections.HashMap<String, String>()

        var mFOCQQty = mFOCQty
        var mRemainQty = mQty
        var mPosition = 0
        var mLastQty = 0.0
        for (mOrderItem in morderItem) {
            var mSlabQty = mOrderItem.fromqty!!
            mLastQty = morderItem[(morderItem.size - 1)].fromqty!!.toDouble()
            if (mRemainQty >= mSlabQty.toDouble()) {
                mFOCQQty += db!!.getPromoOfferQtySlab(
                    mPromotionId!!,
                    mOrderItem.promoseq!!
                ).toDouble()

                mRemainQty -= mSlabQty.toDouble()
                break
            }

//            mPosition++
//            if (mPosition == morderItem.size) {
//                mLastQty = mOrderItem.fromqty!!.toDouble()
//            }
        }

        if (mRemainQty >= mLastQty) {
            mData = getFOCQty(mRemainQty, mFOCQQty, morderItem, mPromotionId)
        } else {
            mData.put("Remain", mRemainQty.toString())
            mData.put("FocQty", Utils.twoDigit(mFOCQQty.toFloat()).toString())
        }

        return mData
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        val color: Int
        color = resources.getColor(R.color.colorWhite)

        /* var drawable =
             menu!!.findItem(R.id.action_search).icon

         drawable = DrawableCompat.wrap(drawable)
         DrawableCompat.setTint(drawable, color)
         menu!!.findItem(R.id.action_search).icon = drawable*/

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
//        val closeButton: View? = searchView.findViewById(androidx.appcompat.R.id.search_close_btn)
//        closeButton?.setOnClickListener {
//            Log.e("close","closss"+itemsListTwo!!.size)
//
//
////            itemsList!!.clear()
////            itemsList!!.addAll(itemsListTwo!!)
////            Log.e("close",itemsList!!.size.toString()+"--"+Gson().toJson(itemsList))
////            setUpRecycler()
//            mAdapter!!.notifyDataSetChanged()
//            searchView.setQuery("", false)
//        }


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
            /*  R.id.action_warehouse -> {
                  startActivity(
                      Intent(this, StockActivity::class.java)
                  )
                  return true
              }*/
            R.id.action_scan -> {
                var intent = Intent(applicationContext, SimpleScannerActivity::class.java)
                startActivityForResult(intent, scanCode)
                return true
            }

            R.id.action_filter -> {
                startActivityForResult(
                    Intent(this, FilterActivity::class.java)
                        .putExtra("Type", "Item"), filterCode
                )
                return true
            }

            android.R.id.home -> {
                finish()
                return true
            }
        }
        return false
    }

    private fun showDateDialog() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = android.app.DatePickerDialog(
            this,
            android.app.DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                baseBinding.toolbarTitle.text = "$year-" + String.format(
                    "%02d",
                    monthOfYear + 1
                ) + "-" + String.format("%02d", dayOfMonth)

            },
            year,
            month,
            day
        )
        dpd.datePicker.minDate = c.timeInMillis

        dpd.show()
    }

    private fun setUpRecycler() {
        binding.rvItemList.layoutManager =
            LinearLayoutManager(this@OrderRequestActivity, LinearLayoutManager.VERTICAL, false)
        mAdapter = OrderRequestItemAdapter(this@OrderRequestActivity, itemsList!!, itemType!!)
        binding.rvItemList.adapter = mAdapter
    }

    private fun setUpCategoryRecycler() {
        binding.rvCategory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mCatAdapter = CategoryAdapter(this, categoryList!!)
        binding.rvCategory.adapter = mCatAdapter
    }

    override fun onItemsClick(position: Int, tag: String) {
        if (tag == "dialog") {
            //var itemId = catItemList!![position].id
            isScan = false
            itemPosition = getItemPosition(position)
            openQuantityDialog()
        }
    }

    fun getItemId(barcode: String): Int {

        var itemId = 0
        for (i in itemsList!!) {
            if (i.barcode == barcode) {
                itemId = i.id
                break
            }
        }

        return itemId
    }

    fun getItemPosition(itemId: Int): Int {

        var position = 0
        itemPositionId = itemId
        for (i in itemsList!!.indices) {
            if (itemsList!![i].id == itemId) {
                position = i
                break
            }
        }
        return position
    }

    private fun openQuantityDialog() {
        if (itemsList!![itemPosition].itemUOM == null)
            itemsList!![itemPosition].itemUOM = ArrayList()

        if (itemType == "order") {

//            if (Utils.isNetworkAvailable(context = this)) {
//                getItemStockDetail(itemsList!![itemPosition].id.toString())
//            } else {
            openDialogActivity(mQty = "0", mUom = "")
            // }
        } else {
            openDialogActivity(mQty = "0", mUom = "")
        }

    }

    private fun openDialogActivity(mQty: String, mUom: String) {
        startActivityForResult(
            Intent(this@OrderRequestActivity, InputOrderQtyDialogActivity::class.java).apply {
                this.putExtra("item_code", itemsList!![itemPosition].itemCode)
                this.putExtra("item_id", itemsList!![itemPosition].id.toString())
                this.putExtra("cust_id", customerId)
                this.putExtra(
                    "uom_list",
                    Gson().toJson(itemsList!![itemPosition].itemSelectUOM)
                )
                this.putExtra(
                    "REuom_list",
                    Gson().toJson(itemsList!![itemPosition].itemUOM)
                )
                this.putExtra(
                    "available_qty",
                    mQty
                )
                this.putExtra(
                    "available_uom",
                    mUom
                )
                this.putExtra(
                    "invNo",
                    ""
                )
                this.putExtra(
                    "invAmt",
                    ""
                )
                this.putExtra("type", itemType)
                this.putExtra("lobCode", lobCode)
                this.putExtra("isRefence", mRefernece)
            },
            intentCode
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == scanCode) {
            if (resultCode == Activity.RESULT_OK) {
                var id = data!!.getStringExtra("scanid")
                Log.e("scanid", id!!)
//                mAdapter!!.filter!!.filter(id)
                for (i in 0 until itemsList!!.size) {
                    if (itemsList!![i].barcode.equals(id)) {
                        itemPositionId = itemsList!![i].id
                        itemPosition = i
                        openQuantityDialog()
                    }
                }
                isScan = true
            }
        } else
            if (requestCode == intentCode) {
                if (resultCode == Activity.RESULT_OK) {

                    itemsList!![itemPosition].itemSelectUOM = emptyList()

                    val strObj = data!!.getStringExtra("uomList")
                    val isReSale = data!!.getStringExtra("isReSale")
                    val listOfUomType: Type =
                        object : TypeToken<List<UomData?>?>() {}.type

                    var mList =
                        Gson().fromJson(strObj, listOfUomType) as ArrayList<UomData>
                    Log.e("dataoum", Gson().toJson(strObj))

                    itemsList!![itemPosition].itemWeight = isReSale!!
                    if (isReSale == "1") {
                        itemsList!![itemPosition].itemShellife = "1"
                    }

                    for (itm in itemsList!!.indices) {
                        if (itemPositionId == itemsList!![itm].id) {
                            if (mList[0].uomQty.toDouble() > 0) {
                                itemsList!![itm].itemSelectUOM = mList
                                addInSelection(itemsList!![itm])
                            } else {
                                removeSelectiedItem(itemsList!![itm])
                            }
                            break
                        }
                    }

                    mAdapter!!.notifyDataSetChanged()
                    getTotal()
                }
            } else if (requestCode == filterCode) {
                if (resultCode == Activity.RESULT_OK) {

                    if (data!!.getStringExtra("isClear") != null) {
                        itemsList!!.clear()
                        itemsList = db!!.getAllItem()
                        setUpRecycler()
                    } else {
                        val strObj = data!!.getStringExtra("itemList")
                        val listOfUomType: Type =
                            object : TypeToken<List<HashMap<String, String>?>?>() {}.type

                        var mList =
                            Gson().fromJson(
                                strObj,
                                listOfUomType
                            ) as ArrayList<HashMap<String, String>>

                        var brand = ""
                        var category = ""
                        var itemGroup = ""
                        for (mDat in mList) {
                            if (mDat["Type"] == "Brand") {
                                if (brand.isEmpty()) {
                                    brand = mDat["Id"]!!
                                } else {
                                    brand = brand + "," + mDat["Id"]!!
                                }
                            } else if (mDat["Type"] == "Category") {
                                if (category.isEmpty()) {
                                    category = mDat["Id"]!!
                                } else {
                                    category = category + "," + mDat["Id"]!!
                                }
                            } else if (mDat["Type"] == "Item Group") {
                                if (itemGroup.isEmpty()) {
                                    itemGroup = mDat["Id"]!!
                                } else {
                                    itemGroup = itemGroup + "," + mDat["Id"]!!
                                }
                            }
                        }

                        itemsList!!.clear()
                        itemsList = db!!.getFilterItem(category, brand, itemGroup)
                        setUpRecycler()
                    }

                }
            }
    }

    override fun onResume() {
        super.onResume()

        if (isOrderCreated) {
            isOrderCreated = false
            if (itemType == "order") {
                OrderActivity.isOrderDone = true
            }
            finish()
        }

    }

    private fun removeSelectiedItem(mItem: ItemData.Data) {
        if (itemsSelectedList!!.size > 0) {
            var mindex = 0
            for (mData in itemsSelectedList!!) {
                mindex = itemsSelectedList!!.indexOf(mData)
                if (mData.id == mItem.id) {
                    itemsSelectedList!!.removeAt(mindex)
                    break
                }
            }
        }
    }

    private fun addInSelection(mItem: ItemData.Data) {
        if (itemsSelectedList!!.size > 0) {
            var mindex = 0
            var isContain = false
            for (mData in itemsSelectedList!!) {
                mindex = itemsSelectedList!!.indexOf(mData)
                if (mData.id == mItem.id) {
                    isContain = true
                    break
                }
            }

            if (isContain) {
                itemsSelectedList!!.set(mindex, mItem)
            } else {
                itemsSelectedList!!.add(mItem)
            }
        } else {
            itemsSelectedList!!.add(mItem)
        }
    }

    private fun getItemStockDetail(itemId: String) {

        showOnlyProgressDialog()

        ApiClient.getClient(this).getItemStock(
            "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
            MyApplication.X_DOMAIN,
            mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.depotId.toInt(),
            itemId!!.toInt()
        ).enqueue(object : Callback<StockData> {
            override fun onResponse(
                call: Call<StockData>,
                response: Response<StockData>
            ) {
                dismissDialog()
                if (response.isSuccessful) {
                    if (response.body()!!.status!!) {
                        if (!response.body()!!.data.itemList.isNullOrEmpty()) {
                            arrList = response.body()!!.data.itemList!!
                            var totalQty = 0.0
                            var mUOMName = ""
                            for (mItem in arrList) {
                                totalQty += mItem.qty.toDouble()
                                mUOMName = mItem.uom.name
                            }
                            openDialogActivity(mQty = totalQty.toString(), mUom = mUOMName)
                        } else {
                            openDialogActivity(mQty = "0", mUom = "")
                        }
                    } else {
                        openDialogActivity(mQty = "0", mUom = "")
                    }
                } else {
                    openDialogActivity(mQty = "0", mUom = "")
                }
            }

            override fun onFailure(call: Call<StockData>, error: Throwable) {
                dismissDialog()
                openDialogActivity(mQty = "0", mUom = "")
            }
        })
    }

    private fun getCategoryItem(catId: String): ArrayList<ItemData.Data>? {
        val arrMItem = ArrayList<ItemData.Data>()
        for (i in itemsList!!) {
            if (i.majorCategory.toString() == catId) {
                arrMItem.add(i)
            }
        }
        return arrMItem
    }

    override fun onCategoryClick(position: Int, tag: String) {
        categoryList!![position].isClick = "1"
        categoryList!![catPosition].isClick = "0"
        catPosition = position
        mCatAdapter!!.notifyDataSetChanged()
        selectCatId = categoryList!![catPosition].catId.toString()

        catItemList = ArrayList()
        catItemList = getCategoryItem(selectCatId)
        setUpRecycler()
    }

    // Method to initialize and enable Scanner and its listeners
    @Throws(ScannerException::class)
    private fun initializeScanner() {
        if (scanner == null) {
            // Get the Barcode Manager object
            barcodeManager = emdkManager!!
                .getInstance(FEATURE_TYPE.BARCODE) as BarcodeManager
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

    override fun onOpened(po: EMDKManager?) {
        emdkManager = po

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
            val scanData: java.util.ArrayList<ScanData> = scanDataCollection.scanData
            for (data in scanData) {
                val dataString = data.data
                AsyncDataUpdate(this@OrderRequestActivity)
                    .execute(dataString)
            }
        }
    }


    override fun onStatus(statusData: StatusData?) {
        val state: ScannerStates = statusData!!.state
        when (state) {
            ScannerStates.IDLE -> {
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

            ScannerStates.WAITING -> {
                statusString = "Scanner is waiting for trigger press..."
                AsyncStatusUpdate()
                    .execute(statusString)
            }

            ScannerStates.SCANNING -> {
                statusString = "Scanning..."
                AsyncStatusUpdate()
                    .execute(statusString)
            }

            ScannerStates.DISABLED -> {
                statusString = statusData!!.friendlyName + " is disabled."
                AsyncStatusUpdate()
                    .execute(statusString)
            }

            ScannerStates.ERROR -> {
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
            if (itemId == 0) {
                Toast.makeText(
                    applicationContext,
                    "Item not available for given barcode!!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                itemPosition = getItemPosition(itemId)
                openQuantityDialog()
            }

        } else {
            Toast.makeText(
                applicationContext,
                "Item not available for given barcode!!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /*  private fun getRoute() {

          showOnlyProgressDialog()

          ApiClient.getClient(this).getVanRoute(
              "Bearer ${userInfo!!.data.accessToken}", MyApplication.X_DOMAIN
          )
              .enqueue(object : Callback<VanTransferRouteListData> {
                  override fun onResponse(
                      call: Call<VanTransferRouteListData>,
                      response: Response<VanTransferRouteListData>
                  ) {

                      if (response.isSuccessful) {
                          if (response.body()!!.status!!) {
                              Log.e("RouteData",Gson().toJson(response.body()!!.data))
                              var responsebody=response.body()!!.data
                              var routeNameList: ArrayList<String>?= ArrayList()
                              for (routeData in responsebody){
                                  routeList!!.add(routeData.route!!)
                                  routeNameList!!.add(routeData.route!!.route_name)
                              }

                              val adapter = ArrayAdapter(
                                  this@OrderRequestActivity,
                                  R.layout.layout_drop_title, routeNameList!!
                              )

                              binding.spRouteSelect!!.adapter = adapter
                              var route_id:Int=0
                              binding.spRouteSelect!!.setOnItemSelectedListener(object :
                                  AdapterView.OnItemSelectedListener {
                                  override fun onItemSelected(
                                      parent: AdapterView<*>,
                                      view: View,
                                      position: Int,
                                      id: Long
                                  ) {
                                      val vendor_name: String = parent!!.getItemAtPosition(position).toString()
                                      for (vendorlist in routeList!!) {
                                          if (vendorlist.route_name.equals(vendor_name)) {
                                             route_id = vendorlist.id
                                              break
                                          }

                                      }
                                      getVanTransferItem(route_id)

                                  }

                                  override fun onNothingSelected(parent: AdapterView<*>?) {}
                              })

                          }
                      }
                      dismissDialog()
                  }

                  override fun onFailure(call: Call<VanTransferRouteListData>, error: Throwable) {
                      Utils.logData(this@OrderRequestActivity, "Route Failed")
                      dismissDialog()

                  }
              })
      }

      private fun getVanTransferItem(route_id: Int) {

          val jObj = JsonObject()
          jObj.addProperty("route_id", route_id)
          Log.e("route_id", jObj.toString())
          ApiClient.getClient(this).getVanTransferItem(
              "Bearer ${userInfo!!.data.accessToken}", MyApplication.X_DOMAIN,
              jObj
          )
              .enqueue(object : Callback<VanTransferItemListData> {
                  override fun onResponse(
                      call: Call<VanTransferItemListData>,
                      response: Response<VanTransferItemListData>
                  ) {

                      if (response.isSuccessful) {
                          if (response.body()!!.status!!) {
                             Log.e("ITEM DATA",Gson().toJson(response.body()!!.data))
                              var responsebody=response.body()!!.data
                              for (routeData in responsebody){
                                  vanItemList!!.add(routeData.item!!)
                                  itemsList = db!!.getAllVanTransferItem(routeData.item_id.toString())
                              }
                              setUpRecycler()

                          }
                      }

                      dismissDialog()


                  }

                  override fun onFailure(call: Call<VanTransferItemListData>, error: Throwable) {
                      Utils.logData(this@OrderRequestActivity, "Customer Failed")
                      dismissDialog()
                  }
              })
      }*/

    private fun getTotal() {
        var mNet = 0.0
        var mGross = 0.0

        for (mInv in itemsSelectedList!!) {

            for (mUom in mInv.itemSelectUOM) {
                mNet += mUom.uomQty.toDouble()
                mGross += mUom.netAmt.toDouble()
            }
        }

        binding.txtNet!!.text = "" + mNet
        binding.txtGross!!.text = "" + mGross
    }

}