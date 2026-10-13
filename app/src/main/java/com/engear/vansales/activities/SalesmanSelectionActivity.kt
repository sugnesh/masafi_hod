package com.engear.vansales.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.engear.vansales.R
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivitySalesmanSelectionBinding
import com.engear.vansales.utils.Utils

class SalesmanSelectionActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding: ActivitySalesmanSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySalesmanSelectionBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {
        Utils.logData(
            this@SalesmanSelectionActivity,
            "On salesman selection Screen "
        )
        setToolbarTitle("Salesman Selection")

        binding.btnNext.setOnClickListener(this)
        binding.llPre.setOnClickListener(this)
        binding.llVan.setOnClickListener(this)
        binding.llDelivery.setOnClickListener(this)
        binding.llHybrid.setOnClickListener(this)
        binding.llMer.setOnClickListener(this)
        binding.llFinance.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        Utils.logData(
            this@SalesmanSelectionActivity,
            "On ${v?.id}  click"
        )
        when (v?.id) {
            R.id.btnNext -> {
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            }
            R.id.llPre -> {
                setUpBackground(binding.cdPre)
                mPreferenceManager!!.setFTokenId("PreSeller")
            }
            R.id.llVan -> {
                setUpBackground(binding.cdVan)
                mPreferenceManager!!.setFTokenId("VanSales")
            }
            R.id.llDelivery -> {
                setUpBackground(binding.cdDelivery)
                mPreferenceManager!!.setFTokenId("Delivery")
            }
            R.id.llHybrid -> {
                setUpBackground(binding.cdHybrid)
                mPreferenceManager!!.setFTokenId("Hybrid")
            }
            R.id.llMer -> {
                setUpBackground(binding.cdMer)
                mPreferenceManager!!.setFTokenId("Merchandiser")
            }
            R.id.llFinance -> {
                setUpBackground(binding.cdFinance)
                mPreferenceManager!!.setFTokenId("Finance")
            }
        }
    }
    private fun setUpBackground(view: View?) {
        binding.cdPre.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite))

        binding.cdVan.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite))

        binding.cdDelivery.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite))

        binding.cdHybrid.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite))

        binding.cdMer.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite))

        binding.cdFinance.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite))

        (view as CardView).setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorLightGreen))
    }


}