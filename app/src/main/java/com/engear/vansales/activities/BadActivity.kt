package com.engear.vansales.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivityBadBinding
import com.engear.vansales.utils.Utils

class BadActivity : BaseActivity() {

    //Unload type from. like 1:Fresh, 2:bad return, 3: , 4:end inventory, 5: variant, 6 : Load variance, 7: variance
    lateinit var binding: ActivityBadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBadBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@BadActivity,
            "On Bad Screen "
        )

        init()
    }

    private fun init() {

        setToolbarTitle("Bad Return")

        binding.lytDamage.setOnClickListener {
            startActivity(
                Intent(this@BadActivity, EndStockActivity::class.java)
                    .putExtra("Type", "Bad Return")
                    .putExtra("mEType", "Damage")
            )
        }

        binding.lytExpiry.setOnClickListener {
            startActivity(
                Intent(this@BadActivity, EndStockActivity::class.java)
                    .putExtra("Type", "Bad Return")
                    .putExtra("mEType", "Expiry")
            )
        }
    }
}