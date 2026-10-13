package com.learnina.sample.fragments

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
import com.engear.vansales.adapter.CallEndroseAdapter
import com.engear.vansales.common.MyApplication
import com.engear.vansales.common.SharedPreferenceManager
import com.engear.vansales.databinding.InvoiceEndroseFragmentBinding
import com.engear.vansales.model.CallEndrose
import com.engear.vansales.restAPI.ApiClient
import com.learnina.materialverticalstepper.verticalstepper.StepperFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CallsEndroseFragment : StepperFragment() {

  lateinit  var binding : InvoiceEndroseFragmentBinding
    private var list = ArrayList<CallEndrose.Data>()
    var isApiCalled = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.invoice_endrose_fragment, container, false
        )
        setText()
        if (!isApiCalled){
            getData()
        }else{
            setRouteAdapter()
        }
        return binding.root
    }

    fun setRouteAdapter() {
        var adapterRoute =
            CallEndroseAdapter(list)
        val layoutManager = LinearLayoutManager(activity)
        binding.recyclerViewEndrose.layoutManager = layoutManager
        binding.recyclerViewEndrose.itemAnimator = DefaultItemAnimator()
        binding.recyclerViewEndrose.adapter = adapterRoute

    }
    private fun setText(){
        binding.tvcutomercode.text = "Schedule Call"
        binding.tvCustomerName.text = "Total Visit"
        binding.tvInvoceNo.text = "Unschedule Call"
        binding.tvNetSale.text = "Schedule Sale"
        binding.tvTotalsale.text = "Unschedule Sale"
    }


    private fun getData() {
        ApiClient.getClient(activity!!)
            .getInvoiceVisitItemCall(
                "Bearer ${SharedPreferenceManager(activity!!)!!.getSupervisorUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN,
                SessionEndrosementActivity.routeId,"call"
            )
            .enqueue(object :
                Callback<CallEndrose> {
                override fun onResponse(
                    call: Call<CallEndrose>,
                    response: Response<CallEndrose>
                ) {
                    Log.e("calls", response.toString())
//                    Log.e("foc", response.body()!!.string())
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            isApiCalled = true
                            list = response.body()!!.data as ArrayList<CallEndrose.Data>
                            setRouteAdapter()

                        }
                    } else {
                    }

                }

                override fun onFailure(call: Call<CallEndrose>, error: Throwable) {
                    // Fail to Post

                    isApiCalled = false
                }
            })
    }

    override fun checkFieldsValidation(): Boolean {
        return true
    }


}
