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
import com.engear.vansales.databinding.FragmentKPIView2Binding


/**
 * A simple [Fragment] subclass.
 * Use the [KPIView2Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class KPIView2Fragment : Fragment() {

    lateinit var binding: FragmentKPIView2Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_k_p_i_view2, container, false
        )

        if (ViewKPIActivity.mSalesmanRequestData != null) {
            binding.tvLoadedItem.text = ViewKPIActivity.mSalesmanRequestData!!.loadedItem
            if (ViewKPIActivity.mSalesmanRequestData!!.totalTarget != null)
                binding.tvTotalTarget.text = ViewKPIActivity.mSalesmanRequestData!!.totalTarget

        }

        binding.lytSlowMove.setOnClickListener {
            startActivity(
                Intent(activity, ItemViewActivity::class.java)
                    .putExtra("salesmanId", ViewKPIActivity.salesmanId)
                    .putExtra("Type", "slow")
            )
        }

        binding.lytFasttMove.setOnClickListener {
            startActivity(
                Intent(activity, ItemViewActivity::class.java)
                    .putExtra("salesmanId", ViewKPIActivity.salesmanId)
                    .putExtra("Type", "fast")
            )
        }

        binding.lytLoaded.setOnClickListener {
            startActivity(
                Intent(activity, ItemViewActivity::class.java)
                    .putExtra("salesmanId", ViewKPIActivity.salesmanId)
                    .putExtra("Type", "load")
            )
        }

        return binding.root
    }

}