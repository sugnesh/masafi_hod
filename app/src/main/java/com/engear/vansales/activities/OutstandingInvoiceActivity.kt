package com.engear.vansales.activities

import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityOutstandingInvoiceBinding
import com.engear.vansales.databinding.RowOverdueItemBinding
import com.engear.vansales.model.OutstandingData
import com.engear.vansales.model.PromoOfferData
import com.engear.vansales.model.UserData
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference

class OutstandingInvoiceActivity : BaseActivity() {

    lateinit var binding: ActivityOutstandingInvoiceBinding
    var sLobId = ""
    var collectionList: ArrayList<OutstandingData.CollectionData> = ArrayList()
    var mAdapter: CollectionAdapter? = null

    companion object {

        class MyAsyncTask internal constructor(context: OutstandingInvoiceActivity) :
            AsyncTask<Int, String, String?>() {

            private var resp: String? = null
            private val activityReference: WeakReference<OutstandingInvoiceActivity> =
                WeakReference(context)

            override fun onPreExecute() {
                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return
                activity.showOnlyProgressDialog()
            }

            override fun doInBackground(vararg params: Int?): String? {
                val activity = activityReference.get()

                activity!!.collectionList = activity!!.db!!.getAllOverdueCollection()
                Log.e("itemss", Gson().toJson(activity!!.collectionList))

                return resp
            }


            override fun onPostExecute(result: String?) {

                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return

                activity.dismissDialog()
                activity.setUpRecycler()
            }

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOutstandingInvoiceBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@OutstandingInvoiceActivity,
            "On Customer Screen Screen "
        )
        init()
    }

    fun init() {
        setToolbarTitle("Outstanding Invoices")

        userInfo = mPreferenceManager!!.getUserInfo()

        if (userInfo != null) {
            var salesLOB: List<UserData.Data.UserInfo.SalesmanInfo.SalesmanLob>? = null
            if (userInfo!!.data.userInfo.salesmanInfo.lobList != null) {
                salesLOB = userInfo!!.data.userInfo.salesmanInfo.lobList
            }

            if (salesLOB!!.isNotEmpty()) {
                for (mSLob in salesLOB!!) {
                    sLobId = mSLob.lob_id.toString()
                }
            }
        }

        val task = Companion.MyAsyncTask(this@OutstandingInvoiceActivity)
        task.execute(10)

        binding.btnRefresh.setOnClickListener {
            Utils.logData(
                this@OutstandingInvoiceActivity,
                "On refresh button click"
            )

            getCustomerOutstanding()

        }
    }

    private fun setUpRecycler() {
        binding.rvList.layoutManager =
            LinearLayoutManager(
                this@OutstandingInvoiceActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
        mAdapter = CollectionAdapter(
            this@OutstandingInvoiceActivity,
            collectionList!!,
            "1"
        )
        binding.rvList.adapter = mAdapter
    }

    private fun getCustomerOutstanding() {

        showOnlyProgressDialog()

        ApiClient.getClient(this).getRoutePendingInvoice(
            "Bearer ${userInfo!!.data.accessToken}",
            MyApplication.X_DOMAIN,
            userInfo!!.data.userInfo.salesmanInfo.routeId
        ).enqueue(object : Callback<OutstandingData> {
            override fun onResponse(
                call: Call<OutstandingData>,
                response: Response<OutstandingData>
            ) {
                if (response.isSuccessful) {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                    if (response.body()!!.status!!) {
                        db!!.deleteOutstanding()
                        db!!.insertOutStandingInvoice(response.body()!!.data, sLobId)
                    }
                } else {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Blank")
                }
                dismissDialog()
            }

            override fun onFailure(call: Call<OutstandingData>, error: Throwable) {
                //Utils.logData(this@DownloadingDataActivity, "Order Type Failed")
                dismissDialog()
            }
        })

    }

    class CollectionAdapter(
        var context: Context,
        var collectionList: ArrayList<OutstandingData.CollectionData>,
        var paymentPriority: String
    ) : RecyclerView.Adapter<CollectionAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding =
                RowOverdueItemBinding.inflate(LayoutInflater.from(context), parent, false)
            return ViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return collectionList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val mCollectionData = collectionList[holder.adapterPosition]
            if (mCollectionData.itype == "Debit") {
                holder.binding.tvInvNo.text = "Deb. No: " + mCollectionData.invoice_number
                holder.binding.tvInvDate.text = "Deb. Date: " + mCollectionData.invoice_date
                holder.binding.tvDueDate.text = "Days: " + mCollectionData.days
                holder.binding.tvDueDate.setTextColor(Color.parseColor("#4A4A4A"))
                if (mCollectionData.tempAmountDue.toFloat() > 0) {
                    holder.binding.tvAmtDue.text = "O/S Amt: -" + Utils.getCurrencyFormate(
                        context!!,
                        mCollectionData.tempAmountDue.toFloat()
                    )
                } else {
                    holder.binding.tvAmtDue.text = "O/S Amt: " + Utils.getCurrencyFormate(
                        context!!,
                        mCollectionData.tempAmountDue.toFloat()
                    )
                }

            } else if (mCollectionData.itype == "Credit") {
                holder.binding.tvInvNo.text = "Cred. No: " + mCollectionData.invoice_number
                holder.binding.tvInvDate.text = "Ced. Date: " + mCollectionData.invoice_date
                holder.binding.tvDueDate.text = "Days: " + mCollectionData.days
                holder.binding.tvDueDate.setTextColor(Color.parseColor("#4A4A4A"))
                if (mCollectionData.tempAmountDue.toFloat() > 0) {
                    holder.binding.tvAmtDue.text = "O/S Amt: -" + Utils.getCurrencyFormate(
                        context!!,
                        mCollectionData.tempAmountDue.toFloat()
                    )
                } else {
                    holder.binding.tvAmtDue.text = "O/S Amt: " + Utils.getCurrencyFormate(
                        context!!,
                        mCollectionData.tempAmountDue.toFloat()
                    )
                }
            } else {
                holder.binding.tvInvNo.text = "Inv. No: " + mCollectionData.invoice_number
                holder.binding.tvInvDate.text = "Inv. Date: " + mCollectionData.invoice_date
                holder.binding.tvDueDate.text = "Days: " + mCollectionData.days
                if (mCollectionData.isOverDue == "1") {
                    holder.binding.tvDueDate.setTextColor(Color.RED)
                } else {
                    holder.binding.tvDueDate.setTextColor(Color.parseColor("#4A4A4A"))
                }
                holder.binding.tvAmtDue.text = "O/S Amt: " + Utils.getCurrencyFormate(
                    context!!,
                    mCollectionData.tempAmountDue.toFloat()
                )
            }

            //holder.binding.edtColAmt.setText(mCollectionData.amountPay)
            holder.binding.tvCustCode.text = "Cust. Code: " + mCollectionData.customer_code
            holder.binding.executePendingBindings()

        }


        inner class ViewHolder(val binding: RowOverdueItemBinding) :
            RecyclerView.ViewHolder(binding.root) {
        }
    }

}