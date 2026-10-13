package com.engear.vansales.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.engear.vansales.R
import com.engear.vansales.activities.supervisor.ItemViewActivity
import com.engear.vansales.activities.supervisor.ViewKPIActivity
import com.engear.vansales.databinding.FragmentKPIView3Binding


/**
 * A simple [Fragment] subclass.
 * Use the [KPIView3Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class KPIView3Fragment : Fragment() {

    lateinit var binding: FragmentKPIView3Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_k_p_i_view3, container, false
        )

        if (ViewKPIActivity.isEndroseReq) {
            binding.btnEndrose.isClickable = true
            binding.btnEndrose.alpha = 1.0f
            binding.btnEndrose.isEnabled = true
        } else {
            binding.btnEndrose.isClickable = false
            binding.btnEndrose.alpha = 0.5f
            binding.btnEndrose.isEnabled = false
        }

        if (ViewKPIActivity.mSalesmanRequestData != null) {
            binding.tvUnloadItem.text = ViewKPIActivity.mSalesmanRequestData!!.unloadedItem
            binding.tvPayment.text = ViewKPIActivity.mSalesmanRequestData!!.totalCollection + " AED"
        }

        binding.lytUnload.setOnClickListener {
            startActivity(
                Intent(activity, ItemViewActivity::class.java)
                    .putExtra("salesmanId", ViewKPIActivity.salesmanId)
                    .putExtra("Type", "unload")
            )
        }

        binding.btnEndrose.setOnClickListener {
            ViewKPIActivity.callEndrosment()
        }

        return binding.root
    }


}