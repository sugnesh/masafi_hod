package com.engear.vansales.activities.picker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.R
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityPickerLoadRequestDetailsBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.interfaces.OnItemUpdateListener
import com.engear.vansales.model.PickerLoadData
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

class PickerLoadRequestDetailsActivity : BaseActivity(), OnItemClickListener,
    OnItemUpdateListener {

    lateinit var binding: ActivityPickerLoadRequestDetailsBinding

    private var loadreuestList: ArrayList<PickerLoadData.PickerDataDetail.LoadRequest>? = null
    private var loadreuestListSend: ArrayList<PickerLoadData.PickerDataDetail.LoadRequest>? =
        ArrayList()
    private var mAdapter: PickerLoadItemAdapter? = null
    var itemPositionId = 0
    var str_uuid: String = ""
    var str_approval: String = ""
    var loadNo: String = ""
    var loaddate: String = ""
    var route_id: String = ""
    var salesman_id: String = ""
    var status: String = ""
    var loadId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPickerLoadRequestDetailsBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@PickerLoadRequestDetailsActivity,
            "On Load request Screen "
        )
        init()
    }

    private fun init() {


        val strObj = intent!!.getStringExtra("loadItem")
        loadNo = intent!!.getStringExtra("loadNo")!!
        loadId = intent!!.getStringExtra("loadId")!!
        loaddate = intent!!.getStringExtra("loadDate")!!
        route_id = intent!!.getStringExtra("routeid")!!
        salesman_id = intent!!.getStringExtra("salesmanid")!!
        val salesman = intent!!.getStringExtra("salesman")!!
        route_id = intent!!.getStringExtra("routeid")!!
        status = intent!!.getStringExtra("status")!!
        str_uuid = intent!!.getStringExtra("uuid")!!
        val listOfUomType: Type =
            object : TypeToken<List<PickerLoadData.PickerDataDetail.LoadRequest?>?>() {}.type

        loadreuestList = Gson().fromJson(strObj, listOfUomType)

        setToolbarTitle("Load No: " + loadNo)

        binding.llBtn.visibility = View.VISIBLE

        setUpRecycler()


        binding.btnConfirm.setOnClickListener {

            val alertDialogBuilder =
                android.app.AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Alert")
                .setMessage("Are you sure you want to Confirm?")
                .setCancelable(false)
                .setPositiveButton(
                    "Yes"
                ) { dialog, which ->
                    if (mPreferenceManager!!.getUserTypeLogin()!! == "3") {
                        getLoadEdit()
                    } else if (mPreferenceManager!!.getUserTypeLogin()!! == "4") {
                        getCheckerLoadEdit()
                    } else {
                        getLoaderLoadConfirm()
                    }
                }.setNegativeButton(
                    "No"
                ) { dialog, which -> dialog.dismiss() }

            alertDialogBuilder.show()


        }

    }


    private fun setUpRecycler() {
        binding!!.rvItemList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = PickerLoadItemAdapter(this, loadreuestList!!, status)
        binding!!.rvItemList.adapter = mAdapter
    }

    override fun onItemsClick(position: Int, tag: String) {

    }

    var mPosition: Int = 0
    override fun onItemsUpdate(position: Int, tag: String) {
        // TODO("Not yet implemented")
        var mIemPosition = getItemPosition(position)
        mPosition = position
        if (loadreuestList!![mIemPosition] != null)
            loadreuestList!![mIemPosition].approval_qty_by_warehouse =
                loadreuestList!![mIemPosition].uomQty
        loadreuestListSend!!.add(loadreuestList!![mIemPosition])
    }

    fun getItemPosition(itemId: Int): Int {

        var position = 0
        itemPositionId = itemId
        for (i in loadreuestList!!.indices) {
            if (loadreuestList!![i].id == itemId) {
                position = i
                break
            }
        }
        return position
    }


    //Load Edit

    private fun getLoadEdit() {

        showOnlyProgressDialog()

        val list = JsonArray()

        for (loadedit in loadreuestList!!) {
            val mItem = JsonObject()

            mItem.addProperty(
                "item_id", loadedit.item_id
            )

            mItem.addProperty(
                "approval_qty_by_supervisor", loadedit.approval_qty_by_supervisor
            )
            mItem.addProperty(
                "item_uom_id", loadedit.item_uom_id
            )
            mItem.addProperty(
                "qty", loadedit.approval_qty_by_warehouse
            )
            mItem.addProperty(
                "requested_qty", loadedit.requested_qty
            )
            mItem.addProperty(
                "variant_id", ""
            )
            mItem.addProperty(
                "item_class_id", ""
            )

            list.add(mItem)
        }
        val jObj = JsonObject()
        jObj.addProperty("load_number", loadNo)
        jObj.add("items", list)

        Log.e("Picker Approval Body", jObj.toString())


        ApiClient.getClient(this)
            .PostLoadEdit(
                "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
                MyApplication.X_DOMAIN,
                jObj
            )
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    //Log.e("Picker Load request", response.raw().request().url().toString())
                    dismissDialog()

                    if (response.isSuccessful) {

                        if (response.body() != null) {
                            Utils.logData(
                                this@PickerLoadRequestDetailsActivity,
                                "Picker Load request ${response.body().toString()}"
                            )
                            Log.e("Picker Load Body", response.body().toString())

                            if (response.body()!!["status"].asBoolean) {

                                Toast.makeText(
                                    this@PickerLoadRequestDetailsActivity,
                                    "Picking Confirm Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    dismissDialog()
                    //  setUpRecycler()
                    if (error.message != null) {
                        Utils.logData(
                            this@PickerLoadRequestDetailsActivity,
                            "Picker Load request  Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@PickerLoadRequestDetailsActivity,
                            "Picker Load request  Response API Fail"
                        )
                    }
                    Toast.makeText(
                        this@PickerLoadRequestDetailsActivity,
                        "Picking Confirm fail",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun getCheckerLoadEdit() {

        showOnlyProgressDialog()

        val list = JsonArray()

        for (loadedit in loadreuestList!!) {
            val mItem = JsonObject()

            mItem.addProperty(
                "item_id", loadedit.item_id
            )

            mItem.addProperty(
                "approval_qty_by_supervisor", loadedit.approval_qty_by_supervisor
            )
            mItem.addProperty(
                "item_uom_id", loadedit.item_uom_id
            )
            mItem.addProperty(
                "qty", loadedit.approval_qty_by_warehouse
            )
            mItem.addProperty(
                "requested_qty", loadedit.requested_qty
            )
            mItem.addProperty(
                "variant_id", ""
            )
            mItem.addProperty(
                "item_class_id", ""
            )

            list.add(mItem)
        }
        val jObj = JsonObject()
        jObj.addProperty("load_number", loadNo)
        jObj.add("items", list)

        Log.e("Checker Approval Body", jObj.toString())


        ApiClient.getClient(this)
            .PostCheckerLoadEdit(
                "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
                MyApplication.X_DOMAIN,
                jObj
            )
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    //Log.e("Checker Load request", response.raw().request().url().toString())
                    dismissDialog()

                    if (response.isSuccessful) {

                        if (response.body() != null) {
                            Utils.logData(
                                this@PickerLoadRequestDetailsActivity,
                                "Checker Load request ${response.body().toString()}"
                            )
                            Log.e("Checker Load Body", response.body().toString())

                            if (response.body()!!["status"].asBoolean) {

                                Toast.makeText(
                                    this@PickerLoadRequestDetailsActivity,
                                    "Checking Confirm Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    dismissDialog()
                    //  setUpRecycler()
                    if (error.message != null) {
                        Utils.logData(
                            this@PickerLoadRequestDetailsActivity,
                            "Checking Load request  Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@PickerLoadRequestDetailsActivity,
                            "Checking Load request  Response API Fail"
                        )
                    }
                    Toast.makeText(
                        this@PickerLoadRequestDetailsActivity,
                        "Checking Confirm fail",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun getLoaderLoadConfirm() {

        showOnlyProgressDialog()

        ApiClient.getClient(this)
            .loaderLoadConfirm(
                "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
                MyApplication.X_DOMAIN,
                loadId
            )
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    //Log.e("Load Confirm", response.raw().request().url().toString())
                    dismissDialog()

                    if (response.isSuccessful) {

                        if (response.body() != null) {
                            Utils.logData(
                                this@PickerLoadRequestDetailsActivity,
                                "Load Confirm ${response.body().toString()}"
                            )
                            Log.e("Load Confirm", response.body().toString())

                            if (response.body()!!["status"].asBoolean) {

                                Toast.makeText(
                                    this@PickerLoadRequestDetailsActivity,
                                    "Load Generated Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    dismissDialog()
                    //  setUpRecycler()
                    if (error.message != null) {
                        Utils.logData(
                            this@PickerLoadRequestDetailsActivity,
                            "Load Confirm  Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@PickerLoadRequestDetailsActivity,
                            "Load Confirm  Response API Fail"
                        )
                    }
                    Toast.makeText(
                        this@PickerLoadRequestDetailsActivity,
                        "Load Confirm fail",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}