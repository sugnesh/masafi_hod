package com.engear.vansales.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.engear.vansales.R
import com.engear.vansales.activities.supervisor.*
import com.engear.vansales.databinding.FragmentKPIView1Binding

/**
 * A simple [Fragment] subclass.
 * Use the [KPIView1Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class KPIView1Fragment : Fragment() {

    lateinit var binding: FragmentKPIView1Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_k_p_i_view1, container, false
        )

        if (ViewKPIActivity.mSalesmanRequestData != null) {

            binding.tvSalesAmt.text = ViewKPIActivity.mSalesmanRequestData!!.totalSales + " AED"
            binding.tvReturnAmt.text = ViewKPIActivity.mSalesmanRequestData!!.totalReturn + " AED"
            binding.tvCategorySale.text =
                ViewKPIActivity.mSalesmanRequestData!!.categoryWiseSale + " AED"
            binding.tvBrandSale.text = ViewKPIActivity.mSalesmanRequestData!!.brandSale + " AED"
            binding.tvItemSale.text = ViewKPIActivity.mSalesmanRequestData!!.itemWiseSales + " AED"

        }

        binding.totalSale.setOnClickListener {
            startActivity(
                Intent(activity, SaleActivity::class.java)
                    .putExtra("salesmanId", ViewKPIActivity.salesmanId)
            )
        }

        binding.totalReturn.setOnClickListener {
            startActivity(
                Intent(activity, CreditActivity::class.java)
                    .putExtra("salesmanId", ViewKPIActivity.salesmanId)
            )
        }

        binding.lytItemSales.setOnClickListener {
            startActivity(
                Intent(activity, ItemViewActivity::class.java)
                    .putExtra("salesmanId", ViewKPIActivity.salesmanId)
                    .putExtra("Type", "itemwise")
            )
        }

        binding.lytCategory.setOnClickListener {
            startActivity(
                Intent(activity, ItemViewActivity::class.java)
                    .putExtra("salesmanId", ViewKPIActivity.salesmanId)
                    .putExtra("Type", "category")
            )
        }

        binding.lytBrand.setOnClickListener {
            startActivity(
                Intent(activity, ItemViewActivity::class.java)
                    .putExtra("salesmanId", ViewKPIActivity.salesmanId)
                    .putExtra("Type", "brand")
            )
        }

        return binding.root
    }

}