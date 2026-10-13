package com.engear.vansales.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ApprovalDetailActiivtyBinding

class ApprovalDetailActiivty : BaseActivity() {
    lateinit var binding: ApprovalDetailActiivtyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ApprovalDetailActiivtyBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val type = intent.extras?.getString("type", "").orEmpty()
        binding.txtTitle.setText(type)

        binding.tvOverdueBalance.setTypeface(
            binding.tvOverdueBalance.getTypeface(),
            Typeface.BOLD_ITALIC
        )
        binding.tvRequestedLimit.setTypeface(
            binding.tvRequestedLimit.getTypeface(),
            Typeface.BOLD_ITALIC
        )
        binding.tvRouteCode.setTypeface(binding.tvRouteCode.getTypeface(), Typeface.BOLD_ITALIC)
        binding.tvRouteCode.visibility = View.GONE
        binding.tvSiteCode.setTypeface(binding.tvSiteCode.getTypeface(), Typeface.BOLD_ITALIC)
        binding.tvSiteName.setTypeface(binding.tvSiteName.getTypeface(), Typeface.BOLD_ITALIC)
        binding.tvSalesMan.setTypeface(binding.tvSalesMan.getTypeface(), Typeface.BOLD_ITALIC)

        showLoaderForTwoSeconds()


    }

    private fun showLoaderForTwoSeconds() {
        binding.progressBar.visibility = View.VISIBLE  // Show ProgressBar
        binding.cardmain.visibility = View.INVISIBLE      // CArdview Gone
        binding.statcbutton.visibility = View.INVISIBLE  // StaticButton Gone


        // Use Handler to wait for 2 seconds and then hide the ProgressBar
        Handler(Looper.getMainLooper()).postDelayed({

            val site_code = intent.extras?.getString("site_code", "").orEmpty()
            val site_name = intent.extras?.getString("site_name", "").orEmpty()
            val salesman = intent.extras?.getString("salesman", "").orEmpty()
            val route_code = intent.extras?.getString("route_code", "").orEmpty()
            val request_limit = intent.extras?.getString("request_limit", "").orEmpty()
            val request_day = intent.extras?.getString("request_day", "").orEmpty()
            val overdue_balance = intent.extras?.getString("overdue_balance", "").orEmpty()
            val objectId = intent.extras?.getString("object_id", "").orEmpty()

            val type = intent.extras?.getString("type", "").orEmpty()

            binding.progressBar.visibility = View.GONE
            binding.cardmain.visibility = View.VISIBLE
            binding.statcbutton.visibility = View.VISIBLE

            binding.tvOverdueBalance.text = "Overdue Balance: " + overdue_balance
            if (type == "Credit Limit") {
                binding.tvRequestedLimit.text = "Requested Limit: " + request_limit
            } else {
                binding.tvRequestedLimit.text = "Requested Days: " + request_day
            }

            binding.tvRouteCode.text = "Route Code: " + route_code
            binding.tvSiteCode.text = "Site Code: " + site_code
            binding.tvSiteName.text = "Site Name: " + site_name
            binding.tvSalesMan.text = "SalesMan: " + salesman

            binding.ivback.setOnClickListener(View.OnClickListener { onBackPressed() })
            binding.hirerarchy.setOnClickListener {
                val aboutIntent = Intent(this@ApprovalDetailActiivty, HierarchyActivity::class.java)
                aboutIntent.putExtra("object_id", objectId)
                startActivity(aboutIntent)
            }
        }, 2000)  // 2000 milliseconds = 2 seconds
    }


}