package com.engear.vansales.activities.supervisor

import android.os.BaseBundle
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.engear.vansales.R
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivityRouteSPDetilBinding
import com.engear.vansales.databinding.ActivitySaleBinding

class RouteSPDetilActivity : BaseActivity() {

    lateinit var binding: ActivityRouteSPDetilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRouteSPDetilBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {
        setToolbarTitle("Sale")
    }
}