package com.engear.vansales.activities

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.R
import com.engear.vansales.adapter.ReturnItemAdapter
import com.engear.vansales.adapter.ReturnPalletItemAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.DeviceListActivity
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityPalletReturnBinding
import com.engear.vansales.databinding.ActivityReturnBinding
import com.engear.vansales.databinding.DialogInputLoadVarianceBinding
import com.engear.vansales.databinding.DialogPalletBinding
import com.engear.vansales.interfaces.OnConfirmationDialogClick
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.interfaces.OnSearchableDialog
import com.engear.vansales.merchandiser.StoreCheckActivity
import com.engear.vansales.model.CreditData
import com.engear.vansales.model.CustomerData
import com.engear.vansales.model.Order
import com.engear.vansales.model.PalletReturnData
import com.engear.vansales.model.SalesmanLoadDetail
import com.engear.vansales.model.Transaction
import com.engear.vansales.printer.BluetoothPrintService
import com.engear.vansales.printer.PrintLog
import com.engear.vansales.printer.WoosimCustomService
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference
import java.util.Date
import java.util.NavigableSet
import java.util.TreeSet
import kotlin.math.roundToInt

class PalletReturnActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivityPalletReturnBinding
    var customerCode: String? = null
    var returnList: ArrayList<PalletReturnData.PalletReturn> = ArrayList()
    var customerObj: CustomerData.DataCustomer? = null
    var mAdapter: ReturnPalletItemAdapter? = null

    private var jsonArray: JSONArray? = null

    private val REQUEST_CONNECT_DEVICE_SECURE = 1
    private val REQUEST_ENABLE_BT = 3
    private var mBluetoothAdapter: BluetoothAdapter? = null


    val MESSAGE_DEVICE_NAME = 1
    val MESSAGE_TOAST = 2
    val MESSAGE_READ = 3

    val DEVICE_NAME = "device_name"
    val TOAST = "toast"
    var isDraftPrint = false
    var isBatch = "0"


    companion object {

        var mActivity: PalletReturnActivity? = null

        fun callbackFunction() {
            ReturnActivity.isReturnDone = true
            Toast.makeText(
                mActivity!!,
                "Return Pallet Added successfully!",
                Toast.LENGTH_SHORT
            )
                .show()
            mActivity!!.finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPalletReturnBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        mActivity = this
        init()
    }

    private fun init() {
        Utils.logData(
            this@PalletReturnActivity,
            "On return Screen "
        )
        setToolbarTitle("Pallet Return")

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        customerCode = intent.getStringExtra("customer_code")
        customerObj = db!!.getCustomerDetail(customerCode!!)

        getReturn()

        binding.btnAdd.setOnClickListener {
            showConfirmAlertDialog("Are you sure you want to take this as Return?",
                object : OnConfirmationDialogClick {
                    override fun onOk() {
                        addPalletRetun()
                    }

                    override fun onCancel() {
                    }
                })
        }
    }

    private fun getReturn() {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty("customer_id", customerObj!!.user_id.toString())
        Log.e("CustomerData", jObj.toString())

        ApiClient.getClient(this).getPalletReturnData(
            "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
            MyApplication.X_DOMAIN,
            jObj
        ).enqueue(object : Callback<PalletReturnData> {
            override fun onResponse(
                call: Call<PalletReturnData>,
                response: Response<PalletReturnData>
            ) {
                if (response.isSuccessful) {
                    Log.e("Paller response", response.body()!!.toString())
                    //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                    if (response.body()!!.status) {
                        var meturnList = response.body()!!.data
                        if (meturnList.size > 0) {
                            returnList = ArrayList()
                            for (mItem in meturnList) {
                                var mPalletData = PalletReturnData.PalletReturn(
                                    mItem.item_id,
                                    mItem.item_code,
                                    mItem.item_name,
                                    mItem.item_qty,
                                    mItem.invoice_id,
                                    mItem.invoice_number,
                                    ""
                                )
                                returnList.add(mPalletData)
                            }
                        }
                    }
                } else {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Blank")
                }

                dismissDialog()
                setUpRecycler()
            }

            override fun onFailure(call: Call<PalletReturnData>, error: Throwable) {
                //Utils.logData(this@DownloadingDataActivity, "Order Type Failed")
                Log.e("Paller response fail", jObj.toString())
                dismissDialog()

            }
        })
    }

    private fun setUpRecycler() {
        //Log.e("Paller response Retu", returnList.size.toString())
        binding.rvItemList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = ReturnPalletItemAdapter(this, returnList)
        binding.rvItemList.adapter = mAdapter
    }

    override fun onItemsClick(position: Int, tag: String) {
        makeDilog(this@PalletReturnActivity, returnList[position], position)
    }

    private fun makeDilog(context: Context, item: PalletReturnData.PalletReturn, position: Int) {
        try {
            val dialogBinding = DialogPalletBinding.inflate(LayoutInflater.from(context))
            val dialogView = dialogBinding.root

            dialogBinding.txtName.text = item.item_name

            val alertDialog = AlertDialog.Builder(context).create().apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setView(dialogView)
            }

            dialogBinding.btnConfirm.setOnClickListener {
                val qtyText = dialogBinding.edtActAlter.text.toString()

                if (qtyText.isNotEmpty()) {
                    val enteredQty = qtyText.toDoubleOrNull()
                    val availableQty = item.item_qty.toDoubleOrNull()

                    if (enteredQty == null || availableQty == null) {
                        Toast.makeText(context, "Invalid quantity entered.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    if (enteredQty > availableQty) {
                        Toast.makeText(
                            context,
                            "You cannot enter qty more than sale Qty",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        returnList[position].enterQty = qtyText
                        alertDialog.dismiss()
                        mAdapter?.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(context, "Please enter quantity.", Toast.LENGTH_SHORT).show()
                }
            }

            dialogBinding.btnCancel.setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error creating dialog", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addPalletRetun() {

        var uniqId = "PR" + Utils.getTimeStamp()!!
            .replace(":", "") + customerObj!!.userDetail.id.toString()
        db!!.insertPalletReturn(
            uniqId,
            returnList, customerObj!!.userDetail.id.toString()
        )

        //get Print Data
        var data: JSONObject? = null
        try {
            jsonArray = createPrintReturnData(uniqId, "")
            data = JSONObject()
            data.put("data", jsonArray as JSONArray?)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val transaction = Transaction()
        transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_PALLET_RETURN
        transaction.tr_date_time = Utils.getCurrentDate() + " " + Utils.getCurrentTime()
        transaction.tr_customer_num = customerCode
        transaction.tr_customer_name =
            customerObj!!.userDetail!!.firstname + " " + customerObj!!.userDetail!!.lastname
        transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
        transaction.tr_invoice_id = ""
        transaction.tr_order_id = uniqId
        transaction.tr_collection_id = ""
        transaction.tr_pyament_id = ""
        transaction.tr_printData = data.toString()
        transaction.tr_is_posted = "No"
        db!!.insertTransaction(transaction)

        Utils.dialogPrint(this@PalletReturnActivity, object : OnSearchableDialog {

            override fun onItemSelected(selection: String?) {

                if (selection.equals("yes", ignoreCase = true)) {
                    PrintLog.mPrintService = mPrintService
                    callDevice()
                } else {
                    ReturnActivity.isReturnDone = true
                    Toast.makeText(
                        this@PalletReturnActivity,
                        "Return Pallet Added successfully!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    finish()
                }
            }
        })


    }

    fun createPrintReturnData(invNum: String?, mType: String): JSONArray? {
        val jArr = JSONArray()
        try {
            var totalGrossAmount = 0F
            var totalDiscount = 0F
            var totalNet = 0F
            var totalVat1 = 0F
            var totalAmount = 0F
            var totalExciseAmount = 0F
            var totalQTy = 0
            val jInter = JSONArray()
            val jDict = JSONObject()
            jDict.put(AppConstants.REQUEST, AppConstants.PALLET_RETURN_REQUEST)
            val mainArr = JSONObject()
            var routeCode =
                mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode
            var vanName = routeCode.replace("Zone", "Vansales")
            mainArr.put("ROUTE", vanName)
            mainArr.put("DOC DATE", Utils.formatDate(Date(), "dd-MM-yyyy"))
            mainArr.put("TIME", Utils.formatTime(Date(), "hh:mm"))
            mainArr.put("SALESMAN", mPreferenceManager!!.getString(AppConstants.DRIVERNAME))
            mainArr.put("CONTACTNO", "-")
            mainArr.put("DOCUMENT NO", invNum) //Load Summary No
            mainArr.put("TRIP START DATE", mPreferenceManager!!.getTripDate())
            mainArr.put("supervisorname", "-")
            mainArr.put("mType", mType) //Invoice No
            mainArr.put(
                "CUSTOMER",
                customerObj!!.userDetail.firstname
            )
            mainArr.put("CUSTOMERID", customerObj!!.customer_code)
            mainArr.put("CUSTOMER LAST", customerObj!!.userDetail.lastname)
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


            if (customerObj!!.customer_phone != null) {
                mainArr.put("Mobile", "" + customerObj!!.customer_phone)
            } else {
                mainArr.put("Mobile", "")
            }
            mainArr.put("CITY", db!!.getCustomerRegion(customerObj!!.user_id.toString()))
            mainArr.put("Country", "UAE")
            var trnNo = db!!.getCustomerTRNNo(customerObj!!.customer_code)
            mainArr.put("TRN", trnNo)
            mainArr.put("LPO", "")
            mainArr.put("OrderReference", "")
            if (customerObj!!.customer_phone != null) {
                mainArr.put("Email", customerObj!!.userDetail.email)
            } else {
                mainArr.put("Email", "")
            }

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
            for (mUOM in returnList!!) {

                //Add Base UOM
                val data = JSONArray()
                data.put("" + (jData.length() + 1))
                data.put(mUOM.item_name) // DESC
                data.put(mUOM.invoice_number) // Inv.No
                data.put("0.0") // Inv Amount
                data.put("NUM") // UOM
                data.put(mUOM.enterQty) //  QTY
                data.put("0.0") // Unit Price
                data.put("0.0") // Gross
                data.put("0.0") // Item Excise
                data.put("5") // vat %
                data.put("0.0") // Net
                data.put("0.0") // Vat Amt
                data.put("0.0") // Total
                data.put("0.0") // barcode
                var plu = ""
                data.put("" + plu) // barcode

                jData.put(data)

                totalQTy += mUOM.enterQty.toInt()

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

            var finalNet = totalNet - totalExciseAmount
            val totalObj = JSONObject()
            totalObj.put("VAT", Utils.twoDigit(totalVat1))
            totalObj.put("NET", Utils.twoDigit(finalNet))
            totalObj.put("EXCISE", Utils.twoDigit(totalExciseAmount))
            totalObj.put("Gross Total", Utils.twoDigit(totalGrossAmount))
            totalObj.put("Discount", Utils.twoDigit(totalDiscount))
            totalObj.put("Total Amount", Utils.twoDigit(totalAmount))
            totalObj.put("Total Qty", "" + totalQTy)
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
            Utils.logData(
                this@PalletReturnActivity,
                e.message
            )
        }
        return jArr
    }


    override fun onStart() {
        super.onStart()

        // If BT is not on, request that it be enabled.
        // setupPrint() will then be called during onActivityResult
        if (!mBluetoothAdapter!!.isEnabled) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT)
            // Otherwise, setup the chat session
        } else {
            if (mPrintService == null) setupPrint()
        }
    }

    private fun setupPrint() {
        mPrintService = BluetoothPrintService(mHandler)
        mWoosim = WoosimCustomService(mHandler)
    }

    private val mHandler = MyHandler(this)

    private class MyHandler internal constructor(activity: PalletReturnActivity) :
        Handler() {
        private val mActivity: WeakReference<PalletReturnActivity>

        init {
            mActivity = WeakReference<PalletReturnActivity>(activity)
        }

        override fun handleMessage(msg: Message) {
            val activity: PalletReturnActivity? = mActivity.get()
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
                this@PalletReturnActivity,
                this@PalletReturnActivity
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
                this@PalletReturnActivity,
                this@PalletReturnActivity
            )
            `object`.execute("", jsonArray)

        }, 3000)
    }

    private fun sendData(data: ByteArray) {
        // Check that we're actually connected before trying printing
        if (mPrintService!!.getState() !== BluetoothPrintService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show()
            return
        }
        // Check that there's actually something to send
        if (data.isNotEmpty()) mPrintService!!.write(data)
    }

}