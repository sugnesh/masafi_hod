package com.engear.vansales.merchandiser

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.R
import com.engear.vansales.activities.SimpleScannerActivity
import com.engear.vansales.adapter.AssetsAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityAssetListBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.Assets
import com.engear.vansales.model.AsstesData
import com.engear.vansales.model.CustomerData
import com.engear.vansales.model.VisitedList
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AssetListActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivityAssetListBinding
    var customerCode: String? = null
    var customerObj: CustomerData.DataCustomer? = null
    private var allData: ArrayList<Assets> = ArrayList()
    var mAdapter: AssetsAdapter? = null
    var visitId: String? = null
    var activityId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAssetListBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {
        setToolbarTitle("Asset Tracking")

        customerCode = intent.getStringExtra("customer_code")
        customerObj = db!!.getCustomerDetail(customerCode!!)
        setActivity()

        getData()

        binding.btnRefresh.setOnClickListener {
            getAssetsList()
        }
    }

    private fun getData() {
        allData = db!!.getAllAssetsList(customerObj!!.userDetail.id.toString())
        setAdapter()
    }

    private fun setActivity() {

        val mVisited = VisitedList()
        mVisited.customerId = customerObj!!.userDetail.id.toString()
        mVisited.customerCode = customerCode
        mVisited.activityName = "Asset Tracking"
        mVisited.activityAction = ""
        mVisited.startTime = Utils.getCurrentTime()
        mVisited.endTime = ""
        activityId = Utils.getVisitActivityNo(customerObj!!.userDetail.id.toString(), "13")
        mVisited.activityId = activityId
        visitId = db!!.getCustomerVisitId(customerObj!!.userDetail.id.toString())
        mVisited.visitedOffId = visitId

        db!!.insertVisitedCustomerActivity(mVisited)
    }

    private fun setAdapter() {
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = AssetsAdapter(this, allData)
        binding.rvList.adapter = mAdapter
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_load, menu)

        val color: Int
        color = resources.getColor(R.color.colorWhite)

        var drawable =
            menu!!.findItem(R.id.action_search).icon

        drawable = DrawableCompat.wrap(drawable!!)
        DrawableCompat.setTint(drawable, color)
        menu!!.findItem(R.id.action_search).icon = drawable

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
            android.R.id.home -> {
                var action = ""
                action = ""
                var endTime = Utils.getCurrentTime()
                db!!.updateCustomerVisitActivity(endTime, action, visitId!!, activityId!!)
                finish()
                return true
            }
        }
        return false
    }

    override fun onItemsClick(position: Int, tag: String) {
        startActivity(Intent(me, AssetDetailActivity::class.java).apply {
            this.putExtra("customer_code", customerCode)
            this.putExtra("assets_id", allData[position].assetsId)
        })
    }

    private fun getAssetsList() {

        showOnlyProgressDialog()

        ApiClient.getClient(this).getCustomerAssets(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN,
            mPreferenceManager!!.getUserId()!!.toInt()
        ).enqueue(object : Callback<AsstesData> {
            override fun onResponse(
                call: Call<AsstesData>,
                response: Response<AsstesData>
            ) {
                if (response.isSuccessful) {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                    if (response.body()!!.status!!) {
                        db!!.insertCustomerAssets(response.body()!!.data)
                    }
                }
                dismissDialog()
                getData()
            }

            override fun onFailure(call: Call<AsstesData>, error: Throwable) {
                //Utils.logData(this@DownloadingDataActivity, "Order Type Failed")
                dismissDialog()
            }
        })
    }

}