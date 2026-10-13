package com.engear.vansales.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.R
import com.engear.vansales.activities.supervisor.SessionEndrosementActivity
import com.engear.vansales.adapter.CarryOverEndroseAdapter
import com.engear.vansales.common.MyApplication
import com.engear.vansales.common.SharedPreferenceManager
import com.engear.vansales.databinding.InvoiceEndroseFragmentBinding
import com.engear.vansales.model.CarryOverEndrose
import com.engear.vansales.restAPI.ApiClient
import com.learnina.materialverticalstepper.verticalstepper.StepperFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CarryOverEndroseFragment : StepperFragment() {

    lateinit var binding: InvoiceEndroseFragmentBinding
    private var list = ArrayList<CarryOverEndrose.Data.Transactiondetail>()
    var isApiCalled = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.invoice_endrose_fragment, container, false
        )
        setText()
        if (!isApiCalled) {
            getData()
        } else {
            setRouteAdapter()
        }
        return binding.root
    }

    private fun setText() {
        binding.tvcutomercode.text = "Item Code"
        binding.tvCustomerName.text = "Item Name"
        binding.tvInvoceNo.text = "UOM"
        binding.tvNetSale.text = "QTY"
        binding.tvTotalsale.visibility = View.GONE
    }

    fun setRouteAdapter() {
        var adapterRoute =
            CarryOverEndroseAdapter(list)
        val layoutManager = LinearLayoutManager(activity)
        binding.recyclerViewEndrose.layoutManager = layoutManager
        binding.recyclerViewEndrose.itemAnimator = DefaultItemAnimator()
        binding.recyclerViewEndrose.adapter = adapterRoute

    }

    private fun getData() {
        ApiClient.getClient(activity!!)
            .getInvoiceVisitItemCarryOver(
                "Bearer ${SharedPreferenceManager(activity!!)!!.getSupervisorUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN,
                SessionEndrosementActivity.routeId, "cov"
            )
            .enqueue(object :
                Callback<CarryOverEndrose> {
                override fun onResponse(
                    call: Call<CarryOverEndrose>,
                    response: Response<CarryOverEndrose>
                ) {
                    Log.e("foc", response.toString())
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            isApiCalled = true
                            if (response.body()!!.data.isNotEmpty()) {
                                list = ArrayList()
                                if (response.body()!!.data.isNotEmpty()) {

                                    for (mCData in response.body()!!.data) {

                                        if (mCData.transactiondetail.isNotEmpty()) {

                                            for (mItemData in mCData.transactiondetail) {
                                                if (mItemData.unload_type == "4") {
                                                    var mTranDeta =
                                                        CarryOverEndrose.Data.Transactiondetail()
                                                    mTranDeta = mItemData
                                                    mTranDeta.code = mCData.code
                                                    list.add(mTranDeta)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            setRouteAdapter()

                        }
                    }

                }

                override fun onFailure(call: Call<CarryOverEndrose>, error: Throwable) {
                    // Fail to Post
                    isApiCalled = false
                }
            })
    }

    override fun checkFieldsValidation(): Boolean {
        return true
    }


}
