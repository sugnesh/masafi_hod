package com.engear.vansales.fragments

import android.app.AlertDialog
import android.app.SearchManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.SEARCH_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.MenuItemCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.engear.vansales.R
import com.engear.vansales.activities.LoadConfirmActivity
import com.engear.vansales.adapter.LoadAdapter
import com.engear.vansales.base.BaseActivity.Companion.userInfo
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.common.SharedPreferenceManager
import com.engear.vansales.databinding.FragmentLoadBinding
import com.engear.vansales.firebase.MyFirebaseMessagingService
import com.engear.vansales.localDB.DatabaseHandler
import com.engear.vansales.model.BatchdetailsList
import com.engear.vansales.model.LoadData
import com.engear.vansales.model.LoadList
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 * Use the [LoadFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoadFragment : Fragment() {

    var binding: FragmentLoadBinding? = null
    var allLoadList: ArrayList<LoadList> = ArrayList()
    var allLoadBatchItem: MutableList<BatchdetailsList> = mutableListOf()
    var db: DatabaseHandler? = null
    var mAdapter: LoadAdapter? = null
    var mPreferenceManager: SharedPreferenceManager? = null

    var warhouse_lat: String = ""
    var warehouse_long: String = ""
    var warehouse_radius: String = ""

    var salesman_current_lat: String = ""
    var salesman_current_long: String = ""
    var salesmen_current_radius: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_load, container, false
        )

        val view = binding!!.root

        db = DatabaseHandler(activity!!)
        mPreferenceManager = SharedPreferenceManager(activity!!)

        userInfo = mPreferenceManager!!.getUserInfo()
        warhouse_lat = mPreferenceManager!!.getString(AppConstants.WAREHOUSE_LAT).toString()
        warehouse_long = mPreferenceManager!!.getString(AppConstants.WAREHOUSE_LONG).toString()
        warehouse_radius =
            mPreferenceManager!!.getString(AppConstants.WAREHOUSE_RADIUS).toString()
        binding!!.swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
//            if (mPreferenceManager!!.getString(AppConstants.ROLE) == "4") {
//                getDeliveryLoad()
//            } else {
                getLoad()
           // }
        })
        checkPermision()
        return view
    }

    var result_check: Float = 0.0f
    fun checkPermision() {
        salesman_current_lat = AppConstants.Latitude.toString()
        salesman_current_long = AppConstants.Longitude.toString()
        /* warhouse_lat="23.0256989"
         warehouse_long="72.5395265"*/
        var results = FloatArray(1)
        Location.distanceBetween(
            warhouse_lat.toDouble(),
            warehouse_long.toDouble(),
            salesman_current_lat.toDouble(),
            salesman_current_long.toDouble(),
            results
        )
        result_check = results[0]

        mPreferenceManager!!.setString(
            AppConstants.SALESMENCURRENTRADIUS,
            result_check.toString()
        )
        /* if (!Utils.canGetLocation) {
             Utils.initialiseLocationVariables(
                 activity,
                 object : Utils.LocationUpdate {
                     override fun onLocationDetect(isDetect: Boolean) {
                         if (isDetect) {

                             *//*Utils.logData(
                                activity,
                                "Location Fetch:" + AppConstants.Latitude.toString() + " ," +
                                        AppConstants.Longitude+","+results[0].toString()
                            )*//*
                        }
                    }
                })
            Utils.startLocationUpdates(activity)
        }*/
    }

    private fun setUpRecycler() {

        binding!!.rvLoadList.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        mAdapter = LoadAdapter(activity!!, allLoadList)
        binding!!.rvLoadList.adapter = mAdapter


    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity!!.unregisterReceiver(broadcastReceiver_Load)
        LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(broadcastReceiver_Load)
        activity!!.registerReceiver(
            broadcastReceiver_Load,
            IntentFilter(MyFirebaseMessagingService.BROADCAST_ACTION)
        )
    }

    override fun onStop() {
        super.onStop()
        activity!!.unregisterReceiver(broadcastReceiver_Load)
        LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(broadcastReceiver_Load)
        activity!!.registerReceiver(
            broadcastReceiver_Load,
            IntentFilter(MyFirebaseMessagingService.BROADCAST_ACTION)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        activity!!.unregisterReceiver(broadcastReceiver_Load)
        LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(broadcastReceiver_Load)
        activity!!.registerReceiver(
            broadcastReceiver_Load,
            IntentFilter(MyFirebaseMessagingService.BROADCAST_ACTION)
        )
    }

    override fun onDetach() {
        super.onDetach()
        activity!!.unregisterReceiver(broadcastReceiver_Load)
        LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(broadcastReceiver_Load)
        activity!!.registerReceiver(
            broadcastReceiver_Load,
            IntentFilter(MyFirebaseMessagingService.BROADCAST_ACTION)
        )
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(broadcastReceiver_Load)
        activity!!.registerReceiver(
            broadcastReceiver_Load,
            IntentFilter(MyFirebaseMessagingService.BROADCAST_ACTION)
        )
    }

    private fun getAllLoad() {
        allLoadList.clear()
        allLoadList = db!!.getAllLoad()
        setUpRecycler()
    }

    override fun onResume() {
        super.onResume()
        getAllLoad()
        activity!!.registerReceiver(
            broadcastReceiver_Load,
            IntentFilter(MyFirebaseMessagingService.BROADCAST_ACTION)
        )
    }

    companion object {

        fun newInstance(): LoadFragment {
            return LoadFragment()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_load, menu)

        val color: Int
        color = resources.getColor(R.color.colorWhite)

        var drawable =
            menu.findItem(R.id.action_search).icon

        drawable = DrawableCompat.wrap(drawable!!)
        DrawableCompat.setTint(drawable, color)
        menu.findItem(R.id.action_search).icon = drawable

        val searchView: SearchView =
            MenuItemCompat.getActionView(menu.findItem(R.id.action_search)) as SearchView
        val searchManager = activity!!.getSystemService(SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity!!.componentName))
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
    }

    /*  override fun onOptionsItemSelected(item: MenuItem): Boolean {
          when (item.itemId) {
              R.id.action_scan -> {
                  startActivity(
                      Intent(activity, SimpleScannerActivity::class.java)
                  )
                  return true
              }
          }
          return false
      }*/

    private fun getLoad() {

        var date = mPreferenceManager!!.getString(AppConstants.STARTDATE)
        if (date!!.isEmpty()) {
            date = Utils.getCurrentDate()
        }
        val jObj = JsonObject()
        jObj.addProperty("route_id", userInfo!!.data.userInfo.salesmanInfo.routeId)
        jObj.addProperty("load_date", date)

        ApiClient.getClient(activity!!)
            .getLoadList(
                "Bearer ${userInfo!!.data.accessToken}",
                MyApplication.X_DOMAIN, jObj
            )
            .enqueue(object : Callback<LoadData> {
                override fun onResponse(
                    call: Call<LoadData>,
                    response: Response<LoadData>
                ) {

                    if (response.isSuccessful) {
                        if (response.body()!!.status!!) {
                            db!!.insertLoad(response.body()!!.data!!)
                        }
                    }

                    getAllLoad()
                    binding!!.swipeRefreshLayout.isRefreshing = false
                }

                override fun onFailure(call: Call<LoadData>, error: Throwable) {
                    //Utils.logData(this@DownloadingDataActivity, "Journey Plan Response")
                    binding!!.swipeRefreshLayout.isRefreshing = false
                }
            })
    }

    private fun getDeliveryLoad() {

        var date = mPreferenceManager!!.getString(AppConstants.STARTDATE)
        if (date!!.isEmpty()) {
            date = Utils.getCurrentDate()
        }
        val jObj = JsonObject()
        jObj.addProperty("salesman_id", userInfo!!.data.userInfo.salesmanInfo.userId)
        jObj.addProperty("date", date)

        ApiClient.getClient(activity!!)
            .getDeliveryLoadList(
                "Bearer ${userInfo!!.data.accessToken}",
                MyApplication.X_DOMAIN, jObj
            )
            .enqueue(object : Callback<LoadData> {
                override fun onResponse(
                    call: Call<LoadData>,
                    response: Response<LoadData>
                ) {

                    if (response.isSuccessful) {
                        if (response.body()!!.status!!) {
                            db!!.insertLoad(response.body()!!.data!!)
                        }
                    }

                    getAllLoad()
                    binding!!.swipeRefreshLayout.isRefreshing = false
                }

                override fun onFailure(call: Call<LoadData>, error: Throwable) {
                    //Utils.logData(this@DownloadingDataActivity, "Journey Plan Response")
                    binding!!.swipeRefreshLayout.isRefreshing = false
                }
            })
    }

    private fun rejectReasonDialog() {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Alert")
            .setMessage("Your request has been rejected by supervisor!")
            .setCancelable(false)
            .setPositiveButton(
                "Ok"
            ) { dialog, which -> dialog.dismiss() }
        // create alert dialog
        val alertDialog = alertDialogBuilder.create()
        // show it
        alertDialog.show()
    }

    private fun acceptReasonDialog() {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Alert")
            .setMessage("Your request has been Approve by supervisor!")
            .setCancelable(false)
            .setPositiveButton(
                "Ok"
            ) { dialog, which ->
                dialog.dismiss()
                startActivity(Intent(context, LoadConfirmActivity::class.java).apply {
                    this.putExtra("loadNo", mAdapter!!.strloadNumber)
                    this.putExtra("loadType", mAdapter!!.strLoadType)
                    this.putExtra("loadID", mAdapter!!.strLoadId)
                })
            }
        // create alert dialog
        val alertDialog = alertDialogBuilder.create()
        // show it
        alertDialog.show()
    }

    val broadcastReceiver_Load = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            if (mAdapter!!.mProgressDialog != null) {
                if (mAdapter!!.mProgressDialog!!.isShowing)
                    mAdapter!!.mProgressDialog!!.dismiss()
            }

            if (intent.getStringExtra("status") != null) {
                if (intent.getStringExtra("status") == "Approve") {
                    try {
                        if (intent.hasExtra("status")) {
                            mAdapter!!.mProgressDialog!!.dismiss()
                            acceptReasonDialog()
                        } else {
                            mAdapter!!.mProgressDialog!!.dismiss()
                            acceptReasonDialog()
                        }

                    } catch (e: Exception) {
                        e.toString()
                    }
                } else {
                    if (intent.hasExtra("reason")) {
                        try {
                            mAdapter!!.mProgressDialog!!.dismiss()
                        } catch (e: Exception) {
                            e.toString()
                        }
                        try {
                            rejectReasonDialog()
                        } catch (e: Exception) {
                            e.toString()
                        }
                    }

                }
            } else {
                Utils.displayAlert(
                    context,
                    "Your request has been rejected by supervisor!"
                )
            }
        }
    }

}