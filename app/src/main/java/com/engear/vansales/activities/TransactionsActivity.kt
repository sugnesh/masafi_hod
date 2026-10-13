package com.engear.vansales.activities

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.DeviceListActivity
import com.engear.vansales.databinding.ActivityTransactionsBinding
import com.engear.vansales.databinding.RowItemPostingBinding
import com.engear.vansales.databinding.RowItemTrCusBinding
import com.engear.vansales.interfaces.OnSearchableDialog
import com.engear.vansales.model.Transaction
import com.engear.vansales.printer.BluetoothPrintService
import com.engear.vansales.printer.PrintLog
import com.engear.vansales.printer.WoosimCustomService
import com.engear.vansales.sync.SyncData
import com.engear.vansales.utils.Utils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference

class TransactionsActivity : BaseActivity() {
    lateinit var binding: ActivityTransactionsBinding
    private var arrData: ArrayList<Transaction> = ArrayList<Transaction>()
    var mCustomerCode : String = ""
    private var jsonArray: JSONArray? = null

    private val REQUEST_ENABLE_BT = 3
    private var mBluetoothAdapter: BluetoothAdapter? = null

    // Member object for the print services

    private val REQUEST_CONNECT_DEVICE_SECURE = 1

    val MESSAGE_DEVICE_NAME = 1
    val MESSAGE_TOAST = 2
    val MESSAGE_READ = 3

    val DEVICE_NAME = "device_name"
    val TOAST = "toast"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionsBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {
        Utils.logData(
            this@TransactionsActivity,
            "On  transection Screen "
        )
        setToolbarTitle("Print Transactions")


        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        mCustomerCode = intent.getStringExtra("customer")!!
        arrData = db!!.getAllTransactionsForCustomer(mCustomerCode, "")
        setAdapter()
    }

    fun setAdapter() {
        var mAdapter = TransactionAdapter(this, arrData)
        binding.rvList.adapter = mAdapter
    }

    inner class TransactionAdapter(
        var context: Context,
        var mList: ArrayList<Transaction>?
    ) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding =
                RowItemTrCusBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
            return ViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return mList!!.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val item = mList!![holder.adapterPosition]

            when (item.tr_type) {
                AppConstants.LoadConfirmation_TR -> {
                    (holder.binding).tvType.setText("LC")
                    (holder.binding).tvTransaction.setText(item.tr_invoice_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_SALES_CREATED -> {
                    (holder.binding).tvType.setText("SI")
                    (holder.binding).tvTransaction.setText(item.tr_invoice_id + "\nSite:" + item.tr_customer_num)
                }

                AppConstants.TRANSACTION_TYPES.TT_RETURN_CREATED -> {
                    val returnType = db!!.getReturnType(item.tr_order_id!!)
                    if (returnType.equals("badReturn", ignoreCase = true)) {
                        (holder.binding).tvType.setText("BR")
                    } else {
                        (holder.binding).tvType.setText("GR")
                    }
                    (holder.binding).tvTransaction.setText(item.tr_order_id + "\nSite:" + item.tr_customer_num)
                }

                AppConstants.TRANSACTION_TYPES.TT_RETURN_REQUEST -> {
                    (holder.binding).tvType.setText("RRC")
                    (holder.binding).tvTransaction.setText(item.tr_pyament_id + "\nSite:" + item.tr_customer_num)
                }

                AppConstants.TRANSACTION_TYPES.TT_OREDER_CREATED -> {
                    (holder.binding).tvType.setText("OR")
                    (holder.binding).tvTransaction.setText(item.tr_order_id + "\nSite:" + item.tr_customer_num)
                }

                AppConstants.TRANSACTION_TYPES.TT_LOAD_VARIANCE -> {
                    (holder.binding).tvType.setText("LCV")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_CASH_COLLECTION -> {
                    (holder.binding).tvType.setText("COL")
                    (holder.binding).tvTransaction.setText(item.tr_collection_id + "\nSite:" + item.tr_customer_num)
                }

                AppConstants.TRANSACTION_TYPES.TT_PAYMENT_BY_CHEQUE -> {
                    (holder.binding).tvType.setText("COL")
                    (holder.binding).tvTransaction.setText(item.tr_collection_id + "\nSite:" + item.tr_customer_num)
                }


                AppConstants.TRANSACTION_TYPES.TT_LOAD_CREATE -> {
                    (holder.binding).tvType.setText("LCR")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_DELIVERY_CANCEL -> {
                    (holder.binding).tvType.setText("DLC")
                    (holder.binding).tvTransaction.setText(item.tr_invoice_id)
                    (holder.binding).ivPrint.setVisibility(View.GONE)
                }

                AppConstants.TRANSACTION_TYPES.TT_UNLOAD -> {
                    (holder.binding).tvType.setText("UL")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_UNLOAD_EXPIRY -> {
                    (holder.binding).tvType.setText("VEXP")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_UNLOAD_VARIANCE -> {
                    (holder.binding).tvType.setText("ULV")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_UNLOAD_VAN_RETURN -> {
                    (holder.binding).tvType.setText("ULR")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_EXCHANGE_CREATED -> {
                    (holder.binding).tvType.setText("EXCH")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_ADD_CUSTOEMR_CREATED -> {
                    (holder.binding).tvType.setText("CUS")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                    (holder.binding).ivPrint.setVisibility(View.GONE)
                }

                AppConstants.TRANSACTION_TYPES.TT_SENSURY_SURVEY -> {
                    (holder.binding).tvType.setText("SEN_SRV")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_COMPLAINT_FEEDBACK -> {
                    (holder.binding).tvType.setText("FEED")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_COMPAIGN_FEEDBACK -> {
                    (holder.binding).tvType.setText("CAMP")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_COMPATITOR -> {
                    (holder.binding).tvType.setText("COMP")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_INVENTORY -> {
                    (holder.binding).tvType.setText("INV")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_DISPLAY_TOOL -> {
                    (holder.binding).tvType.setText("TOOL")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_PLANOGRAM -> {
                    (holder.binding).tvType.setText("PLNG")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_ASSETS -> {
                    (holder.binding).tvType.setText("ASST")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_CONS_SURVEY -> {
                    (holder.binding).tvType.setText("C_SRV")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_ASSETS_SURVEY -> {
                    (holder.binding).tvType.setText("A_SRV")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_DISTRIBUTION_SURVEY -> {
                    (holder.binding).tvType.setText("D_SRV")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_SENSURY_SURVEY -> {
                    (holder.binding).tvType.setText("E_SRV")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_DISPLAY_TOOL_IMAGE -> {
                    (holder.binding).tvType.setText("D-IMG")
                    (holder.binding).tvTransaction.setText(item.tr_order_id)
                }

                AppConstants.TRANSACTION_TYPES.TT_INVOICE_CANCEL -> {
                    (holder.binding).tvType.setText("INCL")
                    (holder.binding).tvTransaction.setText(item.tr_invoice_id)
                }
            }



            holder.binding.ivPrint.setOnClickListener {
                try {
                    var jsonString = item.tr_printData
//                    jsonString = Utils.decodeString(jsonString!!.replace("%".toRegex(), "123ABC"))
//                    val jsonObject = JSONObject(jsonString.replace("123ABC".toRegex(), "%"))
                    val jsonObject = JSONObject(jsonString)
                    jsonArray = jsonObject.getJSONArray("data")

                    if (item.tr_type == AppConstants.TRANSACTION_TYPES.TT_SALES_CREATED ||
                        item.tr_type == AppConstants.TRANSACTION_TYPES.TT_RETURN_CREATED
                        || item.tr_type == AppConstants.TRANSACTION_TYPES.TT_CASH_COLLECTION
                        || item.tr_type == AppConstants.TRANSACTION_TYPES.TT_PAYMENT_BY_CHEQUE
                    ) {
                        Utils.dialogCustomerPrint(this@TransactionsActivity, "Customer", object :
                            OnSearchableDialog {

                            override fun onItemSelected(selection: String?) {

                                if (selection.equals("yes", ignoreCase = true)) {

                                    try {
                                        jsonArray!!.getJSONArray(0).getJSONObject(0)
                                            .getJSONObject("mainArr")
                                            .put("mType", "Account")
                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                    }

                                    PrintLog.mPrintService = mPrintService
                                    callDevice()

                                    //callFunction(jsonArray)
                                    //callDevice()


                                } else if (selection.equals("customer", ignoreCase = true)) {

                                    try {
                                        jsonArray!!.getJSONArray(0).getJSONObject(0)
                                            .getJSONObject("mainArr")
                                            .put("mType", "Customer")
                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                    }


                                    PrintLog.mPrintService = mPrintService
                                    callDevice()
                                    //callFunction(jsonArray)
//                                    val `object` = PrintLog(
//                                        this@DataPostingActivity,
//                                        this@DataPostingActivity
//                                    )
//                                    `object`.execute("", jsonArray)
                                }
                            }
                        })
                    } else {
                        Utils.dialogPrint(this@TransactionsActivity, object :
                            OnSearchableDialog {

                            override fun onItemSelected(selection: String?) {

                                if (selection.equals("yes", ignoreCase = true)) {

                                    PrintLog.mPrintService = mPrintService
                                    callDevice()

                                }
                            }
                        })
                    }


                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }


        }


        inner class ViewHolder(val binding: RowItemTrCusBinding) :
            RecyclerView.ViewHolder(binding.root) {
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
                this@TransactionsActivity,
                this@TransactionsActivity
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
                this@TransactionsActivity,
                this@TransactionsActivity
            )
            `object`.execute("", jsonArray)

        }, 3000)
    }

    override fun onStart() {
        super.onStart()

        // If BT is not on, request that it be enabled.
        // setupPrint() will then be called during onActivityResult
        if (!mBluetoothAdapter!!.isEnabled) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableIntent!!, REQUEST_ENABLE_BT)
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

    private class MyHandler internal constructor(activity: TransactionsActivity) :
        Handler() {
        private val mActivity: WeakReference<TransactionsActivity>

        init {
            mActivity = WeakReference<TransactionsActivity>(activity)
        }

        override fun handleMessage(msg: Message) {
            val activity: TransactionsActivity? = mActivity.get()
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

}