package com.engear.vansales.activities

import android.R
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.OrderRequestItemAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityReturnRefBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.*
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

class ReturnRefActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivityReturnRefBinding
    var customerCode: String? = null
    var itemType: String? = null
    var invoiceList: List<InvoiceData.Invoices> = ArrayList()
    var itemsList: ArrayList<ItemData.Data>? = ArrayList()
    var orderItemsList: ArrayList<ItemData.Data>? = ArrayList()
    var itemPosition = -1
    var spInvList: MutableList<String>? = ArrayList()
    var spInvIdList: MutableList<String>? = ArrayList()
    var spInvAmtList: MutableList<String>? = ArrayList()
    var invoiceId: String = ""
    var invoiceNo: String = ""
    var invoiceAmt: String = "0.0"
    var customerData: CustomerData.DataCustomer? = null
    private val intentCode = 100
    var lobCode = ""

    companion object {
        var isReturnCreated = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReturnRefBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {
        Utils.logData(
            this@ReturnRefActivity,
            "On return Screen "
        )
        setToolbarTitle("Return")

        customerCode = intent.getStringExtra("customer_code")
        itemType = intent.getStringExtra("type")
        customerData = db!!.getCustomerDetail(customerCode!!)

        if (intent.getStringExtra("lob") != null) {
            lobCode = intent.getStringExtra("lob")!!
        }

        getOutstandingInvoice()

        binding.btnSubmit.setOnClickListener {
            Utils.logData(
                this@ReturnRefActivity,
                "On button submit click"
            )
            orderItemsList!!.clear()

            for (i in itemsList!!) {
                if (i.itemSelectUOM != null) {
                    if (i.itemSelectUOM!!.isNotEmpty()) {
                        orderItemsList!!.add(i)
                    }
                }
            }
            if (orderItemsList!!.isNotEmpty()) {
                startActivity(Intent(this, OrderConfirmActivity::class.java).apply {
                    this.putExtra("orderItemList", Gson().toJson(orderItemsList))
                    this.putExtra("customer_code", customerCode)
                    this.putExtra("invoice_id", invoiceId)
                    this.putExtra("type", itemType)
                    this.putExtra("lob", lobCode)
                })
            } else {
                Toast.makeText(this, "Please Add Item!", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun getOutstandingInvoice() {
        showOnlyProgressDialog()

        ApiClient.getClient(this).getCustomerInvoice(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN,
            customerData!!.id
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
                            itemsList!!.clear()
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
                                    itemsList!!.add(mItem!!)
                                }
                            }
                        }
                    }
                }
                dismissDialog()
                setUpRecycler()

            }

            override fun onFailure(call: Call<InvoiceItemData>, error: Throwable) {
                //Utils.logData(this@DownloadingDataActivity, "Order Type Failed")
                dismissDialog()
                setUpRecycler()
            }
        })
    }

    private fun setUpRecycler() {
        binding.rvItemList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvItemList.adapter = OrderRequestItemAdapter(this, itemsList!!,"")
    }

    override fun onItemsClick(position: Int, tag: String) {
        if (tag == "dialog") {
            itemPosition = getItemPosition(position)
            openQuantityDialog()
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

        startActivityForResult(
            Intent(this, InputOrderQtyDialogActivity::class.java).apply {
                this.putExtra("item_code", itemsList!![itemPosition].itemCode)
                this.putExtra("item_id", itemsList!![itemPosition].id.toString())
                this.putExtra("cust_id", "")
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
                    itemsList!![itemPosition].barcode
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
                this.putExtra("type", itemType)
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
                val isReSale = data!!.getStringExtra("isReSale")
                val listOfUomType: Type =
                    object : TypeToken<List<UomData?>?>() {}.type

                var mList =
                    Gson().fromJson(strObj, listOfUomType) as ArrayList<UomData>

                itemsList!![itemPosition].itemSelectUOM = mList
                itemsList!![itemPosition].itemWeight = isReSale!!
                if (isReSale == "1") {
                    itemsList!![itemPosition].itemShellife = "1"
                }
                binding.rvItemList.adapter!!.notifyDataSetChanged()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (isReturnCreated) {
            isReturnCreated = false
            finish()
        }
    }
}