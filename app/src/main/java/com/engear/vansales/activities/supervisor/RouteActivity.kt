package com.engear.vansales.activities.supervisor

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.R
import com.engear.vansales.adapter.LobItemAdapter
import com.engear.vansales.adapter.RouteAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityRouteBinding
import com.engear.vansales.databinding.DialogLobItemBinding
import com.engear.vansales.interfaces.OnShopItemClickListener
import com.engear.vansales.interfaces.RouteOnClickListener
import com.engear.vansales.model.ItemData
import com.engear.vansales.model.SupervisorRoute
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class RouteActivity : BaseActivity(),
    RouteOnClickListener, OnShopItemClickListener {

    lateinit var mRouteBinding: ActivityRouteBinding
    private var listRoute = ArrayList<SupervisorRoute.Data>()
    private var itemLobList = ArrayList<ItemData.Data.Lob>()
    private lateinit var adapterRoute: RouteAdapter
    private var routeid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mRouteBinding = ActivityRouteBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {
        setToolbarTitle("Route")
        setNavigationView()
        var data = db!!.getAllRoute()
        Log.e("routesss", Gson().toJson(data))
//        if (data.size > 0) {
//            for (itm in data) {
//                if ((Collections.frequency(listRoute, itm)) < 1) {
//                    listRoute.add(itm)
//                }
//            }
//            setRouteAdapter()
//        } else {
        getRoute()
        //  }

    }

    fun setRouteAdapter() {
        adapterRoute =
            RouteAdapter(listRoute, this, menuSelection, db)
        val layoutManager = GridLayoutManager(applicationContext, 2)
        mRouteBinding.recyclerViewRoute.layoutManager = layoutManager
        mRouteBinding.recyclerViewRoute.itemAnimator = DefaultItemAnimator()
        mRouteBinding.recyclerViewRoute.adapter = adapterRoute

    }

    private fun getRoute() {

        showOnlyProgressDialog()

        ApiClient.getClient(this)
            .getSupervisorRouteList(
                "Bearer ${mPreferenceManager!!.getSupervisorUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN,
                mPreferenceManager!!.getSupervisorUserInfo()!!.data.userInfo.id.toString()
            )
            .enqueue(object :
                Callback<SupervisorRoute> {
                override fun onResponse(
                    call: Call<SupervisorRoute>,
                    response: Response<SupervisorRoute>
                ) {
                    dismissDialog()
//                    Log.e("route",response.body()!!.string())
                    if (response.isSuccessful) {
                        Utils.logData(
                            this@RouteActivity,
                            "Route  Response ${response.body()!!.toString()}"
                        )
                        if (response.body() != null) {
                            var data = response.body()!!.data
                            for (itm in data) {
                                if ((Collections.frequency(listRoute, itm)) < 1) {
                                    listRoute.add(itm)
                                }
                            }
                            Log.e("routelist", Gson().toJson(listRoute))
                            db!!.insertRoute(listRoute)

                            setRouteAdapter()
                        }
                    }

                }

                override fun onFailure(call: Call<SupervisorRoute>, error: Throwable) {
                    // Fail to Post
                    dismissDialog()
                    if (error.message != null) {
                        Utils.logData(
                            this@RouteActivity,
                            "Route  Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@RouteActivity,
                            "Route  Response API Fail"
                        )
                    }
                }
            })
    }

    /**
     * Alert dialog for capture or select from galley
     */
    private fun selectLob() {
        val items = arrayOf<CharSequence>("RICE", "SPICES", "RTC")
        val builder = AlertDialog.Builder(this)
        builder.setItems(items) { dialog, item ->

            if (items[item] == "SPICES") {
                intent = Intent(applicationContext, DailyFeildActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.putExtra("route", routeid)
                intent.putExtra("lob", "69")
                startActivity(intent)

            } else if (items[item] == "RICE") {
                intent = Intent(applicationContext, DailyFeildActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.putExtra("route", routeid)
                intent.putExtra("lob", "67")
                startActivity(intent)
            } else {
                intent = Intent(applicationContext, DailyFeildActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.putExtra("route", routeid)
                intent.putExtra("lob", "68")
                startActivity(intent)
            }
        }
        builder.show()
    }

    private fun showDialog() {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        val binding: DialogLobItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(applicationContext),
            R.layout.dialog_lob_item,
            null,
            false
        )
        dialog.setContentView(binding.root)

        var adapterRoute =
            LobItemAdapter(itemLobList, this)
        val layoutManager = LinearLayoutManager(applicationContext)
        binding.rvList.layoutManager = layoutManager
        binding.rvList.itemAnimator = DefaultItemAnimator()
        binding.rvList.adapter = adapterRoute

        dialog.show()
    }

    override fun onRouteClickListener(route: SupervisorRoute.Data) {

        if (menuSelection == 3) {
            routeid = route.routeId.toString()
            selectLob()
        } else {
            intent = Intent(applicationContext, ViewKPIActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("route", route.salesman.id.toString())
            intent.putExtra("routeId", route.routeId.toString())
            intent.putExtra(
                "salesmanName",
                route.salesman.firstname + " " + route.salesman.lastname
            )
            intent.putExtra("routeName", route.route.routeName)
            startActivity(intent)
        }

    }

    override fun onShopItemClick(position: Int, tag: String, type: String) {

        intent = Intent(applicationContext, DailyFeildActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra("route", routeid)
        intent.putExtra("lob", itemLobList[position].id.toString())
        startActivity(intent)
    }
}