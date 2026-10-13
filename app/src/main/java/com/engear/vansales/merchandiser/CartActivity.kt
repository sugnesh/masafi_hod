package com.engear.vansales.merchandiser

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.activities.ProductCatalogActivity
import com.engear.vansales.adapter.CartAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.databinding.ActivityCartBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.interfaces.OnUOMUpdateListener
import com.engear.vansales.model.CustomerData
import com.engear.vansales.model.Order
import com.engear.vansales.model.ProductCatelog
import com.engear.vansales.model.Transaction
import com.engear.vansales.utils.Utils
import java.text.DecimalFormat

class CartActivity : BaseActivity(), OnItemClickListener, OnUOMUpdateListener {

    lateinit var binding: ActivityCartBinding
    private var allData: ArrayList<ProductCatelog> = ArrayList()
    var mAdapter: CartAdapter? = null
    var orderAmt: Float = 0f
    var vatAmt: Float = 0f
    var excise: Float = 0f
    var netAmt: Float = 0f
    var grossAmt: Float = 0f
    var discountAmt: Float = 0f
    var totalAmt: Float = 0f
    var orderComment: String? = null
    private val df = DecimalFormat("#,###.##")
    var allCustomerList: ArrayList<CustomerData.DataCustomer> = ArrayList()
    var spCusList: MutableList<String>? = ArrayList()
    var customerData: CustomerData.DataCustomer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {
        setToolbarTitle("Cart")

        allData = ArrayList()
        allData = db!!.getAllCartList()
        calculateOrderPrice()

        allCustomerList = db!!.getAllCustomer("")
        spCusList!!.add("Select Customer")
        for (mInvoice in allCustomerList) {
            spCusList!!.add(mInvoice.userDetail.firstname!! + " " + mInvoice.userDetail.lastname!!)
        }
        setCustomer()
        setAdapter()

        binding.btnProceed.setOnClickListener {
            if (allData.size > 0) {
                insertOrder()
            }
        }

    }

    private fun insertOrder() {

        var orderNo =
            Utils.getLastIndex(mPreferenceManager!!.getString(AppConstants.ORDER_LAST).toString(),"order")

        var orderDate = Utils.getCurrentDate()

        var dueDate =
            Utils.addDays("yyyy-MM-dd", orderDate, customerData!!.credit_days!!.toInt(),"1")!!

        var orderTypeId = if (customerData!!.credit_days == "0") {
            db!!.getOrderTypeId("Cash")
        } else {
            db!!.getOrderTypeId("Credit")
        }

        var mOrder = Order()
        mOrder.orderNo = orderNo
        mOrder.orderType = orderTypeId
        mOrder.orderDate = orderDate
        mOrder.deliveryDate = orderDate
        mOrder.dueDate = dueDate
        mOrder.customerId = customerData!!.customer_code.toString()
        mOrder.gross = grossAmt.toString()
        mOrder.discount = discountAmt.toString()
        mOrder.net = netAmt.toString()
        mOrder.vat = vatAmt.toString()
        mOrder.total = totalAmt.toString()
        mOrder.comment = ""
        mOrder.payemntId = customerData!!.payment_term_id.toString()

        //Insert Order
        db!!.insertCartOrder(mOrder = mOrder, mItemList = allData!!, isAccept = "0")

        //set Order Last Number
        mPreferenceManager!!.setString(AppConstants.ORDER_LAST, orderNo)

        val transaction = Transaction()
        transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_OREDER_CREATED
        transaction.tr_date_time = Utils.getCurrentDate() + " " + Utils.getCurrentTime()
        transaction.tr_customer_num = customerData!!.customer_code.toString()
        transaction.tr_customer_name =
            customerData!!.userDetail!!.firstname + " " + customerData!!.userDetail!!.lastname
        transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
        transaction.tr_invoice_id = ""
        transaction.tr_order_id = orderNo
        transaction.tr_collection_id = ""
        transaction.tr_pyament_id = ""
        transaction.tr_printData = ""
        transaction.tr_is_posted = "No"
        db!!.insertTransaction(transaction)

        db!!.emptyCart()
        ProductCatalogActivity.isOrderCreated = true
        finish()
    }


    fun setCustomer() {
        val dataAdapter = spCusList?.let {
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
                    var invoiceId = allCustomerList!![position].customer_code
                    customerData = db!!.getCustomerDetail(invoiceId!!)
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // your code here
            }
        }
    }

    fun setAdapter() {
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = CartAdapter(
            context = this,
            allList = allData
        )
        binding.rvList.adapter = mAdapter
    }

    private fun calculateOrderPrice() {

        orderAmt = 0f
        vatAmt = 0f
        excise = 0f
        netAmt = 0f
        grossAmt = 0f
        discountAmt = 0f
        totalAmt = 0f

        for (mItem in allData!!) {

            val mLowerUOM =
                db!!.getLowerUOM(
                    mItem.uomId!!,
                    mItem.itemId!!
                )


            var mUomprice = 0.0
            if (mLowerUOM!!.uomPrice != null) {
                mUomprice = mLowerUOM!!.uomPrice.toDouble()
            }
            var grossPrice = mItem.qty!!.toInt() * mUomprice
            var discount = 0.0
            var netA = grossPrice - discount
            var vatA = Utils.getVatAmt(grossPrice, mItem!!.vatPercent!!.toDouble())
            var totalA = netA + vatA
            grossAmt = grossAmt.plus(grossPrice.toFloat())
            discountAmt = discountAmt.plus(discount.toFloat())
            netAmt = netAmt.plus(netA.toFloat())
            vatAmt = vatAmt.plus(vatA.toFloat())
            totalAmt = totalAmt.plus(totalA.toFloat())
        }

        binding.txtGross.text = df.format(grossAmt)
        binding.txtDiscount.text = df.format(discountAmt)
        binding.txtNet.text = df.format(netAmt)
        binding.txtVat.text = df.format(vatAmt)
        binding.txtGrandTotal.text = Utils.getCurrency(this) + " " + df.format(totalAmt)
        binding.tvTotal.text = df.format(totalAmt) + Utils.getCurrency(this)
    }

    override fun onItemsClick(position: Int, tag: String) {
        if (tag == "plus") {
            var qty = allData[position].qty!!.toInt()
            qty++
            var mdata = allData[position]
            mdata.qty = qty.toString()
            allData[position] = mdata
            mAdapter!!.notifyDataSetChanged()
            calculateOrderPrice()
        } else {
            var qty = allData[position].qty!!.toInt()
            qty--
            if (qty == 0) {
                allData.removeAt(position)
            } else {
                var mdata = allData[position]
                mdata.qty = qty.toString()
                allData[position] = mdata
            }
            mAdapter!!.notifyDataSetChanged()
            calculateOrderPrice()
        }
    }

    override fun onUOMUpdate(position: Int, tag: String) {
        var mdata = allData[position]
        mdata.uomId = tag
        allData[position] = mdata
        //mAdapter!!.notifyDataSetChanged()
        calculateOrderPrice()
    }
}