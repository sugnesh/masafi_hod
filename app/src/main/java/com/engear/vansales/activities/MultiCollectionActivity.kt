package com.engear.vansales.activities

import android.os.Bundle
import android.view.LayoutInflater
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivityMultiCollectionBinding
import com.engear.vansales.utils.Utils

class MultiCollectionActivity : BaseActivity() {
    lateinit var binding: ActivityMultiCollectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMultiCollectionBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@MultiCollectionActivity,
            "On Multicollection Screen "
        )
        init()
    }

    private fun init() {
        setToolbarTitle("Multi Collection")
    }
}