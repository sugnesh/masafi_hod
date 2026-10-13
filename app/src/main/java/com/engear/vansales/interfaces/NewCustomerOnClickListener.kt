package com.engear.supervisor.view.interfaces

import com.engear.vansales.model.CustomerData
import com.engear.vansales.model.NewCustomer


interface NewCustomerOnClickListener {
    fun onNewCustomerOnClickListener(customer: NewCustomer.Data)
}