package com.engear.vansales.activities.supervisor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import com.engear.vansales.R
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivitySupervisorDashboardBinding
import com.engear.vansales.model.SPSalesData
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.Gson
import lecho.lib.hellocharts.gesture.ZoomType
import lecho.lib.hellocharts.model.*
import lecho.lib.hellocharts.util.ChartUtils
import lecho.lib.hellocharts.view.LineChartView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SupervisorDashboardActivity : BaseActivity() {
    lateinit var dashboardBinding: ActivitySupervisorDashboardBinding

    private var chart: LineChartView? = null
    lateinit var data: LineChartData
    private var invoiceDetailsList = ArrayList<SPSalesData.Data.InvDetails>()
    val times = ArrayList<String>()
    private var lineData: LineChartData? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //    setContentView(R.layout.activity_dashboard)

        dashboardBinding = ActivitySupervisorDashboardBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()

    }

    fun init() {
        setToolbarTitle("Dashboard")
        setNavigationView()
        showNotification()

        //Chart
        chart = dashboardBinding.lineChartSalesFigures


        getSupervisorData()

    }

    fun prepareLineChart() {
        generateInitialLineData()
    }
    /////////////////////////////////////////



    private fun generateInitialLineData(): Unit {
        val axisValues: MutableList<AxisValue> = ArrayList()
        val values: MutableList<PointValue> = ArrayList()
        var topValue = 0f
        try {
            for (i in invoiceDetailsList.indices) {
                times.add(Utils.formatLocalTime(invoiceDetailsList[i].created_at,"hh:mm")!!)
                if (topValue < invoiceDetailsList[i].grand_total.toFloat()) {
                    topValue = invoiceDetailsList[i].grand_total.toFloat()
                }
            }
        } catch (e: java.lang.Exception) {

        }
        for (i in 0 until times.size) {
            values.add(PointValue(i.toFloat(), invoiceDetailsList[i].grand_total.toFloat()))
            axisValues.add(AxisValue(i.toFloat()).setLabel(times.get(i)))
        }
        val line = Line(values)
        line.setColor(ChartUtils.COLOR_GREEN).isCubic = true
        val lines: MutableList<Line> = ArrayList()
        lines.add(line)
        lineData = LineChartData(lines)
        lineData!!.setAxisXBottom(Axis(axisValues).setHasLines(true))
        lineData!!.setAxisYLeft(Axis().setHasLines(true).setMaxLabelChars(3))
        chart!!.setLineChartData(lineData)

        // For build-up animation you have to disable viewport recalculation.
        chart!!.setViewportCalculationEnabled(false)

        // And set initial max viewport and current viewport- remember to set viewports after data.
        val v = Viewport(0f, topValue, times.size.toFloat(), 0f)
        chart!!.setMaximumViewport(v)
        chart!!.setCurrentViewport(v)
        chart!!.setZoomType(ZoomType.HORIZONTAL)
    }


    //////////////////////////////////////

    private fun getSupervisorData() {

        showOnlyProgressDialog()

        Utils.logData(
            me,
            "Supervisor API Call"
        )

        ApiClient.getClient(this).getSupervisorDashboardData(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN, mPreferenceManager!!.getUserId()!!.toInt()
        )
            .enqueue(object : Callback<SPSalesData> {
                override fun onResponse(
                    call: Call<SPSalesData>,
                    response: Response<SPSalesData>
                ) {
                    Utils.logData(
                        this@SupervisorDashboardActivity,
                        "Supervisor Response$response"
                    )
                    dismissDialog()
                    if (response.isSuccessful) {
                        Utils.logData(
                            this@SupervisorDashboardActivity,
                            "Supervisor Response Body${response.body().toString()}"
                        )
//                        Log.e("dahsvoars",response.body()!!.string())
                        if (response.body()!!.status) {
                            if (!response.body()!!.data.invDetail.isEmpty()){
                                invoiceDetailsList =
                                    response.body()!!.data.invDetail as ArrayList<SPSalesData.Data.InvDetails>
                            }else{
//
                            }


                            setData(response.body()!!.data)
                        }
                    }
                }

                override fun onFailure(call: Call<SPSalesData>, error: Throwable) {
                    dismissDialog()
                    if (error.message != null) {
                        Utils.logData(
                            this@SupervisorDashboardActivity,
                            "Supervisor Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SupervisorDashboardActivity,
                            "Supervisor Response API Fail"
                        )
                    }
                }
            })
    }

    fun setData(mData: SPSalesData.Data) {
//        Log.e("dahsboard", Gson().toJson(mData))
        prepareLineChart()
        var visitCOunt = mData.cusotmer_visit_count
        var sales = mData.invoice_total
        var collection = mData.collection_total
        var mReturnAmt = mData.credit_note_total

        dashboardBinding.textSalesvalue.text =
            "AED " + Utils.getCurrencyFormate(this, sales.toFloat())
        dashboardBinding.textCollection.text =
            "AED " + Utils.getCurrencyFormate(this, collection.toFloat())
        dashboardBinding.textReturnvalue.text =
            "AED " + Utils.getCurrencyFormate(this, mReturnAmt.toFloat())
        dashboardBinding.textVisit.text = visitCOunt
    }
}
