package com.engear.vansales.activities

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.R
import com.engear.vansales.adapter.LoadOrderItemAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivityLoadRequestBinding
import com.engear.vansales.databinding.ActivityVanTransferBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.CustomerItemReturnData
import com.engear.vansales.model.Order
import com.engear.vansales.utils.Utils

class VantoVanTransferActivity : BaseActivity() {
    lateinit var binding: ActivityVanTransferBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVanTransferBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@VantoVanTransferActivity,
            "On van transferScreen "
        )
        init()
    }

    private fun init() {
        setToolbarTitle("Van to Van Transfer")
        setNavigationView(mPreferenceManager!!.getFTokenId()!!)

        binding.btnAdd.setOnClickListener {
            Utils.logData(
                this@VantoVanTransferActivity,
                "On Add button click"
            )
            if (Utils.isNetworkAvailable(this@VantoVanTransferActivity)) {
//                startActivity(Intent(this@VantoVanTransferActivity, VanTransferRequestActivity::class.java).apply {
//                    this.putExtra(
//                        "customer_code",
//                        mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.depotId
//                    )
//                    this.putExtra("type", "vantovantransfer")
//                })
            } else {
                Toast.makeText(
                    this@VantoVanTransferActivity,
                    "Network not available!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

    }



}