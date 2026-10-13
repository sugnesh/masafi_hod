package com.engear.vansales.activities

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.engear.vansales.adapter.ViewPagerAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.DeviceListActivity
import com.engear.vansales.databinding.ActivityManageInventoryBinding
import com.engear.vansales.fragments.LoadFragment
import com.engear.vansales.fragments.UnloadFragment
import com.engear.vansales.fragments.VanStockFragment
import com.engear.vansales.printer.BluetoothPrintService
import com.engear.vansales.printer.PrintLog
import com.engear.vansales.printer.WoosimCustomService
import com.engear.vansales.utils.Utils
import com.google.android.material.tabs.TabLayout
import java.lang.ref.WeakReference

class ManageInventoryActivity : BaseActivity() {

    lateinit var binding: ActivityManageInventoryBinding
    var pagerAdapter: ViewPagerAdapter? = null
    private var currentPage = 0

    private val REQUEST_CONNECT_DEVICE_SECURE = 1
    private val REQUEST_ENABLE_BT = 3
    private var mBluetoothAdapter: BluetoothAdapter? = null


    val MESSAGE_DEVICE_NAME = 1
    val MESSAGE_TOAST = 2
    val MESSAGE_READ = 3

    val DEVICE_NAME = "device_name"
    val TOAST = "toast"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityManageInventoryBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@ManageInventoryActivity,
            "On Manage Inventory Screen "
        )
        init()
    }

    private fun init() {
        setToolbarTitle("Manage Inventory")
        setNavigationView(mPreferenceManager!!.getFTokenId()!!)

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        setStatePageAdapter()
        binding.viewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout))

        binding.tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager!!.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                currentPage = tab.position
            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }

    private fun setStatePageAdapter() {
        pagerAdapter = ViewPagerAdapter(supportFragmentManager)
        pagerAdapter!!.addFragment(LoadFragment(), "Load")
        pagerAdapter!!.addFragment(VanStockFragment(), "VanStock")
        pagerAdapter!!.addFragment(UnloadFragment(), "Unload")
        binding.viewPager.adapter = pagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager, true)

    }

    fun callPrint()
    {
        PrintLog.mPrintService = mPrintService
        callDevice()
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
            (pagerAdapter!!.getItem(1) as VanStockFragment).callPrint()
        }

    }

    override fun onResume() {
        super.onResume()
        if (pagerAdapter != null) {
            setNavigationView(mPreferenceManager!!.getFTokenId()!!)
            if (currentPage == 2) {
                (pagerAdapter!!.getItem(2) as UnloadFragment).enableDisable()
                (pagerAdapter!!.getItem(1) as VanStockFragment).refreshLoad()
            } else if (currentPage == 0) {
                (pagerAdapter!!.getItem(1) as VanStockFragment).refreshLoad()
            }
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
            (pagerAdapter!!.getItem(1) as VanStockFragment).callPrint()

        }, 3000)
    }

    override fun onStart() {
        super.onStart()

        // If BT is not on, request that it be enabled.
        // setupPrint() will then be called during onActivityResult
        if(mBluetoothAdapter != null)
        {
            if (!mBluetoothAdapter!!.isEnabled) {
                val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT)
                // Otherwise, setup the chat session
            } else {
                if (mPrintService == null) setupPrint()
            }
        }

    }

    private fun setupPrint() {
        mPrintService = BluetoothPrintService(mHandler)
        mWoosim = WoosimCustomService(mHandler)
    }

    private val mHandler = MyHandler(this)

    private class MyHandler internal constructor(activity: ManageInventoryActivity) :
        Handler() {
        private val mActivity: WeakReference<ManageInventoryActivity>

        init {
            mActivity = WeakReference<ManageInventoryActivity>(activity)
        }

        override fun handleMessage(msg: Message) {
            val activity: ManageInventoryActivity? = mActivity.get()
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