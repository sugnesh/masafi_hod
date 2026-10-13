package com.engear.vansales.activities

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.R
import com.engear.vansales.adapter.HighLightArrayAdapter
import com.engear.vansales.adapter.InputDialogUomAdapter
import com.engear.vansales.adapter.InputDialogUomBatchAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.MyApplication
import com.engear.vansales.common.SharedPreferenceManager
import com.engear.vansales.databinding.ActivityInputOrderQtyDialogBinding
import com.engear.vansales.databinding.RowFocItemBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.localDB.DatabaseHandler
import com.engear.vansales.merchandiser.StoreCheckActivity
import com.engear.vansales.model.*
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.math.RoundingMode
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class InputOrderQtyDialogActivity : Activity(), OnItemClickListener {
    lateinit var binding: ActivityInputOrderQtyDialogBinding
    var type: String? = null
    var itemCode: String? = null
    var itemId: String? = null
    var customerId: String = ""
    var mSelectUOMId: String = ""
    var mSelectUOMUPC: String = ""
    var mSelectUOMPrice: String = ""
    var mSelectUOMExcise: String = ""
    var mSelectUOMName: String = ""
    var mPriceUOM: String = ""
    var mLowerPrice = 0.0
    var mAvailableQty: String = ""
    var mAvailableUom: String = ""
    var mItem: ItemData.Data? = null
    var mItemDisplayUOM: ItemData.Data.ItemMainUOM? = null
    var spUomList: MutableList<String>? = ArrayList()
    var uomIdList: MutableList<Int>? = ArrayList()
    var uomTypeList: MutableList<String>? = ArrayList()
    var db: DatabaseHandler? = null
    var mUOMList: ArrayList<ItemData.Data.ItemMainUOM> = ArrayList()
    var mSelectUOMList: ArrayList<UomData> = ArrayList()
    var orderItemsBatchList_Return: ArrayList<CustomerItemReturnData.DataCustomerItemReturn>? =
        ArrayList()
    var reason: String = ""
    var isBad: String = ""
    var isResaleItem = "0"
    var expiryDate: String = ""
    var invNo: String = ""
    var invAmt: String = ""
    var arrReason: MutableList<String> = ArrayList()
    var mLobId: String = ""
    var customerData: CustomerData.DataCustomer? = null
    var isCustDiscount = false
    var discountData: DiscountOfferData? = null
    var mDisplayUnitId: String = ""
    var isReSale = "0"
    var isExchange = "0"
    var userInfo: UserData? = null
    var mPreferenceManager: SharedPreferenceManager? = null
    var isExcise = "0"
    var exciseAmt = "0"
    var exciseUOMId = ""
    var allLoadBatchItem: ArrayList<BatchdetailsList> = ArrayList()
    var arrSelectedBacth: ArrayList<BatchdetailsList> = ArrayList()
    var strErrormsg = ""
    var channleId = ""
    var subchannleId = ""
    var mRestrictQty = ""
    var mSalesQTy = ""
    var mFOCAdapter: FocItemAdapter? = null
    var isReference = "0"
    var mDisPer = "0.0"
    var mParent = ""
    var isCouponSales = "0"
    private val scanCode = 300
    var isFromQRClick = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setFinishOnTouchOutside(false)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_input_order_qty_dialog)

        val metrics = resources.displayMetrics
        val screenWidth = (metrics.widthPixels * 0.90).toInt()
        val screenHeight = (metrics.heightPixels * 0.90).toInt()

        window.setLayout(screenWidth, screenHeight)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        db = DatabaseHandler(this)
        Utils.logData(
            this@InputOrderQtyDialogActivity,
            "On Input order qty dialog Screen "
        )
        init()
    }

    private fun init() {
        mPreferenceManager = SharedPreferenceManager(this)
        type = intent.getStringExtra("type")
        itemCode = intent.getStringExtra("item_code")
        itemId = intent.getStringExtra("item_id")
        mAvailableQty = intent.getStringExtra("available_qty")!!
        mAvailableUom = intent.getStringExtra("available_uom")!!

        if (intent.getStringExtra("cust_id") != null) {
            customerId = intent.getStringExtra("cust_id")!!
            customerData = db!!.getCustomerDetailFromCustId(customerId!!)
            mParent = db!!.getCustomerParentCode(customerId)
            Log.e("Parent Code", mParent)
            if (customerData != null) {
                if (customerData!!.channel_id != null) {
                    channleId = customerData!!.channel_id!!
                }

                if (customerData!!.customer_category_id != null) {
                    subchannleId = customerData!!.customer_category_id!!
                }
            }
        }

        if (intent.getStringExtra("lobCode") != null) {
            mLobId = intent.getStringExtra("lobCode")!!
        }

        if (intent.getStringExtra("isCoupon") != null) {
            isCouponSales = intent.getStringExtra("isCoupon")!!
        }

        userInfo = mPreferenceManager!!.getUserInfo()

        val strObj = intent.getStringExtra("uom_list")
        val listOfUomType: Type = object : TypeToken<List<UomData?>?>() {}.type

        mSelectUOMList!!.addAll(0, Gson().fromJson(strObj, listOfUomType))

        if (type == "goodReturn" || type == "badReturn") {

            if (intent.getStringExtra("isRefence") != null) {
                isReference = intent.getStringExtra("isRefence")!!
            }

            if (mSelectUOMList.size > 0) {
                reason = mSelectUOMList[0].reason
            }

            binding.titleQty.text = "Return Qty :"
            if (intent.getStringExtra("invNo") != null) {
                invNo = intent.getStringExtra("invNo")!!
            }
            if (intent.getStringExtra("invAmt") != null) {
                invAmt = intent.getStringExtra("invAmt")!!
            }

            if (invNo.isNotEmpty()) {
                val strObj = intent.getStringExtra("REuom_list")
                val listOfUomType: Type =
                    object : TypeToken<List<ItemData.Data.ItemMainUOM?>?>() {}.type
                mUOMList!!.addAll(0, Gson().fromJson(strObj, listOfUomType))
            } else {
                if (db!!.getItemUomCOunt(itemId!!) > 1) {
                    mUOMList =
                        db!!.getItemOrderPrimarySaleUOM(
                            itemId!!,
                            customerId,
                            mParent,
                            channleId,
                            subchannleId
                        )
                } else {
                    mUOMList = db!!.getItemOrderSaleUOM(itemId!!, customerId, mParent, channleId)
                }

            }
            setUOMPrice()
            //mUOMList = db!!.getItemUOM(itemId!!)
        } else if (type == "load" || type == "Unload") {
            if (intent.getStringExtra("isReSale") != null) {
                isReSale = intent.getStringExtra("isReSale")!!
            }
            if (db!!.getItemUomCOunt(itemId!!) > 1) {
                mUOMList = db!!.getItemPrimaryUOM(itemId!!)
            } else {
                mUOMList = db!!.getItemUOM(itemId!!)
            }

        } else {

            if (intent.getStringExtra("isReSale") != null) {
                isReSale = intent.getStringExtra("isReSale")!!
            }

            if (type == "sales") {

                if (intent.getStringExtra("isExchange") != null) {
                    isExchange = intent.getStringExtra("isExchange")!!
                }
                if (isExchange == "1") {
                    val strObj = intent.getStringExtra("REuom_list")
                    val listOfUomType: Type =
                        object : TypeToken<List<ItemData.Data.ItemMainUOM?>?>() {}.type
                    mUOMList!!.addAll(0, Gson().fromJson(strObj, listOfUomType))
                } else {
                    if (db!!.getItemUomCOunt(itemId!!) > 1) {
                        mUOMList =
                            db!!.getItemOrderPrimarySaleUOM(
                                itemId!!,
                                customerId,
                                mParent,
                                channleId,
                                subchannleId
                            )
                    } else {
                        mUOMList =
                            db!!.getItemOrderSaleUOM(itemId!!, customerId, mParent, channleId)
                    }
                }

                if (isCouponSales == "1") {
                    binding.lytNormal!!.visibility = View.GONE
                    binding.lytCoupon!!.visibility = View.VISIBLE
                    setUOMPrice()
                    if (mUOMList.size > 0) {
                        mSelectUOMPrice = mUOMList[0].uomPrice
                        mSelectUOMId = mUOMList[0].uomId
                        mSelectUOMUPC = mUOMList[0].uomPrice
                        mSelectUOMName = mUOMList[0].uom.name
                    }
                } else {
                    binding.lytNormal!!.visibility = View.VISIBLE
                    binding.lytCoupon!!.visibility = View.GONE
                    allLoadBatchItem = db!!.getAllVanBatchItemByID(itemId!!)
                    setBatches()
                    setUOMPrice()
                }
            } else if (type == "delivery") {


                if (intent.getStringExtra("isExchange") != null) {
                    isExchange = intent.getStringExtra("isExchange")!!
                }
                if (isExchange == "1") {
                    val strObj = intent.getStringExtra("REuom_list")
                    val listOfUomType: Type =
                        object : TypeToken<List<ItemData.Data.ItemMainUOM?>?>() {}.type
                    mUOMList!!.addAll(0, Gson().fromJson(strObj, listOfUomType))
                } else {
                    if (db!!.getItemUomCOunt(itemId!!) > 1) {
                        mUOMList =
                            db!!.getItemOrderPrimarySaleUOM(
                                itemId!!,
                                customerId,
                                mParent,
                                channleId,
                                subchannleId
                            )
                    } else {
                        mUOMList =
                            db!!.getItemOrderSaleUOM(itemId!!, customerId, mParent, channleId)
                    }
                }

                allLoadBatchItem = db!!.getAllVanBatchItemByID(itemId!!)
                setBatches()
                setUOMPrice()

                if (mSelectUOMList.size > 0) {
                    for (mUom in mUOMList!!) {
                        var mIndex = mUOMList!!.indexOf(mUom)
                        for (mSeleUOM in mSelectUOMList!!) {
                            if (mUom.uomId == mSeleUOM.uomId) {
                                mUOMList[mIndex].uomPrice = mSeleUOM.uomPrice
                            }
                        }
                    }
                }

            } else {
                if (db!!.getItemUomCOunt(itemId!!) > 1) {
                    mUOMList =
                        db!!.getItemOrderPrimarySaleUOM(
                            itemId!!,
                            customerId,
                            mParent,
                            channleId,
                            subchannleId
                        )
                } else {
                    mUOMList = db!!.getItemOrderSaleUOM(itemId!!, customerId, mParent, channleId)
                }
                setUOMPrice()
            }

            if (type == "sales" || type == "order") {

                customerData = db!!.getCustomerDetailFromCustId(customerId!!)

                var isCUstPromo = db!!.isDiscountCUst(customerId!!, itemId!!)
                var isParentPromo = db!!.isDiscountCUst(mParent!!, itemId!!)
                if (isCUstPromo) {
                    var mPlanId = db!!.getCustomerDiscout(customerId!!, itemId!!)
                    discountData = db!!.getCustomerDiscountDetail(mPlanId)

                    if (discountData != null) {
                        if (discountData!!.discountMainType == "1") {
                            if (isExchange == "0") {
                                isCustDiscount = true
                            }
                        }
                    }
                } else if (isParentPromo) {
                    var mPlanId = db!!.getCustomerDiscout(mParent!!, itemId!!)
                    discountData = db!!.getCustomerDiscountDetail(mPlanId)

                    if (discountData != null) {
                        if (discountData!!.discountMainType == "1") {
                            if (isExchange == "0") {
                                isCustDiscount = true
                            }
                        }
                    }
                } else {
                    if (mLobId.isNotEmpty()) {
                        var mLobDetail =
                            db!!.getCustomerLob(mLobId, customerData!!.user_id.toString())

                        var isOtherPromo = db!!.isOtherDiscountPDP(
                            if (mLobDetail!!.region_id != null) mLobDetail!!.region_id.toString()
                            else "",
                            if (mLobDetail!!.sales_organisation_id != null) mLobDetail!!.sales_organisation_id.toString()
                            else "",
                            if (mLobDetail!!.channel_id != null) mLobDetail!!.channel_id.toString()
                            else "",
                            if (mLobDetail!!.customer_category_id != null) mLobDetail!!.customer_category_id.toString()
                            else "",
                            userInfo!!.data.userInfo.salesmanInfo.routeId!!.toString(),
                            itemId!!
                        )

                        if (isOtherPromo) {
                            var mPlanId = db!!.otherDiscountPDPPlanId(
                                if (mLobDetail!!.region_id != null) mLobDetail!!.region_id.toString()
                                else "",
                                if (mLobDetail!!.sales_organisation_id != null) mLobDetail!!.sales_organisation_id.toString()
                                else "",
                                if (mLobDetail!!.channel_id != null) mLobDetail!!.channel_id.toString()
                                else "",
                                if (mLobDetail!!.customer_category_id != null) mLobDetail!!.customer_category_id.toString()
                                else "",
                                userInfo!!.data.userInfo.salesmanInfo.routeId!!.toString(),
                                itemId!!
                            )
                            discountData = db!!.getCustomerDiscountDetail(mPlanId)
                            if (discountData != null) {
                                if (discountData!!.discountMainType == "1") {
                                    if (isExchange == "0") {
                                        isCustDiscount = true
                                    }
                                }
                            }
                        }
                    } else {
                        var isOtherPromo = db!!.isOtherDiscountPDP(
                            if (customerData!!.region_id != null) customerData!!.region_id.toString()
                            else "",
                            if (customerData!!.sales_organisation_id != null) customerData!!.sales_organisation_id.toString()
                            else "",
                            if (customerData!!.channel_id != null) customerData!!.channel_id.toString()
                            else "",
                            if (customerData!!.customer_category_id != null) customerData!!.customer_category_id.toString()
                            else "",
                            userInfo!!.data.userInfo.salesmanInfo.routeId!!.toString(),
                            itemId!!
                        )

                        if (isOtherPromo) {
                            var mPlanId = db!!.otherDiscountPDPPlanId(
                                if (customerData!!.region_id != null) customerData!!.region_id.toString()
                                else "",
                                if (customerData!!.sales_organisation_id != null) customerData!!.sales_organisation_id.toString()
                                else "",
                                if (customerData!!.channel_id != null) customerData!!.channel_id.toString()
                                else "",
                                if (customerData!!.customer_category_id != null) customerData!!.customer_category_id.toString()
                                else "",
                                userInfo!!.data.userInfo.salesmanInfo.routeId!!.toString(),
                                itemId!!
                            )
                            discountData = db!!.getCustomerDiscountDetail(mPlanId)
                            if (discountData != null) {
                                if (discountData!!.discountMainType == "1") {
                                    if (isExchange == "0") {
                                        isCustDiscount = true
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        mItem = db!!.getItemDetail(itemCode!!)
        mRestrictQty = db!!.getSalesRestrictItem(itemCode!!)
        mSalesQTy = db!!.getSalesRestrictSalesItem(itemCode!!)
        binding.txtName.text = mItem!!.itemName
        isExcise = mItem!!.is_item_excise.toString()
        exciseAmt = mItem!!.item_excise!!
        exciseUOMId = mItem!!.item_excise_uom_id.toString()
        mSelectUOMExcise = exciseAmt

        if (mUOMList.isNotEmpty()) {
            for (i in mUOMList) {
                if (i!!.uom != null) {
                    spUomList!!.add(i.uom!!.name!!)
                    uomIdList!!.add(i.uom.id!!)
                }
            }
        }

        try {
            binding.etPrice.setText(mUOMList!![0].uomPrice)
        } catch (e: Exception) {
            e.toString()
        }

        when (type) {
            "sales" -> {
                binding.llBatch.visibility = View.GONE

                mDisplayUnitId = db!!.getVanStockItemDIsplayUnit(itemId!!)
                mAvailableUom = db!!.getVanStockItemLowerUnit(itemId!!)

                mAvailableQty = db!!.getVanStockItemQty(itemId!!)

                if (mRestrictQty.isNotEmpty()) {
                    var mPendingQty = mRestrictQty.toDouble() - mSalesQTy.toDouble()
                    binding.tvSalesAllocaQty!!.text = "Promo Allocation Pending Qty: $mPendingQty"
                    binding.tvSalesAllocaQty!!.visibility = View.VISIBLE
                }

                mItemDisplayUOM = db!!.getLowerUOM(mAvailableUom, itemId!!)

//                var deliveryQty = 0.0
//                if (db!!.checkDelvieryItemStockExist()) {
//                    deliveryQty = db!!.getItemStock(itemId!!)
//                }
//
//                if (deliveryQty > mAvailableQty.toDouble()) {
//                    var mQty = 0.0
//                    mAvailableQty = mQty.toString()
//                } else {
//                    var mQty = mAvailableQty.toDouble() - deliveryQty
//                    mAvailableQty = mQty.toString()
//                }

                if (mDisplayUnitId.isEmpty()) {
                    binding.tvAvailableQty.text =
                        "Available Qty: 0"
                } else {
                    if (mDisplayUnitId == mAvailableUom) {
                        var uomQty = Utils.getUOMQty(mItemDisplayUOM!!.uomUPC, mAvailableQty)
                        binding.tvAvailableQty.text =
                            "Available Qty: ${mItemDisplayUOM!!.uom.name} : $uomQty"
                    } else {
                        var uomUPC = db!!.getItemUOMUPC(itemId!!, mDisplayUnitId)
                        var qty = Utils.getUOMQty(uomUPC, mAvailableQty).toDouble()
                        var displayQty = qty.toInt()
                        var totalBaseQty = displayQty * uomUPC.toDouble()
                        var dispayUOMName = db!!.getUOMName(mDisplayUnitId)

                        if (totalBaseQty == mAvailableQty.toDouble()) {
                            binding.tvAvailableQty.text =
                                "Available Qty: $dispayUOMName : $qty"
                        } else {
                            var remain = mAvailableQty.toDouble() - totalBaseQty
                            binding.tvAvailableQty.text =
                                "Available Qty: $dispayUOMName : $displayQty  |  ${mItemDisplayUOM!!.uom.name} : $remain"
                        }
                    }
                }

                if (isCouponSales == "1") {
                    binding.tvAvailableQty.visibility = View.GONE
                }

            }

            "order" -> {
                binding.tvAvailableQty.visibility = View.GONE
                binding.tvAvailableQty.text =
                    "Available Qty: $mAvailableUom : $mAvailableQty"

                if (mRestrictQty.isNotEmpty()) {
                    var mPendingQty = mRestrictQty.toDouble() - mSalesQTy.toDouble()
                    binding.tvSalesAllocaQty!!.text = "Promo Allocation Pending Qty: $mPendingQty"
                    binding.tvSalesAllocaQty!!.visibility = View.VISIBLE
                }
            }

            "load" -> {
                binding.tvAvailableQty.visibility = View.GONE
                binding.etPrice.visibility = View.GONE
                binding.titlePrice.visibility = View.GONE
            }

            "delivery" -> {
                binding.tvAvailableQty.visibility = View.VISIBLE

                arrReason = ArrayList<String>()
                arrReason.add("Less Quantity Required")
                arrReason.add("Less Quantity Available")
                arrReason.add("Load Shortage")
                arrReason.add("Missing Item")


                mDisplayUnitId = db!!.getVanStockItemDIsplayUnit(itemId!!)
                mAvailableUom = db!!.getVanStockItemLowerUnit(itemId!!)

                mAvailableQty = db!!.getVanStockItemQty(itemId!!)
                mItemDisplayUOM = db!!.getLowerUOM(mAvailableUom, itemId!!)

                if (mDisplayUnitId == mAvailableUom) {
                    var uomQty = Utils.getUOMQty(mItemDisplayUOM!!.uomUPC, mAvailableQty)
                    binding.tvAvailableQty.text =
                        "Available Qty: ${mItemDisplayUOM!!.uom.name} : $uomQty"
                } else {
                    var uomUPC = db!!.getItemUOMUPC(itemId!!, mDisplayUnitId)
                    var qty = Utils.getUOMQty(uomUPC, mAvailableQty).toDouble()
                    var displayQty = qty.toInt()
                    var totalBaseQty = displayQty * uomUPC.toDouble()
                    var dispayUOMName = db!!.getUOMName(mDisplayUnitId)

                    if (totalBaseQty == mAvailableQty.toDouble()) {
                        binding.tvAvailableQty.text =
                            "Available Qty: $dispayUOMName : $qty"
                    } else {
                        var remain = mAvailableQty.toDouble() - totalBaseQty
                        binding.tvAvailableQty.text =
                            "Available Qty: $dispayUOMName : $displayQty  |  ${mItemDisplayUOM!!.uom.name} : $remain"
                    }
                }

            }

            "goodReturn" -> {

                mAvailableQty = intent.getStringExtra("available_qty")!!
                binding.llBatch.visibility = View.GONE
                arrReason = db!!.getGoodReason()

                binding.llReason.visibility = View.VISIBLE
                binding.etExpiry.visibility = View.VISIBLE

                if (isReference == "1") {
                    binding.llInvRef.visibility = View.VISIBLE
                    binding.titlePrice.visibility = View.GONE
                    binding.etPrice.visibility = View.GONE
                } else {
                    binding.llInvRef.visibility = View.GONE
                }
                binding.etBatch.visibility = View.GONE
                binding.tvAvailableQty.visibility = View.GONE
                binding.etBatch.visibility = View.GONE
            }

            "badReturn" -> {

                mAvailableQty = intent.getStringExtra("available_qty")!!
                binding.llBatch.visibility = View.GONE
                arrReason = db!!.getBadReason()
                binding.llReason.visibility = View.VISIBLE
                if (isReference == "1") {
                    binding.llInvRef.visibility = View.VISIBLE
                    binding.titlePrice.visibility = View.GONE
                    binding.etPrice.visibility = View.GONE
                } else {
                    binding.llInvRef.visibility = View.GONE
                }
                binding.etExpiry.visibility = View.VISIBLE
                binding.tvAvailableQty.visibility = View.GONE
                binding.etBatch.visibility = View.GONE
            }

            "Unload" -> {

                binding.etPrice.visibility = View.GONE
                binding.titlePrice.visibility = View.GONE
                var mUType = ""
                if (intent.getStringExtra("unloadType") != null) {
                    mUType = intent.getStringExtra("unloadType")!!
                }

                binding.llBatch.visibility = View.GONE
                if (mUType == "5") {
                    arrReason = db!!.getBadReason()
                    binding.llReason.visibility = View.VISIBLE
                    binding.etComment!!.visibility = View.VISIBLE
                } else {
                    arrReason = db!!.getBadReason()
                    binding.llReason.visibility = View.GONE
                }

                mDisplayUnitId = intent.getStringExtra("display_uom")!!

                if (isReSale == "1") {
                    mAvailableQty = db!!.getVanStockReSaleItemQty(itemId!!)
                } else {
                    mAvailableQty = db!!.getVanStockItemQty(itemId!!)
                }
                mItemDisplayUOM = db!!.getLowerUOM(mAvailableUom, itemId!!)

                if (mDisplayUnitId == mAvailableUom) {
                    var uomQty = Utils.getUOMQty(mItemDisplayUOM!!.uomUPC, mAvailableQty)
                    binding.tvAvailableQty.text =
                        "Available Qty: ${mItemDisplayUOM!!.uom.name} : $uomQty"
                } else {
                    var uomUPC = db!!.getItemUOMUPC(itemId!!, mDisplayUnitId)
                    var qty = Utils.getUOMQty(uomUPC, mAvailableQty).toDouble()
                    var displayQty = qty.toInt()
                    var totalBaseQty = displayQty * uomUPC.toDouble()
                    var dispayUOMName = db!!.getUOMName(mDisplayUnitId)

                    if (totalBaseQty == mAvailableQty.toDouble()) {
                        binding.tvAvailableQty.text =
                            "Available Qty: $dispayUOMName : $qty"
                    } else {
                        var remain = mAvailableQty.toDouble() - totalBaseQty
                        binding.tvAvailableQty.text =
                            "Available Qty: $dispayUOMName : $displayQty  |  ${mItemDisplayUOM!!.uom.name} : $remain"
                    }
                }
            }
        }

        binding.btnConfirm.setOnClickListener {

            if (isCouponSales == "1") {

                var discountAmt = 0F
                var discountPer = "0"
                var mUOMPrice = 0F
                var netAmt = 0F
                var vatAmt = 0F
                var totalAmt = 0F
                if (isExcise == "1") {
                    mUOMPrice =
                        Utils.twoDigit(mSelectUOMPrice.toFloat() + mSelectUOMExcise.toFloat())
                } else {
                    mUOMPrice = mSelectUOMPrice.toFloat()
                }
                var grossPrice = binding.etQty.text.toString()
                    .toFloat() * mUOMPrice

                netAmt = Utils.twoDigit((grossPrice - discountAmt))
                var mVat = Utils.getVatAmt(netAmt.toDouble(), 5.0)
                vatAmt = Utils.twoDigit(mVat.toFloat())
                totalAmt = Utils.twoDigit((netAmt + vatAmt))


                mSelectUOMList!!.add(
                    UomData(
                        mItem!!.id.toString(),
                        mItem!!.itemCode,
                        mItem!!.itemName,
                        mSelectUOMId,
                        mSelectUOMName,
                        mSelectUOMUPC,
                        binding.etQty.text.toString(),
                        mSelectUOMPrice,
                        grossPrice.toString(),
                        discountPer,
                        discountAmt = discountAmt.toString(),
                        netAmt = netAmt.toString(),
                        vatAmt = vatAmt.toString(),
                        totalAmt = totalAmt.toString(),
                        promotion_Id = binding.etFromSerial!!.text.toString(),
                        discount_Id = binding.etToSerial!!.text.toString(),
                        reason = reason,
                        expiryDate = expiryDate,
                        invAmount = invAmt,
                        invNo = invNo,
                        isBad = isBad,
                        item_excise = mSelectUOMExcise,
                        batchs = arrSelectedBacth
                    )
                )
                val returnIntent = Intent()
                returnIntent.putExtra("uomList", Gson().toJson(mSelectUOMList))
                returnIntent.putExtra("isReSale", isResaleItem)
                setResult(RESULT_OK, returnIntent)
                finish()
            } else {

                when {
                    mSelectUOMList!!.isNotEmpty() -> {

                        var isSameUom = false
                        if (isValidQTY()) {
                            isSameUom = checkUOMExist()
                            if (isSameUom) {
                                Utils.displayAlert(
                                    mContext = this,
                                    message = "UOM is already added"
                                )
                            } else {
                                addSelectUOM()
                            }
                        } else {
                            isSameUom = true
                        }

                        if (!isSameUom) {
                            val returnIntent = Intent()
                            returnIntent.putExtra("uomList", Gson().toJson(mSelectUOMList))
                            returnIntent.putExtra("isReSale", isResaleItem)
                            setResult(RESULT_OK, returnIntent)
                            finish()
                        }
                    }

                    isValidQTY() -> {
                        addSelectUOM()
                        val returnIntent = Intent()
                        returnIntent.putExtra("uomList", Gson().toJson(mSelectUOMList))
                        returnIntent.putExtra("isReSale", isResaleItem)
                        setResult(RESULT_OK, returnIntent)
                        finish()
                    }
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            if (type == "badReturn" || type == "goodReturn") {
                db!!.deleteCustomerReturnItem(itemId!!)
                mSelectUOMList.clear()
            }
            finish()
        }

        binding.lytFromQR!!.setOnClickListener {
            isFromQRClick = true
            var intent = Intent(applicationContext, SimpleScannerActivity::class.java)
            startActivityForResult(intent, scanCode)
        }

        binding.lytToQR!!.setOnClickListener {
            isFromQRClick = false
            var intent = Intent(applicationContext, SimpleScannerActivity::class.java)
            startActivityForResult(intent, scanCode)
        }

        binding.ivAddQty.setOnClickListener {

            if (isValidQTY()) {

                if (checkUOMExist()) {
                    Utils.displayAlert(mContext = this, message = "UOM is already added")
                } else {
                    addSelectUOM()
                    binding.rvQtyList.adapter!!.notifyDataSetChanged()

                    binding.etQty.setText("")

                    binding.llUom.visibility = View.VISIBLE
                }

            }

        }

        binding.etExpiry.setOnClickListener {
            if (type == "goodReturn" || type == "badReturn") {
                if (binding.etQty!!.text.toString() == "") {
                    binding.tvErrorMsg!!.text = "Enter Quantity."
                } else {
                    binding.tvErrorMsg!!.text = ""
                    showDateDialog()
                }
            }
        }
        if (type == "goodReturn" || type == "badReturn") {
            setUpRecyclerReturn()
        } else {
            setUpRecycler()
        }

        setUpSpinner()

        binding.etQty.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //   selectBatch(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding.invoiceSpinner.setOnClickListener {
            if (isRTValid()) {
                getInvoiceData()
            }

        }
    }

    private fun selectBatch(mQy: String) {
        arrSelectedBacth.clear()
        arrSelectedBacth = ArrayList()
        if (mQy.equals("", ignoreCase = true)) {

        } else if (mQy.toDouble() > 0) {

            var mUomQty = mSelectUOMUPC.toDouble() * mQy.toDouble()

            var remaingQTy = mUomQty
            for (mBatch in allLoadBatchItem) {
                var mBatchdetailsList: BatchdetailsList? = null
                if (remaingQTy > 0) {
                    if (mBatch.qty.toDouble() < remaingQTy) {
                        mBatchdetailsList = BatchdetailsList(
                            mBatch.batchId,
                            mBatch.batchNumber,
                            mBatch.expiryDate,
                            mBatch.qty,
                            mLowerPrice.toString(),
                            "",
                            ""
                        )
                        remaingQTy -= mBatch.qty.toDouble()
                    } else if (mBatch.qty.toDouble() > remaingQTy) {
                        mBatchdetailsList = BatchdetailsList(
                            mBatch.batchId,
                            mBatch.batchNumber,
                            mBatch.expiryDate,
                            remaingQTy.toString(),
                            mLowerPrice.toString(),
                            "",
                            ""
                        )
                        remaingQTy -= remaingQTy
                    } else {
                        mBatchdetailsList = BatchdetailsList(
                            mBatch.batchId,
                            mBatch.batchNumber,
                            mBatch.expiryDate,
                            mBatch.qty,
                            mLowerPrice.toString(),
                            "",
                            ""
                        )
                        remaingQTy -= mBatch.qty.toDouble()
                    }
                    arrSelectedBacth.add(mBatchdetailsList!!)
                } else {
                    break
                }
            }
        }
    }

    private fun setUOMPrice() {

        var mLowerUnit = db!!.getItemLowerUnit(itemId!!)
        for (mUOM in mUOMList) {
            if (mUOM.uomPrice.toDouble() > 0) {
                mPriceUOM = mUOM.uomId
                if (mLowerUnit == mUOM.uomId) {
                    mLowerPrice = mUOM.uomPrice.toDouble()
                } else {
                    mLowerPrice = Utils.getUOMLowerPrice(
                        db!!.getItemUOMUPC(itemId!!, mUOM.uomId),
                        mUOM.uomPrice
                    ).toDouble()
                }
                break
            }
        }

        for (mUOM in mUOMList) {
            var index = mUOMList.indexOf(mUOM)
            if (mUOM.uomPrice.toDouble() > 0) {

            } else {
                var mUOMPrice = mUOM.uomUPC.toDouble() * mLowerPrice
                mUOMList[index].uomPrice = mUOMPrice.toString()
            }
        }

    }

    private fun setBatches() {
        allLoadBatchItem.sortWith(Comparator { o1, o2 ->
            val format: DateFormat =
                SimpleDateFormat("yyyy-MM-dd")
            var mm: Date? = null
            var nn: Date? = null
            try {
                mm = format.parse(o1.expiryDate)
                nn = format.parse(o2.expiryDate)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            mm!!.compareTo(nn)
        })
    }

    private fun showDateDialog() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = android.app.DatePickerDialog(
            this,
            android.app.DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                binding.etExpiry.text = "$year-" + String.format(
                    "%02d",
                    monthOfYear + 1
                ) + "-" + String.format("%02d", dayOfMonth)

//                if (type == "goodReturn" || type == "badReturn") {
//                    if (Utils.isNetworkAvailable(this@InputOrderQtyDialogActivity)) {
//                        getCustomerReturn()
//                    } else {
//                        binding.tvErrorMsg!!.text = "Network not available!"
//                    }
//                }


            },
            year,
            month,
            day
        )

        if (type == "badReturn") {
            dpd.datePicker.maxDate = c.timeInMillis
        } else {
            dpd.datePicker.minDate = c.timeInMillis
        }
        dpd.show()
    }

    private fun isRTValid(): Boolean {
        if (binding.etQty.text.toString().isEmpty()
        ) {
            Toast.makeText(this, "Please Enter Item Quantity!", Toast.LENGTH_SHORT)
                .show()
            return false
        } else {
            expiryDate = binding.etExpiry.text.toString()

            if (expiryDate.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please enter expiry date!",
                    Toast.LENGTH_SHORT
                )
                    .show()
                return false
            }
        }

        return true
    }

    private fun isValidQTY(): Boolean {
        if (binding.etQty.text.toString().isEmpty() || binding.etPrice.text.toString()
                .isEmpty()
        ) {
            Toast.makeText(this, "Please Enter Item Quantity or Price!", Toast.LENGTH_SHORT)
                .show()
            return false
        } else if (type == "order") {
            if (mRestrictQty.isNotEmpty()) {
                var mTotalSalesQty = mSalesQTy.toDouble() + binding.etQty.text.toString().toDouble()
                if (mTotalSalesQty > mRestrictQty.toDouble()) {
                    Toast.makeText(
                        this,
                        "You can not exceed qty more than Allocated qty!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return false
                }
            }
        } else if (type == "sales") {

            if (mRestrictQty.isNotEmpty()) {
                var mTotalSalesQty = mSalesQTy.toDouble() + binding.etQty.text.toString().toDouble()
                if (mTotalSalesQty > mRestrictQty.toDouble()) {
                    Toast.makeText(
                        this,
                        "You can not exceed qty more than Allocated qty!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return false
                }
            } else {
                var uomLowerQty =
                    Utils.getUOMLowerQty(mSelectUOMUPC, binding.etQty.text.toString())

                var existLowerQty = getExistUOMQTY()
                var totalUOMQty = uomLowerQty.toDouble() + existLowerQty
                if (totalUOMQty > mAvailableQty.toDouble()) {
                    Toast.makeText(
                        this,
                        "You can not enter qty more than available Qty!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return false
                }
            }


        } else if (type == "delivery") {

            var uomLowerQty =
                Utils.getUOMLowerQty(mSelectUOMUPC, binding.etQty.text.toString())

            var existLowerQty = getExistUOMQTY()
            var totalUOMQty = uomLowerQty.toDouble() + existLowerQty
            if (totalUOMQty > mAvailableQty.toDouble()) {
                Toast.makeText(
                    this,
                    "You can not enter qty more than available Qty!",
                    Toast.LENGTH_SHORT
                )
                    .show()
                return false
            }
        } else if (type == "Unload") {

            var uomLowerQty =
                Utils.getUOMLowerQty(mSelectUOMUPC, binding.etQty.text.toString())

            var existLowerQty = getExistUOMQTY()
            var totalUOMQty = uomLowerQty.toDouble() + existLowerQty
            if (totalUOMQty > mAvailableQty.toDouble()) {
                Toast.makeText(
                    this,
                    "You can not enter qty more than available Qty!",
                    Toast.LENGTH_SHORT
                )
                    .show()
                return false
            }
        } else if (type == "goodReturn") {

            expiryDate = binding.etExpiry.text.toString()

            var uomLowerQty =
                Utils.getUOMLowerQty(mSelectUOMUPC, binding.etQty.text.toString())

//            var existLowerQty = getExistUOMQTY()
//            var totalUOMQty = uomLowerQty.toDouble() + existLowerQty
//            if (totalUOMQty > mAvailableQty.toDouble()) {
//                Toast.makeText(
//                    this,
//                    "You can not enter qty more than invoice Qty!",
//                    Toast.LENGTH_SHORT
//                )
//                    .show()
//                return false
//            } else
            if (expiryDate.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please enter expiry date!",
                    Toast.LENGTH_SHORT
                )
                    .show()
                return false
            } else if (invNo.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please select invoice!",
                    Toast.LENGTH_SHORT
                )
                    .show()
                return false
            }

        } else if (type == "badReturn") {

            expiryDate = binding.etExpiry.text.toString()

            var uomLowerQty =
                Utils.getUOMLowerQty(mSelectUOMUPC, binding.etQty.text.toString())

//            var existLowerQty = getExistUOMQTY()
//            var totalUOMQty = uomLowerQty.toDouble() + existLowerQty
//            if (totalUOMQty > mAvailableQty.toDouble()) {
//                Toast.makeText(
//                    this,
//                    "You can not enter qty more than invoice Qty!",
//                    Toast.LENGTH_SHORT
//                )
//                    .show()
//                return false
//            } else
            if (expiryDate.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please enter expiry date!",
                    Toast.LENGTH_SHORT
                )
                    .show()
                return false
            } else if (invNo.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please select invoice!",
                    Toast.LENGTH_SHORT
                )
                    .show()
                return false
            }
        }
        return true
    }

    private fun checkUOMExist(): Boolean {

        var isExist = false
        for (mUom in mSelectUOMList) {
            if (mUom.uomType == mSelectUOMName) {
                isExist = true
                break
            }
        }
        return isExist
    }

    private fun getExistUOMQTY(): Double {

        var totalQty = 0.0
        for (mUOM in mSelectUOMList) {
            totalQty += Utils.getUOMLowerQty(mUOM.uomUPC, mUOM.uomQty).toDouble()
        }

        return totalQty
    }

    private fun addReturnSelectUOM(mCustomerReturn: List<CustomerItemReturnData.DataCustomerItemReturn>) {

        var arrSelectedBacth: ArrayList<BatchdetailsList> = ArrayList()
        for (mCBAtch in mCustomerReturn) {

            var mBatchdetailsList: BatchdetailsList? = null
            mBatchdetailsList = BatchdetailsList(
                0,
                mCBAtch.lot_no,
                mCBAtch.expiry_date,
                mCBAtch.qty.toString(),
                mLowerPrice.toString(),
                mCBAtch.invoice_id.toString(),
                mCBAtch.invoice_no
            )
            arrSelectedBacth.add(mBatchdetailsList)

        }

        var discountAmt = 0F
        var discountPer = ""
        var mUOMPrice = 0F
        var netAmt = 0F
        var vatAmt = 0F
        var totalAmt = 0F
        if (isExcise == "1") {
            mUOMPrice = Utils.twoDigit(mSelectUOMPrice.toFloat() + mSelectUOMExcise.toFloat())
        } else {
            mUOMPrice = mSelectUOMPrice.toFloat()
        }
        var grossPrice = binding.etQty.text.toString()
            .toInt() * mUOMPrice

        netAmt = Utils.twoDigit((grossPrice - discountAmt))
        var mVat = Utils.getVatAmt(netAmt.toDouble(), 5.0)
        vatAmt = Utils.twoDigit(mVat.toFloat())
        totalAmt = Utils.twoDigit((netAmt + vatAmt))

        if (type == "badReturn") {
            isBad = Utils.getIsDamaged(reason)
        } else if (type == "goodReturn") {
            if (reason == "Resalable Near Expiry") {
                isResaleItem = "1"
            }
        }

        mSelectUOMList!!.add(
            UomData(
                mItem!!.id.toString(),
                mItem!!.itemCode,
                mItem!!.itemName,
                mSelectUOMId,
                mSelectUOMName,
                mSelectUOMUPC,
                binding.etQty.text.toString(),
                mSelectUOMPrice,
                grossPrice.toString(),
                discountPer,
                discountAmt = discountAmt.toString(),
                netAmt = netAmt.toString(),
                vatAmt = vatAmt.toString(),
                totalAmt = totalAmt.toString(),
                promotion_Id = "",
                discount_Id = "",
                reason = reason,
                expiryDate = expiryDate,
                invAmount = invAmt,
                invNo = invNo,
                isBad = isBad,
                item_excise = mSelectUOMExcise,
                batchs = arrSelectedBacth
            )
        )

    }

    private fun addSelectUOM() {

        var discountAmt = 0F
        var discountPer = "0"
        var mUOMPrice = 0F
        var netAmt = 0F
        var vatAmt = 0F
        var totalAmt = 0F
        if (isExcise == "1") {
            mUOMPrice = Utils.twoDigit(mSelectUOMPrice.toFloat() + mSelectUOMExcise.toFloat())
        } else {
            mUOMPrice = mSelectUOMPrice.toFloat()
        }
        var grossPrice = binding.etQty.text.toString()
            .toFloat() * mUOMPrice

        if (isCustDiscount) {
            var appalyOn = discountData!!.discountApply
            if (appalyOn == "2") {
                var value = discountData!!.discountValue
                var to = discountData!!.discountTo
                var type = discountData!!.discountType
                if (type == "1") {
                    if (grossPrice >= to!!.toFloat()) {
                        var factor = (grossPrice / to!!.toFloat()).toInt()
                        discountAmt = (value!!.toFloat() * factor)
                        netAmt = Utils.twoDigit((grossPrice - discountAmt))
                        var mVat = Utils.getVatAmt(netAmt.toDouble(), 5.0).toFloat()
                        vatAmt = Utils.twoDigit(mVat)
                        totalAmt = Utils.twoDigit((netAmt + vatAmt))
                    } else {
                        netAmt = Utils.twoDigit((grossPrice - discountAmt))
                        var mVat =
                            Utils.getVatAmt(netAmt.toDouble(), 5.0)
                        vatAmt = Utils.twoDigit(mVat.toFloat())
                        totalAmt = Utils.twoDigit((netAmt + vatAmt))
                    }
                } else {
                    if (grossPrice >= to!!.toFloat()) {
                        discountPer = discountData!!.discountPercentage!!.toString()
                        discountAmt =
                            Utils.twoDigit((grossPrice * discountPer!!.toFloat()) / 100)
                        netAmt = Utils.twoDigit((grossPrice - discountAmt))
                        var mVat = Utils.getVatAmt(netAmt.toDouble(), 5.0).toDouble()
                        vatAmt = Utils.twoDigit(mVat.toFloat())
                        totalAmt = Utils.twoDigit((netAmt + vatAmt))
                    } else {
                        netAmt = Utils.twoDigit((grossPrice - discountAmt))
                        var mVat =
                            Utils.getVatAmt(netAmt.toDouble(), 5.0)
                        vatAmt = Utils.twoDigit(mVat.toFloat())
                        totalAmt = Utils.twoDigit((netAmt + vatAmt))
                    }
                }
            } else {
                var mQty = binding.etQty.text.toString()

                var value = discountData!!.discountValue
                var to = discountData!!.discountTo
                var type = discountData!!.discountType
                if (type == "1") {
                    if (mQty.toDouble() >= to!!.toFloat()) {

                        //  var factor = (mQty.toFloat() / to!!.toFloat()).toInt()
                        discountAmt = Utils.twoDigit((value!!.toFloat()))
                        netAmt = Utils.twoDigit((grossPrice - discountAmt))
                        var mVat = Utils.getVatAmt(netAmt.toDouble(), 5.0).toDouble()
                        vatAmt = Utils.twoDigit(mVat.toFloat())
                        totalAmt = Utils.twoDigit((netAmt + vatAmt))
                    } else {
                        netAmt = Utils.twoDigit((grossPrice - discountAmt))
                        var mVat =
                            Utils.getVatAmt(netAmt.toDouble(), 5.0)
                        vatAmt = Utils.twoDigit(mVat.toFloat())
                        totalAmt = Utils.twoDigit((netAmt + vatAmt))
                    }
                } else {
                    if (mQty.toDouble() >= to!!.toFloat()) {

                        var percet = (discountData!!.discountPercentage!!.toDouble())
                        discountPer = percet.toString()
                        discountAmt =
                            Utils.twoDigit(((grossPrice * percet!!.toFloat()) / 100))
                        netAmt = Utils.twoDigit((grossPrice - discountAmt))
                        var mVat = Utils.getVatAmt(netAmt.toDouble(), 5.0).toDouble()
                        vatAmt = Utils.twoDigit(mVat.toFloat())
                        totalAmt = Utils.twoDigit((netAmt + vatAmt))
                    } else {
                        netAmt = Utils.twoDigit((grossPrice - discountAmt))
                        var mVat =
                            Utils.getVatAmt(netAmt.toDouble(), 5.0)
                        vatAmt = Utils.twoDigit(mVat.toFloat())
                        totalAmt = Utils.twoDigit((netAmt + vatAmt))
                    }
                }
            }
        } else {

            if (type == "delivery") {
                if (mDisPer.toDouble() > 0) {
                    var percet = (mDisPer.toDouble())
                    discountPer = percet.toString()
                    discountAmt =
                        Utils.twoDigit(((grossPrice * percet!!.toFloat()) / 100))
                    netAmt = Utils.twoDigit((grossPrice - discountAmt))
                    var mVat = Utils.getVatAmt(netAmt.toDouble(), 5.0).toDouble()
                    vatAmt = Utils.twoDigit(mVat.toFloat())
                    totalAmt = Utils.twoDigit((netAmt + vatAmt))
                } else {
                    netAmt = Utils.twoDigit((grossPrice - discountAmt))
                    var mVat = Utils.getVatAmt(netAmt.toDouble(), 5.0)
                    vatAmt = Utils.twoDigit(mVat.toFloat())
                    totalAmt = Utils.twoDigit((netAmt + vatAmt))
                }
            } else {
                netAmt = Utils.twoDigit((grossPrice - discountAmt))
                var mVat = Utils.getVatAmt(netAmt.toDouble(), 5.0)
                vatAmt = Utils.twoDigit(mVat.toFloat())
                totalAmt = Utils.twoDigit((netAmt + vatAmt))
            }

        }


        if (type == "badReturn") {
            isBad = Utils.getIsDamaged(reason)
        } else if (type == "goodReturn") {
            if (reason == "Resalable Near Expiry") {
                isResaleItem = "1"
            }
        }

        mSelectUOMList!!.add(
            UomData(
                mItem!!.id.toString(),
                mItem!!.itemCode,
                mItem!!.itemName,
                mSelectUOMId,
                mSelectUOMName,
                mSelectUOMUPC,
                binding.etQty.text.toString(),
                mSelectUOMPrice,
                grossPrice.toString(),
                discountPer,
                discountAmt = discountAmt.toString(),
                netAmt = netAmt.toString(),
                vatAmt = vatAmt.toString(),
                totalAmt = totalAmt.toString(),
                promotion_Id = "",
                discount_Id = "",
                reason = reason,
                expiryDate = expiryDate,
                invAmount = invAmt,
                invNo = invNo,
                isBad = isBad,
                item_excise = mSelectUOMExcise,
                batchs = arrSelectedBacth
            )
        )
    }

    private fun setUpSpinner() {

        val dataAdapter = spUomList?.let {
            ArrayAdapter<String>(
                applicationContext,
                R.layout.my_spinner_item, it
            )
        }
        dataAdapter!!.setDropDownViewResource(R.layout.my_spinner_item)
        binding.qtySpinner.adapter = dataAdapter

        binding.qtySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                mSelectUOMId = mUOMList!![position].uom.id.toString()
                mSelectUOMPrice = mUOMList!![position].uomPrice
                mSelectUOMUPC = mUOMList!![position].uomUPC
                mSelectUOMName = mUOMList!![position].uom.name
                var number3digits: Double = mUOMList!![position].uomPrice.toDouble()
                var str_digit = roundOffDecimal(number3digits)
                binding.etPrice.setText(str_digit.toString())
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // your code here
            }
        }

        val dataReasonAdapter = arrReason?.let {
            ArrayAdapter<String>(
                applicationContext,
                R.layout.my_spinner_item, it
            )
        }
        dataReasonAdapter!!.setDropDownViewResource(R.layout.my_spinner_item)
        binding.reasonSpinner.adapter = dataReasonAdapter
        if (reason.isNotEmpty()) {
            var index = arrReason.indexOf(reason)
            binding.reasonSpinner.setSelection(index)
        } else {
            binding.reasonSpinner.setSelection(0)
        }


        binding.reasonSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                reason = arrReason[position]!!.toString()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // your code here
            }
        }

        binding.batchSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    binding.batchSpinner.adapter.let {
                        val x: HighLightArrayAdapter = it as HighLightArrayAdapter
                        x.setSelection(p2)
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }

        val adapterBatch = HighLightArrayAdapter(
            this,
            R.layout.row_spinner,
            R.id.txt,
            ArrayList(
                listOf(
                    "BATCH001",
                    "BATCH002",
                    "BATCH003",
                    "BATCH004",
                    "BATCH005",
                    "BATCH006",
                    "BATCH007",
                    "BATCH008"
                )
            )
        )

        adapterBatch.add("Select Batch")

        binding.batchSpinner.adapter = adapterBatch
        binding.batchSpinner.setSelection(adapterBatch.count)


        val viewTreeObserverBatch = binding.batchSpinner.viewTreeObserver
        if (viewTreeObserverBatch.isAlive) {
            viewTreeObserverBatch.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    try {
                        binding.batchSpinner.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        binding.batchSpinner.dropDownWidth = binding.batchSpinner.measuredWidth
                    } catch (e: Exception) {

                    }

                }
            })
        }
        binding.batchSpinner.apply {
            setSelection(this.selectedItemPosition)
        }

        binding.batchSpinner.setSelection(0)

//        binding.invoiceSpinner.onItemSelectedListener =
//            object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                    binding.invoiceSpinner.adapter.let {
//                        val x: HighLightArrayAdapter = it as HighLightArrayAdapter
//                        x.setSelection(p2)
//                    }
//                }
//
//                override fun onNothingSelected(p0: AdapterView<*>?) {
//
//                }
//            }
//
//        val adapterInvoice = HighLightArrayAdapter(
//            this,
//            R.layout.row_spinner,
//            R.id.txt,
//            ArrayList(
//                listOf(
//                    "Ref1",
//                    "Ref2",
//                    "Ref3",
//                    "Ref4",
//                    "Ref5",
//                    "Ref6",
//                    "Ref7",
//                    "Ref8"
//                )
//            )
//        )
//
//        adapterInvoice.add("Select Invoice Ref.")
//
//        binding.invoiceSpinner.adapter = adapterInvoice
//        binding.invoiceSpinner.setSelection(adapterInvoice.count)
//
//
//        val viewTreeObserverInvoice = binding.invoiceSpinner.viewTreeObserver
//        if (viewTreeObserverInvoice.isAlive) {
//            viewTreeObserverInvoice.addOnGlobalLayoutListener(object :
//                ViewTreeObserver.OnGlobalLayoutListener {
//                override fun onGlobalLayout() {
//                    try {
//                        binding.invoiceSpinner.viewTreeObserver.removeOnGlobalLayoutListener(this)
//                        binding.invoiceSpinner.dropDownWidth = binding.invoiceSpinner.measuredWidth
//                    } catch (e: Exception) {
//
//                    }
//
//                }
//            })
//        }
//        binding.invoiceSpinner.apply {
//            setSelection(this.selectedItemPosition)
//        }
//
//        binding.invoiceSpinner.setSelection(0)


    }

    fun roundOffDecimal(number: Double): Double? {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
    }

    private fun setUpRecycler() {
        if (mSelectUOMList.isNotEmpty()) {
            binding.llUom.visibility = View.VISIBLE
        }
        binding.rvQtyList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvQtyList.adapter = InputDialogUomAdapter(this, mSelectUOMList)
    }

    private fun setUpRecyclerReturn() {

        if (mSelectUOMList.isNotEmpty()) {
            binding.llUom.visibility = View.VISIBLE
            binding.txtBatch!!.visibility = View.GONE
            binding.txtExepiry!!.visibility = View.VISIBLE
//            orderItemsBatchList_Return = db!!.getCustomerReturnItemList(itemId!!)
//            for (order in orderItemsBatchList_Return!!) {
//                for (mSelect in mSelectUOMList) {
//                    uomTypeList!!.add(mSelect.uomType)
//                }
//
//            }
        }
        binding.rvQtyList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvQtyList.adapter = InputDialogUomBatchAdapter(
            this,
            mSelectUOMList
        )
        binding.rvQtyList.adapter!!.notifyDataSetChanged()
    }

    override fun onItemsClick(position: Int, tag: String) {
        if (tag == "remove") {
            if (type == "goodReturn" || type == "badReturn") {
                binding.etQty.setText(mSelectUOMList!![position].uomQty.toString())
                binding.etPrice.setText(mSelectUOMList!![position].uomPrice)
                mSelectUOMPrice = mSelectUOMList!![position].uomPrice
                binding.etExpiry.setText(mSelectUOMList!![position].expiryDate)
                invNo = mSelectUOMList!![position].invNo
                mSelectUOMList!!.removeAt(position)
            } else {
                if(type == "delivery")
                {
                    binding.llReason.visibility = View.GONE
                }
                binding.etQty.setText(mSelectUOMList!![position].uomQty)
                binding.etPrice.setText(mSelectUOMList!![position].uomPrice)
                // var mDiscAmt = mSelectUOMList!![position].discountAmt
                mDisPer = mSelectUOMList!![position].discountPer
                if (mDisPer.isEmpty()) {
                    mDisPer = "0.0"
                }
                mSelectUOMList!!.removeAt(position)
            }

        } else if (tag == "edit") {
            mSelectUOMList!!.removeAt(position)
        }
        binding.rvQtyList.adapter!!.notifyDataSetChanged()
    }


    private fun getCustomerReturn() {
        var qty = binding.etQty.text.toString()

        var mLowerUom = db!!.getItemLowerUnit(itemId!!)

        var mQty = "0"
        if (mLowerUom == mSelectUOMId) {
            mQty = qty
        } else {
            mQty = (mSelectUOMUPC.toDouble() * qty.toDouble()).toString()
        }
        val jObj = JsonObject()
        jObj.addProperty("customer_id", customerId.toInt())
        jObj.addProperty("item_id", itemId!!.toInt())
        jObj.addProperty("qty", mQty)
        /* jObj.addProperty("expiry_date", binding.etExpiry.text.toString())*/
        jObj.addProperty("expiry_date", "")
        Log.e("CustomerReturn request", jObj.toString())

        ApiClient.getClient(this).customerReturnItem(
            "Bearer ${BaseActivity.userInfo!!.data.accessToken}", MyApplication.X_DOMAIN,
            jObj
        ).enqueue(object : Callback<CustomerItemReturnData> {
            override fun onResponse(
                call: Call<CustomerItemReturnData>,
                response: Response<CustomerItemReturnData>
            ) {
                Log.e("CustomerReturn Success", "Success")
                if (response.isSuccessful) {
                    if (response.body()!!.status!!) {
                        var isSameUom = false
                        if (isValidQTY()) {
                            isSameUom = checkUOMExist()
                            if (isSameUom) {
                                Utils.displayAlert(
                                    mContext = this@InputOrderQtyDialogActivity,
                                    message = "UOM is already added"
                                )
                            }
                        }

                        if (isValidQTY()) {
                            //Riddhi
                            if (type == "goodReturn" || type == "badReturn") {
                                if (Utils.isNetworkAvailable(this@InputOrderQtyDialogActivity)) {
                                    binding.tvErrorMsg!!.text = ""
                                    mSelectUOMList.clear()
                                    if (response.body()!!.data.isNotEmpty()) {
                                        var mCustomerReturn: List<CustomerItemReturnData.DataCustomerItemReturn> =
                                            response.body()!!.data
                                        addReturnSelectUOM(mCustomerReturn)
                                    }
//                                    addSelectUOM()
//                                    db!!.insertCustomerReturnItem(response.body()!!.data, itemId)
                                    setUpRecyclerReturn()
                                } else {
                                    binding.tvErrorMsg!!.text = "Network not available!"
                                    Toast.makeText(
                                        this@InputOrderQtyDialogActivity,
                                        "Network not available!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                binding.tvErrorMsg!!.text = ""
                                addSelectUOM()
                                db!!.insertCustomerReturnItem(response.body()!!.data, itemId)
                                setUpRecyclerReturn()
                            }
                        }
                        binding.tvErrorMsg!!.text = ""
                        strErrormsg = ""
                    }
                } else {
                    strErrormsg = "Item Stock not available."
                    binding.tvErrorMsg!!.text = "Item Stock not available."
                    Toast.makeText(
                        this@InputOrderQtyDialogActivity,
                        "Item Stock not available.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<CustomerItemReturnData>, error: Throwable) {
                Log.e("CustomerReturn Fail", "Fail")
                Utils.logData(this@InputOrderQtyDialogActivity, "Fail Customer Return")
                strErrormsg = "Fail Customer Return"
            }
        })
    }

    private fun getInvoiceData() {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty("item_id", itemId!!)
        jObj.addProperty("customer_id", customerId)

        ApiClient.getClient(this).getGRVData(
            "Bearer ${BaseActivity.userInfo!!.data.accessToken}", MyApplication.X_DOMAIN,
            jObj
        )
            .enqueue(object : Callback<GRVDumpData> {
                override fun onResponse(
                    call: Call<GRVDumpData>,
                    response: Response<GRVDumpData>
                ) {
                    var arrItemData = ArrayList<GRVDumpData.INVDATA>()
                    dismissDialog()
                    if (response.isSuccessful) {
                        Log.e("GRV", Gson().toJson(response.body()!!.data))
                        if (response.body()!!.status!!) {
                            arrItemData = response.body()!!.data as ArrayList<GRVDumpData.INVDATA>
                        }
                    }

                    if (arrItemData.size > 0) {
                        showInvoiceDialog(binding.etQty.text.toString(), arrItemData)
                    }

                }

                override fun onFailure(call: Call<GRVDumpData>, error: Throwable) {
                    Utils.logData(this@InputOrderQtyDialogActivity, "GRV Failed")
                    Log.e("GRV", error.message!!)
                    dismissDialog()
                }
            })
    }

    private var mDialog: Dialog? = null
    var mProgressDialog: ProgressDialog? = null
    fun showOnlyProgressDialog() {
        try {
            if (mDialog != null && mDialog!!.isShowing) {
                return
            }
            mDialog = Dialog(this@InputOrderQtyDialogActivity)
            mDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            mDialog!!.setCancelable(false)
            mDialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            mDialog!!.setContentView(R.layout.custom_only_progress_dialog)
            mDialog!!.window!!.setLayout(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT
            )
            mDialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun dismissDialog() {
        try {
            if (mDialog != null && mDialog!!.isShowing) {
                mDialog!!.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showInvoiceDialog(mOfferQty: String, arrData: List<GRVDumpData.INVDATA>) {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_return)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val metrics = resources.displayMetrics
        val screenWidth = (metrics.widthPixels * 0.95).toInt()
        dialog.window!!.setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT)

        val txtFocQty = dialog.findViewById(R.id.txtFocQty) as TextView
        txtFocQty.text = "Return Qty: $mOfferQty"

        val txtEntQty = dialog.findViewById(R.id.txtEntQty) as TextView
        txtEntQty.visibility = View.GONE
        txtEntQty.text = "Enter Qty: "

        val rvList = dialog.findViewById(R.id.rvList) as RecyclerView
        rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mFOCAdapter = FocItemAdapter(
            context = this,
            allList = arrData!!
        )
        rvList.adapter = mFOCAdapter

        val btnSumbit = dialog.findViewById(R.id.btnAdd) as LinearLayout
        btnSumbit.setOnClickListener {

            if (isValidENterQTy()) {
                var mAllItem = mFOCAdapter!!.getItems()

                for (mDataItem in mAllItem!!) {

                    if (mDataItem!!.itemQty != null) {


                        if (mDataItem!!.itemQty.toDouble() > 0) {

                            var discountAmt = 0F
                            var discountPer = "0"
                            var mUOMPrice = 0F
                            var netAmt = 0F
                            var vatAmt = 0F
                            var totalAmt = 0F
                            if (isExcise == "1") {
                                mUOMPrice =
                                    Utils.twoDigit(mDataItem.unitPrice.toFloat() + mSelectUOMExcise.toFloat())
                                mSelectUOMPrice = mUOMPrice.toString()
                            } else {
                                mUOMPrice = mDataItem.unitPrice.toFloat()
                                mSelectUOMPrice = mDataItem.unitPrice
                            }
                            var grossPrice = mDataItem.itemQty
                                .toFloat() * mUOMPrice

                            netAmt = Utils.twoDigit((grossPrice - discountAmt))
                            var mVat = Utils.getVatAmt(netAmt.toDouble(), 5.0)
                            vatAmt = Utils.twoDigit(mVat.toFloat())
                            totalAmt = Utils.twoDigit((netAmt + vatAmt))
                            invNo = mDataItem.invoice_no
                            mSelectUOMList!!.add(
                                UomData(
                                    mItem!!.id.toString(),
                                    mItem!!.itemCode,
                                    mItem!!.itemName,
                                    mSelectUOMId,
                                    mSelectUOMName,
                                    mSelectUOMUPC,
                                    mDataItem!!.itemQty,
                                    mSelectUOMPrice,
                                    grossPrice.toString(),
                                    discountPer,
                                    discountAmt = discountAmt.toString(),
                                    netAmt = netAmt.toString(),
                                    vatAmt = vatAmt.toString(),
                                    totalAmt = totalAmt.toString(),
                                    promotion_Id = "",
                                    discount_Id = "",
                                    reason = reason,
                                    expiryDate = expiryDate,
                                    invAmount = "0.0",
                                    invNo = mDataItem.invoice_no,
                                    isBad = isBad,
                                    item_excise = mSelectUOMExcise,
                                    batchs = arrSelectedBacth
                                )
                            )
                        }
                    }
                }

                dialog.dismiss()

                val returnIntent = Intent()
                returnIntent.putExtra("uomList", Gson().toJson(mSelectUOMList))
                returnIntent.putExtra("isReSale", isResaleItem)
                setResult(RESULT_OK, returnIntent)
                finish()
            }

        }

        dialog.show()
    }


    class FocItemAdapter(
        var context: Activity,
        var allList: List<GRVDumpData.INVDATA>
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

        fun getItems(): List<GRVDumpData.INVDATA?>? {
            return allList
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(
            holder: FocItemAdapter.ViewHolder,
            position: Int
        ) {
            holder.binding.tvName.text = "Invoice No: " + allList[holder.adapterPosition].invoice_no
            holder.binding.tvCode.text = "Invoice Qty: " + allList[holder.adapterPosition].sales_qty
            holder.binding.txtPDQty.visibility = View.VISIBLE
            holder.binding.txtPDQty.text =
                "Pending Qty: " + allList[holder.adapterPosition].pending_qty

            if (allList[holder.adapterPosition].net_price != null) {
                if (allList[holder.adapterPosition].net_price.isNotEmpty()) {
                    var unitPrice =
                        allList[holder.adapterPosition].net_price.toDouble() / allList[holder.adapterPosition].sales_qty.toDouble()
                    var mFinalPrice = Utils.twoDigit(unitPrice.toFloat())
                    holder.binding.txtVanQty.text =
                        "Unit Price: " + mFinalPrice
                    allList[holder.adapterPosition].unitPrice = "" + mFinalPrice
                } else {
                    holder.binding.txtVanQty.text =
                        "Unit Price: 0.00"
                    allList[holder.adapterPosition].unitPrice = "0.0"
                }
            } else {
                holder.binding.txtVanQty.text =
                    "Unit Price: 0.00"
                allList[holder.adapterPosition].unitPrice = "0.0"
            }


            holder.binding.etQty.setText(allList[holder.adapterPosition].itemQty)

            holder.binding.etQty.addTextChangedListener(
                object : TextWatcher {

                    override fun afterTextChanged(s: Editable) {
                        if (s.toString().isNotEmpty()) {
                            var mPendinqty = allList[holder.adapterPosition].pending_qty.toDouble()
                            if (s.toString().toDouble() > mPendinqty) {
                                s.clear()
                                Toast.makeText(
                                    context,
                                    "You can not enter qty more than pending qty",
                                    Toast.LENGTH_SHORT
                                ).show();
                                allList[holder.adapterPosition].itemQty = ""
                            } else {
                                allList[holder.adapterPosition].itemQty = s.toString()
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

            holder.binding.executePendingBindings()

        }

        inner class ViewHolder(val binding: RowFocItemBinding) :
            RecyclerView.ViewHolder(binding.root) {
        }

    }

    private fun isValidENterQTy(): Boolean {

        var mAllItem = mFOCAdapter!!.getItems()
        var mQTy = 0.0
        for (mDataItem in mAllItem!!) {
            if (mDataItem!!.itemQty != null) {
                if (mDataItem!!.itemQty.toDouble() > 0) {
                    mQTy += mDataItem!!.itemQty.toDouble()
                }
            }

        }

        if (binding.etQty.text.toString().toDouble() < mQTy) {
            Toast.makeText(this, "Please enter qty same as Return qty", Toast.LENGTH_SHORT)
                .show()
            return false
        } else if (binding.etQty.text.toString().toDouble() > mQTy) {
            Toast.makeText(this, "Please enter qty same as Return qty", Toast.LENGTH_SHORT)
                .show()
            return false
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == scanCode) {
            if (resultCode == Activity.RESULT_OK) {
                var id = data!!.getStringExtra("scanid")
                Log.e("scanid", id!!)

                if (isFromQRClick) {
                    binding.etFromSerial!!.setText(id!!)
                } else {
                    binding.etToSerial!!.setText(id!!)
                    getCountSerial()
                }
            }
        }
    }

    fun getCountSerial() {
        var fromserail = binding.etFromSerial!!.text.toString().toLong()
        var toSerial = binding.etToSerial!!.text.toString().toLong()

        var diff = (toSerial - fromserail) + 1
        binding.etCount!!.setText(diff.toString())
        binding.etQty.setText(diff.toString())
    }

}