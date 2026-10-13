package com.engear.vansales

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitsecure.dunncpplugin.handlers.DunnPay
import com.digitsecure.dunncpplugin.models.DUNNStatusResponse
import com.digitsecure.dunncpplugin.models.DunnStatusRequest
import com.digitsecure.dunncpplugin.models.ErrorResponse
import com.digitsecure.dunncpplugin.result.ITransactionStatusResult
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.DeviceListActivity
import com.engear.vansales.databinding.ActivityCardFailBinding
import com.engear.vansales.databinding.RowCardFailItemBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.CardFail
import com.engear.vansales.model.CustomerData
import com.engear.vansales.utils.Utils


class CardFailActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivityCardFailBinding
    var customerCode: String? = null
    var customerData: CustomerData.DataCustomer? = null
    var cardData: ArrayList<CardFail> = ArrayList()
    var mAdapter: CardFailAdapter? = null
    open var dunnCPayy: DunnPay? = null

    companion object {

        var mActivity: CardFailActivity? = null

        fun callStatusAPI(mData: CardFail, position: Int) {
            mActivity!!.checkStatusAPI(mData.transactionId.toString(), position)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardFailBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@CardFailActivity,
            "On CardFail Screen "
        )
        mActivity = this
        init()
        initDunnPay()

    }

    private fun initDunnPay() {
        dunnCPayy = DunnPay.getInstance()
        dunnCPayy?.setContext(this)
        dunnCPayy?.setDoInitAsync(false)
    }

    private fun init() {
        setToolbarTitle("Card Transactions")

        customerCode = intent.getStringExtra("customer_code")
        customerData = db!!.getCustomerDetail(customerCode!!)

        cardData = db!!.getAllCardTransactionList(customerData!!.user_id.toString())
        setUpRecycler()

    }

    private fun setUpRecycler() {
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = CardFailAdapter(this@CardFailActivity, cardData!!)
        binding.rvList.adapter = mAdapter
    }

    class CardFailAdapter(
        var context: Context,
        var cardData: ArrayList<CardFail>
    ) : RecyclerView.Adapter<CardFailAdapter.ViewHolder>() {

        val onItemClickListener = context as OnItemClickListener

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding =
                RowCardFailItemBinding.inflate(LayoutInflater.from(context), parent, false)
            return ViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return cardData.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val mCollectionData = cardData[holder.adapterPosition]

            holder.binding.tvTransaction.text = "Transaction No: " + mCollectionData.transactionId
            holder.binding.tvAmount.text = "Amount: " + mCollectionData.amount
            holder.binding.tvtStatus.text = "Status: " + mCollectionData.mStatus

            holder.binding.executePendingBindings()

            holder.binding.root.setOnClickListener {
                if (mCollectionData.mTYpe != "Success") {
                    callStatusAPI(cardData[holder.adapterPosition], holder.adapterPosition)
                }
            }
        }


        inner class ViewHolder(val binding: RowCardFailItemBinding) :
            RecyclerView.ViewHolder(binding.root) {
        }
    }

    fun checkStatusAPI(mTransaction: String, position: Int) {

        dunnPay!!.dunnGetStatus(getStatusRequest(mTransaction), object : ITransactionStatusResult {
            override fun onSuccess(payResponse: DUNNStatusResponse) {
                updateStatus(mTransaction, "Success")
                displayAlert(this@CardFailActivity, payResponse.responseMessage, payResponse)
            }

            override fun onError(errorResponse: ErrorResponse) {
                Utils.displayAlert(
                    this@CardFailActivity,
                    mTransaction + errorResponse.responseMessage
                )
                updateStatus(mTransaction, "Fail")

            }
        })

    }

    fun displayAlert(mContext: Context, message: String, payResponse: DUNNStatusResponse) {
        val alertDialogBuilder = AlertDialog.Builder(mContext)
        alertDialogBuilder.setTitle("Alert")
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(
                "Ok"
            ) { dialog, which ->
                dialog.dismiss()

                var cardRefrenceId = payResponse.approvalCode
                var merchantId = payResponse.merchantId
                var transactionid = payResponse.transactionId
                var terminalId = "mashreq POS"
                var lastFour = payResponse.lastFour
                var cardType = payResponse.cardBrand

                val intent = Intent()
                intent.putExtra("cardRefrenceId", cardRefrenceId)
                intent.putExtra("merchantId", merchantId)
                intent.putExtra("transactionid", transactionid)
                intent.putExtra("terminalId", terminalId)
                intent.putExtra("lastFour", lastFour)
                intent.putExtra("cardType", cardType)
                intent.putExtra("mType", "Success")
                // Set result and finish this Activity
                setResult(RESULT_OK, intent)
                finish()
            }
        // create alert dialog
        val alertDialog = alertDialogBuilder.create()
        // show it
        alertDialog.show()
    }

    private fun updateStatus(mTransaction: String, mStatus: String) {
        db!!.updateCardFailTranacPost(mTransaction, mStatus)
    }

    private fun getStatusRequest(mTransaction: String): DunnStatusRequest? {
        val DUNNStatusRequest = DunnStatusRequest()
        DUNNStatusRequest.authId = "6000203"
        DUNNStatusRequest.merchantType = "MID"
        DUNNStatusRequest.fullReceipt = "Y"
        DUNNStatusRequest.flag = "M"
        DUNNStatusRequest.mode = "1"
        DUNNStatusRequest.searchField = mTransaction
        return DUNNStatusRequest
    }

    override fun onItemsClick(position: Int, tag: String) {

    }
}