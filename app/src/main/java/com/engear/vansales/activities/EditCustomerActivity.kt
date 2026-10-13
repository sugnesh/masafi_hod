package com.engear.vansales.activities

import android.os.Bundle
import android.view.LayoutInflater
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivityEditCustomerBinding
import com.engear.vansales.model.CustomerData
import com.engear.vansales.utils.Utils

class EditCustomerActivity : BaseActivity() {
    lateinit var binding: ActivityEditCustomerBinding
    var customerId: String? = null
    var customerData: CustomerData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditCustomerBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@EditCustomerActivity,
            "On Edit customer Screen "
        )
        init()
    }

    private fun init() {
        setToolbarTitle("Edit Customer")

        customerId = intent.getStringExtra("customerId")

        binding.btnSave.setOnClickListener {
            Utils.logData(
                this@EditCustomerActivity,
                "On Save button click "
            )
            /*db.customerDao().updateCustomer(
                CustomerData(
                    customerData!!.id,
                    binding.etCustomerName.text.trim().toString(),
                    binding.etCustomerId.text.trim().toString()
                )
            )
            finish()*/
        }
    }
/*
    private fun setUpData() {
        customerData = db.customerDao().loadCustomerById(customerId!!)

        binding.etCustomerId.setText(customerData!!.customerId)
        binding.etCustomerName.setText(customerData!!.customerName)
    }
*/
}