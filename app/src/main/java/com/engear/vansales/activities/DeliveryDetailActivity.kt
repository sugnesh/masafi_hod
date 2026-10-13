package com.engear.vansales.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.OrderRequestItemAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityDeliveryDetailBinding
import com.engear.vansales.interfaces.OnDeliveryItemClickListener
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.BatchdetailsList
import com.engear.vansales.model.CustomerData
import com.engear.vansales.model.CustomerPromotionData
import com.engear.vansales.model.ItemData
import com.engear.vansales.model.UomData
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.util.HashMap

class DeliveryDetailActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivityDeliveryDetailBinding
    private var deliveryNo: String = ""
    private var deliveryId: String = ""
    private var orderId: String = ""
    private var mIcashBalance: String = ""
    private var mCustomerBalance: String = ""
    private var mAccountNumber: String = ""
    private var mEmail: String = ""
    private var mMobile: String = ""
    private var mIsChange = false
    private var customerCode: String = ""
    var customerObj: CustomerData.DataCustomer? = null
    var itemsList: ArrayList<ItemData.Data>? = ArrayList()
    var itemPosition = -1
    var mAdapter: OrderRequestItemAdapter? = null
    var lobCode = ""
    private val intentCode = 100
    private val intentAddCode = 120
    var balance = ""

    companion object {
        var isDeliveryDone = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDeliveryDetailBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@DeliveryDetailActivity,
            "On Delivery Details Screen "
        )
        init()
    }

    private fun init() {
        setToolbarTitle("Delivery Detail")

        deliveryNo = intent.getStringExtra("delivery_no")!!
        deliveryId = intent.getStringExtra("delivery_id")!!
        orderId = intent.getStringExtra("order_id")!!
        balance = intent.getStringExtra("balance")!!
        customerCode = intent.getStringExtra("customer_code")!!
        mIcashBalance = intent.getStringExtra("iCashBalance")!!
        mCustomerBalance = intent.getStringExtra("accountBalance")!!
        mAccountNumber = intent.getStringExtra("accountNumber")!!
        mMobile = intent.getStringExtra("mobile")!!
        mEmail = intent.getStringExtra("email")!!
        customerObj = db!!.getCustomerDetail(customerCode!!)
        itemsList = db!!.getDeliveryDetail(deliveryNo!!)

//        var mPalletItem = db!!.getAllPalletSaleItem()
//        itemsList!!.addAll(mPalletItem)

        if (intent.getStringExtra("lob_code") != null) {
            lobCode = intent.getStringExtra("lob_code")!!
        }

        binding.btnSubmit.setOnClickListener {
            Utils.logData(
                this@DeliveryDetailActivity,
                "On Submit Button Screen "
            )

            if (mIsChange) {
                if (isAnyFreeItem()) {
                    getCustomerPromotion()
                } else {
                    openNextScreen()
                }
            } else {
                openNextScreen()
            }


        }

        binding.btnAdd!!.setOnClickListener {
            Utils.logData(
                this@DeliveryDetailActivity,
                "On Add Button Screen "
            )

            startActivityForResult(
                Intent(this, AddMoreItemActivity::class.java)
                    .putExtra("customer_code", customerCode)
                    .putExtra("type", "sales"),
                intentAddCode
            )

        }

        setUpRecycler()
    }

    private fun openNextScreen() {
        if (checkStockAavilable()) {

            var mFinalIem = getAllItem()

            startActivity(Intent(this, OrderConfirmActivity::class.java).apply {
                this.putExtra("orderItemList", Gson().toJson(mFinalIem))
                this.putExtra("customer_code", customerCode)
                this.putExtra("delivery_date", deliveryNo)
                this.putExtra("delivery_id", deliveryId)
                this.putExtra("order_id", orderId)
                this.putExtra("type", "delivery")
                this.putExtra("lob", lobCode)
                this.putExtra("balance", balance)
                this.putExtra("iCashBalance", mIcashBalance)
                this.putExtra("accountBalance", mCustomerBalance)
                this.putExtra("accountNumber", mAccountNumber)
                this.putExtra("mobile", mMobile)
                this.putExtra("email", mEmail)
                this.putExtra("isBatch", "0")
            })

        } else {
            Utils.displayAlert(this, "Stock not available")
        }
    }

    private fun getAllItem(): ArrayList<ItemData.Data>? {
        var mFinalItem: ArrayList<ItemData.Data>? = ArrayList()
        for (mData in itemsList!!) {
            if (mData.itemSelectUOM.isNotEmpty()) {
                mFinalItem!!.add(mData);
            }
        }

        return mFinalItem
    }

    private fun checkStockAavilable(): Boolean {

        var isAvailable = true
        if (itemsList != null) {
            for (mItem in itemsList!!) {
                var mQty = 0.0
                for (mUOM in mItem.itemSelectUOM) {
                    mQty += Utils.getUOMLowerQty(mUOM.uomUPC, mUOM.uomQty).toDouble()
                }

                if (!db!!.isItemAvailable(mItem.id!!.toString(), mQty.toString())) {
                    isAvailable = false
                    break
                }
            }
        }
        return isAvailable
    }

    private fun setUpRecycler() {
        binding.rvDeliveryList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = OrderRequestItemAdapter(this, itemsList!!, "Delivery")
        binding.rvDeliveryList.adapter = mAdapter
    }

    override fun onItemsClick(position: Int, tag: String) {

        itemPosition = position
        Log.e("Item Possiton Free", itemsList!![itemPosition].isFree)

        if (tag == "LongClick") {
            val alertDialogBuilder = AlertDialog.Builder(this@DeliveryDetailActivity)
            alertDialogBuilder.setTitle("Delete")
                .setMessage("Are you sure you want to delete this item?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, which ->
                    itemsList!!.removeAt(itemPosition)
                    mAdapter!!.notifyDataSetChanged()

                    dialog.dismiss()
                }.setNegativeButton(
                    "No"
                ) { dialog, which -> dialog.dismiss() }
            // create alert dialog
            // create alert dialog
            val alertDialog = alertDialogBuilder.create()
            // show it
            // show it
            alertDialog.show()
        } else {
            if (itemsList!![itemPosition].isFree == "1") {
                Utils.displayAlert(this, "You can not edit the FOC item!")
            } else {
                openQuantityDialog()
            }
        }
    }

    fun getItemPosition(itemId: Int): Int {

        var position = 0

        for (mItem in itemsList!!) {
            if (mItem.id == itemId) {
                position = itemsList!!.indexOf(mItem)
                break
            }
        }
        return position
    }

    private fun openQuantityDialog() {
        if (itemsList!![itemPosition].itemUOM == null)
            itemsList!![itemPosition].itemUOM = ArrayList()

        mIsChange = true
        startActivityForResult(
            Intent(this, InputOrderQtyDialogActivity::class.java).apply {
                this.putExtra("item_code", itemsList!![itemPosition].itemCode)
                this.putExtra("item_id", itemsList!![itemPosition].id.toString())
                this.putExtra("cust_id", customerObj!!.userDetail.id.toString())
                this.putExtra(
                    "uom_list",
                    Gson().toJson(itemsList!![itemPosition].itemSelectUOM)
                )
                this.putExtra(
                    "available_qty",
                    "0"
                )
                this.putExtra(
                    "available_uom",
                    itemsList!![itemPosition].vanUOM
                )
                this.putExtra("type", "delivery")
                this.putExtra("lobCode", lobCode)
            },
            intentCode
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == intentCode) {
            if (resultCode == Activity.RESULT_OK) {

                itemsList!![itemPosition].itemSelectUOM = emptyList()

                val strObj = data!!.getStringExtra("uomList")
                val listOfUomType: Type =
                    object : TypeToken<List<UomData?>?>() {}.type

                var mList =
                    Gson().fromJson(strObj, listOfUomType) as ArrayList<UomData>

                itemsList!![itemPosition].itemSelectUOM = mList

                binding.rvDeliveryList.adapter!!.notifyDataSetChanged()
            }
        } else if (requestCode == intentAddCode) {
            if (resultCode == Activity.RESULT_OK) {

                val strObj = data!!.getStringExtra("ItemList")
                val listOfUomType: Type =
                    object : TypeToken<List<ItemData.Data?>?>() {}.type

                var mList =
                    Gson().fromJson(strObj, listOfUomType) as ArrayList<ItemData.Data>

                itemsList!!.addAll(mList)
                binding.rvDeliveryList.adapter!!.notifyDataSetChanged()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (isDeliveryDone) {
            finish()
        }
    }

    fun isAnyFreeItem(): Boolean {

        var isFree = false

        for (mData in itemsList!!) {
            if (mData.isFree == "1") {
                isFree = true
                break
            }
        }

        if (customerObj!!.channel_id != "1") {
            isFree = false
        }
        return isFree
    }

    private fun getCustomerPromotion() {

        showOnlyProgressDialog()
        val jItem = JsonArray()

        for (i in itemsList!!) {
            if (i.isFree == "0") {
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
        }
        val jObj = JsonObject()
        jObj.addProperty(
            "customer_id",
            db!!.getCustomerCustId(customerCode)
        )
        jObj.addProperty(
            "delivery_date",
            Utils.getCurrentDate()
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
                    //openConfrimActivity()
                    removeFOCandNext(true)
                }
            }

            override fun onFailure(call: Call<CustomerPromotionData>, error: Throwable) {
                dismissDialog()
                Log.e("fails", error.message!!)
                // openConfrimActivity()
            }
        })
    }

    fun customerHODPROMO(promoList: List<CustomerPromotionData.CustomerPromotion.ItemPromoInfo>) {

        removeFOCandNext(false)
        for (i in promoList!!) {
            if (i.type == "Item") {
                if (i.discount_type == "1") {

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

                        itemsList!!.add(mDItem)
                    }
                }
            }

        }

        openNextScreen()

    }

    fun removeFOCandNext(isNext: Boolean) {

        itemsList!!.removeIf { item ->
            // your condition
            item.isFree == "1"
        }

        if (isNext) {
            openNextScreen()
        }

    }
}