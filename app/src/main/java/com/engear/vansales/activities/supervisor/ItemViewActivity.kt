package com.engear.vansales.activities.supervisor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.*
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityItemViewBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.*
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ItemViewActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivityItemViewBinding
    var mType: String = ""
    var itemlist: List<ItemMonitoring.Data> = ArrayList()
    var itemLoadlist: List<ItemLoadSPMonitoring.Data> = ArrayList()
    var itemUnLoadlist: List<ItemSPMonitoring.Data> = ArrayList()
    var categorylist: List<CategoryMonitoring.Data> = ArrayList()
    var brandlist: List<BrandMonitoring.Data> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemViewBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )

        mType = intent.getStringExtra("Type")!!

        if (mType == "itemwise") {
            getItemWiseSales()
            setToolbarTitle("Itemwise Sales")
        } else if (mType == "load") {
            getItemLoad()
            setToolbarTitle("Loaded Item")
        } else if (mType == "unload") {
            getItemUnloaded()
            setToolbarTitle("Unloaded Item")
        } else if (mType == "slow") {
            getItemSlowMove()
            setToolbarTitle("Slow Moving Item")
        } else if (mType == "fast") {
            getItemFastMove()
            setToolbarTitle("Fast Moving Item")
        } else if (mType == "category") {
            getCategoryWiseSales()
            setToolbarTitle("Category wise Sales")
        } else if (mType == "brand") {
            getBrandWiseSales()
            setToolbarTitle("Brand wise Sales")
        }
    }

    private fun setUpCategoryRecycler() {
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = CategorySalesAdapter(this, categorylist)
    }

    private fun setUpBrandRecycler() {
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = BrandSalesAdapter(this, brandlist)
    }

    private fun setUpRecycler() {
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = ItemSalesAdapter(this, itemlist, mType)
    }

    private fun setUpLoadRecycler() {
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = ItemUnLoadAdapter(this, itemUnLoadlist, mType)
    }
    private fun setUpLoadMainRecycler() {
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = ItemLoadAdapter(this, itemLoadlist, mType)
    }

    private fun getItemWiseSales() {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty("user_id", intent.getStringExtra("salesmanId"))
        jObj.addProperty("role_id", "1")
        jObj.addProperty("from_date", Utils.getCurrentDate())
        jObj.addProperty("to_date", Utils.getCurrentDate())
        Log.e("Item request", jObj.toString())

        ApiClient.getClient(this)
            .getItemWiseSales(
                "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
                MyApplication.X_DOMAIN,
                intent.getStringExtra("salesmanId")!!,
                "1",
                Utils.getCurrentDate(),
                Utils.getCurrentDate()
            )
            .enqueue(object :
                Callback<ItemMonitoring> {
                override fun onResponse(
                    call: Call<ItemMonitoring>,
                    response: Response<ItemMonitoring>
                ) {

                    dismissDialog()

                    if (response.isSuccessful) {
                        Utils.logData(
                            this@ItemViewActivity,
                            "Item  Response ${response.body().toString()}"
                        )
                        itemlist = response.body()!!.data
                    }
                    setUpRecycler()
                }

                override fun onFailure(call: Call<ItemMonitoring>, error: Throwable) {
                    // Fail to Post
                    dismissDialog()
                    setUpRecycler()
                    if (error.message != null) {
                        Utils.logData(
                            this@ItemViewActivity,
                            "Item  Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@ItemViewActivity,
                            "Item  Response API Fail"
                        )
                    }
                }
            })
    }

    private fun getItemLoad() {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty("user_id", intent.getStringExtra("salesmanId"))
        jObj.addProperty("role_id", "1")
        jObj.addProperty("from_date", Utils.getCurrentDate())
        jObj.addProperty("to_date", Utils.getCurrentDate())
        Log.e("Item request", jObj.toString())

        ApiClient.getClient(this)
            .getLoadedItem(
                "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
                MyApplication.X_DOMAIN,
                intent.getStringExtra("salesmanId")!!,
                "1",
                Utils.getCurrentDate(),
                Utils.getCurrentDate()
            )
            .enqueue(object :
                Callback<ItemLoadSPMonitoring> {
                override fun onResponse(
                    call: Call<ItemLoadSPMonitoring>,
                    response: Response<ItemLoadSPMonitoring>
                ) {

                    dismissDialog()

                    if (response.isSuccessful) {
                        Utils.logData(
                            this@ItemViewActivity,
                            "Item  Response ${response.body().toString()}"
                        )
                        itemLoadlist = response.body()!!.data
                    }
                    setUpLoadMainRecycler()
                }

                override fun onFailure(call: Call<ItemLoadSPMonitoring>, error: Throwable) {
                    // Fail to Post
                    dismissDialog()
                    setUpLoadMainRecycler()
                    if (error.message != null) {
                        Utils.logData(
                            this@ItemViewActivity,
                            "Item  Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@ItemViewActivity,
                            "Item  Response API Fail"
                        )
                    }
                }
            })
    }

    private fun getItemUnloaded() {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty("user_id", intent.getStringExtra("salesmanId"))
        jObj.addProperty("role_id", "1")
        jObj.addProperty("from_date", Utils.getCurrentDate())
        jObj.addProperty("to_date", Utils.getCurrentDate())
        Log.e("Item request", jObj.toString())

        ApiClient.getClient(this)
            .getUnLoadedItem(
                "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
                MyApplication.X_DOMAIN,
                intent.getStringExtra("salesmanId")!!,
                "1",
                Utils.getCurrentDate(),
                Utils.getCurrentDate()
            )
            .enqueue(object :
                Callback<ItemSPMonitoring> {
                override fun onResponse(
                    call: Call<ItemSPMonitoring>,
                    response: Response<ItemSPMonitoring>
                ) {

                    dismissDialog()

                    if (response.isSuccessful) {
                        Utils.logData(
                            this@ItemViewActivity,
                            "Item  Response ${response.body().toString()}"
                        )
                        itemUnLoadlist = response.body()!!.data
                    }
                    setUpLoadRecycler()
                }

                override fun onFailure(call: Call<ItemSPMonitoring>, error: Throwable) {
                    // Fail to Post
                    dismissDialog()
                    setUpLoadRecycler()
                    if (error.message != null) {
                        Utils.logData(
                            this@ItemViewActivity,
                            "Item  Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@ItemViewActivity,
                            "Item  Response API Fail"
                        )
                    }
                }
            })
    }

    private fun getItemSlowMove() {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty("user_id", intent.getStringExtra("salesmanId"))
        jObj.addProperty("role_id", "1")
        jObj.addProperty("from_date", Utils.getCurrentDate())
        jObj.addProperty("to_date", Utils.getCurrentDate())
        Log.e("Item request", jObj.toString())

        ApiClient.getClient(this)
            .getSlowMoveItem(
                "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
                MyApplication.X_DOMAIN,
                intent.getStringExtra("salesmanId")!!,
                "1",
                Utils.getCurrentDate(),
                Utils.getCurrentDate()
            )
            .enqueue(object :
                Callback<ItemMonitoring> {
                override fun onResponse(
                    call: Call<ItemMonitoring>,
                    response: Response<ItemMonitoring>
                ) {

                    dismissDialog()

                    if (response.isSuccessful) {
                        Utils.logData(
                            this@ItemViewActivity,
                            "Item  Response ${response.body().toString()}"
                        )
                        itemlist = response.body()!!.data
                    }
                    setUpRecycler()
                }

                override fun onFailure(call: Call<ItemMonitoring>, error: Throwable) {
                    // Fail to Post
                    dismissDialog()
                    setUpRecycler()
                    if (error.message != null) {
                        Utils.logData(
                            this@ItemViewActivity,
                            "Item  Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@ItemViewActivity,
                            "Item  Response API Fail"
                        )
                    }
                }
            })
    }

    private fun getItemFastMove() {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty("user_id", intent.getStringExtra("salesmanId"))
        jObj.addProperty("role_id", "1")
        jObj.addProperty("from_date", Utils.getCurrentDate())
        jObj.addProperty("to_date", Utils.getCurrentDate())
        Log.e("Item request", jObj.toString())

        ApiClient.getClient(this)
            .getFastMoveItem(
                "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
                MyApplication.X_DOMAIN,
                intent.getStringExtra("salesmanId")!!,
                "1",
                Utils.getCurrentDate(),
                Utils.getCurrentDate()
            )
            .enqueue(object :
                Callback<ItemMonitoring> {
                override fun onResponse(
                    call: Call<ItemMonitoring>,
                    response: Response<ItemMonitoring>
                ) {

                    dismissDialog()

                    if (response.isSuccessful) {
                        Utils.logData(
                            this@ItemViewActivity,
                            "Item  Response ${response.body().toString()}"
                        )
                        itemlist = response.body()!!.data
                    }
                    setUpRecycler()
                }

                override fun onFailure(call: Call<ItemMonitoring>, error: Throwable) {
                    // Fail to Post
                    dismissDialog()
                    setUpRecycler()
                    if (error.message != null) {
                        Utils.logData(
                            this@ItemViewActivity,
                            "Item  Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@ItemViewActivity,
                            "Item  Response API Fail"
                        )
                    }
                }
            })
    }

    private fun getCategoryWiseSales() {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty("user_id", intent.getStringExtra("salesmanId"))
        jObj.addProperty("role_id", "1")
        jObj.addProperty("from_date", Utils.getCurrentDate())
        jObj.addProperty("to_date", Utils.getCurrentDate())
        Log.e("Item request", jObj.toString())

        ApiClient.getClient(this)
            .getCategoryWiseSales(
                "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
                MyApplication.X_DOMAIN,
                intent.getStringExtra("salesmanId")!!,
                "1",
                Utils.getCurrentDate(),
                Utils.getCurrentDate()
            )
            .enqueue(object :
                Callback<CategoryMonitoring> {
                override fun onResponse(
                    call: Call<CategoryMonitoring>,
                    response: Response<CategoryMonitoring>
                ) {

                    dismissDialog()

                    if (response.isSuccessful) {
                        Utils.logData(
                            this@ItemViewActivity,
                            "Item  Response ${response.body().toString()}"
                        )
                        categorylist = response.body()!!.data
                    }
                    setUpCategoryRecycler()
                }

                override fun onFailure(call: Call<CategoryMonitoring>, error: Throwable) {
                    // Fail to Post
                    dismissDialog()
                    setUpCategoryRecycler()
                    if (error.message != null) {
                        Utils.logData(
                            this@ItemViewActivity,
                            "Item  Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@ItemViewActivity,
                            "Item  Response API Fail"
                        )
                    }
                }
            })
    }

    private fun getBrandWiseSales() {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty("user_id", intent.getStringExtra("salesmanId"))
        jObj.addProperty("role_id", "1")
        jObj.addProperty("from_date", Utils.getCurrentDate())
        jObj.addProperty("to_date", Utils.getCurrentDate())
        Log.e("Item request", jObj.toString())

        ApiClient.getClient(this)
            .getBrandWiseSales(
                "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
                MyApplication.X_DOMAIN,
                intent.getStringExtra("salesmanId")!!,
                "1",
                Utils.getCurrentDate(),
                Utils.getCurrentDate()
            )
            .enqueue(object :
                Callback<BrandMonitoring> {
                override fun onResponse(
                    call: Call<BrandMonitoring>,
                    response: Response<BrandMonitoring>
                ) {

                    dismissDialog()

                    if (response.isSuccessful) {
                        Utils.logData(
                            this@ItemViewActivity,
                            "Item  Response ${response.body().toString()}"
                        )
                        brandlist = response.body()!!.data
                    }
                    setUpBrandRecycler()
                }

                override fun onFailure(call: Call<BrandMonitoring>, error: Throwable) {
                    // Fail to Post
                    dismissDialog()
                    setUpBrandRecycler()
                    if (error.message != null) {
                        Utils.logData(
                            this@ItemViewActivity,
                            "Item  Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@ItemViewActivity,
                            "Item  Response API Fail"
                        )
                    }
                }
            })
    }

    override fun onItemsClick(position: Int, tag: String) {

    }
}