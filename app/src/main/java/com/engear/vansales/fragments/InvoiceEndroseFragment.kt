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
import com.engear.vansales.adapter.InvoiceEndroseAdapter
import com.engear.vansales.common.MyApplication
import com.engear.vansales.common.SharedPreferenceManager
import com.engear.vansales.databinding.InvoiceEndroseFragmentBinding
import com.engear.vansales.model.InvoiceEndrose
import com.engear.vansales.restAPI.ApiClient
import com.learnina.materialverticalstepper.verticalstepper.StepperFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InvoiceEndroseFragment : StepperFragment() {

  lateinit  var binding : InvoiceEndroseFragmentBinding
    private var invoicelist = ArrayList<InvoiceEndrose.Data>()
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

    private fun setText(){
        binding.tvcutomercode.text = "Customer Code"
        binding.tvCustomerName.text = "Customer Name"
        binding.tvInvoceNo.text = "Invoice No"
        binding.tvNetSale.text = "Net Sale"
        binding.tvTotalsale.text = "Total Sale"
    }
    fun setRouteAdapter() {
        var adapterRoute =
            InvoiceEndroseAdapter(invoicelist)
        val layoutManager = LinearLayoutManager(activity)
        binding.recyclerViewEndrose.layoutManager = layoutManager
        binding.recyclerViewEndrose.itemAnimator = DefaultItemAnimator()
        binding.recyclerViewEndrose.adapter = adapterRoute

    }

    private fun getData() {
        ApiClient.getClient(activity!!)
            .getInvoiceVisitItemEndrose(
                "Bearer ${SharedPreferenceManager(activity!!)!!.getSupervisorUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN,
                SessionEndrosementActivity.routeId,"invoice"
            )
            .enqueue(object : Callback<InvoiceEndrose> {
                override fun onResponse(
                    call: Call<InvoiceEndrose>,
                    response: Response<InvoiceEndrose>
                ) {
                    Log.e("foc", response.toString())
//                    Log.e("foc", response.body()!!.string())
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            isApiCalled = true
                            invoicelist  = response.body()!!.data as ArrayList<InvoiceEndrose.Data>
                            setRouteAdapter()
                        }
                    } else {

                    }

                }

                override fun onFailure(call: Call<InvoiceEndrose>, error: Throwable) {
                    // Fail to Post
                    isApiCalled = false

                }
            })
    }

    override fun checkFieldsValidation(): Boolean {
        return true
    }


}
