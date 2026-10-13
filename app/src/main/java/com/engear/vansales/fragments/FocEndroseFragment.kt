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
import com.engear.vansales.adapter.FOCEndroseAdapter
import com.engear.vansales.common.MyApplication
import com.engear.vansales.common.SharedPreferenceManager
import com.engear.vansales.databinding.InvoiceEndroseFragmentBinding
import com.engear.vansales.model.FocEndrose
import com.engear.vansales.restAPI.ApiClient
import com.learnina.materialverticalstepper.verticalstepper.StepperFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FocEndroseFragment : StepperFragment() {

  lateinit  var binding : InvoiceEndroseFragmentBinding
    private var list = ArrayList<FocEndrose.Data>()
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
            FOCEndroseAdapter(list)
        val layoutManager = LinearLayoutManager(activity)
        binding.recyclerViewEndrose.layoutManager = layoutManager
        binding.recyclerViewEndrose.itemAnimator = DefaultItemAnimator()
        binding.recyclerViewEndrose.adapter = adapterRoute

    }
    private fun setText(){
        binding.tvcutomercode.text = "Invoice No"
        binding.tvCustomerName.text = "Customer Name"
        binding.tvInvoceNo.text = "FOC Qty"
        binding.tvNetSale.text = "FOC UOM"
        binding.tvTotalsale.visibility = View.GONE
    }

    private fun getData() {
        ApiClient.getClient(activity!!)
            .getInvoiceVisitItemDetails(
                "Bearer ${SharedPreferenceManager(activity!!)!!.getSupervisorUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN,
                SessionEndrosementActivity.routeId,"foc"
            )
            .enqueue(object :
                Callback<FocEndrose> {
                override fun onResponse(
                    call: Call<FocEndrose>,
                    response: Response<FocEndrose>
                ) {
                    Log.e("foc", response.toString())
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            isApiCalled = true
                            list = response.body()!!.data as ArrayList<FocEndrose.Data>
                            setRouteAdapter()

                        }
                    } else {

                    }

                }

                override fun onFailure(call: Call<FocEndrose>, error: Throwable) {
                    // Fail to Post
                    isApiCalled = false

                }
            })
    }

    override fun checkFieldsValidation(): Boolean {
        return true
    }


}
