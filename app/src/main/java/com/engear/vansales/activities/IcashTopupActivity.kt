package com.engear.vansales.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.databinding.ActivityIcashTopupBinding
import com.engear.vansales.merchandiser.StoreCheckActivity
import com.engear.vansales.model.CustomerData
import com.engear.vansales.model.IcashBounsl
import com.engear.vansales.model.IcashDetail
import com.engear.vansales.model.OutstandingData
import com.engear.vansales.restAPI.ApiClient
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Comparator
import java.util.Date


class IcashTopupActivity : BaseActivity() {

    var grandtotal: String = ""

    var customerObj: CustomerData.DataCustomer? = null
    var customerlob: CustomerData.DataCustomer.Customerlob? = null
    var customerCode: String? = null
    lateinit var binding: ActivityIcashTopupBinding
    var arrTopupValue: ArrayList<IcashBounsl.BonusData> = ArrayList()
    var mTopupValue = 0.00
    var mBonus = 0.00
    var mBalance = 0.00
    var mICashAccount = ""
    var mMobile = ""
    var mEmail = ""
    var mCurrentBalance = "0.0"
    var isClick = false

    val items = listOf("Select Amount", "200", "400", "600","800","1000","1200")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIcashTopupBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )

        setToolbarTitle("iCash Top Up")
        customerCode = intent.getStringExtra("customer_code")
        mMobile = intent.getStringExtra("mobile")!!
        mEmail = intent.getStringExtra("email")!!
        mICashAccount= intent.getStringExtra("icashAccount")!!
        customerObj = db!!.getCustomerDetail(customerCode!!)

        var mTotpUp = IcashBounsl.BonusData("600", "75")
        arrTopupValue!!.add(mTotpUp)
        mTotpUp = IcashBounsl.BonusData("400", "40")
        arrTopupValue!!.add(mTotpUp)
        mTotpUp = IcashBounsl.BonusData("200", "10")
        arrTopupValue!!.add(mTotpUp)

        Collections.sort(arrTopupValue, object :
            Comparator<IcashBounsl.BonusData> {
            override fun compare(
                o1: IcashBounsl.BonusData?,
                o2: IcashBounsl.BonusData?
            ): Int {

                return o2!!.topup_value.toDouble()!!
                    .compareTo(o1!!.topup_value.toDouble())
            }
        })

        //getICashBonus()

        binding.eticashamount.addTextChangedListener(
            object : TextWatcher {

                override fun afterTextChanged(s: Editable) {
                    if (s.toString().isNotEmpty()) {
                        getTopupBonusValue(s.toString().toDouble())
                    } else {
                        mTopupValue = 0.00
                        mBonus = 0.00
                        var mTotal = mBalance + mTopupValue + mBonus
                        binding.tvtotalBought!!.text = mTopupValue.toString()
                        binding.tvtotalbous!!.text = mBonus.toString()
                        binding.tvtotal!!.text = mTotal.toString()
                    }

                }

                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int
                ) {

                }
            })


//        val adapter = ArrayAdapter(
//            this,
//            android.R.layout.simple_spinner_item,
//            items
//        )
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//
//        binding.amtSpinner.adapter = adapter
//
//        binding.amtSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                val selected = items[position]
//                if (selected != "Select Amount") {
//                    mTopupValue = selected.toDouble()
//                    if (selected == "200") {
//                        mBonus = 10.00
//                    } else if (selected == "400") {
//                        mBonus = 40.00
//                    } else if (selected == "600") {
//                        mBonus = 75.00
//                    }else if (selected == "800") {
//                        mBonus = 85.00
//                    }else if (selected == "1000") {
//                        mBonus = 125.00
//                    }else if (selected == "1200") {
//                        mBonus = 150.00
//                    }
//                }
//
//                var mTotal = mBalance + mTopupValue + mBonus
//                binding.tvtotalBought!!.text = mTopupValue.toString()
//                binding.tvtotalbous!!.text = mBonus.toString()
//                binding.tvtotal!!.text = mTotal.toString()
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//        }

        binding.btnsubmit.setOnClickListener(View.OnClickListener {

            if (!isClick) {
                isClick = true
                if (mTopupValue > 0.0) {
//                    Log.e("iCashAccount", mICashAccount)
//                    Log.e("balance", mCurrentBalance)
                    val `in` = Intent(this@IcashTopupActivity, PaymentDetailActivity::class.java)
                    `in`.putExtra("customer", customerCode.toString())
                    `in`.putExtra("topupValue", "" + mTopupValue)
                    `in`.putExtra("mBonus", "" + mBonus)
                    `in`.putExtra("iCashAccount", mICashAccount)
                    `in`.putExtra("mobile", mMobile)
                    `in`.putExtra("email", mEmail)
                    `in`.putExtra("CurrentBalance", mCurrentBalance)
                    `in`.putExtra("mBonus", "" + mBonus)
                    `in`.putExtra("Type", "topup")
                    startActivity(`in`)
                    finish()
                } else {
                    showToast("Please Select Amount")
                    isClick = false
                }
            }

        })
    }

    private fun getTopupBonusValue(mValue: Double) {

        mTopupValue = mValue

        mBonus = getTopupBonus(mTopupValue, 0.0)
        binding.tvtotalBought!!.text = mTopupValue.toString()
        binding.tvtotalbous!!.text = mBonus.toString()
        var mTotal = mBalance + mTopupValue + mBonus
        binding.tvtotal!!.text = mTotal.toString()

    }

    fun getTopupBonus(mRemainValue: Double, mBonusValue: Double): Double {

        var mFinalBValue = mBonusValue
        var mRemain = mRemainValue
        var mLastQty = 0.0
        if (arrTopupValue.isNotEmpty()) {

            for (mData in arrTopupValue) {
                mLastQty = arrTopupValue[(arrTopupValue.size - 1)].topup_value.toDouble()

                if (mData.topup_value.toDouble() <= mRemain) {
                    mFinalBValue += mData.bonus_value.toDouble()
                    mRemain -= mData.topup_value.toDouble()
                    break
                }

            }

            if (mRemain >= mLastQty) {
                mFinalBValue = getTopupBonus(mRemain, mFinalBValue)
            }
        }

        return mFinalBValue
    }

    private fun getICashBonus() {

        showOnlyProgressDialog()

        ApiClient.getClient(this).getICashBonus(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}"
        )

            .enqueue(object : Callback<IcashBounsl> {
                override fun onFailure(call: Call<IcashBounsl>, t: Throwable) {
                    getCurrentBalance()
                    addTopupBOnus()
                }

                override fun onResponse(
                    p0: Call<IcashBounsl>,
                    response: Response<IcashBounsl>
                ) {
                    if (response.isSuccessful) {
//                        arrTopupValue = ArrayList()
//                        arrTopupValue = response.body()!!.data
//
//                        Collections.sort(arrTopupValue, object :
//                            Comparator<IcashBounsl.BonusData> {
//                            override fun compare(
//                                o1: IcashBounsl.BonusData?,
//                                o2: IcashBounsl.BonusData?
//                            ): Int {
//
//                                return o2!!.topup_value.toDouble()!!
//                                    .compareTo(o1!!.topup_value.toDouble())
//                            }
//                        })
                    }

                    getCurrentBalance()
                    addTopupBOnus()
                }
            })
    }

    fun addTopupBOnus() {
        var mTotpUp = IcashBounsl.BonusData("600", "75")
        arrTopupValue!!.add(mTotpUp)
        mTotpUp = IcashBounsl.BonusData("400", "40")
        arrTopupValue!!.add(mTotpUp)
        mTotpUp = IcashBounsl.BonusData("200", "10")
        arrTopupValue!!.add(mTotpUp)

        Collections.sort(arrTopupValue, object :
            Comparator<IcashBounsl.BonusData> {
            override fun compare(
                o1: IcashBounsl.BonusData?,
                o2: IcashBounsl.BonusData?
            ): Int {

                return o2!!.topup_value.toDouble()!!
                    .compareTo(o1!!.topup_value.toDouble())
            }
        })
    }

    public fun getCurrentBalance() {


        val jObj = JsonObject()
        jObj.addProperty(
            "customer_id",
            customerObj!!.userDetail.id.toString()
        )
        Log.e("topup", jObj.toString())
        ApiClient.getClient(this).getICashDetail(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            jObj,
        )

            .enqueue(object : Callback<IcashDetail> {
                override fun onFailure(call: Call<IcashDetail>, t: Throwable) {
                    dismissDialog()
                    binding.tvcurrentbalance!!.text = "0.00"
                }

                override fun onResponse(
                    p0: Call<IcashDetail>,
                    response: Response<IcashDetail>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { apiResponse ->
                            dismissDialog()

                            binding.llmain.visibility =
                                View.VISIBLE      // LinearLayout Visible

                            if (apiResponse.data.customer_data.balance != null) {
                                val originalString =
                                    (apiResponse.data.customer_data.balance.toDouble() + apiResponse.data.customer_data.bonus.toDouble())
                                binding.tvcurrentbalance!!.text = originalString.toString()
                                mBalance = originalString.toDouble()
                                mCurrentBalance = mBalance.toString()
                            } else {
                                binding.tvcurrentbalance!!.text = "0.00"
                                mBalance = 0.00
                                mCurrentBalance = mBalance.toString()
                            }

                            if (apiResponse.data.customer_data != null) {
                                if (apiResponse.data.customer_data.account_number != null) {
                                    mICashAccount = apiResponse.data.customer_data.account_number
                                }
                            }

                        }
                    }
                }


            })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}

