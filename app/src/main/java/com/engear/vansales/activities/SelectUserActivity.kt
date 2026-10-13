package com.engear.vansales.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.engear.vansales.activities.supervisor.SupervisorLoginActivity
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivitySelectUserBinding
import com.engear.vansales.utils.Utils

class SelectUserActivity : BaseActivity() {

    lateinit var binding: ActivitySelectUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectUserBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {
        Utils.logData(
            this@SelectUserActivity,
            "On select user Screen "
        )
        setFullScreen()

        binding.llSalesman.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.llSupervisor.setOnClickListener {
            startActivity(Intent(this, SupervisorLoginActivity::class.java))
            finish()
        }

    }
}