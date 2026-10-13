package com.engear.vansales.activities.picker

import android.app.ActionBar
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.R
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.common.SharedPreferenceManager
import com.engear.vansales.databinding.FragmentAllLoadRequestBinding
import com.engear.vansales.databinding.FragmentAssignedLoadBinding
import com.engear.vansales.localDB.DatabaseHandler
import com.engear.vansales.model.PickerLoadData
import com.engear.vansales.restAPI.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AssignedLoadFragment : Fragment() {

    var binding: FragmentAssignedLoadBinding? = null
    var db: DatabaseHandler? = null
    var mPreferenceManager: SharedPreferenceManager? = null
    var allLoadList: ArrayList<PickerLoadData.PickerDataDetail> = ArrayList()
    var mAdapter: PickerLoadAdapter? = null
    private var mDialog: Dialog? = null
    var picker_id: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_assigned_load, container, false
        )

        val view = binding!!.root

        db = DatabaseHandler(activity!!)
        mPreferenceManager = SharedPreferenceManager(activity!!)

        picker_id = mPreferenceManager!!.getUserId().toString()

//        if (mPreferenceManager!!.getUserTypeLogin() == "3") {
//            getLoad()
//        }else if (mPreferenceManager!!.getUserTypeLogin() == "4") {
//            getCheckerLoad()
//        }else{
//            getLoaderLoad()
//        }

        return view
    }

    private fun getLoad() {

        showOnlyProgressDialog()

        ApiClient.getClient(activity!!)
            .getAssignedPickerLoadList(
                "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
                MyApplication.X_DOMAIN
            )
            .enqueue(object : Callback<PickerLoadData> {
                override fun onResponse(
                    call: Call<PickerLoadData>,
                    response: Response<PickerLoadData>
                ) {

                    dismissDialog()

                    if (response.isSuccessful) {
                        if (response.body()!!.status!!) {
                            allLoadList =
                                (response.body()!!.data as ArrayList<PickerLoadData.PickerDataDetail>)

                            setUpRecycler()
                        }
                    }

                }

                override fun onFailure(call: Call<PickerLoadData>, error: Throwable) {
                    //Utils.logData(this@DownloadingDataActivity, "Journey Plan Response")
                    dismissDialog()
                }
            })
    }

    private fun getLoaderLoad() {

        showOnlyProgressDialog()
        Log.e("Check Log","Assign checker")
        ApiClient.getClient(activity!!)
            .getAssignedLoaderLoadList(
                "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
                MyApplication.X_DOMAIN
            )
            .enqueue(object : Callback<PickerLoadData> {
                override fun onResponse(
                    call: Call<PickerLoadData>,
                    response: Response<PickerLoadData>
                ) {

                    dismissDialog()

                    if (response.isSuccessful) {
                        if (response.body()!!.status!!) {
                            allLoadList =
                                (response.body()!!.data as ArrayList<PickerLoadData.PickerDataDetail>)

                            setUpRecycler()
                        }
                    }

                }

                override fun onFailure(call: Call<PickerLoadData>, error: Throwable) {
                    //Utils.logData(this@DownloadingDataActivity, "Journey Plan Response")
                    dismissDialog()
                }
            })
    }
    private fun getCheckerLoad() {

        showOnlyProgressDialog()
        Log.e("Check Log","Assign checker")
        ApiClient.getClient(activity!!)
            .getAssignedCheckerLoadList(
                "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
                MyApplication.X_DOMAIN
            )
            .enqueue(object : Callback<PickerLoadData> {
                override fun onResponse(
                    call: Call<PickerLoadData>,
                    response: Response<PickerLoadData>
                ) {

                    dismissDialog()

                    if (response.isSuccessful) {
                        if (response.body()!!.status!!) {
                            allLoadList =
                                (response.body()!!.data as ArrayList<PickerLoadData.PickerDataDetail>)

                            setUpRecycler()
                        }
                    }

                }

                override fun onFailure(call: Call<PickerLoadData>, error: Throwable) {
                    //Utils.logData(this@DownloadingDataActivity, "Journey Plan Response")
                    dismissDialog()
                }
            })
    }
    private fun setUpRecycler() {

        binding!!.rvLoadList.layoutManager =
            LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        mAdapter = PickerLoadAdapter(activity!!, allLoadList)
        binding!!.rvLoadList.adapter = mAdapter

    }

    fun showOnlyProgressDialog() {
        try {
            if (mDialog != null && mDialog!!.isShowing) {
                return
            }
            mDialog = Dialog(activity!!)
            mDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            mDialog!!.setCancelable(false)
            mDialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            mDialog!!.setContentView(R.layout.custom_only_progress_dialog)
            mDialog!!.window!!.setLayout(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT
            )
            mDialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun dismissDialog() {
        try {
            if (mDialog != null && mDialog!!.isShowing) {
                mDialog!!.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun refreshLoad() {

        if (binding != null) {
            if (mPreferenceManager!!.getUserTypeLogin() == "3") {
                getLoad()
            }else if (mPreferenceManager!!.getUserTypeLogin() == "4") {
                getCheckerLoad()
            }else{
                getLoaderLoad()
            }

        }
    }

}