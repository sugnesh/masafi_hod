package com.engear.vansales.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.databinding.DataBindingUtil
import com.engear.vansales.R
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.FragmentSalesSummaryBinding
import com.engear.vansales.databinding.FragmentTargetBinding
import com.engear.vansales.localDB.DatabaseHandler
import com.engear.vansales.model.MTDdata
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 * Use the [SalesSummaryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SalesSummaryFragment : Fragment() {

    lateinit var binding: FragmentSalesSummaryBinding

    var day: String? = null
    var weekNumber: String? = null
    var totalCustomer = 0
    var visitedCustomer = 0

    var db: DatabaseHandler? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sales_summary, container, false
        )

        db = DatabaseHandler(activity!!)

        binding.txtCurrentDay.text = Utils.getCurrentDate()

        setMyProgress()

        binding.swcDtdMtd!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (!isChecked) {
            } else {
            }
        })

        return binding.root
    }

    fun setMyProgress() {

        day = Utils.getCurrentTimeInPattern("EEEE")
        weekNumber = "week${Utils.getWeekNumberOfMonth()}"
        if (weekNumber == "week6") {
            weekNumber = "week5"
        }
        //var allCustomerList = db!!.getSequnceCustomer(day!!, weekNumber!!,"")
        totalCustomer = db!!.getSequnceCustomerSize(day!!, weekNumber!!, "").toInt()
        var customerVisitList = db!!.getAllVisitedCustomer()
        visitedCustomer = customerVisitList.size

        if (totalCustomer > 0) {
            var ttl: Float = (visitedCustomer.toFloat() / (totalCustomer.toFloat())) * 100
            binding.tvProgress.setText(String.format("%.2f", ttl) + " %")
        } else {
            binding.tvProgress.setText("0 %")
        }

        var mGross = db!!.getGrossSaleAmount()
        var mNet = db!!.getNetSaleAmount()
        var mReturn = db!!.getNetReturnAmount()

        binding.tvGrossAmt.text =
            Utils.getCurrency(activity!!) + " " + Utils.getCurrencyFormate(
                activity!!,
                mGross.toFloat()
            )
        binding.tvNetAmt.text =
            Utils.getCurrency(activity!!) + " " + Utils.getCurrencyFormate(
                activity!!,
                mNet.toFloat()
            )
        binding.tvReturnAmt.text =
            Utils.getCurrency(activity!!) + " " + Utils.getCurrencyFormate(
                activity!!,
                mReturn.toFloat()
            )
        binding.tvCustomerCount.text = "" + visitedCustomer

        setDailyData()
    }

    private fun setDailyData() {
        binding.circularProgressBar.apply {
            // Set Progress
            progress = visitedCustomer.toFloat()
            // or with animation

            // Set Progress Max
            progressMax = totalCustomer.toFloat()

//            // Set ProgressBar Color
            progressBarColor = resources.getColor(R.color.colorPrimary)
            // or with gradient
//            progressBarColorStart = Color.GRAY
//            progressBarColorEnd = Color.RED
//            progressBarColorDirection = CircularProgressBar.GradientDirection.TOP_TO_BOTTOM

//            // Set background ProgressBar Color
            backgroundProgressBarColor = resources.getColor(R.color.white_200)
//            // or with gradient
//            backgroundProgressBarColorStart = Color.WHITE
//            backgroundProgressBarColorEnd = Color.RED
//            backgroundProgressBarColorDirection = CircularProgressBar.GradientDirection.TOP_TO_BOTTOM

            // Set Width
            progressBarWidth = 7f // in DP
            backgroundProgressBarWidth = 3f // in DP

            // Other
            roundBorder = true
            startAngle = 0f
            progressDirection = CircularProgressBar.ProgressDirection.TO_RIGHT
        }
    }


}