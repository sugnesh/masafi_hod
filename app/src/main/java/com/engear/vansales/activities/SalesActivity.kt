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
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.R
import com.engear.vansales.adapter.CategoryAdapter
import com.engear.vansales.adapter.OrderRequestItemAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivitySalesBinding
import com.engear.vansales.databinding.CouponInsertDialogBinding
import com.engear.vansales.databinding.OrderCommentDialogBinding
import com.engear.vansales.databinding.RowFocItemBinding
import com.engear.vansales.interfaces.OnCategoryClickListener
import com.engear.vansales.interfaces.OnDeliveryItemClickListener
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
import java.util.*
import kotlin.collections.ArrayList
import com.symbol.emdk.EMDKManager.FEATURE_TYPE

import com.symbol.emdk.barcode.BarcodeManager


class SalesActivity : BaseActivity(), OnItemClickListener, OnCategoryClickListener,
    Scanner.DataListener, Scanner.StatusListener,
    EMDKManager.EMDKListener {
    lateinit var binding: ActivitySalesBinding
    var customerCode: String? = null
    var allItemsList: ArrayList<ItemData.Data>? = ArrayList()
    var allItemsChckList: ArrayList<ItemData.Data>? = ArrayList()
    var saleItemsList: ArrayList<ItemData.Data>? = ArrayList()
    var nonFocItemsList: ArrayList<ItemData.Data>? = ArrayList()

    var itemPosition = -1
    var itemPositionId = 0
    private val intentCode = 100
    private val filterCode = 200
    private val scanCode = 300
    var mAdapter: OrderRequestItemAdapter? = null
    var catItemList: ArrayList<ItemData.Data>? = ArrayList()
    var categoryList: ArrayList<CategoryData>? = ArrayList()
    var mCatAdapter: CategoryAdapter? = null
    var customerObj: CustomerData.DataCustomer? = null
    var visitId: String? = null
    var activityId: String? = null
    var isCustPromotion = false
    var isScan = false
    var promoOfferData: PromoOfferData? = null
    var slabData: ArrayList<Slab> = ArrayList()
    var arrApplyPromo: ArrayList<PromoOfferData> = ArrayList()
    var arrPromoOfferData: ArrayList<PromoOfferData> = ArrayList()
    var orderType = ""
    var offerType = ""
    var customerId = ""
    var lobCode = ""
    var catPosition = 0
    var selectCatId = ""
    var balance = ""
    var offerQty = "0"
    var offerUOMId = ""
    var singleScan = "1"
    var isCouponSales = "0"
    var isReSaleCust = false
    var customerPortItem: ArrayList<String> = ArrayList()
    var customerPortSaleItem: ArrayList<String> = ArrayList()

    // Declare a variable to store EMDKManager object
    private var emdkManager: EMDKManager? = null

    // Declare a variable to store Barcode Manager object
    private var barcodeManager: BarcodeManager? = null

    // Declare a variable to hold scanner device to scan
    private var scanner: Scanner? = null

    private var dataLength = 0
    private var statusString = ""
    private val bContinuousMode = true
    var mFOCAdapter: FocItemAdapter? = null
    var isPayByCoupon = false
    var isPayCouponPaid = "0"
    private val scanFRCode = 900
    var isFromQRClick = true

    var fromScanText: EditText? = null
    var toScanText: EditText? = null
    var totalCountText: EditText? = null
    var mICashBalance: String = ""
    var mCustomerBalance: String = ""

    companion object {
        var isSaleCreate = false

        class AsyncDataUpdate internal constructor(context: SalesActivity) :
            AsyncTask<String?, Void?, String?>() {

            private var resp: String? = null
            private val activityReference: WeakReference<SalesActivity> =
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
        binding = ActivitySalesBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {
        setToolbarTitle("Sales")
        Utils.logData(
            this@SalesActivity,
            "On sales Screen "
        )

        // The EMDKManager object will be created and returned in the callback.
        /* val results: EMDKResults = EMDKManager.getEMDKManager(
             applicationContext, this@SalesActivity
         )*/
        // Check the return status of getEMDKManager and update the status Text
        // View accordingly
        // Check the return status of getEMDKManager and update the status Text
        // View accordingly
        /*   if (results.statusCode !== EMDKResults.STATUS_CODE.SUCCESS) {
               Log.i("EMDK Scan", "EMDKManager Request Failed")
           }
   */
        customerCode = intent.getStringExtra("customer_code")
        balance = intent.getStringExtra("balance")!!

        customerObj = db!!.getCustomerDetail(customerCode!!)
        customerId = customerObj!!.userDetail.id!!.toString()

        userInfo = mPreferenceManager!!.getUserInfo()

        if (intent.getStringExtra("lob_code") != null) {
            lobCode = intent.getStringExtra("lob_code")!!
        }

        if (intent.getStringExtra("isCoupon") != null) {
            isCouponSales = intent.getStringExtra("isCoupon")!!
        }

        mICashBalance = intent.getStringExtra("iCashBalance")!!
        mCustomerBalance = intent.getStringExtra("accountBalance")!!

        isReSaleCust = db!!.isCustReSalePrice(customerId!!)
        customerPortItem = db!!.getAllCUstomerItemList(customerId!!)

        var isCUstPromo = db!!.isPromotoCUst(customerId!!)
        if (isCUstPromo) {
            isCustPromotion = true
            arrPromoOfferData = db!!.getCustomerPromotion(customerId!!)
        }

        var mParent = ""
        mParent = db!!.getCustomerParentCode(customerId!!)

        var isParentPromo = db!!.isPromotoParent(mParent)
        if (isParentPromo) {
            isCustPromotion = true
            var arrPromoParentData = db!!.getParentPromotion(mParent, customerId!!)

            for (mData in arrPromoParentData) {
                arrPromoOfferData.add(mData)
            }
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
            userInfo!!.data.userInfo.salesmanInfo.routeId!!.toString(),
            if (customerObj!!.sub_channel != null) customerObj!!.sub_channel.toString()
            else "",
            mParent
        )
        if (isOtherPromo) {
            isCustPromotion = true
            var arrPromoOtherData = db!!.getAllOtherPromotion(
                if (customerObj!!.region_id != null) customerObj!!.region_id.toString()
                else "",
                if (customerObj!!.sales_organisation_id != null) customerObj!!.sales_organisation_id.toString()
                else "",
                if (customerObj!!.channel_id != null) customerObj!!.channel_id.toString()
                else "",
                if (customerObj!!.customer_category_id != null) customerObj!!.customer_category_id.toString()
                else "",
                userInfo!!.data.userInfo.salesmanInfo.routeId!!.toString(),
                if (customerObj!!.sub_channel != null) customerObj!!.sub_channel.toString()
                else "",
                mParent,
                customerId
            )

            for (mData in arrPromoOtherData) {
                arrPromoOfferData.add(mData)
            }
        }

        nonFocItemsList = ArrayList()

        if (isCouponSales == "1") {
            allItemsChckList = db!!.getAllCouponSaleItem()
        } else {
            allItemsChckList = db!!.getAllFreshSaleItem()
        }


        for (allItemListCheck in allItemsChckList!!) {
            var saleEnable =
                db!!.getCustomeItemSale(allItemListCheck.id.toString())
            if (saleEnable == "0") {

            } else {
                allItemsList!!.add(allItemListCheck)
            }
        }

        if (customerObj!!.channel_id == "1") {
            if (isCouponSales == "1") {
                binding.lytCoupon!!.visibility = View.GONE
            } else {
                binding.lytCoupon!!.visibility = View.VISIBLE
            }
        }

        binding.lytCoupon!!.setOnClickListener {

            if (isPayByCoupon) {
                isPayByCoupon = false
                binding.chk!!.isChecked = false
            } else {
                isPayByCoupon = true
                binding.chk!!.isChecked = true
            }
        }

        setUpRecycler()
        setActivity()

        binding.btnSubmit.setOnClickListener {
            Utils.logData(
                this@SalesActivity,
                "On submit button click"
            )
            saleItemsList!!.clear()
            saleItemsList = ArrayList()


            for (i in allItemsList!!) {
                if (i.itemSelectUOM != null) {
                    if (i.itemSelectUOM!!.isNotEmpty()) {
                        var isConatin = false
                        for (mItem in saleItemsList!!) {
                            if (mItem.itemCode == i.itemCode) {
                                isConatin = true
                                break
                            }
                        }

                        if (!isConatin) {
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
                            saleItemsList!!.add(i)
                        }
                    }
                }
            }

            if (binding.chk!!.isChecked) {
                isPayCouponPaid = "1"
            } else {
                isPayCouponPaid = "0"
            }
            Log.e("isCouponPiad", isPayCouponPaid)
            if (isPayCouponPaid == "1") {
                if (saleItemsList!!.size > 1) {
                    Utils.displayAlert(
                        this,
                        "You need to make seprate invoice for coupon item and normal items!!"
                    )
                } else {
                    if (saleItemsList!![0].itemCode == "MMGW001" || saleItemsList!![0].itemCode == "MMGW003" ) {
                        couponSerialDialog()
                    } else {
                        Utils.displayAlert(
                            this,
                            "You can not Redeem coupon in normal items"
                        )
                    }
                }
            } else {
                if (saleItemsList!!.isNotEmpty()) {
                    if (isCustPromotion) {
                        if (isPromoApplay()) {
                            showApplyPromoDialog(0)
                        } else {
                            openConfrimActivity()
                        }
                    } else {
                        openConfrimActivity()
                    }
                } else {
                    Toast.makeText(this, "Please Add Item!", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun couponSerialDialog() {
        val factory = LayoutInflater.from(this)
        val binding: CouponInsertDialogBinding =
            DataBindingUtil.inflate(factory, R.layout.coupon_insert_dialog, null, false)

        val deleteDialogView: View = binding.root

        val deleteDialog: AlertDialog = AlertDialog.Builder(this).create()
        deleteDialog.setView(deleteDialogView)
        deleteDialog.setCancelable(false)

        fromScanText = binding.etFromSerial
        toScanText = binding.etToSerial
        totalCountText = binding.etCount

        var mQty = saleItemsList!![0].itemSelectUOM!![0].uomQty
        binding.txtName.text = saleItemsList!![0].itemName
        binding.txtQty.text = "Quantity: " + saleItemsList!![0].itemSelectUOM!![0].uomQty
        binding.txtUOM.text = "UOM: " + saleItemsList!![0].itemSelectUOM!![0].uomType

        binding.rgSerial.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbSingle -> {
                    binding.lytToRange.visibility = View.GONE
                    binding.txtFrom.text = "Serial"
                    singleScan = "1"
                }

                R.id.rbRange -> {
                    binding.lytToRange.visibility = View.VISIBLE
                    binding.txtFrom.text = "From"
                    singleScan = "0"
                }
            }
        }

        binding.lytFromQR.setOnClickListener {
            isFromQRClick = true
            var intent = Intent(applicationContext, SimpleScannerActivity::class.java)
            startActivityForResult(intent, scanFRCode)
        }

        binding.lytToQR.setOnClickListener {
            isFromQRClick = false
            var intent = Intent(applicationContext, SimpleScannerActivity::class.java)
            startActivityForResult(intent, scanFRCode)
        }

        binding.btnConfirm.setOnClickListener {

            var mCount = binding.etCount.text.toString()
            if (mCount.isEmpty()) {
                Toast.makeText(this, "Please scan serial number", Toast.LENGTH_SHORT).show()
            } else {
                if (mCount.toDouble() == mQty.toDouble()) {
                    var mCouponId = db!!.getCustomerCouponId(customerObj!!.user_id!!.toString())
                    var mCouponPrice =
                        db!!.getCustomerCouponPrice(customerObj!!.user_id!!.toString())

                    var mUOMPrice =
                        mCouponPrice.toDouble()
                    var mQty = saleItemsList!![0].itemSelectUOM[0].uomQty
                    var mGross =
                        mQty.toDouble() * mUOMPrice
                    var mDNet = mGross
                    var mNet = mDNet
                    var mVat = Utils.getVatAmt(mNet.toDouble(), 5.0).toDouble()
                    var vatAmt = Utils.twoDigit(mVat.toFloat())
                    var totalAmt = Utils.twoDigit((mNet + vatAmt).toFloat())

                    saleItemsList!![0].itemSelectUOM[0].uomPrice =
                        "" + mUOMPrice
                    saleItemsList!![0].itemSelectUOM[0].grossPrice = "" + mGross
                    saleItemsList!![0].itemSelectUOM[0].netAmt = "" + mGross
                    saleItemsList!![0].itemSelectUOM[0].vatAmt = "" + vatAmt
                    saleItemsList!![0].itemSelectUOM[0].totalAmt = "" + totalAmt

                    deleteDialog.dismiss()
                    openOtherActiviey(
                        binding.etFromSerial.text.toString(),
                        binding.etToSerial.text.toString()
                    )
                } else {
                    Toast.makeText(
                        this,
                        "Please scan serial number as per total sales qty",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }

        binding.btnCancel.setOnClickListener {
            deleteDialog.dismiss()
        }
        deleteDialog.show()
    }

    fun openOtherActiviey(mFrom: String, mTo: String) {


        saleItemsList!![0].itemSelectUOM[0].promotion_Id = mFrom
        saleItemsList!![0].itemSelectUOM[0].discount_Id = mTo

        startActivity(Intent(this, OrderConfirmActivity::class.java).apply {
            this.putExtra("orderItemList", Gson().toJson(saleItemsList))
            this.putExtra("customer_code", customerCode)
            this.putExtra("delivery_date", "")
            this.putExtra("type", "sales")
            this.putExtra("lob", lobCode)
            this.putExtra("isCoupon", isCouponSales)
            this.putExtra("balance", balance)
            this.putExtra("isCouponRedeem", isPayCouponPaid)
        })

    }

    private fun setUpCategoryRecycler() {
        binding.rvCategory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mCatAdapter = CategoryAdapter(this, categoryList!!)
        binding.rvCategory.adapter = mCatAdapter
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


    private fun openConfrimActivity() {
        startActivity(Intent(this, OrderConfirmActivity::class.java).apply {
            this.putExtra("orderItemList", Gson().toJson(saleItemsList))
            this.putExtra("customer_code", customerCode)
            this.putExtra("delivery_date", "")
            this.putExtra("type", "sales")
            this.putExtra("lob", lobCode)
            this.putExtra("isCoupon", isCouponSales)
            this.putExtra("balance", balance)
            this.putExtra("isCouponRedeem", isPayCouponPaid)
            this.putExtra("iCashBalance", mICashBalance)
            this.putExtra("accountBalance", mCustomerBalance)
        })
    }

    private fun getCategoryItem(catId: String): ArrayList<ItemData.Data>? {
        val arrMItem = ArrayList<ItemData.Data>()
        for (i in allItemsList!!) {
            if (i.majorCategory.toString() == catId) {
                arrMItem.add(i)
            }
        }
        return arrMItem
    }

    private fun isPromoApplay(): Boolean {

        var isPromo = false

        arrApplyPromo = ArrayList()

        Log.e("Promo OfferSize", arrPromoOfferData.size.toString())
        for (mPromo in arrPromoOfferData) {

            if (mPromo.type == "2") {

                if (mPromo.orderType == "Any") {
                    var mIsApply = false

                    var mFOCQty = 0.0
                    var mOrderQTy = 0.0
                    var mRemainQty = 0.0

                    if (mPromo!!.orderPromo[0].type == "Item") {
                        for (i in saleItemsList!!) {
                            if (i.id.toString() == mPromo!!.orderPromo[0].itemId) {
                                for (mUom in i.itemSelectUOM) {
                                    mOrderQTy += mUom.uomQty.toDouble()
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

                    } else {
                        var mItemArr: kotlin.collections.ArrayList<String> = ArrayList()
                        mItemArr = db!!.getPromoGroupItem(mPromo!!.orderPromo[0].promoGroupId!!)

                        for (i in saleItemsList!!) {

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
                            for (i in saleItemsList!!) {
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

                        for (i in saleItemsList!!) {

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
                            for (i in saleItemsList!!) {
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

                        for (i in saleItemsList!!) {

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

                        for (i in saleItemsList!!) {

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

//        for (mOrderItem in morderItem) {
//            var mSlabQty = mOrderItem.fromqty!!
//
//            if (mRemainQty >= mSlabQty.toDouble()) {
//                mFOCQQty += db!!.getPromoOfferQtySlab(
//                    mPromotionId!!,
//                    mOrderItem.promoseq!!
//                ).toDouble()
//
//                mRemainQty -= mSlabQty.toDouble()
//            }
//
//            mPosition++
//            if (mPosition == morderItem.size) {
//                mLastQty = mOrderItem.fromqty!!.toDouble()
//            }
//        }

        if (mRemainQty >= mLastQty) {
            mData = getFOCQty(mRemainQty, mFOCQQty, morderItem, mPromotionId)
        } else {
            mData.put("Remain", mRemainQty.toString())
            mData.put("FocQty", mFOCQQty.toString())
        }

        return mData
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

                    for (mDataItem in saleItemsList!!) {

                        var index = saleItemsList!!.indexOf(mDataItem);


                        if (saleItemsList!![index].id!!.toString() == mFOCITem!!.itemId) {

                            if (saleItemsList!![index].isFree != "1") {
                                var mGross =
                                    saleItemsList!![index].itemSelectUOM[0].grossPrice.toDouble()
                                var mExDisPer =
                                    saleItemsList!![index].itemSelectUOM[0].discountPer.toDouble()
                                var mExDisAmt =
                                    saleItemsList!![index].itemSelectUOM[0].discountAmt.toDouble()
                                var mDNet =
                                    saleItemsList!![index].itemSelectUOM[0].netAmt.toDouble()
                                var mDiscntPerc = mOfferQty.toFloat()
                                var discountAmt =
                                    Utils.twoDigit(((mDNet * mDiscntPerc!!.toFloat()) / 100).toFloat())
                                var mExcise =
                                    saleItemsList!![index].itemSelectUOM[0].item_excise.toDouble()
                                var mNet = mDNet - discountAmt + mExcise
                                var mVat = Utils.getVatAmt(mNet.toDouble(), 5.0).toDouble()
                                var vatAmt = Utils.twoDigit(mVat.toFloat())
                                var totalAmt = Utils.twoDigit((mNet + vatAmt).toFloat())

                                var mFInalDisAmt = mExDisAmt + discountAmt
                                var mDisPer = mExDisPer + mDiscntPerc
                                saleItemsList!![index].itemSelectUOM[0].discountPer =
                                    "" + mDisPer
                                saleItemsList!![index].itemSelectUOM[0].discountAmt =
                                    "" + mFInalDisAmt
                                saleItemsList!![index].itemSelectUOM[0].netAmt = "" + mNet
                                saleItemsList!![index].itemSelectUOM[0].vatAmt = "" + vatAmt
                                saleItemsList!![index].itemSelectUOM[0].totalAmt = "" + totalAmt
                                saleItemsList!![index].barcode = "apply"

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

                    var isAvailable = true
                    var mItemName = ""
                    for (mItem in mAllItem!!) {
                        if (!mItem!!.itemQty!!.isEmpty()) {
                            if (db!!.isVanStockItemAvailable(
                                    mItem!!.itemId!!,
                                    mItem!!.itemQty!!.toString()
                                ).equals("0")
                            ) {
                                mItemName = mItem!!.itemName!!.toString()
                                isAvailable = false
                                break
                            }
                        }
                    }

                    if (isAvailable) {
                        var mFOCItemDetail = getFOCItemDetail(mAllItem!!, mPlanId!!)
                        saleItemsList!!.addAll(mFOCItemDetail)

                        arrApplyPromo[mPosition].isApply = "1"
                        dialog.dismiss()
                        var mNext = mPosition + 1
                        showApplyPromoDialog(mNext)
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "You can not sales $mItemName item because stock not available in van",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

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

//    private fun isPromoApplay(): Boolean {
//
//        var isPromo = false
//
//        arrApplyPromo = ArrayList()
//
//        Log.e("Promo OfferSize", arrPromoOfferData.size.toString())
//        for (mPromo in arrPromoOfferData) {
//
//            if (mPromo.isCombination!!.toString() == "1") {
//
//                var mTotalQty = 0.0
//
//                for (i in saleItemsList!!) {
//                    for (mItem in mPromo.orderItem) {
//                        if (i.id.toString() == mItem.id.toString()) {
//
//                            for (mUOM in i.itemSelectUOM) {
//                                var mLowerQty = mUOM.uomQty.toDouble() * mUOM.uomUPC.toDouble()
//                                mTotalQty += mLowerQty
//                            }
//                        }
//                    }
//                }
//
//                var slabData = db!!.getPromoSalb(mPromo.promotionId!!)
//
//                Log.e("total qty", mTotalQty.toString())
//                for (mSlab in slabData) {
//                    Log.e("SalabFrom", mSlab.fromQty!!.toString())
//                    Log.e("SalabTo", mSlab.toQty!!.toString())
//                    if (mTotalQty >= mSlab.fromQty!!.toDouble() && mTotalQty <= mSlab.toQty!!.toDouble()) {
//                        var mApPromo = mPromo
//                        mApPromo.isApply = "0"
//                        mApPromo.offerQty = mSlab.offerQty!!.toString()
//                        mApPromo.offerUom = mSlab.offerUOM!!.toString()
//                        arrApplyPromo.add(mApPromo)
////                        offerQty = mSlab.offerQty!!.toString()
////                        offerUOMId = mSlab.offerUOM!!.toString()
//                        //  isPromo = true
//                        Log.e("isPromo", "true")
//                        break
//                    }
//                }
//            } else {
//
//                if (mPromo.orderType == "Any") {
//                    var mIsApply = false
//                    for (i in saleItemsList!!) {
//
//                        var itemIndex = saleItemsList!!.indexOf(i)
//                        for (mItem in mPromo!!.orderItem) {
//
//                            if (i.id.toString() == mItem.id.toString()) {
//
//                                var orderUom = mItem.itemSelectUOM[0]
//
//                                var qty = 0.0
//                                for (mUom in i.itemSelectUOM) {
//                                    if (mUom.uomId == orderUom.uomId) {
//                                        qty = mUom.uomQty.toDouble()
//                                        if (qty >= orderUom.uomQty.toDouble()) {
//                                            mIsApply = true
//                                            break
//                                        }
//                                    }
//                                }
//                                if (mIsApply) {
//                                    break
//                                }
//                            }
//                        }
//
//                        if (mIsApply) {
//                            var mApPromo = mPromo
//                            mApPromo.isApply = "0"
//                            mApPromo.offerQty = "0"
//                            mApPromo.offerUom = ""
//                            arrApplyPromo.add(mApPromo)
//                        }
//                    }
//                } else {
//
//                    var mAllOrderItem: ArrayList<ItemData.Data> = ArrayList()
//                    for (i in saleItemsList!!) {
//                        for (mItem in mPromo!!.orderItem) {
//                            if (i.id.toString() == mItem.id.toString()) {
//
//                                var orderUom = mItem.itemSelectUOM[0]
//
//                                var qty = 0.0
//                                for (mUom in i.itemSelectUOM) {
//                                    if (mUom.uomId == orderUom.uomId) {
//                                        qty += mUom.uomQty.toDouble()
//                                    }
//                                }
//                                if (qty >= orderUom.uomQty.toDouble()) {
//                                    mAllOrderItem.add(i)
//                                    break
//                                }
//                            }
//                        }
//                    }
//
//                    if (mAllOrderItem.size == mPromo.orderItem.size) {
//                        var mApPromo = mPromo
//                        mApPromo.isApply = "0"
//                        mApPromo.offerQty = "0"
//                        mApPromo.offerUom = ""
//                        arrApplyPromo.add(mApPromo)
//                    }
//                }
//
//            }
//        }
//
//        Log.e("Apply size", arrApplyPromo.size.toString())
//        if (arrApplyPromo.size > 0) {
//            isPromo = true
//        }
//        return isPromo
//    }

    private fun setActivity() {

        val mVisited = VisitedList()
        mVisited.customerId = customerObj!!.userDetail.id.toString()
        mVisited.customerCode = customerCode
        mVisited.activityName = "invoice"
        mVisited.activityAction = ""
        mVisited.startTime = Utils.get24CurrentTime()
        mVisited.endTime = ""
        activityId = Utils.getVisitActivityNo(customerObj!!.userDetail.id.toString(), "1")
        mVisited.activityId = activityId
        visitId = db!!.getCustomerVisitId(customerObj!!.userDetail.id.toString())
        mVisited.visitedOffId = visitId

        db!!.insertVisitedCustomerActivity(mVisited)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_load, menu)
        val color: Int
        color = resources.getColor(R.color.colorWhite)

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
            /* R.id.action_scan -> {
                 startActivity(
                     Intent(this, SimpleScannerActivity::class.java)
                 )
                 return true
             }*/
            R.id.action_scan -> {
                var intent = Intent(applicationContext, SimpleScannerActivity::class.java)
                startActivityForResult(intent, scanCode)
                return true
            }

            android.R.id.home -> {
                var endTime = Utils.getCurrentTime()
                var action = "Invoice not created"
                db!!.updateCustomerVisitActivity(endTime, action, visitId!!, activityId!!)
                finish()
                return true
            }
        }
        return false
    }

    private fun setUpRecycler() {
        binding.rvSalesList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        if (isCouponSales == "1") {
            mAdapter = OrderRequestItemAdapter(this, allItemsList!!, "SaleCoupon")
        } else {
            mAdapter = OrderRequestItemAdapter(this, allItemsList!!, "Sale")
        }

        binding.rvSalesList.adapter = mAdapter
    }

    override fun onItemsClick(position: Int, tag: String) {
        if (tag == "dialog") {
            itemPosition = getItemPosition(position)
            isScan = false
            openQuantityDialog()
        }
    }

    fun getItemPosition(itemId: Int): Int {
        var position = 0
        itemPositionId = itemId
        for (mItem in allItemsList!!) {
            if (mItem.id == itemId) {
                position = allItemsList!!.indexOf(mItem)
                break
            }
        }
        return position
    }


    private fun openQuantityDialog() {
        if (allItemsList!![itemPosition].itemUOM == null)
            allItemsList!![itemPosition].itemUOM = ArrayList()

        if (isCouponSales == "1") {
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
                    this.putExtra("type", "sales")
                    this.putExtra("lobCode", lobCode)
                    this.putExtra("isCoupon", isCouponSales)
                    this.putExtra("isReSale", allItemsList!![itemPosition].itemWeight)
                },
                intentCode
            )
        } else {
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
                    this.putExtra("type", "sales")
                    this.putExtra("lobCode", lobCode)
                    this.putExtra("isCoupon", isCouponSales)
                    this.putExtra("isReSale", allItemsList!![itemPosition].itemWeight)
                },
                intentCode
            )
        }

    }

    fun getCountSerial() {
        if (singleScan == "1") {
            totalCountText!!.setText("1")
        } else {
            var fromserail = fromScanText!!.text.toString().toLong()
            var toSerial = toScanText!!.text.toString().toLong()

            var diff = (toSerial - fromserail) + 1
            totalCountText!!.setText(diff.toString())
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == scanCode) {
            if (resultCode == Activity.RESULT_OK) {
                var id = data!!.getStringExtra("scanid")
                Log.e("scanid", id!!)

                for (i in 0 until allItemsList!!.size) {
                    if (allItemsList!![i].barcode.equals(id)) {
                        itemPosition = i
                        openQuantityDialog()
                    }
                }

                isScan = true
            }
        } else if (requestCode == scanFRCode) {
            if (resultCode == Activity.RESULT_OK) {
                var id = data!!.getStringExtra("scanid")
                Log.e("scanid", id!!)

                var isScanVi = db!!.getIsValidCoupon(customerObj!!.user_id.toString(), id!!)

                if (isScanVi == "1") {
                    if (isFromQRClick) {
                        fromScanText!!.setText(id!!)
                        if (singleScan == "1") {
                            getCountSerial()
                        }
                    } else {
                        toScanText!!.setText(id!!)
                        getCountSerial()
                    }
                } else {
                    Utils.displayAlert(this, "Invalid Serial please scan correct serial number")
                }

            }
        } else
            if (requestCode == intentCode) {
                if (resultCode == Activity.RESULT_OK) {

                    allItemsList!![itemPosition].itemSelectUOM = emptyList()
                    allItemsList!![itemPosition].itemExistUOM = emptyList()

                    val strObj = data!!.getStringExtra("uomList")
                    val listOfUomType: Type =
                        object : TypeToken<List<UomData?>?>() {}.type

                    var mList =
                        Gson().fromJson(strObj, listOfUomType) as ArrayList<UomData>

                    for (itm in allItemsList!!.indices) {
                        if (itemPositionId == allItemsList!![itm].id) {
                            if (mList[0].uomQty.toDouble() > 0) {
                                allItemsList!![itm].itemSelectUOM = mList
                                allItemsList!![itemPosition].itemExistUOM = mList
                            }
                            break
                        }
                    }

//                    allItemsList!![itemPosition].itemExistUOM = mList
//                    allItemsList!![itemPosition].itemSelectUOM = mList
                    if (itemPosition > 0) {
                        val toMove = allItemsList!![itemPosition]
                        allItemsList!!.set(itemPosition, allItemsList!![itemPosition - 1])
                        allItemsList!!.set(itemPosition - 1, toMove)
                    }

                    binding.rvSalesList.adapter!!.notifyDataSetChanged()
                    getTotal()
                }
            } else if (requestCode == filterCode) {
                if (resultCode == Activity.RESULT_OK) {
                    if (data!!.getStringExtra("isClear") != null) {
                        allItemsList!!.clear()
                        allItemsList = db!!.getAllFreshSaleItem()
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

                        allItemsList!!.clear()
                        setUpRecycler()
                    }
                }
            }
    }

    override fun onResume() {
        super.onResume()

        if (isSaleCreate) {
            isSaleCreate = false
            CustomerDetailActivity.completedTask++
            var endTime = Utils.getCurrentTime()
            var action = "Invoice created"
            db!!.updateCustomerVisitActivity(endTime, action, visitId!!, activityId!!)
            finish()
        }

    }

    override fun onBackPressed() {
        if (!isSaleCreate) {
            db!!.deleteVisitActivity(activityId!!)
        } else {
            isSaleCreate = false
            CustomerDetailActivity.completedTask++
            var endTime = Utils.get24CurrentTime()
            var action = "Invoice created"
            db!!.updateCustomerVisitActivity(endTime, action, visitId!!, activityId!!)
        }
        super.onBackPressed()
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


    override fun onData(scanDataCollection: ScanDataCollection?) {
        if (scanDataCollection != null && scanDataCollection.result == ScannerResults.SUCCESS) {
            val scanData: java.util.ArrayList<ScanDataCollection.ScanData> =
                scanDataCollection.scanData
            for (data in scanData) {
                val dataString = data.data
                AsyncDataUpdate(this@SalesActivity)
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

    fun getItemId(barcode: String): Int {

        var itemId = 0
        itemPosition = -1
        for (i in allItemsList!!) {
            if (i.barcode == barcode) {
                itemId = i.id
                break
            }
        }

        return itemId
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

    override fun onOpened(p0: EMDKManager?) {
        emdkManager = p0

        try {
            // Call this method to enable Scanner and its listeners
            initializeScanner()
        } catch (e: ScannerException) {
            e.printStackTrace()
        }


// Toast to indicate that the user can now start scanning
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

    private fun getTotal() {
        var mNet = 0.0
        var mGross = 0.0

        for (mInv in allItemsList!!) {

            for (mUom in mInv.itemSelectUOM) {
                mNet += mUom.uomQty.toDouble()
                mGross += mUom.netAmt.toDouble()
            }
        }

        binding.txtNet!!.text = "" + mNet
        binding.txtGross!!.text = "" + mGross
    }



}