package com.engear.vansales.activities.picker

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.R
import com.engear.vansales.adapter.ViewPagerAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityPickingBinding
import com.engear.vansales.databinding.DialogSearchableSpinnerBinding
import com.engear.vansales.fragments.LoadFragment
import com.engear.vansales.fragments.UnloadFragment
import com.engear.vansales.fragments.VanStockFragment
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.PickerLoadData
import com.engear.vansales.model.PickingRouteListData
import com.engear.vansales.model.SupervisorUserData
import com.engear.vansales.model.UserData
import com.engear.vansales.printer.BluetoothPrintService
import com.engear.vansales.printer.WoosimCustomService
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class PickingActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivityPickingBinding

    //var allLoadList: ArrayList<PickerDataDetail> = ArrayList()
    var pagerAdapter: ViewPagerAdapter? = null
    private var currentPage = 0

    companion object {

        var userTypeBase = ""
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPickingBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@PickingActivity,
            "On Picking "
        )
        init()
    }

    private fun init() {
        setToolbarTitle("Load Request")
        setPickerNavigationView()

        mUserType = mPreferenceManager!!.getUserTypeLogin()!!

        setStatePageAdapter()
        binding.viewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout))

        binding.tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager!!.currentItem = tab.position
                currentPage = tab.position

                if (currentPage == 1) {
                    (pagerAdapter!!.getItem(1) as AssignedLoadFragment).refreshLoad()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })


    }

    private fun setStatePageAdapter() {
        pagerAdapter = ViewPagerAdapter(supportFragmentManager)
        pagerAdapter!!.addFragment(AllLoadRequestFragment(), "ALL")
        pagerAdapter!!.addFragment(AssignedLoadFragment(), "Assigned")
        binding.viewPager.adapter = pagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager, true)

    }

    override fun onItemsClick(position: Int, tag: String) {
        if (tag == "Assign") {
            if (pagerAdapter != null) {
                if (currentPage == 0) {
                    (pagerAdapter!!.getItem(0) as AllLoadRequestFragment).showAssignedDialog(
                        position
                    )
                }
            }
        } else if (tag == "Load") {
            if (pagerAdapter != null) {
                if (currentPage == 0) {
                    (pagerAdapter!!.getItem(0) as AllLoadRequestFragment).openLoadDetail(
                        position
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (pagerAdapter != null) {
            if (currentPage == 0) {
                (pagerAdapter!!.getItem(0) as AllLoadRequestFragment).refreshLoad()
            } else if (currentPage == 1) {
                (pagerAdapter!!.getItem(1) as AssignedLoadFragment).refreshLoad()
            }
        }

    }

}