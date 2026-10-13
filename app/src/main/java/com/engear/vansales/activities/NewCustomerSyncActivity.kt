package com.engear.vansales.activities

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.engear.vansales.adapter.CustomerSyncAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityNewCustomerSyncBinding
import com.engear.vansales.localDB.CustomerSyncEntity
import com.engear.vansales.restAPI.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.gson.JsonParser
import okhttp3.MediaType.Companion.toMediaTypeOrNull

class NewCustomerSyncActivity : BaseActivity() {
    private lateinit var binding: ActivityNewCustomerSyncBinding
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewCustomerSyncBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }


        uiScope.launch {
            val customers = withContext(Dispatchers.IO) {
                //dbnc.newCustomerSyncDao().getAll()
            }
        }
    }

    private fun setupRecyclerView(customers: List<CustomerSyncEntity>) {
        val adapter = CustomerSyncAdapter(customers) { customer ->
            if (!customer.isSynced) {
                showSyncDialog(customer)
            }
        }
        binding.customerRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.customerRecyclerView.adapter = adapter
    }

    private fun showSyncDialog(customer: CustomerSyncEntity) {
        AlertDialog.Builder(this)
            .setMessage("Do you want to sync this customer?")
            .setPositiveButton("Yes") { dialog, id ->
                syncCustomer(customer)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun syncCustomer(customer: CustomerSyncEntity) {
     // deleteCustomerById(1)
        val payload = customer.payload
            // sendCustomerData(payload, customer)
    }
//    private fun sendCustomerData(payload: String , customer: CustomerSyncEntity) {
//        val progressDialog = ProgressDialog(this)
//        progressDialog.setMessage("Syncing customer...")
//        progressDialog.setCancelable(false)
//        progressDialog.show()
//
//        val requestBody =
//            RequestBody.create("application/json".toMediaTypeOrNull(), payload.toString())
//
//        val call = ApiClient.getClient(this).saveNewCustomerFormData(
//            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
//            MyApplication.X_DOMAIN,
//            requestBody
//        )
//
//        call.enqueue(object : Callback<ResponseBody> {
//            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                progressDialog.dismiss()
//                if (response.isSuccessful) {
//                    Toast.makeText(this@NewCustomerSyncActivity, "Customer synced successfully", Toast.LENGTH_LONG).show()
//                    uiScope.launch {
//                        withContext(Dispatchers.IO) {
//                            customer.isSynced = true
//                            dbnc.newCustomerSyncDao().update(customer)
//                        }
//                        val updatedCustomers = withContext(Dispatchers.IO) {
//                            dbnc.newCustomerSyncDao().getAll()
//                        }
//                        setupRecyclerView(updatedCustomers)
//                    }
//                } else {
//                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
//                    Log.e("API Error", "API call failed with message: $errorMessage")
//                    Toast.makeText(this@NewCustomerSyncActivity, "Failed to sync customer: $errorMessage", Toast.LENGTH_LONG).show()
//                }
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, error: Throwable) {
//                progressDialog.dismiss()
//                Log.e("Network Error", "Failed to make API call: ${error.message}")
//                Toast.makeText(this@NewCustomerSyncActivity, "Network error:Customer not Synced ${error.message}", Toast.LENGTH_LONG).show()
//            }
//        })
//    }
    private fun deleteCustomerById(customerId: Int) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                //dbnc.newCustomerSyncDao().deleteById(customerId)
            }
            val updatedCustomers = withContext(Dispatchers.IO) {
                //dbnc.newCustomerSyncDao().getAll()
            }
            //setupRecyclerView(updatedCustomers)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
