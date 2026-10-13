package com.engear.vansales.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.digitsecure.dunncpplugin.handlers.DunnPay
import com.digitsecure.dunncpplugin.models.DunnBilling
import com.digitsecure.dunncpplugin.models.DunnPayRequest
import com.digitsecure.dunncpplugin.models.DunnPayResponse
import com.digitsecure.dunncpplugin.models.ErrorResponse
import com.digitsecure.dunncpplugin.result.IPayResult
import com.engear.vansales.R
import com.engear.vansales.activities.PaymentDetailActivity.Companion.isOpen
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityPartialPaymentBinding
import com.engear.vansales.databinding.ActivityPaymentDetailBinding
import com.engear.vansales.model.CardFail
import com.engear.vansales.model.CustomerData
import com.engear.vansales.model.IcashDetail
import com.engear.vansales.model.Transaction
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.JsonObject
import org.opencv.android.OpenCVLoader
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.SecureRandom
import java.util.Random

class ParitalPaymentActivity : BaseActivity() {

    lateinit var binding: ActivityPartialPaymentBinding

    var mRequiredAmt: String = "0.0"
    var mPaidAmt: String = "0"
    var collectionType = "3"
    private var isRecursive = false
    private var cardRefrenceId = ""
    private var merchantId = ""
    private var transactionid = ""
    private var terminalId = ""
    private var lastFour = ""
    private var cardType = ""
    private var mBankCharge = ""
    private var mBankTaxAmt = ""
    private val random: Random = SecureRandom()
    private var prevRefId = ""
    private var mType: String = ""
    var customerId: String = ""
    var customerData: CustomerData.DataCustomer? = null
    open var dunnPayy: DunnPay? = null
    private var iCashAmount = 0f
    private var onAccountAmount = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPartialPaymentBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )

        Utils.logData(
            this@ParitalPaymentActivity,
            "On payment details Screen "
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
        val myApplication = application as MyApplication
        dunnPayy = DunnPay.getInstance()
        dunnPayy?.setContext(this)
        dunnPayy?.setDoInitAsync(false)
    }

    private fun init() {
        setToolbarTitle("Payment Detail")

        mRequiredAmt = intent.getStringExtra("mRequiredAmt")!!
        mType = intent.getStringExtra("type")!!
        customerId = intent.getStringExtra("customer")!!
        customerData = db!!.getCustomerDetailFromCustId(customerId!!)
        binding.edtRDAmt.setText(mRequiredAmt)

        if (mType == "topup") {
            binding.rbICash.visibility = View.GONE

            if (customerData!!.customer_category_id == "87") {
                binding.rbCash.visibility = View.VISIBLE
                binding.rbCash.isChecked = true
                collectionType = "1"
            }else{
                binding.lytAuthCode!!.visibility = View.VISIBLE
            }
        }else{
            if (customerData!!.customer_category_id == "87") {
                binding.rbCash!!.visibility = View.VISIBLE
                binding.rbCash!!.isChecked = true
                collectionType = "1"
            }else{
                binding.lytAuthCode!!.visibility = View.GONE
            }
        }

        binding.rgPaymentMode!!.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbCash -> {
                    collectionType = "1"
                    binding.lytAuthCode!!.visibility = View.GONE
                    binding.lytICash!!.visibility = View.GONE
                }

                R.id.rbCard -> {
                    collectionType = "3"
                    binding.lytAuthCode!!.visibility = View.GONE
                    binding.lytICash!!.visibility = View.GONE
                }

                R.id.rbICash -> {
                    collectionType = "5"
                    binding.lytAuthCode!!.visibility = View.GONE
                    binding.lytICash!!.visibility = View.VISIBLE
                    getCurrentBalance()
                }

                R.id.rbOnBalance -> {
                    collectionType = "7"
                    binding.lytAuthCode!!.visibility = View.GONE
                    binding.lytICash!!.visibility = View.VISIBLE
                    getCurrentOnAccountBalance()
                }

            }
        }

        binding.btnPayment.setOnClickListener {

            if (binding.edtPaidAmt.text.toString().isEmpty()) {
                Toast.makeText(this, "Please enter paid amount!", Toast.LENGTH_SHORT).show()
            } else {
                mPaidAmt = binding.edtPaidAmt.text.toString()

                if (mPaidAmt.toDouble() > mRequiredAmt.toDouble()) {
                    Toast.makeText(
                        this,
                        "You can not enter amount more than Required Amount!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {

                    if (collectionType == "1") {
                        var mDetailCollection = HashMap<String, String>()
                        mDetailCollection.put("PaymentType", "Cash")
                        mDetailCollection.put("Amount", mPaidAmt)
                        mDetailCollection.put("BankCharge", "")
                        mDetailCollection.put("TaxAmount", "")
                        mDetailCollection.put("AuthCode", cardRefrenceId)
                        PaymentDetailActivity.mPartialArray.add(mDetailCollection)
                        val resultIntent = Intent()
                        resultIntent.putExtra("scanid", mDetailCollection)
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    } else if (collectionType == "5") {

                        if (Utils.twoDigit(iCashAmount.toFloat()) >= Utils.twoDigit(mPaidAmt.toFloat())) {
                            var mDetailCollection = HashMap<String, String>()
                            mDetailCollection.put("PaymentType", "ICash")
                            mDetailCollection.put("Amount", mPaidAmt)
                            mDetailCollection.put("BankCharge", "")
                            mDetailCollection.put("TaxAmount", "")
                            mDetailCollection.put("AuthCode", cardRefrenceId)
                            PaymentDetailActivity.mPartialArray.add(mDetailCollection)
                            val resultIntent = Intent()
                            resultIntent.putExtra("scanid", mDetailCollection)
                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()
                        } else {
                            val builder = MaterialAlertDialogBuilder(this)
                            builder.setTitle("Alert")
                            builder.setMessage("iCash balance is low. Kindly topup balance and redeam?")
                            builder.setCancelable(false)
                            builder.setPositiveButton(
                                "OK"
                            ) { dialog, _ ->
                                dialog.cancel()
                            }
                            builder.show()
                        }

                    } else if (collectionType == "7") {
                        if (Utils.twoDigit(onAccountAmount.toFloat()) >= Utils.twoDigit(mPaidAmt.toFloat())) {
                            var mDetailCollection = HashMap<String, String>()
                            mDetailCollection.put("PaymentType", "onBalance")
                            mDetailCollection.put("Amount", mPaidAmt)
                            mDetailCollection.put("BankCharge", "")
                            mDetailCollection.put("TaxAmount", "")
                            mDetailCollection.put("AuthCode", cardRefrenceId)
                            PaymentDetailActivity.mPartialArray.add(mDetailCollection)
                            val resultIntent = Intent()
                            resultIntent.putExtra("scanid", mDetailCollection)
                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()
                        } else {
                            val builder = MaterialAlertDialogBuilder(this)
                            builder.setTitle("Alert")
                            builder.setMessage("Account balance is low?")
                            builder.setCancelable(false)
                            builder.setPositiveButton(
                                "OK"
                            ) { dialog, _ ->
                                dialog.cancel()
                            }
                            builder.show()
                        }

                    } else {

//                        if (binding.edtAuthCode!!.text.toString().isEmpty()) {
//                            Toast.makeText(
//                                this@ParitalPaymentActivity,
//                                "Please enter authorization code for MPOS",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        } else {
//                            cardRefrenceId = binding.edtAuthCode.text.toString()
//                            var mDetailCollection = HashMap<String, String>()
//                            mDetailCollection.put("PaymentType", "Card")
//                            mDetailCollection.put("Amount", mPaidAmt)
//                            mDetailCollection.put("BankCharge", mBankCharge)
//                            mDetailCollection.put("TaxAmount", mBankTaxAmt)
//                            mDetailCollection.put("AuthCode", cardRefrenceId)
//                            mDetailCollection.put("merchantId", merchantId)
//                            mDetailCollection.put("transactionId", transactionid)
//                            mDetailCollection.put("terminalId", terminalId)
//                            mDetailCollection.put("lastFour", lastFour)
//                            mDetailCollection.put("cardType", cardType)
//                            PaymentDetailActivity.mPartialArray.add(mDetailCollection)
//                            val resultIntent = Intent()
//                            resultIntent.putExtra("scanid", mDetailCollection)
//                            setResult(Activity.RESULT_OK, resultIntent)
//                            finish()
//                        }

                          callCardPayment()
//                        var mDetailCollection = HashMap<String, String>()
//                        mDetailCollection.put("PaymentType", "Card")
//                        mDetailCollection.put("Amount", mPaidAmt)
//                        mDetailCollection.put("BankCharge", "0")
//                        mDetailCollection.put("TaxAmount", "0")
//                        mDetailCollection.put("AuthCode", "32432")
//                        PaymentDetailActivity.mPartialArray.add(mDetailCollection)
//                        val resultIntent = Intent()
//                        resultIntent.putExtra("scanid", mDetailCollection)
//                        setResult(Activity.RESULT_OK, resultIntent)
//                        finish()
                    }
                }
            }
        }
    }

    private fun callCardPayment() {
        isRecursive = false

        dunnPayy?.dunnStartPayment(
            getPaymentRequest(),

            object : IPayResult {
                override fun onSuccess(payResponse: DunnPayResponse) {
                    getTRansactionSuceessCallBack(payResponse)
                }

                override fun onError(errorResponse: ErrorResponse) {
                    cardFailPayment(errorResponse)
                    //insertCardRecord("Fail")
                    //Log.e("Card Pay", errorResponse.responseMessage)
                }
            })

    }

    private fun insertCardRecord(mType: String) {
        var mCardData = CardFail()
        mCardData.transactionId = prevRefId
        mCardData.customer_id = customerData!!.user_id.toString()
        mCardData.amount = mPaidAmt.toString()
        mCardData.mTYpe = mType
        mCardData.mStatus = mType

        db!!.insertCardTransaction(mCardData)


       /* val transaction = Transaction()
        transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_CARD_FAIL_COLLECTION
        transaction.tr_date_time = Utils.getCurrentDate() + " " + Utils.getCurrentTime()
        transaction.tr_customer_num = customerData!!.customer_code
        transaction.tr_customer_name =
            customerData!!.userDetail!!.firstname + " " + customerData!!.userDetail!!.lastname
        transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
        transaction.tr_invoice_id = ""
        transaction.tr_order_id = prevRefId
        transaction.tr_collection_id = ""
        transaction.tr_pyament_id = ""
        transaction.tr_is_posted = "No"
        transaction.tr_printData = ""
        db!!.insertTransaction(transaction)*/
    }

    private fun cardFailPayment(errorResponse: ErrorResponse) {
        var message = ""

        if (errorResponse.responseMessage != null) {
            message = errorResponse.responseMessage.toString()
        }
        isOpen = false
        Toast.makeText(this, "Card Payment Fail : $message", Toast.LENGTH_SHORT).show()
    }

    private fun getTRansactionSuceessCallBack(payResponse: DunnPayResponse) {
        // cardRefrenceId = payResponse.dunnPaymentReceipt.rrn
        cardRefrenceId = payResponse.approvalCode
        merchantId = payResponse.merchantId
        transactionid = payResponse.transactionId
        terminalId = "mashreq POS"
        lastFour = payResponse.lastFour
        cardType = payResponse.cardBrand

        //insertCardRecord("Success")

        //update status
        db!!.updateCardFailTranacPost(prevRefId, "Success")

        var mDetailCollection = HashMap<String, String>()
        mDetailCollection.put("PaymentType", "Card")
        mDetailCollection.put("Amount", mPaidAmt)
        mDetailCollection.put("BankCharge", mBankCharge)
        mDetailCollection.put("TaxAmount", mBankTaxAmt)
        mDetailCollection.put("AuthCode", cardRefrenceId)
        mDetailCollection.put("merchantId", merchantId)
        mDetailCollection.put("transactionId", transactionid)
        mDetailCollection.put("terminalId", terminalId)
        mDetailCollection.put("lastFour", lastFour)
        mDetailCollection.put("cardType", cardType)
        PaymentDetailActivity.mPartialArray.add(mDetailCollection)
        val resultIntent = Intent()
        resultIntent.putExtra("scanid", mDetailCollection)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()

//        if (type == "topup") {
//            postTopupInvoice()
//        }else{
//            addCollection()
//        }

    }

    @SuppressLint("SetTextI18n")
    private fun getPaymentRequest(): DunnPayRequest? {
        val DUNNPayRequest = DunnPayRequest()
        DUNNPayRequest.authId = "6000203"
        DUNNPayRequest.authKey = "G4NOQ814JCH7TV1ZU13PZP2K"
        DUNNPayRequest.phoneNo = "0000000000"
        DUNNPayRequest.emailId = "test@gmail.com"
        DUNNPayRequest.merchantType = "MID"
        DUNNPayRequest.displayDetails = "N"
        DUNNPayRequest.fullReceipt = "Y"

        val dunnBillingList: MutableList<DunnBilling> = java.util.ArrayList()
        val dUNNBilling = DunnBilling()

        var bankcharge = 0.0
//        var mTaxAmount = Utils.getVatAmt(bankcharge.toDouble(), 5.0)
//        var mOtherAmount = Utils.twoDigit((bankcharge + mTaxAmount).toFloat())
        var mFinalAmount = mPaidAmt

        mBankCharge = "" + bankcharge
        mBankTaxAmt = "0.0"

        dUNNBilling.amount = "" + mPaidAmt
        dUNNBilling.otherAmount = "0"
        prevRefId = getRandomNum(15)!!
        dUNNBilling.refId = prevRefId
        dUNNBilling.keyField1 = "SAMPLE"
        dunnBillingList.add(dUNNBilling)
        DUNNPayRequest.totalBillingAmount = "" + mFinalAmount
        DUNNPayRequest.dunnBillings = dunnBillingList

        insertCardRecord("Request")

        return DUNNPayRequest
    }

    fun getRandomNum(length: Int): String? {
        val letters = "1234567890"
        val pw = StringBuilder()
        for (i in 0 until length) {
            val index = (random.nextDouble() * letters.length).toInt()
            pw.append(letters[index])
        }
        if (pw.toString().startsWith("0")) {
            pw.setLength(0)
            pw.append(getRandomNum(length))
        }
        prevRefId = pw.toString()
        return pw.toString()
    }

    public fun getCurrentBalance() {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty(
            "customer_id",
            customerData!!.userDetail.id.toString()
        )

        ApiClient.getClient(this).getICashDetail(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            jObj,
        )

            .enqueue(object : Callback<IcashDetail> {
                override fun onFailure(call: Call<IcashDetail>, t: Throwable) {
                    dismissDialog()

                }

                override fun onResponse(
                    p0: Call<IcashDetail>,
                    response: Response<IcashDetail>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { apiResponse ->
                            dismissDialog()

                            if (apiResponse.data.customer_data.balance != null) {
                                val originalString =
                                    (apiResponse.data.customer_data.balance.toDouble() + apiResponse.data.customer_data.bonus.toDouble())
                                iCashAmount = originalString.toFloat()
                            } else {
                                iCashAmount = 0F
                            }
                        }
                    } else {
                        dismissDialog()
                    }

                    binding.txtICashBalance!!.setText("ICash Balance: $iCashAmount")

                }

            })
    }

    public fun getCurrentOnAccountBalance() {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty(
            "customer_id",
            customerData!!.userDetail.id.toString()
        )

        ApiClient.getClient(this).getOnAccountBalanceDetail(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            jObj,
        )

            .enqueue(object : Callback<IcashDetail> {
                override fun onFailure(call: Call<IcashDetail>, t: Throwable) {
                    dismissDialog()

                }

                override fun onResponse(
                    p0: Call<IcashDetail>,
                    response: Response<IcashDetail>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { apiResponse ->
                            dismissDialog()

                            if (apiResponse.data.customer_data.balance != null) {
                                val originalString =
                                    (apiResponse.data.customer_data.balance.toDouble())
                                onAccountAmount = originalString.toFloat()
                            } else {
                                onAccountAmount = 0F
                            }
                        }
                    } else {
                        dismissDialog()
                    }

                    binding.txtICashBalance!!.setText("Account Balance: $onAccountAmount")

                }

            })
    }

}