package com.engear.vansales.activities

import android.app.ActionBar
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.Approvals.ApprovalAdapter
import com.engear.vansales.R
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.databinding.ActivityApprovalsBinding
import com.engear.vansales.databinding.ActivityManageInventoryBinding
import com.engear.vansales.model.ApprovalRes
import com.engear.vansales.restAPI.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApprovalsActivity : BaseActivity() {

    private lateinit var progressBar: ProgressBar
    lateinit var binding: ActivityApprovalsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApprovalsBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )

        setToolbarTitle("Approvals")
        setNavigationView(mPreferenceManager!!.getFTokenId()!!)

        progressBar = findViewById(R.id.progressBar)

        binding.hirarchyrvitem.layoutManager = LinearLayoutManager(this) // Use this for a vertical list
        val mLayoutManager = GridLayoutManager(this, 1) // Context, number of columns
        binding.hirarchyrvitem.layoutManager = mLayoutManager

        // Setting the ItemAnimator
        binding.hirarchyrvitem.itemAnimator = DefaultItemAnimator()
        Log.d("USSSSSSS", "${mPreferenceManager?.getUserInfo()}")
        progressBar.visibility = View.VISIBLE

        ApiClient.getClient(this).getUsers(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            mPreferenceManager!!.getUserId()!!,/*mPreferenceManager?.getUserInfo()?.data?.userInfo?.id.toString()*/
        )
            .enqueue(object : Callback<ApprovalRes> {
                override fun onFailure(call: Call<ApprovalRes>, t: Throwable) {
                    // Handle error
                    //Toast.makeText(this, "This is a Toast message!", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    p0: Call<ApprovalRes>,
                    response: Response<ApprovalRes>
                ) {
                    progressBar.visibility = View.GONE

                    if (response.isSuccessful) {
                        response.body()?.let { apiResponse ->
                            val adapter = ApprovalAdapter(apiResponse.data)
                            binding.hirarchyrvitem.adapter = adapter
                        }
                    }
                }
            })

    }

    private fun showLoaderForTwoSeconds() {
        progressBar.visibility = View.VISIBLE  // Show ProgressBar

        // Use Handler to wait for 2 seconds and then hide the ProgressBar
        Handler(Looper.getMainLooper()).postDelayed({
            progressBar.visibility = View.GONE
        }, 2000)  // 2000 milliseconds = 2 seconds
    }
}