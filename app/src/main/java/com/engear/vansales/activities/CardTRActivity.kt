package com.engear.vansales.activities

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import com.digitsecure.dunncpplugin.handlers.DunnPay
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivityCardTractivityBinding
import org.opencv.android.OpenCVLoader

class CardTRActivity : BaseActivity() {

    lateinit var binding: ActivityCardTractivityBinding
    open var dunnPayy: DunnPay? = null
    var customerId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardTractivityBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )

        if (!OpenCVLoader.initDebug()) {
            Log.e(ContentValues.TAG, "OpenCV initialization failed.")
        } else {
            Log.d(ContentValues.TAG, "OpenCV initialization succeeded.")
        }

        init()
        initDunnPay()

    }

    private fun initDunnPay() {
        //val myApplication = application as MyApplication
        dunnPayy = DunnPay.getInstance()
        dunnPayy?.setContext(this)
        dunnPayy?.setDoInitAsync(false)
    }

    private fun init() {
        setToolbarTitle("Card Transaction")


        customerId = intent.getStringExtra("customer")!!
    }
}