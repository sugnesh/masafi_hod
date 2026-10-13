package com.engear.vansales.activities

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.OrderRequestItemAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityMasterItemsBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.ItemData
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference

class ItemListActivity : BaseActivity(), OnItemClickListener {


    lateinit var binding: ActivityMasterItemsBinding
    var itemList: ArrayList<ItemData.Data> = ArrayList()

    companion object {

        class MyAsyncTask internal constructor(context: ItemListActivity) :
            AsyncTask<Int, String, String?>() {

            private var resp: String? = null
            private val activityReference: WeakReference<ItemListActivity> =
                WeakReference(context)

            override fun onPreExecute() {
                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return
                activity.showOnlyProgressDialog()
            }

            override fun doInBackground(vararg params: Int?): String? {
                val activity = activityReference.get()

                activity!!.itemList = activity!!.db!!.getAllItem()
                Log.e("itemss", Gson().toJson(activity!!.itemList))

                return resp
            }


            override fun onPostExecute(result: String?) {

                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return

                activity.dismissDialog()
                activity.setAdapter()
            }

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMasterItemsBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@ItemListActivity,
            "On Item List Screen "
        )
        init()
    }


    fun init() {
        setToolbarTitle("Items")

        val task = MyAsyncTask(this)
        task.execute(10)

        binding.btnRefresh.setOnClickListener {
            Utils.logData(
                this@ItemListActivity,
                "On Refresh button click "
            )
            getItem()
        }
    }

    private fun getItem() {

        showOnlyProgressDialog()

        ApiClient.getClient(this)
            .getAllItem("Bearer ${userInfo!!.data.accessToken}", MyApplication.X_DOMAIN)
            .enqueue(object : Callback<ItemData> {
                override fun onResponse(
                    call: Call<ItemData>,
                    response: Response<ItemData>
                ) {
                    dismissDialog()
                    if (response.isSuccessful) {

                        if (response.body()!!.status!!) {
                            db!!.deleteItem()
                            db!!.insertItem(response.body()!!.data)
                            setAdapter()
                        }
                    }

                }

                override fun onFailure(call: Call<ItemData>, error: Throwable) {
                    dismissDialog()
                }
            })
    }

    fun setAdapter() {
        var mAdapter = OrderRequestItemAdapter(this, itemList!!,"")
        val layoutManager = LinearLayoutManager(applicationContext)
        binding.recyclerViewNewCustomer.layoutManager = layoutManager
        binding.recyclerViewNewCustomer.itemAnimator = DefaultItemAnimator()
        binding.recyclerViewNewCustomer.adapter = mAdapter
    }

    override fun onItemsClick(position: Int, tag: String) {
        Log.e("click", tag)
    }

}