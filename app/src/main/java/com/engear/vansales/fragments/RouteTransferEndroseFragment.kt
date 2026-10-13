package com.learnina.sample.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.engear.vansales.R
import com.engear.vansales.activities.supervisor.SessionEndrosementActivity
import com.engear.vansales.common.MyApplication
import com.engear.vansales.common.SharedPreferenceManager
import com.engear.vansales.databinding.InvoiceEndroseFragmentBinding
import com.engear.vansales.restAPI.ApiClient
import com.learnina.materialverticalstepper.verticalstepper.StepperFragment
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RouteTransferEndroseFragment : StepperFragment() {

  lateinit  var binding : InvoiceEndroseFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.invoice_endrose_fragment, container, false
        )
        setText()
//        getData()
        return binding.root
    }

    private fun setText(){
        binding.tvcutomercode.text = "Item Code"
        binding.tvCustomerName.text = "Item Name"
        binding.tvInvoceNo.text = "UOM"
        binding.tvNetSale.text = "QTY"
        binding.tvTotalsale.text = "Transfer Route"
    }

//    private fun getData() {
//        ApiClient.getClient(activity!!)
//            .getInvoiceVisitItemDetails(
//                "Bearer ${SharedPreferenceManager(activity!!)!!.getSupervisorUserInfo()!!.data.accessToken}",
//                MyApplication.X_DOMAIN,
//                SessionEndrosementActivity.routeId,"foc"
//            )
//            .enqueue(object :
//                Callback<ResponseBody> {
//                override fun onResponse(
//                    call: Call<ResponseBody>,
//                    response: Response<ResponseBody>
//                ) {
//                    Log.e("foc", response.toString())
//                    Log.e("foc", response.body()!!.string())
//                    if (response.isSuccessful) {
//                        if (response.body() != null) {
//
//                        }
//                    } else {
//
//                    }
//
//                }
//
//                override fun onFailure(call: Call<ResponseBody>, error: Throwable) {
//                    // Fail to Post
//
//                }
//            })
//    }

    override fun checkFieldsValidation(): Boolean {
        return true
    }


}
