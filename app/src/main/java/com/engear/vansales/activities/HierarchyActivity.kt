package com.engear.vansales.activities

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.Approvals.HirerachyAdapter
import com.engear.vansales.R
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.model.Hirearchytabledata
import com.engear.vansales.restAPI.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HierarchyActivity : BaseActivity() {

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hierarchy)

        val hirarchyrvitem = findViewById<RecyclerView>(R.id.hirarchyrvitem)
        val ivback = findViewById<ImageView>(R.id.ivback)
        progressBar = findViewById(R.id.progressBar)

        ivback.setOnClickListener(View.OnClickListener { onBackPressed() })
        hirarchyrvitem.layoutManager = LinearLayoutManager(this) // Use this for a vertical list
        val mLayoutManager = GridLayoutManager(this, 1) // Context, number of columns
        hirarchyrvitem.layoutManager = mLayoutManager

        var objectId = intent.extras?.getString("object_id", "").orEmpty()

        // Setting the ItemAnimator
        hirarchyrvitem.itemAnimator = DefaultItemAnimator()
        progressBar.visibility = View.VISIBLE
        ApiClient.getClient(this).getApprovalHistory(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN, objectId
        ).enqueue(object : Callback<Hirearchytabledata> {
            override fun onResponse(
                call: Call<Hirearchytabledata>,
                response: Response<Hirearchytabledata>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    response.body()?.let { apiResponse ->
                        val adapter = HirerachyAdapter(apiResponse.data)
                        hirarchyrvitem.adapter = adapter
                    }
                }
            }

            override fun onFailure(call: Call<Hirearchytabledata>, t: Throwable) {
                // Handle error
                // Toast.makeText(this, "This is a Toast message!", Toast.LENGTH_SHORT).show()
            }
        })
    }


}



