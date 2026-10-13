package com.engear.vansales.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.engear.vansales.adapter.OrderRequestItemAdapter
import com.engear.vansales.adapter.ReturnRequestItemAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivityOrderPostBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.CustomerData
import com.engear.vansales.model.ItemData
import com.engear.vansales.model.UomData
import com.engear.vansales.utils.Utils
import java.lang.reflect.Type

class OrderPostActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivityOrderPostBinding
    var orderNo: String? = null
    var balance: String? = null
    var itemType: String? = null
    var isRequested: String? = null
    var orderAmount: String = "0"
    var itemsList: ArrayList<ItemData.Data>? = ArrayList()
    var orderItemsList: ArrayList<ItemData.Data>? = ArrayList()
    var itemPosition = -1
    var mAdapter: ReturnRequestItemAdapter? = null
    var customerCode: String? = null
    var customerObj: CustomerData.DataCustomer? = null
    private val intentCode = 100
    private val intentAddCode = 120

    companion object {
        var isOrderCreated = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderPostBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
        setClickEvent()
    }

    private fun init() {

        Utils.logData(
            this@OrderPostActivity,
            "On order post Screen "
        )
        itemType = intent.getStringExtra("type")
        orderNo = intent.getStringExtra("order_no")
        customerCode = intent.getStringExtra("customer_code")
        customerObj = db!!.getCustomerDetail(customerCode!!)
        if (customerObj!!.customer_type_id.toString() == "2") {
            customerObj!!.credit_days = "0"
        }

        if (itemType == "order") {
            setToolbarTitle("Order Detail")
            binding.btnAdd.visibility = View.GONE
            itemsList = db!!.getAllOrderItem(orderNo!!)
        } else if (itemType == "sales") {
            setToolbarTitle("Invoice Detail")
            binding.btnAdd.visibility = View.VISIBLE
            itemsList = db!!.getAllInvoiceItem(orderNo!!)

            if (customerObj!!.credit_days == "0") {
                balance = "0"
            } else {
                var creditLimit = customerObj!!.credit_limit.toDouble()
                var amtDue = 0.0
                amtDue = db!!.getCustomerBalance(customerObj!!.userDetail.id.toString())
                balance = Utils.twoDigit((creditLimit.toFloat() - amtDue.toFloat())).toString()
            }

        } else {
            binding.btnAdd.visibility = View.GONE
            setToolbarTitle("Return Detail")
            isRequested = intent.getStringExtra("isRequested")
            itemsList = db!!.getAllReturnItem(orderNo!!)
        }

        setUpRecycler()

    }

    private fun setClickEvent() {
        binding.rlCheckout.setOnClickListener {
            orderItemsList!!.clear()

            for (i in itemsList!!) {
                if (i.itemSelectUOM != null) {
                    if (i.itemSelectUOM!!.isNotEmpty()) {
                        orderItemsList!!.add(i)
                    }
                }
            }

            if (itemType == "order") {
                startActivity(Intent(this, OrderConfirmActivity::class.java).apply {
                    this.putExtra("orderItemList", Gson().toJson(orderItemsList))
                    this.putExtra("customer_code", customerCode)
                    this.putExtra("delivery_date", Utils.getCurrentDate())
                    this.putExtra("type", itemType)
                    this.putExtra("isAccept", "1")
                    this.putExtra("orderNo", orderNo)
                    this.putExtra("lob", "")
                })
            } else if (itemType == "sales") {
                if (checkStockAavilable()) {
                    startActivity(Intent(this, OrderConfirmActivity::class.java).apply {
                        this.putExtra("orderItemList", Gson().toJson(orderItemsList))
                        this.putExtra("customer_code", customerCode)
                        this.putExtra("delivery_date", Utils.getCurrentDate())
                        this.putExtra("type", itemType)
                        this.putExtra("isAccept", "1")
                        this.putExtra("orderNo", orderNo)
                        this.putExtra("lob", "")
                        this.putExtra("balance", balance)
                    })
                } else {
                    Toast.makeText(this, "Stock not available", Toast.LENGTH_SHORT).show()
                }

            } else {
                startActivity(Intent(this, OrderConfirmActivity::class.java).apply {
                    this.putExtra("orderItemList", Gson().toJson(orderItemsList))
                    this.putExtra("customer_code", customerCode)
                    this.putExtra("type", itemType)
                    this.putExtra("isAccept", "1")
                    this.putExtra("isRequest", isRequested)
                    this.putExtra("orderNo", orderNo)
                    this.putExtra("lob", "")
                })
            }

        }

        binding.btnAdd.setOnClickListener {

            startActivityForResult(
                Intent(this, AddMoreItemActivity::class.java)
                    .putExtra("customer_code", customerCode)
                    .putExtra("type", itemType),
                intentAddCode
            )
        }
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
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = ReturnRequestItemAdapter(this, itemsList!!)
        binding.rvList.adapter = mAdapter
    }

    override fun onItemsClick(position: Int, tag: String) {
        if (tag == "dialog") {
            itemPosition = position
            openQuantityDialog()
        } else if (tag == "LongClick") {
            val alertDialogBuilder = AlertDialog.Builder(this@OrderPostActivity)
            alertDialogBuilder.setTitle("Delete")
                .setMessage("Are you sure you want to delete this item?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, which ->
                    itemPosition = position
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

        if (itemType == "order") {
            openDialogActivity(mQty = "0", mUom = "")
        } else {
            openDialogActivity(mQty = "0", mUom = "")
        }

    }

    private fun openDialogActivity(mQty: String, mUom: String) {
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
                    mQty
                )
                this.putExtra(
                    "available_uom",
                    mUom
                )
                this.putExtra("type", itemType)
                this.putExtra(
                    "display_uom",
                    ""
                )
                this.putExtra("lobCode", "")
                this.putExtra("isReSale", "0")
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
                itemsList!![itemPosition].isStockAvailable = "1"

                binding.rvList.adapter!!.notifyDataSetChanged()
            }
        } else if (requestCode == intentAddCode) {
            if (resultCode == Activity.RESULT_OK) {

                val strObj = data!!.getStringExtra("ItemList")
                val listOfUomType: Type =
                    object : TypeToken<List<ItemData.Data?>?>() {}.type

                var mList =
                    Gson().fromJson(strObj, listOfUomType) as ArrayList<ItemData.Data>

                itemsList!!.addAll(mList)
                setUpRecycler()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isOrderCreated) {
            isOrderCreated = false
            if (itemType == "order") {
                OrderActivity.isOrderDone = true
            } else if (itemType == "sales") {
                InvoiceActivity.isInvoiceDone = true
            } else {
                ReturnActivity.isReturnDone = true
            }
            finish()
        }
    }
}