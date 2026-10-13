package com.engear.vansales.merchandiser

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.engear.vansales.R
import com.engear.vansales.adapter.DistributionAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityDistributionListBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.CustomerData
import com.engear.vansales.model.Distribution
import com.engear.vansales.model.DistributionData
import com.engear.vansales.model.VisitedList
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DistributionListActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivityDistributionListBinding
    var customerCode: String? = null
    var brandId: String = ""
    var categoryId: String = ""
    var headbrandId: String = ""
    var headcategoryId: String = ""
    private var customerObj: CustomerData.DataCustomer? = null
    private var allData: ArrayList<Distribution> = ArrayList()
    var mAdapter: DistributionAdapter? = null
    var visitId: String? = null
    var activityId: String? = null

    companion object {
        var isCompleted = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDistributionListBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
        setClickEvent()
    }

    private fun init() {
        Utils.logData(
            me,
            "On Distribution List Screen"
        )
        setToolbarTitle("Shelf Display")

        brandId = intent.getStringExtra("BrandId")!!
        categoryId = intent.getStringExtra("CategoryId")!!
        headbrandId = intent.getStringExtra("HeadBrandId")!!
        headcategoryId = intent.getStringExtra("HeadCategoryId")!!
        customerCode = intent.getStringExtra("customer_code")
        customerObj = db!!.getCustomerDetail(customerCode!!)

        getData()
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    private fun getData() {
        allData = ArrayList()
        allData = db!!.getCustomerDistributionFinalList(
            customerObj!!.userDetail.id.toString()
        )
        setAdapter()
    }

    private fun setClickEvent() {
        binding.btnRefresh.setOnClickListener {
            getDistribution()
        }
    }

    private fun setAdapter() {
        binding.rvList.layoutManager = GridLayoutManager(applicationContext, 2)
        mAdapter = DistributionAdapter(this, allData)
        binding.rvList.adapter = mAdapter
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_scan_search, menu)
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

    override fun onItemsClick(position: Int, tag: String) {
        startActivity(Intent(me, StoreCheckActivity::class.java).apply {
            this.putExtra("customer_code", customerCode)
            this.putExtra("display_tool_id", allData[position].distributionId)
            this.putExtra("name", allData[position].name)
            this.putExtra("CategoryId", categoryId)
            this.putExtra("BrandId", brandId)
            this.putExtra("HeadCategoryId", headcategoryId)
            this.putExtra("HeadBrandId", headbrandId)
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }


    private fun getDistribution() {

        showOnlyProgressDialog()

        ApiClient.getClient(this).getCustomerDistribution(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN,
            mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.routeId
        ).enqueue(object : Callback<DistributionData> {
            override fun onResponse(
                call: Call<DistributionData>,
                response: Response<DistributionData>
            ) {
                if (response.isSuccessful) {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                    if (response.body()!!.status!!) {
                        db!!.deleteShelf()
                        db!!.insertCustomerDistribution(response.body()!!.data)
                    }
                }

                dismissDialog()
                getData()
            }

            override fun onFailure(call: Call<DistributionData>, error: Throwable) {
                //Utils.logData(this@DownloadingDataActivity, "Order Type Failed")
                dismissDialog()
            }
        })
    }


}